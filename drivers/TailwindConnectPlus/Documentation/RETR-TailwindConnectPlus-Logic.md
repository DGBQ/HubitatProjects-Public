# 🚪 Tailwind Connect Plus – Technical Specification (RETR-TailwindConnectPlus-Logic)

**File Name:** `RETR-TailwindConnectPlus-Logic.md`\
**Project Branch:** The Local Bridge\
**Integration Logic:** Parent/Child Architecture with Polling Watchdog and Timeout-Based Failure Detection\
**Document Status:** Final Production Specification\
**Driver Version:** Hub v1.3.2 / Child v1.1.3

***

## 1. Project Context & Technical Philosophy

### 1.1 The Problem: `toInteger()` Error

The original driver (`dbadge` / `Gelix`) used the following code in the `checkStatus()` method:

groovy

```
def checkStatus() {
    def params = [uri: "http://${IP}/status", headers: ['TOKEN': "${token}"]]
    httpGet(params) { resp ->
        if(debugEnable) log.debug "Door Status: ${resp.data}"
        return resp.data.toInteger()  // ❌ THIS LINE BREAKS
    }
}
```

**Why It Broke:**

Recent Hubitat platform updates changed how HTTP response data is handled internally. The response object (`resp.data`) no longer supports a static `.toInteger()` method call. This resulted in:

text

```
groovy.lang.MissingMethodException: No signature of method: static 
com.hubitat.hub.executor.helpers.ExecutorHttpClient5.toInteger() 
is applicable for argument types: () values: []
```

**Impact on Users:**

* The driver would fail on every poll attempt

* Door status would not update

* Child devices would not reflect correct state

* Automations relying on door status would not trigger

* Users would see repeated error messages in the logs

***

### 1.2 The Solution: Type Casting

The fix is simple but critical. Instead of calling `.toInteger()` directly on the response object, we first cast it to a String:

groovy

```
def checkStatus() {
    def params = [uri: "http://${IP}/status", headers: ['TOKEN': "${token}"]]
    httpGet(params) { resp ->
        if(debugEnable) logDebug "Door Status: ${resp.data}"
        return (resp.data as String).toInteger()  // ✅ FIXED
    }
}
```

**Why This Works:**

1. Casting `resp.data` to `String` forces the response to be treated as a string value.

2. The String class has a native `.toInteger()` method that works reliably.

3. This approach is compatible with all Hubitat platform versions.

The driver also includes a safety check in the `poll()` method:

groovy

```
def poll() {
    def s = checkStatus()
    def old = state.lastNumericStatus ?: 0  // ✅ Safe default from state
    if (s != old) {
        logDebug "Status changed from ${old} to ${s}"
        setDoorStatus(s)
    }
}
```

This ensures that if `checkStatus()` returns `null` for any reason, the driver won't crash.

***

### 1.3 Polling Watchdog & Initialize Robustness

To ensure the driver self-heals after network hiccups or Hubitat reboots, two features were implemented in Phase 2:

**Polling Watchdog (v1.1.0):**\
Every 15 minutes, the driver checks when it last polled the controller. If the last poll is older than `(poll interval + 5 minutes)`, it forces a recovery poll. This ensures the driver never silently stops updating.

**Initialize Robustness (v1.1.0):**\
On `initialize()`, the driver actively schedules polling and runs an immediate poll. This ensures the driver starts working automatically after a reboot or driver update.

***

### 1.4 Timeout-Based Failure Detection (v1.1.9)

An analysis of the controller's behavior showed that it always returns `0` as an acknowledgment, and the door always moves on the first attempt. Retry logic was causing unnecessary door cycles.

**The Solution:** Instead of retrying, the driver relies on a timeout. If the door doesn't reach the desired state within `garageDoorTimeout` (default: 60 seconds), it's marked as a failure (e.g., `driverHealth` = "failed"). This gives the user a clear signal that something is physically wrong (obstruction, sensor issue, etc.) without spamming the door with commands.

***

### 1.5 UI Cleanup & Finalization (v1.3.2)

Phase 4 focused on cleaning the parent UI and finalizing commands:

* **Hide Internal Commands:** Removed `childOpen` and `childClose` from the parent metadata. These commands are for internal use by child devices and were confusing to users.

* **Identify Rename:** Renamed `Identify Controller` back to `Identify` for consistency with other commands.

* **Identify Success Log:** Added `logInfo "Identify command completed successfully"` at INFO level.

* **LED Brightness Control (Removed):** Testing confirmed that the `/led` endpoint is not available on all controller models. The feature was removed to avoid confusion.

***

## 2. Full Command API (Functional Methods)

### 2.1 Parent Hub Commands

| Command            | Description                                                                                                                                                |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`Refresh`**      | Manual Sync: Triggers an immediate poll of the controller and updates the door status.                                                                     |
| **`Clear Debug`**  | Clears the debug buffer stored in the `rawDebug` attribute.                                                                                                |
| **`Identify`**     | Flashes the LED on the Tailwind controller. Useful for identifying which physical controller you're communicating with when you have multiple controllers. |
| **`Verify Setup`** | Tests your configuration and confirms the controller is reachable. Returns a detailed report.                                                              |

> **Note:** The legacy `poll` command was removed in v1.2.8. Use `refresh` instead.

> **Note:** The `childOpen` and `childClose` commands were removed from the parent UI in v1.2.9. They are for internal use by child devices and remain in the code.

***

### 2.2 Child Door Commands

| Command     | Description                                        |
| ----------- | -------------------------------------------------- |
| **`Open`**  | Initiates movement to fully open the garage door.  |
| **`Close`** | Initiates movement to fully close the garage door. |

***

## 3. User Preferences & Configuration

### 3.1 Parent Hub Preferences

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

### 3.2 Child Door Preferences

The Child driver does not have any user-configurable preferences. All configuration is managed by the Parent driver.

***

## 4. Current States (Dashboard Attributes)

### 4.1 Parent Hub Attributes

| Attribute           | Description                                                                                 |
| ------------------- | ------------------------------------------------------------------------------------------- |
| **`Status`**        | Human-readable status message (e.g., "All doors closed").                                   |
| **`driverVersion`** | The current driver version (e.g., `v1.3.2`).                                                |
| **`driverHealth`**  | Driver health status: `normal`, `failed`, `timeout`, `communication_error`.                 |
| **`lastError`**     | Most recent error message (or `None`).                                                      |
| **`lastFetch`**     | Timestamp of the last successful poll.                                                      |
| **`rawDebug`**      | Debug buffer showing detailed log entries (populated only when `Log Verbosity` is `DEBUG`). |

### 4.2 Child Door Attributes

| Attribute        | Description                                                                             |
| ---------------- | --------------------------------------------------------------------------------------- |
| **`contact`**    | Current contact sensor state: `open` or `closed`.                                       |
| **`door`**       | Current door state: `open` or `closed`.                                                 |
| **`lastOpen`**   | Timestamp of when the door last opened (formatted per the Date/Time Format preference). |
| **`lastClosed`** | Timestamp of when the door last closed (formatted per the Date/Time Format preference). |

***

## 5. Internal Logic & State Variables

### 5.1 Parent Hub State Variables

* **`lastNumericStatus`**: Tracks numeric status separately from the human-readable `Status` attribute.

* **`lastPollMs`**: Timestamp (in milliseconds) of the last successful poll.

* **`debugBuf`**: Buffer holding debug log entries.

### 5.2 Child Door State Variables

The Child driver does not use any state variables.

***

## 6. Logic Guardrails & Fail-Safes

### 6.1 Physical Safety

Garage doors are heavy machinery. The driver includes:

| Safety Feature           | Description                                                         |
| ------------------------ | ------------------------------------------------------------------- |
| **Timeout Protection**   | `garageDoorTimeout` (default 60 seconds) prevents infinite polling. |
| **Status Verification**  | Driver verifies door actually reached desired state.                |
| **No Automatic Closing** | Driver does not automatically close doors.                          |
| **Manual Control**       | Physical wall buttons and remotes still work independently.         |

### 6.2 Operational Safety

| Safety Feature           | Description                                                         |
| ------------------------ | ------------------------------------------------------------------- |
| **Failure Detection**    | Timeout-based failure detection for door operations.                |
| **State Caching**        | `state.lastNumericStatus` prevents unnecessary API calls.           |
| **Graceful Degradation** | If controller is unreachable, driver logs errors but doesn't crash. |
| **Safe Defaults**        | On init, status defaults to 0 (all closed).                         |
| **Error Logging**        | All errors are logged with appropriate severity levels.             |

***

## 7. Versioning & Lineage

* **Maintainer:** David Ball-Quenneville (DGBQ)

* **Lineage:** Maintained fork of the `dbadge` / `Gelix` original branch.

* **Current Driver Version:** Hub v1.3.2 / Child v1.1.3

* **Key Enhancements (v1.0.0 – v1.3.2):**

  * **Phase 1 (v1.0.0):** Fixed `toInteger()` error, added driver header, version tracking, `refresh` command, logging helpers.

  * **Phase 2 (v1.1.9):** Added error handling, `driverHealth`, `lastError`, `lastFetch`, Polling Watchdog, Initialize Robustness, Identify Command. Removed retry logic.

  * **Phase 3 (v1.2.7):** Added preference descriptions, command descriptions, `verifySetup()`, improved error messages, `lastOpen`/`lastClosed`, Date/Time Format, Door Status Change INFO Log.

  * **Phase 4 (v1.3.2):** Hid internal commands, renamed Identify, added Identify success log. Attempted and removed LED Brightness Control.

***

## 📝 Summary of Updates Applied to RETR-TailwindConnectPlus-Logic.md

**Version Control:**

* Bumped **Driver Version** to `v1.3.2` / `v1.1.3`.

* Updated **Document Status** to Final Production Specification.

**Section 1 – Project Context:**

* Replaced the Rollease-specific "Burst-and-Close" narrative with the Tailwind-specific `toInteger()` fix.

* Documented Polling Watchdog & Initialize Robustness.

* Documented Timeout-Based Failure Detection.

* Documented UI Cleanup & Finalization.

**Section 2 – Full Command API:**

* Separated Parent Hub commands and Child Door commands into distinct tables.

**Section 3 – User Preferences:**

* Separated Parent Hub preferences and Child Door preferences into distinct tables.

**Section 4 – Current States:**

* Separated Parent Hub attributes and Child Door attributes into distinct tables.

**Section 5 – Internal Logic & State Variables:**

* Separated Parent Hub state variables and Child Door state variables.

**Section 6 – Logic Guardrails & Fail-Safes:**

* Added physical and operational safety sections specific to garage doors.

**Section 7 – Versioning & Lineage:**

* Added a full version history for Phases 1-4.
