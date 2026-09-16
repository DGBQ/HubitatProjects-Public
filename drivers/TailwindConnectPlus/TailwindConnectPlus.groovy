/**
 *  Tailwind Connect Plus
 *  Type: Parent Driver
 *  Purpose: Provides local control for Tailwind Garage Door Controllers
 *           using the Tailwind Local API (firmware v9.95+).
 *
 *  Original code by: dbadge
 *  Modified and maintained by: David Ball-Quenneville
 *  Copyright 2026 David Ball-Quenneville
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Revision History:
 *  v1.3.2 - 2026-09-09 - David Ball-Quenneville
 *    - Removed LED Brightness Control (not supported on all controller models)
 *    - Removed "Controller Appearance" section (no longer needed)
 *    - Cleaned up preferences and metadata
 *    - Child driver remains at v1.1.3
 *  v1.3.1 - 2026-09-09 - David Ball-Quenneville
 *    - Added auto-apply for LED brightness when preferences are saved
 *  v1.3.0 - 2026-09-09 - David Ball-Quenneville
 *    - Phase 4: Added LED Brightness Control (removed in v1.3.2)
 *    - Added Identify success log
 *    - Fixed Identify button label
 *  v1.2.9 - 2026-09-09 - David Ball-Quenneville
 *    - Phase 4: Identify rename, description update, hide childOpen/childClose
 *  v1.2.7 - 2026-08-31 - David Ball-Quenneville
 *    - Phase 3: User Experience & Logging
 *  v1.1.9 - 2026-08-18 - David Ball-Quenneville
 *    - Phase 2: Reliability & Visibility
 *  v1.0.0 - 2026-08-12 - David Ball-Quenneville
 *    - Initial DGBQ Fork
 */

import groovy.transform.Field

@Field static final String DRIVER_VERSION = "v1.3.2"

// ============================================================
// LOGGING HELPERS
// ============================================================

private String getLogLevel() {
    def level = settings.logVerbosity ?: "INFO"
    return level.toString().toUpperCase()
}

private void logDebug(String msg) {
    if (getLogLevel() in ["DEBUG"]) log.debug "${device.label}: ${msg}"
    if (getLogLevel() in ["DEBUG"]) debugAdd(msg)
}

private void logInfo(String msg) {
    if (getLogLevel() in ["DEBUG","INFO"]) log.info "${device.label}: ${msg}"
}

private void logWarn(String msg) {
    if (getLogLevel() in ["DEBUG","INFO","WARN"]) log.warn "${device.label}: ${msg}"
}

private void logError(String msg) {
    log.error "${device.label}: ${msg}"
}

private void debugAdd(String msg) {
    if (getLogLevel() != "DEBUG") return
    String stamp = new Date().format("MM-dd HH:mm:ss")
    String line = "${stamp} ${msg}"
    String buf = (state.debugBuf instanceof String) ? (state.debugBuf as String) : ""
    buf = buf ? (buf + "\n" + line) : line
    Integer max = safeInt(debugMaxChars, 6000)
    if (buf.length() > max) buf = buf.substring(buf.length() - max)
    state.debugBuf = buf
    sendEvent(name: "rawDebug", value: buf)
}

private Integer safeInt(v, Integer defVal) {
    try { return v ? v.toInteger() : defVal } catch (e) { return defVal }
}

// ============================================================
// DATE/TIME FORMATTING
// ============================================================

private String formatTimestamp(Date date) {
    if (date == null) return "Never"
    def format = settings.dateTimeFormat ?: "MM/DD/YYYY AM/PM"
    switch(format) {
        case "MM/DD/YYYY AM/PM":
            return date.format("MM/dd/yyyy hh:mm:ss a")
        case "YYYY-MM-DD (24hour)":
            return date.format("yyyy-MM-dd HH:mm:ss")
        case "YYYY-MM-DD AM/PM":
            return date.format("yyyy-MM-dd hh:mm:ss a")
        case "MM/DD/YYYY (24hour)":
            return date.format("MM/dd/yyyy HH:mm:ss")
        case "DD/MM/YYYY (24hour)":
            return date.format("dd/MM/yyyy HH:mm:ss")
        case "DD/MM/YYYY AM/PM":
            return date.format("dd/MM/yyyy hh:mm:ss a")
        case "Mon DD YYYY hh:MM AM/PM":
            return date.format("MMM dd yyyy hh:mm:ss a")
        case "Day, Mon DD, YYYY hh:MM AM/PM":
            return date.format("EEE, MMM dd, yyyy hh:mm:ss a")
        default:
            return date.format("MM/dd/yyyy hh:mm:ss a")
    }
}

// ============================================================
// METADATA
// ============================================================

metadata {
    definition(
        name: "Tailwind Connect Plus",
        namespace: "DGBQ",
        author: "David Ball-Quenneville",
        version: DRIVER_VERSION,
        importUrl: "https://raw.githubusercontent.com/YourGitHub/hubitat-tailwind-connect-plus/main/tailwind-connect-plus.groovy"
    ) {
        capability "Polling"
        attribute "Status", "string"
        attribute "driverVersion", "string"
        attribute "driverHealth", "string"
        attribute "lastError", "string"
        attribute "lastFetch", "string"
        attribute "rawDebug", "string"

        // Commands visible to users
        command "refresh", [[name: "Refresh", description: "Manual Sync: Triggers an immediate poll of the controller and updates the door status."]]
        command "clearDebug", [[name: "Clear Debug", description: "Clears the debug buffer stored in the rawDebug attribute."]]
        command "identify", [[name: "Identify", description: "Flashes the LED on the Tailwind controller. Useful for identifying which physical controller you're communicating with when you have multiple controllers."]]
        command "verifySetup", [[name: "Verify Setup", description: "Tests your configuration and confirms the controller is reachable. Returns a detailed report."]]

        // NOTE: childOpen and childClose are NOT in metadata (hidden from UI)
        // They remain in the code for internal use by child devices
    }
}

// ============================================================
// PREFERENCES WITH DESCRIPTIONS
// ============================================================

preferences {
    section("<b>Controller Configuration</b>") {
        input name: "IP", type: "string", title: "Tailwind Controller IP",
              description: '<em>Required</em> The local IP address of your Tailwind controller. Example: 192.168.1.100',
              required: true
        input name: "token", type: "password", title: "Local Command Key",
              description: '<em>Required</em> Generated from web.gotailwind.com → Local Control Key. This is per-account and shared across all devices.',
              required: true
        input name: "cName", type: "string", title: "Controller Display Name",
              description: 'Name used for the controller in dashboards. Affects child device IDs. Changing this will re-create children. (Default: Home Garage)',
              required: true
    }

    section("<b>Door Configuration</b>") {
        input name: "doorCount", type: "number", title: "Number of Doors",
              description: 'How many doors are connected to this controller. Valid range: 0-3. (Default: 1)',
              required: true, range: "0..3", defaultValue: 1
        if (doorCount > 0) input name: "d1Name", type: "string", title: "Door 1 Name",
              description: 'Name displayed for Door 1 in dashboards. (Default: Door 1)',
              defaultValue: "Door 1"
        if (doorCount > 1) input name: "d2Name", type: "string", title: "Door 2 Name",
              description: 'Name displayed for Door 2 in dashboards. (Default: Door 2)',
              defaultValue: "Door 2"
        if (doorCount > 2) input name: "d3Name", type: "string", title: "Door 3 Name",
              description: 'Name displayed for Door 3 in dashboards. (Default: Door 3)',
              defaultValue: "Door 3"
    }

    section("<b>Polling Settings</b>") {
        input name: "interval", type: "enum", title: "Polling Interval (minutes)",
              description: 'How often the driver checks door status. (Default: 1 minute)',
              required: true, options: ["1","5","10","15","30"], defaultValue: "1"
        input name: "fastPollInterval", type: "number", title: "Fast Polling Interval (seconds)",
              description: 'How often to check status after a door command. Lower = faster response but more network traffic. (Default: 2 seconds)',
              required: true, range: "1-30", defaultValue: 2
        input name: "garageDoorTimeout", type: "number", title: "Door Operation Timeout (seconds)",
              description: 'Maximum time to wait for door to change state. If exceeded, mark as failure. (Default: 60 seconds)',
              required: true, defaultValue: 60
    }

    section("<b>Display Settings</b>") {
        input name: "dateTimeFormat", type: "enum", title: "Date/Time Format",
              description: 'Select how date and time values appear in Current States and logs.',
              defaultValue: "MM/DD/YYYY AM/PM",
              options: [
                  "MM/DD/YYYY AM/PM",
                  "YYYY-MM-DD (24hour)",
                  "YYYY-MM-DD AM/PM",
                  "MM/DD/YYYY (24hour)",
                  "DD/MM/YYYY (24hour)",
                  "DD/MM/YYYY AM/PM",
                  "Mon DD YYYY hh:MM AM/PM",
                  "Day, Mon DD, YYYY hh:MM AM/PM"
              ]
    }

    section("<b>Logging & Debugging</b>") {
        input name: "logVerbosity", type: "enum", title: "Log Verbosity",
              description: 'Controls how much detail is written to the logs. ERROR shows only critical issues, DEBUG shows everything. (Default: INFO)',
              defaultValue: "INFO", options: ["ERROR","WARN","INFO","DEBUG"], required: true
        input name: "autoRevertDebug", type: "bool", title: "Auto-Revert Debug",
              description: 'When enabled, automatically reverts Log Verbosity from DEBUG to INFO after 30 minutes. (Default: ON)',
              defaultValue: true
        input name: "debugMaxChars", type: "number", title: "Max debug buffer chars",
              description: 'Maximum characters stored in the rawDebug attribute. (Default: 6000)',
              defaultValue: 6000
    }
}

// ============================================================
// LIFECYCLE METHODS
// ============================================================

def installed() {
    sendEvent(name: "driverVersion", value: DRIVER_VERSION, displayed: true)
    sendEvent(name: "driverHealth", value: "initializing")
    sendEvent(name: "lastError", value: "None")
    sendEvent(name: "lastFetch", value: "Never")
    state.lastNumericStatus = 0
    initialize()
}

def uninstalled() {
    getChildDevices().each { deleteChildDevice("${it.deviceNetworkId}") }
}

def updated() {
    if (settings.logVerbosity == "DEBUG" && settings.autoRevertDebug == true) {
        runIn(1800, "revertDebug")
    }
    initialize()
}

def initialize() {
    logInfo "Initializing driver v${DRIVER_VERSION}"
    unschedule()
    state.lastPollMs = null
    state.lastNumericStatus = 0
    addChildren()
    sendEvent(name: "Status", value: getStatusMessage(0))
    sendEvent(name: "driverHealth", value: "initializing")
    schedulePolling()
    scheduleWatchdog()
    poll()
}

def revertDebug() {
    if (settings.logVerbosity == "DEBUG" && settings.autoRevertDebug == true) {
        logInfo "Debug logging auto-reverted after 30 minutes"
        device.updateSetting("logVerbosity", [value: "INFO", type: "enum"])
    }
}

// ============================================================
// WATCHDOG – Self-healing polling timer
// ============================================================

private void scheduleWatchdog() {
    unschedule("watchdog")
    runIn(900, "watchdog")  // 15 minutes
}

def watchdog() {
    Long lastPoll = state.lastPollMs instanceof Long ? state.lastPollMs as Long : 0L
    Long nowMs = now()
    Long maxAge = (safeInt(interval, 1).toInteger() * 60 + 300) * 1000L

    if (lastPoll > 0 && (nowMs - lastPoll > maxAge)) {
        logWarn "Polling chain broken (last poll ${(nowMs - lastPoll)/60000} min ago) – forcing recovery"
        sendEvent(name: "lastError", value: "Watchdog: broken chain detected")
        sendEvent(name: "driverHealth", value: "recovering")
        poll([force: true, reason: "watchdog"])
        logInfo "Polling chain recovered by watchdog"
        sendEvent(name: "lastError", value: "None")
        sendEvent(name: "driverHealth", value: "normal")
    } else if (lastPoll > 0) {
        logDebug "Watchdog: Polling healthy (${(nowMs - lastPoll)/60000} min since last poll)"
    } else {
        logWarn "Watchdog: No lastPollMs – forcing initial poll"
        poll([force: true, reason: "watchdog"])
        logInfo "Initial poll triggered by watchdog"
    }

    scheduleWatchdog()
}

// ============================================================
// COMMAND HANDLERS
// ============================================================

def refresh() {
    logInfo "Manual refresh triggered"
    poll([force: true, reason: "manual"])
}

def clearDebug() {
    state.debugBuf = ""
    sendEvent(name: "rawDebug", value: "")
    logInfo "Debug buffer cleared"
}

def identify() {
    logInfo "Identify command sent – flashing controller LED"
    def params = [uri: "http://${IP}/identify", headers: ['TOKEN': "${token}"]]
    try {
        httpPost(params) { resp ->
            logDebug "Identify response: ${resp.data}"
            logInfo "Identify command completed successfully"
            sendEvent(name: "lastError", value: "None")
            sendEvent(name: "driverHealth", value: "normal")
        }
    } catch (Exception e) {
        logWarn "Identify command not supported by controller: ${e.message}"
    }
}

def verifySetup() {
    logInfo "Running setup verification..."
    def errors = []
    def warnings = []

    if (!settings.IP || settings.IP.trim().isEmpty()) {
        errors.add("IP address is not configured. Please set the Tailwind Controller IP in preferences.")
    } else {
        logDebug "IP configured: ${settings.IP}"
    }

    if (!settings.token || settings.token.trim().isEmpty()) {
        errors.add("Local Command Key is not configured. Please set it in preferences.")
    } else {
        logDebug "Token configured: ${settings.token}"
    }

    if (!settings.cName || settings.cName.trim().isEmpty()) {
        errors.add("Controller Display Name is not configured. Please set it in preferences.")
    } else {
        logDebug "Controller Name: ${settings.cName}"
    }

    if (!settings.doorCount || settings.doorCount.toInteger() < 1 || settings.doorCount.toInteger() > 3) {
        errors.add("Number of Doors must be between 1 and 3. Current value: ${settings.doorCount}")
    } else {
        logDebug "Door Count: ${settings.doorCount}"
    }

    def childCount = getChildDevices().size()
    if (childCount != settings.doorCount.toInteger()) {
        warnings.add("Expected ${settings.doorCount} child devices, found ${childCount}. Child devices may need to be created or removed.")
    } else {
        logDebug "Child devices: ${childCount} (matches door count)"
    }

    try {
        def params = [uri: "http://${IP}/status", headers: ['TOKEN': "${token}"]]
        httpGet(params) { resp ->
            logInfo "Controller reachable. Status response: ${resp.data}"
            sendEvent(name: "lastError", value: "None")
            sendEvent(name: "driverHealth", value: "normal")
        }
    } catch (Exception e) {
        errors.add("Controller is not reachable at ${IP}. Please verify the IP address and that the controller is powered on.")
        logError "Controller unreachable: ${e.message}"
        sendEvent(name: "lastError", value: "Setup verification failed: ${e.message}")
        sendEvent(name: "driverHealth", value: "communication_error")
    }

    if (errors.isEmpty() && warnings.isEmpty()) {
        logInfo "✅ Setup verification PASSED – all checks successful"
        sendEvent(name: "lastError", value: "None")
        sendEvent(name: "driverHealth", value: "normal")
        return "✅ Setup verification PASSED – all checks successful"
    }

    def msg = "⚠️ Setup verification completed with issues:"
    if (!errors.isEmpty()) {
        msg += "\n\n❌ ERRORS:\n- " + errors.join("\n- ")
    }
    if (!warnings.isEmpty()) {
        msg += "\n\n⚠️ WARNINGS:\n- " + warnings.join("\n- ")
    }

    logWarn msg
    sendEvent(name: "lastError", value: "Setup verification failed")
    return msg
}

def poll(Map data = null) {
    def s = checkStatus()
    if (s != null) {
        def old = state.lastNumericStatus ?: 0
        if (s != old) {
            logInfo "Door status changed: ${getStatusMessage(old)} → ${getStatusMessage(s)}"
            setDoorStatus(s)
        }
        state.lastPollMs = now()
        sendEvent(name: "lastFetch", value: formatTimestamp(new Date()))
        sendEvent(name: "lastError", value: "None")
        sendEvent(name: "driverHealth", value: "normal")
        sendEvent(name: "Status", value: getStatusMessage(s))
        state.lastNumericStatus = s
    }
}

def schedulePolling() {
    if (settings.interval == "1") runEvery1Minute(poll)
    else if (settings.interval == "5") runEvery5Minutes(poll)
    else if (settings.interval == "10") runEvery10Minutes(poll)
    else if (settings.interval == "15") runEvery15Minutes(poll)
    else if (settings.interval == "30") runEvery30Minutes(poll)
}

// ============================================================
// DOOR OPERATIONS
// ============================================================

def childOpen(Integer doorNumber) {
    open(doorNumber)
}

def childClose(Integer doorNumber) {
    close(doorNumber)
}

def open(Integer doorNumber) {
    openClose("open", doorNumber)
}

def close(Integer doorNumber) {
    openClose("close", doorNumber)
}

def openClose(String command, Integer doorNumber) {
    def desiredStatus = "closed"
    def Integer cmd = doorNumber * -1
    if (doorNumber == 3) cmd = cmd - 1
    if (command == "open") {
        desiredStatus = "open"
        cmd = cmd * -1
    }

    logInfo "Attempting to ${command} door ${doorNumber}"
    def postParams = [uri: "http://${IP}/cmd", body: "${cmd}", headers: ['TOKEN': "${token}"]]
    logDebug "${postParams}"

    try {
        httpPost(postParams) { resp ->
            def actualResp = "${resp.data}"
            logDebug "${command} Response: ${actualResp}"
            runIn(1, "postActionRefresh", [data: ["desiredStatus": "${desiredStatus}", "doorNumber": doorNumber]])
            sendEvent(name: "lastError", value: "None")
            sendEvent(name: "driverHealth", value: "normal")
        }
    } catch (Exception e) {
        logWarn "Command ${command} door ${doorNumber}: ${e.message} (continuing with fast polling)"
        sendEvent(name: "lastError", value: "Command sent, but communication error: ${e.message}")
        sendEvent(name: "driverHealth", value: "command_sent")
        runIn(1, "postActionRefresh", [data: ["desiredStatus": "${desiredStatus}", "doorNumber": doorNumber]])
    }
}

// ============================================================
// FAST POLLING AFTER DOOR COMMAND - WITH TIMEOUT
// ============================================================

void postActionRefresh(data) {
    def Integer loopSpeed = safeInt(fastPollInterval, 2)
    String desiredStatus = data.get("desiredStatus")
    Integer doorNumber = data.get("doorNumber").toInteger()

    logDebug "Now polling every ${loopSpeed} seconds for door ${doorNumber} to ${desiredStatus}"

    def Integer elapsedSeconds = 0
    def Integer currentStatus = checkStatus()
    def String currentDoorState = doorCheck(doorNumber, currentStatus)

    logDebug "Door #${doorNumber} Desired: ${desiredStatus} | Current: ${currentDoorState}"

    while (currentDoorState != desiredStatus) {
        pauseExecution(loopSpeed * 1000)
        elapsedSeconds += loopSpeed

        if (elapsedSeconds >= safeInt(garageDoorTimeout, 60)) {
            logWarn "Door #${doorNumber} FAILED to ${desiredStatus} - timeout after ${elapsedSeconds} seconds"
            sendEvent(name: "lastError", value: "Door ${doorNumber} failed to ${desiredStatus} - timeout. Check for obstructions, sensor issues, or mechanical problems.")
            sendEvent(name: "driverHealth", value: "failed")
            currentStatus = checkStatus()
            setDoorStatus(currentStatus)
            return
        }

        currentStatus = checkStatus()
        currentDoorState = doorCheck(doorNumber, currentStatus)
        logDebug "Door #${doorNumber} Desired: ${desiredStatus} | Current: ${currentDoorState} (${elapsedSeconds}s elapsed)"
    }

    logInfo "Door #${doorNumber} ${desiredStatus} SUCCESS after ${elapsedSeconds} seconds"
    currentStatus = checkStatus()
    setDoorStatus(currentStatus)
    sendEvent(name: "lastError", value: "None")
    sendEvent(name: "driverHealth", value: "normal")
}

// ============================================================
// CHILD DEVICE MANAGEMENT
// ============================================================

void addChildren() {
    int dc = doorCount.toString().toInteger()
    getChildDevices().each {
        def spl = it.deviceNetworkId.split(':')
        if (spl[0] != cName || spl[1].toInteger() > dc) {
            logDebug "delete ${it.deviceNetworkId}"
            deleteChildDevice("${it.deviceNetworkId}")
        }
    }
    for (int c = 0; c < dc; c++) {
        def d = c + 1
        def dn = ""
        if (d == 1) dn = d1Name
        if (d == 2) dn = d2Name
        if (d == 3) dn = d3Name
        logDebug ("${cName}:${d}")
        def cd = getChildDevice("${cName}:${d}")
        if (!cd) {
            cd = addChildDevice("DGBQ", "Tailwind Connect Plus - Child", "${cName}:${d}", [label: "${cName} : ${dn}", name: "${d}", isComponent: true])
            if (cd) {
                logDebug "Child device ${cd.displayName} was created"
            } else if (!cd) {
                logError "Could not create child device"
            }
        }
        logDebug "deviceNetworkId ${cd.deviceNetworkId}=${cName}:${d} name ${cd.name}=${d} label ${cd.label}=${cName} : ${dn}"
        if (cd.label != "${cName} : ${dn}") {
            logDebug "Correcting child label mismatch ${cd.label}=${cName} : ${dn}"
            cd.label = "${cName} : ${dn}"
        }
        if (cd.name != "${d}") {
            logDebug "Correcting child name mismatch ${cd.name}=${d}"
            cd.name = "${d}"
        }
    }
}

void setChildStatus(dNum, status) {
    def cd = getChildDevice("${cName}:${dNum}")
    def currentStatus = cd.latestValue("door")
    if (currentStatus == status) {
        logDebug "Child device ${cName}:${dNum} matches real door (${status})"
    } else {
        logDebug "Syncing child device ${cName}:${dNum} from ${currentStatus} to ${status}"
        cd.sendEvent(name: "door", value: "${status}")

        def now = new Date()
        def timestamp = formatTimestamp(now)
        if (status == "open") {
            cd.sendEvent(name: "lastOpen", value: timestamp)
        } else if (status == "closed") {
            cd.sendEvent(name: "lastClosed", value: timestamp)
        }
    }
    if (status ==~ /open|closed/ && cd.latestValue('contact') != status) {
        cd.sendEvent(name: 'contact', value: "${status}")
    }
}

// ============================================================
// API CALLS
// ============================================================

def checkStatus() {
    def params = [uri: "http://${IP}/status", headers: ['TOKEN': "${token}"]]
    try {
        def result = null
        httpGet(params) { resp ->
            logDebug "Door Status: ${resp.data}"
            result = (resp.data as String).toInteger()
        }
        if (result != null) {
            return result
        }
    } catch (Exception e) {
        logError "Failed to check status: ${e.message}"
        sendEvent(name: "lastError", value: "Status check failed: ${e.message}")
        sendEvent(name: "driverHealth", value: "communication_error")
    }
    return state.lastNumericStatus ?: 0
}

def doorCheck(Integer doorNumber, Integer doorStatus) {
    checkNumber = doorNumber - 1
    r = getDoorOpenClose(getDoorStatus(doorStatus, checkNumber))
    return r
}

void setDoorStatus(status) {
    logDebug "Setting Door Status attribute to ${status}"
    state.lastNumericStatus = status
    sendEvent(name: "Status", value: getStatusMessage(status))
    for (int i = 0; i < doorCount.toInteger(); i++) {
        ds = getDoorStatus(status, i)
        dStatus = getDoorOpenClose(ds)
        logDebug "Real door ${i+1} is ${ds} ${dStatus}"
        setChildStatus(i + 1, dStatus)
    }
}

def getStatusMessage(Integer status) {
    if (status == null) return "Unknown"
    def messages = [
        (-1): "All doors closed",
        (1): "Door 1 open, others closed",
        (-3): "Door 1 closed, Door 2 open",
        (3): "Door 1 open, Door 2 open",
        (-5): "Door 1 closed, Door 2 closed, Door 3 open",
        (5): "Door 1 open, Door 2 closed, Door 3 open",
        (-7): "Door 1 closed, Door 2 open, Door 3 open",
        (7): "All doors open"
    ]
    return messages[status] ?: "Status: ${status}"
}

// ============================================================
// UTILITY METHODS
// ============================================================

def getDoorStatus(Integer status, Integer door) {
    def statusCodes = [
        [-1, -2, -4],
        [1, -2, -4],
        [-1, 2, -4],
        [1, 2, -4],
        [-1, -2, 4],
        [1, -2, 4],
        [-1, 2, 4],
        [1, 2, 4]
    ]
    return statusCodes[status][door]
}

def getDoorOpenClose(Integer curStatus) {
    if (curStatus < 0) {
        return "closed"
    } else if (curStatus > 0) {
        return "open"
    } else {
        return "unknown"
    }
}