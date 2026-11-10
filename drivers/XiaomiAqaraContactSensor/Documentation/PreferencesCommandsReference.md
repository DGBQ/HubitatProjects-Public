# Commands & Preferences Reference

**Project:** Xiaomi Aqara Contact Sensor Hubitat Driver (DGBQ)\
**Driver Version:** v1.0.17\
**Document Version:** 1.0.0

***

## 🎮 Commands

| Command         | Description                                                                                                                                                  |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`Configure`** | Sets up Zigbee bindings and reporting intervals (battery every 5 hours, contact on change). Run after pairing or if the sensor stops reporting.              |
| **`Refresh`**   | Reads the current contact state and battery percentage. **Wake‑sensitive:** Press the sensor’s button or open/close the contact immediately before clicking. |

> **Note:** There is no `Ping` command – it is not useful for battery‑powered sleepy sensors and has been removed.

***

## ⚙️ Preferences

| Preference               | Detail                                                                                                                 | Requirement | Default     |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------------- | ----------- | ----------- |
| **Log verbosity**        | Select level: `Debug` (log everything), `Info` (important events), `Warning`, `Error`.                                 | Optional    | **Debug**   |
| **Auto-Revert Debug**    | When enabled (default), Debug logging automatically turns off after 30 minutes. Disable to keep debug on indefinitely. | Optional    | **Enabled** |
| **Invert contact state** | Swaps `open` and `closed` reports. Useful if the sensor is physically installed backwards.                             | Optional    | **Off**     |

***

## 📊 Attributes

| Attribute                | Description                                             | Values / Unit                  |
| ------------------------ | ------------------------------------------------------- | ------------------------------ |
| **`contact`**            | Primary door/window state.                              | `open`, `closed`               |
| **`battery`**            | Calculated charge percentage.                           | 0–100%                         |
| **`batteryVoltage`**     | Voltage in volts (e.g., 3.06V)                          | Volts                          |
| **`lastBattery`**        | Timestamp of last battery report.                       | Date/time                      |
| **`healthStatus`**       | Device reachability indicator (custom, no Ping button). | `online`, `offline`, `unknown` |
| **`networkRejoinCount`** | Number of times device rejoined Zigbee mesh.            | Integer                        |

**Note:** Health status is logged at `Info` level only when the status changes (e.g., from `online` to `offline`). The hourly health check itself logs only at `Debug` level to avoid log clutter.

***

## 🛠️ Maintenance Notes

### Updating to a New Driver Version

1. Save the new driver code in **Drivers Code**.

2. Go to the device page and click **Change Driver** (select the driver again, even if the name is the same).

3. Click **Configure**.

4. Verify the `lastCx` state variable matches the new version number.

### What to Do After a Device Re-Pair

When you double-pair the sensor to force an immediate battery report, the driver's `state` is cleared. The `healthStatus` will appear as `unknown` until the next report. You can click **Configure** to immediately force the health status to update.

### What to Do When a Sensor Drops Offline

If the `healthStatus` shows `offline`, the sensor has not communicated in over 12 hours.

* **Action:** Press the physical reset button on the sensor to wake it, or open/close the door.

* **If it persists:** Re-pair the sensor using the "5-Flash" method (hold reset until the LED blinks 5 times) to force a stable binding.

### Zigbee Mesh Notes

Aqara sensors are notorious for dropping off the mesh if they route through incompatible Zigbee repeaters. If you experience frequent dropouts, ensure the sensor is paired directly to the Hubitat hub, or through a known-compatible repeater (e.g., Third Reality). Avoid pairing them through IKEA plugs.

***

## 📚 Related Documents

* **[README.md](https://./README.md)** – Project overview, key improvements, and credits.

* **[CHANGELOG.md](https://./CHANGELOG.md)** – Full version history for the driver.

* **[QuickStartInstallation\&Configuration.md](https://./QuickStartInstallation\&Configuration.md)** – Step-by-step guide for installing and configuring.

* **[RETR-Contact-Logic.md](https://./RETR-Contact-Logic.md)** – Technical deep-dive into the dual-path parsing and health logic.

* **[RoadmapPhasedPlan.md](https://./RoadmapPhasedPlan.md)** – Project requirements and phased rollout history.

***

**Driver Version:** v1.0.17\
**Document Version:** 1.0.0\
**Date:** November 10, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀

