# 🚪 Tailwind Connect Plus (DGBQ Fork)

### _Local Control Garage Door Driver for Hubitat_

***

## 🔢 Version Control

| Item                    | Details                                                                                        |
| ----------------------- | ---------------------------------------------------------------------------------------------- |
| **Document Control**    | 1.4.0                                                                                          |
| **Current Status**      | **Production Stable**                                                                          |
| **Hub Drivers**         | `TailwindConnectPlus.groovy` (Parent v1.3.2) `TailwindConnectPlus-Child.groovy` (Child v1.1.3) |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ)                                                                  |
| **Original Developers** | dbadge / Gelix                                                                                 |

***

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭 Overview

* 🚀 Key Improvements

* 📦 Required Files

* 📑 Documentation

* 🛡️ Disclaimers

* 📜 Revision History

\</details>

***

\<h2 id="overview">🧭 Overview\</h2>

This repository contains a **a production fork** of the Tailwind Garage Door driver, originally developed by **dbadge** and hosted on **Gelix**'s GitHub repository.

The original driver provided the essential foundation for local control of Tailwind Garage Door Controllers using the Tailwind Local API (firmware v9.95+). This fork builds upon that foundation with critical fixes, documentation, and a comprehensive feature roadmap.

**Why this fork exists:**

* The original driver **failed on recent Hubitat platform updates** due to a `groovy.lang.MissingMethodException` caused by the `toInteger()` method no longer being supported on HTTP response objects.

* The original driver **lacked documentation**, version tracking, and standardized code structure.

* The original driver **did not include common Hubitat features** like a `refresh` command, auto-revert debug, or logging helpers.

* This fork continues to provide **full local control** with enhanced reliability and visibility.

Acting as the **Project Manager** for this fork, I performed a deep‑dive code audit, implemented critical fixes, and continue to maintain and enhance the driver with a comprehensive feature roadmap.

> **_Insuo Periculo_**: This software is provided "as-is" for personal use; please proceed at your own risk.

***

\<h2 id="key-improvements">🚀 Key Improvements in this Fork\</h2>

### Phase 1 – Foundation & Bug Fix (✅ COMPLETED)

* **Fixed `toInteger()` Error** – Resolved `groovy.lang.MissingMethodException` by casting HTTP response to String first:

  * Original: `resp.data.toInteger()` ❌

  * Fixed: `(resp.data as String).toInteger()` ✅

* **Driver Renamed** – Now called **Tailwind Connect Plus** (Parent) and **Tailwind Connect Plus - Child**.

* **Driver Header** – Added full copyright, Apache 2.0 license, and revision history header following SOP standards.

* **Version Tracking** – Added `@Field static final String DRIVER_VERSION` and `driverVersion` attribute.

* **refresh Command** – Added manual refresh/poll command for troubleshooting.

* **Auto-Revert Debug** – Added preference to automatically disable debug logging after 2 hours.

* **Logging Helpers** – Implemented `logDebug`, `logInfo`, `logWarn`, `logError` helpers following SOP standards.

* **Sectioned Preferences** – Grouped preferences into collapsible sections with `<b>` tags.

* **Child Driver Synced** – Version and metadata match the parent driver.

### Phase 2 – Reliability & Visibility (✅ COMPLETED)

* **Error Handling** – Wrapped HTTP calls in try-catch blocks to prevent crashes.

* **Driver Health / Command Status** – Added `driverHealth` attribute to track driver health (normal, failed, timeout, communication\_error).

* **Last Error Attribute** – Dedicated `lastError` attribute for troubleshooting.

* **Last Fetch Attribute** – Shows when the last successful poll occurred.

* **Enhanced Status Messages** – Human-readable status messages instead of numeric codes (e.g., "All doors closed").

* **Polling Watchdog** – Self‑healing timer that detects and repairs broken polling chains.

* **Initialize Robustness** – Ensures polling resumes reliably after Hubitat reboots.

* **Identify Command** – Flash controller LED for identification (multi-controller setups).

* **Removed Retry Logic** – Simplified command flow. Commands are sent once, fast polling confirms success or timeout.

* **Timeout-Based Failure Detection** – If the door doesn't reach the desired state within `garageDoorTimeout` (default: 60 seconds), it's marked as a failure.

### Phase 3 – User Experience & Logging (✅ COMPLETED)

* **Preference Descriptions** – All preferences now have clear descriptions with default values shown in UI.

* **Command Descriptions** – Descriptive text for commands in the UI (refresh, clearDebug, identify, verifySetup).

* **Installation Verification** – `verifySetup()` command to test configuration on‑demand.

* **Improved Error Messages** – Actionable guidance when IP is invalid, token is missing, or door fails to operate.

* **Improved Child Sync Log Message** – Now shows "Syncing child X from Y to Z" instead of "DOESN'T match real door".

* **lastOpen / lastClosed Attributes** – Added to child devices to track when doors last opened or closed.

* **Date/Time Format Preference** – 8 display options for timestamps (MM/DD/YYYY AM/PM, YYYY-MM-DD, etc.).

* **Door Status Change INFO Log** – Logs when door status changes from any source (app, remote, driver).

### Phase 4 – Advanced Features (✅ COMPLETED)

* **Hide Internal Commands** – Removed `childOpen` and `childClose` from parent metadata (UI cleanup).

* **Identify Rename** – Renamed to `Identify` with updated description clarifying it identifies the controller.

* **Identify Success Log** – Added success log at INFO level.

* **LED Brightness Control** – Added and then removed due to compatibility issues with some controller models/firmware versions. The `/led` endpoint is not available on all controllers.

***

\<h2 id="required-files">📦 Required Files\</h2>

To successfully integrate this driver, you must install the following drivers into your Hubitat "Drivers Code" section.

1. **`TailwindConnectPlus.groovy`** – The parent driver (v1.3.2). You will create one virtual device using this driver and configure it with your IP, Local Command Key, and door preferences.

2. **`TailwindConnectPlus-Child.groovy`** – The child driver (v1.1.3). This is installed automatically when the parent creates child devices for each door.

⚠️ **Note:** Original, unedited source files are available in the original author's repository for reference, and are also archived in the `Archive/LegacySource/` folder. For active use, always install the drivers from this repository, as they contain critical fixes and enhancements.

***

\<h2 id="known-limitations">⚠️ Known Limitations\</h2>

| Limitation                                   | Explanation                                                                                                                                |
| -------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **No descriptions for childOpen/childClose** | Hubitat does not support descriptions for commands that take parameters (like `integer`). This is a platform limitation, not a driver bug. |
| **Removed `poll` Command**                   | The legacy `poll` command has been removed. Use `refresh` instead for manual polling.                                                      |
| **LED Brightness Control Removed**           | The `/led` endpoint is not available on all Tailwind controller models or firmware versions. This feature has been removed.                |

***

\<h2 id="documentation">📑 Documentation\</h2>

### Quick Start, Installation, Configuration

* **[Quick Start Installation & Configuration](https://./Documentation/QuickStartInstallation\&Configuration.md)** – Step‑by‑step instructions for installing and configuring the driver.

### Requirements & Technical Specifications

* **[Requirements Document](https://./Documentation/RequirementsDocument.md)** – Project overview, feature roadmap, and complete feature index.

* **[RETR-Tailwind-API.md](https://./Documentation/RETR-Tailwind-API.md)** – Technical deep‑dive on the `toInteger()` fix, Tailwind Local API, status code mapping, and child device architecture.

### Revision History

Detailed version‑by‑version changes are available in the specific changelog:

* **[CHANGELOG.md](https://./CHANGELOG.md)**

### Archived Legacy Source

* **[Legacy Source README](https://./Archive/LegacySource/LegacySourceREADME.md)** – Explains the original developer's source files, why they are archived, and why they should not be used for active installations.

***

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

### Community & Personal Use

These drivers are provided "as‑is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Hubitat Elevation**, **Tailwind**, or the original developers (`dbadge` / `Gelix`).

### AI‑Assisted Development Notice

This driver patch was a collaborative effort between a human **Project Manager** and a few **AI**. While AI‑assisted development allows for deep‑dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases.

* **User Responsibility:** Users should monitor their initial installation to ensure their specific garage door configuration responds as intended.

### Operational Safety

* **_Insuo Periculo_**: While this driver is tested in a live production environment, custom code interacts with your garage door controller and Hubitat automations in unique ways. I am not responsible for any unintended triggers, missed events, or unexpected door behavior.

* **Physical Safety Warning:** Garage doors are heavy machinery. Always ensure proper safety sensors are installed and functional. This driver should NOT be used as the primary safety mechanism for your garage door.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Production Status:** This fork is currently in **Production (Stable)** with all phases (1-4) complete. The driver is feature-complete.

***

\<h2 id="revision-history">📜 Revision History\</h2>

| Version    | Date       | Changes                                                                                                                                                                                                                                                                                                                      |
| ---------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.4.0**  | 2026-09-16 | Updated README to correct driver file names (`TailwindConnectPlus.groovy` and `TailwindConnectPlus-Child.groovy`), removed the word "professional", added reference to the archived Legacy Source, and corrected folder structure paths.                                                                                     |
| **1.3.0**  | 2026-09-09 | Updated README to reflect Phase 4 completion; Current versions: Parent v1.3.2 / Child v1.1.3; Added Phase 4 features; Updated Known Limitations; Updated Production Status                                                                                                                                                   |
| **1.2.0**  | 2026-08-31 | Updated README to reflect Phase 3 completion; Current versions: Parent v1.2.7 / Child v1.1.3; Added Phase 3 features (Preference Descriptions, Command Descriptions, verifySetup, lastOpen/lastClosed, Date/Time Format, Door Status Change INFO Log); Added Known Limitations section; Updated Production Status            |
| **1.1.0**  | 2026-08-18 | Updated README to reflect Phase 2 completion; Current versions: Parent v1.1.9 / Child v1.1.1; Phase 3 features updated; Removed retry logic from feature list; Added Driver Health/Command Status                                                                                                                            |
| **1.0.0**  | 2026-08-12 | **Initial DGBQ Fork** – Fixed `toInteger()` error; Added driver header, version constant, `driverVersion` attribute, `refresh` command, auto-revert debug, logging helpers, sectioned preferences; Renamed to Tailwind Connect Plus; Updated namespace and author; Synced child driver; Created complete documentation suite |
| **v0.0.0** | Pre-Fork   | Original driver (dbadge / Gelix) – Local API control, up to 3 doors, parent-child architecture, original capabilities                                                                                                                                                                                                        |

***

