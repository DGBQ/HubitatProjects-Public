# 🏠 Rollease Acmeda Hubitat Integration
### _Quick Start, Installation & Configuration_

## 🔢 Version Control
**Document Control:** 1.0.0  
**Current Releases:** Beta  
- **Hub Driver:** `RolleaseAcmedaHub-DGBQ_3310.groovy` (V3.3.10)  
- **Shade Driver:** `RolleaseAcmedaShade_DGBQ_231.groovy` (V2.3.1)  
**Maintenance Lead:** David Ball-Quenneville (DGBQ)  
**Original Developer:** Younes Oughla (Yoonoo)

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- 🧭 [Introduction & Overview](#introduction)
- 🚀 [Quick Start & Installation](#installation)
- ⚙️ [Parent Hub: Command & Configuration](#parent-reference)
- 🪟 [Shade (Child): Command & Configuration](#shade-reference)
- 🛠️ [Support & Enhancements](#support-and-enhancements)
- 🛡️ [Disclaimers](#disclaimers)
- 📜 [Revision History](#revision-history)

</details>

---

<h2 id="introduction">🧭 Introduction & Overview</h2>

Welcome to the maintained branch of the Rollease Acmeda Hubitat integration. This project was born out of a need for "set-and-forget" reliability in a modern smart home environment. 

While the original drivers by **Younes Oughla** provided a vital foundation, hardware evolution and Hubitat environment changes necessitated a "Surgical" overhaul. Acting as the **Project Manager**, I have utilized **Gemini AI** to perform deep-dive code audits, resolving long-standing logic-casting conflicts and implementing advanced telemetry (Battery/RSSI) that wasn't previously available.

This version is optimized for the **Automate Pulse 2 Hub** and aims to provide a transparent, robust interface for your shades, ensuring they respond as intended—every single time.

---

<h2 id="installation">🚀 Quick Start, Installation & Configuration</h2>

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
1. On the Parent Hub device page, click the **`ShadeDiscover`** button.
2. The driver will perform a "White-Hat" query of your Hub to identify all registered motors.
3. Navigate back to your **Devices** list. Your shades will now appear as Child devices, ready for use.

---

<h2 id="parent-reference">⚙️ Parent Hub: Command & Configuration Reference</h2>

### **Rollease Acmeda Hub (Parent)**
The **Parent Hub** driver is the "Command and Control" center of this integration. Because Hubitat cannot communicate with every motor individually, this driver maintains a single, persistent **Telnet Socket** to the physical Pulse 2 hardware. It acts as a protocol translator, converting Hubitat instructions into ARC-compliant strings. It also monitors connection health and manages the lifecycle of all "Child" shade devices.

---

### **Commands**
Commands are manual actions triggered within the Hubitat interface to execute specific driver logic or hardware instructions.

| Command | Description | Default / Example | Caution |
| :--- | :--- | :--- | :--- |
| **`Configure`** | Re-initializes internal driver settings and commits preferences to the Hubitat database. | Run after changing any Preference. | None. |
| **`Initialize`** | **The Reset Switch.** Forcibly terminates the current Telnet socket and attempts a clean reconnection. | Use if `status` is `Disconnected`. | Wait 15s between clicks to avoid network buffer hangs. |
| **`Refresh`** | Queries the Pulse 2 Hub for the immediate position and battery telemetry of every motor. | Use to sync states if a remote was used. | None. |
| **`Send Msg`** | Transmits a raw ARC protocol string directly to the Hub. | `!001m050` (Move Shade 001 to 50%). | **Advanced only.** Malformed strings can crash the Hub's listener. |
| **`Send Pulse`** | Transmits a "Keep-Alive" heartbeat signal to prevent socket timeouts. | Automated by driver logic. | Manual use is for latency testing only. |
| **`Send Telnet Command`** | Sends raw Telnet-layer instructions (distinct from ARC messages). | Blank. | Generally not required for standard use. |
| **`Shade Add`** | Manually generates a Child Shade device by its 3-digit ID. | Enter `005` to create Shade 5. | Requires the exact ID assigned in the Rollease App. |
| **`Shade Discover`** | **The Auto-Scanner.** Queries the Hub for all motors and spawns any missing Child devices. | The primary setup tool for new installs. | Process may take up to 30 seconds. |
| **`Shade Remove`** | Deletes a specific Child Shade device from Hubitat by its ID. | Enter `002` to remove Shade 2. | Permanent; removes device from all Rules. |
| **`Shade Delete All`** | **The Nuclear Option.** Purges every associated Child Shade device from Hubitat. | Use for a complete system reset. | **High Risk:** Breaks all Dashboards and Rules. |

---

### **Preferences**
Preferences define the communication parameters and logging behavior of the driver.

| Preference | Detail | Requirement | Default |
| :--- | :--- | :--- | :--- |
| **Hub Address\*** | The static local IP of your Pulse 2 Hub. | **Mandatory** | None |
| **Hub Port\*** | The ARC Protocol port for the Pulse 2. | **Mandatory** | **1487** |
| **Connection Retry Interval** | Seconds the driver waits before attempting to reconnect after a drop. | Optional | **15** |
| **Maximum Idle Time** | The window of silence allowed before the driver resets the socket. | Optional | **300** |
| **Enable Error Silencer** | Suppresses the benign `Stream is closed` warnings native to the Hub. | Optional | **Enabled** |
| **Enable Debug Logging** | Outputs high-detail technical data to the system logs. | Optional | **Off** (Auto-off 30m) |
| **Enable Description Logging** | Logs human-readable events (e.g., "Shade 1 is Opening"). | Optional | **On** |

---

### **Current States**
Current States provide real-time telemetry regarding the Hub's health and activity.

* **`status`**: The primary health indicator. Reports **Connected**, **Disconnected**, or **Initializing**.
* **`lastAction`**: A summary of the most recent internal driver event.
* **`lastCommand`**: Displays the name of the last button pressed in the UI.
* **`lastUpdate`**: A high-precision timestamp of the last successful data burst from the Hub.
* **`shades`**: A dynamic list of the motor IDs currently recognized by the Parent bridge.

---

### **State Variables**
Internal memory points used for driver logic.
* **`shadeList`**: A technical map correlating 3-digit motor IDs to Hubitat Device Network IDs (DNI).

---

### **Troubleshooting (Parent Level)**
1. **Status is "Disconnected":** Confirm the **IP Address** and ensure **Hub Port** is set to `1487`. Click `Initialize`.
2. **Shades Missing during Discovery:** Verify shades are active in the official Rollease Mobile App before running `ShadeDiscover`.
3. **Log Noise:** Ensure **Enable Error Silencer** is `Enabled` to filter out non-critical Hub communication spikes.

---

<h2 id="shade-reference">🪟 Shade (Child): Command & Configuration Reference</h2>

### **Rollease Acmeda Shade (Child)**
The **Shade (Child)** driver represents the individual motor head in your window. While the Parent handles the Telnet bridge to the Hub, the Child driver is the operational interface for daily use. This driver implements multiple Hubitat capabilities—including **WindowShade**, **Switch**, and **Level**—to ensure maximum compatibility with voice assistants (Alexa/Google Home) and automation engines. Each shade is uniquely identified by the 3-digit **Motor Address** assigned by the Pulse 2 Hub.

---

### **Commands**
Commands are manual actions triggered within the Hubitat interface to control the physical movement or state of the motor.

| Command | Description | Default / Example | Caution |
| :--- | :--- | :--- | :--- |
| **`On / Off`** | **The Alexa Fix.** Maps to Open (On) and Close (Off) to prevent "Device not responding" voice errors. | N/A | Essential for reliable Voice Assistant integration. |
| **`Open / Close`** | Initiates the standard "WindowShade" movement to the fully open (0%) or closed (100%) limits. | N/A | None. |
| **`Initialize`** | Re-syncs the Child device with the Parent Hub's communication settings. | Use after Parent IP changes. | None. |
| **`Refresh`** | Forces a targeted poll to the Hub for this specific motor's position, battery, and RSSI telemetry. | Updates all Current States. | None. |
| **`Set Level`** | Sets position via "SwitchLevel." Accepts **Level** (0-100) and **Duration** (seconds). | `setLevel(50, 0)` | Commonly used by Dashboards and Dimmer-based rules. |
| **`Set Position`** | Commands the shade to move to a precise percentage (0–100). | `setPosition(25)` | 0 = Fully Open; 100 = Fully Closed. |
| **`Start Position Change`** | Begins continuous movement in a specified direction (`open` or `close`). | Used for "Press-and-Hold" logic. | Movement continues until a `Stop` command is issued. |
| **`Stop`** | Immediately halts motor rotation at its current location. | Use during active travel. | **Critical:** The master "Emergency Stop" for all automations. |
| **`Stop Position Change`** | Ceases movement initiated by a `Start Position Change` command. | N/A | Use to end a "Press-and-Hold" action. |
| **`Toggle`** | Reverses the current state (if Open, it will Close; if Closed, it will Open). | Useful for single-button remotes. | None. |

---

### **Preferences**
Preferences define the identification and logging behavior for the individual motor.

| Preference | Detail | Requirement | Default |
| :--- | :--- | :--- | :--- |
| **Motor Address\*** | The unique 3-digit ARC identifier (e.g., `001`) for this specific shade. | **Mandatory** | None |
| **Enable Debug Logging** | Outputs high-detail technical tracing to the system logs. | Optional | **Off** (Auto-off 30m) |
| **Enable Description Logging** | Logs human-readable events (e.g., "Bedroom Shade was Closed"). | Optional | **On** |

---

### **Current States**
Current States provide the live telemetry and operational attributes used for Dashboards, Alexa, and Rule Machine triggers.

* **`battery`**: The calculated charge percentage (0–100%).
* **`batteryVoltage`**: The raw voltage reading used for precise power monitoring.
    * *Detail:* Calibrated for the **10.8V to 12.6V** discharge curve.
* **`closed`**: A binary state (**true/false**) indicating if the shade has reached its lower limit.
* **`level`**: The numeric position (0–100) used for "SwitchLevel" compatibility.
* **`moving`**: Indicates if the motor is currently in an active travel state.
* **`open`**: A binary state (**true/false**) indicating if the shade has reached its upper limit.
* **`position`**: The primary vertical location attribute (0–100).
* **`rssi`**: **Received Signal Strength Indicator.** Measures the RF link quality between the Hub and motor.
* **`voltage`**: Displays the current voltage of the Li-ion battery pack.
* **`windowShade`**: The primary operational status (**open**, **closed**, **opening**, **closing**, or **partially open**).

---

### **State Variables**
Internal memory points used by the driver logic.
* **`lastDirection`**: Tracks the trajectory of the motor's most recent movement (`up` or `down`).

---

### **Troubleshooting (Child Level)**
1. **"Device Not Responding" in Alexa:** Ensure you are using the `On/Off` commands in your voice routines. This fork's **Switch Capability** was implemented specifically to eliminate this latency error.
2. **RSSI is "0" or Missing:** The Hub has not yet pushed a signal update. Click **`Refresh`** on the Child device to force a telemetry pull.
3. **Shade Moves the Wrong Way:** Verify your open/close limits are set correctly in the native Rollease App before integrating with Hubitat.

---

<h2 id="support-and-enhancements">🛠️ Support & Enhancements</h2>

### **A "Helpful Peer" Approach to Support**
I want to be clear and polite: **I am a Project Manager, not a professional Groovy developer.** I have spent considerable time refining these drivers using AI to ensure they work perfectly in my own home. Because I am not a coder by trade, I cannot provide traditional technical support or "hotfixes" for unique environment issues, but fell free to contact me see "How to Contact Me" below.

**May I suggest, how to get help:**
If you run into an error, I highly recommend doing what I do! 
1. Copy the error from your Hubitat logs.
2. Copy the relevant section of the driver code.
3. Paste both into an AI (like Gemini or ChatGPT) and ask: *"How do I fix this in Hubitat?"* It is a fantastic way to learn and solve problems in real-time! It’s how I built this!

### **Enhancement Requests**
I am always open to making this integration better. If you have an idea for an enhancement that would benefit a broad range of users (and specifically if it improves it for my own environment!), I am happy to take a look. 

However, if your request doesn't quite fit my specific setup, please don't take it personally! You are more than welcome to do exactly what I did: **Branch the code and make the changes yourself.**

### **How to Contact Me**
Please reach out via **GitHub only** and complete a ticket. I do not check the Hubitat community forums often. I try to check my GitHub notifications once or twice a week, so please be patient—I’m a nice guy, just a busy one! I'll get back to you when I can.

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

| Version | Date | Changes |
| :--- | :--- | :--- |
| **1.0.0** | 2026-03-20 | **Initial Production Documentation.** Comprehensive guide covering Parent/Child command structures, telemetry calibration, and AI-assisted development disclaimers. |