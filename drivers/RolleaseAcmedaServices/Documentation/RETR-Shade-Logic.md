# Master Functional Specifications: RETR-Shade-Logic

**File Name:** RETR-Shade-Logic.md (Rollease Engineering Technical Reference Shade Logic)\
**Project Branch:** The Surgical Observer\
**Integration Logic:** Child Device Architecture with Proactive State-Engine, Position Tolerance, Confirmation-Based Retry, and RF Jitter\
**Document Status:** Final Production Specification\
**Driver Version:** 2.5.5

***

## 1. Project Context & Technical Philosophy

### 1.1 The Role of the Surgical Observer (Child)

The **RETR-Shade-Logic** driver serves as the terminal interface for individual Rollease Acmeda motors within the Hubitat ecosystem. While the Parent Hub (RETR-Hub-Logic) manages the "Burst-and-Close" socket lifecycle, the Child Driver is tasked with the high-fidelity translation of that raw data into actionable telemetry and real-time state management.

### 1.2 The "Alexa Timeout" & Proactive Response Engine

A critical limitation identified during the "Surgical Observer" audit was the latency inherent in RF mesh networks. Cloud-to-cloud integrations like **Amazon Alexa** require a state-change acknowledgement within a narrow 1–3 second window.

* **The Original Limitation:** The legacy driver waited for the Hub to complete its RF transmission and send back a confirmation burst before updating the Hubitat UI. This often exceeded the timeout, resulting in "Device Not Responding" errors despite successful movement.

* **The Proactive Solution:** This branch implements a **Proactive State-Engine**. Upon receiving a command (`open`, `close`, `setPosition`), the child driver immediately broadcasts its target state to Hubitat.

* **The Result:** Instant visual feedback for the user and immediate "Success" handshakes for Alexa/Google Home, with the Hub's eventual status burst acting as a background "Surgical Correction" to ensure 100% accuracy.

### 1.3 Confirmation-Based Retry, Position Tolerance & RF Jitter

To address occasional RF dropouts and Hub processing delays, v2.5.2 introduced a **confirmation-based retry mechanism**. This replaced an earlier position-based attempt (v2.5.1) that was found to be unreliable.

**How it works (v2.5.5):**

1. After sending a command, the driver stores the target position and starts a confirmation timer (`cmdRetryWait`, default 10s).

2. When the Hub sends back a position report (`!IDrPPP`) that is within **`Position Tolerance`** of the target (default ±1%), the driver marks the command as **confirmed** and clears all pending state.

3. If the position report does not yet match the target but the position has changed since the last report (i.e., the shade is actively moving), the driver **resets the confirmation timer** — up to 2 times per command. This gives slow shades time to finish without firing jitter.

4. If no confirmation arrives before the timer expires (and no intermediate position resets remain), the driver retries the command (up to `cmdRetryCount` times).

5. If all retries are exhausted and **RF Jitter** is enabled, the driver sends a harmless status request (`!IDr?`) using the shade's own `motorAddress` to "wake" the Hub's RF transmitter, then retries the original command once more.

6. The jitter attempt counts as one of the retry attempts, so the user-configured `cmdRetryCount` is respected.

**Why Position Tolerance?** Early production logs (2026-09-12, 2026-09-15) showed shades like BSG (Kitchen) and I39 (Office) firing jitter and retries even though they were working correctly. The root cause was an **exact-match confirmation requirement** – shades that physically stop 1–2% short of the target were treated as failures. The `Position Tolerance` preference (introduced in v2.5.5) fixes this without masking real failures.

**Why Intermediate Position Reset?** Slow shades (UE2, 9E3, XDG) report intermediate positions while moving (e.g., 51% on the way to a 53% target). The intermediate reset gives them time to finish without firing jitter, while a stuck position (no change between reports) will still trigger retries.

**Why RF Jitter?** In some environments, the Hub's RF transmitter appears to go idle between commands. Sending a status request forces the Hub to re-activate its transmitter, improving the odds of the following command reaching the shade.

### 1.4 UI Clarity & Alexa Compatibility (v2.4.2 onwards)

In v2.4.0, the `Switch` capability was removed to hide the `On`/`Off` UI buttons. Initially, we added explicit `command "on"` and `command "off"` declarations to keep Alexa functional, but these still appeared as buttons.

In v2.4.2, those explicit command declarations were removed entirely. Alexa now uses the native `Window Shade` and `Switch Level` capabilities directly:

* **`open()`** and **`close()`** are called by Alexa via the `Window Shade` capability.

* **`setLevel()`** is called by Alexa via the `Switch Level` capability.

* The `on()` and `off()` methods remain in the code for internal/automation use, but they are no longer exposed as UI buttons.

**Result:**

* **UI:** No `On`/`Off` buttons – just `Open` / `Close` from `Window Shade`.

* **Alexa:** Voice commands (`open`, `close`, `set percentage`) work as before.

* **Performance:** Slightly faster due to cleaner metadata.

### 1.5 Battery Formula Recalibration (v2.5.4 Hotfix)

Early battery reporting used a linear formula mapping **10.8V = 0%** and **12.6V = 100%**. After comparing against the Rollease app across eight shades (roman and roller, external and embedded batteries), we observed a consistent gap where the app reported **higher** percentages than our driver, with the gap growing as voltage dropped (from +9% at 12.28V to +25% at 11.57V).

The formula was recalibrated to use **9.5V = 0%** and **12.6V = 100%**. Testing after the change showed all eight shades within **±5%** of the app, with most reading conservatively (slightly below the app). The `Battery Offset` preference remains available for per-shade fine-tuning.

### 1.6 Log Verbosity Dropdown (v2.5.5)

In v2.5.5 the confusing boolean logging preferences (`Enable Debug Logging`, `Enable Description Logging`) were replaced with a single **Log Verbosity** dropdown (ERROR, WARN, INFO, DEBUG). This unifies the logging UI with modern Hubitat standards and eliminates the ambiguity between the two toggles. The `Auto-Revert Debug` preference now reverts `Log Verbosity` from `DEBUG` back to `INFO` after 30 minutes.

***

## 2. Full Command API (Methods)

The following commands represent the "DGBQ" optimized interface, ensuring protocol-compliant communication with the Parent Hub.

### 2.1 Standard Movement Commands

* **`open()`**: Initiates a full ascent. **Proactive Action:** Immediately sets `windowShade` to `opening` and `position` to `100`. Logs `Command sent: Opening` at INFO level.

* **`close()`**: Initiates a full descent. **Proactive Action:** Immediately sets `windowShade` to `closing` and `position` to `0`. Logs `Command sent: Closing` at INFO level.

* **`setPosition(level)`**: Moves the shade to a specific percentage.

  * **Optimization:** Includes an "Already-at-Target" check. If the requested position matches the current state, the driver suppresses the RF command to reduce Hub congestion while still refreshing the Hubitat state.

  * **Logging:** Logs `Command sent: Moving to X%` at INFO level.

* **`stop()`**: Interrupts current movement. Resets the `moving` flag to `false` and sets the state to `partially open`. Also cancels any pending retry timers and clears retry state. Logs `Command sent: Stop` at INFO level.

* **`toggle()`**: A convenience method that cycles through `open` → `stop` → `close` logic based on the `lastDirection` variable.

* **`requestBatteryStatus()`**: Forces the Hub to send a fresh battery and signal report. Immediately updates `battery`, `batteryVoltage`, `voltage`, and `rssi`. Logs `Requesting battery status from hub` at INFO level.

### 2.2 Compatibility Aliases

* **`on()` / `off()`**: Maps directly to `open()` and `close()`. These methods are retained for internal use and automation calls, but they are **not** exposed as UI buttons (v2.4.2).

* **`setLevel(level)`**: Required for `Switch Level` capability; maps to `setPosition()`. The optional `duration` parameter is ignored and logs a warning if non-zero.

### 2.3 Non-Functional Capability Stubs

The following commands are provided by Hubitat capabilities but are **not supported** by this driver. They log an informational message when invoked:

* **`startPositionChange(String direction)`**: Logs "Start Position Change is not supported by this driver. Use Open/Close or Set Position instead."

* **`stopPositionChange()`**: Logs "Stop Position Change is not supported by this driver. Use Stop instead."

***

## 3. User Preferences & Configuration

These inputs define the child device's unique identity on the ARC mesh, its retry behaviour, and its logging behaviour.

| Preference                  | Format  | Default      | Description                                                                                                                                                                                           |
| --------------------------- | ------- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Motor Address**           | String  | 000          | The unique 3-character alphanumeric ID (e.g., `A1C`) assigned by the Hub. Set automatically by the Parent when the child is created.                                                                  |
| **Battery Offset**          | Number  | 0            | Adjusts reported battery percentage by a fixed amount (−30 to +30). Use for calibrating against the official Rollease app when the default formula and the app differ by a few percent.               |
| **Position Tolerance**      | Number  | 1            | Allow the shade to be considered "confirmed" if it reaches within this percentage of the target. Increase if your shade consistently stops a few percent short. Range: 0–5. Set to 0 for exact match. |
| **Command Retry Count**     | Number  | 2            | Number of retries if a command fails to receive confirmation. Set to `0` to disable retries. Range: 0–5.                                                                                              |
| **Command Retry Wait Time** | Number  | 10 (seconds) | Time to wait before retrying a command. A longer wait gives the shade more time to respond. Range: 5–30.                                                                                              |
| **Enable RF Jitter**        | Boolean | True         | If a command fails after all retries, send a harmless status request to wake the Hub's RF transmitter, then retry once more.                                                                          |
| **Log Verbosity**           | Enum    | INFO         | Controls the amount of detail written to the log and debug buffer. Options: `ERROR`, `WARN`, `INFO`, `DEBUG`.                                                                                         |
| **Auto-Revert Debug**       | Boolean | True         | Automatically reverts `Log Verbosity` from `DEBUG` to `INFO` after 30 minutes.                                                                                                                        |

***

## 4. Telemetry & Current States (Attributes)

The "Surgical Observer" branch significantly expands the telemetry captured from the Hub's data bursts.

### 4.1 Primary Motion Attributes

* **`windowShade`**: The primary status (`open`, `closed`, `opening`, `closing`, `partially open`).

* **`position` / `level`**: Real-time integer value (0–100) of the shade's height.

* **`moving`**: A boolean flag used to trigger high-frequency dashboard updates.

* **`open` / `closed`**: Binary states indicating if the shade has reached its upper or lower limit.

* **`switch`**: Mirrors the power-style state (`on` if position > 0, `off` if position = 0). Provided for compatibility even though the `Switch` capability is not exposed in the UI.

### 4.2 Power & Signal Telemetry

* **`battery`**: Calculated percentage based on the linear mapping (9.5V = 0%, 12.6V = 100%). Matches the Rollease app within ±5% across tested shades. Fine-tune with `Battery Offset` if needed.

* **`batteryVoltage`**: The decoded decimal voltage (e.g., `12.04V`).

* **`voltage`**: The raw integer value received from the Hub burst (e.g., `1204`).

* **`rssi`**: The signal strength indicator (e.g., `R75`), allowing for mesh health diagnostics.

***

## 5. Internal Logic & State Variables

The child driver uses a small set of state variables to manage movement, retries, and confirmation tracking.

* **`state.lastDirection`**: Tracks the most recent movement (`opening` or `closing`) to ensure the `toggle()` command operates intuitively.

* **`state.lastLoggedPosition`**: Stores the last position that was logged as `INFO`. Prevents duplicate position confirmation logs when the Hub sends multiple identical status bursts.

* **`state.pendingTarget`**: The position currently awaiting confirmation from the Hub. Cleared on confirmation or when retries are exhausted.

* **`state.retryCount`**: Number of retries attempted for the current command.

* **`state.confirmed`**: Boolean set when the Hub confirms the target position.

* **`state.jitterUsed`**: Tracks whether RF Jitter has been sent for the current command (so it is only sent once per command).

* **`state.currentCommand`**: The ARC string last sent (used to resend on retry).

* **`state.commandId`**: Incremented on every new command. Used to invalidate stale callbacks from previous commands (prevents race conditions).

* **`state.reportResetCount`**: Number of times the confirmation timer has been reset due to intermediate position reports (max 2 per command).

* **`state.lastReportedPosition`**: The last position reported by the Hub. Used to detect whether the position has actually changed since the previous report.

* **Attribute Seeding:** Upon installation or initialization, the driver "seeds" default values (Position 0, Closed) to ensure Alexa has a baseline state before the first Hub report is received.

***

## 6. Logic Guardrails (The DNA of Reliability)

### 6.1 Data Validation & Sanitization

The driver employs a "Surgical" approach to incoming Hub data:

* **Zero-Padding:** All outgoing position commands are automatically padded to three digits (e.g., `50` becomes `050`) to prevent ARC protocol rejection.

* **Type Casting:** Advanced handling for `String` vs `Integer` inputs in `setPosition` and `setLevel` to prevent "GroovyCast" exceptions.

* **Voltage Mapping:** Battery percentage is calculated using a calibrated 9.5V (0%) to 12.6V (100%) scale.

* **Safe Preference Access:** All preferences are read using `settings.` prefix to ensure reliable access across driver versions.

### 6.2 Intelligent Parsing

The `parse()` method is designed to handle multi-part data bursts. It splits the raw `pVc` string into discrete components, stripping leading zeros and applying "Try-Catch" blocks to prevent the driver from crashing on malformed Hub reports.

### 6.3 Position Confirmation Deduplication

The driver uses `state.lastLoggedPosition` to ensure that `Position confirmed:` logs are only emitted when the position actually changes. This prevents log clutter from duplicate status bursts.

### 6.4 Retry State Hygiene

Every time a command is sent via `sendCommand()`, all retry-related timers are unscheduled and state variables are reset. On confirmation, `pendingTarget` is immediately cleared. On `stop()`, all retry state is wiped. This prevents stale state from earlier commands interfering with later ones.

### 6.5 Jitter Safety

The jitter request uses the shade's own `motorAddress` – never another shade's ID. This guarantees the request is always valid and never accidentally triggers movement on a different shade.

### 6.6 Stale-Callback Guard (`commandId`)

Every scheduled callback (`checkPositionConfirmation`, `retryWithJitter`) captures the `commandId` at the time it was scheduled. When the callback fires, it compares the captured ID against the current `state.commandId`. If they differ, the callback bails silently. This eliminates the race condition where a new command could trigger the previous command's pending jitter retry. This guard was added in v2.5.5.

### 6.7 Intermediate Position Reset Guard

To prevent an infinite reset loop when a shade is stuck at a position but keeps reporting it, the intermediate position reset is capped at **2 resets per command**. Additionally, the timer only resets if the reported position has **changed** since the previous report. If the position is unchanged, the timer is allowed to expire and normal retry logic takes over. This guard was added in v2.5.5.

### 6.8 `unschedule()` Method Name Accuracy

In v2.5.5 the `unschedule()` calls were corrected. Previous versions called `unschedule("retryCommand")` and `unschedule("jitterRetry")`, but the actual scheduled methods are `checkPositionConfirmation` and `retryWithJitter`. Those calls were no-ops. This is now fixed.

***

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)

* **Lineage:** Optimized fork of the Younes Oughla (arcautomate) original branch.

* **Current Driver Version:** 2.5.5

* **Key Enhancements (v2.5.1 – v2.5.5):**

  * **v2.5.1** – Initial position-based retry feature (later superseded).

  * **v2.5.2** – Confirmation-based retry logic, RF Jitter workaround, new preferences (`Command Retry Count`, `Command Retry Wait Time`, `Enable RF Jitter`), debug preference consolidation.

  * **v2.5.3** – Fixed `logWarn` → `logWarning` bug that crashed retry logic, improved jitter ID selection to use the shade's own `motorAddress`, cleared `pendingTarget` on confirmation, counted jitter as one retry.

  * **v2.5.4** – Battery formula recalibrated from 10.8V–12.6V to 9.5V–12.6V to match the Rollease app within ±5%.

  * **v2.5.5** – Added `Position Tolerance` preference, added intermediate position reset, fixed `unschedule()` method names, added `commandId` stale-callback guard, replaced logging toggles with `Log Verbosity` dropdown, updated `Auto-Revert Debug` to revert `DEBUG` back to `INFO`.

### Lessons Learned (v2.5.x)

* **`logWarn` vs `logWarning`** – a typo in the logging helper name silently killed the retry logic. Fixed in v2.5.3.

* **`getChildDevices()` in a child driver** – always returns empty. Jitter ID selection now uses the shade's own `motorAddress`.

* **Jitter must count as a retry** – otherwise the user's `cmdRetryCount` is not respected.

* **State hygiene matters** – `pendingTarget` must be cleared on confirmation to prevent stale state from interfering with later commands.

* **Do not use `ShadeRemove` to swap addresses** – the Parent's safety check (v3.3.24) prevents this, but the recommended approach is to update the `motorAddress` preference directly on the child.

* **Exact-match confirmation is too strict** – shades physically stop 1–2% short of the target. The `Position Tolerance` preference (v2.5.5) fixes false retries without masking real failures.

* **Slow shades report intermediate positions** – the driver must accept these as evidence of progress, not as a reason to fire jitter. The intermediate position reset (v2.5.5) handles this.

* **`unschedule()` method names must match scheduled methods exactly** – the previous incorrect names were silent no-ops. Fixed in v2.5.5.

* **Stale callbacks are a real race condition** – the `commandId` guard (v2.5.5) prevents a new command from being affected by a previous command's pending callback.

***

## 📝 Summary of Updates Applied to RETR-Shade-Logic.md (v2.5.4 → v2.5.5)

### Version Control

* Bumped **Driver Version** from `2.5.4` → **`2.5.5`**.

* Updated **Integration Logic** description to include **Position Tolerance**.

### Section 1 – Project Context

* **Section 1.3** retitled from "Confirmation-Based Retry & RF Jitter (v2.5.2)" to "Confirmation-Based Retry, Position Tolerance & RF Jitter". Rewrote the "How it works" sequence to reflect the v2.5.5 flow: position tolerance in step 2, intermediate reset in step 3.

* **Section 1.3** added explanations for why position tolerance and intermediate position reset exist.

* **Section 1.6** (new) added: "Log Verbosity Dropdown (v2.5.5)" – replaces the two boolean logging preferences with a single dropdown.

### Section 3 – User Preferences

* **Added** `Position Tolerance` preference row.

* **Replaced** `Enable Description Logging`, `Enable Debug Logging`, `Auto-Revert Debug` rows with `Log Verbosity` (Enum) and updated `Auto-Revert Debug` row.

* **Removed** the "Planned change (Phase 2)" note – the change has been applied.

### Section 5 – Internal Logic & State Variables

* **Added** `state.commandId`.

* **Added** `state.reportResetCount`.

* **Added** `state.lastReportedPosition`.

### Section 6 – Logic Guardrails

* **Section 6.6** (new) – Stale-Callback Guard (`commandId`).

* **Section 6.7** (new) – Intermediate Position Reset Guard (max 2 resets, position must change).

* **Section 6.8** (new) – `unschedule()` Method Name Accuracy.

### Section 7 – Versioning & Lineage

* **Bumped** Current Driver Version to 2.5.5.

* **Added v2.5.5** entry to Key Enhancements.

* **Added three new Lessons Learned entries** (exact-match confirmation, intermediate positions, unschedule names, stale callbacks).

### What Did NOT Change

* Section 1.1, 1.2, 1.4, 1.5 (project context, Alexa timeout, UI clarity, battery recalibration).

* Section 2 (Full Command API) – the command API itself is unchanged; only the retry behaviour described under Commands changed slightly (covered in the Retry Behaviour note within Section 1.3).

* Section 4 (Telemetry & Current States) – unchanged.

* Sections 6.1–6.5 – unchanged.

***

