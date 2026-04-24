# Master Functional Specifications: RETR-Hub-Logic

**File Name:** RETR-Hub-Logic.md (Rollease Engineering Technical Reference Hub Logic)
**Project Branch:** The Surgical Observer  
**Integration Logic:** Parent/Child Architecture with Surgical Error Silencer 
**Document Status:** Final Production Specification

---

## 1. Project Context & Technical Evolution

### 1.1 The "Rollease Shade" Project Genesis
This documentation defines a specialized development branch of the **Rollease Acmeda Pulse 2 Hub** integration. While the foundation rests on the reliable ARC protocol layer established by **Younes (@yannick00000)**, this "Surgical Observer" branch has been architected specifically for high-performance, professional-grade automation environments where log cleanliness and protocol stability are mandatory.

### 1.2 The "Burst-and-Close" Protocol Discovery
The defining breakthrough of this branch was the systematic diagnostic analysis of the Pulse 2 Hub’s native behavior. Using **Raw TCP/IP (PuTTY) monitoring**, we confirmed that the Hub is a **"Burst Communicator"** by design. 

The Hub follows a rigid, non-persistent connection lifecycle:
1.  **Socket Opening:** Triggered immediately upon a command or status request.
2.  **RF Transmission:** The Hub converts the TCP command into an RF signal for the shade motor.
3.  **Status Dump:** The Hub immediately broadcasts a high-speed data burst (Position, Battery, Signal, and Motor ID).
4.  **Forced Termination:** The Hub terminates the socket connection abruptly once the burst is complete.

### 1.3 The Surgical Resolution
Prior community versions often flagged the `Stream is closed` message as a "Socket Error" or "Connection Failure." In the **RETR-Hub-Logic** branch, we have reframed this:
* **The Error Silencer:** We implemented a "Surgical Error Silencer" that intercepts the hang-up event. Instead of a "failure," the driver recognizes this as a **successful completion of a transmission cycle.**
* **The Result:** Users enjoy a 100% reliable "Direct-Fire" communication timing without the constant "Error" or "Warn" spam in the Hubitat logs.

---

## 2. Full Command API (Functional Methods)

These methods represent the actionable interface within the Hubitat UI.

### 2.1 Core System Commands
* **`configure()`**: The primary discovery tool. It transmits the `!000v?` global request to the Hub, forcing it to report every known shade on the mesh. This triggers the automatic creation of child devices.
* **`initialize()`**: The "Reset" button. This method clears any stuck Telnet sockets, resets internal state flags, and prepares a clean handshake for the next burst.
* **`refresh()`**: Included for Hubitat Capability compliance. It polls the Hub for the current status of all child devices to ensure the Dashboard is in sync.
* **`sendTelnetCommand(String msg)`**: The internal engine. This method handles the actual delivery of ARC strings and manages the "Burst-and-Close" lifecycle.
* **`sendPulseCommand(String msg)` / `sendMsg(String msg)`**: Explicit aliases maintained to ensure compatibility with legacy WebCoRE or Rule Machine scripts.

### 2.2 Advanced Management Suite (DGBQ)
The following "Shade-Prefix" commands allow for precision management of the shade mesh without requiring code edits.

* **`ShadeDiscover`**: A programmatic scan that reconciles the Hub's physical motor list with the Hubitat child device list. It ignores existing devices and only spawns new ones.
* **`ShadeAdd(String motorId)`**: Manual entry for a specific motor. It requires a 3-character ID (e.g., `I39`). It includes "Guardrail" logic to ensure the ID is correctly formatted and not a duplicate.
* **`ShadeRemove(String motorId)`**: Deletes a specific child device from the parent and the Hubitat database.
* **`ShadeDeleteAll`**: A safety clearing utility that removes all child devices under the `DGBQ` namespace.

---

## 3. User Preferences & Configuration

These inputs allow the user to tune the driver to their specific network environment.

| Preference | Format | Default | Description |
| :--- | :--- | :--- | :--- |
| **Hub IP Address** | String | (Required) | Static IP of the Pulse 2 Hub on the local network. |
| **Hub Port** | Number | 1487 | The standard ARC protocol port for Pulse 2 communication. |
| **Retry Interval** | Number | 15s | Time between reconnection attempts if the Hub is offline. |
| **Max Idle Time** | Number | 300s | The timeout before the driver closes an inactive socket. |
| **Error Silencer** | Boolean | True | Enables the logic that hides standard "Burst" disconnects from logs. |
| **Debug Logging** | Boolean | False | Enables high-verbosity traces for protocol debugging. |
| **Description Logs**| Boolean | True | Enables user-friendly logs for shade movements and battery alerts. |

---

## 4. Current States (Dashboard Attributes)

This section tracks the real-time telemetry visible to the user and external integrations.

* **`status`**: Reports `Connected` or `Disconnected`. This is filtered by the Silencer to reflect actual Hub availability rather than transient socket closes.
* **`shades`**: A dynamic string listing every active Motor ID managed by the Hub (e.g., `A01, I39, C12`).
* **`lastAction`**: A human-readable record of the most recent activity (e.g., `Moving Shade A01 to 50%`).
* **`lastCommand`**: Displays the raw ARC string sent to the Hub for technical verification.
* **`lastUpdate`**: A timestamp indicating the last time a successful status burst was received.

---

## 5. Internal Logic & State Variables

This section defines the background memory and initialization logic used to maintain driver stability.

* **`shadeList`**: The master internal list used to track motor IDs. This ensures `ShadeAdd` and `ShadeDiscover` never create duplicate child devices.
* **`connStatus`**: A binary flag (Open/Closed) that prevents "Socket Collisions" during rapid automation bursts.
* **`isInitializing`**: A temporary semaphore used during the boot-up sequence to prevent overlapping initialization attempts.
* **Attribute Seeding**: Upon installation or initialization, the driver ensures critical attributes are populated with baseline values to prevent null-errors in Dashboards and Alexa integrations.

---

## 6. Logic Guardrails & Fail-Safes

* **Input Sanitization:** The driver forces all Motor IDs to Uppercase and validates that they are exactly 3 alphanumeric characters before transmitting.
* **Namespace Isolation:** All child devices are created within the `DGBQ` namespace, preventing collisions with other Rollease drivers.
* **Burst Integrity:** All manual commands are automatically zero-padded (e.g., `1` becomes `001`) to comply with the Hub's strict protocol formatting.

---

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)
* **Lineage:** Optimized fork of the Younes Oughla (arcautomate) original branch.
* **Version:** 2.2.0 (The Surgical Observer Branch)