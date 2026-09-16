# 🚪 Tailwind Connect Plus – Quick Start, Installation & Configuration

**Version:** v1.3.2 (2026-09-16)

***

## 🚀 Overview

This document provides step‑by‑step instructions for installing and configuring the **Tailwind Connect Plus (DGBQ Fork)** driver – a production‑stable driver for local control of Tailwind Garage Door Controllers using the Tailwind Local API (firmware v9.95+).

**Key Features:**

* Full local (LAN) control – no cloud dependency

* Supports up to 3 garage doors

* Parent-child architecture with individual door controls

* Capabilities: `GarageDoorControl`, `Actuator`, `ContactSensor`, `Sensor`

* Configurable polling intervals

* Fast polling after door commands

* Auto-revert debug logging (30-minute auto-revert)

* Driver Health / Command Status monitoring

* lastOpen / lastClosed timestamps on child devices

* Identify command with success logging

* Documentation and version tracking

***

## 📋 Prerequisites

Before you begin, ensure you have:

* ✅ A Hubitat Elevation hub (any model)

* ✅ A Tailwind Garage Door Controller with firmware **v9.95** or later (enables Local API)

* ✅ The local IP address of your Tailwind controller

* ✅ A Local Command Key from [web.gotailwind.com](https://web.gotailwind.com/)

* ✅ Administrative access to your Hubitat hub

* ✅ (Optional) webCoRE or Rule Machine automations for advanced control

***

## 🔑 Step 0: Get Your Local Command Key

Before installing the driver, you need to obtain your Local Command Key:

1. **Log in** to [web.gotailwind.com](https://web.gotailwind.com/) using your Tailwind app credentials

2. **Navigate** to **Local Control Key** in the settings menu

3. **Create a new** Local Command Key

4. **Copy the key** – you'll need it during configuration

> **⚠️ Important:** The Local Command Key is per-account and is the same for each device you may have on your account. Keep this key secure.

***

## 📦 Installation

### Step 1: Add the Driver Code (Parent)

1. Log in to your Hubitat web interface

2. Go to **Drivers Code** (left sidebar)

3. Click the **+ New Driver** button

4. **Paste** the entire parent driver code from the repository (`TailwindConnectPlus.groovy`) into the editor

5. Set the **Name** to `Tailwind Connect Plus`

6. Click **Save**

### Step 2: Add the Driver Code (Child)

1. Go to **Drivers Code** (left sidebar)

2. Click the **+ New Driver** button

3. **Paste** the entire child driver code from the repository (`TailwindConnectPlus-Child.groovy`) into the editor

4. Set the **Name** to `Tailwind Connect Plus - Child`

5. Click **Save**

> **⚠️ Important:** Both drivers must be installed. The child driver is required for individual door control.

### Step 3: Create a Parent Device

1. Go to **Devices** (left sidebar)

2. Click the **+ Add Device** button

3. Set the **Name** to something descriptive (e.g., `Garage Main Door`)

4. Set the **Type** to the driver you just created (`Tailwind Connect Plus`)

5. Assign a **Room** (optional)

6. Click **Save Device**

***

## ⚙️ Configuration

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

***

## 🧪 Testing & Verification

### Step 6: Verify Installation

Review the device attributes. Key attributes include:

| Attribute         | Description                                                                |
| ----------------- | -------------------------------------------------------------------------- |
| **Status**        | Human-readable status message (e.g., "All doors closed")                   |
| **driverVersion** | Should show `v1.3.2`                                                       |
| **driverHealth**  | Driver health status: `normal`, `failed`, `timeout`, `communication_error` |
| **lastError**     | Most recent error message (or `None`)                                      |
| **lastFetch**     | Timestamp of the last successful poll                                      |

### Step 7: Check Child Devices

1. Look in your device list for child devices

2. They should appear with names like:

   * `Home Garage : Door 1`

   * `Home Garage : Door 2` (if applicable)

   * `Home Garage : Door 3` (if applicable)

3. Each child device should have these capabilities:

   * `GarageDoorControl` – Open/Close commands

   * `Actuator` – Physical device control

   * `ContactSensor` – Open/Closed status

   * `Sensor` – General sensor capabilities

4. Each child device should also show:

   * `lastOpen` – Timestamp of when the door last opened

   * `lastClosed` – Timestamp of when the door last closed

### Step 8: Test Open/Close Commands

#### Test Door 1

| Test             | How to Test                          | Expected Result        |
| ---------------- | ------------------------------------ | ---------------------- |
| **Open Door 1**  | Go to child device → Click **Open**  | Door physically opens  |
| **Close Door 1** | Go to child device → Click **Close** | Door physically closes |

#### Test All Doors (If Applicable)

| Test             | How to Test                               | Expected Result |
| ---------------- | ----------------------------------------- | --------------- |
| **Open Door 2**  | Child device for Door 2 → Click **Open**  | Door 2 opens    |
| **Close Door 2** | Child device for Door 2 → Click **Close** | Door 2 closes   |
| **Open Door 3**  | Child device for Door 3 → Click **Open**  | Door 3 opens    |
| **Close Door 3** | Child device for Door 3 → Click **Close** | Door 3 closes   |

### Step 9: Test Refresh Command

1. On the parent device, click the **Refresh** command

2. Check the logs for:

   * `Manual refresh triggered`

   * `Door Status: -1` (or similar)

### Step 10: Test Identify Command

1. On the parent device, click the **Identify** command

2. Check the logs for:

   * `Identify command sent – flashing controller LED`

   * `Identify command completed successfully`

> **Note:** The LED will flash on the controller. The log may take a moment to appear due to the asynchronous HTTP request.

### Step 11: Test Verify Setup Command

1. On the parent device, click the **Verify Setup** command

2. Check the response – it should show:

   * ✅ Configuration status

   * ✅ Controller reachability

   * ✅ Child device count

### Step 12: Check Logs

Open the Live Logs and look for:

text

```
Garage Main Door: Initializing driver v1.3.2
Garage Main Door: Door Status: 0
Garage Main Door: Attempting to open door 1
Garage Main Door: open Response: 0
Garage Main Door: Now polling every 2 seconds for door 1 to open
Garage Main Door: Door #1 Desired: open | Current: closed
Garage Main Door: Door #1 Desired: open | Current: open
Garage Main Door: Completed open Successfully on Door #1
Garage Main Door: Setting Door Status attribute to 1
Garage Main Door: Syncing child device Home Garage:1 from closed to open
```

**Additionally, when the door opens from any source (app, remote, driver):**

text

```
Garage Main Door: Door status changed: All doors closed → Door 1 open, others closed
```

> **✅ Success:** You should **NOT** see any `toInteger()` errors in the logs.

### Step 13: Verify Driver Health

Check the device attributes after testing:

| Attribute        | Expected Value                                 |
| ---------------- | ---------------------------------------------- |
| **driverHealth** | `normal`                                       |
| **lastError**    | `None`                                         |
| **Status**       | Human-readable message matching the door state |

***

## 📋 Known Limitations

| Limitation                                   | Explanation                                                                                                                                |
| -------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **No descriptions for childOpen/childClose** | Hubitat does not support descriptions for commands that take parameters (like `integer`). This is a platform limitation, not a driver bug. |
| **Legacy `poll` command removed**            | The legacy `poll` command has been removed. Use `refresh` instead for manual polling.                                                      |
| **LED Brightness Control Removed**           | The `/led` endpoint is not available on all Tailwind controller models or firmware versions. This feature has been removed.                |

***

## 🐛 Troubleshooting

### Common Issues and Solutions

| Issue                                         | Possible Cause                                                                  | Solution                                                                      |
| --------------------------------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| **Driver won't save**                         | Syntax error in code                                                            | Verify you copied the entire code correctly                                   |
| **Parent device won't create**                | Missing driver code                                                             | Save the parent driver code before creating the device                        |
| **Child devices not created**                 | Door count set to 0                                                             | Set **Number of Doors** to 1, 2, or 3                                         |
| **Child devices not created**                 | Incorrect Controller Display Name                                               | Ensure **Controller Display Name** is set correctly                           |
| **Door doesn't respond**                      | Wrong IP address                                                                | Verify the IP in preferences                                                  |
| **Door doesn't respond**                      | Wrong Local Command Key                                                         | Verify the key from [web.gotailwind.com](https://web.gotailwind.com/)         |
| **Door doesn't respond**                      | Controller not on network                                                       | Verify the controller is connected                                            |
| **Door doesn't respond**                      | Wrong door number                                                               | Check **Number of Doors** preference                                          |
| **`toInteger()` error**                       | Code not fully updated                                                          | Re-paste the latest code and save again                                       |
| **No logs showing**                           | Log Verbosity set to ERROR or WARN                                              | Set **Log Verbosity** to `INFO` or `DEBUG`                                    |
| **Door is slow**                              | Fast polling interval too high                                                  | Reduce **Fast Polling Interval** to `1` second                                |
| **Door times out**                            | Door operation timeout too low                                                  | Increase **Door Operation Timeout** to `90` seconds                           |
| **Child device status mismatch**              | Child device not updated                                                        | Click **Refresh** on the parent device                                        |
| **Child device missing**                      | Door count changed                                                              | Set **Number of Doors** correctly and save preferences                        |
| **driverHealth shows "failed"**               | Door didn't reach desired state within timeout                                  | Check for obstructions, sensor issues, or mechanical problems                 |
| **driverHealth shows "communication\_error"** | Controller unreachable                                                          | Verify IP address and network connectivity                                    |
| **lastOpen / lastClosed not updating**        | Child device not synced                                                         | Click **Refresh** on the parent device                                        |
| **No Identify logs appearing**                | Log Verbosity set to WARN or ERROR, or delayed due to asynchronous HTTP request | Set **Log Verbosity** to `INFO` or `DEBUG`. Logs may take a moment to appear. |

### Still Having Issues?

1. **Check** the Live Logs for errors

2. **Verify** your Local Command Key is correct

3. **Test** the API directly from a browser:

   * `http://[YOUR_IP]/status` (should show a number)

4. **Verify** the controller is on firmware v9.95 or later

5. **Check** `driverHealth` and `lastError` attributes for clues

6. **Set Log Verbosity to DEBUG** for detailed logs

7. **Run the Verify Setup command** for a configuration report

8. **Reboot** your Hubitat hub

9. **Post** in the Hubitat Community forums for help

***

## 📞 Support

If you encounter issues, please check the [CHANGELOG.md](https://changelog.md/) for known issues or reach out to the Hubitat community forums. When reporting issues, include:

* Driver version (`driverVersion` attribute)

* Your Hubitat model and platform version

* `driverHealth` and `lastError` values

* Relevant log entries

* Tailwind controller firmware version

***

## ✅ Installation Checklist

| Step                                                                | Completed? |
| ------------------------------------------------------------------- | ---------- |
| Local Command Key obtained                                          | ☐          |
| IP address of controller known                                      | ☐          |
| Parent driver code saved (`TailwindConnectPlus.groovy` v1.3.2)      | ☐          |
| Child driver code saved (`TailwindConnectPlus-Child.groovy` v1.1.3) | ☐          |
| Parent device created with Tailwind Connect Plus                    | ☐          |
| Preferences configured                                              | ☐          |
| Child devices created                                               | ☐          |
| Door 1 opens/closes                                                 | ☐          |
| Door 2 opens/closes (if applicable)                                 | ☐          |
| Door 3 opens/closes (if applicable)                                 | ☐          |
| Refresh command works                                               | ☐          |
| Identify command works                                              | ☐          |
| Verify Setup command works                                          | ☐          |
| No `toInteger()` errors in logs                                     | ☐          |
| driverHealth shows "normal"                                         | ☐          |
| lastError shows "None"                                              | ☐          |
| lastOpen / lastClosed appear on child devices                       | ☐          |

***

## ✅ Summary

| What You Should See                 | Status |
| ----------------------------------- | ------ |
| Driver initializes without errors   | ✅      |
| Child devices appear in device list | ✅      |
| Doors open and close                | ✅      |
| Status updates correctly            | ✅      |
| Refresh command works               | ✅      |
| Identify command works              | ✅      |
| Verify Setup command works          | ✅      |
| No `toInteger()` errors             | ✅      |
| driverHealth shows "normal"         | ✅      |
| lastError shows "None"              | ✅      |
| lastOpen / lastClosed are updating  | ✅      |

***

**Happy Automating!** 🚀

***

## ✅ Summary of Changes for This Document (v1.3.2)

| Section               | Change                                                                    |
| --------------------- | ------------------------------------------------------------------------- |
| **Version**           | Updated to v1.3.2 – 2026-09-16                                            |
| **Overview**          | Removed the word "professional"                                           |
| **Installation**      | Updated parent driver file reference to `TailwindConnectPlus.groovy`      |
| **Installation**      | Updated child driver file reference to `TailwindConnectPlus-Child.groovy` |
| **Checklist**         | Updated driver file names and versions to match current names             |
| **Version Control**   | Updated current versions: Parent v1.3.2, Child v1.1.3                     |
| **Key Features**      | Added Identify command with success logging                               |
| **Testing**           | Added Step 10: Test Identify Command                                      |
| **Testing**           | Updated Step 6: driverVersion should show v1.3.2                          |
| **Log Examples**      | Updated to show v1.3.2 initialization log                                 |
| **Log Examples**      | Added note about Identify logs taking a moment to appear                  |
| **Known Limitations** | Added LED Brightness Control removed note                                 |
| **Troubleshooting**   | Added "No Identify logs appearing" issue and solution                     |
| **Checklist**         | Added Identify command works to checklist                                 |
| **Summary**           | Added Identify command works to summary                                   |

***

