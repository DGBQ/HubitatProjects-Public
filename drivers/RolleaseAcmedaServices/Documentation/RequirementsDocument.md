# 📅 Rollease Acmeda Hubitat Integration – Requirements Document (DGBQ Fork)

### _Project Requirements & Feature Roadmap_

***

## 🔢 Version Control

| Item                    | Details                                         |
| ----------------------- | ----------------------------------------------- |
| **Document Version**    | 1.0.0                                           |
| **Document Status**     | Final – Project Complete                        |
| **Project Name**        | Rollease Acmeda Hubitat Integration (DGBQ Fork) |
| **Current Version**     | Hub `v3.3.26` / Shade `v2.5.9` (Deployed)       |
| **Next Target Version** | N/A (Maintenance Mode)                          |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ)                   |
| **Original Developer**  | Younes Oughla (Yoonoo)                          |
| **AI Collaboration**    | AI as Code Master                               |

***

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭 Project Overview

* 🎯 Mission Statement

* 📦 Scope

* 📦 Repository Structure

* 🚀 Feature Roadmap

  * Phase 1 – Critical Bug Fixes

  * Hot Fix 1 – Battery Recalibration

  * Phase 2A – Retry Logic + Logging

  * Hot Fix 2 – Repeater Protection

  * Hot Fix 3 – Smart Stateful Retry

  * Phase 2B – Parent Logging + Initialize

  * Hot Fix 4 – Refresh Guard

  * Phase 3B – Actionable Event Tracking

* 📊 Implementation Details

* 📋 Complete Feature Index

* 📝 Documentation Plan

* 🛡️ Disclaimers

\</details>

***

## 🧭 Project Overview

This project is a **maintained fork** of the Hubitat drivers for the Rollease Acmeda Automate Pulse 2 Hub, originally developed by Younes Oughla. The drivers facilitate local IP control of motorized shades using the ARC (Automate Remote Control) protocol over Telnet.

**Why a Fork?**

The original drivers provided a vital foundation, but as hardware evolved and real-world RF environments proved unpredictable, several critical flaws emerged:

* **Repeater Flooding:** Aggressive retry logic crashed the RF repeaters, taking down entire rooms.

* **Exact-Match Failures:** Shades stopping 1–2% short of their target were treated as failures, causing log spam and unnecessary retries.

* **Device Deletion:** Address mismatches caused the parent driver to accidentally delete working child devices.

* **UI Latency:** Waiting for physical confirmation broke Alexa responsiveness.

This fork:

* ✅ Implements **Smart Stateful Retry** with an 8-second Heartbeat Check.

* ✅ Protects RF hardware via **Exponential Backoff**.

* ✅ Introduces **Actionable Event Tracking** (`lastEvent`) for automations.

* ✅ Unifies the UI with a modern **Log Verbosity** dropdown.

* ✅ Is fully documented and maintained.

***

## 🎯 Mission Statement

To provide a **reliable, stateful, and self-healing** shade integration for Hubitat that:

* Eliminates false failures caused by physical motor tolerances.

* Protects RF repeaters from command floods.

* Offers full visibility into driver operations via actionable attributes.

* Is well-documented and follows DGBQ coding standards.

***

## 📦 Scope

### In Scope

* Parent Hub driver (Telnet management, child lifecycle)

* Child Shade driver (Movement, Retries, Telemetry, Event Tracking)

* Smart Stateful Retry (Heartbeat Check)

* Exponential Backoff & Repeater Protection

* Actionable Event Tracking (`lastEvent`, `lastUpdate`)

* Position Tolerance

* Battery Telemetry (Recalibrated to 9.5V–12.6V)

* Comprehensive documentation (README, CHANGELOGs, QuickStart, RETR specs, Preferences Reference, Roadmap)

### Out of Scope (Future)

* Health Check & Auto-Recovery (Parent Hub) – Deferred indefinitely.

* Native Hubitat `lastCommand` attribute on children – Rejected (no user value).

***

## 📦 Repository Structure

text

```
RolleaseAcmeda-DGBQ/
├── README.md
├── CHANGELOG-Hub.md
├── CHANGELOG-Shade.md
├── RolleaseAcmedaHub-DGBQ.groovy
├── RolleaseAcmedaShade-DGBQ.groovy
├── Documentation/
│   ├── QuickStartInstallation&Configuration.md
│   ├── RETR-Hub-Logic.md
│   ├── RETR-Shade-Logic.md
│   ├── PreferencesCommandsReference.md
│   ├── ProjectStatementofPurpose.md
│   ├── RequirementsDocument.md
│   └── RoadmapPhasedPlan.md              ← internal, never published
└── LegacySource/
    └── (Original files for reference)
```

***

## 🚀 Feature Roadmap

### Phase 1 – Critical Bug Fixes (✅ COMPLETED)

**Target:** Hub v3.3.24

| # | Feature                | Description                                                                         | Status      |
| - | ---------------------- | ----------------------------------------------------------------------------------- | ----------- |
| 1 | `ShadeAdd()` Fix       | Added `child.initialize()` to ensure new children get the correct `motorAddress`.   | ✅ COMPLETED |
| 2 | `parse()` Validation   | Auto-corrects child addresses when parent/child state diverges.                     | ✅ COMPLETED |
| 3 | `ShadeRemove()` Safety | Verifies child `motorAddress` before deletion to prevent removing the wrong device. | ✅ COMPLETED |

***

### Hot Fix 1 – Battery Recalibration (✅ COMPLETED)

**Target:** Shade v2.5.4

| # | Feature                | Description                                                                            | Status      |
| - | ---------------------- | -------------------------------------------------------------------------------------- | ----------- |
| 4 | Voltage Formula Update | Changed range from 10.8V–12.6V to **9.5V–12.6V** to match the Rollease app within ±5%. | ✅ COMPLETED |

***

### Phase 2A – Retry Logic + Logging (✅ COMPLETED)

**Target:** Shade v2.5.5

| # | Feature              | Description                                                                       | Status      |
| - | -------------------- | --------------------------------------------------------------------------------- | ----------- |
| 5 | Position Tolerance   | Accepts shades within ±2% of the target as "confirmed." Eliminates false retries. | ✅ COMPLETED |
| 6 | Intermediate Reset   | Resets the timer if the shade reports movement progress (up to 2 times).          | ✅ COMPLETED |
| 7 | `unschedule()` Fixes | Fixed no-op timer cancellations (`retryCommand` → `checkPositionConfirmation`).   | ✅ COMPLETED |
| 8 | `commandId` Guard    | Prevents stale callbacks from a previous command affecting a new one.             | ✅ COMPLETED |
| 9 | Log Verbosity UX     | Replaced boolean logging toggles with a unified dropdown.                         | ✅ COMPLETED |

***

### Hot Fix 2 – Repeater Protection (✅ COMPLETED)

**Target:** Shade v2.5.7

| #  | Feature             | Description                                                                       | Status      |
| -- | ------------------- | --------------------------------------------------------------------------------- | ----------- |
| 10 | Defaults Update     | Set `Position Tolerance` to 2 and `Command Retry Wait Time` to 20s.               | ✅ COMPLETED |
| 11 | Enum Retry Count    | Changed `cmdRetryCount` to a dropdown (0, 1, 2, 3) to enforce a hard cap.         | ✅ COMPLETED |
| 12 | Exponential Backoff | Wait times increase by 50% each retry (20s, 30s, 45s) to protect the RF repeater. | ✅ COMPLETED |

***

### Hot Fix 3 – Smart Stateful Retry (✅ COMPLETED)

**Target:** Shade v2.5.8

| #  | Feature                 | Description                                                           | Status      |
| -- | ----------------------- | --------------------------------------------------------------------- | ----------- |
| 13 | 8-Second Heartbeat      | Added `checkHeartbeat()` to evaluate state 8 seconds after a command. | ✅ COMPLETED |
| 14 | Deaf/Moving/Stuck Logic | Fires jitter if deaf; resets timer if moving; aborts if stuck.        | ✅ COMPLETED |

***

### Phase 2B – Parent Logging + Initialize (✅ COMPLETED)

**Target:** Hub v3.3.25

| #  | Feature                   | Description                                                             | Status      |
| -- | ------------------------- | ----------------------------------------------------------------------- | ----------- |
| 15 | Log Verbosity UX          | Applied unified logging dropdown to the Parent Hub.                     | ✅ COMPLETED |
| 16 | Remove Refresh Capability | Removed the no-op Refresh button from the UI.                           | ✅ COMPLETED |
| 17 | Initialize Optimization   | Moved child refresh loop to `refreshAllChildren()` to prevent blocking. | ✅ COMPLETED |

***

### Hot Fix 4 – Refresh Guard (✅ COMPLETED)

**Target:** Hub v3.3.26

| #  | Feature          | Description                                                                                                   | Status      |
| -- | ---------------- | ------------------------------------------------------------------------------------------------------------- | ----------- |
| 18 | Connection Guard | `refreshAllChildren()` now exits immediately if the Telnet connection is dead, preventing a 160-second block. | ✅ COMPLETED |

***

### Phase 3B – Actionable Event Tracking (✅ COMPLETED)

**Target:** Shade v2.5.9

| #  | Feature                | Description                                                                                      | Status      |
| -- | ---------------------- | ------------------------------------------------------------------------------------------------ | ----------- |
| 19 | `lastEvent` Attribute  | Tracks current state (`command_sent`, `jitter`, `failed`, etc.) for precise automation triggers. | ✅ COMPLETED |
| 20 | `lastUpdate` Attribute | Timestamp of the last successful data burst.                                                     | ✅ COMPLETED |

***

## 📊 Implementation Details

### Version Numbering Strategy

**Format:** `vX.Y.Z`

* **X** – Major release (architectural changes)

* **Y** – Minor release (new features)

* **Z** – Patch release (bug fixes)

### Code Style Standards

* **Parent Driver:** Handles Telnet, routes messages, manages child lifecycle.

* **Child Driver:** Handles ARC commands, state tracking, retry logic, telemetry.

* **Logging:** Strict adherence to `ERROR`, `WARN`, `INFO`, `DEBUG` hierarchy.

***

## 📋 Complete Feature Index

| #  | Feature                                 | Phase     | Status      |
| -- | --------------------------------------- | --------- | ----------- |
| 1  | `ShadeAdd()` Initialization             | Phase 1   | ✅ COMPLETED |
| 2  | `parse()` Address Validation            | Phase 1   | ✅ COMPLETED |
| 3  | `ShadeRemove()` Safety Check            | Phase 1   | ✅ COMPLETED |
| 4  | Battery Formula Recalibration           | Hot Fix 1 | ✅ COMPLETED |
| 5  | Position Tolerance                      | Phase 2A  | ✅ COMPLETED |
| 6  | Intermediate Position Reset             | Phase 2A  | ✅ COMPLETED |
| 7  | `unschedule()` Name Fix                 | Phase 2A  | ✅ COMPLETED |
| 8  | `commandId` Stale Guard                 | Phase 2A  | ✅ COMPLETED |
| 9  | Child Log Verbosity UX                  | Phase 2A  | ✅ COMPLETED |
| 10 | New Defaults (Tol 2, Wait 20)           | Hot Fix 2 | ✅ COMPLETED |
| 11 | Enum Retry Count Dropdown               | Hot Fix 2 | ✅ COMPLETED |
| 12 | Exponential Backoff                     | Hot Fix 2 | ✅ COMPLETED |
| 13 | 8-Second Heartbeat Check                | Hot Fix 3 | ✅ COMPLETED |
| 14 | Deaf/Moving/Stuck Logic                 | Hot Fix 3 | ✅ COMPLETED |
| 15 | Parent Log Verbosity UX                 | Phase 2B  | ✅ COMPLETED |
| 16 | Remove Refresh Capability               | Phase 2B  | ✅ COMPLETED |
| 17 | Initialize Split (`refreshAllChildren`) | Phase 2B  | ✅ COMPLETED |
| 18 | Refresh Connection Guard                | Hot Fix 4 | ✅ COMPLETED |
| 19 | `lastEvent` Attribute                   | Phase 3B  | ✅ COMPLETED |
| 20 | `lastUpdate` Attribute                  | Phase 3B  | ✅ COMPLETED |

**Total Features:** 20\
**Completed:** 20\
**Deferred:** 0

***

## 📝 Documentation Plan

| Document                                     | Status    | Description                                              |
| -------------------------------------------- | --------- | -------------------------------------------------------- |
| **README.md**                                | ✅ Current | Project overview, features, installation, credits        |
| **CHANGELOG-Hub.md**                         | ✅ Current | Version history for the Parent Hub                       |
| **CHANGELOG-Shade.md**                       | ✅ Current | Version history for the Child Shade                      |
| **QuickStartInstallation\&Configuration.md** | ✅ Current | Step-by-step setup guide and tuning guide                |
| **RETR-Hub-Logic.md**                        | ✅ Current | Technical deep-dive on Telnet, Silencer, Initialize fix  |
| **RETR-Shade-Logic.md**                      | ✅ Current | Technical deep-dive on Retry, Tolerance, Heartbeat       |
| **PreferencesCommandsReference.md**          | ✅ Current | Quick reference for UI preferences and commands          |
| **ProjectStatementofPurpose.md**             | ✅ Current | Vision and use case for the project                      |
| **RequirementsDocument.md**                  | ✅ Current | Project requirements and feature roadmap (this document) |
| **RoadmapPhasedPlan.md**                     | ✅ Current | Working plan and design deep-dive (internal only)        |

***

## 🛡️ Disclaimers

### Community & Personal Use

These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Rollease Acmeda** or **Hubitat Elevation**.

### AI-Assisted Development Notice

This project is a collaborative effort between a human **Project Manager** and **AI as Code Master**. The Project Manager defines requirements, makes architectural decisions, and validates the result on production hardware. The AI drafts the code, documentation, and design proposals under the Project Manager's review and approval. All decisions are human-owned; the AI executes and documents them.

### Operational Safety

* **_Insuo Periculo_**: While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" shade movements, or unintended Hubitat behavior.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Production Status:** This fork is currently in **Production (Stable)**.

***

## 🚀 Next Steps

| Step | Action                                      | Owner |
| ---- | ------------------------------------------- | ----- |
| 1    | **Project Complete**                        | Both  |
| 2    | Monitor for bugs and maintain compatibility | David |

***

**Document Approved By:** David Ball-Quenneville (DGBQ)\
**Document Prepared By:** AI as Code Master\
**Date:** November 6, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀
