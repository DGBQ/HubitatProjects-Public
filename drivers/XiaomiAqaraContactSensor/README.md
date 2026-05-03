# 🚪 Xiaomi Aqara Contact Sensor Hubitat Driver
### _A Clean, Reliable Driver for Zigbee Contact Sensors (MCCGQ11LM / AS006CNW01)_

## 🔢 Version Control
**Document Control:** 1.0.3  
**Current Status:** Stable  
- **Driver:** `XiaomiAqaraContactSensor-DGBQ.groovy` (v1.0.17)  
**Maintenance Lead:** David Ball-Quenneville (DGBQ)  
**Original Developers:** Jonathan Michaelsen (Xiaomi Aqara Mijia driver), Dan Danache (IKEA Parasoll driver structure)

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- [🧭 Overview](#overview)
- [🚀 Key Improvements](#key-improvements)
- [📦 Required Files](#required-files)
- [📑 Documentation](#documentation)
- [🛡️ Disclaimers](#disclaimers)
- [📜 Revision History](#revision-history)

</details>

---

<h2 id="overview">🧭 Overview</h2>

This driver provides a **lightweight, stable, and feature‑focused** integration for the **Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01)** and the **Mijia Door/Window Sensor (lumi.sensor_magnet)** on the Hubitat platform.

The original universal driver by **Jonathan Michaelsen** supported dozens of Xiaomi sensors but included many capabilities (motion, vibration, weather, buttons) that are irrelevant for a simple contact sensor. The IKEA Parasoll driver by **Dan Danache** introduced a modern, structured approach with health checks and clean logging.

This driver combines the best of both: it retains the **proprietary Xiaomi Zigbee parsing** needed for reliable battery and contact reporting, while adopting the **IKEA driver’s robust framework** – including health status, configurable logging, and contact state inversion.

> **_Insuo Periculo_**: This software is provided "as‑is" for personal use; please proceed at your own risk.

---

<h2 id="key-improvements">🚀 Key Improvements over the Universal Driver</h2>

### **Removed Clutter**
- Stripped out all capabilities and code for motion, vibration, temperature, humidity, pressure, water leak, and button devices.
- Removed virtual commands (`open`, `closed`, `push`, `hold`, etc.) that do not apply to a contact sensor.
- Removed the `ping` command (not useful for battery‑powered sleepy sensors).
- Removed the `HealthCheck` capability – this eliminates the stray "Ping" button in the UI, but the custom `healthStatus` attribute and hourly health monitoring remain fully functional.

### **Added Reliability Features**
- **Health Check:** Automatically tracks `lastRx` (last received message) and sets `healthStatus` to `online`, `offline`, or `unknown`. Scheduled checks run every hour; marks offline after 12 hours of no communication.
- **Network Rejoin Count:** Tracks how many times the device rejoins the Zigbee mesh, useful for diagnosing connectivity issues.
- **Contact State Inversion:** Preference to swap `open` and `closed` if the sensor is installed backwards.
- **Log Verbosity Control:** Choose between Debug, Info, Warning, or Error logging. Debug mode auto‑reverts to Info after 30 minutes (can be disabled with the `Auto-Revert Debug` preference).
- **Battery Voltage Reporting:** `batteryVoltage` attribute provides raw voltage in volts (e.g., 3.06V) for advanced diagnostics.
- **Improved Proprietary Logging:** Only logs contact changes when the state actually changes (clearer `Contact reported as open/closed (proprietary message)`).

### **Preserved Essential Xiaomi Handling**
- Maintains proprietary `FF01`/`FF02` attribute parsing for devices that do not use standard Zigbee clusters for battery and contact reporting.
- Supports both standard `OnOff` cluster (0x0006) and proprietary methods.

---

<h2 id="required-files">📦 Required Files</h2>

You only need **one driver file** for this sensor:

- **`XiaomiAqaraContactSensor-DGBQ.groovy`** – Install this in your Hubitat **Drivers Code** section. After installation, pair your Xiaomi contact sensor normally. The driver will automatically recognize the device via its Zigbee fingerprint.

No parent/child relationship is required – this is a standalone device driver.

---

<h2 id="documentation">📑 Documentation</h2>

### **Quick Start**
1. Copy the driver code into **Drivers Code** → **New Driver**.
2. Save the driver.
3. Put your Xiaomi contact sensor into pairing mode.
4. In Hubitat, go to **Devices** → **Add Device** → **Zigbee** → your sensor should appear and will use this driver automatically.
5. After pairing, click **Configure** in the device page to set up reporting intervals.

### **Preferences**
- **Log verbosity:** Select level (Debug logs everything; Info is recommended for daily use).
- **Auto-Revert Debug:** When enabled (default), Debug logging automatically turns off after 30 minutes. Disable to keep debug on indefinitely for extended troubleshooting.
- **Invert contact state:** Check if you want `open` reported as `closed` and vice versa.

### **Commands**
- **Configure:** Sets up Zigbee bindings and reporting (battery every 5 hours, contact on change).
- **Refresh:** Reads current contact and battery state (press the sensor’s button or open/close it first to wake it).

### **Attributes (States)**
- `contact` – `open` or `closed`
- `battery` – Percentage (0–100%)
- `batteryVoltage` – Voltage in volts (e.g., 3.06V)
- `lastBattery` – Timestamp of last battery report
- `healthStatus` – `online`, `offline`, or `unknown`
- `networkRejoinCount` – Number of times device rejoined mesh

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

### **Community & Personal Use**
This driver is provided “as‑is” as a service to the Hubitat community. It is **not** an official product of, nor endorsed by, **Xiaomi**, **Aqara**, or **Hubitat Elevation**.

### **AI‑Assisted Development Notice**
This driver was refined with the assistance of **Gemini AI** to audit, restructure, and test the logic. While the driver has been tested in a live environment, generative AI can occasionally produce unexpected artifacts. Users should monitor their initial installation.

### **Operational Safety**
- **_Insuo Periculo_**: Custom drivers interact with your Zigbee network and devices. The author is not responsible for any unintended behavior, battery drain, or loss of functionality.
- **Backup:** Always perform a full backup of your Hubitat database before installing new custom drivers.

---

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date | Changes |
| :--- | :--- | :--- |
| **1.0.3** | 2026-05-03 | Updated for driver v1.0.17: added `batteryVoltage` attribute, removed `checkInterval`, corrected voltage conversion description, improved logging and health status after configure. |
| **1.0.2** | 2026-04-27 | Updated for driver v1.0.11: added Auto-Revert Debug preference, removed HealthCheck capability (no more stray "Ping" button). Preserved all existing content. |
| **1.0.0** | 2026-04-24 | Initial release – stripped universal driver, added health check, log levels, contact inversion, and network rejoin count. |