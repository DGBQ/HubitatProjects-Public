# Xiaomi Aqara Contact Sensor – CHANGELOG

#### v1.0.8 - 2026-04-25 - David Ball-Quenneville
- Fixed version tracking: added `state.lastCx = DRIVER_VERSION` in `updated()` method.
- Ensures the driver version synchronises immediately after saving the code, without needing to click **Configure**.
- Minor internal cleanup.

#### v1.0.7 - 2026-04-25 - David Ball-Quenneville
- Fixed version‑tracking bug in `configure()`: now always sets `state.lastCx` to the current driver version.
- Previously the version could remain stuck on an older value.

#### v1.0.6 - 2026-04-25 - David Ball-Quenneville
- Fixed critical `NullPointerException` in `configure()` caused by nested list from `refresh()`.
- Changed `cmds += refresh(true)` to `cmds.addAll(refresh(true))` and improved `sendZigbee()` to flatten all commands.
- This resolved the `java.lang.ArrayIndexOutOfBoundsException` and `startsWith on ArrayList` errors.

#### v1.0.5 - 2026-04-25 - David Ball-Quenneville
- Replaced broken `runIn` calculation in `configure()` with a fixed 5‑second delay (avoided `startsWith` error on a list).
- Simplified command list handling.

#### v1.0.4 - 2026-04-25 - David Ball-Quenneville
- **Fixed contact state parsing** – converted `attrInt` from hex string to integer so the `case` statement correctly matched `contact` reports.
- Added debug logging for unmatched messages to ease future troubleshooting.

#### v1.0.3 - 2026-04-25 - David Ball-Quenneville
- Fixed `NullPointerException` in logging helpers when `logLevel` preference was null.
- Added early initialisation of `logLevel` in `installed()` and `configure()`.

#### v1.0.2 - 2026-04-25 - David Ball-Quenneville
- Fixed `NullPointerException` in `configure()` due to missing `device.endpointId` or `device.zigbeeId`.
- **Removed the `ping` command** (it is not useful for battery‑powered sleepy sensors and could cause confusion).

#### v1.0.1 - 2026-04-25 - David Ball-Quenneville
- Fixed `NullPointerException` when parsing Zigbee messages that have no `value` field (e.g., configure responses).

#### v1.0.0 - 2026-04-24 - David Ball-Quenneville
- **Initial release** – a clean, specialised driver for the Xiaomi Aqara contact sensor.
- Based on the IKEA Parasoll driver (Dan Danache) and the Xiaomi Aqara Mijia universal driver (Jonathan Michaelsen).
- Supports **Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01)** and **Mijia Door/Window Sensor (lumi.sensor_magnet)**.
- Implemented core capabilities: `Configuration`, `Refresh`, `Sensor`, `ContactSensor`, `Battery`, `HealthCheck`.
- **Added reliability features:**
  - Health status tracking (`online` / `offline` / `unknown`) with hourly checks (offline after 12 hours of silence).
  - Network rejoin counter for diagnosing Zigbee mesh stability.
  - Contact state inversion preference (swap `open` / `closed` if sensor is installed backwards).
  - Log verbosity preference (`Debug`, `Info`, `Warning`, `Error`) with auto‑revert from `Debug` to `Info` after 30 minutes.
- **Retained essential Xiaomi handling:**
  - Proprietary `FF01` / `FF02` attribute parsing for battery and contact when standard Zigbee clusters are not used.
  - Support for both standard `OnOff` cluster (0x0006) and proprietary methods.
- **Removed all clutter (compared to the universal driver):**
  - Unrelated capabilities: `IlluminanceMeasurement`, `RelativeHumidityMeasurement`, `TemperatureMeasurement`, `PressureMeasurement`, `AccelerationSensor`, `MotionSensor`, `WaterSensor`.
  - Button capabilities (`PushableButton`, `HoldableButton`, `DoubleTapableButton`, `ReleasableButton`).
  - Unrelated device fingerprints (motion, vibration, weather, water leak, remote switches).
  - Virtual commands (`open`, `closed`, `push`, `hold`, `doubleTap`, `release`, `shake`, `wet`, `dry`).
  - Device binding and firmware update features (not supported by the contact sensor).
  - Presence detection (replaced by standard `HealthCheck`).

---

**Note:** Versions 1.0.1 through 1.0.8 contain no functional changes to the core contact/battery reporting beyond fixing parsing and stability issues. The driver is now considered **stable** and ready for production use.