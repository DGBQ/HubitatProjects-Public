# Xiaomi Aqara Contact Sensor – CHANGELOG

#### v1.0.11 - 2026-04-27 - David Ball-Quenneville
- **Added Auto-Revert Debug preference** (`autoRevertDebug`, default `true`).
  - When enabled, debug logging (`logLevel = 1`) automatically turns off after 30 minutes.
  - When disabled, debug stays on until manually turned off.
  - The `updated()` method now schedules or cancels the auto‑off based on the preference and current log level.
  - The `logsOff()` method only disables debug if it is currently enabled.
- **Removed the `HealthCheck` capability** from the metadata.
  - This eliminates the stray "Ping" button that the platform automatically injects.
  - The custom `healthStatus` attribute and the `healthCheck()` method remain fully functional – health monitoring is unaffected.
- **Restored the standard header format** with full Apache 2.0 license, purpose statement, and revision history inside the header (as requested).
- **Minor cleanup** of comments and preference initialisation.

#### v1.0.10 - 2026-04-25 - David Ball-Quenneville
- **Based on the fully stable v1.0.8** – re‑added only the safe `state.lastTx = now()` updates in user commands without altering any parsing or binding logic.
- **Fixed the cosmetic `lastTx = 0` issue**:
  - In `configure()`, after clearing state, `state.lastTx = now()` is set immediately.
  - In `refresh()` (manual call), `state.lastTx = now()` is set at the start.
  - `configureApply()` also sets `state.lastTx = now()` after sending commands.
- **No changes to Zigbee handling** – contact toggling, battery reporting, and health checks work exactly as in v1.0.8.
- This version was confirmed stable after re‑pairing the sensor (the previous regression in v1.0.9 was due to a lost Zigbee binding, not a driver bug).

#### v1.0.9 - 2026-04-25 - David Ball-Quenneville
- **Attempted to fix `lastTx = 0`** by adding `state.lastTx = now()` inside `configure()`, `configureApply()`, and `refresh()`.
- **However, some users experienced a loss of contact reporting** after installing this version.
- Investigation showed the issue was caused by the sensor losing its Zigbee binding (possibly triggered by the state clear), not by the added `lastTx` lines.
- **This version is deprecated** – users should use v1.0.10 or later, which re‑implements the same `lastTx` fix in a way that does not interfere with pairing.

#### v1.0.8 - 2026-04-25 - David Ball-Quenneville
- **Fixed version tracking** in `updated()`: added `state.lastCx = DRIVER_VERSION` so that the version synchronises immediately after saving the driver code, without needing to click **Configure**.
- **Minor internal cleanup** (removed unused variables, improved comments).
- This is the first fully stable release with all core functionality (contact, battery, health check, version tracking) verified.

#### v1.0.7 - 2026-04-25 - David Ball-Quenneville
- **Fixed version‑tracking bug** in `configure()`: now always sets `state.lastCx` to the current driver version, instead of preserving an old saved value.
- Previously the version could remain stuck on an older value after a driver update.

#### v1.0.6 - 2026-04-25 - David Ball-Quenneville
- **Fixed critical `NullPointerException`** in `configure()` caused by a nested list from `refresh()`.
- Changed `cmds += refresh(true)` to `cmds.addAll(refresh(true))` – this flattens the list correctly.
- Improved `sendZigbee()` to safely flatten and filter commands, avoiding `ArrayList.startsWith()` errors.
- Resolved the `java.lang.ArrayIndexOutOfBoundsException` and the `startsWith on ArrayList` errors reported in the logs.

#### v1.0.5 - 2026-04-25 - David Ball-Quenneville
- **Replaced broken `runIn` calculation** in `configure()` with a fixed 5‑second delay.
- Simplified command list handling – removed the dynamic counting of commands that caused a `startsWith` error on a list element.
- This allowed `configure()` to complete without exceptions.

#### v1.0.4 - 2026-04-25 - David Ball-Quenneville
- **Fixed contact state parsing** – converted `attrInt` from a hex string to an integer so the `case` statement correctly matched `contact` reports.
- Added debug logging for unmatched messages to ease future troubleshooting.
- This was the key change that made contact toggling work reliably.

#### v1.0.3 - 2026-04-25 - David Ball-Quenneville
- **Fixed `NullPointerException` in logging helpers** when the `logLevel` preference was `null`.
- Added early initialisation of `logLevel` in `installed()` and `configure()` to ensure a default value exists before any logging occurs.

#### v1.0.2 - 2026-04-25 - David Ball-Quenneville
- **Fixed `NullPointerException` in `configure()`** due to missing `device.endpointId` or `device.zigbeeId` (added safety checks).
- **Removed the `ping` command** – it is not useful for battery‑powered sleepy sensors and could cause confusion. The `ping` function is not supported by the hardware in a reliable way.

#### v1.0.1 - 2026-04-25 - David Ball-Quenneville
- **Fixed `NullPointerException`** when parsing Zigbee messages that have no `value` field (e.g., configure responses or default responses).
- Added a guard clause in `parse()` to ignore messages without a `value`.

#### v1.0.0 - 2026-04-24 - David Ball-Quenneville
- **Initial release** – a clean, specialised driver for the Xiaomi Aqara contact sensor.
- Based on the IKEA Parasoll driver (Dan Danache) and the Xiaomi Aqara Mijia universal driver (Jonathan Michaelsen).
- Supports **Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01)** and **Mijia Door/Window Sensor (lumi.sensor_magnet)**.
- Implemented core capabilities: `Configuration`, `Refresh`, `Sensor`, `ContactSensor`, `Battery`, `HealthCheck`.
- **Added reliability features:**
  - Health status tracking (`online` / `offline` / `unknown`) with hourly checks (offline after 12 hours of silence).
  - Network rejoin counter for diagnosing Zigbee mesh stability.
  - Contact state inversion preference (swap `open` / `closed` if the sensor is installed backwards).
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
  - Presence detection (replaced by the standard `HealthCheck` capability).

---

**Note:** Versions 1.0.9 is deprecated; v1.0.10 and v1.0.11 are the recommended stable releases. The driver is fully functional for contact reporting, battery monitoring, and health checks.