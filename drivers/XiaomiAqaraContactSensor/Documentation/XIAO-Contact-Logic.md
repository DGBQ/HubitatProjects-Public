# Master Functional Specifications: Xiaomi Aqara Contact Sensor Logic

**File Name:** XIAO-Contact-Logic.md (Xiaomi Integration & Automation Overwatch Contact Sensor Logic)  
**Project Branch:** The Clean Contact Driver  
**Integration Logic:** Standalone Device Architecture with Health Monitoring  
**Document Status:** Final Production Specification

---

## 1. Project Context & Technical Philosophy

### 1.1 The Need for a Specialized Driver
The original universal driver by **Jonathan Michaelsen** supported dozens of Xiaomi sensors (motion, vibration, weather, buttons, water leak) but included unnecessary capabilities and code paths that added complexity and potential failure points for a simple contact sensor. The IKEA Parasoll driver by **Dan Danache** introduced a modern framework with health checks, ping commands, and structured logging.

This driver merges the best of both: it retains **proprietary Xiaomi Zigbee parsing** (for sensors that do not use standard clusters) while adopting the **IKEA driver’s robust framework** – including health status tracking, configurable logging, and contact state inversion. All unrelated device code has been stripped away, leaving a lean, reliable driver that does one thing well: reporting open/closed state and battery level.

### 1.2 The Challenge of Battery-Powered Zigbee Sensors
Xiaomi contact sensors are **battery‑powered** and spend most of their time in a deep sleep to conserve energy. They wake up only when:
- The magnet is opened or closed (reporting the change immediately).
- The internal reset button is pressed.
- A scheduled battery report occurs (typically every 50–60 minutes).

Because the sensor is often asleep, commands like `Refresh` may fail unless the user wakes the device first. This driver handles this gracefully by logging a reminder and queuing the request; it does not treat a missed wake‑up as an error.

### 1.3 Health Monitoring for “Set‑and‑Forget” Reliability
To provide confidence that a sensor is still operational, the driver implements a **Health Check** capability:
- Every hour, the driver checks the timestamp of the last received Zigbee message (`state.lastRx`).
- If no message has been received in the last 12 hours, the `healthStatus` attribute is set to `offline`.
- As soon as any message arrives (contact change, battery report, or a successful ping), the status returns to `online`.

This allows users to create automations that alert them when a sensor goes offline (e.g., battery dead or out of range).

> **Note:** The official `HealthCheck` capability has been removed from the driver metadata to eliminate the stray "Ping" button. The custom `healthStatus` attribute and the `healthCheck()` method remain fully functional – health monitoring is unchanged.

---

## 2. Full Command API (Methods)

The following commands are available on the device page. All commands are synchronous and respect the sensor’s sleep cycle.

| Command | Description | Implementation Notes |
| :--- | :--- | :--- |
| **`configure()`** | Sets up Zigbee bindings and reporting intervals. | Binds to clusters `0x0001` (Power Configuration) and `0x0006` (On/Off). Configures battery reporting every 5 hours (delta 1%) and contact reporting immediately on change. Also puts the device into identify mode (blinking LED) for 30 seconds. |
| **`refresh()`** | Reads the current contact state and battery percentage. | Issues `readAttribute` for `0x0006` (OnOff) and `0x0001` (BatteryPercentage). Logs a reminder if the device is battery‑powered: *“Click Refresh immediately after opening/closing the contact to wake the device!”* |

> **Note:** No virtual commands (e.g., `open`, `closed`, `push`) are exposed because the sensor is a passive device. It does not accept commands to change its physical state. The `ping` command is not supported (removed in v1.0.2 as it is not useful for battery‑powered sleepy sensors).

---

## 3. User Preferences & Configuration

These inputs allow the user to tailor logging and contact interpretation.

| Preference | Format | Default | Description |
| :--- | :--- | :--- | :--- |
| **Log verbosity** | Enum (`1`, `2`, `3`, `4`) | `1` | `1` = Debug (log everything), `2` = Info (important events), `3` = Warning, `4` = Error. |
| **Auto-Revert Debug** | Boolean | `true` | When enabled, Debug logging automatically turns off after 30 minutes. Disable to keep debug on indefinitely (for extended troubleshooting). |
| **Invert contact state** | Boolean | `false` | When enabled, swaps `open` and `closed` reports. Useful if the sensor is physically installed backwards (magnet on the door frame, sensor on the moving part). |

No other preferences are required. The driver automatically configures reporting intervals and bindings during `configure()`.

---

## 4. Telemetry & Current States (Attributes)

The driver exposes the following attributes for use in dashboards, Rule Machine, and Alexa/Google Home.

| Attribute | Type | Values / Unit | Description |
| :--- | :--- | :--- | :--- |
| **`contact`** | String | `open`, `closed` | The primary door/window state. This is the main attribute used for automations. |
| **`battery`** | Integer | 0–100% | Battery charge percentage, derived from voltage (2.0V = 0%, 3.0V = 100%). Reported by the sensor periodically (every 5 hours via standard cluster) or via proprietary Xiaomi data. |
| **`lastBattery`** | Date | ISO timestamp | The date and time of the last battery report. Used to know how fresh the battery reading is. |
| **`healthStatus`** | String | `online`, `offline`, `unknown` | Indicates whether the sensor has communicated recently. `online` means a message was received within the last 12 hours. |
| **`checkInterval`** | Integer | 3600 seconds | Hardcoded interval for the health check schedule (every hour). |
| **`networkRejoinCount`** | Integer | Counter | Increments each time the sensor announces a rejoin to the Zigbee mesh (Device_annce message). Helps diagnose mesh stability issues. |

---

## 5. Internal Logic & State Variables

These variables are stored in `state` and are visible in the device’s “State Variables” tab for advanced troubleshooting.

| Variable | Type | Purpose |
| :--- | :--- | :--- |
| **`lastCx`** | String | Stores the driver version (`DRIVER_VERSION`) at last configuration. If mismatched, `configure()` is automatically re-run to apply any changes. |
| **`lastRx`** | Long (epoch millis) | Timestamp of the last received Zigbee message (any cluster). Used by `healthCheck()` to determine if the device is `online`. |
| **`lastTx`** | Long (epoch millis) | Timestamp of the last Zigbee message sent by the driver. Used to differentiate between physical (`type = physical`) and digital (`type = digital`) events. |

**Attribute Seeding:**  
When the device is first installed or after a driver update, the driver seeds default values (`healthStatus = unknown`, `contact` remains unset until first report). This prevents null errors in dashboards.

---

## 6. Zigbee Message Parsing (The Dual-Path Strategy)

Xiaomi contact sensors can report data in two ways: **standard Zigbee clusters** or **proprietary manufacturer‑specific attributes** (`FF01`/`FF02`). This driver supports both.

### 6.1 Standard Parsing Path (Preferred)
* **Contact:** Cluster `0x0006` (On/Off), attribute `0x0000`. Value `01` = open, `00` = closed.
* **Battery:** Cluster `0x0001` (Power Configuration), attribute `0x0021` (BatteryPercentage). Value is a uint8 (0–200) representing 0–100% in 0.5% steps. The driver divides by 2.

### 6.2 Proprietary Parsing Path (For Older Firmware)
When standard clusters are not used, the driver extracts data from manufacturer‑specific attributes (`FF01` or `FF02`) using the patterns documented by the Hubitat community:
- **Battery voltage:** Extracted from specific byte positions (e.g., `value[8..9] + value[6..7]` when followed by `21`). Converted to voltage (0.01V units) then to percentage.
- **Contact state:** Extracted from `6410` pattern in `FF01` or `060010` pattern in `FF02`. A value of `1` indicates open; `0` indicates closed.

### 6.3 Physical vs. Digital Event Flagging
The driver sets an event `type`:
- `physical` – The message originated from the sensor (e.g., contact change, battery report).
- `digital` – The message was generated by the driver itself (e.g., after sending a `Refresh` command) or was a direct response to a driver request.

This distinction is not used by most users but is available for advanced debugging.

---

## 7. Logic Guardrails & Fail‑Safes

### 7.1 Auto‑Configuration on Version Change
If the driver is updated and `state.lastCx` does not match `DRIVER_VERSION`, the driver automatically schedules a `configure()` after 1.5 seconds. This ensures new bindings or reporting configurations are applied without user intervention.

### 7.2 Debug Log Auto‑Revert
To prevent performance degradation from excessive logging, when `logLevel = 1` (Debug) and `autoRevertDebug = true`, the driver schedules `logsOff()` to run after 30 minutes. That method changes the preference to `2` (Info) and logs a notice. If `autoRevertDebug` is disabled, debug remains on until manually turned off.

### 7.3 Battery Percentage Guard
The driver clamps calculated battery percentage between `0` and `100`. Invalid values (e.g., `0xFF` from a missing battery attribute) are ignored with a warning log.

### 7.4 Contact State Inversion
The inversion preference is applied immediately during parsing. If `swapOpenClosed = true`, a raw value of `01` (open) becomes `closed`, and `00` becomes `open`. This transformation happens without modifying the original Zigbee data.

### 7.5 Network Rejoin Debouncing
The driver does not perform any automatic action when `networkRejoinCount` increments; it is purely a diagnostic counter. However, the attribute can be used in Rule Machine to trigger a notification if the count exceeds a threshold within a short period (indicating a flaky mesh connection).

---

## 8. Fingerprints & Compatibility

The driver includes two Zigbee fingerprints to automatically recognise Xiaomi contact sensors:

| Model ID | Device Name |
| :--- | :--- |
| `lumi.sensor_magnet.aq2` | Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01) |
| `lumi.sensor_magnet` | Xiaomi Mijia Door and Window Sensor (MCCGQ01LM) |

Other Xiaomi sensors (motion, vibration, weather, buttons, water leak) will **not** match these fingerprints and should continue to use the universal driver or their dedicated drivers.

---

## 9. Versioning & Lineage

- **Maintainer:** David Ball‑Quenneville (DGBQ)
- **Lineage:** Based on the universal driver by Jonathan Michaelsen (Xiaomi Aqara Mijia Sensors) and the IKEA Parasoll driver structure by Dan Danache.
- **Driver Version:** 1.0.11
- **Document Version:** 1.0.2
- **Status:** Stable – tested with MCCGQ11LM (AS006CNW01) and lumi.sensor_magnet.