# 🚪 Tailwind Connect Plus – Quick Start, Installation & Configuration

### _Local Control Garage Door Driver for Hubitat_

***

## 🔢 Version Control

| Item                    | Details                                     |
| ----------------------- | ------------------------------------------- |
| **Document Control**    | 2.0.0                                       |
| **Current Release**     | Production Stable                           |
| **Parent Driver**       | `TailwindConnectPlus.groovy` (v1.3.2)       |
| **Child Driver**        | `TailwindConnectPlus-Child.groovy` (v1.1.3) |
| **Maintenance Lead**    | David Ball-Quenneville (DGBQ)               |
| **Original Developers** | dbadge / Gelix                              |

***

## 📑 Table of Contents

\```
<summary>Click to expand Table of Contents</summary>

- [🧭 Introduction & Overview](#introduction)
- [⚠️ Important: Response Time and Polling Behavior](#response-time)
- [🚀 Quick Start & Installation](#installation)
- [⚙️ Parent Hub: Command & Configuration Reference](#parent-reference)
- [🚪 Door (Child): Command & Configuration Reference](#child-reference)
- [🛠️ Support & Enhancements](#support-and-enhancements)
- [🛡️ Disclaimers](#disclaimers)
- [📜 Revision History](#revision-history)

</details>
```

***

\<h2 id="introduction">🧭 Introduction & Overview\</h2>

Welcome to the maintained branch of the Tailwind Connect Plus Hubitat integration. This project provides full local (LAN) control of Tailwind Garage Door Controllers using the Tailwind Local API (firmware v9.95+).

The original driver by **dbadge** (hosted on **Gelix**'s GitHub repository) provided the essential foundation for local control. However, recent Hubitat platform updates introduced a breaking change: the `toInteger()` method can no longer be called directly on HTTP response objects. This caused the original driver to throw a `groovy.lang.MissingMethodException` on every poll attempt.

Acting as the **Project Manager**, I have utilized **AI** to perform a deep-dive code audit, resolving the breaking change and implementing a comprehensive feature roadmap that adds reliability, visibility, and user experience improvements.

This version is optimized for the **Tailwind iQ3 Controller** and aims to provide a transparent, reliable interface for your garage doors.

**Current release highlights:**

* **Fixed `toInteger()` Error** – Resolved the platform breaking change by casting HTTP response to String first.

* **Driver Health / Command Status** – Real-time visibility into driver health and door operation status.

* **Last Error Attribute** – Dedicated attribute for troubleshooting without digging through logs.

* **Polling Watchdog** – Self-healing timer that detects and repairs broken polling chains.

* **Timeout-Based Failure Detection** – If the door doesn't reach the desired state within the timeout, it's marked as a failure.

* **lastOpen / lastClosed Timestamps** – Child devices track when doors last opened or closed.

* **Date/Time Format Preference** – 8 display options for timestamps.

* **Door Status Change INFO Log** – Logs when door status changes from any source (app, remote, driver).

* **Installation Verification** – `verifySetup()` command to test configuration on-demand.

* **Identify Command** – Flash the controller LED for identification.

***

\<h2 id="response-time">⚠️ Important: Response Time and Polling Behavior\</h2>

The Tailwind Connect Plus driver uses **polling** to check door status. It does not receive instant push notifications from the Tailwind controller.

**What this means:**

* The driver checks the controller at the interval set in **Polling Interval (minutes)** (default: 1 minute).

* After you issue an open or close command, the driver uses **Fast Polling Interval (seconds)** (default: 2 seconds) to confirm the door's state.

* **When the door is opened by another source** (physical button, Tailwind app, remote), the driver will not know until the next regular poll.

**Worst-case delay:**

* Up to **1 minute** (or your configured polling interval) after a door is opened by another source.

**For security or alarm use:**

* If you need **instant** notification when a door opens (e.g., for security alerts, night-time monitoring), a polling-based driver may not meet your needs.

* We strongly recommend installing a **dedicated Z-Wave or Zigbee contact/tilt sensor** directly on the garage door. These sensors report instantly to Hubitat with no polling delay.

* You can keep the Tailwind driver for control, and use the dedicated sensor for security monitoring.

**For control use:**

* The driver is well-suited for controlling doors (open/close) and tracking status for dashboards and general automations.

***

\<h2 id="installation">🚀 Quick Start & Installation\</h2>

Following this setup guide will ensure your Tailwind controller communicates reliably with Hubitat.

### Prerequisites

Before you begin, ensure you have:

* ✅ A Hubitat Elevation hub (any model)

* ✅ A Tailwind Garage Door Controller with firmware **v9.95** or later (enables Local API)

* ✅ The local IP address of your Tailwind controller

* ✅ A Local Command Key from [web.gotailwind.com](https://web.gotailwind.com/)

* ✅ Administrative access to your Hubitat hub

* ✅ (Optional) webCoRE or Rule Machine automations for advanced control

### Step 0: Get Your Local Command Key

1. **Log in** to [web.gotailwind.com](https://web.gotailwind.com/) using your Tailwind app credentials

2. **Navigate** to **Local Control Key** in the settings menu

3. **Create a new** Local Command Key

4. **Copy the key** – you'll need it during configuration

> **⚠️ Important:** The Local Command Key is per-account and is the same for each device you may have on your account. Keep this key secure.

### Step 1: Install the Driver Code (Parent)

1. Log in to your Hubitat web interface

2. Go to **Drivers Code** (left sidebar)

3. Click the **+ New Driver** button

4. **Paste** the contents of `TailwindConnectPlus.groovy` into the editor

5. Set the **Name** to `Tailwind Connect Plus`

6. Click **Save**

### Step 2: Install the Driver Code (Child)

1. Go to **Drivers Code** (left sidebar)

2. Click the **+ New Driver** button

3. **Paste** the contents of `TailwindConnectPlus-Child.groovy` into the editor

4. Set the **Name** to `Tailwind Connect Plus - Child`

5. Click **Save**

> **⚠️ Important:** Both drivers must be installed. The child driver is required for individual door control.

### Step 3: Create the Parent Device

1. Navigate to **Devices** > **Add Device** > **Virtual**

2. **Device Name:** Use a recognizable name (e.g., "Garage Main Door")

3. **Type:** Search for and select **Tailwind Connect Plus**

4. **Room:** Assign a room (optional)

5. Click **Save Device**

### Step 4: Configure the Driver Preferences

On the parent device page, scroll down to the **Preferences** section and fill in the settings.

#### Controller Configuration

| Setting                     | Description                                                                | Example         |
| --------------------------- | -------------------------------------------------------------------------- | --------------- |
| **Tailwind Controller IP**  | The local IP address of your Tailwind controller                           | `192.168.1.100` |
| **Local Command Key**       | The key you created from [web.gotailwind.com](https://web.gotailwind.com/) | `abc123...`     |
| **Controller Display Name** | A name for the controller (used for child device IDs)                      | `Home Garage`   |

> **⚠️ Important:** The **Controller Display Name** is used in the Device Network ID (DNI) of child devices. Changing this will re-create the children devices.

#### Door Configuration

| Setting             | Description                                           | Default  |
| ------------------- | ----------------------------------------------------- | -------- |
| **Number of Doors** | How many doors are connected to this controller (0-3) | `1`      |
| **Door 1 Name**     | Name displayed for Door 1                             | `Door 1` |
| **Door 2 Name**     | Name displayed for Door 2 (if applicable)             | `Door 2` |
| **Door 3 Name**     | Name displayed for Door 3 (if applicable)             | `Door 3` |

> **Note:** Door names appear in the device list and dashboards. They do not affect child device creation.

#### Polling Settings

| Setting                              | Description                                                         | Default | Recommendation                                   |
| ------------------------------------ | ------------------------------------------------------------------- | ------- | ------------------------------------------------ |
| **Polling Interval (minutes)**       | How often the driver checks the controller                          | `1`     | `1` for responsive control, `5` for less traffic |
| **Fast Polling Interval (seconds)**  | How often to check after a door command                             | `2`     | `1` for faster response, `2` for less traffic    |
| **Door Operation Timeout (seconds)** | Max time to wait for door to change state before marking as failure | `60`    | `60` is safe for most doors                      |

> **Note:** The driver polls at regular intervals to check door status. After a door command, it switches to fast polling to confirm the door has reached the desired state. If the door doesn't reach the desired state within the timeout, it's marked as a failure.

#### Display Settings

| Setting              | Description                                                | Default            |
| -------------------- | ---------------------------------------------------------- | ------------------ |
| **Date/Time Format** | How date and time values appear in Current States and logs | `MM/DD/YYYY AM/PM` |

**Available Date/Time Formats:**

* `MM/DD/YYYY AM/PM`

* `YYYY-MM-DD (24hour)`

* `YYYY-MM-DD AM/PM`

* `MM/DD/YYYY (24hour)`

* `DD/MM/YYYY (24hour)`

* `DD/MM/YYYY AM/PM`

* `Mon DD YYYY hh:MM AM/PM`

* `Day, Mon DD, YYYY hh:MM AM/PM`

#### Logging & Debugging

| Setting                    | Description                                          | Default |
| -------------------------- | ---------------------------------------------------- | ------- |
| **Log Verbosity**          | Controls log detail: ERROR, WARN, INFO, or DEBUG     | `INFO`  |
| **Auto-Revert Debug**      | Automatically reverts DEBUG to INFO after 30 minutes | `On`    |
| **Max debug buffer chars** | Maximum characters stored in the rawDebug attribute  | `6000`  |

> **Note:** To enable debug logging, set **Log Verbosity** to `DEBUG`. The **Auto-Revert Debug** feature will revert it to `INFO` after 30 minutes.

### Step 5: Save Preferences

1. Click **Save Preferences** at the bottom of the page

2. The driver will initialize and create child devices for each door

> **Pro Tip:** Set a **DHCP Reservation** in your router for the Tailwind controller to ensure the IP never changes, preventing future communication drops.

***

\<h2 id="parent-reference">⚙️ Parent Hub: Command & Configuration Reference\</h2>

### Tailwind Connect Plus (Parent)

The **Parent Driver** is the "Command and Control" center of this integration. Because Hubitat cannot communicate with each door individually without the controller, this driver maintains the HTTP connection to the physical Tailwind controller. It acts as a protocol translator, converting Hubitat instructions into Local API commands. It also monitors connection health and manages the lifecycle of all "Child" door devices.

***

### Commands

Commands are manual actions triggered within the Hubitat interface to execute specific driver logic or hardware instructions.

| Command            | Description                                                                                                                                                | Caution |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- |
| **`Refresh`**      | Manual Sync: Triggers an immediate poll of the controller and updates the door status.                                                                     | None.   |
| **`Clear Debug`**  | Clears the debug buffer stored in the `rawDebug` attribute.                                                                                                | None.   |
| **`Identify`**     | Flashes the LED on the Tailwind controller. Useful for identifying which physical controller you're communicating with when you have multiple controllers. | None.   |
| **`Verify Setup`** | Tests your configuration and confirms the controller is reachable. Returns a detailed report.                                                              | None.   |

> **Note:** The legacy `poll` command was removed in v1.2.8. Use `refresh` instead.

> **Note:** The `childOpen` and `childClose` commands were removed from the parent UI in v1.2.9. They are for internal use by child devices and remain in the code.

> **Note:** The `Identify` command may take a moment for the log to appear due to the asynchronous HTTP request.

***

### Preferences

Preferences define the communication parameters and logging behavior of the driver.

| Preference                           | Detail                                                                              | Requirement   | Default            |
| ------------------------------------ | ----------------------------------------------------------------------------------- | ------------- | ------------------ |
| **Tailwind Controller IP**           | The static local IP of your Tailwind controller.                                    | **Mandatory** | None               |
| **Local Command Key**                | The key from [web.gotailwind.com](https://web.gotailwind.com/) → Local Control Key. | **Mandatory** | None               |
| **Controller Display Name**          | Name used for the controller in dashboards. Affects child device IDs.               | **Mandatory** | None               |
| **Number of Doors**                  | How many doors are connected to this controller (0-3).                              | **Mandatory** | `1`                |
| **Door 1 Name**                      | Name displayed for Door 1 in dashboards.                                            | Optional      | `Door 1`           |
| **Door 2 Name**                      | Name displayed for Door 2 in dashboards.                                            | Optional      | `Door 2`           |
| **Door 3 Name**                      | Name displayed for Door 3 in dashboards.                                            | Optional      | `Door 3`           |
| **Polling Interval (minutes)**       | How often the driver checks door status.                                            | Optional      | `1`                |
| **Fast Polling Interval (seconds)**  | How often to check status after a door command.                                     | Optional      | `2`                |
| **Door Operation Timeout (seconds)** | Maximum time to wait for door to change state before marking as failure.            | Optional      | `60`               |
| **Date/Time Format**                 | Select how date and time values appear in Current States and logs.                  | Optional      | `MM/DD/YYYY AM/PM` |
| **Log Verbosity**                    | Controls how much detail is written to the logs. Options: ERROR, WARN, INFO, DEBUG. | Optional      | `INFO`             |
| **Auto-Revert Debug**                | Automatically reverts Log Verbosity from DEBUG to INFO after 30 minutes.            | Optional      | `On`               |
| **Max debug buffer chars**           | Maximum characters stored in the `rawDebug` attribute.                              | Optional      | `6000`             |

***

### Current States

Current States provide real-time telemetry regarding the controller's health and activity.

* **`Status`**: Human-readable status message (e.g., "All doors closed").

* **`driverVersion`**: The current driver version (e.g., `v1.3.2`).

* **`driverHealth`**: Driver health status: `normal`, `failed`, `timeout`, `communication_error`.

* **`lastError`**: Most recent error message (or `None`).

* **`lastFetch`**: Timestamp of the last successful poll.

* **`rawDebug`**: Debug buffer showing detailed log entries (populated only when `Log Verbosity` is `DEBUG`).

***

### State Variables

Internal memory points used for driver logic.

* **`lastNumericStatus`**: Tracks numeric status separately from the human-readable `Status` attribute.

* **`lastPollMs`**: Timestamp (in milliseconds) of the last successful poll.

* **`debugBuf`**: Buffer holding debug log entries.

***

### Troubleshooting (Parent Level)

1. **Driver won't save:** Verify you copied the entire code correctly.

2. **Parent device won't create:** Save the parent driver code before creating the device.

3. **Child devices not created:** Set **Number of Doors** to 1, 2, or 3 and ensure **Controller Display Name** is set correctly.

4. **Door doesn't respond:** Verify the IP address, Local Command Key, and that the controller is on the network.

5. **`toInteger()` error:** Re-paste the latest code and save again.

6. **No logs showing:** Set **Log Verbosity** to `INFO` or `DEBUG`.

7. **Door is slow:** Reduce **Fast Polling Interval** to `1` second.

8. **Door times out:** Increase **Door Operation Timeout** to `90` seconds.

9. **`driverHealth` shows "failed":** Door didn't reach desired state within timeout. Check for obstructions, sensor issues, or mechanical problems.

10. **`driverHealth` shows "communication\_error":** Controller unreachable. Verify IP address and network connectivity.

11. **No Identify logs appearing:** Set **Log Verbosity** to `INFO` or `DEBUG`. Logs may take a moment to appear due to the asynchronous HTTP request.

***

\<h2 id="child-reference">🚪 Door (Child): Command & Configuration Reference\</h2>

### Tailwind Connect Plus - Child

The **Child Driver** represents the individual door in your garage. While the Parent handles the HTTP bridge to the controller, the Child driver is the operational interface for daily use. This driver implements multiple Hubitat capabilities—including **GarageDoorControl**, **Actuator**, **ContactSensor**, and **Sensor**—to ensure maximum compatibility with voice assistants (Alexa/Google Home) and automation engines. Each door is uniquely identified by its **Device Network ID (DNI)** assigned by the Parent driver.

***

### Commands

Commands are manual actions triggered within the Hubitat interface to control the physical movement or state of the door.

| Command     | Description                                        | Caution |
| ----------- | -------------------------------------------------- | ------- |
| **`Open`**  | Initiates movement to fully open the garage door.  | None.   |
| **`Close`** | Initiates movement to fully close the garage door. | None.   |

***

### Preferences

The Child driver does not have any user-configurable preferences. All configuration is managed by the Parent driver.

***

### Current States

Current States provide the live telemetry and operational attributes used for Dashboards, Alexa, and Rule Machine triggers.

* **`contact`**: Current contact sensor state: `open` or `closed`.

* **`door`**: Current door state: `open` or `closed`.

* **`lastOpen`**: Timestamp of when the door last opened (formatted per the Date/Time Format preference).

* **`lastClosed`**: Timestamp of when the door last closed (formatted per the Date/Time Format preference).

***

### State Variables

The Child driver does not use any state variables.

***

### Troubleshooting (Child Level)

1. **Door doesn't respond:** Verify the parent driver is configured correctly and the controller is reachable. Click **Refresh** on the parent device.

2. **Child device status mismatch:** Child device not updated. Click **Refresh** on the parent device.

3. **Child device missing:** Door count changed. Set **Number of Doors** correctly on the parent device and save preferences.

4. **`lastOpen` / `lastClosed` not updating:** Child device not synced. Click **Refresh** on the parent device.

5. **Door opens slowly:** The Tailwind controller reports "open" when the door starts moving, and "closed" only when fully seated. This is normal behavior of the controller.

***

\<h2 id="support-and-enhancements">🛠️ Support & Enhancements\</h2>

### A "Helpful Peer" Approach to Support

I want to be clear and polite: **I am a Project Manager, not a professional Groovy developer.** I have spent considerable time refining these drivers using AI to ensure they work perfectly in my own home. Because I am not a coder by trade, I cannot provide traditional technical support or "hotfixes" for unique environment issues, but feel free to contact me (see "How to Contact Me" below).

**May I suggest how to get help:**\
If you run into an error, I highly recommend doing what I do!

1. Copy the error from your Hubitat logs.

2. Copy the relevant section of the driver code.

3. Paste both into an AI (like Gemini or ChatGPT) and ask: _"How do I fix this in Hubitat?"_ It is a fantastic way to learn and solve problems in real-time! It's how I built this!

### Enhancement Requests

I am always open to making this integration better. If you have an idea for an enhancement that would benefit a broad range of users (and specifically if it improves it for my own environment!), I am happy to take a look.

However, if your request doesn't quite fit my specific setup, please don't take it personally! You are more than welcome to do exactly what I did: **Branch the code and make the changes yourself.**

### How to Contact Me

Please reach out via **GitHub only** and complete a ticket. I do not check the Hubitat community forums often. I try to check my GitHub notifications once or twice a week, so please be patient—I'm a nice guy, just a busy one! I'll get back to you when I can.

***

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

### Community & Personal Use

These drivers are provided "as-is" as a service to the Hubitat community. This is a personal maintenance fork and is **not** an official product of, nor is it endorsed by, **Tailwind**, **Hubitat Elevation**, or the original developers (`dbadge` / `Gelix`). All logic refinements were developed through independent analysis to provide local integration where official documentation was unavailable.

### AI-Assisted Development Notice

This driver overhaul was a collaborative effort between a human **Project Manager** and **AI**. While AI-assisted development allows for deep-dive logic auditing and rapid bug resolution, generative models can occasionally produce code artifacts or logic paths that behave unexpectedly in specific edge cases.

* **User Responsibility:** Users should monitor their initial installation to ensure their specific garage door configuration responds as intended.

### Operational Safety

* **_Insuo Periculo_:** While these drivers are tested in a live production environment, custom code interacts with your local network and hardware in unique ways. I am not responsible for any unintended triggers, missed events, or unexpected door behavior.

* **Physical Safety Warning:** Garage doors are heavy machinery. Always ensure proper safety sensors are installed and functional. This driver should NOT be used as the primary safety mechanism for your garage door.

* **Database Integrity:** It is a best practice to perform a **Full Cloud or Local Backup** of your Hubitat Elevation database before installing or updating custom driver code.

* **Production Status:** This fork is currently in **Production (Stable)** with all phases complete. The driver is feature-complete.

***

\<h2 id="revision-history">📜 Revision History\</h2>

| Version   | Date       | Changes                                                                                                                                                                                                                                                                                                                                                                                                                          |
| --------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **2.0.0** | 2026-09-16 | **Major restructure.** Adopted Rollease-style layout: Table of Contents, Introduction & Overview, dedicated Parent Hub Reference section (Commands, Preferences, Current States, State Variables, Troubleshooting), dedicated Door (Child) Reference section, and a proper Support & Enhancements section with a "Helpful Peer" approach. Added response time warning section. Corrected driver file names and folder structure. |
| **1.3.2** | 2026-09-09 | Updated to v1.3.2. Added Identify command with success logging. Added note about Identify logs taking a moment to appear. Added LED Brightness Control removed note. Added "No Identify logs appearing" to troubleshooting.                                                                                                                                                                                                      |
| **1.2.0** | 2026-08-31 | Updated to v1.2.7 / v1.1.3. Added Phase 3 features (Preference Descriptions, Command Descriptions, verifySetup, lastOpen/lastClosed, Date/Time Format, Door Status Change INFO Log). Added Known Limitations section.                                                                                                                                                                                                            |
| **1.1.0** | 2026-08-18 | Updated to v1.1.9 / v1.1.1. Added Phase 2 features (Driver Health, Last Error, Last Fetch, Enhanced Status, Watchdog, Initialize Robustness). Removed retry logic from feature list.                                                                                                                                                                                                                                             |
| **1.0.0** | 2026-08-12 | **Initial DGBQ Fork.** Fixed `toInteger()` error. Added driver header, version constant, `driverVersion` attribute, `refresh` command, auto-revert debug, logging helpers, sectioned preferences. Renamed to Tailwind Connect Plus.                                                                                                                                                                                              |

***

**Happy Automating!** 🚀

***

## ✅ Summary of Changes for This Document (v2.0.0)

| Section                                             | Change                                                                                          |
| --------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| **Version Control**                                 | Updated to v2.0.0 – 2026-09-16                                                                  |
| **Document Control**                                | Bumped from 1.3.2 to 2.0.0                                                                      |
| **Table of Contents**                               | Added collapsible `<details>` block (new)                                                       |
| **Introduction & Overview**                         | Rewritten as a proper Introduction with prose and current release highlights                    |
| **Response Time Warning**                           | Kept as its own dedicated section                                                               |
| **Quick Start & Installation**                      | Renamed and reorganized (was "Installation")                                                    |
| **Parent Hub: Command & Configuration Reference**   | New dedicated section (Commands, Preferences, Current States, State Variables, Troubleshooting) |
| **Door (Child): Command & Configuration Reference** | New dedicated section (Commands, Preferences, Current States, State Variables, Troubleshooting) |
| **Support & Enhancements**                          | New "Helpful Peer" section (adopted from Rollease)                                              |
| **Disclaimers**                                     | Restructured (Community, AI Notice, Operational Safety)                                         |
| **Revision History**                                | Added v2.0.0 entry                                                                              |
| **Driver file names**                               | Corrected to `TailwindConnectPlus.groovy` and `TailwindConnectPlus-Child.groovy`                |
| **Removed word "professional"**                     | Throughout the document                                                                         |

***

