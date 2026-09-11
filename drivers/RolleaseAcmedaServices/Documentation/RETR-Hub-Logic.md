# Master Functional Specifications: RETR-Hub-Logic

**File Name:** RETR-Hub-Logic.md (Rollease Engineering Technical Reference Hub Logic)\
**Project Branch:** The Surgical Observer\
**Integration Logic:** Parent/Child Architecture with Surgical Error Silencer\
**Document Status:** Final Production Specification\
**Driver Version:** 3.3.24

***

## 1. Project Context & Technical Evolution

### 1.1 The "Rollease Shade" Project Genesis

This documentation defines a specialized development branch of the **Rollease Acmeda Pulse 2 Hub** integration. While the foundation rests on the reliable ARC protocol layer established by **Younes (@yannick00000)**, this "Surgical Observer" branch has been architected specifically for high-performance, professional-grade automation environments where log cleanliness and protocol stability are mandatory.

### 1.2 The "Burst-and-Close" Protocol Discovery

The defining breakthrough of this branch was the systematic diagnostic analysis of the Pulse 2 Hub's native behavior. Using **Raw TCP/IP (PuTTY) monitoring**, we confirmed that the Hub is a **"Burst Communicator"** by design.

The Hub follows a rigid, non-persistent connection lifecycle:

1. **Socket Opening:** Triggered immediately upon a command or status request.

2. **RF Transmission:** The Hub converts the TCP command into an RF signal for the shade motor.

3. **Status Dump:** The Hub immediately broadcasts a high-speed data burst (Position, Battery, Signal, and Motor ID).

4. **Forced Termination:** The Hub terminates the socket connection abruptly once the burst is complete.

### 1.3 The Surgical Resolution

Prior community versions often flagged the `Stream is closed` message as a "Socket Error" or "Connection Failure." In the **RETR-Hub-Logic** branch, we have reframed this:

* **The Error Silencer:** We implemented a "Surgical Error Silencer" that intercepts the hang-up event. Instead of a "failure," the driver recognizes this as a **successful completion of a transmission cycle.**

* **The Result:** Users enjoy a 100% reliable "Direct-Fire" communication timing without the constant "Error" or "Warn" spam in the Hubitat logs.

### 1.4 Phase 1 Critical Bug Fixes (v3.3.24)

An incident during production use revealed a fundamental design flaw in how the parent driver managed child device address mapping. When a shade's ARC ID changed (e.g., `6R0` → `XDG`) and the parent's internal mapping became stale, calling `ShadeRemove` could delete the **wrong** child device. This caused the unexpected loss of a working shade.

Phase 1 addresses three root causes:

* **`ShadeAdd()`:** New child devices are now initialized immediately after creation via `child.initialize()`. This ensures the child's `motorAddress` preference is set correctly from the outset, eliminating the "default 000" bug.

* **`parse()`:** When a new child is created from an incoming hub message, it is now initialized. When an existing child is detected with a mismatched `motorAddress`, the parent auto-corrects the preference to match the actual address the hub is reporting. This keeps parent and child state synchronized at all times.

* **`ShadeRemove()`:** A safety check now compares the target child's `motorAddress` preference against the requested `motorId`. If they do not match (or the address is unset), deletion is **aborted** with a warning. This prevents accidental removal of the wrong device.

***

## 2. Full Command API (Functional Methods)

These methods represent the actionable interface within the Hubitat UI.

### 2.1 Core System Commands

* **`configure()`**: The primary discovery tool. It transmits the `!000v?` global request to the Hub, forcing it to report every known shade on the mesh. This triggers the automatic creation of child devices.

* **`initialize()`**: The "Reset" button. This method clears any stuck Telnet sockets, resets internal state flags, updates the `connectionState` attribute, and prepares a clean handshake for the next burst. It also automatically refreshes all child shades after reconnection.

* **`refresh()`**: Currently a **no-op** on the Parent driver. It is included for Hubitat Capability compliance but performs no action. To poll children for status, use `initialize()` (which auto-refreshes children) or call `refresh()` directly on each child device.

* **`sendTelnetCommand(String msg)`**: The internal engine. This method handles the actual delivery of ARC strings and manages the "Burst-and-Close" lifecycle.

> **Note:** The aliases `sendPulseCommand` and `sendMsg` were removed in v3.3.11 to reduce UI clutter. Users should use `sendTelnetCommand` for raw ARC transmission.

### 2.2 Advanced Management Suite (DGBQ)

The following "Shade-Prefix" commands allow for precision management of the shade mesh without requiring code edits.

* **`ShadeDiscover`**: A programmatic scan that reconciles the Hub's physical motor list with the Hubitat child device list. It ignores existing devices and only spawns new ones.

* **`ShadeAdd(String motorId)`**: Manual entry for a specific motor. Requires a 3-character ID (e.g., `I39`). Includes "Guardrail" logic to ensure the ID is correctly formatted and not a duplicate. As of v3.3.24, the new child is initialized immediately and its `motorAddress` preference is set.

* **`ShadeRemove(String motorId)`**: Deletes a specific child device from the parent and the Hubitat database. As of v3.3.24, a **safety check** verifies the child's `motorAddress` matches the requested ID before deletion. If mismatched, deletion is aborted.

* **`ShadeDeleteAll`**: A safety clearing utility that removes all child devices under the `DGBQ` namespace. Note: this bypasses the per-device safety check and deletes all children unconditionally.

***

## 3. User Preferences & Configuration

These inputs allow the user to tune the driver to their specific network environment.

| Preference                     | Format  | Default    | Description                                                                                                                    |
| ------------------------------ | ------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------ |
| **Hub Address**                | String  | (Required) | Static IP of the Pulse 2 Hub on the local network.                                                                             |
| **Hub Port**                   | String  | 1487       | The standard ARC protocol port for Pulse 2 communication. Stored as text to avoid comma formatting.                            |
| **Connection Retry Interval**  | Number  | 300s       | Time between reconnection attempts if the Hub is offline. Set to 0 to disable.                                                 |
| **Maximum Idle Time**          | Number  | 3600s      | The timeout before the driver closes an inactive socket. Set to 0 to disable.                                                  |
| **Enable Error Silencer**      | Boolean | True       | Enables the logic that hides standard "Burst" disconnects from logs.                                                           |
| **Enable Debug Logging**       | Boolean | False      | Enables high-verbosity traces for protocol debugging. Auto-off after 30 minutes if `Auto-Revert Debug` is enabled.             |
| **Keep Debug Logging On**      | Boolean | False      | When enabled, debug logging will NOT auto-disable after 30 minutes.                                                            |
| **Auto-Revert Debug**          | Boolean | True       | When enabled, debug logging automatically turns off after 30 minutes. When disabled, debug stays on until manually turned off. |
| **Enable Description Logging** | Boolean | True       | Enables user-friendly logs for shade movements and battery alerts.                                                             |

> **Planned changes (Phase 2 & 3):** Phase 2 will replace the three logging preferences with a single **Log Verbosity** dropdown (`ERROR`, `WARN`, `INFO`, `DEBUG`). Phase 3 will replace `Connection Retry Interval` and `Maximum Idle Time` with a Health Check mechanism (`Health Check Interval`, `Health Check Max Retries`, `Health Check Retry Delay`) that includes auto-recovery.

***

## 4. Current States (Dashboard Attributes)

This section tracks the real-time telemetry visible to the user and external integrations.

* **`status`**: Reports `online`, `idle`, or `offline`. This is filtered by the Silencer to reflect actual Hub availability rather than transient socket closes.

* **`connectionState`**: Provides a granular connection status for advanced automation and webCoRE integration. Possible values:

  * `connected` – Telnet connection is active and healthy.

  * `connecting` – Driver is attempting to establish a connection.

  * `idle` – The Hub completed a burst and disconnected normally (Silencer path).

  * `error` – A critical error occurred (e.g., `connect timed out`).

* **`lastConnectionChange`**: Timestamp of the last time `connectionState` changed. Helps identify when a failure occurred.

* **`shades`**: A dynamic string listing every active Motor ID managed by the Hub (e.g., `A01, I39, C12`).

* **`lastAction`**: A human-readable record of the most recent activity (e.g., `Moving Shade A01 to 50%`).

* **`lastCommand`**: Displays the raw ARC string sent to the Hub for technical verification.

* **`lastUpdate`**: A timestamp indicating the last time a successful status burst was received.

***

## 5. Internal Logic & State Variables

This section defines the background memory and initialization logic used to maintain driver stability.

* **`shadeList`**: The master internal list used to track motor IDs. This ensures `ShadeAdd` and `ShadeDiscover` never create duplicate child devices.

* **`state.shadeList`**: Persistent storage of the shade list across reboots.

* **Attribute Seeding**: Upon installation or initialization, the driver ensures critical attributes are populated with baseline values to prevent null-errors in Dashboards and Alexa integrations.

* **Auto Child Refresh**: After every `initialize()`, the driver automatically calls `refresh()` on every child shade. This eliminates the need to manually refresh each shade after a power cycle or network glitch.

* **Connection State Updates**: The `connectionState` attribute is set as follows:

  * `"connecting"` – when `initialize()` starts.

  * `"connected"` – when `telnetConnect()` succeeds.

  * `"idle"` – when `telnetStatus()` receives `Stream is closed` or `receive error` (and the Silencer is enabled).

  * `"error"` – when a `SocketTimeoutException` or other critical error occurs.

* **`lastConnectionChange`**: Updated every time `connectionState` changes, with a human-readable timestamp.

* **Address Reconciliation (v3.3.24)**: During `parse()`, if an existing child's `motorAddress` preference does not match the incoming address from the hub, the parent silently updates the child's preference. This keeps parent and child state synchronized and prevents mismatches that could lead to incorrect `ShadeRemove` behaviour.

* **New Child Initialization (v3.3.24)**: Whenever the parent creates a child (via `ShadeAdd` or automatically in `parse()`), it calls `child.initialize()` immediately. This ensures the child seeds its default attributes and logs its motor address before the first message is parsed.

***

## 6. Logic Guardrails & Fail-Safes

* **Input Sanitization:** The driver forces all Motor IDs to Uppercase and validates that they are exactly 3 alphanumeric characters before transmitting.

* **Namespace Isolation:** All child devices are created within the `DGBQ` namespace, preventing collisions with other Rollease drivers.

* **Burst Integrity:** All manual commands are automatically zero-padded (e.g., `1` becomes `001`) to comply with the Hub's strict protocol formatting.

* **ShadeRemove Safety Check (v3.3.24):** Before deleting a child device, the driver compares the child's `motorAddress` preference against the requested `motorId`. If they do not match (or the address is unset), deletion is aborted with a warning. This prevents accidental deletion of the wrong device — a bug that previously caused the loss of a working shade.

* **External Recovery Support:** The `connectionState` attribute provides a reliable trigger for webCoRE pistons to perform event-driven recovery actions (e.g., power-cycling the hub). The `lastConnectionChange` attribute provides a timestamp for auditing failures.

> **Critical operational note:** Do **not** use `ShadeRemove` as a means of swapping addresses between devices. To change a shade's ARC ID, update the `motorAddress` preference directly on the child device. To update automations that reference the device, use the Hubitat **Swap Apps Device** tool (noting that this tool does not support child devices, so webCoRE pistons must be updated manually). See the QuickStart guide for details.

***

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)

* **Lineage:** Optimized fork of the Younes Oughla (arcautomate) original branch.

* **Current Driver Version:** 3.3.24

* **Key Enhancements (v3.3.23 – v3.3.24):**

  * Added `connectionState` attribute for granular health monitoring.

  * Added `lastConnectionChange` attribute for failure timestamp auditing.

  * Auto child refresh after `Initialize`.

  * **Phase 1 (v3.3.24):** Child address validation in `parse()`, immediate child initialization in `ShadeAdd` and `parse()`, safety check in `ShadeRemove()`.

* **Planned Enhancements (Phase 2 & 3):**

  * Phase 2 – Logging UX Overhaul: unified Log Verbosity dropdown.

  * Phase 3 – Health Check & Auto-Recovery: proactive connection monitoring with exponential backoff.

***

