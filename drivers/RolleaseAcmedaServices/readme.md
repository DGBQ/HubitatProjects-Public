# 🏠 Rollease Acmeda Hubitat Integration

### _Managed Production Drivers for the Automate Pulse 2 Hub (README)_

## 🔢 Version Control

**Document Control:** 1.2.0\
**Current Status:** Beta

* **Hub Driver:** `RolleaseAcmedaHub-DGBQ.groovy` (v3.3.24)

* **Shade Driver:** `RolleaseAcmedaShade-DGBQ.groovy` (v2.5.5)\
  **Maintenance Lead:** David Ball-Quenneville (DGBQ)\
  **Original Developer:** Younes Oughla (Yoonoo)

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

This repository contains a stabilized (in beta) and refined version of the Hubitat drivers for the **Rollease Acmeda Automate Pulse 2 Hub**.

The original drivers developed by **Younes Oughla** provided the essential foundation for local IP control, and this project would not exist without that critical early work. As Rollease hardware and Hubitat environments have evolved, I found that my own home setup required a more robust logic framework to maintain peak reliability—especially given the lack of official, open API documentation for home automation hobbyists.

Acting as the **Project Manager** for this overhaul, I utilized **AI** for deep-dive code auditing and technical execution. Together, we performed a "white-hat" analysis of the Hub's communication patterns to smooth out **logic-casting** conflicts that occasionally interrupted command sequences. This personal maintenance branch introduces advanced battery telemetry, RSSI reporting, confirmation-based command retry with position tolerance, and a streamlined shade management API—delivering the level of transparency and "set-and-forget" reliability that modern smart homes demand.

> **_Insuo Periculo_**: This software is provided "as-is" for personal use; please proceed at your own risk.

***

\<h2 id="key-improvements">🚀 Key Improvements in this Fork\</h2>

### **Core Stability & Bug Fixes**

* **GroovyCastException Resolution:** Resolved a persistent `java.lang.ClassCastException` during timing calculations that previously caused the driver to fail and crash when sending commands to **Shade Child** devices.

* **Surgical Error Silencer:** Mitigated the frequent `receive error: Stream is closed` log noise. This is now correctly identified as **"Normal Behavior"** (Info) resulting from the Hub's native **"Burst-and-Close"** communication logic, ensuring Hubitat logs remain clean while the Hub operates as designed.

* **Protocol Integrity:** Implemented strict **Zero-Padding** for ARC commands (e.g., `!001m050`) and removed artificial handshake delays, ensuring the Hub never ignores rapid-fire commands.

* **Child Device Address Validation (v3.3.24):** The Parent Hub now validates and auto-corrects a child's `motorAddress` preference when a mismatch is detected. New children created via `ShadeAdd` are now initialized immediately, ensuring they receive the correct address from the start.

* **Safe Shade Removal (v3.3.24):** `ShadeRemove` now performs a safety check against the child's `motorAddress` before deletion. If the address does not match, deletion is aborted to prevent accidentally removing the wrong device—a bug that previously caused an unintended loss of a working shade.

* **Retry State Fixes (v2.5.5):** Fixed `unschedule()` calls that were using non-existent method names (`retryCommand` and `jitterRetry`). These were no-ops that left stale timers running. Also added a `commandId` stale-callback guard to prevent race conditions when commands are sent in rapid succession.

### **New Features & Enhancements**

* **Alexa Responsiveness:** Implemented **Proactive State Updates**. By instantly updating attributes upon command execution, the driver satisfies Alexa's strict response-time windows, eliminating "Device not responding" voice errors.

* **Confirmation-Based Command Retry (v2.5.2):** Commands are now retried only if the Hub does not confirm the target position. Users can configure the retry count and wait time. This dramatically improves reliability in noisy RF environments.

* **Position Tolerance (v2.5.5):** New preference (default ±1%) that allows a shade to be considered "confirmed" when it reaches within tolerance of the target. This eliminates false retries on shades that physically stop 1–2% short of the target—a common occurrence on roman shades and older motors.

* **Intermediate Position Reset (v2.5.5):** If a shade reports an intermediate position while moving, the confirmation timer is now reset (up to 2 times) instead of firing jitter. This gives slow shades time to complete their movement without generating false failure warnings.

* **RF Jitter Workaround (v2.5.2):** If all retries fail, the driver sends a harmless status request to wake the Hub's RF transmitter and then retries the original command. This feature is enabled by default and can be disabled.

* **Log Verbosity Dropdown (v2.5.5):** Replaced the confusing `Enable Debug Logging` and `Enable Description Logging` toggles with a single **Log Verbosity** dropdown (ERROR, WARN, INFO, DEBUG; default INFO). `Auto-Revert Debug` now reverts DEBUG back to INFO after 30 minutes.

* **Advanced Battery Telemetry (v2.5.4):** The battery percentage formula has been recalibrated to **9.5V–12.6V** to closely match the Rollease app (previously 10.8V–12.6V). Testing across eight shades showed typical accuracy within ±5%. A per-shade **Battery Offset** preference remains available for fine-tuning (e.g., for roman vs roller shades with different discharge curves).

* **RSSI Reporting:** Signal strength is reported natively for troubleshooting and mesh health monitoring.

* **Shade Management API:** Introduced a new suite of commands (`ShadeDiscover`, `ShadeAdd`, `ShadeRemove`, and `ShadeDeleteAll`) and a real-time `shades` attribute to provide granular control over the child device lifecycle.

* **Performance Optimization:** Streamlined the Telnet parsing logic to provide faster, more reliable status updates for Dashboards, Alexa, and webCoRE.

***

\<h2 id="required-files">📦 Required Files\</h2>

To successfully integrate your shades, you must install **both** of the following drivers into your Hubitat "Drivers Code" section. I have maintained the original naming convention for consistency, adding the **-DGBQ** suffix to clearly identify this maintained fork from the original work.

1. **`RolleaseAcmedaHub-DGBQ.groovy` (The Parent):** Acts as the primary communication bridge. You will create one virtual device using this driver to manage the persistent Telnet connection to your Pulse 2 Hub's IP address.

2. **`RolleaseAcmedaShade-DGBQ.groovy` (The Child):** The device-level driver. Once the Parent Hub is initialized and a discovery command is issued, it will automatically "spawn" your blinds, creating a dynamic device for each motor using this driver.

⚠️ **Note on Legacy Files:** Original, unedited source files (circa 2020) are available in the [`LegacySource`](https://./LegacySource) folder for developer reference. For active use, always install the drivers from the root directory.

***

\<h2 id="documentation">📑 Documentation\</h2>

### **Quick Start, Installation, Configuration**

* **[Setup Guide](https://./Documentation/QuickStartInstallation\&Configuration.md):** Step-by-step setup instructions, including preference references and battery offset guidance.

### **Operational Specifications & Technical Deep-Dives**

For those interested in the "under-the-hood" logic of this fork, I have documented the overhaul process and the functional specifications of the drivers:

* **[RETR-Hub-Logic](https://./Documentation/RETR-Hub-Logic.md):** Specification for the Telnet stability, error silencing, and Parent driver logic.

* **[RETR-Shade-Logic](https://./Documentation/RETR-Shade-Logic.md):** Specification for the proactive state engine, confirmation-based retry, position tolerance, RF jitter, battery telemetry, and RSSI reporting.

* **[ARC Protocol Reference](https://./Documentation/RolleaseAcmedaARCProtocolReference.md):** Detailed reference for the raw ARC commands used by the Pulse 2 Hub.

### **Revision History**

Detailed version-by-version changes are available in the specific changelogs:

* **[Hub Driver Changelog](https://./Documentation/CHANGELOG-Hub.md)**

* **[Shade Driver Changelog](https://./Documentation/CHANGELOG-Shade.md)**

***

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

### **Community & Personal Use**

These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Rollease Acmeda** or **Hubitat Elevation**. All logic refinements were developed through independent analysis to provide local integration where official documentation was unavailable.

### **AI-Assisted Development Notice**

This driver overhaul was a collaborative effort between a human **Project Manager** and **AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases.

* **User Responsibility:** Users should monitor their initial installation to ensure their specific hardware configuration responds as intended.

### **Operational Safety**

* **_Insuo Periculo_**: While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" shade movements, or unintended Hubitat behavior.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Child Device Removal:** Do **not** use the `Shade Remove` command as a means of swapping addresses between devices. Use the Hubitat **Swap Apps Device** tool for automations, and update the `motorAddress` preference directly on the child device to perform an address change.

* **Beta Status:** Please note that this fork is currently in **Beta**. While stable in my environment, you may encounter edge cases depending on your specific Pulse 2 Hub firmware version.

***

\<h2 id="revision-history">📜 Revision History\</h2>

| Version   | Date       | Changes                                                                                                                                                                                                                                                              |
| --------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.2.0** | 2026-09-16 | Aligned README with Shade v2.5.5. Added Position Tolerance, Intermediate Position Reset, retry state fixes, and Log Verbosity dropdown to Key Improvements. Updated overview to mention position tolerance.                                                          |
| 1.1.0     | 2026-09-11 | Aligned README with Hub v3.3.24 and Shade v2.5.4. Added confirmation-based retry, RF jitter, battery formula recalibration (9.5V–12.6V), Phase 1 bug fixes (child address validation, safe `ShadeRemove`), battery offset guidance, and updated documentation links. |
| 1.0.1     | 2026-03-24 | Update links and add more information                                                                                                                                                                                                                                |
| 1.0.0     | 2026-03-22 | New Readme; Operational Specification Alignment                                                                                                                                                                                                                      |

***

**End of updated README.md**
