# Project Statement of Purpose

**Project Name:** Xiaomi Aqara Contact Sensor Hubitat Driver (DGBQ)

***

## About This Project

The Xiaomi Aqara Contact Sensor Hubitat Driver is a specialized, lightweight fork of a community driver, overhauled to provide professional-grade reliability and clean logging for the MCCGQ11LM / AS006CNW01 sensors. It bridges the gap between Hubitat and the Xiaomi/Aqara proprietary Zigbee data format, translating standard and proprietary clusters into actionable telemetry.

Unlike the original universal drivers that supported dozens of device types and included unnecessary code paths (motion, vibration, weather, buttons), this driver does exactly one thing perfectly: it monitors doors and windows. It handles the harsh realities of sleepy, battery-powered Zigbee sensors—intermittent dropouts and non-standard reporting cycles—ensuring that your contact state and battery telemetry are as accurate and responsive as the hardware allows.

***

## The Problem

Xiaomi/Aqara contact sensors are notorious for their non-standard Zigbee implementation. While reliable, they present several challenges that standard drivers fail to handle gracefully:

* **Sleepy Devices:** They spend most of their time in a deep sleep, only waking to report state changes or scheduled battery levels. Polling commands often fail.

* **Inconsistent Reporting:** Some firmware versions report battery data in millivolts, others in proprietary `FF01`/`FF02` payloads.

* **Log Clutter:** Standard drivers often inject a "Ping" command (useless for battery devices) and log every single proprietary message, even if the contact state hasn't changed.

* **Mesh Instability:** These sensors are picky about Zigbee repeaters (often dropping bindings with certain brands like IKEA plugs).

***

## The Solution

A robust, standalone driver that:

1. **Implements a Dual-Path Parser** — Seamlessly handles both standard Zigbee clusters (`0x0006`) and proprietary manufacturer payloads (`FF01`/`FF02`).

2. **Provides "Set-and-Forget" Health Monitoring** — Uses an hourly check against the last received message to accurately determine if the sensor is online, offline, or unknown.

3. **Cleans Up the UI** — Removes unsupported commands (like Ping) and hides unnecessary attributes, while adding a custom `batteryVoltage` attribute for advanced diagnostics.

4. **Improves Logging UX** — Uses a structured Log Verbosity dropdown, preventing log spam when a contact state hasn't actually changed.

***

## What It Is

A **production-grade, standalone device driver** for Hubitat that parses both standard and proprietary Zigbee data for Xiaomi contact sensors. It manages contact state, battery percentage, battery voltage, and connection health.

## What It Is Not

* It is **not** a parent/child driver architecture (it is standalone).

* It does **not** support motion, vibration, temperature, or buttons (even if the hardware has them).

* It does **not** include a firmware update mechanism (Xiaomi does not provide OTA files for these devices to Hubitat).

***

## Architecture

* **Standalone Driver:** Direct communication with the Zigbee sensor.

* **Dual-Path Parser:** Checks for standard `0x0006` reports, falling back to proprietary `FF01`/`FF02` parsing for older firmware.

* **Health Check Engine:** A scheduled hourly job that compares the current time to `state.lastRx`.

***

## Why This Approach

| Alternative                     | Why It Doesn't Work                                                                                                                     |
| ------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| **Universal Community Drivers** | Support dozens of devices but include irrelevant capabilities, clutter the UI with virtual commands, and log every proprietary message. |
| **Built-in Generic Drivers**    | Often fail to parse the proprietary Xiaomi battery data or health status, resulting in stale battery percentages.                       |
| **Cloud Integrations**          | Introduces latency, relies on an internet connection, and lacks fine-grained control over device bindings.                              |

***

## Success Criteria

The project is successful when:

1. A user can pair the device and receive accurate `contact` updates without any errors.

2. The driver correctly parses both standard and proprietary battery payloads.

3. The `healthStatus` accurately reflects when a sensor has dropped off the mesh.

4. The driver does not flood the logs with repetitive contact messages when the battery reports.

5. The integration is fully documented, following DGBQ SOPs, with comprehensive troubleshooting guides for Zigbee mesh management.

***

**Insuo Periculo** – Enter at your own risk. 🚀

