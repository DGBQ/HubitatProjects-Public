# Rollease Acmeda Hub (Parent) – CHANGELOG

#### v3.3.26 - 2026-11-04 - David Ball-Quenneville

* **HOTFIX – `refreshAllChildren` Blocking Issue:**

  * **The Problem:** In v3.3.25, the child refresh loop was moved out of `initialize()` to prevent UI blocking during a network outage. However, `refreshAllChildren()` was still hanging for up to 160 seconds if the Telnet connection was dead. This caused a new warning in the logs: `method refreshAllChildren of device Pulse 2 Hub ran for 169,243ms`.

  * **The Fix:** Added a connection state guard at the top of `refreshAllChildren()`. The driver now checks the `connectionState` attribute. If the state is not `connected` or `idle`, the method logs a debug message and exits immediately. This prevents the driver from attempting to send Telnet commands while the socket is dead.

  * **Result:** The 160-second blocking warning is eliminated. If the Hubitat hub reboots and the network is not ready, the driver will skip the child refresh and wait for the next successful `initialize()`.

#### v3.3.25 - 2026-10-28 - David Ball-Quenneville

* **PHASE 2B – Logging UX Overhaul & Initialize Fix:**

  * **Logging UX:** Replaced `logEnable`, `txtEnable`, and `keepDebugOn` with a unified `logVerbosity` dropdown (Enum: ERROR, WARN, INFO, DEBUG; default: INFO) to match the Child Shade driver.

  * **Auto-Revert Debug:** Updated logic to revert `Log Verbosity` from `DEBUG` back to `INFO` after 30 minutes.

  * **UI Cleanup:** Removed `capability "Refresh"` from the metadata block. The Parent Hub no longer displays a no-op Refresh button, which was confusing since `Initialize` already auto-refreshes all child shades.

  * **Initialize Fix:** Added diagnostic timestamps around `telnetConnect()`. Moved the child refresh loop (`getChildDevices().each { child.refresh() }`) out of the main `initialize()` method and into a separate scheduled method (`runIn(3, "refreshAllChildren")`). This prevents the driver from blocking the UI thread for up to 140 seconds when the network is unreachable during a Hubitat reboot.

#### v3.3.24 - 2026-09-11 - David Ball-Quenneville

* **Phase 1 – Critical Bug Fixes:**

  * `ShadeAdd()`: Added `child.initialize()` after setting `motorAddress` so new child devices are initialized immediately and receive the correct address from the start.

  * `parse()`: Added `child.initialize()` when creating a new child. Also added validation for existing children – if the child's `motorAddress` preference does not match the incoming address, it is now auto-corrected.

  * `ShadeRemove()`: Added a safety check that compares the child's `motorAddress` against the requested `motorId` before deletion. If they do not match (or the address is unset), deletion is aborted with a warning to prevent removing the wrong device.

* These fixes address the root cause of the recent incident where a child device was deleted unexpectedly due to stale parent-side address mapping.

* No changes to logging, preferences, or connection handling in this release – those are scheduled for Phase 2 (Logging UX) and Phase 3 (Health Check).

#### v3.3.23 - 2026-07-25 - David Ball-Quenneville

* Added `connectionState` attribute (`connected`, `connecting`, `idle`, `error`).

* Added `lastConnectionChange` attribute (timestamp of last state change).

* Added `status` mirroring – `status` always matches `connectionState`.

* Added Auto Child Refresh – all child shades are refreshed automatically after `Initialize`.

* Added `keepDebugOn` preference – prevents debug logging from auto‑disabling.

* All recovery features remain disabled (Health Check = Disabled, Command Queue = Disabled, Auto‑Refresh = Disabled).

#### Lessons Learned (v3.3.13 – v3.3.22)

During development, we introduced aggressive recovery features (Health Check, Command Queue, Auto‑Refresh) to address telnet instability. While well‑intentioned, these features caused connection storms and resource exhaustion when the hub was already unstable. The aggressive reconnect logic in v3.3.16–v3.3.21 proved to be the root cause of the storms. With Ethernet now providing a stable network foundation, we are reintroducing features gently and incrementally – starting with visibility (Phase 1), followed by recovery (Phase 2), and finally convenience (Phase 3).

#### v3.3.12 - 2026-04-07 - David Ball-Quenneville

* Fixed preference access in logging helpers (now uses `settings.` prefix) to ensure reliable INFO logs.

#### v3.3.11 - 2026-04-06 - David Ball-Quenneville

* Added Auto-Revert Debug preference to control automatic disabling of debug logs.

* Removed redundant custom commands SendMsg and SendPulse (kept built‑in sendMsg from Telnet capability).

* Updated logsOff() to respect autoRevertDebug setting.

#### v3.3.10 – 2026-03-20 – David Ball-Quenneville

* Changed hubPort preference from number to text to avoid comma formatting.

* Added port validation with fallback to default if invalid.

#### v3.3.9 – 2026-03-20 – David Ball-Quenneville

* Added hubPort preference (default 1487) for configurable telnet port.

* Removed temporary CleanupOldState command.

#### v3.3.8 – 2026-03-20 – David Ball-Quenneville

* Fixed NullPointerException in rebuildShadeList by using safe navigation.

#### v3.3.7 – 2026-03-20 – David Ball-Quenneville

* Fixed reading of child motor address: replaced child.getPreference() with child.settings.motorAddress.

#### v3.3.6 – 2026-03-20 – David Ball-Quenneville

* Added temporary command CleanupOldState.

#### v3.3.5 – 2026-03-20 – David Ball-Quenneville

* Removed all unused state variables.

#### v3.3.4 – 2026-03-20 – David Ball-Quenneville

* Fixed shade list population.

#### v3.3.3 – 2026-03-20 – David Ball-Quenneville

* Fixed reading of motor addresses from child preferences.

#### v3.3.2 – 2026-03-20 – David Ball-Quenneville

* Added automatic update of shades attribute.

#### v3.3.1 – 2026-03-20 – David Ball-Quenneville

* Added attribute "shades".

#### v3.3.0 – 2026-03-20 – David Ball-Quenneville

* Renamed custom commands for UI grouping.

#### v3.2.0 – 2026-03-20 – David Ball-Quenneville

* Added shade management commands.

#### v3.1.0 – 2026-03-19 – David Ball-Quenneville

* Added auto‑disable debug logging, human‑readable states, standardized logging.

#### v3.0.1 – 2026-03-19 – David Ball-Quenneville

* Added explicit unschedule("checkIdle").

#### v3.0.0 – 2026-03-19 – David Ball-Quenneville

* Added Surgical Error Silencer.

#### v2020.06.08.1 – Younes Oughla

* Added "opening" and "closing" states, toggle command.

#### v2020.03.30 – Younes Oughla

* Initial Release.
