Here is the updated **RETR-Shade-Logic.md**.

I have bumped the version to v2.5.9 and added Section 1.7 to document the Phase 3B Event Tracking feature.

# Master Functional Specifications: RETR-Shade-Logic

**File Name:** RETR-Shade-Logic.md (Rollease Engineering Technical Reference Shade Logic)\
**Project Branch:** The Surgical Observer\
**Integration Logic:** Child Device Architecture with Proactive State-Engine, Position Tolerance, Exponential Backoff, Smart Stateful Retry, Actionable Event Tracking, and RF Jitter\
**Document Status:** Final Production Specification\
**Driver Version:** 2.5.9

***

## 1. Project Context & Technical Philosophy

### 1.1 The Role of the Surgical Observer (Child)

The **RETR-Shade-Logic** driver serves as the terminal interface for individual Rollease Acmeda motors within the Hubitat ecosystem. While the Parent Hub (RETR-Hub-Logic) manages the "Burst-and-Close" socket lifecycle, the Child Driver is tasked with the high-fidelity translation of that raw data into actionable telemetry and real-time state management.

### 1.2 The "Alexa Timeout" & Proactive Response Engine

A critical limitation identified during the "Surgical Observer" audit was the latency inherent in RF mesh networks. Cloud-to-cloud integrations like **Amazon Alexa** require a state-change acknowledgement within a narrow 1–3 second window.

* **The Original Limitation:** The legacy driver waited for the Hub to complete its RF transmission and send back a confirmation burst before updating the Hubitat UI. This often exceeded the timeout, resulting in "Device Not Responding" errors despite successful movement.

* **The Proactive Solution:** This branch implements a **Proactive State-Engine**. Upon receiving a command (`open`, `close`, `setPosition`), the child driver immediately broadcasts its target state to Hubitat.

* **The Result:** Instant visual feedback for the user and immediate "Success" handshakes for Alexa/Google Home, with the Hub's eventual status burst acting as a background "Surgical Correction" to ensure 100% accuracy.

### 1.3 Confirmation-Based Retry, Position Tolerance & Smart Stateful Retry

To address occasional RF dropouts and Hub processing delays, v2.5.2 introduced a **confirmation-based retry mechanism**. This replaced an earlier position-based attempt (v2.5.1) that was found to be unreliable.

**How it works (v2.5.9):**

1. After sending a command, the driver stores the target position and starts a **Heartbeat Timer** (8 seconds).

2. **Heartbeat Check:** When the heartbeat fires, the driver evaluates the state:

   * **Deaf:** If no position reports were received, the shade missed the packet. The driver fires the **RF Jitter** immediately to wake it up.

   * **Stuck:** If a position report was received, but the position is identical to where it started, the shade is physically jammed. The driver **aborts** the retry sequence to save RF bandwidth.

   * **Moving:** If the position has changed, the shade heard the command and is moving. The driver resets the main confirmation timer.

3. The driver waits for a position report (`!IDrPPP`) that is within **`Position Tolerance`** of the target (default ±2%) to mark the command as **confirmed**.

4. If the shade reports intermediate positions while moving, the driver resets the confirmation timer (up to 2 times) to give slow shades time to finish.

5. If no confirmation arrives before the timer expires, the driver retries the command (up to `cmdRetryCount` times).

6. **Exponential Backoff:** The wait time between retries increases by 50% each time (e.g., 20s, 30s, 45s). This prevents flooding the RF repeater during a prolonged shade failure.

7. The jitter attempt counts as one of the retry attempts, so the user-configured `cmdRetryCount` is respected.

**Why Position Tolerance?** Early production logs (2026-09-12, 2026-09-15) showed shades like BSG (Kitchen) and I39 (Office) firing jitter and retries even though they were working correctly. The root cause was an **exact-match confirmation requirement** – shades that physically stop 1–2% short of the target were treated as failures.

**Why Smart Stateful Retry?** In late October 2026, logs showed that the exponential backoff was protecting the repeater, but the driver was still firing unnecessary retries. If a shade was moving slowly (reporting 18% on its way to 50%), the driver would still fire a jitter and retry after 20 seconds because the target hadn't been reached. The Heartbeat Check solves this by verifying movement _before_ the timer expires.

### 1.4 UI Clarity & Alexa Compatibility (v2.4.2 onwards)

In v2.4.0, the `Switch` capability was removed to hide the `On`/`Off` UI buttons. Initially, we added explicit `command "on"` and `command "off"` declarations to keep Alexa functional, but these still appeared as buttons.

In v2.4.2, those explicit command declarations were removed entirely. Alexa now uses the native `Window Shade` and `Switch Level` capabilities directly.

**Result:**

* **UI:** No `On`/`Off` buttons – just `Open` / `Close` from `Window Shade`.

* **Alexa:** Voice commands (`open`, `close`, `set percentage`) work as before.

* **Performance:** Slightly faster due to cleaner metadata.

### 1.5 Battery Formula Recalibration (v2.5.4 Hotfix)

Early battery reporting used a linear formula mapping **10.8V = 0%** and **12.6V = 100%**. After comparing against the Rollease app across eight shades, we observed a consistent gap where the app reported **higher** percentages than our driver.

The formula was recalibrated to use **9.5V = 0%** and **12.6V = 100%**. Testing after the change showed all eight shades within **±5%** of the app. The `Battery Offset` preference remains available for per-shade fine-tuning.

### 1.6 Log Verbosity Dropdown (v2.5.5)

In v2.5.5 the confusing boolean logging preferences were replaced with a single **Log Verbosity** dropdown (ERROR, WARN, INFO, DEBUG). The `Auto-Revert Debug` preference now reverts `Log Verbosity` from `DEBUG` back to `INFO` after 30 minutes.

### 1.7 Actionable Event Tracking (v2.5.9)

While the driver was stable, users wanted to write automations based on specific failure states. The existing global `lastAction` on the Parent hub was not granular enough. Phase 3B introduced two new attributes: `lastEvent` and `lastUpdate`.

`lastEvent` tracks the current state of the device (e.g., `command_sent`, `jitter`, `failed`). `lastUpdate` records the timestamp of the last successful data burst. This allows users to write precise automations, such as receiving a push notification if a shade's battery dies and it goes completely silent.

***

## 2. Full Command API (Methods)

### 2.1 Standard Movement Commands

* **`open()`**: Initiates a full ascent. Proactively sets `windowShade` to `opening` and `position` to `100`.

* **`close()`**: Initiates a full descent. Proactively sets `windowShade` to `closing` and `position` to `0`.

* **`setPosition(level)`**: Moves the shade to a specific percentage. Includes an "Already-at-Target" check.

* **`stop()`**: Interrupts current movement. Cancels any pending retry timers and clears retry state.

* **`toggle()`**: Cycles through `open` → `stop` → `close` logic based on `lastDirection`.

* **`requestBatteryStatus()`**: Forces the Hub to send a fresh battery and signal report.

### 2.2 Compatibility Aliases

* **`on()` / `off()`**: Maps directly to `open()` and `close()`. Retained for internal/automation use, but not exposed as UI buttons.

* **`setLevel(level)`**: Required for `Switch Level` capability; maps to `setPosition()`.

### 2.3 Non-Functional Capability Stubs

* **`startPositionChange(String direction)`**: Logs an informational message (not supported by ARC).

* **`stopPositionChange()`**: Logs an informational message (not supported by ARC).

***

## 3. User Preferences & Configuration

| Preference                  | Format  | Default | Description                                                                                                  |
| --------------------------- | ------- | ------- | ------------------------------------------------------------------------------------------------------------ |
| **Motor Address**           | String  | 000     | The unique 3-character alphanumeric ID.                                                                      |
| **Battery Offset**          | Number  | 0       | Adjusts reported battery percentage (−30 to +30).                                                            |
| **Position Tolerance**      | Number  | 2       | Allow the shade to be considered "confirmed" if it reaches within this percentage of the target. Range: 0–5. |
| **Command Retry Count**     | Enum    | 2       | Number of retries if a command fails to receive confirmation. Options: 0, 1, 2, 3.                           |
| **Command Retry Wait Time** | Number  | 20      | Time to wait before retrying. The wait time increases by 50% for each subsequent retry. Range: 5–30.         |
| **Enable RF Jitter**        | Boolean | True    | If a command fails after all retries, send a harmless status request to wake the Hub's RF transmitter.       |
| **Log Verbosity**           | Enum    | INFO    | Controls the amount of detail written to the log. Options: ERROR, WARN, INFO, DEBUG.                         |
| **Auto-Revert Debug**       | Boolean | True    | Automatically reverts Log Verbosity from DEBUG to INFO after 30 minutes.                                     |

***

## 4. Telemetry & Current States (Attributes)

### 4.1 Primary Motion Attributes

* **`windowShade`**: The primary status (`open`, `closed`, `opening`, `closing`, `partially open`).

* **`position` / `level`**: Real-time integer value (0–100) of the shade's height.

* **`moving`**: A boolean flag used to trigger high-frequency dashboard updates.

* **`open` / `closed`**: Binary states indicating if the shade has reached its upper or lower limit.

* **`switch`**: Mirrors the power-style state. Provided for compatibility.

* **`lastEvent`**: The most recent event for this shade. Values: `idle`, `command_sent`, `position_confirmed`, `retry`, `jitter`, `failed`, `stopped`, `battery_report`.

* **`lastUpdate`**: Timestamp of the last successful data burst from the shade.

### 4.2 Power & Signal Telemetry

* **`battery`**: Calculated percentage based on the linear mapping (9.5V = 0%, 12.6V = 100%).

* **`batteryVoltage`**: The decoded decimal voltage (e.g., `12.04V`).

* **`voltage`**: The raw integer value received from the Hub burst.

* **`rssi`**: The signal strength indicator (e.g., `R75`).

***

## 5. Internal Logic & State Variables

* **`state.lastDirection`**: Tracks the most recent movement.

* **`state.lastLoggedPosition`**: Prevents duplicate position confirmation logs.

* **`state.pendingTarget`**: The position currently awaiting confirmation.

* **`state.retryCount`**: Number of retries attempted for the current command.

* **`state.confirmed`**: Boolean set when the Hub confirms the target position.

* **`state.jitterUsed`**: Tracks whether RF Jitter has been sent.

* **`state.currentCommand`**: The ARC string last sent (used to resend on retry).

* **`state.commandId`**: Incremented on every new command. Used to invalidate stale callbacks.

* **`state.reportResetCount`**: Number of times the confirmation timer has been reset due to intermediate position reports.

* **`state.lastReportedPosition`**: The last position reported by the Hub.

* **`state.lastHeartbeatPosition`**: The position recorded at the 8-second Heartbeat Check to determine if the shade is moving or stuck.

***

## 6. Logic Guardrails (The DNA of Reliability)

### 6.1 Data Validation & Sanitization

The driver employs a "Surgical" approach to incoming Hub data. Zero-padding, type casting, and safe preference access are applied everywhere.

### 6.2 Intelligent Parsing

The `parse()` method handles multi-part data bursts, stripping leading zeros and applying "Try-Catch" blocks to prevent crashes on malformed reports.

### 6.3 Position Confirmation Deduplication

The driver uses `state.lastLoggedPosition` to ensure that confirmation logs are only emitted when the position actually changes.

### 6.4 Retry State Hygiene

Every time a command is sent, all retry-related timers are unscheduled and state variables are reset. On confirmation, `pendingTarget` is immediately cleared.

### 6.5 Jitter Safety

The jitter request uses the shade's own `motorAddress` – never another shade's ID.

### 6.6 Stale-Callback Guard (`commandId`)

Every scheduled callback captures the `commandId`. When the callback fires, it compares the captured ID against the current `state.commandId`. If they differ, the callback bails silently.

### 6.7 Intermediate Position Reset Guard

To prevent an infinite reset loop when a shade is stuck, the intermediate position reset is capped at **2 resets per command**. The timer only resets if the reported position has **changed**.

### 6.8 `unschedule()` Method Name Accuracy

The `unschedule()` calls were corrected in v2.5.5 to match the actual scheduled methods.

### 6.9 Exponential Backoff & Repeater Protection

In v2.5.7, the retry logic was updated to protect the RF repeater from being flooded. The driver increases the wait time between retries by 50% each time.

### 6.10 Smart Stateful Retry (Heartbeat Check)

In v2.5.8, the driver introduced an 8-second Heartbeat Check before the main confirmation timer expires.

* **Deaf:** No reports -> Fire Jitter.

* **Moving:** Position changed -> Reset timer.

* **Stuck:** Position unchanged -> Abort retries to save RF bandwidth.

***

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)

* **Lineage:** Optimized fork of the Younes Oughla (arcautomate) original branch.

* **Current Driver Version:** 2.5.9

* **Key Enhancements (v2.5.1 – v2.5.9):**

  * **v2.5.1** – Initial position-based retry feature.

  * **v2.5.2** – Confirmation-based retry logic, RF Jitter workaround.

  * **v2.5.3** – Fixed `logWarn` → `logWarning` bug.

  * **v2.5.4** – Battery formula recalibrated to 9.5V–12.6V.

  * **v2.5.5** – Added `Position Tolerance`, intermediate position reset, fixed `unschedule()`, added `commandId` guard, replaced logging toggles with `Log Verbosity`.

  * **v2.5.7** – Changed defaults (Tolerance 2, Wait 20s), added Enum for retry count, added Exponential Backoff (50% increase per retry) and Repeater Protection.

  * **v2.5.8** – Added Smart Stateful Retry (8-second Heartbeat Check) to differentiate between Deaf, Moving, and Stuck shades.

  * **v2.5.9** – Added Actionable Event Tracking (`lastEvent` and `lastUpdate`).

### Lessons Learned (v2.5.x)

* **`logWarn` vs `logWarning`** – a typo silently killed the retry logic.

* **`getChildDevices()` in a child driver** – always returns empty. Use the shade's own `motorAddress`.

* **Jitter must count as a retry** – otherwise `cmdRetryCount` is not respected.

* **State hygiene matters** – `pendingTarget` must be cleared on confirmation.

* **Do not use `ShadeRemove` to swap addresses** – update the `motorAddress` preference directly.

* **Exact-match confirmation is too strict** – `Position Tolerance` fixes false retries.

* **Slow shades report intermediate positions** – the intermediate position reset handles this.

* **`unschedule()` method names must match exactly** – incorrect names were silent no-ops.

* **Stale callbacks are a real race condition** – the `commandId` guard prevents this.

* **Retry floods can crash the repeater** – Exponential Backoff prevents this.

* **Blind retries waste RF bandwidth** – The Heartbeat Check ensures we only retry a shade that is actually deaf or stuck.

* **Global attributes are not enough** – `lastEvent` provides per-shade granularity for automations.

***

## 📝 Summary of Updates Applied to RETR-Shade-Logic.md (v2.5.8 → v2.5.9)

**Version Control:**

* Bumped **Driver Version** from `2.5.8` → **`2.5.9`**.

* Updated **Integration Logic** description to include **Actionable Event Tracking**.

**Section 1 – Project Context:**

* **Section 1.7** (new) – **Actionable Event Tracking (v2.5.9)**. Documents the addition of `lastEvent` and `lastUpdate`.

**Section 4 – Telemetry & Current States:**

* Added `lastEvent` and `lastUpdate` to the attributes list.

**Section 7 – Versioning & Lineage:**

* Bumped Current Driver Version to 2.5.9.

* Added **v2.5.9** to Key Enhancements.

* Added new Lessons Learned entry about per-shade granularity.
