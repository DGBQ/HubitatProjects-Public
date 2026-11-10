# Rollease Acmeda Shade (Child) – CHANGELOG

#### v2.5.9 - 2026-11-04 - David Ball-Quenneville

* **PHASE 3B – Actionable Event Tracking:**

  * **New Attributes:** Added `lastEvent` (enum) and `lastUpdate` (string).

  * **`lastEvent`:** Tracks the current state of the device for use in automations. Values include: `idle`, `command_sent`, `position_confirmed`, `retry`, `jitter`, `failed`, `stopped`, and `battery_report`.

  * **`lastUpdate`:** A timestamp of the last successful data burst received from the shade. Useful for dashboard health checks or detecting a dead battery.

  * **Implementation:** Added `sendEvent()` calls across `sendCommand`, `checkHeartbeat`, `checkPositionConfirmation`, `retryWithJitter`, `stop`, and `parse` to update these attributes in real-time.

  * **Why:** This allows users to write precise automations (e.g., "If XDG's lastEvent becomes 'failed', send a push notification").

#### v2.5.8 - 2026-10-28 - David Ball-Quenneville

* **HOTFIX 3 – Smart Stateful Retry (Heartbeat Check):**

  * **New State Variable:** Added `state.lastHeartbeatPosition` to track movement progress.

  * **New `checkHeartbeat()` Method:** Introduced an 8-second heartbeat check immediately after a command is sent to differentiate between three physical states:

    * **Deaf (No Reports):** If no position reports are received within 8 seconds, the driver assumes the shade missed the packet and fires the RF Jitter to wake it up.

    * **Moving (Reports Changed):** If the shade is reporting intermediate positions (e.g., 18%) that differ from the last known position, the driver resets the timer and waits for the target.

    * **Stuck (Reports Unchanged):** If the shade reports the same position it was at when the command was sent, the driver aborts retries and logs a "Stuck" failure (saving RF bandwidth).

  * This eliminates unnecessary retries for slow-moving shades and speeds up recovery for shades with flaky RF receivers (like XDG).

#### v2.5.7 - 2026-10-23 - David Ball-Quenneville

* **HOTFIX – Exponential Backoff & Repeater Protection:**

  * **Changed `Position Tolerance` default** from 1 to **2**. Empirical testing across eight shades confirmed that motors physically stop 1–2% short of the target. Tolerance 2 eliminates false retries without masking real failures.

  * **Changed `Command Retry Wait Time` default** from 10 to **20 seconds**. Production logs showed most shades require 11–20 seconds to complete movement and report position.

  * **Changed `Command Retry Count` from a number field to an Enum dropdown** (0, 1, 2, 3; default 2) to prevent users from entering invalid values above the hard cap.

  * **Implemented Exponential Backoff:** The wait time between retries now increases by 50% each time (e.g., 20s, 30s, 45s).

  * **Added Repeater Protection:** Capped the maximum number of RF commands sent per movement to 4 (Initial + Jitter + 2 retries at maximum setting). This prevents the driver from flooding an RF repeater during a prolonged shade failure, which was causing the repeater to lock up.

  * **Updated logging:** Retry logs now display the calculated wait time (e.g., `"Retry 2/3 in 30 seconds"`).

* **Verified in production:** The flooding issue on XDG (Living Room Left) was traced to the driver sending 5 commands in 60 seconds, which overwhelmed the repeater. The new backoff and cap prevent this.

#### v2.5.5 - 2026-09-16 - David Ball-Quenneville

* **PHASE 2A – Retry Logic + Logging UX:**

  * **Added `Position Tolerance` preference** (default 1, range 0–5). The confirmation check now accepts a shade that reaches within ±tolerance of the target, eliminating false retries on shades that physically stop 1–2% short. Real failures (shade stuck far from target) still trigger retries.

  * **Added intermediate position reset.** If the reported position has changed since the last report, the confirmation timer is reset (up to 2 times) instead of firing jitter. This eliminates false retries on slow shades that report movement progress . After 2 resets, the timer is allowed to expire and normal retry logic takes over.

  * **Fixed `unschedule()` method names.** `retryCommand` → `checkPositionConfirmation`; `jitterRetry` → `retryWithJitter`. These were no‑ops in v2.5.4 – the timers were never actually cancelled.

  * **Added `state.commandId` stale‑callback guard.** Incremented on every new command. Scheduled callbacks check it and bail silently if stale. Prevents a new command from triggering the previous command's pending jitter retry.

  * **Replaced `logEnable` and `txtEnable` with `logVerbosity`** (Enum: ERROR, WARN, INFO, DEBUG; default INFO). Unified logging UI, consistent with modern Hubitat standards.

  * **Updated `autoRevertDebug`** to revert `Log Verbosity` from `DEBUG` back to `INFO` after 30 minutes.

  * **Updated logging helpers** to check `settings.logVerbosity` with proper severity ordering.

* **Verified in production:** BSG, I39, and XDG all confirmed cleanly with no jitter and no retries on the first test after applying.

#### v2.5.4 - 2026-09-11 - David Ball-Quenneville

* **HOTFIX – Battery Voltage Formula Recalibrated:**

  * Changed the voltage-to-percentage range from **10.8V–12.6V** to **9.5V–12.6V**.

  * This brings driver-reported battery percentages into close alignment with the Rollease app (tested within ±5% across eight shades of mixed types – roman and roller, external and embedded batteries).

  * The `Battery Offset` preference remains available for per-shade fine-tuning (±30%).

* No changes to movement, retry, jitter, logging, or preferences.

#### v2.5.3 - 2026-09-02 - David Ball-Quenneville

* **Fixed** `MissingMethodException` – replaced all `logWarn` calls with the correct `logWarning` method. This bug caused the retry logic to crash the moment a retry was attempted.

* **Improved** RF jitter ID selection – now uses the shade's own `motorAddress` instead of `getChildDevices()` (which always returned empty in the child driver).

* **Cleaned** retry state – `state.pendingTarget` is now cleared immediately upon confirmation to prevent stale state.

* **Counted** jitter as one of the retry attempts – ensures the user-configured `cmdRetryCount` is respected.

#### v2.5.2 - 2026-09-02 - David Ball-Quenneville

* Added **confirmation-based retry logic** – commands are now retried only if the Hub does not confirm the target position.

  * New preferences: `Command Retry Count` (default 2) and `Command Retry Wait Time` (default 10s).

* Added **RF Jitter workaround** – if all retries fail, the driver sends a harmless status request (`!IDr?`) to wake the Hub's RF transmitter, then retries the original command.

  * New preference: `Enable RF Jitter` (default On).

* Grouped retry preferences between `Battery Offset` and `Enable Description Logging` for better UI flow.

* Removed `Keep Debug Logging On` – consolidated into `Auto-Revert Debug`.

#### v2.5.1 - 2026-09-02 - David Ball-Quenneville

* Added initial command retry feature (position-based). Superseded by the improved confirmation-based logic in v2.5.2.

#### v2.4.4 - 2026-07-25 - David Ball-Quenneville

* No functional changes – compatible with Hub v3.3.23.

* Proactive state updates retained.

* Clean UI (On/Off buttons hidden).

#### Lessons Learned (v2.5.9 – Event Tracking)

While the driver was stable, users wanted to write automations based on specific failure states. The existing global `lastAction` on the Parent hub was not granular enough. The solution was to add per-shade `lastEvent` and `lastUpdate` attributes, allowing for precise webCoRE/Rule Machine triggers (e.g., "If XDG's lastEvent becomes 'failed', send a notification").

#### Lessons Learned (v2.5.8 – Smart Stateful Retry)

In late October 2026, it was discovered that the exponential backoff (while successful at protecting the repeater) was still firing unnecessary retries. If a shade took longer than 20 seconds to move but _was_ reporting intermediate positions (e.g., 18%), the driver still fired a jitter and retry because the target had not been reached. The fix was the "Heartbeat Check" – checking for movement at 8 seconds. If the shade is moving, wait. If it's deaf, fire jitter. If it's stuck (no position change), abort.

#### Lessons Learned (v2.5.7 – Repeater Flooding)

In late October 2026, a single shade (XDG) began failing repeatedly. Deep-dive analysis of the logs revealed that during a failure, the driver was sending 5 RF commands in 60 seconds. This flooded the RF repeater's internal buffer, causing it to lock up in a boot loop (flashing LED). This affected other shades sharing the repeater. The fix was to implement Exponential Backoff (increasing wait times) and a hard cap on the number of retries, ensuring the repeater is never overwhelmed by a failing shade.

#### Lessons Learned (v2.5.5 – Retry Logic Deep Dive)

The chatty logs observed in September 2026 on BSG (Kitchen) and I39 (Office) were initially thought to be a workflow problem. A deep-dive analysis revealed three separate issues:

* **Exact-match confirmation was too strict.** Shades physically stop 1–2% short of the target, and the driver treated this as a failure. The fix is a `Position Tolerance` preference.

* **Slow shades report intermediate positions.** The driver only recognised the exact target position as "confirmed", ignoring the fact that intermediate reports (e.g., 51% for a 53% target) proved the shade was moving. The fix is an intermediate position reset – up to 2 resets before the timer is allowed to expire.

* **Latent bugs in the retry state handling.** `unschedule()` was calling non-existent method names, and there was no stale-callback guard. Both fixed in this release.

#### Lessons Learned (v2.5.x – Retry & Jitter)

During the development of the retry and jitter logic, we discovered several important issues:

* **`logWarn` vs `logWarning`** – a simple typo caused a `MissingMethodException` that silently killed the retry logic. This was fixed in v2.5.3.

* **`getChildDevices()` in a child driver** – this method always returns empty in a child driver, making the original jitter ID selection useless. The fix was to use the shade's own `motorAddress` instead.

* **Jitter counting** – we now count the jitter attempt as one of the retry attempts so the user's configured `cmdRetryCount` is respected.

* **State hygiene** – `pendingTarget` is now cleared immediately on confirmation, preventing stale state from affecting later commands.

#### Lessons Learned (v2.4.0 – v2.4.2)

During development, we experimented with removing the `Switch` capability and using a `developer` block to hide On/Off buttons. The `developer` block failed because it is not supported in drivers. We then tried explicit `command "on"` and `command "off"` declarations, which restored Alexa functionality but kept the buttons visible. The final solution (v2.4.3) removed the `Switch` capability entirely and relied on the `Window Shade` and `Switch Level` capabilities for Alexa control – giving us a clean UI without losing voice control.

#### v2.4.3 - 2026-07-14 - David Ball-Quenneville

* Removed explicit on/off command declarations – Alexa uses Window Shade and Switch Level capabilities directly.

* Removed unsupported developer block.

* Clean UI: no On/Off buttons, just Open/Close.

* Proactive state updates retained.

#### v2.3.9 - 2026-04-07 - David Ball-Quenneville

* Added stub implementations for `StartPositionChange` and `StopPositionChange` (logs INFO).

* Added duration parameter warning to `setLevel` (logs INFO if non‑zero duration supplied).

* No functional changes; improves user feedback for unsupported UI commands.

#### v2.3.8 - 2026-04-07 - David Ball-Quenneville

* Prevent duplicate position confirmation logs: only log when position actually changes.

#### v2.3.7 - 2026-04-07 - David Ball-Quenneville

* Added INFO logs for command initiation and final position confirmation.

#### v2.3.6 - 2026-04-07 - David Ball-Quenneville

* Fixed preference access in logging helpers (`settings.` prefix).

#### v2.3.5 - 2026-04-05 - David Ball-Quenneville

* Added `Auto-Revert Debug` preference. Reordered preferences for better UX.

#### v2.3.4 - 2026-04-05 - David Ball-Quenneville

* Added manual `Request Battery Status` command.

#### v2.3.3 - 2026-04-05 - David Ball-Quenneville

* Added battery offset preference (`batteryOffset`) to allow user calibration of reported battery percentage.

#### v2.3.2 - 2026-03-30 - David Ball-Quenneville

* Added auto-repair of parent link on `initialize` to prevent loss of reference after power cycles.

* Fixed parent discovery using `getAllDevices()` instead of `getChildDevices()`.

#### v2.3.1 - 2026-03-20 - David Ball-Quenneville

* Removed `status` attribute and subscription to parent (not supported in drivers).

* Eliminated `subscribe()` call causing `MissingMethodException`.

#### v2.3.0 - 2026-03-20 - David Ball-Quenneville

* Aligned logging with parent driver.

* Replaced `debug` preference with `logEnable` and `txtEnable`.

* Added auto‑disable of debug logging after 30 minutes.

#### v2.2.5 - 2026-03-20 - David Ball-Quenneville

* Added initial attribute values in `initialize()` to give Alexa a baseline state.

#### v2.2.4 - 2026-03-20 - David Ball-Quenneville

* Added immediate position and level events for Alexa responsiveness.

#### v2.2.3 - 2026-03-20 - David Ball-Quenneville

* Fixed `GroovyCastException` in `setPosition` by safely converting `device.currentValue("position")` to integer.

#### v2.2.2 - 2026-03-20 - David Ball-Quenneville

* Fixed `GroovyCastException` in `setLevel` by explicitly converting argument to integer.

#### v2.2.1 - 2026-03-20 - David Ball-Quenneville

* Added missing `setLevel` method to satisfy `Switch Level` capability.

#### v2.2.0 - 2026-03-19 - David Ball-Quenneville

* Added proactive state updates on command to fix Alexa timeout issues.

* Immediately sets `windowShade`, `moving`, and `switch` attributes when a command is sent.

* Position is only updated after hub confirmation to avoid inaccurate reporting.

* Subscribes to parent's `status` attribute for status mirroring.

#### v2.1.2 - 2026-03-09 - David Ball-Quenneville

* Improved string cleaning, voltage parsing, battery percentage, RSSI.

#### v2.0.0 - 2026-03-09 - David Ball-Quenneville

* Maintenance takeover, Apache license, fixed `ClassCastException`.

#### v2020.06.08.01 - Younes Oughla

* Added `opening` and `closing` states, `toggle` command.

#### v2020.03.30 - Younes Oughla

* Initial Release.

