# 🚪 Xiaomi Aqara Contact Sensor Hubitat Integration
### _Quick Start, Installation & Configuration_

## 🔢 Version Control
**Document Control:** 1.0.2  
**Current Status:** Stable  
- **Driver:** `XiaomiAqaraContactSensor-DGBQ.groovy` (v1.0.11)  
**Maintenance Lead:** David Ball-Quenneville (DGBQ)  
**Original Developers:** Jonathan Michaelsen (Xiaomi Aqara Mijia driver), Dan Danache (IKEA Parasoll driver structure)

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- [🧭Introduction & Overview](#introduction)
- [🚀Quick Start & Installation](#installation)
- [⚙️Driver: Command & Configuration Reference](#driver-reference)
- [🛠️Support & Enhancements](#support-and-enhancements)
- [🛡️Disclaimers](#disclaimers)
- [📜Revision History](#revision-history)

</details>

---

<h2 id="introduction">🧭 Introduction & Overview</h2>

Welcome to the maintained branch of the Xiaomi Aqara Contact Sensor integration. This project was born out of a need for a clean, reliable driver that does only one thing well: monitoring your doors and windows.

The original universal driver by **Jonathan Michaelsen** supported dozens of Xiaomi sensors but included many capabilities (motion, vibration, weather, buttons) that are irrelevant for a simple contact sensor. The IKEA Parasoll driver by **Dan Danache** introduced a modern, structured approach with health checks and clean logging.

Acting as the **Project Manager**, I have utilized **Gemini AI** to perform deep-dive code audits, stripping out all unnecessary clutter and adding reliability features such as automatic health checks, contact state inversion, and network rejoin tracking.

This version is optimized for the **Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01)** and the **Mijia Door/Window Sensor (lumi.sensor_magnet)**. It aims to provide a transparent, robust interface that ensures your sensor reports open/closed state and battery level reliably—every single time.

---

<h2 id="installation">🚀 Quick Start, Installation & Configuration</h2>

This guide will ensure your Xiaomi contact sensor pairs correctly and communicates reliably with Hubitat.

### **1. Install the Driver Code**
1. Log into your **Hubitat Elevation Web Interface**.
2. Navigate to **Drivers Code** in the sidebar.
3. Click **+ New Driver** and paste the contents of `XiaomiAqaraContactSensor-DGBQ.groovy`. Click **Save**.

### **2. Pair Your Sensor**
1. Put your Xiaomi contact sensor into pairing mode. (Typically, press and hold the reset button with a pin for 5 seconds until the LED blinks blue three times.)
2. In Hubitat, go to **Devices** → **Add Device** → **Zigbee**.
3. Your sensor will appear as "Xiaomi Aqara Contact Sensor". Select it and click **Add Device**.
4. The driver will automatically assign itself based on the Zigbee fingerprint.

### **3. Configure the Device**
1. Open your newly created contact sensor device page.
2. Click **Configure** (this sets up Zigbee bindings and reporting intervals).
3. Under **Preferences**, adjust as desired:
   - **Log verbosity:** Default `Debug` is fine for testing; change to `Info` for daily use.
   - **Auto-Revert Debug:** When enabled (default), Debug logging automatically turns off after 30 minutes. Disable to keep debug on indefinitely.
   - **Invert contact state:** Check if your sensor is installed backwards (reports open as closed).
4. Click **Save Preferences**.

### **4. Test the Sensor**
1. Open and close the magnet pair. The device page should update `contact` to `open` or `closed` within a few seconds.
2. Check the Logs tab for any errors.

---

<h2 id="driver-reference">⚙️ Driver: Command & Configuration Reference</h2>

### **Xiaomi Aqara Contact Sensor (Standalone)**
This is a **standalone driver** – no parent/child relationship is required. It communicates directly with the Zigbee sensor. The driver implements **ContactSensor**, **Battery**, and (custom) **HealthCheck** capabilities, ensuring compatibility with Hubitat dashboards, Rule Machine, and Alexa/Google Home (via contact sensor capability).

---

### **Commands**
Commands are manual actions triggered within the Hubitat interface to interact with the sensor.

| Command | Description | Notes |
| :--- | :--- | :--- |
| **`Configure`** | Sets up Zigbee bindings and reporting intervals (battery every 5 hours, contact on change). | Run after pairing or if the sensor stops reporting. |
| **`Refresh`** | Reads the current contact state and battery percentage. | **Wake‑sensitive:** Press the sensor’s button or open/close the contact immediately before clicking. |

> **Note:** There is no `Ping` command – it is not useful for battery‑powered sleepy sensors and has been removed.

---

### **Preferences**
Preferences define the logging and behavior of the driver.

| Preference | Detail | Requirement | Default |
| :--- | :--- | :--- | :--- |
| **Log verbosity** | Select level: `Debug` (log everything), `Info` (important events), `Warning`, `Error`. | Optional | **Debug** |
| **Auto-Revert Debug** | When enabled (default), Debug logging automatically turns off after 30 minutes. Disable to keep debug on indefinitely. | Optional | **Enabled** |
| **Invert contact state** | Swaps `open` and `closed` reports. | Optional | **Off** |

> **Note:** The `HealthCheck` capability has been removed from the driver metadata – this only removes the stray "Ping" button from the UI. The custom `healthStatus` attribute and hourly health checks remain fully functional.

---

### **Current States**
Current States provide live telemetry used for Dashboards, Rule Machine, and Alexa.

| Attribute | Description | Values / Unit |
| :--- | :--- | :--- |
| **`contact`** | Primary door/window state. | `open`, `closed` |
| **`battery`** | Calculated charge percentage. | 0–100% |
| **`lastBattery`** | Timestamp of last battery report. | Date/time |
| **`healthStatus`** | Device reachability indicator (custom, no Ping button). | `online`, `offline`, `unknown` |
| **`checkInterval`** | Frequency of health checks. | 3600 seconds (hardcoded) |
| **`networkRejoinCount`** | Number of times device rejoined Zigbee mesh. | Integer |

---

### **State Variables**
Internal memory points used for driver logic (visible in the device’s **State Variables** tab for advanced troubleshooting).

| Variable | Purpose |
| :--- | :--- |
| **`lastCx`** | Stores driver version to auto‑configure if updated. |
| **`lastRx`** | Timestamp of the last received Zigbee message. |
| **`lastTx`** | Timestamp of the last Zigbee message sent by the driver. |

---

### **Troubleshooting**

1. **Sensor not reporting contact changes:** The device may be asleep. Press the physical reset button or open/close the contact 2–3 times in quick succession to wake it.
2. **Battery shows 0% or unknown:** Click **Refresh** after waking the device. If still zero, the sensor may use proprietary reporting; ensure the driver’s proprietary parser is active (it is, by default).
3. **Health status is "offline":** The driver has not received a Zigbee message in over 12 hours. Press the sensor’s button to wake it; status should return to `online` within a few minutes.
4. **Network rejoin count increments frequently:** The sensor has poor Zigbee mesh connectivity. Check your mesh by moving a repeater (e.g., a smart plug) closer to the sensor.
5. **Device not appearing during pairing:** Ensure the sensor is in pairing mode (LED blinking). If still not found, exclude/reset the device first (press and hold reset button for 10 seconds until LED flashes rapidly).

---

<h2 id="support-and-enhancements">🛠️ Support & Enhancements</h2>

### **A "Helpful Peer" Approach to Support**
I want to be clear and polite: **I am a Project Manager, not a professional Groovy developer.** I have spent considerable time refining this driver using AI to ensure it works perfectly in my own home. Because I am not a coder by trade, I cannot provide traditional technical support or "hotfixes" for unique environment issues, but feel free to contact me (see "How to Contact Me" below).

**May I suggest how to get help:**
If you run into an error, I highly recommend doing what I do! 
1. Copy the error from your Hubitat logs.
2. Copy the relevant section of the driver code.
3. Paste both into an AI (like Gemini or ChatGPT) and ask: *"How do I fix this in Hubitat?"* It is a fantastic way to learn and solve problems in real-time! It’s how I built this!

### **Enhancement Requests**
I am always open to making this integration better. If you have an idea for an enhancement that would benefit a broad range of users (and specifically if it improves it for my own environment!), I am happy to take a look. 

However, if your request doesn't quite fit my specific setup, please don't take it personally! You are more than welcome to do exactly what I did: **Fork the code and make the changes yourself.**

### **How to Contact Me**
Please reach out via **GitHub only** and complete a ticket. I do not check the Hubitat community forums often. I try to check my GitHub notifications once or twice a week, so please be patient—I’m a nice guy, just a busy one! I'll get back to you when I can.

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

### **Community & Personal Use**
This driver is provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Xiaomi**, **Aqara**, or **Hubitat Elevation**. All logic refinements were developed through independent analysis to provide local integration where official documentation was limited.

### **AI-Assisted Development Notice**
This driver was refined through a collaborative effort between a human **Project Manager** and **Gemini AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases. 
* **User Responsibility:** Users should monitor their initial installation to ensure their specific hardware configuration responds as intended.

### **Operational Safety**
* **_Insuo Periculo_:** While this driver is tested in a live production environment, custom code interacts with your Zigbee network and devices in unique ways. I am not responsible for any unintended behavior, battery drain, or loss of functionality.
* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.
* **Stable Status:** This driver is marked **Stable**. It has been tested with MCCGQ11LM (AS006CNW01) and lumi.sensor_magnet. Other Xiaomi models may work but are not officially supported.

---

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date | Changes |
| :--- | :--- | :--- |
| **1.0.2** | 2026-04-27 | Updated for driver v1.0.11: added Auto-Revert Debug preference, removed HealthCheck capability (no more stray "Ping" button). Preserved all existing content. |
| **1.0.1** | 2026-04-25 | Updated for driver v1.0.10; clarified `lastTx` behaviour. |
| **1.0.0** | 2026-04-24 | Initial documentation – stripped universal driver, added health check, log levels, contact inversion, network rejoin count. |