# 🏠 Rollease Acmeda Hubitat Integration
### _Managed Production Drivers for the Automate Pulse 2 Hub (README)_

## 🔢 Version Control
**Document Control:** 1.0.0  
**Current Status:** Beta  
- **Hub Driver:** `RolleaseAcmedaHub-DGBQ.groovy`
- **Shade Driver:** `RolleaseAcmedaShade-DGBQ.groovy`
**Maintenance Lead:** David Ball-Quenneville (DGBQ)  
**Original Developer:** Younes Oughla (Yoonoo)

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- [🧭 Overview](#overview)
- [🚀 Key Improvements](#key-improvements)
- [📦 Required Files](#required-files)
- [📑 Documentation](#documentation)
- [🛡️ Disclaimers](#disclaimers)
- [📜 Revision History](#revision-history)

</details>

---

<h2 id="overview">🧭 Overview</h2>

This repository contains a stabilized (in beta) and refined version of the Hubitat drivers for the **Rollease Acmeda Automate Pulse 2 Hub**. 

The original drivers developed by **Younes Oughla** provided the essential foundation for local IP control, and this project would not exist without that critical early work. As Rollease hardware and Hubitat environments have evolved, I found that my own home setup required a more robust logic framework to maintain peak reliability—especially given the lack of official, open API documentation for home automation hobbyists. 

Acting as the **Project Manager** for this overhaul, I utilized **Gemini AI** for deep-dive code auditing and technical execution. Together, we performed a "white-hat" analysis of the Hub's communication patterns to smooth out **logic-casting** conflicts that occasionally interrupted command sequences. This personal maintenance branch introduces advanced battery telemetry, RSSI reporting, and a streamlined shade management API—delivering the level of transparency and "set-and-forget" reliability that modern smart homes demand.

> **_Insuo Periculo_**: This software is provided "as-is" for personal use; please proceed at your own risk.

---

<h2 id="key-improvements">🚀 Key Improvements in this Fork</h2>

### **Core Stability & Bug Fixes**
* **GroovyCastException Resolution:** Resolved a persistent `java.lang.ClassCastException` during timing calculations that previously caused the driver to fail and crash when sending commands to **Shade Child** devices.
* **Surgical Error Silencer:** Mitigated the frequent `receive error: Stream is closed` log noise. This is now correctly identified as **"Normal Behavior"** (Info) resulting from the Hub's native **"Burst-and-Close"** communication logic, ensuring Hubitat logs remain clean while the Hub operates as designed.
* **Protocol Integrity:** Implemented strict **Zero-Padding** for ARC commands (e.g., `!001m050`) and removed artificial handshake delays, ensuring the Hub never ignores rapid-fire commands.

### **New Features & Enhancements**
* **Alexa Responsiveness:** Implemented **Proactive State Updates**. By instantly updating attributes upon command execution, the driver satisfies Alexa's strict response-time windows, eliminating "Device not responding" voice errors.
* **Advanced Telemetry:** Added native Hubitat `Battery` capability (calibrated for **10.8V–12.6V** ranges) and **RSSI (Signal Strength)** reporting to assist in troubleshooting and maintenance.
* **Shade Management API:** Introduced a new suite of commands (`ShadeDiscover`, `ShadeAdd`, `ShadeRemove`, and `ShadeDeleteAll`) and a real-time `shades` attribute to provide granular control over the child device lifecycle.
* **Performance Optimization:** Streamlined the Telnet parsing logic to provide faster, more reliable status updates for Dashboards, Alexa, and webCoRE.

---

<h2 id="required-files">📦 Required Files</h2>

To successfully integrate your shades, you must install **both** of the following drivers into your Hubitat "Drivers Code" section. I have maintained the original naming convention for consistency, adding the **-DGBQ** suffix to clearly identify this maintained fork from the original work.

1. **`RolleaseAcmedaHub-DGBQ.groovy` (The Parent):** Acts as the primary communication bridge. You will create one virtual device using this driver to manage the persistent Telnet connection to your Pulse 2 Hub’s IP address.
2. **`RolleaseAcmedaShade-DGBQ.groovy` (The Child):** The device-level driver. Once the Parent Hub is initialized and a discovery command is issued, it will automatically "spawn" your blinds, creating a dynamic device for each motor using this driver.

⚠️ **Note on Legacy Files:** Original, unedited source files (circa 2020) are available in the [`LegacySource`](./LegacySource) folder for developer reference. For active use, always install the drivers from the root directory.

---

<h2 id="documentation">📑 Documentation</h2>

### **Quick Start, Installation, Configuration**
* **[Setup Guide](./Documentation/QuickStartInstallation&Configuration.md):** I am currently refining the step-by-step setup instructions for this fork. Please check back shortly for a full visual guide.

### **Operational Specifications & Technical Deep-Dives**
For those interested in the "under-the-hood" logic of this fork, I have documented the overhaul process and the functional specifications of the drivers:

* **[RETR-Hub-Logic](./Documentation/RETR-Hub-Logic.md):** Specification for the Telnet stability, error silencing, and Parent driver logic.
* **[RETR-Shade-Logic](./Documentation/RETR-Shade-Logic.md):** Specification for the proactive state engine, battery telemetry, and RSSI reporting.

### **Revision History**
Detailed version-by-version changes are available in the specific changelogs:
* **[Hub Driver Changelog](./Documentation/CHANGELOG-Hub.md)**
* **[Shade Driver Changelog](./Documentation/CHANGELOG-Shade.md)**

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

### **Community & Personal Use**
These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Rollease Acmeda** or **Hubitat Elevation**. All logic refinements were developed through independent analysis to provide local integration where official documentation was unavailable.

### **AI-Assisted Development Notice**
This driver overhaul was a collaborative effort between a human **Project Manager** and **Gemini AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases. 
* **User Responsibility:** Users should monitor their initial installation to ensure their specific hardware configuration responds as intended.

### **Operational Safety**
* **_Insuo Periculo_**: While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" shade movements, or unintended Hubitat behavior.
* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.
* **Beta Status:** Please note that this fork is currently in **Beta**. While stable in my environment, you may encounter edge cases depending on your specific Pulse 2 Hub firmware version.

---

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date | Changes |
| :--- | :--- | :--- |
|      **1.0.1**     |      2026-03-24      |                         Update links and add more information                        |
| **1.0.0** | 2026-03-22 | New Readme; Operational Specification Alignment |