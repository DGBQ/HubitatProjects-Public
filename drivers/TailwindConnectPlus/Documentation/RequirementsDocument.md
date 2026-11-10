# 🚪 Tailwind Connect Plus – Requirements Document (DGBQ Fork)

### _Project Requirements & Feature Roadmap_

***

## 🔢 Version Control

| Item                    | Details                                  |
| ----------------------- | ---------------------------------------- |
| **Document Version**    | 1.5.0                                    |
| **Document Status**     | Final – Project Complete                 |
| **Project Name**        | Tailwind Connect Plus (DGBQ Fork)        |
| **Current Version**     | Hub `v1.3.2` / Child `v1.1.3` (Deployed) |
| **Next Target Version** | N/A (Maintenance Mode)                   |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ)            |
| **Original Developers** | dbadge / Gelix                           |
| **AI Collaboration**    | AI as Code Master                        |

***

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭 Project Overview

* 🎯 Mission Statement

* 📦 Repository Structure

* 🚀 Feature Roadmap

  * Phase 1 – Foundation & Bug Fix

  * Phase 2 – Reliability & Visibility

  * Phase 3 – User Experience & Logging

  * Phase 4 – UI Cleanup & Finalization

* 📊 Implementation Details

  * Version Numbering Strategy

  * Code Style Standards

* 📋 Complete Feature Index

* 📝 Documentation Plan

* 🛡️ Disclaimers

\</details>

***

\<h2 id="overview">🧭 Project Overview\</h2>

This project is a **maintained fork** of the Tailwind Garage Door driver originally developed by `dbadge` and hosted on `Gelix`'s GitHub repository. The driver provides local control for Tailwind Garage Door Controllers using the Tailwind Local API (firmware v9.95+).

**Why a Fork?**

Recent Hubitat platform updates introduced a breaking change: the `toInteger()` method can no longer be called directly on HTTP response objects. This caused the original driver to throw a `groovy.lang.MissingMethodException` on every poll attempt.

Additionally, the original driver lacked documentation, versioning, and the standardized code structure we require for our projects.

**This fork:**

* ✅ Fixes the `toInteger()` error

* ✅ Adopts standard DGBQ code practices

* ✅ Adds driver versioning and tracking

* ✅ Includes `refresh` command and auto-revert debug

* ✅ Is fully documented and maintained

***

\<h2 id="mission-statement">🎯 Mission Statement\</h2>

To provide a **reliable, self-healing, and professionally maintained** Tailwind Garage Door driver for Hubitat that:

* Eliminates the `toInteger()` error through proper type handling

* Offers full visibility into driver operations

* Includes guardrails and fail-safes for production use

* Is well-documented and easy to configure

* Follows standard DGBQ coding standards

* Retains all original capabilities (`Actuator`, `Sensor`, `ContactSensor`, `GarageDoorControl`)

***

\<h2 id="repository-structure">📦 Repository Structure\</h2>

text

```
TailwindConnectPlus/
├── README.md
├── CHANGELOG-Hub.md
├── CHANGELOG-Child.md
├── TailwindConnectPlus.groovy
├── TailwindConnectPlus-Child.groovy
├── Documentation/
│   ├── QuickStartInstallation&Configuration.md
│   ├── RequirementsDocument.md
│   ├── RETR-TailwindConnectPlus-Logic.md
│   ├── PreferencesCommandsReference.md
│   ├── ProjectStatementofPurpose.md
│   └── RoadmapPhasedPlan.md              ← internal, never published
└── Archive/
    └── LegacySource/
        ├── LegacySourceREADME.md
        ├── TailwindGarageDoor.groovy
        └── TailwindGarageDoorChildDevice.groovy
```

***

\<h2 id="feature-roadmap">🚀 Feature Roadmap\</h2>

***

\<h3 id="phase-1">Phase 1 – Foundation & Bug Fix (✅ COMPLETED)\</h3>

**Target:** Hub v1.0.0

| #  | Feature                     | Description                                                                   | Status      |
| -- | --------------------------- | ----------------------------------------------------------------------------- | ----------- |
| 1  | **Apply Bug Fix**           | Fix `toInteger()` error in `checkStatus()` by casting to String first         | ✅ COMPLETED |
| 2  | **Fork Repository**         | Create personal fork from `Gelix/HubitatTailwind`                             | ✅ COMPLETED |
| 3  | **Rename Driver**           | Parent: "Tailwind Connect Plus", Child: "Tailwind Connect Plus - Child"       | ✅ COMPLETED |
| 4  | **Update Metadata**         | Change `namespace`, `author`, `importUrl` to reflect new maintainer           | ✅ COMPLETED |
| 5  | **Driver Header**           | Add full copyright, license, and revision history header (SOP standard)       | ✅ COMPLETED |
| 6  | **Version Constant**        | Add `@Field static final String DRIVER_VERSION = "v1.0.0"`                    | ✅ COMPLETED |
| 7  | **driverVersion Attribute** | Add attribute to display version in device details                            | ✅ COMPLETED |
| 8  | **refresh Command**         | Add manual refresh/poll command for troubleshooting                           | ✅ COMPLETED |
| 9  | **Auto-Revert Debug**       | Auto-revert DEBUG logging after 2 hours                                       | ✅ COMPLETED |
| 10 | **Logging Helpers**         | Implement `logDebug`, `logInfo`, `logWarn`, `logError` helpers (SOP standard) | ✅ COMPLETED |
| 11 | **Sectioned Preferences**   | Group preferences into collapsible sections with `<b>` tags                   | ✅ COMPLETED |
| 12 | **Sync Both Drivers**       | Ensure header, metadata, and version match in parent and child drivers        | ✅ COMPLETED |

**Phase 1 Total:** 12 features – ✅ **ALL COMPLETED**

***

\<h3 id="phase-2">Phase 2 – Reliability & Visibility (✅ COMPLETED)\</h3>

**Target:** Hub v1.1.9

| #  | Feature                            | Description                                                               | Status      |
| -- | ---------------------------------- | ------------------------------------------------------------------------- | ----------- |
| 13 | **Error Handling**                 | Wrap HTTP calls in try-catch blocks to prevent crashes                    | ✅ COMPLETED |
| 14 | **Connection Retry Logic**         | Add retry logic with configurable attempts for failed HTTP requests       | ❌ REMOVED   |
| 15 | **Driver Health / Command Status** | Add `driverHealth` attribute to track driver health                       | ✅ COMPLETED |
| 16 | **Last Error Attribute**           | Add `lastError` attribute showing the most recent error                   | ✅ COMPLETED |
| 17 | **Last Fetch Attribute**           | Add `lastFetch` attribute showing when the last successful poll occurred  | ✅ COMPLETED |
| 18 | **Enhanced Status Messages**       | Improve `Status` attribute with descriptive messages                      | ✅ COMPLETED |
| 19 | **Polling Watchdog**               | Self-healing timer that checks if the driver is polling and resets if not | ✅ COMPLETED |
| 20 | **Initialize Robustness**          | Ensure polling resumes after Hubitat reboots                              | ✅ COMPLETED |
| 21 | **Identify Command**               | Add command to flash controller LED for identification                    | ✅ COMPLETED |

**Phase 2 Total:** 9 features – **8 COMPLETED, 1 REMOVED**

**Note:** Feature 14 (Connection Retry Logic) was removed in v1.1.9 after analysis showed that the controller always returns `0` as acknowledgment and the door always moves on the first attempt. Retry logic was causing unnecessary door cycles.

***

\<h3 id="phase-3">Phase 3 – User Experience & Logging (✅ COMPLETED)\</h3>

**Target:** Hub v1.2.7

| #  | Feature                              | Description                                                             | Status      |
| -- | ------------------------------------ | ----------------------------------------------------------------------- | ----------- |
| 22 | **Preference Descriptions**          | All preferences have clear descriptions with default values shown in UI | ✅ COMPLETED |
| 23 | **Command Descriptions**             | All non-parameterized commands have descriptions in UI                  | ✅ COMPLETED |
| 24 | **Installation Verification**        | Add `verifySetup()` command to test configuration on-demand             | ✅ COMPLETED |
| 25 | **Improved Error Messages**          | Better clarity when IP is invalid, token is missing, or door fails      | ✅ COMPLETED |
| 26 | **Improved Child Sync Log Message**  | Change from "DOESN'T match real door" to "Syncing child X from Y to Z"  | ✅ COMPLETED |
| 27 | **lastOpen / lastClosed Attributes** | Add timestamp attributes to child devices                               | ✅ COMPLETED |
| 28 | **Date/Time Format Preference**      | 8 display options for timestamps                                        | ✅ COMPLETED |
| 29 | **Door Status Change INFO Log**      | Log INFO message when any status change is detected                     | ✅ COMPLETED |

**Phase 3 Total:** 8 features – ✅ **ALL COMPLETED**

***

\<h3 id="phase-4">Phase 4 – UI Cleanup & Finalization (✅ COMPLETED)\</h3>

**Target:** Hub v1.3.2

| #  | Feature                    | Description                                                 | Status      |
| -- | -------------------------- | ----------------------------------------------------------- | ----------- |
| 30 | **Hide Internal Commands** | Remove `childOpen` and `childClose` from parent metadata    | ✅ COMPLETED |
| 31 | **Identify Rename**        | Rename to `Identify` with updated description               | ✅ COMPLETED |
| 32 | **Identify Success Log**   | Add success log at INFO level                               | ✅ COMPLETED |
| 33 | **LED Brightness Control** | Add preference and command to control status LED brightness | ❌ REMOVED   |

**Phase 4 Total:** 4 features – **3 COMPLETED, 1 REMOVED**

**Note:** Feature 33 (LED Brightness Control) was removed in v1.3.2 after testing confirmed that the `/led` endpoint is not available on all Tailwind controller models and firmware versions.

***

\<h2 id="implementation-details">📊 Implementation Details\</h2>\<h3 id="version-numbering">Version Numbering Strategy\</h3>

| Version             | Stage                                                                 |
| ------------------- | --------------------------------------------------------------------- |
| `v1.0.0`            | Initial release of the DGBQ Fork (Phase 1)                            |
| `v1.1.0` – `v1.1.9` | Phase 2 features and hotfixes                                         |
| `v1.2.0` – `v1.2.7` | Phase 3 features (User Experience & Logging)                          |
| `v1.3.0` – `v1.3.2` | Phase 4 features (UI Cleanup & Finalization)                          |
| `vX.Y.Z`            | Format: X=Major (new phases), Y=Minor (features), Z=Patch (bug fixes) |

**Format:** `vX.Y.Z`

* **X** – Major release (new phases)

* **Y** – Minor release (new features within a phase)

* **Z** – Patch release (bug fixes)

\<h3 id="code-style-standards">Code Style Standards\</h3>

The driver follows standard DGBQ code practices, which include:

* **Header Block:** Full copyright, Apache 2.0 license, and revision history.

* **Logging Standards:** Strict adherence to `ERROR`, `WARN`, `INFO`, `DEBUG` hierarchy.

* **Preference Descriptions:** Clear, descriptive labels with default values.

* **Command Descriptions:** Descriptive text for all non-parameterized commands.

***

\<h2 id="complete-feature-index">📋 Complete Feature Index\</h2>

| #  | Feature                          | Phase | Status      |
| -- | -------------------------------- | ----- | ----------- |
| 1  | Apply Bug Fix                    | 1     | ✅ COMPLETED |
| 2  | Fork Repository                  | 1     | ✅ COMPLETED |
| 3  | Rename Driver                    | 1     | ✅ COMPLETED |
| 4  | Update Metadata                  | 1     | ✅ COMPLETED |
| 5  | Driver Header                    | 1     | ✅ COMPLETED |
| 6  | Version Constant                 | 1     | ✅ COMPLETED |
| 7  | driverVersion Attribute          | 1     | ✅ COMPLETED |
| 8  | refresh Command                  | 1     | ✅ COMPLETED |
| 9  | Auto-Revert Debug (2 hours)      | 1     | ✅ COMPLETED |
| 10 | Logging Helpers                  | 1     | ✅ COMPLETED |
| 11 | Sectioned Preferences            | 1     | ✅ COMPLETED |
| 12 | Sync Both Drivers                | 1     | ✅ COMPLETED |
| 13 | Error Handling                   | 2     | ✅ COMPLETED |
| 14 | Connection Retry Logic           | 2     | ❌ REMOVED   |
| 15 | Driver Health / Command Status   | 2     | ✅ COMPLETED |
| 16 | Last Error Attribute             | 2     | ✅ COMPLETED |
| 17 | Last Fetch Attribute             | 2     | ✅ COMPLETED |
| 18 | Enhanced Status Messages         | 2     | ✅ COMPLETED |
| 19 | Polling Watchdog                 | 2     | ✅ COMPLETED |
| 20 | Initialize Robustness            | 2     | ✅ COMPLETED |
| 21 | Identify Command                 | 2     | ✅ COMPLETED |
| 22 | Preference Descriptions          | 3     | ✅ COMPLETED |
| 23 | Command Descriptions             | 3     | ✅ COMPLETED |
| 24 | Installation Verification        | 3     | ✅ COMPLETED |
| 25 | Improved Error Messages          | 3     | ✅ COMPLETED |
| 26 | Improved Child Sync Log Message  | 3     | ✅ COMPLETED |
| 27 | lastOpen / lastClosed Attributes | 3     | ✅ COMPLETED |
| 28 | Date/Time Format Preference      | 3     | ✅ COMPLETED |
| 29 | Door Status Change INFO Log      | 3     | ✅ COMPLETED |
| 30 | Hide Internal Commands           | 4     | ✅ COMPLETED |
| 31 | Identify Rename                  | 4     | ✅ COMPLETED |
| 32 | Identify Success Log             | 4     | ✅ COMPLETED |
| 33 | LED Brightness Control           | 4     | ❌ REMOVED   |

**Total Features:** 33\
**Completed:** 30\
**Removed:** 2

***

\<h2 id="documentation-plan">📝 Documentation Plan\</h2>

| Document                                     | Status    | Description                                                                    |
| -------------------------------------------- | --------- | ------------------------------------------------------------------------------ |
| **README.md**                                | ✅ Current | Project overview, features, installation, credits                              |
| **CHANGELOG-Hub.md**                         | ✅ Current | Version history for the Parent Hub                                             |
| **CHANGELOG-Child.md**                       | ✅ Current | Version history for the Child Driver                                           |
| **QuickStartInstallation\&Configuration.md** | ✅ Current | Step-by-step setup guide and troubleshooting                                   |
| **RETR-TailwindConnectPlus-Logic.md**        | ✅ Current | Technical deep-dive on the toInteger fix, polling flow, and child architecture |
| **PreferencesCommandsReference.md**          | ✅ Current | Quick reference for UI preferences and commands                                |
| **ProjectStatementofPurpose.md**             | ✅ Current | Vision and use case for the project                                            |
| **RequirementsDocument.md**                  | ✅ Current | Project requirements and feature roadmap (this document)                       |
| **RoadmapPhasedPlan.md**                     | ✅ Current | Working plan and design deep-dive (internal only)                              |
| **LegacySourceREADME.md**                    | ✅ Current | Explanation of the archived original source files                              |

***

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

### Community & Personal Use

These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Tailwind** or **Hubitat Elevation**.

### AI-Assisted Development Notice

This project is a collaborative effort between a human **Project Manager** and **AI as Code Master**. The Project Manager defines requirements, makes architectural decisions, and validates the result on production hardware. The AI drafts the code, documentation, and design proposals under the Project Manager's review and approval. All decisions are human-owned; the AI executes and documents them.

### Operational Safety

* **_Insuo Periculo_**: While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" door movements, or unintended Hubitat behavior.

* **Physical Safety Warning:** Garage doors are heavy machinery. Always ensure proper safety sensors are installed and functional. This driver should NOT be used as the primary safety mechanism for your garage door.

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
**Date:** November 10, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀
