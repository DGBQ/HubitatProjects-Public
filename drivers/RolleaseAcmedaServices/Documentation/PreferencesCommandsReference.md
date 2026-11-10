# Commands & Preferences Reference

**Project:** Rollease Acmeda Hubitat Integration (DGBQ)\
**Driver Version:** Hub v3.3.26 / Shade v2.5.9\
**Document Version:** 1.0.0

***

## 🎮 Parent Hub Commands

| Command               | Phase | Description                                                                                                                                                                                             |
| --------------------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Configure**         | 1     | Re-initializes internal driver settings and commits preferences to the Hubitat database. Run after changing any Preference.                                                                             |
| **Initialize**        | 2     | **The Reset Switch.** Forcibly terminates the Telnet socket, attempts a clean reconnection, and schedules an auto-refresh of all child shades. Use if `status` is `offline` or after a hub power cycle. |
| **sendTelnetCommand** | 1     | Sends raw ARC protocol strings directly to the Hub. **Advanced only.** Standard users should use the Child device controls. Malformed strings can crash the Hub's listener.                             |
| **Shade Add**         | 1     | Manually recreates a Child Shade device when the Parent Hub knows about a shade but Hubitat does not. Enter the exact ID from the Rollease app (e.g., `I39`, `BSG`, `XDG`).                             |
| **Shade Discover**    | 1     | **The Auto-Scanner.** Queries the Hub for all motors and spawns any missing Child devices. The primary setup tool for new installs. Process may take up to 30 seconds.                                  |
| **Shade Remove**      | 1     | Deletes a specific Child Shade device from Hubitat by its ID. **Safety check:** Verifies the child's `Motor Address` matches the requested ID before deleting.                                          |
| **Shade Delete All**  | 1     | **The Nuclear Option.** Purges every associated Child Shade device from Hubitat. **High Risk:** Breaks all Dashboards and Rules.                                                                        |

***

## ⚙️ Parent Hub Preferences

| Preference                    | Phase | Description                                                                                       | Default   |
| ----------------------------- | ----- | ------------------------------------------------------------------------------------------------- | --------- |
| **Hub Address\***             | 1     | The static local IP of your Pulse 2 Hub. **Mandatory.**                                           | _(None)_  |
| **Hub Port\***                | 1     | The ARC Protocol port for the Pulse 2. **Mandatory.**                                             | `1487`    |
| **Connection Retry Interval** | 1     | Seconds the driver waits before attempting to reconnect after a drop. Set to `0` to disable.      | `300`     |
| **Maximum Idle Time**         | 1     | The window of silence allowed before the driver resets the socket. Set to `0` to disable.         | `3600`    |
| **Enable Error Silencer**     | 1     | Suppresses the benign `Stream is closed` warnings native to the Hub's "Burst-and-Close" protocol. | `Enabled` |
| **Log Verbosity**             | 2     | Controls the amount of detail written to the log. Options: `ERROR`, `WARN`, `INFO`, `DEBUG`.      | `INFO`    |
| **Auto-Revert Debug**         | 2     | Automatically reverts Log Verbosity from `DEBUG` to `INFO` after 30 minutes.                      | `Enabled` |

***

## 📊 Parent Hub Attributes

| Attribute                | Type     | Phase | Description                                                                                                       | Automation? |
| ------------------------ | -------- | ----- | ----------------------------------------------------------------------------------------------------------------- | ----------- |
| **status**               | `ENUM`   | 1     | Primary health indicator (`online`, `idle`, `offline`). Filtered by the Silencer.                                 | Yes         |
| **connectionState**      | `ENUM`   | 1     | Granular connection status (`connected`, `connecting`, `idle`, `error`). Ideal for webCoRE event-driven recovery. | Yes         |
| **lastConnectionChange** | `string` | 1     | Timestamp of the last time `connectionState` changed. Helps identify when a failure occurred.                     | Yes         |
| **lastAction**           | `string` | 1     | A summary of the most recent internal driver event (e.g., "Moving Shade A01 to 50%").                             | Yes         |
| **lastCommand**          | `string` | 1     | Displays the raw ARC command sent to the Hub for technical verification.                                          | No          |
| **lastUpdate**           | `string` | 1     | A high-precision timestamp of the last successful data burst from the Hub.                                        | Yes         |
| **shades**               | `string` | 1     | A dynamic list of the motor IDs currently recognized by the Parent bridge (e.g., `A01, I39, C12`).                | Yes         |

***

## 🎮 Child Shade Commands

| Command                    | Phase | Description                                                                                             |
| -------------------------- | ----- | ------------------------------------------------------------------------------------------------------- |
| **Open / Close**           | 1     | Initiates standard movement to fully open (100%) or closed (0%).                                        |
| **Stop**                   | 1     | Immediately halts motor rotation at its current location. Critical "Emergency Stop".                    |
| **Set Position**           | 1     | Moves shade to a specific percentage (0–100).                                                           |
| **Set Level**              | 1     | Accepts `level` (0–100) and `duration` (seconds). The `duration` parameter is ignored.                  |
| **Initialize**             | 2     | Re-syncs the Child device with the Parent Hub. Use after Parent IP changes.                             |
| **Refresh**                | 1     | Forces a targeted poll for position only (`!IDr?`). Updates `position` and `windowShade`.               |
| **Toggle**                 | 1     | Reverses the current state (open ↔ close). Useful for single-button remotes.                            |
| **Request Battery Status** | 1     | Polls the Hub for battery level, voltage, and RSSI.                                                     |
| **Start Position Change**  | 1     | **Not supported by ARC.** Logs an informational message. Exists only for Hubitat capability compliance. |
| **Stop Position Change**   | 1     | **Not supported by ARC.** Logs an informational message. Exists only for Hubitat capability compliance. |

***

## ⚙️ Child Shade Preferences

| Preference                  | Phase | Description                                                                                                                                                      | Default   |
| --------------------------- | ----- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- |
| **Motor Address\***         | 1     | The unique 3-digit ARC identifier (e.g., `001`). **Mandatory.**                                                                                                  | _(None)_  |
| **Battery Offset**          | 1     | Adjusts reported battery percentage by a fixed amount (−30 to +30). Use for calibrating against the Rollease app.                                                | `0`       |
| **Position Tolerance**      | 2     | Allows the shade to be considered "confirmed" if it reaches within this percentage of the target. Increase if your shade consistently stops a few percent short. | `2`       |
| **Command Retry Count**     | 3     | Number of retries if a command fails to receive confirmation. Options: `0`, `1`, `2`, `3`.                                                                       | `2`       |
| **Command Retry Wait Time** | 3     | Seconds to wait before retrying. The wait time increases by 50% for each subsequent retry (Exponential Backoff).                                                 | `20`      |
| **Enable RF Jitter**        | 2     | If a command fails, send a harmless status request to wake the Hub's RF transmitter, then retry once more.                                                       | `Enabled` |
| **Log Verbosity**           | 2     | Controls the amount of detail written to the log. Options: `ERROR`, `WARN`, `INFO`, `DEBUG`.                                                                     | `INFO`    |
| **Auto-Revert Debug**       | 2     | Automatically reverts Log Verbosity from `DEBUG` to `INFO` after 30 minutes.                                                                                     | `Enabled` |

***

## 📊 Child Shade Attributes

| Attribute          | Type      | Phase | Description                                                                                                                                    | Automation? |
| ------------------ | --------- | ----- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| **windowShade**    | `ENUM`    | 1     | Primary status: `open`, `closed`, `opening`, `closing`, `partially open`.                                                                      | Yes         |
| **position**       | `int`     | 1     | Real-time integer value (0–100) of the shade's height.                                                                                         | Yes         |
| **level**          | `int`     | 1     | Numeric position (0–100) for "SwitchLevel" compatibility.                                                                                      | Yes         |
| **moving**         | `bool`    | 1     | Indicates if the motor is currently in active travel.                                                                                          | Yes         |
| **open**           | `bool`    | 1     | Binary state indicating if the shade has reached its upper limit.                                                                              | Yes         |
| **closed**         | `bool`    | 1     | Binary state indicating if the shade has reached its lower limit.                                                                              | Yes         |
| **switch**         | `ENUM`    | 1     | Mirrors the power-style state (`on`/`off`).                                                                                                    | Yes         |
| **battery**        | `int`     | 1     | Calculated charge percentage (0–100%), calibrated for a 9.5V–12.6V discharge curve.                                                            | Yes         |
| **batteryVoltage** | `decimal` | 1     | Decimal voltage reading (e.g., `12.04V`).                                                                                                      | Yes         |
| **voltage**        | `int`     | 1     | Raw voltage integer value (e.g., `1204` = 12.04V).                                                                                             | Yes         |
| **rssi**           | `string`  | 1     | Signal strength indicator (RF link quality, e.g., `R75`).                                                                                      | Yes         |
| **lastEvent**      | `ENUM`    | 4     | The most recent event for this shade (`idle`, `command_sent`, `position_confirmed`, `retry`, `jitter`, `failed`, `stopped`, `battery_report`). | Yes         |
| **lastUpdate**     | `string`  | 4     | Timestamp of the last successful data burst from the shade.                                                                                    | Yes         |

***

## 🛠️ Maintenance Notes

### Updating to a New Driver Version

1. **Save the new driver code** in Drivers Code for both Parent and Child.

2. Go to each device page.

3. Click **Initialize**.

4. Verify the `lastEvent` attribute populates on the next command.

### What to Do After a Hub Reboot

* The Parent Hub will automatically attempt to reconnect on boot. If the network is not ready, it will log an error and retry based on the `Connection Retry Interval`.

* The `refreshAllChildren` guard (v3.3.26) prevents the driver from blocking for 160 seconds if the network is down. It will skip the child refresh and wait for the next successful `initialize()`.

### What to Do When a Shade Fails to Move

* Check the `lastEvent` attribute. If it is `failed`, the driver exhausted all retries.

* Check the `lastUpdate` attribute. If the timestamp is old, the shade has gone completely deaf.

* **Action:** Power-cycle the nearest RF repeater for 10 seconds. Then try the command again.

### Using `lastEvent` for Automations

The `lastEvent` attribute allows you to build self-healing automations. For example:

* **Trigger:** `XDG's lastEvent` changes to `failed`.

* **Action:** Send a push notification. Power-cycle the repeater. Send the command again.

* **Trigger:** `XDG's lastUpdate` is older than 2 hours.

* **Action:** Send a "Battery Dead" notification.

***

## 📚 Related Documents

* **[README.md](https://./README.md)** – Project overview, key improvements, credits, and why the fork exists.

* **[CHANGELOG-Hub.md](https://./CHANGELOG-Hub.md)** – Full version history for the Parent Hub.

* **[CHANGELOG-Shade.md](https://./CHANGELOG-Shade.md)** – Full version history for the Child Shade.

* **[QuickStartInstallation\&Configuration.md](https://./QuickStartInstallation\&Configuration.md)** – Step-by-step guide for installing, configuring, and troubleshooting.

* **[RETR-Hub-Logic.md](https://./RETR-Hub-Logic.md)** – Technical deep-dive into the Telnet stability, error silencing, and Parent driver logic.

* **[RETR-Shade-Logic.md](https://./RETR-Shade-Logic.md)** – Technical deep-dive into the Smart Stateful Retry, position tolerance, event tracking, and RSSI reporting.

* **[RoadmapPhasedPlan.md](https://./RoadmapPhasedPlan.md)** – Project requirements and phased rollout history.

***

**Driver Version:** Hub v3.3.26 / Shade v2.5.9\
**Document Version:** 1.0.0\
**Date:** November 6, 2026

***

**Insuo Periculo** – Enter at your own risk. 🚀
