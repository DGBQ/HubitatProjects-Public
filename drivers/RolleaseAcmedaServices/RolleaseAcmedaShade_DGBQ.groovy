/*
 * Name: Rollease Acmeda Shade
 * Type: Child Driver
 * Purpose: Represents a single shade motor. Receives commands from the parent hub driver
 *          and updates its state. Features proactive state updates to satisfy Alexa's
 *          response time requirements.
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
 * v2.3.1 - 2026-03-20 - David Ball-Quenneville
 * Note: Full revision history available in RolleaseAcmedaHub-DGBQ_CHANGELOG.md
 */

metadata {
    definition (
        name: "Rollease Acmeda Shade",
        namespace: "DGBQ",
        author: "David Ball-Quenneville (based on Younes Oughla previous work",
        version: "2.3.1",
        vid: "generic-shade",
        importUrl: ""
    ) {
        capability "Initialize"
        capability "Refresh"
        capability "Switch Level"
        capability "Switch"
        capability "Window Shade"
        capability "Battery"

        command "stop"
        command "toggle"

        attribute "open", "bool"
        attribute "closed", "bool"
        attribute "position", "int"
        attribute "moving", "bool"
        attribute "voltage", "int"
        attribute "batteryVoltage", "decimal"
        attribute "rssi", "string"
        // Removed "status" attribute
    }

    preferences {
        input name: "motorAddress", type: "string", title: "Motor Address", description: "", defaultValue: "000", required: true, displayDuringSetup: true
        input name: "logEnable", type: "bool", title: "Enable Debug Logging", description: "Detailed logs; auto‑off after 30 mins", defaultValue: false, required: false, displayDuringSetup: false
        input name: "txtEnable", type: "bool", title: "Enable Description Logging", description: "Human‑readable activity logs", defaultValue: true, required: false, displayDuringSetup: false
    }
}

def installed() {
    initialize()
}

def updated() {
    if (!logEnable) unschedule("logsOff")
    initialize()
}

def initialize() {
    logDebug "Motor Address: ${settings?.motorAddress}"

    // Initialize attributes with default values so Alexa has a baseline state
    if (device.currentValue("position") == null) {
        sendEvent(name: "position", value: 0)
        sendEvent(name: "level", value: 0)
        sendEvent(name: "switch", value: "off")
        sendEvent(name: "windowShade", value: "closed")
        sendEvent(name: "moving", value: false)
        logDebug "Initialized default attributes"
    }

    // Auto‑disable debug logging after 30 minutes
    if (logEnable) runIn(1800, "logsOff")
}

// ========== Command Handlers with Proactive Updates ==========

def on() { open() }
def off() { close() }

def setLevel(level) {
    int target = level instanceof Number ? level.toInteger() : level.toString().toInteger()
    setPosition(target)
}

def open() {
    logDebug "Opening Shade"
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: 100)
    sendEvent(name: "position", value: 100)
    sendEvent(name: "windowShade", value: "opening")
    sendEvent(name: "moving", value: true)
    parent.sendTelnetCommand("!${motorAddress}m000")
}

def close() {
    logDebug "Closing Shade"
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: 0)
    sendEvent(name: "position", value: 0)
    sendEvent(name: "windowShade", value: "closing")
    sendEvent(name: "moving", value: true)
    parent.sendTelnetCommand("!${motorAddress}m100")
}

def setPosition(position) {
    logDebug "Set Position: ${position}"
    int target = position instanceof Number ? position.toInteger() : position.toString().toInteger()
    def currentVal = device.currentValue("position")
    int current = (currentVal != null) ? currentVal.toInteger() : 0

    if (target > 0) {
        sendEvent(name: "switch", value: "on")
    } else {
        sendEvent(name: "switch", value: "off")
    }
    sendEvent(name: "level", value: target)
    sendEvent(name: "position", value: target)

    if (target == current) {
        logDebug "Already at target position, no command sent"
        if (target == 100) {
            sendEvent(name: "windowShade", value: "open")
        } else if (target == 0) {
            sendEvent(name: "windowShade", value: "closed")
        } else {
            sendEvent(name: "windowShade", value: "partially open")
        }
        sendEvent(name: "moving", value: false)
        return
    }

    if (target > current) {
        sendEvent(name: "windowShade", value: "opening")
    } else {
        sendEvent(name: "windowShade", value: "closing")
    }
    sendEvent(name: "moving", value: true)

    int inverted = 100 - target
    String posStr = inverted.toString().padLeft(3, '0')
    parent.sendTelnetCommand("!${motorAddress}m${posStr}")
}

def stop() {
    logDebug "Stopping Shade"
    sendEvent(name: "windowShade", value: "partially open")
    sendEvent(name: "moving", value: false)
    parent.sendTelnetCommand("!${motorAddress}s")
}

def toggle() {
    logDebug "Toggling Shade"
    String current = device.currentValue("windowShade")
    switch (current) {
        case "opening":
        case "closing":
            stop()
            break
        case "open":
            close()
            break
        case "closed":
            open()
            break
        case "partially open":
            if (state.lastDirection == "closing") {
                open()
            } else {
                close()
            }
            break
        default:
            open()
            break
    }
}

// ========== Parsing Hub Responses ==========

def parse(String msg) {
    if (msg.startsWith("!${motorAddress}r")) {
        String posStr = msg.substring(5, 8)
        int position = 100 - Integer.parseInt(posStr)
        positionUpdated(position)
    }

    if (msg.startsWith("!${motorAddress}m")) {
        String posStr = msg.substring(5, 8)
        int targetPos = 100 - Integer.parseInt(posStr)
        def currentPosVal = device.currentValue("position")
        int currentPos = (currentPosVal != null) ? currentPosVal.toInteger() : 0
        if (targetPos > currentPos) {
            sendEvent(name: "windowShade", value: "opening")
            state.lastDirection = "opening"
        } else if (targetPos < currentPos) {
            sendEvent(name: "windowShade", value: "closing")
            state.lastDirection = "closing"
        }
        sendEvent(name: "moving", value: true)
    }

    if (msg.startsWith("!${motorAddress}pVc")) {
        logDebug "Parsing Battery/Signal Report: ${msg}"
        String rawData = msg.substring(7)
        def parts = rawData.split(",")
        String vStr = parts[0].trim()
        while (vStr.startsWith("0") && vStr.length() > 1) {
            vStr = vStr.substring(1)
        }
        try {
            int rawVolt = vStr.toInteger()
            double voltDecimal = rawVolt / 100.0
            double pct = ((voltDecimal - 10.8) / (12.6 - 10.8)) * 100
            int batteryPct = Math.min(Math.max(pct.toInteger(), 0), 100)

            sendEvent(name: "voltage", value: rawVolt)
            sendEvent(name: "batteryVoltage", value: voltDecimal, unit: "V")
            sendEvent(name: "battery", value: batteryPct, unit: "%")

            if (parts.size() > 1) {
                sendEvent(name: "rssi", value: parts[1].trim())
            }

            logDebug "Parsed Success: ${voltDecimal}V, ${batteryPct}%, RSSI: ${parts.size() > 1 ? parts[1] : 'N/A'}"
        } catch (e) {
            logWarning "Failed to parse voltage string '${vStr}': ${e}"
        }
    }
}

def positionUpdated(int position) {
    logDebug "Position Updated: ${position}"
    if (position == 100) {
        sendEvent(name: "open", value: true)
        sendEvent(name: "closed", value: false)
        sendEvent(name: "windowShade", value: "open")
        sendEvent(name: "switch", value: "on")
    } else if (position == 0) {
        sendEvent(name: "closed", value: true)
        sendEvent(name: "open", value: false)
        sendEvent(name: "windowShade", value: "closed")
        sendEvent(name: "switch", value: "off")
    } else {
        sendEvent(name: "closed", value: false)
        sendEvent(name: "open", value: false)
        sendEvent(name: "windowShade", value: "partially open")
    }
    sendEvent(name: "position", value: position)
    sendEvent(name: "level", value: position)
    sendEvent(name: "moving", value: false)
}

def requestStatus() {
    parent.sendTelnetCommand("!${motorAddress}r?")
}

def refresh() {
    logDebug "Refreshing"
    requestStatus()
}

def logsOff() {
    log.warn "Debug logging auto-disabled"
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

// ========== Logging Helpers ==========

private def logDebug(message) {
    if (logEnable) log.debug "${device.name} [DEBUG]: ${message}"
}

private def logInfo(message) {
    if (txtEnable) log.info "${device.name} [INFO]: ${message}"
}

private def logWarning(message) {
    log.warn "${device.name} [WARN]: ${message}"
}

private def logError(message) {
    log.error "${device.name} [ERROR]: ${message}"
}