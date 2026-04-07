# Rollease Acmeda Shade (Child) – CHANGELOG

#### v2.3.9 - 2026-04-07 - David Ball-Quenneville
 - Added stub implementations for StartPositionChange and StopPositionChange (logs INFO).
 - Added duration parameter warning to setLevel (logs INFO if non‑zero duration supplied).
 - No functional changes; improves user feedback for unsupported UI commands.
   
####  v2.3.8 - 2026-04-07 - David Ball-Quenneville
  - Prevent duplicate position confirmation logs: only log when position actually changes.

#### v2.3.7 - 2026-04-07 - David Ball-Quenneville
- Added INFO logs for command initiation and final position confirmation.

#### v2.3.6 - 2026-04-07 - David Ball-Quenneville
- Fixed preference access in logging helpers (settings. prefix).

#### v2.3.5 - 2026-04-05 - David Ball-Quenneville
 - Added Auto-Revert Debug preference. Reordered preferences for better UX.

#### v2.3.4 - 2026-04-05 - David Ball-Quenneville
 - Added manual "Request Battery Status" command.

#### v2.3.3 - 2026-04-05 - David Ball-Quenneville
 - Added battery offset preference (batteryOffset) to allow user calibration of reported battery percentage.

#### v2.3.2 - 2026-03-30 - David Ball-Quenneville
 - Added auto-repair of parent link on initialize to prevent loss of reference after power cycles.
- Fixed parent discovery using getAllDevices() instead of getChildDevices().

#### v2.3.1 - 2026-03-20 - David Ball-Quenneville
- Removed status attribute and subscription to parent (not supported in drivers).
- Eliminated subscribe() call causing MissingMethodException.

#### v2.3.0 - 2026-03-20 - David Ball-Quenneville
- Aligned logging with parent driver.

#### v2.2.5 - 2026-03-20 - David Ball-Quenneville
- Added initial attribute values in initialize().

#### v2.2.4 - 2026-03-20 - David Ball-Quenneville
- Added immediate position and level events.

#### v2.2.3 - 2026-03-20 - David Ball-Quenneville
- Fixed GroovyCastException in setPosition.

#### v2.2.2 - 2026-03-20 - David Ball-Quenneville
- Fixed GroovyCastException in setLevel.

#### v2.2.1 - 2026-03-20 - David Ball-Quenneville
- Added missing setLevel method.

#### v2.2.0 - 2026-03-19 - David Ball-Quenneville
- Added proactive state updates.

#### v2.1.2 - 2026-03-09 - David Ball-Quenneville
- Improved string cleaning, voltage parsing, battery percentage, RSSI.

#### v2.0.0 - 2026-03-09 - David Ball-Quenneville
- Maintenance takeover, Apache license, fixed ClassCastException.

#### v2020.06.08.01 - Younes Oughla
- Added "opening" and "closing" states, toggle command.

#### v2020.03.30 - Younes Oughla
- Initial Release.
