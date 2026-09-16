# 🔌 Rollease Acmeda ARC Protocol Reference – Pulse 2 Hub Integration

### 🔢 Version Control

**Document Control:** 1.2.0\
**Current Releases:** Beta

* **Hub Driver:** `RolleaseAcmedaHub-DGBQ.groovy` (v3.3.24)

* **Shade Driver:** `RolleaseAcmedaShade_DGBQ.groovy` (v2.5.5)\
  **Maintenance Lead:** David Ball-Quenneville (DGBQ)\
  **Original Developer:** Younes Oughla (Yoonoo)

***

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭 Introduction & Overview

* 📡 ARC Command Structure

* 📋 Supported ARC Commands

* 📨 Response Messages (Hub to Driver)

* 🔧 How the Driver Uses These Commands

* 🔋 Battery Reporting Notes

* 🛠️ Troubleshooting ARC Communication

* 🔗 Reference / External Links

* 🛡️ Disclaimers

* 📜 Revision History

\</details>

***

\<h2 id="introduction--overview">🧭 Introduction & Overview\</h2>

Welcome to the **Rollease Acmeda ARC Protocol Reference** for the Pulse 2 Hub integration. This document describes the raw command structure and communication patterns used by the DGBQ driver suite. While the manufacturer does not provide official documentation, the community has reverse‑engineered the **ARC (Automate Remote Control) protocol** to enable local control via Telnet (port 1487). This reference is intended for advanced users, integrators, and developers who wish to understand the low‑level commands that appear in logs or to troubleshoot unusual behaviour.

### Integration and API Usage

* **Rollease Acmeda Pulse 2 Hub Access:** Communication with the **Pulse 2 Hub** occurs over the local network via the **ARC protocol** (Telnet port 1487). This is a proprietary protocol; no official public documentation exists. The command structures documented here are derived from community reverse-engineering and testing.

* **Information Accuracy:** Development was performed using the best available resources at the time of release. **Hubitat Elevation** firmware, **Pulse 2 Hub** firmware, or integration methods may change, which may impact the functionality of these drivers.

* **Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with, endorsed by, or supported by **Rollease Acmeda**, **Hubitat Inc.**, or any other mentioned entities.

***

\<h2 id="arc-command-structure">📡 ARC Command Structure\</h2>

The ARC protocol uses a fixed‑length, text‑based command format. Every command sent to the Pulse 2 Hub follows this pattern:

text

```
! [Motor ID (3 chars)] [Command (1 char)] [Data (0–3 chars)]
```

**Example:** `!I39m050` moves shade `I39` to 50% closed.

### Command Components

| Component    | Length    | Description                                                                                      |
| ------------ | --------- | ------------------------------------------------------------------------------------------------ |
| **`!`**      | 1 char    | Start‑of‑message delimiter (required)                                                            |
| **Motor ID** | 3 chars   | Unique identifier for a shade (e.g., `I39`, `BSG`, `9E3`). `000` is reserved for the hub itself. |
| **Command**  | 1 char    | Single letter defining the action (case‑sensitive).                                              |
| **Data**     | 0–3 chars | Parameter for the command (e.g., position, request symbol `?`).                                  |

***

\<h2 id="supported-arc-commands">📋 Supported ARC Commands\</h2>

The following commands are recognised by the Pulse 2 Hub and used by the DGBQ driver.

| Command | Data          | Action                                                                                                                                                                                             | Example                                                     |
| ------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| `m`     | `000` – `100` | Move shade to a **position**. The value is three digits, zero‑padded. **Important:** The value is inverted relative to the "openness" position — `000` = fully **open**, `100` = fully **closed**. | `!I39m050` → Move to 50% (halfway between open and closed). |
| `r`     | `?`           | Request **position report**. The hub responds with a `r` message containing the current position.                                                                                                  | `!I39r?` → Request position.                                |
| `s`     | (none)        | **Stop** the shade immediately.                                                                                                                                                                    | `!I39s` → Stop movement.                                    |
| `p`     | `Vc?`         | Request **battery and signal report**. The hub responds with a `pVc` message containing voltage and RSSI.                                                                                          | `!I39pVc?` → Request battery/RSSI.                          |
| `L`     | `?`           | **Discover** all shades paired to the hub. This command exists in the ARC protocol but is **not used by the current driver** — the driver uses `!000v?` for discovery instead (see below).         | `!000L?` → (not used by the driver)                         |
| `v`     | `?`           | Request **hub version / global status**. Also used by the driver as a discovery trigger: the hub responds by broadcasting status messages for every shade on the mesh.                             | `!000v?` → Query hub version / trigger discovery.           |

***

\<h2 id="response-messages-hub-to-driver">📨 Response Messages (Hub to Driver)\</h2>

The hub sends back messages that the parent driver parses and forwards to child devices.

| Response Format  | Meaning                                                                                                                                  | Example                                      |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------- |
| `!IDrPPP`        | Position report. `PPP` = 3‑digit inverted position (`000` = open, `100` = closed). The driver converts this to `position` = `100 - PPP`. | `!I39r050` → Shade I39 reported at 50% open. |
| `!IDmPPP`        | Movement confirmation / target position. Same inversion as above.                                                                        | `!I39m050` → Shade I39 moving to 50% open.   |
| `!IDpVcVVVV,RRR` | Battery and signal report. `VVVV` = voltage × 100 (e.g., `1228` = 12.28 V). `RRR` = RSSI (e.g., `R67`).                                  | `!I39pVc01228,R67` → 12.28 V, RSSI ‑67 dBm.  |
| `!IDv...`        | Version / status response. Triggers the parent driver's discovery logic when received for `000`.                                         | `!000v...` → Hub version response.           |

> **Note on discovery:** The parent driver's `configure()` and `ShadeDiscover()` methods send `!000v?`. The hub responds with version information and then broadcasts status messages for each shade on the mesh. Each broadcast is parsed by the parent, which creates or updates the corresponding child device. This is why the driver does not need to use the `L` command.

***

\<h2 id="how-the-driver-uses-these-commands">🔧 How the Driver Uses These Commands\</h2>

The **parent hub driver** translates Hubitat commands into ARC strings. The **child shade drivers** send movement commands and parse responses.

| Hubitat Action                            | ARC Command Sent                                       | Driver Method                                       |
| ----------------------------------------- | ------------------------------------------------------ | --------------------------------------------------- |
| `open()`                                  | `!IDm000`                                              | `open()` (Child)                                    |
| `close()`                                 | `!IDm100`                                              | `close()` (Child)                                   |
| `setPosition(50)`                         | `!IDm050`                                              | `setPosition()` (Child)                             |
| `stop()`                                  | `!IDs`                                                 | `stop()` (Child)                                    |
| `refresh()` (Child)                       | `!IDr?`                                                | `requestStatus()` (Child)                           |
| `requestBatteryStatus()` (Child)          | `!IDpVc?`                                              | `requestBatteryStatus()` (Child)                    |
| `configure()` / `ShadeDiscover` (Parent)  | `!000v?`                                               | `configure()` / `ShadeDiscover()` (Parent)          |
| **RF Jitter** (Child, on command failure) | `!IDr?`                                                | `checkPositionConfirmation()` → `retryWithJitter()` |
| `ShadeAdd(String id)` (Parent)            | _(no ARC command — creates a local child device only)_ | `ShadeAdd()` (Parent)                               |
| `ShadeRemove(String id)` (Parent)         | _(no ARC command — deletes a local child device only)_ | `ShadeRemove()` (Parent)                            |

> **Important:** `ShadeAdd` and `ShadeRemove` are **Hubitat-side management commands only**. They do not transmit any ARC commands to the Pulse 2 Hub — they simply create or delete child devices in Hubitat. The physical shade pairing must already exist in the Rollease app. The `ShadeRemove` command includes a safety check (v3.3.24) that verifies the child's `motorAddress` matches the requested ID before deletion.

> **Note on retries and jitter (v2.5.5):** When the driver sends a movement command (`!IDm...`), it waits for a position report (`!IDr...`) from the hub. The driver accepts a position within `Position Tolerance` (default ±1%) of the target as confirmation. If the shade reports intermediate positions while moving, the confirmation timer is reset (up to 2 times) to give slow shades time to finish. If no confirmation arrives, the driver retries the original command (up to `Command Retry Count`). If all retries fail and `Enable RF Jitter` is on, the driver sends a status request (`!IDr?`) to wake the hub's RF transmitter, then retries once more. These are all driver-level behaviors — the ARC commands themselves are unchanged.

***

\<h2 id="battery-reporting-notes">🔋 Battery Reporting Notes\</h2>

The `pVc` response includes the raw battery voltage, which the driver converts to a percentage. Since **v2.5.4**, the driver uses a calibrated linear mapping:

| Voltage    | Reported Percentage |
| ---------- | ------------------- |
| **12.60V** | 100%                |
| **11.05V** | 50%                 |
| **9.50V**  | 0%                  |

This range was tuned against the Rollease app across eight shades of mixed types (roman and roller, external and embedded batteries). Typical accuracy is within **±5%** of the app.

**Factors that affect accuracy:**

* **Shade type** — roman shades (external batteries) may have a slightly different discharge curve than roller shades (embedded batteries).

* **Battery age and temperature** — affect the voltage reading.

* **Charge settling** — a freshly charged battery reads high for 1–2 days.

* **App algorithm** — the app likely uses a non‑linear curve that cannot be exactly replicated by a linear formula.

The per‑shade **Battery Offset** preference (−30 to +30) allows fine‑tuning if the driver and app differ by more than a few percent.

***

\<h2 id="troubleshooting-arc-communication">🛠️ Troubleshooting ARC Communication\</h2>

* **No response from hub** – Verify the hub's IP address and that port `1487` is reachable. Check the parent driver's `status` attribute.

* **Shade moves but no position update** – The hub may not have sent a `r` response. Run `Refresh` on the child device. If the shade is in **Simple C** mode (not Online), it will not send position reports. Check the Rollease app to confirm the shade is Online.

* **`lastCommand` shows unexpected characters** – The raw command is logged as sent; it may include a newline (`\r\n`) for telnet termination.

* **Battery report missing** – Use the `Request Battery Status` command to force a `pVc?` query.

* **Command retried multiple times** – The driver retries if the hub fails to confirm the target position. Check the child device's `Command Retry Count` and `Command Retry Wait Time` preferences. If all retries fail and `Enable RF Jitter` is on, the driver sends a status request to wake the hub's transmitter and retries once more.

* **Shade stops 1–2% short of target and triggers retries** – Increase the child device's `Position Tolerance` preference (default 1). This is common on roman shades and older motors that physically cannot hit the exact target.

* **Command retries but the shade is clearly moving** – The `Position Tolerance` preference may need to be increased, or the `Command Retry Wait Time` may be too short for the shade's physical travel time. Slow shades report intermediate positions and the driver now resets the confirmation timer for those reports.

* **`Shade Remove` says "SAFETY CHECK FAILED"** – The child's `Motor Address` does not match the ID you requested to remove. This is intentional (v3.3.24) and prevents deleting the wrong device. Update the `Motor Address` preference directly on the child instead.

***

\<h2 id="reference--external-links">🔗 Reference / External Links\</h2>

* 📘 [Hubitat Community Thread for Rollease Acmeda Driver](https://community.hubitat.com/%E2%80%A6)

* 📘 [ARC Protocol (Unofficial) – Home Assistant Integration Notes](https://github.com/%E2%80%A6)

***

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

### **Community & Personal Use**

These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Rollease Acmeda** or **Hubitat Elevation**. All logic refinements were developed through independent analysis to provide local integration where official documentation was unavailable.

### **AI-Assisted Development Notice**

This driver overhaul was a collaborative effort between a human **Project Manager** and **AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases.

* **User Responsibility:** Users should monitor their initial installation to ensure their specific hardware configuration responds as intended.

### **Operational Safety**

* **_Insuo Periculo_:** While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any hardware malfunctions, "ghost" shade movements, or unintended Hubitat behavior.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Do not use `Shade Remove` to swap addresses** – the parent's safety check prevents accidental deletion, but the correct way to change a shade's ARC ID is to update the `Motor Address` preference directly on the child device.

* **Beta Status:** Please note that this fork is currently in **Beta**. While stable in my environment, you may encounter edge cases depending on your specific Pulse 2 Hub firmware version.

***

\<h2 id="revision-history">📜 Revision History\</h2>

| Version   | Date       | Author | Changes                                                                                                                                                                                                                                                                                                                                                    |
| --------- | ---------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.2.0** | 2026-09-16 | DGBQ   | Aligned with Shade v2.5.5. Added note on retries and jitter behaviour to "How the Driver Uses These Commands" section (Position Tolerance, intermediate position reset, retry sequence). Added two new troubleshooting entries: "Shade stops 1–2% short of target" and "Command retries but the shade is clearly moving". No ARC protocol content changed. |
| 1.1.0     | 2026-09-11 | DGBQ   | Aligned with Hub v3.3.24 and Shade v2.5.4. Corrected discovery method (`!000v?`, not `!000L?`). Added clarification on `m` command inversion. Added battery reporting section with 9.5V–12.6V calibration. Documented RF Jitter usage of `!IDr?`. Added `ShadeAdd` / `ShadeRemove` note (Hubitat-only). Added `ShadeRemove` safety check troubleshooting.  |
| 1.00      | 2026-04-12 | DGBQ   | Initial release of **Rollease Acmeda ARC Protocol Reference**.                                                                                                                                                                                                                                                                                             |

***

## 📝 Summary of Updates Applied to RolleaseAcmedaARCProtocolReference.md (v1.1.0 → v1.2.0)

### Version Control

* Bumped **Document Control** from `1.1.0` → **`1.2.0`**.

* Bumped **Shade Driver** from `v2.5.4` → **`v2.5.5`**.

### How the Driver Uses These Commands Section

* Added a **new note** titled "Note on retries and jitter (v2.5.5)" that explains:

  * Position Tolerance is applied to the confirmation check.

  * Intermediate position reports reset the confirmation timer (up to 2 times).

  * Retries fire after the timer expires.

  * Jitter fires if all retries fail.

  * Emphasises that these are driver-level behaviours, not ARC protocol changes.

### Troubleshooting Section

* Added **new entry**: "Shade stops 1–2% short of target and triggers retries" → increase `Position Tolerance`.

* Added **new entry**: "Command retries but the shade is clearly moving" → may need higher tolerance or longer retry wait time; slow shades now reset the timer automatically.

* All existing entries preserved.

### Revision History

* Added **v1.2.0** entry (2026-09-16).

* Updated the existing "1.10" entry to **1.1.0** for version number consistency.

### What Did NOT Change

* ARC Command Structure.

* Supported ARC Commands table.

* Response Messages table.

* Battery Reporting Notes (still aligned with v2.5.4 battery hotfix).

* Reference / External Links.

* Disclaimers.

***

