# 🗺️ Xiaomi Aqara Contact Sensor – Roadmap & Phased Plan

### _Working Planning Document – Not For Publication_

***

## 🔢 Version Control

| Item                       | Details                                 |
| -------------------------- | --------------------------------------- |
| **Document Version**       | 1.0.0                                   |
| **Document Status**        | Living Document – Project Complete      |
| **Audience**               | DGBQ (Project Manager) + AI Code Master |
| **Project Name**           | Xiaomi Aqara Contact Sensor (DGBQ Fork) |
| **Current Driver Version** | v1.0.17                                 |
| **Next Target Version**    | N/A (Maintenance Mode)                  |

> **📌 Note:** This document is a **working planning tool** between the Project Manager and the AI Code Master. It is intentionally **not published** to GitHub and **not linked** from the README. Its purpose is to keep a live view of where we are, what's next, and what was deliberately left out — and to preserve the reasoning behind those decisions.

***

## 🗺️ Roadmap Overview

| Phase       | Name                        | Status         | Version                  |
| ----------- | --------------------------- | -------------- | ------------------------ |
| **Phase 1** | Foundation & Cleanup        | ✅ **COMPLETE** | Driver v1.0.0            |
| **Phase 2** | Stability & Bug Fixes       | ✅ **COMPLETE** | Driver v1.0.1 – v1.0.10  |
| **Phase 3** | Feature Polish & Logging UX | ✅ **COMPLETE** | Driver v1.0.11 – v1.0.17 |
| **Phase 4** | Documentation & Release     | ✅ **COMPLETE** | N/A                      |

***

## ✅ Phase 1 – Foundation & Cleanup (Driver v1.0.0)

**Status:** COMPLETE (2026-04-24)\
**Scope:** Strip out all unnecessary universal driver code and establish a lean, single-purpose driver.

### What was actually done

The original universal driver supported motion, vibration, weather, buttons, and water leak sensors. This phase stripped all of that away, leaving only the core contact sensor logic.

| #   | Change                          | Rationale                                                                                                                                                                  |
| --- | ------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.1 | Stripped unrelated capabilities | Removed `IlluminanceMeasurement`, `RelativeHumidityMeasurement`, `TemperatureMeasurement`, `PressureMeasurement`, `AccelerationSensor`, `MotionSensor`, and `WaterSensor`. |
| 1.2 | Removed button capabilities     | Removed `PushableButton`, `HoldableButton`, `DoubleTapableButton`, and `ReleasableButton`.                                                                                 |
| 1.3 | Removed virtual commands        | Removed `open`, `closed`, `push`, `hold`, `doubleTap`, `release`, `shake`, `wet`, and `dry`. The sensor is passive and cannot accept these.                                |
| 1.4 | Retained dual-path parsing      | Kept both standard Zigbee (`0x0006`) and proprietary (`FF01`/`FF02`) parsing to ensure compatibility with all hardware revisions.                                          |
| 1.5 | Added Health Check              | Implemented hourly `healthStatus` monitoring to detect dropped connections.                                                                                                |

***

## ✅ Phase 2 – Stability & Bug Fixes (Driver v1.0.1 – v1.0.10)

**Status:** COMPLETE\
**Scope:** Fix parsing crashes, remove useless UI elements, and ensure version tracking works correctly.

### What was actually done

This phase was driven entirely by real-world testing and debugging on the Hubitat platform.

| #   | Change                                                 | Rationale                                                                                                                                                                           |
| --- | ------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1 | Fixed `NullPointerException` (v1.0.1)                  | Added guard clauses to ignore Zigbee messages with no `value` field (e.g., configuration responses).                                                                                |
| 2.2 | Removed `ping` command (v1.0.2)                        | The `ping` function is useless for a sleepy sensor and only caused confusion.                                                                                                       |
| 2.3 | Fixed `NullPointerException` in `configure()` (v1.0.2) | Added safety checks for missing `device.endpointId` and `device.zigbeeId`.                                                                                                          |
| 2.4 | Fixed logging helper crashes (v1.0.3)                  | Added early initialisation of `logLevel` to prevent null errors during boot.                                                                                                        |
| 2.5 | Fixed contact parsing (v1.0.4)                         | Converted `attrInt` from a hex string to an integer so the `case` statement could correctly match `contact` reports. This was the key fix that made contact toggling work reliably. |
| 2.6 | Fixed `configure()` list flattening (v1.0.5 – v1.0.6)  | Addressed `ArrayList.startsWith` errors by changing `cmds += refresh(true)` to `cmds.addAll(refresh(true))` and improving `sendZigbee()` to safely flatten commands.                |
| 2.7 | Fixed version tracking (v1.0.7 – v1.0.8)               | Ensured `state.lastCx` always updates to the current driver version when the code is saved or configured.                                                                           |
| 2.8 | Fixed `lastTx` cosmetic zero value (v1.0.9 – v1.0.10)  | Added `state.lastTx = now()` in user commands so the UI reflects outgoing activity. (v1.0.9 was deprecated due to a pairing regression, v1.0.10 re-implemented the fix safely).     |

***

## ✅ Phase 3 – Feature Polish & Logging UX (Driver v1.0.11 – v1.0.17)

**Status:** COMPLETE\
**Scope:** Add the final modern features, clean up log spam, and remove stray UI elements.

### What was actually done

This phase added the last layer of professional-grade polish to the driver.

| #   | Change                                     | Rationale                                                                                                                           |
| --- | ------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------- |
| 3.1 | Added Auto-Revert Debug (v1.0.11)          | Prevents debug logs from filling up the system log indefinitely. Auto-turns off after 30 minutes.                                   |
| 3.2 | Removed `HealthCheck` capability (v1.0.11) | Eliminates the stray "Ping" button injected by Hubitat. The custom `healthStatus` attribute remains fully functional.               |
| 3.3 | Added `batteryVoltage` attribute (v1.0.12) | Provides raw voltage in volts (e.g., 3.01V) for advanced diagnostics.                                                               |
| 3.4 | Removed `checkInterval` event (v1.0.16)    | The 3600-second health check interval was cluttering the Current States list and is hardcoded anyway.                               |
| 3.5 | Fixed integer division trap (v1.0.16)      | Changed `voltageRaw / 1000` to `voltageRaw / 1000.0` to preserve decimal precision (e.g., 3.06V instead of 30.6V).                  |
| 3.6 | Improved proprietary logging (v1.0.17)     | Stops log spam by only logging contact changes when the state actually changes.                                                     |
| 3.7 | Immediate health status update (v1.0.17)   | Sets `healthStatus` to `unknown` immediately after `configure()` to honestly reflect that the driver is waiting for a fresh report. |

***

## ⏳ Phase 4 – Documentation & Release

**Status:** COMPLETE (2026-11-10)\
**Scope:** Align all documentation with the new DGBQ Standard Operating Procedure (SOP).

### What was actually done

* Restructured `README.md`, `CHANGELOG.md`, and `QuickStartInstallation&Configuration.md` to match the new formatting.

* Created new SOP documents: `PreferencesCommandsReference.md`, `ProjectStatementofPurpose.md`, `RequirementsDocument.md`, and `RoadmapPhasedPlan.md`.

* Migrated `XIAO-Contact-Logic.md` to `RETR-Contact-Logic.md` to follow the technical reference naming convention.

***

## 🧠 Design Deep-Dive Reference

Decisions we analysed and **deliberately did not implement**. These are preserved so we don't re-litigate the same questions in future updates.

| Considered                          | Reason Not Implemented                                                                                                                                                  |
| ----------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Health Check via `ping`**         | `ping` is useless for battery-powered sleepy sensors. The sensor is asleep 99% of the time and will not respond to a poll.                                              |
| **OTA Firmware Updates**            | Xiaomi does not provide public OTA firmware files that Hubitat can use. Adding an update button would be purely cosmetic and non-functional.                            |
| **Adding Motion to Contact Sensor** | The original universal driver had an option to add a `motion` state to contact sensors. We removed this as it clutters the UI and most users do not need it.            |
| **Battery Offset**                  | Unlike the Rollease shades, Xiaomi sensors do not have a consistent linear discharge curve. A fixed offset would be inaccurate. We rely on raw voltage for diagnostics. |
| **Retaining `checkInterval`**       | The 1-hour health check is hardcoded and not user-adjustable. Displaying it in the UI was unnecessary clutter.                                                          |
| **Parent/Child Architecture**       | The Xiaomi contact sensor is a single, standalone device. A parent/child architecture would add unnecessary complexity.                                                 |

***

## 🚦 Current Status

**As of 2026-11-10:**

* **All phases are complete.** The driver is stable and running on production hardware (four sensors and counting).

* **Documentation is fully aligned** with the DGBQ SOP.

* **The project is officially in Maintenance Mode.**

**Next action:** Monitor the system. As you add more sensors to your fleet, use the "5-Flash" pairing method to ensure a stable binding. Enjoy your smart home!

***

## 📝 Change Log (this document)

| Version   | Date       | Changes                                                                                                                               |
| --------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| **1.0.0** | 2026-11-10 | Initial creation. Documented all completed phases (1 through 4), the design deep-dive rejections, and marked the project as complete. |

***

**Insuo Periculo** – Enter at your own risk. 🚀
