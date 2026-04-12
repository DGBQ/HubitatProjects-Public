# 🔌 Rollease Acmeda ARC Protocol Reference – Pulse 2 Hub Integration

### 🔢 Version Control
**Document Control:** 1.0.0  
**Current Releases:** Beta  
- **Hub Driver:** `RolleaseAcmedaHub-DGBQ.groovy` (v3.3.12)
- **Shade Driver:** `RolleaseAcmedaShade_DGBQ.groovy` (v2.3.9)  
**Maintenance Lead:** David Ball-Quenneville (DGBQ)  
**Original Developer:** Younes Oughla (Yoonoo)

---

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- [🧭 Introduction & Overview](#introduction--overview)
- [📡 ARC Command Structure](#arc-command-structure)
- [📋 Supported ARC Commands](#supported-arc-commands)
- [📨 Response Messages (Hub to Driver)](#response-messages-hub-to-driver)
- [🔧 How the Driver Uses These Commands](#how-the-driver-uses-these-commands)
- [🛠️ Troubleshooting ARC Communication](#troubleshooting-arc-communication)
- [🔗 Reference / External Links](#reference--external-links)
- [🛡️ Disclaimers](#disclaimers)
- [📜 Revision History](#revision-history)

</details>

---

<h2 id="introduction--overview">🧭 Introduction & Overview</h2>

Welcome to the **Rollease Acmeda ARC Protocol Reference** for the Pulse 2 Hub integration. This document describes the raw command structure and communication patterns used by the DGBQ driver suite. While the manufacturer does not provide official documentation, the community has reverse‑engineered the **ARC (Automate Remote Control) protocol** to enable local control via Telnet (port 1487). This reference is intended for advanced users, integrators, and developers who wish to understand the low‑level commands that appear in logs or to troubleshoot unusual behaviour.

### Integration and API Usage
*   **Rollease Acmeda Pulse 2 Hub Access:** Communication with the **Pulse 2 Hub** occurs over the local network via the **ARC protocol** (Telnet port 1487). This is a proprietary protocol; no official public documentation exists. The command structures documented here are derived from community reverse-engineering and testing.
*   **Information Accuracy:** Development was performed using the best available resources at the time of release. **Hubitat Elevation** firmware, **Pulse 2 Hub** firmware, or integration methods may change, which may impact the functionality of these drivers.
*   **Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with, endorsed by, or supported by **Rollease Acmeda**, **Hubitat Inc.**, or any other mentioned entities.

---

<h2 id="arc-command-structure">📡 ARC Command Structure</h2>

The ARC protocol uses a fixed‑length, text‑based command format. Every command sent to the Pulse 2 Hub follows this pattern:

```
! \[Motor ID (3 chars)] \[Command (1 char)] \[Data (0–3 chars)]
```

**Example:** `!I39m050` moves shade `I39` to 50% closed.

### Command Components

| Component | Length | Description |
| :--- | :--- | :--- |
| **`!`** | 1 char | Start‑of‑message delimiter (required) |
| **Motor ID** | 3 chars | Unique identifier for a shade (e.g., `I39`, `BSG`, `9E3`). `000` is reserved for the hub itself. |
| **Command** | 1 char | Single letter defining the action (case‑sensitive). |
| **Data** | 0–3 chars | Parameter for the command (e.g., position, request symbol `?`). |

---

<h2 id="supported-arc-commands">📋 Supported ARC Commands</h2>

The following commands are recognised by the Pulse 2 Hub and used by the DGBQ driver.

| Command | Data | Action | Example |
| :--- | :--- | :--- | :--- |
| `m` | `000` – `100` | Move shade to a **position** (0 = fully open, 100 = fully closed). The value is three digits, zero‑padded. | `!I39m050` → Move to 50% closed. |
| `r` | `?` | Request **status** (position, battery, RSSI). The hub responds with a `r` message containing the current position. | `!I39r?` → Request status. |
| `s` | (none) | **Stop** the shade immediately. | `!I39s` → Stop movement. |
| `p` | `Vc?` | Request **battery and signal report**. The hub responds with a `pVc` message containing voltage and RSSI. | `!I39pVc?` → Request battery/RSSI. |
| `L` | `?` | **Discover** all shades paired to the hub. The hub responds with a list of motor IDs. | `!000L?` → Discover shades. |
| `v` | `?` | Request **hub version** information. | `!000v?` → Query hub version. |

---

<h2 id="response-messages-hub-to-driver">📨 Response Messages (Hub to Driver)</h2>

The hub sends back messages that the parent driver parses and forwards to child devices.

| Response Format | Meaning | Example |
| :--- | :--- | :--- |
| `!IDrPPP` | Position report. `PPP` = 3‑digit position (0‑100). | `!I39r050` → Shade I39 at 50%. |
| `!IDmPPP` | Movement initiation (direction inferred from position). | `!I39m050` → Moving to 50%. |
| `!IDpVcVVVV,RRR` | Battery and signal report. `VVVV` = voltage × 100 (e.g., `1242` = 12.42 V). `RRR` = RSSI (e.g., `R72`). | `!I39pVc1242,R72` → 12.42 V, RSSI 72. |
| `!000LID1,ID2,…` | Shade discovery response. Lists all motor IDs. | `!000LI39,BSG,9E3` → Three shades discovered. |

---

<h2 id="how-the-driver-uses-these-commands">🔧 How the Driver Uses These Commands</h2>

The **parent hub driver** translates Hubitat commands into ARC strings. The **child shade drivers** send movement commands and parse responses.

| Hubitat Action | ARC Command Sent | Driver Method |
| :--- | :--- | :--- |
| `open()` | `!IDm000` | `open()` |
| `close()` | `!IDm100` | `close()` |
| `setPosition(50)` | `!IDm050` | `setPosition()` |
| `stop()` | `!IDs` | `stop()` |
| `refresh()` | `!IDr?` | `requestStatus()` |
| `requestBatteryStatus()` | `!IDpVc?` | `requestBatteryStatus()` |
| `ShadeDiscover` (parent) | `!000L?` | `ShadeDiscover()` |

---

<h2 id="troubleshooting-arc-communication">🛠️ Troubleshooting ARC Communication</h2>

- **No response from hub** – Verify the hub’s IP address and that port `1487` is reachable. Check the parent driver’s `status` attribute.
- **Shade moves but no position update** – The hub may not have sent a `r` response. Run `Refresh` on the child device.
- **`lastCommand` shows unexpected characters** – The raw command is logged as sent; it may include a newline (`\r\n`) for telnet termination.
- **Battery report missing** – Use the `Request Battery Status` command to force a `pVc?` query.

---

<h2 id="reference--external-links">🔗 Reference / External Links</h2>

- 📘 [Hubitat Community Thread for Rollease Acmeda Driver](https://community.hubitat.com/…)
- 📘 [ARC Protocol (Unofficial) – Home Assistant Integration Notes](https://github.com/…)

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

### **Community & Personal Use**
These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Rollease Acmeda** or **Hubitat Elevation**. All logic refinements were developed through independent analysis to provide local integration where official documentation was unavailable.

### **AI-Assisted Development Notice**
This driver overhaul was a collaborative effort between a human **Project Manager** and **Gemini AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases. 
* **User Responsibility:** Users should monitor their initial installation to ensure their specific hardware configuration responds as intended.

### **Operational Safety**
* **_Insuo Periculo_:** While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" shade movements, or unintended Hubitat behavior.
* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.
* **Beta Status:** Please note that this fork is currently in **Beta**. While stable in my environment, you may encounter edge cases depending on your specific Pulse 2 Hub firmware version.

---

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date | Author | Changes |
| :--- | :--- | :--- | :--- |
| 1.00 | 2026-04-12 | DGBQ | Initial release of **Rollease Acmeda ARC Protocol Reference**. |