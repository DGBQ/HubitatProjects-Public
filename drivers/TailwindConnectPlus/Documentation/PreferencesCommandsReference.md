# Commands & Preferences Reference

**Project:** Tailwind Connect Plus (DGBQ Fork)\
**Driver Version:** Hub v1.3.2 / Child v1.1.3\
**Document Version:** 1.0.0

***

## 🎮 Parent Hub Commands

| Command          | Description                                                                                                                                                |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Refresh**      | Manual Sync: Triggers an immediate poll of the controller and updates the door status.                                                                     |
| **Clear Debug**  | Clears the debug buffer stored in the `rawDebug` attribute.                                                                                                |
| **Identify**     | Flashes the LED on the Tailwind controller. Useful for identifying which physical controller you're communicating with when you have multiple controllers. |
| **Verify Setup** | Tests your configuration and confirms the controller is reachable. Returns a detailed report.                                                              |

> **Note:** The legacy `poll` command was removed in v1.2.8. Use `refresh` instead.

> **Note:** The `childOpen` and `childClose` commands were removed from the parent UI in v1.2.9. They are for internal use by child devices and remain in the code.

***

## ⚙️ Parent Hub Preferences

| Preference                           | Description                                                                         | Requirement   | Default            |
| ------------------------------------ | ----------------------------------------------------------------------------------- | ------------- | ------------------ |
| **Tailwind Controller IP**           | The static local IP of your Tailwind controller.                                    | **Mandatory** | _(None)_           |
| **Local Command Key**                | The key from [web.gotailwind.com](https://web.gotailwind.com/) → Local Control Key. | **Mandatory** | _(None)_           |
| **Controller Display Name**          | Name used for the controller in dashboards. Affects child device IDs.               | **Mandatory** | _(None)_           |
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

## 📊 Parent Hub Attributes

| Attribute         | Type     | Description                                                                                 | Automation? |
| ----------------- | -------- | ------------------------------------------------------------------------------------------- | ----------- |
| **Status**        | `string` | Human-readable status message (e.g., "All doors closed").                                   | Yes         |
| **driverVersion** | `string` | The current driver version (e.g., `v1.3.2`).                                                | No          |
| **driverHealth**  | `ENUM`   | Driver health status: `normal`, `failed`, `timeout`, `communication_error`.                 | Yes         |
| **lastError**     | `string` | Most recent error message (or `None`).                                                      | Yes         |
| **lastFetch**     | `string` | Timestamp of the last successful poll.                                                      | Yes         |
| **rawDebug**      | `string` | Debug buffer showing detailed log entries (populated only when `Log Verbosity` is `DEBUG`). | No          |

***

## 🎮 Child Door Commands

| Command   | Description                                        |
| --------- | -------------------------------------------------- |
| **Open**  | Initiates movement to fully open the garage door.  |
| **Close** | Initiates movement to fully close the garage door. |

***

## ⚙️ Child Door Preferences

The Child driver does not have any user-configurable preferences. All configuration is managed by the Parent driver.

***

## 📊 Child Door Attributes

| Attribute      | Type     | Description                                                                             | Automation? |
| -------------- | -------- | --------------------------------------------------------------------------------------- | ----------- |
| **contact**    | `ENUM`   | Current contact sensor state: `open` or `closed`.                                       | Yes         |
| **door**       | `ENUM`   | Current door state: `open` or `closed`.                                                 | Yes         |
| **lastOpen**   | `string` | Timestamp of when the door last opened (formatted per the Date/Time Format preference). | Yes         |
| **lastClosed** | `string` | Timestamp of when the door last closed (formatted per the Date/Time Format preference). | Yes         |

***

## 🛠️ Maintenance Notes

### Updating to a New Driver Version

1. **Save the new driver code** in Drivers Code for both Parent and Child.

2. Go to each device page.

3. Click **Refresh** to ensure the status is up to date.

### What to Do After a Hub Reboot

* The driver's Initialize Robustness feature will automatically resume polling. You do not need to do anything.

### Using `lastOpen` / `lastClosed` for Automations

These attributes allow you to build self-healing automations. For example:

* **Trigger:** `Garage Main Door's lastOpen` is greater than 10:00 PM.

* **Action:** Send a push notification "Garage door opened late!".

***

## 📚 Related Documents

* **[README.md](https://../README.md)** – Project overview, key improvements, credits, and why the fork exists.

* **[CHANGELOG-Hub.md](https://changelog-hub.md/)** – Full version history for the Parent Hub.

* **[CHANGELOG-Child.md](https://changelog-child.md/)** – Full version history for the Child Door.

* **[QuickStartInstallation\&Configuration.md](https://quickstartinstallation\&configuration.md/)** – Step-by-step guide for installing, configuring, and troubleshooting.

* **[RETR-TailwindConnectPlus-Logic.md](https://retr-tailwindconnectplus-logic.md/)** – Technical deep-dive into the toInteger fix, polling flow, and child device architecture.

* **[RoadmapPhasedPlan.md](https://roadmapphasedplan.md/)** – Project requirements and phased rollout history.

***

**Driver Version:** Hub v1.3.2 / Child v1.1.3\
**Document Version:** 1.0.0\
**Date:** November 10, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀
