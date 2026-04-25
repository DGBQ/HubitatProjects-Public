/**
 *  Xiaomi Aqara Contact Sensor
 *  Type: Standalone Driver
 *  Purpose: Represents a single door/window contact sensor. Receives and processes
 *           Zigbee messages from the Xiaomi Aqara contact sensor (MCCGQ11LM / AS006CNW01)
 *           and reports contact state, battery level, and health status.
 *
 *  Author: David Ball-Quenneville (maintainer, based on previous work by Jonathan Michaelsen and Dan Danache)
 *  Copyright 2026 David Ball-Quenneville
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
 *  v1.0.8 - 2026-04-25 - David Ball-Quenneville
 *    - Fixed version tracking: added state.lastCx = DRIVER_VERSION in updated().
 *    - Ensures version sync on every driver code save, not just on Configure.
 *
 *  v1.0.7 - 2026-04-25 - Fixed version tracking in configure().
 *  v1.0.6 - 2026-04-25 - Fixed nested list bug in configure().
 *  v1.0.5 - 2026-04-25 - Fixed configure() runIn delay.
 *  v1.0.4 - 2026-04-25 - Fixed attrInt conversion for contact parsing.
 *  v1.0.3 - 2026-04-25 - Fixed logLevel null errors.
 *  v1.0.2 - 2026-04-25 - Removed ping, added null checks.
 *  v1.0.1 - 2026-04-25 - Fixed null value parsing.
 *  v1.0.0 - 2026-04-24 - Initial release.
 */

import groovy.transform.CompileStatic
import groovy.transform.Field

@Field static final String DRIVER_NAME = 'Xiaomi Aqara Contact Sensor'
@Field static final String DRIVER_VERSION = '1.0.8'

// Fields for capability.HealthCheck
import groovy.time.TimeCategory

@Field static final Map<String, String> HEALTH_CHECK = [
    'schedule': '0 0 0/1 ? * * *', // Check every hour
    'threshold': '43200' // Mark offline if no message in last 12 hours
]

metadata {
    definition (
        name: "Xiaomi Aqara Contact Sensor",
        namespace: "DGBQ",
        author: "David Ball-Quenneville",
        version: "1.0.8",
        vid: "generic-contact",
        importUrl: ""
    ) {
        capability "Configuration"
        capability "Refresh"
        capability "Sensor"
        capability "ContactSensor"
        capability "Battery"
        capability "HealthCheck"

        // Attributes
        attribute "healthStatus", "enum", ["offline", "online", "unknown"]
        attribute "lastBattery", "date"
        attribute "networkRejoinCount", "number"

        // Fingerprints for Xiaomi contact sensors
        fingerprint profileId:"0104", inClusters:"0000,0003,FFFF,0006", outClusters:"0000,0004,FFFF", manufacturer:"LUMI", model:"lumi.sensor_magnet.aq2", deviceJoinName:"Xiaomi Aqara Contact Sensor"
        fingerprint profileId:"0104", inClusters:"0000,0003,FFFF,0019", outClusters:"0000,0004,0003,0006,0008,0005,0019", manufacturer:"LUMI", model:"lumi.sensor_magnet", deviceJoinName:"Xiaomi Mijia Door and Window Sensor"
    }
}

preferences {
    input(
        name:"logLevel", type:"enum", title:"Log verbosity", required:true,
        description:"Select what messages appear in the Logs section",
        options:["1":"Debug - log everything", "2":"Info - log important events", "3":"Warning - log events that require attention", "4":"Error - log errors"],
        defaultValue:"1"
    )
    input(
        name:"swapOpenClosed", type:"bool", title:"Invert contact state", required:true,
        description:"Swaps 'open' and 'closed' status reports",
        defaultValue:false
    )
}

// ===================================================================
// Lifecycle methods
// ===================================================================

void installed() {
    if (logLevel == null) device.updateSetting("logLevel", [value:"1", type:"enum"])
    log_warn "Installing device ..."
    state.lastCx = DRIVER_VERSION
}

void updated() {
    if (logLevel == null) device.updateSetting("logLevel", [value:"1", type:"enum"])
    log_info "Saving preferences ..."
    
    // FIX: Immediately sync the version state on every driver code save
    state.lastCx = DRIVER_VERSION
    
    unschedule()
    if (logLevel == "1") runIn 1800, "logsOff"
    schedule(HEALTH_CHECK.schedule, "healthCheck")
    runInMillis(1000, "configureApply")
}

void logsOff() {
    log_info "Automatically reverting log level to Info"
    device.updateSetting("logLevel", [value:"2", type:"enum"])
}

// ===================================================================
// Capability commands
// ===================================================================

void configure() {
    if (logLevel == null) device.updateSetting("logLevel", [value:"1", type:"enum"])
    log_warn "Configuring device ..."
    if (device.deviceNetworkId == null) { log_error "deviceNetworkId null"; return }
    if (device.zigbeeId == null) { log_error "zigbeeId null – re-pair device"; return }

    // Save any important state before clearing (none needed)
    state.clear()
    // Always set version to current driver version
    state.lastCx = DRIVER_VERSION
    state.lastTx = 0
    state.lastRx = 0

    List<String> cmds = []
    cmds += "he raw 0x${device.deviceNetworkId} 0x01 0x01 0x0003 {014300 3C00}"
    cmds.addAll(refresh(true))
    sendZigbee(cmds)
    // Wait 5 seconds for the above commands to be sent, then run configureApply
    runIn(5, "configureApply")
}

void configureApply() {
    if (logLevel == null) device.updateSetting("logLevel", [value:"1", type:"enum"])
    log_info "Finishing device configuration ..."
    if (device.deviceNetworkId == null || device.zigbeeId == null) {
        log_error "Missing identifiers"
        return
    }
    String ep = (device.endpointId != null) ? device.endpointId : "01"
    String nwk = device.deviceNetworkId
    String ieee = device.zigbeeId

    List<String> cmds = ["he raw 0x${nwk} 0x01 0x01 0x0003 {014300 3C00}"]
    cmds += "zdo bind 0x${nwk} 0x${ep} 0x01 0x0001 {${ieee}} {}"
    cmds += "he cr 0x${nwk} 0x${ep} 0x0001 0x0021 0x20 0x0000 0x4650 {02} {}"
    cmds += "zdo bind 0x${nwk} 0x${ep} 0x01 0x0006 {${ieee}} {}"
    cmds += "he cr 0x${nwk} 0x${ep} 0x0006 0x0000 0x10 0x0000 0x0001 {01} {}"

    sendEvent(name:"healthStatus", value:"online", descriptionText:"Health status initialized to online")
    sendEvent(name:"checkInterval", value:3600, unit:"second", descriptionText:"Health check interval is 3600 seconds")

    cmds += zigbee.readAttribute(0x0000, [0x0001, 0x0004, 0x0005])
    cmds += "he raw 0x${nwk} 0x01 0x01 0x0003 {014300 0000}"
    sendZigbee(cmds)
}

List<String> refresh(boolean auto = false) {
    if (auto) log_debug "Refreshing device state (auto) ..."
    else log_info "Refreshing device state ..."
    if (!auto) {
        log_warn "Click 'Refresh' immediately after opening/closing the contact to wake the device!"
    }
    return [zigbee.readAttribute(0x0006, 0x0000), zigbee.readAttribute(0x0001, 0x0021)]
}

void healthCheck() {
    log_debug "Running health check"
    if (!state.lastRx || state.lastRx == 0) {
        sendEvent(name:"healthStatus", value:"unknown", descriptionText:"Health status unknown (no communication yet)")
        return
    }
    long thresholdSec = (HEALTH_CHECK.threshold as Integer) ?: 43200
    boolean online = (now() - state.lastRx) < (thresholdSec * 1000)
    sendEvent(name:"healthStatus", value: online ? "online" : "offline", descriptionText:"Health status is ${online ? 'online' : 'offline'}")
}

// ===================================================================
// Zigbee message parsing
// ===================================================================

void parse(String description) {
    log_debug "description=${description}"

    // Auto-configure if driver version changed (lastCx mismatch)
    if (state.lastCx != DRIVER_VERSION) {
        state.lastCx = DRIVER_VERSION
        runInMillis(1500, "configure")
    }

    Map msg = parseDescriptionAsMap(description)
    log_debug "msg=${msg}"

    state.lastRx = now()
    if (device.currentValue("healthStatus") != "online") {
        sendEvent(name:"healthStatus", value:"online", descriptionText:"Health status changed to online", type:"digital")
    }

    String type = (state.lastTx && (now() - state.lastTx < 3000)) ? "digital" : "physical"

    if (msg.value == null) {
        log_debug "Message has no value field, ignoring"
        return
    }

    // Ensure attrInt is integer for reliable matching
    if (msg.attrInt instanceof String) {
        msg.attrInt = Integer.parseInt(msg.attrInt, 16)
    }

    switch (msg) {
        // Contact state from OnOff cluster (standard)
        case { contains(it, [clusterInt:6, commandInt:10, attrInt:0]) }:
        case { contains(it, [clusterInt:6, commandInt:1, attrInt:0]) }:
            String raw = msg.value
            boolean isOpen = (raw == "01")
            if (swapOpenClosed) isOpen = !isOpen
            String contact = isOpen ? "open" : "closed"
            sendEvent(name:"contact", value:contact, descriptionText:"Contact is ${contact}", type:type)
            log_info "Contact changed to ${contact}"
            return

        // Battery percentage from Power Configuration cluster
        case { contains(it, [clusterInt:1, commandInt:10, attrInt:33]) }:
        case { contains(it, [clusterInt:1, commandInt:1, attrInt:33]) }:
            if (msg.value == null && msg.data != null && msg.data[0] == "21" && msg.data[1] == "00") {
                msg.value = msg.data[2]
            }
            if (msg.value == null || msg.value == "FF") {
                log_warn "Ignored invalid battery percentage (${msg.value})"
                return
            }
            Integer percentage = Integer.parseInt(msg.value, 16) / 2
            Date nowDate = new Date()
            sendEvent(name:"battery", value:percentage, unit:"%", descriptionText:"Battery is ${percentage}% full", type:type)
            sendEvent(name:"lastBattery", value:nowDate, descriptionText:"Last battery report at ${nowDate}", type:type)
            log_info "Battery reported ${percentage}%"
            return

        // Xiaomi proprietary data
        case { contains(it, [clusterInt:0, attrInt:0xFF01]) }:
        case { contains(it, [clusterInt:0, attrInt:0xFF02]) }:
            parseXiaomiProprietary(msg, type)
            return

        // Device rejoined network
        case { contains(it, [endpointInt:0, clusterInt:19, commandInt:0]) }:
            log_warn "Device rejoined Zigbee mesh"
            Integer rejoinCount = (device.currentValue("networkRejoinCount") ?: 0) + 1
            sendEvent(name:"networkRejoinCount", value:rejoinCount, descriptionText:"Rejoin count = ${rejoinCount}", type:"physical")
            sendZigbee(refresh(true))
            return

        // Ignore common noise
        case { contains(it, [commandInt:11]) }: // Default response
        case { contains(it, [clusterInt:3]) }: // Identify
        case { contains(it, [endpointInt:0, clusterInt:32768]) }: // ZDP responses
            log_debug "Ignored message: ${msg}"
            return

        default:
            log_debug "Unhandled message: ${msg}"
    }
}

private void parseXiaomiProprietary(Map msg, String type) {
    String value = msg.value
    if (value == null || value.size() < 10) {
        log_debug "Proprietary data too short"
        return
    }

    // Battery voltage extraction
    String batteryHex = null
    if (msg.attrInt == 0xFF01 && value[4..5] == "21") {
        batteryHex = value[8..9] + value[6..7]
    } else if (msg.attrInt == 0xFF02 && value[8..9] == "21") {
        batteryHex = value[12..13] + value[10..11]
    }
    if (batteryHex) {
        int voltage = Integer.parseInt(batteryHex, 16)
        int percentage = Math.min(100, Math.max(0, (voltage - 20) * 10))
        Date nowDate = new Date()
        sendEvent(name:"battery", value:percentage, unit:"%", descriptionText:"Battery is ${percentage}% full", type:type)
        sendEvent(name:"lastBattery", value:nowDate, descriptionText:"Last battery report at ${nowDate}", type:type)
        log_info "Battery reported ${percentage}% (proprietary)"
    }

    // Contact state extraction
    String contactHex = null
    if (msg.attrInt == 0xFF01 && value.size() >= 6 && value[-6..-3] == "6410") {
        contactHex = value[-2..-1]
    } else if (msg.attrInt == 0xFF02 && value.size() >= 8 && value[0..5] == "060010") {
        contactHex = value[6..7]
    }
    if (contactHex) {
        int raw = Integer.parseInt(contactHex, 16)
        boolean isOpen = (raw == 1)
        if (swapOpenClosed) isOpen = !isOpen
        sendEvent(name:"contact", value: isOpen ? "open" : "closed", descriptionText:"Contact is ${isOpen ? 'open' : 'closed'}", type:type)
        log_info "Contact changed to ${isOpen ? 'open' : 'closed'} (proprietary)"
    }
}

// ===================================================================
// Helper methods
// ===================================================================

private void sendZigbee(List cmds) {
    if (!cmds) return
    // Flatten any nested lists and filter out non-strings and any leftover "delay" strings
    List<String> flatCmds = cmds.flatten().findAll { it instanceof String && !it.startsWith("delay") }
    if (flatCmds.isEmpty()) return
    List<String> send = delayBetween(flatCmds, 1000)
    log_debug "Sending Zigbee: ${send}"
    state.lastTx = now()
    sendHubCommand(new hubitat.device.HubMultiAction(send, hubitat.device.Protocol.ZIGBEE))
}

private Map parseDescriptionAsMap(String description) {
    Map map = [:]
    if (description.startsWith("read attr -")) {
        description.split(", ").each {
            def pair = it.split(": ")
            map[pair[0]] = pair[1]
        }
    } else {
        map = zigbee.parseDescriptionAsMap(description)
    }
    if (map.cluster) map.clusterInt = Integer.parseInt(map.cluster, 16)
    if (map.attrId) map.attrInt = Integer.parseInt(map.attrId, 16)
    if (map.command) map.commandInt = Integer.parseInt(map.command, 16)
    if (map.endpoint) map.endpointInt = Integer.parseInt(map.endpoint, 16)
    return map
}

private boolean contains(Map msg, Map spec) {
    return msg.keySet().containsAll(spec.keySet()) && spec.every { it.value == msg[it.key] }
}

private void log_debug(String msg) {
    String level = (logLevel != null) ? logLevel : "1"
    if (level == "1") log.debug "${device.displayName} ${msg.uncapitalize()}"
}
private void log_info(String msg) {
    String level = (logLevel != null) ? logLevel : "1"
    if (level.toInteger() <= 2) log.info "${device.displayName} ${msg.uncapitalize()}"
}
private void log_warn(String msg) {
    String level = (logLevel != null) ? logLevel : "1"
    if (level.toInteger() <= 3) log.warn "${device.displayName} ${msg.uncapitalize()}"
}
private void log_error(String msg) {
    log.error "${device.displayName} ${msg.uncapitalize()}"
}