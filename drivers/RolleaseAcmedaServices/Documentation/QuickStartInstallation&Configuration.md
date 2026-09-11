# 🏠 Rollease Acmeda Hubitat Integration

### _Quick Start, Installation & Configuration_

## 🔢 Version Control

**Document Control:** 1.1.0\
**Current Releases:** Beta

* **Hub Driver:** `RolleaseAcmedaHub-DGBQ.groovy` (v3.3.24)

* **Shade Driver:** `RolleaseAcmedaShade_DGBQ.groovy` (v2.5.4)\
  **Maintenance Lead:** David Ball-Quenneville (DGBQ)\
  **Original Developer:** Younes Oughla (Yoonoo)

## 📑 Table of Contents

\<details> \<summary>Click to expand Table of Contents\</summary>

* 🧭Introduction & Overview

* 🚀Quick Start & Installation

* ⚙️Parent Hub: Command & Configuration

* 🪟Shade (Child): Command & Configuration

* 🔋Battery Reporting & Calibration

* 🛠️Support & Enhancements

* 🛡️Disclaimers

* 📜Revision History

\</details>

***

\<h2 id="introduction">🧭 Introduction & Overview\</h2>

Welcome to the maintained branch of the Rollease Acmeda Hubitat integration. This project was born out of a need for "set-and-forget" reliability in a modern smart home environment.

While the original drivers by **Younes Oughla** provided a vital foundation, hardware evolution and Hubitat environment changes necessitated a "Surgical" overhaul. Acting as the **Project Manager**, I have utilized **AI** to perform deep-dive code audits, resolving long-standing logic-casting conflicts and implementing advanced telemetry (Battery/RSSI) that wasn't previously available.

This version is optimized for the **Automate Pulse 2 Hub** and aims to provide a transparent, robust interface for your shades, ensuring they respond as intended—every single time.

**Current release highlights:**

* **Confirmation‑based command retry** – commands are retried only if the Hub fails to confirm the target position.

* **RF Jitter workaround** – wakes the Hub's RF transmitter if all retries fail.

* **Recalibrated battery reporting** – tuned to match the Rollease app within ±5%.

* **Child device safety checks** – prevents accidental deletion of the wrong shade.

***

\<h2 id="installation">🚀 Quick Start, Installation & Configuration\</h2>

Following this "Surgical" setup guide will ensure your Pulse 2 Hub communicates reliably with Hubitat.

### **1. Install the Driver Code**

1. Log into your **Hubitat Elevation Web Interface**.

2. Navigate to **Drivers Code** in the sidebar.

3. Click **+ New Driver** and paste the contents of `RolleaseAcmedaHub-DGBQ.groovy`. Click **Save**.

4. Repeat the process for `RolleaseAcmedaShade-DGBQ.groovy`.

   * **Note:** You do not need to manually create devices for the shades; the Parent driver will handle this dynamically.

### **2. Create the Parent Device**

1. Navigate to **Devices** > **Add Device** > **Virtual**.

2. **Device Name:** Use a recognizable name (e.g., "Rollease Pulse Hub").

3. **Type:** Search for and select **Rollease Acmeda Hub - DGBQ**.

4. Click **Save**.

### **3. Network Configuration**

1. Open your newly created "Rollease Pulse Hub" device.

2. Under **Preferences**, enter the **IP Address** of your Pulse 2 Hub.

   * **Pro Tip:** Set a **DHCP Reservation** in your router for the Pulse 2 Hub to ensure the IP never changes, preventing future communication drops.

3. Click **Save Preferences**.

4. Check the **Logs** tab; you should see a message confirming a successful Telnet connection.

### **4. Shade Discovery**

1. On the Parent Hub device page, click the **`Shade Discover`** button.

2. The driver will perform a "White-Hat" query of your Hub to identify all registered motors.

3. Navigate back to your **Devices** list. Your shades will now appear as Child devices, ready for use.

***

\<h2 id="parent-reference">⚙️ Parent Hub: Command & Configuration Reference\</h2>

### **Rollease Acmeda Hub (Parent)**

The **Parent Hub** driver is the "Command and Control" center of this integration. Because Hubitat cannot communicate with every motor individually, this driver maintains a single, persistent **Telnet Socket** to the physical Pulse 2 hardware. It acts as a protocol translator, converting Hubitat instructions into ARC-compliant strings. It also monitors connection health and manages the lifecycle of all "Child" shade devices.

***

### **Commands**

Commands are manual actions triggered within the Hubitat interface to execute specific driver logic or hardware instructions.

| Command                 | Description                                                                                                                                       | Default / Example                                             | Caution                                                            |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ------------------------------------------------------------------ |
| **`Configure`**         | Re-initializes internal driver settings and commits preferences to the Hubitat database.                                                          | Run after changing any Preference.                            | None.                                                              |
| **`Initialize`**        | **The Reset Switch.** Forcibly terminates the current Telnet socket, attempts a clean reconnection, and automatically refreshes all child shades. | Use if `status` is `Disconnected` or after a hub power cycle. | Wait 15s between clicks to avoid network buffer hangs.             |
| **`Refresh`**           | Currently a no‑op on the Parent driver. Included for Hubitat capability compliance.                                                               | —                                                             | None.                                                              |
| **`sendTelnetCommand`** | Sends raw ARC protocol strings directly to the Hub.                                                                                               | `!001m050` (Move Shade 001 to 50%).                           | **Advanced only.** For advanced troubleshooting only. Standard users should never need this command. Use the Child device's controls for normal operation.Malformed strings can crash the Hub's listener. |
| **`Shade Add`**         | Manually recreates a Child Shade device when the Parent Hub knows about a shade but Hubitat does not.             | Example: Enter the exact ID from the Rollease app (e.g., I39, BSG, XDG).                                | Caution: The ID must match the Rollease app exactly. It is assigned by the Pulse 2 Hub at pairing time – you cannot choose it.                |
| **`Shade Discover`**    | **The Auto‑Scanner.** Queries the Hub for all motors and spawns any missing Child devices.                                                        | The primary setup tool for new installs.                      | Process may take up to 30 seconds or more.                                 |
| **`Shade Remove`**      | Deletes a specific Child Shade device from Hubitat by its ID. **A safety check now verifies the child's `Motor Address` before deleting.**        | Enter `002` to remove Shade 2.                                | Permanent; removes device from all Rules.                          |
| **`Shade Delete All`**  | **The Nuclear Option.** Purges every associated Child Shade device from Hubitat.                                                                  | Use for a complete system reset.                              | **High Risk:** Breaks all Dashboards and Rules.                    |

> **Note:** The aliases `sendMsg` and `sendPulseCommand` were removed in v3.3.11. Use `sendTelnetCommand` for all raw ARC transmission.

> **Important:** `Shade Remove` now includes a safety check (v3.3.24). If the child's `Motor Address` preference does not match the requested ID, deletion is aborted. This prevents the accidental removal of the wrong device — a bug that previously caused an unintended loss of a working shade. If you need to swap addresses between devices, do **not** use `Shade Remove`. Update the `Motor Address` preference directly on the child device and use the Hubitat **Swap Apps Device** tool for automations.

> **When to use `Shade Add`:** This command is for **recovery**, not routine setup. Normal setup uses `Shade Discover`, which scans the Hub and creates all child devices automatically. Use `Shade Add` only when a shade is known to the Pulse 2 Hub (visible in the Rollease app) but is missing from Hubitat.


***

### **Preferences**

Preferences define the communication parameters and logging behavior of the driver.

| Preference                     | Detail                                                                                                           | Requirement   | Default                |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------- | ------------- | ---------------------- |
| **Hub Address\***              | The static local IP of your Pulse 2 Hub.                                                                         | **Mandatory** | None                   |
| **Hub Port\***                 | The ARC Protocol port for the Pulse 2.                                                                           | **Mandatory** | **1487**               |
| **Connection Retry Interval**  | Seconds the driver waits before attempting to reconnect after a drop.                                            | Optional      | **300**                |
| **Maximum Idle Time**          | The window of silence allowed before the driver resets the socket.                                               | Optional      | **3600**               |
| **Enable Error Silencer**      | Suppresses the benign `Stream is closed` warnings native to the Hub.                                             | Optional      | **Enabled**            |
| **Enable Debug Logging**       | Outputs high-detail technical data to the system logs.                                                           | Optional      | **Off** (Auto-off 30m) |
| **Keep Debug Logging On**      | When enabled, debug logging will NOT auto-disable after 30 minutes.                                              | Optional      | **Off**                |
| **Auto-Revert Debug**          | When enabled, debug logging turns off after 30 minutes. When disabled, debug stays on until manually turned off. | Optional      | **On**                 |
| **Enable Description Logging** | Logs human-readable events (e.g., "Shade 1 is Opening").                                                         | Optional      | **On**                 |

> **Note:** Phase 2 (Logging UX) will replace `Enable Debug Logging`, `Keep Debug Logging On`, and `Enable Description Logging` with a single **Log Verbosity** dropdown. Phase 3 (Health Check) will replace `Connection Retry Interval` and `Maximum Idle Time` with Health Check preferences. These changes are planned but not yet released.

***

### **Current States**

Current States provide real-time telemetry regarding the Hub's health and activity.

* **`status`**: The primary health indicator. Reports **online**, **idle**, or **offline**.

* **`connectionState`**: Granular connection status for advanced automation. Values: `connected`, `connecting`, `idle`, `error`. Ideal for webCoRE event‑driven recovery.

* **`lastConnectionChange`**: Timestamp of the last time `connectionState` changed. Helps identify when a failure occurred.

* **`lastAction`**: A summary of the most recent internal driver event.

* **`lastCommand`**: Displays the raw ARC command sent to the Hub.

* **`lastUpdate`**: A high-precision timestamp of the last successful data burst from the Hub.

* **`shades`**: A dynamic list of the motor IDs currently recognized by the Parent bridge.

***

### **State Variables**

Internal memory points used for driver logic.

* **`shadeList`**: A technical map correlating 3-digit motor IDs to Hubitat Device Network IDs (DNI).

***

### **Troubleshooting (Parent Level)**

1. **Status is "offline":** Confirm the **IP Address** and ensure **Hub Port** is set to `1487`. Click `Initialize`.

2. **Shades Missing during Discovery:** Verify shades are active in the official Rollease Mobile App before running `Shade Discover`.

3. **Log Noise:** Ensure **Enable Error Silencer** is `Enabled` to filter out non-critical Hub communication spikes.

4. **Pulse 2 Hub was unplugged or lost power:** After the hub comes back online, click **`Initialize`** on the parent device. The driver will automatically refresh all child shades.

5. **Using `connectionState` for automation:** The `connectionState` attribute can be used in webCoRE to trigger automated recovery (e.g., power‑cycling the hub) when it changes to `disconnected` or `error`. The `lastConnectionChange` attribute tells you exactly when the failure occurred.

6. **`Shade Remove` says "SAFETY CHECK FAILED":** The child's `Motor Address` does not match the requested ID. This is intentional — it prevents deleting the wrong device. To change a shade's address, update the `Motor Address` preference directly on the child device instead.

***

\<h2 id="shade-reference">🪟 Shade (Child): Command & Configuration Reference\</h2>

### **Rollease Acmeda Shade (Child)**

The **Shade (Child)** driver represents the individual motor head in your window. While the Parent handles the Telnet bridge to the Hub, the Child driver is the operational interface for daily use. This driver implements multiple Hubitat capabilities—including **WindowShade**, **Switch Level**, and **Battery**—to ensure maximum compatibility with voice assistants (Alexa/Google Home) and automation engines. Each shade is uniquely identified by the 3-digit **Motor Address** assigned by the Pulse 2 Hub.

***

### **Commands**

Commands are manual actions triggered within the Hubitat interface to control the physical movement or state of the motor.

| Command                      | Description                                                      | Notes                                                                   |
| ---------------------------- | ---------------------------------------------------------------- | ----------------------------------------------------------------------- |
| **`Open` / `Close`**         | Initiates standard movement to fully open (100%) or closed (0%). | Works as expected.                                                      |
| **`Stop`**                   | Immediately halts motor rotation at its current location.        | Critical "Emergency Stop".                                              |
| **`Set Position`**           | Moves shade to a specific percentage (0–100).                    | Fully functional.                                                       |
| **`Start Position Change`**  | Begins continuous movement (`open`/`close`).                     | **Not supported by ARC.** The Rollease protocol only supports "move to position X" – there is no continuous movement mode. This stub exists only to satisfy Hubitat's `Window Shade` capability and logs an informational message.                  |
| **`Stop Position Change`**   | Ceases movement initiated by `Start Position Change`.            | **Not supported by ARC.** The Rollease protocol only supports "move to position X" – there is no continuous movement mode. This stub exists only to satisfy Hubitat's `Window Shade` capability and logs an informational message.                  |
| **`Set Level`**              | Accepts `level` (0–100) and `duration` (seconds).                | The `duration` parameter is ignored (logs a warning).                   |
| **`Initialize`**             | Re-syncs the Child device with the Parent Hub.                   | Use after Parent IP changes.                                            |
| **`Refresh`**                | Forces a targeted poll for position only (`!IDr?`).              | Updates `position` and `windowShade`.                                   |
| **`Toggle`**                 | Reverses the current state (open ↔ close).                       | Useful for single-button remotes.                                       |
| **`Request Battery Status`** | Polls the Hub for battery level, voltage, and RSSI.              | Immediately updates `battery`, `batteryVoltage`, `voltage`, and `rssi`. |

> **Note:** The `On`/`Off` buttons are no longer visible in the UI (v2.4.2) but remain fully functional for Alexa and cloud integrations. Alexa uses the native `Window Shade` and `Switch Level` capabilities directly.

> **Retry Behaviour:** When you issue a command, the driver immediately updates the UI (proactive state) and then waits for the Hub to confirm the shade's new position. If no confirmation arrives within `Command Retry Wait Time`, the driver retries (up to `Command Retry Count`). If all retries fail and **RF Jitter** is enabled, the driver sends a harmless status request to wake the Hub's RF transmitter and retries once more.

***

### **Preferences**

Preferences define the identification, retry, and logging behavior for the individual motor.

| Preference                     | Detail                                                                                                                | Requirement   | Default                |
| ------------------------------ | --------------------------------------------------------------------------------------------------------------------- | ------------- | ---------------------- |
| **Motor Address\***            | The unique 3-digit ARC identifier (e.g., `001`) for this specific shade.                                              | **Mandatory** | None                   |
| **Battery Offset**             | Adjusts reported battery percentage by a fixed amount (−30 to +30). See Battery Reporting & Calibration for guidance. | Optional      | **0**                  |
| **Command Retry Count**        | Number of retries if a command fails to receive confirmation. Set to `0` to disable retries.                          | Optional      | **2**                  |
| **Command Retry Wait Time**    | Seconds to wait before retrying a command. A longer wait gives the shade time to respond.                             | Optional      | **10**                 |
| **Enable RF Jitter**           | If a command fails, send a harmless status request to wake the Hub's RF transmitter, then retry once more.            | Optional      | **On**                 |
| **Enable Description Logging** | Logs human-readable events (e.g., "Bedroom Shade was Closed").                                                        | Optional      | **On**                 |
| **Enable Debug Logging**       | Outputs high-detail technical tracing to the system logs.                                                             | Optional      | **Off** (Auto-off 30m) |
| **Auto-Revert Debug**          | When enabled, debug logging turns off after 30 minutes. When disabled, debug stays on until manually turned off.      | Optional      | **On**                 |

> **Note:** Phase 2 (Logging UX) will replace `Enable Debug Logging` and `Enable Description Logging` with a single **Log Verbosity** dropdown. This change is planned but not yet released.

***

### **Current States**

Current States provide the live telemetry and operational attributes used for Dashboards, Alexa, and Rule Machine triggers.

* **`battery`**: The calculated charge percentage (0–100%). Calibrated to match the Rollease app within ±5% (see below).

* **`batteryVoltage`**: The decimal voltage reading (e.g., `12.04V`), used for precise power monitoring. The driver uses a **9.5V to 12.6V** discharge curve.

* **`closed`**: Binary state indicating if shade has reached its lower limit.

* **`level`**: Numeric position (0–100) for "SwitchLevel" compatibility.

* **`moving`**: Indicates if the motor is currently in active travel.

* **`open`**: Binary state indicating if shade has reached its upper limit.

* **`position`**: The primary vertical location attribute (0–100).

* **`rssi`**: Received Signal Strength Indicator (RF link quality).

* **`voltage`**: Raw voltage integer value (e.g., `1204` = 12.04V).

* **`windowShade`**: Operational status: `open`, `closed`, `opening`, `closing`, or `partially open`.

***

### **State Variables**

Internal memory points used by the driver logic.

* **`lastDirection`**: Tracks the trajectory of the motor's most recent movement (`opening` or `closing`).

* **`lastLoggedPosition`**: Prevents duplicate position confirmation logs.

* **`pendingTarget`**: The position currently awaiting confirmation (for retry logic).

* **`retryCount`**: Number of retries attempted for the current command.

* **`confirmed`**: Boolean set when the Hub confirms the target position.

* **`jitterUsed`**: Tracks whether RF Jitter has been sent for the current command.

* **`currentCommand`**: The ARC string last sent (used for retries).

***

### **Troubleshooting (Child Level)**

1. **"Device Not Responding" in Alexa:** The `On`/`Off` buttons are hidden in the UI but fully functional. Voice commands will work as expected.

2. **RSSI is "0" or Missing:** The Hub has not yet pushed a signal update. Click **`Refresh`** on the Child device to force a telemetry pull.

3. **Shade Moves the Wrong Way:** Verify your open/close limits are set correctly in the native Rollease App before integrating with Hubitat.

4. **Non‑functional commands (`Start Position Change`, `Stop Position Change`, `Set Level` duration):** These are stubs that log an informational message. They do not affect normal operation and may be supported in a future update.

5. **Position not confirmed after retries:** The shade moved but the Hub did not send a position report. Check the Rollease app to confirm the shade is in **Online** mode (not Simple C). If the position is correct in the app, the shade is working — the Hub simply did not forward the report. This can happen occasionally and does not affect functionality.

6. **Battery percentage doesn't match the Rollease app:** See Battery Reporting & Calibration below. Small differences (±5%) are normal. You can fine-tune with the **Battery Offset** preference.

***

\<h2 id="battery-reporting">🔋 Battery Reporting & Calibration\</h2>

### **How Battery Reporting Works**

The driver reads the raw battery voltage from the Hub (e.g., `12.04V`) and converts it to a percentage using a calibrated formula. As of **v2.5.4**, the formula uses a **9.5V–12.6V** discharge curve. This was tuned against eight shades across multiple types and brought the driver's readings to within **±5%** of the Rollease app.

### **Why the Driver and App Sometimes Differ**

Battery percentage is an **approximation**. Several factors can cause small differences between the driver and the app:

| Factor              | Impact                                                                                                                                     |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **Shade type**      | Roman shades (often with external batteries) may have slightly different discharge curves than roller shades (usually embedded batteries). |
| **Battery age**     | Older batteries hold voltage differently than new ones.                                                                                    |
| **Temperature**     | Voltage readings drift with temperature.                                                                                                   |
| **Charge settling** | A freshly charged battery reads higher until it settles (typically 1–2 days).                                                              |
| **App algorithm**   | The Rollease app likely uses a more sophisticated (non‑linear) curve or coulomb counting, which a linear formula cannot replicate exactly. |

### **When and How to Use Battery Offset**

The **Battery Offset (%)** preference allows you to fine-tune each shade's reported percentage by up to ±30%. Use it when:

| Situation                                               | Action                                |
| ------------------------------------------------------- | ------------------------------------- |
| Driver reads **higher** than the app (e.g., 66% vs 61%) | Set a **negative offset** (e.g., −5). |
| Driver reads **lower** than the app (e.g., 71% vs 76%)  | Set a **positive offset** (e.g., +5). |
| Driver matches the app                                  | Leave at **0**.                       |

**Recommended workflow:**

1. Run **`Request Battery Status`** on the shade.

2. Compare the driver's `battery` attribute to the Rollease app.

3. If the difference is more than a few percent, adjust the offset by the difference.

4. Run **`Request Battery Status`** again to confirm.

### **Observed Behaviour by Shade Type**

From testing eight shades (both roman and roller, external and embedded batteries):

* Most shades read **slightly lower** than the app (conservative — safer for the user).

* Some roman shades with external batteries read **slightly higher** than the app.

* The average error is approximately **±4%**.

If you have a mix of shade types, you may want to adjust offsets individually. This is expected and normal.

***

\<h2 id="support-and-enhancements">🛠️ Support & Enhancements\</h2>

### **A "Helpful Peer" Approach to Support**

I want to be clear and polite: **I am a Project Manager, not a professional Groovy developer.** I have spent considerable time refining these drivers using AI to ensure they work perfectly in my own home. Because I am not a coder by trade, I cannot provide traditional technical support or "hotfixes" for unique environment issues, but feel free to contact me (see "How to Contact Me" below).

**May I suggest how to get help:**\
If you run into an error, I highly recommend doing what I do!

1. Copy the error from your Hubitat logs.

2. Copy the relevant section of the driver code.

3. Paste both into an AI (like Gemini or ChatGPT) and ask: _"How do I fix this in Hubitat?"_ It is a fantastic way to learn and solve problems in real-time! It's how I built this!

### **Enhancement Requests**

I am always open to making this integration better. If you have an idea for an enhancement that would benefit a broad range of users (and specifically if it improves it for my own environment!), I am happy to take a look.

However, if your request doesn't quite fit my specific setup, please don't take it personally! You are more than welcome to do exactly what I did: **Branch the code and make the changes yourself.**

### **How to Contact Me**

Please reach out via **GitHub only** and complete a ticket. I do not check the Hubitat community forums often. I try to check my GitHub notifications once or twice a week, so please be patient—I'm a nice guy, just a busy one! I'll get back to you when I can.

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

* **Child Device Removal:** Do **not** use the `Shade Remove` command as a means of swapping addresses between devices. Use the Hubitat **Swap Apps Device** tool for automations, and update the `Motor Address` preference directly on the child device to perform an address change.

* **Beta Status:** Please note that this fork is currently in **Beta**. While stable in my environment, you may encounter edge cases depending on your specific Pulse 2 Hub firmware version.

***

\<h2 id="revision-history">📜 Revision History\</h2>

| Version   | Date       | Changes                                                                                                                                                                                                                                                                                                                                                                                      |
| --------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.1.0** | 2026-09-11 | Aligned with Hub v3.3.24 and Shade v2.5.4. Added confirmation‑based retry, RF jitter, and new child preferences. Updated battery reporting section (9.5V–12.6V curve, ±5% accuracy, Battery Offset guidance, shade type differences). Documented Phase 1 bug fixes (child address validation, safe `ShadeRemove`). Added `Shade Remove` safety note. Added upcoming Phase 2 / Phase 3 notes. |
| 1.0.8     | 2026-07-10 | Updated Shade Driver to v2.4.2 (removed explicit on/off commands; cleaner UI).                                                                                                                                                                                                                                                                                                               |
| 1.0.7     | 2026-07-10 | Updated Hub Driver to v3.3.15. Added `lastConnectionChange` attribute. Reordered preferences for better UX.                                                                                                                                                                                                                                                                                  |
| 1.0.6     | 2026-07-10 | Updated driver versions to v3.3.14 (Hub) and v2.4.0 (Shade). Added `Auto‑Refresh Interval` preference and `connectionState` attribute.                                                                                                                                                                                                                                                       |
| 1.0.5     | 2026-04-25 | Added troubleshooting step for Pulse 2 Hub power loss.                                                                                                                                                                                                                                                                                                                                       |
| 1.0.4     | 2026-04-07 | Corrected preference defaults; added notes for non‑functional commands.                                                                                                                                                                                                                                                                                                                      |
| 1.0.3     | 2026-04-05 | Added Auto-Revert Debug, removed duplicate commands.                                                                                                                                                                                                                                                                                                                                         |
| 1.0.2     | 2026-04-05 | Added Request Battery Status and Auto-Revert Debug.                                                                                                                                                                                                                                                                                                                                          |
| 1.0.1     | 2026-04-05 | Added Battery Offset.                                                                                                                                                                                                                                                                                                                                                                        |
| **1.0.0** | 2026-03-20 | Initial production documentation.                                                                                                                                                                                                                                                                                                                                                            |

***

