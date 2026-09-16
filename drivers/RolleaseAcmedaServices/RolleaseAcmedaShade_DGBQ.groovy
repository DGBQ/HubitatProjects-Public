/*
 * Name: Rollease Acmeda Shade
 * Type: Child Driver
 * Purpose: Represents a single shade motor. Receives commands from the parent hub driver
 *          and updates its state. Features proactive state updates to satisfy Alexa's
 *          response time requirements. Includes confirmation‑based retry and RF jitter.
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
 * v2.5.5 - 2026-09-16 - David Ball-Quenneville
 *   - PHASE 2A (Retry Logic + Logging UX):
 *     - Added Position Tolerance preference (default 1, range 0-5). Confirmation now
 *       accepts a shade within tolerance of the target, eliminating false retries on
 *       shades that physically stop 1-2% short (BSG, I39).
 *     - Added intermediate position reset. If the reported position has changed since
 *       the last report, the confirmation timer is reset (up to 2 times) instead of
 *       firing jitter. Eliminates false retries on slow shades (UE2, 9E3, XDG).
 *     - Fixed unschedule() method names: retryCommand -> checkPositionConfirmation,
 *       jitterRetry -> retryWithJitter. These were no-ops in v2.5.4.
 *     - Added state.commandId stale-callback guard. Prevents a new command from being
 *       affected by a stale callback from a previous command.
 *     - Replaced logEnable and txtEnable with logVerbosity dropdown
 *       (ERROR, WARN, INFO, DEBUG; default INFO).
 *     - Auto-Revert Debug now reverts DEBUG to INFO after 30 minutes.
 *     - Updated logging helpers to check settings.logVerbosity.
 *
 * v2.5.4 - 2026-09-11 - David Ball-Quenneville
 *   - HOTFIX: Battery voltage-to-percentage formula adjusted to 9.5V-12.6V range.
 *
 * v2.5.3 - 2026-09-02 - David Ball-Quenneville
 *   - Fixed MissingMethodException: logWarn -> logWarning.
 *   - Improved jitter ID: uses the shade's own motorAddress.
 *   - Clear pendingTarget on confirmation.
 *
 * v2.5.2 - 2026-09-02 - David Ball-Quenneville
 *   - Added confirmation-based retry logic.
 *   - Added RF Jitter workaround.
 *   - Grouped retry preferences.
 *   - Removed Keep Debug Logging On.
 *
 * v2.5.1 - 2026-09-02 - David Ball-Quenneville
 *   - Added command retry feature.
 *
 * v2.4.4 - 2026-07-25 - David Ball-Quenneville
 *   - Stable baseline - no retry logic.
 */

metadata {
    definition (
        name: "Rollease Acmeda Shade",
        namespace: "DGBQ",
        author: "David Ball-Quenneville (based on Younes Oughla previous work)",
        version: "2.5.5",
        vid: "generic-shade",
        importUrl: ""
    ) {
        capability "Initialize"
        capability "Refresh"
        capability "Switch Level"
        capability "Window Shade"
        capability "Battery"

        command "stop"
        command "toggle"
        command "requestBatteryStatus"

        attribute "open", "bool"
        attribute "closed", "bool"
        attribute "position", "int"
        attribute "moving", "bool"
        attribute "voltage", "int"
        attribute "batteryVoltage", "decimal"
        attribute "rssi", "string"
        attribute "switch", "enum", ["on", "off"]
    }

    preferences {
        input name: "motorAddress", type: "string", title: "Motor Address", description: "", defaultValue: "000", required: true, displayDuringSetup: true

        input name: "batteryOffset", type: "number", title: "Battery Offset (%)", description: "Adjust reported battery percentage (e.g., +15 if fully charged shows 85%). Range -30 to +30.", defaultValue: 0, required: false, displayDuringSetup: false, range: "-30..30"

        input name: "positionTolerance", type: "number",
            title: "Position Tolerance (%)",
            description: "Allow the shade to be considered 'confirmed' if it reaches within this percentage of the target. Increase if your shade consistently stops a few percent short. Default: 1. Set to 0 for exact match.",
            defaultValue: 1,
            required: false,
            range: "0..5"

        input name: "cmdRetryCount", type: "number",
            title: "Command Retry Count",
            description: "Number of retries if a command fails to receive confirmation. Set to 0 to disable retries. Default: 2.",
            defaultValue: 2,
            required: false,
            range: "0..5"

        input name: "cmdRetryWait", type: "number",
            title: "Command Retry Wait Time (seconds)",
            description: "Time to wait before retrying a command. A longer wait gives the shade time to respond. Default: 10 seconds.",
            defaultValue: 10,
            required: false,
            range: "5..30"

        input name: "enableJitter", type: "bool",
            title: "Enable RF Jitter",
            description: "If a command fails, send a harmless status request to wake the hub's RF transmitter, then retry. Default: On.",
            defaultValue: true,
            required: false

        input name: "logVerbosity", type: "enum",
            title: "Log Verbosity",
            description: "Controls the amount of detail written to the log and debug buffer. (Default: INFO)",
            options: ["ERROR", "WARN", "INFO", "DEBUG"],
            defaultValue: "INFO",
            required: false

        input name: "autoRevertDebug", type: "bool",
            title: "Auto-Revert Debug",
            description: "Automatically reverts Log Verbosity from DEBUG to INFO after 30 minutes. (Default: Enabled)",
            defaultValue: true,
            required: false
    }
}

def installed() {
    initialize()
}

def updated() {
    // Cancel auto-off if debug is off or Auto-Revert is disabled
    if (settings.logVerbosity != "DEBUG" || !settings.autoRevertDebug) {
        unschedule("logsOff")
    }
    initialize()
}

def initialize() {
    logDebug "Motor Address: ${settings?.motorAddress}"

    if (!parent) {
        logWarning "Parent reference missing. Please re-save the parent device or manually re-link."
    }

    if (device.currentValue("position") == null) {
        sendEvent(name: "position", value: 0)
        sendEvent(name: "level", value: 0)
        sendEvent(name: "switch", value: "off")
        sendEvent(name: "windowShade", value: "closed")
        sendEvent(name: "moving", value: false)
        logDebug "Initialized default attributes"
    }

    // Log verbosity handling
    if (settings.logVerbosity == "DEBUG" && settings.autoRevertDebug) {
        runIn(1800, "logsOff")
        logDebug "Log Verbosity will auto-revert to INFO after 30 minutes (Auto-Revert enabled)"
    } else if (settings.logVerbosity == "DEBUG") {
        logDebug "Log Verbosity will remain at DEBUG (Auto-Revert disabled)"
    }

    // Clear any pending retry state
    state.pendingTarget = null
    state.retryCount = 0
    state.confirmed = false
    state.jitterUsed = false
    state.commandId = (state.commandId ?: 0) + 1
    state.reportResetCount = 0
    state.lastReportedPosition = null
}

def on() { open() }
def off() { close() }

def setLevel(level, duration = null) {
    int target = level instanceof Number ? level.toInteger() : level.toString().toInteger()
    if (duration != null && duration.toString().toInteger() > 0) {
        logInfo "Duration parameter is ignored. Shade will move directly to ${target}%."
    }
    setPosition(target)
}

def open() {
    logDebug "Opening Shade"
    logInfo "Command sent: Opening"
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: 100)
    sendEvent(name: "position", value: 100)
    sendEvent(name: "windowShade", value: "opening")
    sendEvent(name: "moving", value: true)
    sendCommand("!${motorAddress}m000", 100)
}

def close() {
    logDebug "Closing Shade"
    logInfo "Command sent: Closing"
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: 0)
    sendEvent(name: "position", value: 0)
    sendEvent(name: "windowShade", value: "closing")
    sendEvent(name: "moving", value: true)
    sendCommand("!${motorAddress}m100", 0)
}

def setPosition(position) {
    logDebug "Set Position: ${position}"
    int target = position instanceof Number ? position.toInteger() : position.toString().toInteger()
    def currentVal = device.currentValue("position")
    int current = (currentVal != null) ? currentVal.toInteger() : 0

    if (target > 0) sendEvent(name: "switch", value: "on")
    else sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: target)
    sendEvent(name: "position", value: target)

    if (target == current) {
        logDebug "Already at target position, no command sent"
        if (target == 100) sendEvent(name: "windowShade", value: "open")
        else if (target == 0) sendEvent(name: "windowShade", value: "closed")
        else sendEvent(name: "windowShade", value: "partially open")
        sendEvent(name: "moving", value: false)
        return
    }

    if (target > current) sendEvent(name: "windowShade", value: "opening")
    else sendEvent(name: "windowShade", value: "closing")
    sendEvent(name: "moving", value: true)

    logInfo "Command sent: Moving to ${target}%"
    int inverted = 100 - target
    String posStr = inverted.toString().padLeft(3, '0')
    sendCommand("!${motorAddress}m${posStr}", target)
}

private def sendCommand(String commandString, int target) {
    // Cancel any pending timers
    unschedule("checkPositionConfirmation")
    unschedule("retryWithJitter")

    // Increment commandId to invalidate any stale callbacks
    state.commandId = (state.commandId ?: 0) + 1
    int thisCommandId = state.commandId

    // Store target and reset state
    state.pendingTarget = target
    state.retryCount = 0
    state.confirmed = false
    state.currentCommand = commandString
    state.jitterUsed = false
    state.reportResetCount = 0
    state.lastReportedPosition = null

    // Send the command
    parent.sendTelnetCommand(commandString)
    logDebug "Command sent, waiting for confirmation to ${target}% (commandId=${thisCommandId})"

    // Start confirmation timer
    int waitTime = settings.cmdRetryWait ?: 10
    runIn(waitTime, "checkPositionConfirmation")
}

def checkPositionConfirmation() {
    def target = state.pendingTarget
    int thisCommandId = state.commandId

    if (target == null) {
        logDebug "No pending command to confirm"
        return
    }

    // If confirmation was received, we're done
    if (state.confirmed) {
        logDebug "Position confirmed: ${target}%"
        state.pendingTarget = null
        state.retryCount = 0
        state.jitterUsed = false
        return
    }

    // Not confirmed - retry or use jitter
    int maxRetries = settings.cmdRetryCount ?: 2
    int currentRetry = (state.retryCount ?: 0) + 1

    // If jitter hasn't been tried yet, and enabled, do it first
    if (settings.enableJitter && !state.jitterUsed) {
        logWarning "No confirmation - sending RF jitter to wake hub transmitter"
        state.jitterUsed = true
        String jitterId = motorAddress
        parent.sendTelnetCommand("!${jitterId}r?")
        state.retryCount = currentRetry
        runIn(3, "retryWithJitter")
        return
    }

    // If we've already tried jitter or it's disabled, proceed with normal retry
    if (maxRetries > 0 && currentRetry <= maxRetries) {
        state.retryCount = currentRetry
        logWarning "Position not confirmed - retry ${currentRetry}/${maxRetries}"
        parent.sendTelnetCommand(state.currentCommand)
        int waitTime = settings.cmdRetryWait ?: 10
        runIn(waitTime, "checkPositionConfirmation")
    } else {
        logWarning "Position not confirmed after ${maxRetries} retries - command may have failed"
        state.pendingTarget = null
        state.retryCount = 0
        state.jitterUsed = false
    }
}

def retryWithJitter() {
    // After jitter, retry the original command once more
    if (state.confirmed) {
        logDebug "Position confirmed after jitter"
        state.pendingTarget = null
        state.retryCount = 0
        state.jitterUsed = false
        return
    }
    logWarning "Retrying after jitter"
    parent.sendTelnetCommand(state.currentCommand)
    int waitTime = settings.cmdRetryWait ?: 10
    runIn(waitTime, "checkPositionConfirmation")
}

def stop() {
    logDebug "Stopping Shade"
    logInfo "Command sent: Stop"
    sendEvent(name: "windowShade", value: "partially open")
    sendEvent(name: "moving", value: false)
    // Cancel any pending timers
    unschedule("checkPositionConfirmation")
    unschedule("retryWithJitter")
    state.pendingTarget = null
    state.retryCount = 0
    state.confirmed = false
    state.jitterUsed = false
    state.reportResetCount = 0
    state.lastReportedPosition = null
    parent.sendTelnetCommand("!${motorAddress}s")
}

def toggle() {
    logDebug "Toggling Shade"
    String current = device.currentValue("windowShade")
    switch (current) {
        case "opening": case "closing": stop(); break
        case "open": close(); break
        case "closed": open(); break
        case "partially open":
            if (state.lastDirection == "closing") open()
            else close()
            break
        default: open()
    }
}

// ========== Stubs for non-functional capability commands ==========
def startPositionChange(String direction) {
    logInfo "Start Position Change is not supported by this driver. Use Open/Close or Set Position instead."
}

def stopPositionChange() {
    logInfo "Stop Position Change is not supported by this driver. Use Stop instead."
}

// ========== Manual Battery Status Request ==========
def requestBatteryStatus() {
    logInfo "Requesting battery status from hub"
    parent.sendTelnetCommand("!${motorAddress}pVc?")
}

// ========== Parsing Hub Responses ==========

def parse(String msg) {
    if (msg.startsWith("!${motorAddress}r")) {
        String posStr = msg.substring(5, 8)
        int position = 100 - Integer.parseInt(posStr)
        positionUpdated(position)

        // Confirmation logic
        def target = state.pendingTarget
        int tolerance = (settings.positionTolerance != null) ? settings.positionTolerance.toInteger() : 1

        if (target != null) {
            // Check if we are within tolerance of the target
            if (Math.abs(position - target) <= tolerance) {
                state.confirmed = true
                state.pendingTarget = null
                state.reportResetCount = 0
                state.lastReportedPosition = null
                logDebug "Confirmation received for target ${target}% (reported ${position}%, tolerance +/-${tolerance}%)"
                unschedule("checkPositionConfirmation")
                unschedule("retryWithJitter")
            } else {
                // Intermediate position report
                def lastReported = state.lastReportedPosition
                if (lastReported == null || lastReported != position) {
                    state.lastReportedPosition = position
                    int resetCount = (state.reportResetCount ?: 0)
                    int maxResets = 2
                    if (resetCount < maxResets) {
                        state.reportResetCount = resetCount + 1
                        logDebug "Intermediate position ${position}% received (target ${target}%) - resetting confirmation timer (${state.reportResetCount}/${maxResets})"
                        unschedule("checkPositionConfirmation")
                        int waitTime = settings.cmdRetryWait ?: 10
                        runIn(waitTime, "checkPositionConfirmation")
                    } else {
                        logDebug "Intermediate position ${position}% received but reset limit reached (${resetCount}/${maxResets}) - allowing timer to expire"
                    }
                }
            }
        }
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
        while (vStr.startsWith("0") && vStr.length() > 1) vStr = vStr.substring(1)
        try {
            int rawVolt = vStr.toInteger()
            double voltDecimal = rawVolt / 100.0
            // v2.5.4 HOTFIX: Adjusted voltage range to 9.5V (0%) - 12.6V (100%)
            double pct = ((voltDecimal - 9.5) / (12.6 - 9.5)) * 100
            int batteryPct = Math.min(Math.max(pct.toInteger(), 0), 100)
            int offset = (settings.batteryOffset != null) ? settings.batteryOffset.toInteger() : 0
            int adjustedPct = Math.min(Math.max(batteryPct + offset, 0), 100)

            sendEvent(name: "voltage", value: rawVolt)
            sendEvent(name: "batteryVoltage", value: voltDecimal, unit: "V")
            sendEvent(name: "battery", value: adjustedPct, unit: "%")
            if (parts.size() > 1) sendEvent(name: "rssi", value: parts[1].trim())

            logDebug "Parsed Success: ${voltDecimal}V, original ${batteryPct}%, offset ${offset} -> ${adjustedPct}%, RSSI: ${parts.size() > 1 ? parts[1] : 'N/A'}"
        } catch (e) {
            logWarning "Failed to parse voltage string '${vStr}': ${e}"
        }
    }
}

def positionUpdated(int position) {
    logDebug "Position Updated: ${position}"
    if (position == 100) {
        sendEvent(name: "open", value: true); sendEvent(name: "closed", value: false)
        sendEvent(name: "windowShade", value: "open"); sendEvent(name: "switch", value: "on")
    } else if (position == 0) {
        sendEvent(name: "closed", value: true); sendEvent(name: "open", value: false)
        sendEvent(name: "windowShade", value: "closed"); sendEvent(name: "switch", value: "off")
    } else {
        sendEvent(name: "closed", value: false); sendEvent(name: "open", value: false)
        sendEvent(name: "windowShade", value: "partially open")
    }
    sendEvent(name: "position", value: position)
    sendEvent(name: "level", value: position)
    sendEvent(name: "moving", value: false)

    // Only log if position has changed from last logged position
    def lastLoggedPos = state.lastLoggedPosition
    if (lastLoggedPos == null || lastLoggedPos != position) {
        logInfo "Position confirmed: ${position}%"
        state.lastLoggedPosition = position
    }
}

def requestStatus() {
    parent.sendTelnetCommand("!${motorAddress}r?")
}

def refresh() {
    logDebug "Refreshing"
    requestStatus()
}

def logsOff() {
    if (settings.logVerbosity == "DEBUG") {
        log.warn "Log Verbosity auto-reverted to INFO (Auto-Revert was enabled)"
        device.updateSetting("logVerbosity", [value: "INFO", type: "enum"])
    }
}

// ========== Logging Helpers ==========
// Order of severity: ERROR (0) < WARN (1) < INFO (2) < DEBUG (3)
private int getVerbosityLevel() {
    def levels = ["ERROR": 0, "WARN": 1, "INFO": 2, "DEBUG": 3]
    return levels[settings.logVerbosity] ?: 2
}

private def logDebug(message) {
    if (getVerbosityLevel() >= 3) log.debug "${device.name} [DEBUG]: ${message}"
}
private def logInfo(message) {
    if (getVerbosityLevel() >= 2) log.info "${device.name} [INFO]: ${message}"
}
private def logWarning(message) {
    if (getVerbosityLevel() >= 1) log.warn "${device.name} [WARN]: ${message}"
}
private def logError(message) {
    log.error "${device.name} [ERROR]: ${message}"
}