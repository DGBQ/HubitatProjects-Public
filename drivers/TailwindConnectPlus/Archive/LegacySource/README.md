### 🚪 Legacy Source – README

### _Original Driver Files – Reference Only_

***

### 🔢 Version Control

| Item                    | Details                       |
| ----------------------- | ----------------------------- |
| **Document Control**    | 1.0.0                         |
| **Current Status**      | **Archived – Reference Only** |
| **Folder**              | `Archive/LegacySource/`       |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ) |
| **Original Developers** | dbadge / Gelix                |

***

### 🧭 Overview

This folder contains the **original, unmodified source files** of the Tailwind Garage Door driver, as published by the original developers (`dbadge` / `Gelix`).

These files are preserved here for **historical reference, transparency, and attribution purposes only**.

They are **NOT** maintained, **NOT** updated, and **NOT** intended for active use.

***

### 📜 Why This Folder Exists

The **Tailwind Connect Plus** driver is a community fork of the original Tailwind Garage Door driver. When the fork was created, the goal was to:

| Goal                      | Description                                                 |
| ------------------------- | ----------------------------------------------------------- |
| **Preserve the original** | Keep the original files available for reference             |
| **Provide transparency**  | Show exactly what changed between the original and the fork |
| **Give proper credit**    | Acknowledge the original developers' work                   |
| **Enable comparison**     | Allow users and developers to compare versions if needed    |

Recent Hubitat platform updates introduced a **breaking change** that caused the original driver to fail on every poll attempt. The fork was created to fix this issue and add modern features.

***

### 📦 What's Inside

| File                                   | Description                         |
| -------------------------------------- | ----------------------------------- |
| `TailwindGarageDoor.groovy`            | Original parent driver (unmodified) |
| `TailwindGarageDoorChildDevice.groovy` | Original child driver (unmodified)  |

***

### ⚠️ Critical Warning

> **DO NOT USE THESE FILES FOR ACTIVE INSTALLATIONS.**

| Reason                              | Explanation                                                                                                          |
| ----------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| **Broken on current Hubitat**       | The original driver throws a `groovy.lang.MissingMethodException` on every poll attempt due to the `toInteger()` bug |
| **No longer maintained**            | These files are frozen in time and will not receive updates                                                          |
| **Missing modern features**         | No `driverHealth`, `lastError`, `lastFetch`, `verifySetup()`, `lastOpen`, `lastClosed`, or other improvements        |
| **Not compatible with current SOP** | Does not follow the DGBQ code style standards                                                                        |
| **No support provided**             | No troubleshooting or support will be offered for these files                                                        |

**If you install these files, the driver will fail.** Please use the active drivers from the main repository instead.

***

### 🐛 Known Issues in the Original

| Issue                                   | Impact                                                            | Resolved In |
| --------------------------------------- | ----------------------------------------------------------------- | ----------- |
| **`toInteger()` Error**                 | Driver fails on every poll attempt with `MissingMethodException`  | v1.0.0      |
| **No Error Handling**                   | Unhandled HTTP errors can crash the driver                        | v1.1.0      |
| **No Driver Health Tracking**           | No visibility into driver status                                  | v1.1.0      |
| **No Last Error Attribute**             | Errors only visible in logs, not in device attributes             | v1.1.0      |
| **No Enhanced Status Messages**         | Status attribute shows numeric codes instead of readable messages | v1.1.0      |
| **No Polling Watchdog**                 | Polling chain can break silently after hub reboots                | v1.1.0      |
| **No Installation Verification**        | No way to test configuration on-demand                            | v1.2.0      |
| **No Door Timestamps**                  | No `lastOpen` / `lastClosed` tracking                             | v1.2.0      |
| **No User-Facing Command Descriptions** | Command UI lacks helpful descriptions                             | v1.2.7      |
| **Missing Phase 4 UI Cleanups**         | Internal commands visible to users; Identify label confusing      | v1.3.2      |

***

### 🙏 Original Developers

The original Tailwind Garage Door driver was developed by:

| Role                    | Developer                                                                    |
| ----------------------- | ---------------------------------------------------------------------------- |
| **Original Author**     | dbadge                                                                       |
| **Repository Host**     | Gelix                                                                        |
| **Original Repository** | [github.com/Gelix/HubitatTailwind](https://github.com/Gelix/HubitatTailwind) |

Without their foundational work, the Tailwind Connect Plus fork would not exist. We are grateful for their contribution to the Hubitat community.

***

### ✅ Where to Get the Active Drivers

For active use, please install the current, maintained versions from the main repository:

| Resource              | Link                                                                                                          |
| --------------------- | ------------------------------------------------------------------------------------------------------------- |
| **Parent Driver**     | `TailwindConnectPlus.groovy` (v1.3.2)                                                                         |
| **Child Driver**      | `TailwindConnectPlus-Child.groovy` (v1.1.3)                                                                   |
| **Main README**       | [README.md](https://../README.md)                                                                             |
| **Quick Start Guide** | [QuickStartInstallation\&Configuration.md](https://../Documentation/QuickStartInstallation\&Configuration.md) |
| **Changelog**         | [CHANGELOG.md](https://../CHANGELOG.md)                                                                       |

***

### 🛡️ Disclaimers

#### Community & Personal Use

These files are provided "as-is" as a service to the Hubitat community for **reference and archival purposes only**. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Hubitat Elevation**, **Tailwind**, or the original developers.

#### Operational Safety

* **_Insuo Periculo_**: These files are not supported and should not be used in a production environment. Use at your own risk.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **No Support:** No troubleshooting or support will be provided for the files in this folder.

***

### 📜 Revision History

| Version    | Date       | Changes                                                                                                                                                                                                       |
| ---------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.0.0**  | 2026-09-16 | **Initial Document** – Created Legacy Source README to explain the purpose of this folder, warn against active use, document known issues, credit original developers, and point users to the active drivers. |
| **v0.0.0** | Pre-Fork   | Original driver (dbadge / Gelix) – Local API control, up to 3 doors, parent-child architecture, original capabilities.                                                                                        |

***

**Insuo Periculo** – Enter at your own risk. 🚀
