# Rollease Acmeda Hub (Parent) – CHANGELOG

####  v3.3.10 – 2026-03-20 – David Ball-Quenneville
- Changed hubPort preference from number to text to avoid comma formatting.
- Added port validation with fallback to default if invalid.

#### v3.3.9 – 2026-03-20 – David Ball-Quenneville
- Added hubPort preference (default 1487) for configurable telnet port.
- Removed temporary CleanupOldState command.

####  v3.3.8 – 2026-03-20 – David Ball-Quenneville
- Fixed NullPointerException in rebuildShadeList by using safe navigation.

####  v3.3.7 – 2026-03-20 – David Ball-Quenneville
- Fixed reading of child motor address: replaced child.getPreference() with child.settings.motorAddress.

####  v3.3.6 – 2026-03-20 – David Ball-Quenneville
- Added temporary command CleanupOldState.

####  v3.3.5 – 2026-03-20 – David Ball-Quenneville
- Removed all unused state variables.

####  v3.3.4 – 2026-03-20 – David Ball-Quenneville
- Fixed shade list population.

####  v3.3.3 – 2026-03-20 – David Ball-Quenneville
- Fixed reading of motor addresses from child preferences.

####  v3.3.2 – 2026-03-20 – David Ball-Quenneville
- Added automatic update of shades attribute.

####  v3.3.1 – 2026-03-20 – David Ball-Quenneville
- Added attribute "shades".

####  v3.3.0 – 2026-03-20 – David Ball-Quenneville
- Renamed custom commands for UI grouping.

####  v3.2.0 – 2026-03-20 – David Ball-Quenneville
- Added shade management commands.

####  v3.1.0 – 2026-03-19 – David Ball-Quenneville
- Added auto‑disable debug logging, human‑readable states, standardized logging.

####  v3.0.1 – 2026-03-19 – David Ball-Quenneville
- Added explicit unschedule("checkIdle").

####  v3.0.0 – 2026-03-19 – David Ball-Quenneville
- Added Surgical Error Silencer.

####  v2020.06.08.1 – Younes Oughla
- Added "opening" and "closing" states, toggle command.

####  v2020.03.30 – Younes Oughla
- Initial Release.
