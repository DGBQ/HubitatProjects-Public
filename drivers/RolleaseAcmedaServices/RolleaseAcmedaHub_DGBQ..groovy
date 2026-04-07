/*
 * Name: Rollease Acmeda Hub
 * Type: Driver
 * Purpose: Parent driver for Rollease Acmeda Pulse 2 Hub. Manages telnet communication,
 *          child device discovery, and provides surgical error handling for "Stream is closed".
 *          Includes shade management commands (discover, add, remove) and a shade list state.
 *
 * Author: David Ball-Quenneville (maintainer, based on Younes Oughla previous work)
 * Copyright 2026 David Ball-Quenneville
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision History:
 * v3.3.12 - 2026-04-07 - David Ball-Quenneville
 *   - Fixed preference access in logging helpers (now uses settings. prefix) to ensure reliable INFO logs.
 *
 * v3.3.11 - 2026-04-06 - David Ball-Quenneville
 *   - Added Auto-Revert Debug preference.
 *   - Removed redundant custom commands SendMsg and SendPulse.
 *
 * v3.3.10 - 2026-03-20 - David Ball-Quenneville
 * Note: Full revision history available in RolleaseAcmedaHub-DGBQ_CHANGELOG.md
 */

metadata {
    definition (
        name: "Rollease Acmeda Hub",
        namespace: "DGBQ",
        author: "David Ball-Quenneville (based on Younes Oughla previous work)",
        version: "3.3.12",
        vid: "generic-shade",
        importUrl: ""
    ) {
        capability "Initialize"
        capability "Refresh"
        capability "Configuration"
        capability "Telnet"

        command "ShadeDiscover"
        command "ShadeAdd", ["string"]
        command "ShadeRemove", ["string"]
        command "ShadeDeleteAll"
        command "sendTelnetCommand", ["string"]

        attribute "status", "enum", ["online", "idle", "offline"]
        attribute "lastCommand", "string"
        attribute "lastAction", "string"
        attribute "lastUpdate", "string"
        attribute "shades", "string"
    }

    preferences {
        input name: "hubAddress", type: "text", title: "Hub Address", description: "IP Address of the Hub", defaultValue: "", required: true, displayDuringSetup: true
        input name: "hubPort", type: "text", title: "Hub Port", description: "Telnet port of the Pulse 2 Hub", defaultValue: "1487", required: true, displayDuringSetup: true
        input name: "connectionRetryInterval", type: "number", title: "Connection Retry Interval", description: "Number of seconds to wait before re-attempting the connection. 0=Do Not Retry", defaultValue: 300, required: false, displayDuringSetup: false
        input name: "maxIdleTime", type: "number", title: "Maximum Idle Time", description: "Reset connection to hub if no status reports are received within this time (Seconds). 0=Disabled", defaultValue: 3600, required: false, displayDuringSetup: false
        input name: "enableSilencer", type: "bool", title: "Enable Error Silencer", description: "Intercept 'Stream is closed' and log as INFO instead of ERROR", defaultValue: true, required: false, displayDuringSetup: false
        input name: "logEnable", type: "bool", title: "Enable Debug Logging", description: "Detailed logs; auto‑off after 30 mins (unless Auto-Revert is disabled)", defaultValue: false, required: false, displayDuringSetup: false
        input name: "autoRevertDebug", type: "bool", title: "Auto-Revert Debug", description: "When enabled, automatically turns off debug logging after 30 minutes. When disabled, debug stays on until manually turned off.", defaultValue: true, required: false, displayDuringSetup: false
        input name: "txtEnable", type: "bool", title: "Enable Description Logging", description: "Human‑readable activity logs", defaultValue: true, required: false, displayDuringSetup: false
    }
}

def installed() {
    initialize()
}

def updated() {
    if (!settings.autoRevertDebug) {
        unschedule("logsOff")
    }
    if (!settings.logEnable) unschedule("logsOff")
    initialize()
}

def initialize() {
    unschedule("checkIdle")
    unschedule()

    telnetClose()

    def port = (settings.hubPort ?: "1487").toString()
    if (!port.isInteger() || port.toInteger() <= 0) {
        logError "Invalid hubPort: ${port}. Using default 1487."
        port = "1487"
    }

    logInfo "Opening telnet connection to ${settings.hubAddress}:${port}"
    telnetConnect([termChars:[59]], settings.hubAddress, port.toInteger(), null, null)
    startMaxIdleTimer()

    if (settings.logEnable && settings.autoRevertDebug) {
        runIn(1800, "logsOff")
        logDebug "Debug logging will auto-disable after 30 minutes (Auto-Revert enabled)"
    } else if (settings.logEnable && !settings.autoRevertDebug) {
        logDebug "Debug logging will remain on (Auto-Revert disabled)"
    }

    sendEvent(name: "status", value: "online")
    rebuildShadeList()
    updateShadesAttribute()
}

def rebuildShadeList() {
    def ids = []
    getChildDevices().each { child ->
        def motorId = child.settings?.motorAddress
        if (motorId == null) {
            def parts = child.deviceNetworkId.split("-")
            if (parts.size() > 1) motorId = parts[-1]
        }
        if (motorId) {
            ids << motorId
            logDebug "Found child: ${child.displayName}, motor ID: ${motorId}"
        } else {
            logDebug "Could not determine motor ID for child: ${child.displayName}, networkId: ${child.deviceNetworkId}"
        }
    }
    state.shadeList = ids
    logDebug "Shade list rebuilt: ${state.shadeList}"
}

def updateShadesAttribute() {
    def list = state.shadeList ?: []
    sendEvent(name: "shades", value: list.join(", "))
}

def sendTelnetCommand(String commandString) {
    logDebug "Sending Telnet Command: ${commandString}"
    sendEvent(name: "lastCommand", value: commandString)
    return new hubitat.device.HubAction(commandString, hubitat.device.Protocol.TELNET)
}

def telnetStatus(String status) {
    if (settings.enableSilencer && (status.contains("Stream is closed") || status.contains("receive error"))) {
        logInfo "Hub finished transmission and disconnected (Normal Behavior)."
        sendEvent(name: "status", value: "idle")
        return
    }

    logError "telnetStatus: error: " + status
    sendEvent(name: "status", value: "offline")

    if (settings.connectionRetryInterval > 1) {
        logInfo "Will try to reconnect in ${settings.connectionRetryInterval} seconds"
        runIn(settings.connectionRetryInterval, initialize)
    }
}

private parse(String msg) {
    logDebug "Event Received: ${msg}"
    sendEvent(name: "lastUpdate", value: new Date().format("yyyy-MM-dd HH:mm:ss"))

    motorAddress = msg[1..3]
    lastThree = msg[-3..-1]

    if (motorAddress == "EUC" || motorAddress == "BR1" || lastThree == "Enp") return

    String thisId = device.deviceNetworkId
    def cd = getChildDevice("${thisId}-${motorAddress}")

    if (!cd) {
        logInfo "Found New Shade: " + motorAddress
        cd = addChildDevice("DGBQ", "Rollease Acmeda Shade", "${thisId}-${motorAddress}", [name: "Rollease Acmeda Shade - ${motorAddress}", isComponent: false])
        cd.updateSetting("motorAddress", [type:"STRING", value:motorAddress])
        if (!state.shadeList) state.shadeList = []
        if (!state.shadeList.contains(motorAddress)) {
            state.shadeList << motorAddress
            updateShadesAttribute()
            logDebug "Added ${motorAddress} to shadeList"
        }
    }

    cd.parse(msg)
    startMaxIdleTimer()
    updateLastAction(cd, msg)
}

private def updateLastAction(child, String msg) {
    String name = child.displayName
    String action = "Reported"
    if (msg.contains("m")) {
        def posMatch = (msg =~ /m(\d{3})/)
        if (posMatch) {
            int pos = 100 - Integer.parseInt(posMatch[0][1])
            action = "Moving to ${pos}%"
        } else action = "Moving"
    } else if (msg.contains("r")) {
        def posMatch = (msg =~ /r(\d{3})/)
        if (posMatch) {
            int pos = 100 - Integer.parseInt(posMatch[0][1])
            action = "Position verified at ${pos}%"
        } else action = "Position Verified"
    } else if (msg.contains("s")) {
        action = "Stopped"
    }
    sendEvent(name: "lastAction", value: "${name}: ${action}")
}

private def startMaxIdleTimer() {
    if (settings.maxIdleTime > 0) {
        logDebug "Will reset connection if no events received in ${settings.maxIdleTime} seconds"
        runIn(settings.maxIdleTime, "resetConnection")
    }
}

def configure() {
    logInfo "Scanning for shades..."
    sendTelnetCommand "!000v?"
}

def refresh() {}

private def resetConnection() {
    logInfo "Resetting Connection..."
    initialize()
}

def logsOff() {
    if (settings.logEnable) {
        log.warn "Debug logging auto-disabled (Auto-Revert was enabled)"
        device.updateSetting("logEnable", [value: "false", type: "bool"])
    }
}

def ShadeDeleteAll() {
    logWarning "Deleting all child devices..."
    getChildDevices().each { child ->
        String motorId = child.settings?.motorAddress
        if (motorId == null) {
            def parts = child.deviceNetworkId.split("-")
            motorId = parts.size() > 1 ? parts[-1] : "unknown"
        }
        try {
            deleteChildDevice(child.deviceNetworkId)
            logInfo "Deleted child device: ${child.displayName} (ID: ${motorId})"
        } catch (e) {
            logError "Failed to delete child device ${child.displayName}: ${e.message}"
        }
    }
    state.shadeList = []
    updateShadesAttribute()
    logInfo "All child devices deleted."
}

def ShadeDiscover() {
    logInfo "Discovering shades..."
    sendTelnetCommand("!000v?")
    runIn(5, "reportDiscoveryResults")
}

def reportDiscoveryResults() {
    def currentList = state.shadeList ?: []
    if (currentList.isEmpty()) logInfo "No shades found or hub did not respond."
    else logInfo "Shades present: ${currentList.join(', ')}"
}

def ShadeAdd(String motorId) {
    if (!(motorId ==~ /[A-Z0-9]{3}/)) {
        logError "Invalid motor ID: ${motorId}. Must be 3 uppercase letters or digits."
        return
    }
    if (getChildDevice("${device.deviceNetworkId}-${motorId}")) {
        logWarning "Shade ${motorId} already exists."
        return
    }
    logInfo "Manually adding shade: ${motorId}"
    try {
        def child = addChildDevice("DGBQ", "Rollease Acmeda Shade", "${device.deviceNetworkId}-${motorId}", [
            name: "Rollease Acmeda Shade - ${motorId}",
            isComponent: false
        ])
        child.updateSetting("motorAddress", [type:"STRING", value:motorId])
        if (!state.shadeList) state.shadeList = []
        state.shadeList << motorId
        updateShadesAttribute()
        logInfo "Shade ${motorId} created."
    } catch (e) {
        logError "Failed to create shade ${motorId}: ${e.message}"
    }
}

def ShadeRemove(String motorId) {
    if (!(motorId ==~ /[A-Z0-9]{3}/)) {
        logError "Invalid motor ID: ${motorId}. Must be 3 uppercase letters or digits."
        return
    }
    def child = getChildDevice("${device.deviceNetworkId}-${motorId}")
    if (!child) {
        logWarning "Shade ${motorId} does not exist."
        return
    }
    logInfo "Manually removing shade: ${motorId}"
    try {
        deleteChildDevice(child.deviceNetworkId)
        if (state.shadeList) state.shadeList.remove(motorId)
        updateShadesAttribute()
        logInfo "Shade ${motorId} removed."
    } catch (e) {
        logError "Failed to remove shade ${motorId}: ${e.message}"
    }
}

private def logDebug(message) {
    if (settings.logEnable) log.debug "Pulse 2 [DEBUG]: ${message}"
}
private def logInfo(message) {
    if (settings.txtEnable) log.info "Pulse 2 [INFO]: ${message}"
}
private def logWarning(message) {
    log.warn "Pulse 2 [WARN]: ${message}"
}
private def logError(message) {
    log.error "Pulse 2 [ERROR]: ${message}"
}