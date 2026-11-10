# Master Functional Specifications: RETR-Contact-Logic

**File Name:** RETR-Xiamoi-Contact-Logic.md (Xiaomi Engineering Technical Reference Contact Logic)\
**Project Branch:** The Clean Contact Driver\
**Integration Logic:** Standalone Device Architecture with Dual-Path Health Monitoring\
**Document Status:** Final Production Specification\
**Driver Version:** 1.0.17

***

## 1. Project Context & Technical Philosophy

### 1.1 The "Clean Contact" Project Genesis

This documentation defines a specialized development branch of the **Xiaomi Aqara Contact Sensor** integration. While the foundation rests on the universal driver by **Jonathan Michaelsen** (which supported dozens of Xiaomi devices) and the structural framework of **Dan Danache's** IKEA Parasoll driver, this "Clean Contact" branch has been architected specifically for a single-purpose, professional-grade automation environment where log cleanliness and state accuracy are mandatory.

### 1.2 The "Sleepy Sensor" Challenge

The defining characteristic of the Xiaomi contact sensor is that it is a **battery-powered, sleepy device**. It spends 99% of its time in a deep sleep to conserve energy and only wakes up to broadcast a message under three conditions:

1. The magnet is opened or closed.

2. The internal reset button is physically pressed.

3. An internal 50–60 minute timer fires to send a battery report.

Because of this, traditional polling commands (like `Refresh` or `Ping`) often fail because the radio is turned off. This driver embraces this reality by acting as a passive listener rather than an aggressive poller.

### 1.3 The Dual-Path Parsing Resolution

Prior community versions often failed to report accurate battery percentages or missed contact changes because Xiaomi uses two different reporting methods:

* **Standard Zigbee Clusters:** Used by newer firmware.

* **Proprietary Xiaomi Payloads (`FF01`/`FF02`):** Used by older firmware to send voltage and contact state in a single, encrypted-looking hex string.

This branch implements a **Dual-Path Parser**. It checks for standard cluster reports first. If none are found, it tears apart the proprietary hex payload to extract the exact millivolt reading and contact state.

### 1.4 Phase 3 Log Cleanup & Health Status (v1.0.17)

In the final phase of development, two critical UI/Log improvements were made:

* **Health Status Honesty:** When a user clicks `Configure`, the driver previously showed the _old_ health status until the next hourly check. In v1.0.17, the driver immediately forces `healthStatus` to `unknown` after clearing state, which is an honest reflection that it is waiting for a fresh report.

* **Contact Log Spam:** Because the proprietary payload sends the contact state alongside _every_ battery report, the driver used to log `Contact changed to closed` every 50 minutes, even if the door hadn't moved. The logic was updated to compare the new state against the old state, only logging at `INFO` level if the door actually opened or closed.

***

## 2. Full Command API (Functional Methods)

These methods represent the actionable interface within the Hubitat UI. No virtual commands (like `open` or `close`) are exposed because the physical sensor is a passive device; you cannot force a door to close via software.

### 2.1 Core System Commands

* **`configure()`**: The primary setup tool. It binds the sensor to the hub for clusters `0x0001` (Power) and `0x0006` (On/Off). It sets up reporting intervals, sends an identify blink (LED), and schedules `configureApply()` to run 5 seconds later.

* **`refresh()`**: Sends a read request to the sensor. Because the sensor is usually asleep, the UI logs a warning reminding the user to physically open/close the door immediately after clicking Refresh to wake the device up.

> **Note:** The `ping` command was explicitly removed in v1.0.2. It is useless for sleepy sensors and only caused confusion.

***

## 3. User Preferences & Configuration

These inputs allow the user to tune the driver.

| Preference               | Format  | Default | Description                                                                                                                |
| ------------------------ | ------- | ------- | -------------------------------------------------------------------------------------------------------------------------- |
| **Log verbosity**        | Enum    | `Debug` | 1=Debug, 2=Info, 3=Warning, 4=Error.                                                                                       |
| **Auto-Revert Debug**    | Boolean | `True`  | Automatically reverts Log verbosity from `Debug` back to `Info` after 30 minutes.                                          |
| **Invert contact state** | Boolean | `False` | Swaps `open` and `closed` reports. Useful if the magnet is mounted on the door frame and the sensor is on the moving door. |

***

## 4. Current States (Dashboard Attributes)

This section tracks the real-time telemetry visible to the user and external integrations.

* **`contact`**: The primary state (`open` or `closed`).

* **`battery`**: Calculated percentage (0-100%) based on a 2.0V (0%) to 3.0V (100%) discharge curve.

* **`batteryVoltage`**: The raw voltage converted to decimal volts (e.g., `3.06V`). _Note: Some hardware batches send raw millivolts, others send raw centivolts. The driver uses floating-point division to parse millivolts._

* **`lastBattery`**: Timestamp of the last battery payload received.

* **`healthStatus`**: `online`, `offline`, or `unknown`. Reflects the last 12 hours of communication.

* **`networkRejoinCount`**: Increments every time the sensor announces itself to the Zigbee mesh (Device_annce).

***

## 5. Internal Logic & State Variables

This section defines the background memory and initialization logic used to maintain driver stability.

* **`state.lastCx`**: Tracks the driver version (e.g., `1.0.17`). If the version changes, the driver auto-triggers a `configure()`.

* **`state.lastRx`**: The epoch timestamp of the last received message. Used by the health check.

* **`state.lastTx`**: The epoch timestamp of the last sent message. Updated by `configure` and `refresh`.

### Attribute Seeding

Upon a `configure()`, the driver clears its state and immediately seeds `healthStatus` to `unknown`, preventing stale "online" states from misleading the user.

***

## 6. Logic Guardrails & Fail-Safes

### 6.1 Null-Value Protection

Because `zigbee.parseDescriptionAsMap` can sometimes return null values for configuration responses (e.g., "Default Response" or "Read Attribute Response"), the driver employs a guard clause: `if (msg.value == null) return`. This prevents null pointer exceptions from crashing the parse loop.

### 6.2 The Integer Division Trap

When parsing the proprietary voltage hex string, the driver must divide the raw value (e.g., `3055`) by `1000.0`. Using `1000` (integer) would result in `3`, truncating the decimal. The driver enforces floating-point division `voltageRaw / 1000.0` and applies `.setScale(2)` to ensure exact voltage reporting.

### 6.3 List Flattening (The `.startsWith` Error)

Hubitat's Groovy environment sometimes turns lists of commands into nested arrays when using `+=`. The driver's `sendZigbee()` method applies `.flatten()` and `.findAll { !it.startsWith("delay") }` to ensure only valid string commands are passed to the hub, preventing `java.lang.ArrayIndexOutOfBoundsException`.

### 6.4 Health Check Engine

A scheduled cron job (`0 0 0/1 ? * * *`) runs `healthCheck()` every hour. It compares `now()` to `state.lastRx`. If the difference is greater than 43200 seconds (12 hours), it marks the sensor `offline`. This is a vital feature for sleepy sensors, as it alerts the user if the battery dies or the Zigbee mesh drops the route.

***

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)

* **Lineage:** Based on the universal driver by Jonathan Michaelsen and IKEA Parasoll structure by Dan Danache.

* **Current Driver Version:** 1.0.17

* **Key Enhancements (v1.0.0 – v1.0.17):**

  * Stripped all motion/vibration/weather code.

  * Implemented dual-path parsing (Standard + Proprietary).

  * Added `batteryVoltage` with floating-point division.

  * Fixed `lastTx` cosmetic zero-value issue.

  * Removed `checkInterval` clutter.

  * Improved proprietary logging to stop contact log spam.

  * Added immediate `healthStatus = unknown` after configure.

### Lessons Learned

* **Proximity matters:** Aqara sensors will lose their bindings if paired through incompatible repeaters (e.g., IKEA plugs). They are most stable paired directly to the Hubitat hub or through a compatible repeater (e.g., Third Reality).

* **Double-pairing resets state:** If a user double-pairs the device to force a battery report, the driver's `state` is wiped. Clicking `Configure` is the quickest way to re-seed the UI.

* **The "5-Flash" rule:** When pairing these sensors, the reset button must be held until the LED flashes exactly 5 times to ensure a stable binding.
