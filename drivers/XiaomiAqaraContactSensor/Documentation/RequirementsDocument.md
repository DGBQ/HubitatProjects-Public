# 📅 Xiaomi Aqara Contact Sensor – Requirements Document (DGBQ Fork)

### _Project Requirements & Feature Roadmap_

***

## 🔢 Version Control

| Item                    | Details                                 |
| ----------------------- | --------------------------------------- |
| **Document Version**    | 1.0.0                                   |
| **Document Status**     | Final – Project Complete                |
| **Project Name**        | Xiaomi Aqara Contact Sensor (DGBQ Fork) |
| **Current Version**     | Driver `v1.0.17` (Deployed)             |
| **Next Target Version** | N/A (Maintenance Mode)                  |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ)           |
| **Original Developers** | Jonathan Michaelsen, Dan Danache        |
| **AI Collaboration**    | AI as Code Master                       |

***

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭 Project Overview

* 🎯 Mission Statement

* 📦 Scope

* 📦 Repository Structure

* 🚀 Feature Roadmap

* 📊 Implementation Details

* 📋 Complete Feature Index

* 📝 Documentation Plan

* 🛡️ Disclaimers

\</details>

***

## 🧭 Project Overview

This project is a **maintained fork** of the Hubitat drivers for the Xiaomi Aqara Contact Sensor, originally based on the universal driver by Jonathan Michaelsen and the structural framework of Dan Danache's IKEA Parasoll driver.

**Why a Fork?**

The original drivers provided a vital foundation but as hardware evolved, several critical flaws emerged:

* **Excessive Bloat:** Universal drivers supported motion, vibration, buttons, and weather sensors, creating massive code bloat and irrelevant UI commands.

* **UI Clutter:** Unnecessary capabilities injected stray buttons (like "Ping") that do not work on battery-powered sleepy sensors.

* **Parsing Failures:** Standard generic drivers often failed to parse proprietary Xiaomi battery reports, leading to stale data.

* **Log Spam:** The original drivers logged every proprietary message, even when the contact state had not changed.

This fork:

* ✅ Strips all unrelated capabilities, leaving a lean, single-purpose driver.

* ✅ Retains essential proprietary Xiaomi parsing (`FF01`/`FF02`).

* ✅ Adds modern health monitoring (`healthStatus`) and diagnostic attributes (`batteryVoltage`).

* ✅ Unifies the UI with a modern Log Verbosity dropdown.

***

## 🎯 Mission Statement

To provide a **reliable, stateful, and clean** contact sensor integration for Hubitat that:

* Eliminates false failures and log spam.

* Offers full visibility into sensor health and battery telemetry.

* Is well-documented and follows DGBQ coding standards.

***

## 📦 Scope

### In Scope

* Standalone Device driver.

* Dual-path parsing (Standard Zigbee and Proprietary).

* Health Monitoring (`healthStatus`, `lastRx`).

* Battery Telemetry (Percentage and Voltage).

* Log Verbosity Control and Auto-Revert Debug.

### Out of Scope (Future)

* OTA Firmware Updates (Hardware does not support this via Hubitat).

* Motion, Vibration, or Button capabilities (Stripped out).

***

## 📦 Repository Structure

text

```
XiaomiAqaraContact-DGBQ/
├── README.md
├── CHANGELOG.md
├── XiaomiAqaraContactSensor-DGBQ.groovy
├── Documentation/
│   ├── QuickStartInstallation&Configuration.md
│   ├── XIAO-Contact-Logic.md
│   ├── PreferencesCommandsReference.md
│   ├── ProjectStatementofPurpose.md
│   ├── RequirementsDocument.md
└── LegacySource/
    └── (Original files for reference)
```

***

## 🚀 Feature Roadmap

### Phase 1 – Foundation (✅ COMPLETED)

**Target:** Driver v1.0.0

| # | Feature       | Description                                                      | Status      |
| - | ------------- | ---------------------------------------------------------------- | ----------- |
| 1 | Strip Bloat   | Removed all motion, vibration, button, and weather capabilities. | ✅ COMPLETED |
| 2 | Basic Parsing | Retained standard and proprietary Zigbee parsing.                | ✅ COMPLETED |
| 3 | Health Check  | Implemented hourly `healthStatus` monitoring.                    | ✅ COMPLETED |

### Phase 2 – Stability & Bug Fixes (✅ COMPLETED)

**Target:** Driver v1.0.1 – v1.0.10

| # | Feature          | Description                                                               | Status      |
| - | ---------------- | ------------------------------------------------------------------------- | ----------- |
| 4 | Null Handling    | Fixed `NullPointerException` when parsing messages with no `value` field. | ✅ COMPLETED |
| 5 | Remove Ping      | Removed the useless `ping` command from the UI.                           | ✅ COMPLETED |
| 6 | List Flattening  | Fixed `ArrayList.startsWith` errors during `configure()`.                 | ✅ COMPLETED |
| 7 | Version Tracking | Fixed driver version state updating after saving code.                    | ✅ COMPLETED |

### Phase 3 – Feature Polish (✅ COMPLETED)

**Target:** Driver v1.0.11 – v1.0.17

| #  | Feature                       | Description                                                                | Status      |
| -- | ----------------------------- | -------------------------------------------------------------------------- | ----------- |
| 8  | Auto-Revert Debug             | Added preference to disable debug logging after 30 minutes.                | ✅ COMPLETED |
| 9  | Remove HealthCheck Capability | Removed platform capability to hide the stray "Ping" button.               | ✅ COMPLETED |
| 10 | Battery Voltage               | Added `batteryVoltage` attribute with floating-point millivolt conversion. | ✅ COMPLETED |
| 11 | Remove checkInterval          | Removed the transient `checkInterval` state to clean up the UI.            | ✅ COMPLETED |
| 12 | Clean Logging                 | Implemented precise proprietary logging to stop contact log spam.          | ✅ COMPLETED |
| 13 | Unknown Health Status         | Set `healthStatus` to `unknown` immediately after a `configure()`.         | ✅ COMPLETED |

***

## 📊 Implementation Details

### Version Numbering Strategy

**Format:** `vX.Y.Z`

* **X** – Major release (architectural changes)

* **Y** – Minor release (new features)

* **Z** – Patch release (bug fixes)

### Code Style Standards

* **Standalone Driver:** Handles all Zigbee parsing and state tracking internally.

* **Logging:** Strict adherence to `ERROR`, `WARN`, `INFO`, `DEBUG` hierarchy with Auto-Revert protection.

***

## 📋 Complete Feature Index

| #  | Feature                       | Phase   | Status      |
| -- | ----------------------------- | ------- | ----------- |
| 1  | Strip Bloat                   | Phase 1 | ✅ COMPLETED |
| 2  | Basic Parsing                 | Phase 1 | ✅ COMPLETED |
| 3  | Health Check                  | Phase 1 | ✅ COMPLETED |
| 4  | Null Handling                 | Phase 2 | ✅ COMPLETED |
| 5  | Remove Ping                   | Phase 2 | ✅ COMPLETED |
| 6  | List Flattening               | Phase 2 | ✅ COMPLETED |
| 7  | Version Tracking              | Phase 2 | ✅ COMPLETED |
| 8  | Auto-Revert Debug             | Phase 3 | ✅ COMPLETED |
| 9  | Remove HealthCheck Capability | Phase 3 | ✅ COMPLETED |
| 10 | Battery Voltage               | Phase 3 | ✅ COMPLETED |
| 11 | Remove checkInterval          | Phase 3 | ✅ COMPLETED |
| 12 | Clean Logging                 | Phase 3 | ✅ COMPLETED |
| 13 | Unknown Health Status         | Phase 3 | ✅ COMPLETED |

**Total Features:** 13\
**Completed:** 13\
**Deferred:** 0

***

## 📝 Documentation Plan

| Document                                     | Status    | Description                                              |
| -------------------------------------------- | --------- | -------------------------------------------------------- |
| **README.md**                                | ✅ Current | Project overview, features, installation                 |
| **CHANGELOG.md**                             | ✅ Current | Version history for the Driver                           |
| **QuickStartInstallation\&Configuration.md** | ✅ Current | Step-by-step setup guide                                 |
| **XIAO-Contact-Logic.md**                    | ✅ Current | Technical deep-dive on parsing and health logic          |
| **PreferencesCommandsReference.md**          | ✅ Current | Quick reference for UI preferences and commands          |
| **ProjectStatementofPurpose.md**             | ✅ Current | Vision and use case for the project                      |
| **RequirementsDocument.md**                  | ✅ Current | Project requirements and feature roadmap (this document) |

***

## 🛡️ Disclaimers

### Community & Personal Use

This driver is provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Xiaomi**, **Aqara**, or **Hubitat Elevation**.

### AI-Assisted Development Notice

This project is a collaborative effort between a human **Project Manager** and **AI as Code Master**. The Project Manager defines requirements, makes architectural decisions, and validates the result on production hardware. The AI drafts the code, documentation, and design proposals under the Project Manager's review and approval. All decisions are human-owned; the AI executes and documents them.

### Operational Safety

* **_Insuo Periculo_**: While this driver is tested in a live production environment, custom code interacts with your Zigbee network and devices in unique ways. I am not responsible for any unintended behavior, battery drain, or loss of functionality.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Production Status:** This fork is currently in **Production (Stable)**.

***

**Document Approved By:** David Ball-Quenneville (DGBQ)\
**Document Prepared By:** AI as Code Master\
**Date:** November 10, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀
