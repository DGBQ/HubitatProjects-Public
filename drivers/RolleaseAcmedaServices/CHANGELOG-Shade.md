# Rollease Acmeda Shade (Child) – CHANGELOG

#### v2.5.4 - 2026-09-11 - David Ball-Quenneville
- **HOTFIX – Battery Voltage Formula Recalibrated:**
  - Changed the voltage-to-percentage range from **10.8V–12.6V** to **9.5V–12.6V**.
  - This brings driver-reported battery percentages into close alignment with the Rollease app (tested within ±5% across eight shades of mixed types – roman and roller, external and embedded batteries).
  - The `Battery Offset` preference remains available for per-shade fine-tuning (±30%).
- No changes to movement, retry, jitter, logging, or preferences.

#### v2.5.3 - 2026-09-02 - David Ball-Quenneville
- **Fixed** `MissingMethodException` – replaced all `logWarn` calls with the correct `logWarning` method. This bug caused the retry logic to crash the moment a retry was attempted.
- **Improved** RF jitter ID selection – now uses the shade's own `motorAddress` instead of `getChildDevices()` (which always returned empty in the child driver).
- **Cleaned** retry state – `state.pendingTarget` is now cleared immediately upon confirmation to prevent stale state.
- **Counted** jitter as one of the retry attempts – ensures the user-configured `cmdRetryCount` is respected.

#### v2.5.2 - 2026-09-02 - David Ball-Quenneville
- Added **confirmation-based retry logic** – commands are now retried only if the Hub does not confirm the target position.
  - New preferences: `Command Retry Count` (default 2) and `Command Retry Wait Time` (default 10s).
- Added **RF Jitter workaround** – if all retries fail, the driver sends a harmless status request (`!IDr?`) to wake the Hub's RF transmitter, then retries the original command.
  - New preference: `Enable RF Jitter` (default On).
- Grouped retry preferences between `Battery Offset` and `Enable Description Logging` for better UI flow.
- Removed `Keep Debug Logging On` – consolidated into `Auto-Revert Debug`.

#### v2.5.1 - 2026-09-02 - David Ball-Quenneville
- Added initial command retry feature (position-based). Superseded by the improved confirmation-based logic in v2.5.2.

#### v2.4.4 - 2026-07-25 - David Ball-Quenneville
- No functional changes – compatible with Hub v3.3.23.
- Proactive state updates retained.
- Clean UI (On/Off buttons hidden).

#### Lessons Learned (v2.5.x – Retry & Jitter)
During the development of the retry and jitter logic, we discovered several important issues:
- **`logWarn` vs `logWarning`** – a simple typo caused a `MissingMethodException` that silently killed the retry logic. This was fixed in v2.5.3.
- **`getChildDevices()` in a child driver** – this method always returns empty in a child driver, making the original jitter ID selection useless. The fix was to use the shade's own `motorAddress` instead.
- **Jitter counting** – we now count the jitter attempt as one of the retry attempts so the user's configured `cmdRetryCount` is respected.
- **State hygiene** – `pendingTarget` is now cleared immediately on confirmation, preventing stale state from affecting later commands.

#### Lessons Learned (v2.4.0 – v2.4.2)
During development, we experimented with removing the `Switch` capability and using a `developer` block to hide On/Off buttons. The `developer` block failed because it is not supported in drivers. We then tried explicit `command "on"` and `command "off"` declarations, which restored Alexa functionality but kept the buttons visible. The final solution (v2.4.3) removed the `Switch` capability entirely and relied on the `Window Shade` and `Switch Level` capabilities for Alexa control – giving us a clean UI without losing voice control.

#### v2.4.3 - 2026-07-14 - David Ball-Quenneville
- Removed explicit on/off command declarations – Alexa uses Window Shade and Switch Level capabilities directly.
- Removed unsupported developer block.
- Clean UI: no On/Off buttons, just Open/Close.
- Proactive state updates retained.

#### v2.3.9 - 2026-04-07 - David Ball-Quenneville
- Added stub implementations for `StartPositionChange` and `StopPositionChange` (logs INFO).
- Added duration parameter warning to `setLevel` (logs INFO if non‑zero duration supplied).
- No functional changes; improves user feedback for unsupported UI commands.

#### v2.3.8 - 2026-04-07 - David Ball-Quenneville
- Prevent duplicate position confirmation logs: only log when position actually changes.

#### v2.3.7 - 2026-04-07 - David Ball-Quenneville
- Added INFO logs for command initiation and final position confirmation.

#### v2.3.6 - 2026-04-07 - David Ball-Quenneville
- Fixed preference access in logging helpers (`settings.` prefix).

#### v2.3.5 - 2026-04-05 - David Ball-Quenneville
- Added `Auto-Revert Debug` preference. Reordered preferences for better UX.

#### v2.3.4 - 2026-04-05 - David Ball-Quenneville
- Added manual `Request Battery Status` command.

#### v2.3.3 - 2026-04-05 - David Ball-Quenneville
- Added battery offset preference (`batteryOffset`) to allow user calibration of reported battery percentage.

#### v2.3.2 - 2026-03-30 - David Ball-Quenneville
- Added auto-repair of parent link on `initialize` to prevent loss of reference after power cycles.
- Fixed parent discovery using `getAllDevices()` instead of `getChildDevices()`.

#### v2.3.1 - 2026-03-20 - David Ball-Quenneville
- Removed `status` attribute and subscription to parent (not supported in drivers).
- Eliminated `subscribe()` call causing `MissingMethodException`.

#### v2.3.0 - 2026-03-20 - David Ball-Quenneville
- Aligned logging with parent driver.
- Replaced `debug` preference with `logEnable` and `txtEnable`.
- Added auto‑disable of debug logging after 30 minutes.

#### v2.2.5 - 2026-03-20 - David Ball-Quenneville
- Added initial attribute values in `initialize()` to give Alexa a baseline state.

#### v2.2.4 - 2026-03-20 - David Ball-Quenneville
- Added immediate position and level events for Alexa responsiveness.

#### v2.2.3 - 2026-03-20 - David Ball-Quenneville
- Fixed `GroovyCastException` in `setPosition` by safely converting `device.currentValue("position")` to integer.

#### v2.2.2 - 2026-03-20 - David Ball-Quenneville
- Fixed `GroovyCastException` in `setLevel` by explicitly converting argument to integer.

#### v2.2.1 - 2026-03-20 - David Ball-Quenneville
- Added missing `setLevel` method to satisfy `Switch Level` capability.

#### v2.2.0 - 2026-03-19 - David Ball-Quenneville
- Added proactive state updates on command to fix Alexa timeout issues.
- Immediately sets `windowShade`, `moving`, and `switch` attributes when a command is sent.
- Position is only updated after hub confirmation to avoid inaccurate reporting.
- Subscribes to parent's `status` attribute for status mirroring.

#### v2.1.2 - 2026-03-09 - David Ball-Quenneville
- Improved string cleaning, voltage parsing, battery percentage, RSSI.

#### v2.0.0 - 2026-03-09 - David Ball-Quenneville
- Maintenance takeover, Apache license, fixed `ClassCastException`.

#### v2020.06.08.01 - Younes Oughla
- Added `opening` and `closing` states, `toggle` command.

#### v2020.03.30 - Younes Oughla
- Initial Release.