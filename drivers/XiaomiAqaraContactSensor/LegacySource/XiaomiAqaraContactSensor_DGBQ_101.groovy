/**
 *  Xiaomi Aqara Contact Sensor
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
 *  v1.0.0 - 2026-04-24 - David Ball-Quenneville
 *    - Initial release based on IKEA Parasoll driver structure (Dan Danache) and
 *      Xiaomi Aqara Mijia Sensors driver (Jonathan Michaelsen).
 *    - Supports Xiaomi Aqara Contact Sensor (lumi.sensor_magnet.aq2) and Mijia Door/Window Sensor (lumi.sensor_magnet).
 *    - Features: contact state, battery percentage, health check (online/offline), network rejoin count.
 *    - Removed all unrelated device code (motion, vibration, weather, buttons, water leak, etc.).
 *    - Added ping command, configure, refresh.
 *    - Preferences for log verbosity and contact state inversion.
 */

import groovy.transform.CompileStatic
import groovy.transform.Field

@Field static final String DRIVER_NAME = 'Xiaomi Aqara Contact Sensor'
@Field static final String DRIVER_VERSION = '1.0.0'

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
        version: "1.0.0",
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
    log_warn "Installing device ..."
    state.lastCx = DRIVER_VERSION
}

void updated() {
    log_info "Saving preferences ..."
    unschedule()
    if (logLevel == "1") runIn 1800, "logsOff"
    schedule(HEALTH_CHECK.schedule, "healthCheck")
    // Re-apply configuration after preference change
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
    log_warn "Configuring device ..."
    // Clear state except lastCx
    def savedCx = state.lastCx
    state.clear()
    state.lastCx = savedCx ?: DRIVER_VERSION
    state.lastTx = 0
    state.lastRx = 0

    List<String> cmds = []
    // Put device in identify mode (blink LED if supported)
    cmds += "he raw 0x${device.deviceNetworkId} 0x01 0x01 0x0003 {014300 3C00}"
    cmds += refresh(true)
    sendZigbee(cmds)

    runIn(cmds.findAll { !it.startsWith("delay") }.size() + 1, "configureApply")
}

void configureApply() {
    log_info "Finishing device configuration ..."
    List<String> cmds = ["he raw 0x${device.deviceNetworkId} 0x01 0x01 0x0003 {014300 3C00}"]

    // Bind and configure reporting for Battery cluster (0x0001)
    cmds += "zdo bind 0x${device.deviceNetworkId} 0x${device.endpointId} 0x01 0x0001 {${device.zigbeeId}} {}"
    cmds += "he cr 0x${device.deviceNetworkId} 0x${device.endpointId} 0x0001 0x0021 0x20 0x0000 0x4650 {02} {}" // Report BatteryPercentage (uint8) every 5 hours, delta 1%

    // Bind On/Off cluster (0x0006) for contact reports
    cmds += "zdo bind 0x${device.deviceNetworkId} 0x${device.endpointId} 0x01 0x0006 {${device.zigbeeId}} {}"
    cmds += "he cr 0x${device.deviceNetworkId} 0x${device.endpointId} 0x0006 0x0000 0x10 0x0000 0x0001 {01} {}" // Report OnOff (boolean) immediately on change

    // Initialize health status
    sendEvent(name:"healthStatus", value:"online", descriptionText:"Health status initialized to online")
    sendEvent(name:"checkInterval", value:3600, unit:"second", descriptionText:"Health check interval is 3600 seconds")

    // Query basic attributes for info
    cmds += zigbee.readAttribute(0x0000, [0x0001, 0x0004, 0x0005]) // App version, manufacturer, model

    // Stop identify mode
    cmds += "he raw 0x${device.deviceNetworkId} 0x01 0x01 0x0003 {014300 0000}"
    sendZigbee(cmds)
}

List<String> refresh(boolean auto = false) {
    if (auto) log_debug "Refreshing device state (auto) ..."
    else log_info "Refreshing device state ..."
    if (!auto && device.currentValue("powerSource", true) == "battery") {
        log_warn "Click the 'Refresh' button immediately after opening/closing the contact to wake the device!"
    }

    List<String> cmds = []
    cmds += zigbee.readAttribute(0x0006, 0x0000) // OnOff (contact)
    cmds += zigbee.readAttribute(0x0001, 0x0021) // BatteryPercentage
    return cmds
}

void ping() {
    log_warn "ping ..."
    sendZigbee(zigbee.readAttribute(0x0000, 0x0000)) // Read basic cluster to elicit response
    runIn(5, "pingExecute")
}

void pingExecute() {
    if (state.lastRx == 0) {
        log_info "Device has not sent any messages since configuration"
        return
    }
    Date now = new Date()
    Date lastRx = new Date(state.lastRx)
    String lastRxAgo = TimeCategory.minus(now, lastRx).toString()
    log_info "Last message received ${lastRxAgo} ago"
}

void healthCheck() {
    log_debug "Running health check"
    if (!state.lastRx || state.lastRx == 0) {
        sendEvent(name:"healthStatus", value:"unknown", descriptionText:"Health status unknown (no communication yet)")
        return
    }
    long thresholdSec = (HEALTH_CHECK.threshold as Integer) ?: 43200
    boolean online = (now() - state.lastRx) < (thresholdSec * 1000)
    String status = online ? "online" : "offline"
    sendEvent(name:"healthStatus", value:status, descriptionText:"Health status is ${status}")
}

// ===================================================================
// Zigbee message parsing
// ===================================================================

void parse(String description) {
    log_debug "description=${description}"

    // Auto-configure if driver version changed
    if (state.lastCx != DRIVER_VERSION) {
        state.lastCx = DRIVER_VERSION
        runInMillis(1500, "configure")
    }

    Map msg = parseDescriptionAsMap(description)
    log_debug "msg=${msg}"

    state.lastRx = now()

    // Update health status to online if it wasn't
    if (device.currentValue("healthStatus") != "online") {
        sendEvent(name:"healthStatus", value:"online", descriptionText:"Health status changed to online", type:"digital")
    }

    // Determine event type: physical if device initiated, digital if driver initiated
    String type = (state.lastTx && (now() - state.lastTx < 3000)) ? "digital" : "physical"

    switch (msg) {
        // Contact state from OnOff cluster (standard)
        case { contains(it, [clusterInt:0x0006, commandInt:0x0A, attrInt:0x0000]) }:
        case { contains(it, [clusterInt:0x0006, commandInt:0x01, attrInt:0x0000]) }:
            String raw = msg.value
            boolean isOpen = (raw == "01")
            if (swapOpenClosed) isOpen = !isOpen
            String contact = isOpen ? "open" : "closed"
            sendEvent(name:"contact", value:contact, descriptionText:"Contact is ${contact}", type:type)
            log_info "Contact changed to ${contact}"
            return

        // Battery percentage from Power Configuration cluster
        case { contains(it, [clusterInt:0x0001, commandInt:0x0A, attrInt:0x0021]) }:
        case { contains(it, [clusterInt:0x0001, commandInt:0x01, attrInt:0x0021]) }:
            // Handle Hubitat's occasional misparse
            if (msg.value == null && msg.data != null && msg.data[0] == "21" && msg.data[1] == "00") {
                msg.value = msg.data[2]
            }
            if (msg.value == "FF") {
                log_warn "Ignored invalid battery percentage (0xFF)"
                return
            }
            Integer percentage = Integer.parseInt(msg.value, 16) / 2
            Date nowDate = new Date()
            sendEvent(name:"battery", value:percentage, unit:"%", descriptionText:"Battery is ${percentage}% full", type:type)
            sendEvent(name:"lastBattery", value:nowDate, descriptionText:"Last battery report at ${nowDate}", type:type)
            log_info "Battery reported ${percentage}%"
            return

        // Xiaomi proprietary data (FF01/FF02) - for contact and battery when not using standard clusters
        case { contains(it, [clusterInt:0x0000, attrInt:"FF01"]) }:
        case { contains(it, [clusterInt:0x0000, attrInt:"FF02"]) }:
            parseXiaomiProprietary(msg, type)
            return

        // Device rejoined the network
        case { contains(it, [endpointInt:0x00, clusterInt:0x0013, commandInt:0x00]) }:
            log_warn "Device rejoined the Zigbee mesh. Syncing state ..."
            Integer rejoinCount = (device.currentValue("networkRejoinCount") ?: 0) + 1
            sendEvent(name:"networkRejoinCount", value:rejoinCount, descriptionText:"Network rejoin count = ${rejoinCount}", type:"physical")
            sendZigbee(refresh(true))
            return

        // Ignore common noise
        case { contains(it, [commandInt:0x0B]) }: // Default response
        case { contains(it, [clusterInt:0x0003]) }: // Identify
        case { contains(it, [endpointInt:0x00, clusterInt:0x8000]) }: // ZDP responses
            log_debug "Ignored message: ${msg}"
            return

        default:
            log_debug "Unhandled message: ${msg}"
    }
}

private void parseXiaomiProprietary(Map msg, String type) {
    String value = msg.value
    if (!value || value.size() < 10) return

    // Battery voltage extraction (varies by attribute)
    String batteryHex = null
    if (msg.attrInt == "FF01" && value[4..5] == "21") {
        batteryHex = value[8..9] + value[6..7]
    } else if (msg.attrInt == "FF02" && value[8..9] == "21") {
        batteryHex = value[12..13] + value[10..11]
    }
    if (batteryHex) {
        int voltage = Integer.parseInt(batteryHex, 16) // in 0.01V units
        int percentage = Math.min(100, Math.max(0, (voltage - 20) * 10)) // rough conversion from 2.0V-3.0V
        Date nowDate = new Date()
        sendEvent(name:"battery", value:percentage, unit:"%", descriptionText:"Battery is ${percentage}% full", type:type)
        sendEvent(name:"lastBattery", value:nowDate, descriptionText:"Last battery report at ${nowDate}", type:type)
        log_info "Battery reported ${percentage}% (proprietary)"
    }

    // Contact state extraction
    String contactHex = null
    if (msg.attrInt == "FF01" && value[-6..-3] == "6410") {
        contactHex = value[-2..-1]
    } else if (msg.attrInt == "FF02" && value[0..5] == "060010") {
        contactHex = value[6..7]
    }
    if (contactHex) {
        int raw = Integer.parseInt(contactHex, 16)
        boolean isOpen = (raw == 1)
        if (swapOpenClosed) isOpen = !isOpen
        String contact = isOpen ? "open" : "closed"
        sendEvent(name:"contact", value:contact, descriptionText:"Contact is ${contact}", type:type)
        log_info "Contact changed to ${contact} (proprietary)"
    }
}

// ===================================================================
// Helper methods
// ===================================================================

private void sendZigbee(List<String> cmds) {
    if (!cmds) return
    List<String> send = delayBetween(cmds.findAll { !it.startsWith("delay") }, 1000)
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
    // Add convenience integer fields
    if (map.cluster) map.clusterInt = Integer.parseInt(map.cluster, 16)
    if (map.attrId) map.attrInt = map.attrId
    if (map.command) map.commandInt = Integer.parseInt(map.command, 16)
    if (map.endpoint) map.endpointInt = Integer.parseInt(map.endpoint, 16)
    return map
}

private boolean contains(Map msg, Map spec) {
    return msg.keySet().containsAll(spec.keySet()) && spec.every { it.value == msg[it.key] }
}

private void log_debug(String msg) {
    if (logLevel == "1") log.debug "${device.displayName} ${msg.uncapitalize()}"
}
private void log_info(String msg) {
    if (logLevel.toInteger() <= 2) log.info "${device.displayName} ${msg.uncapitalize()}"
}
private void log_warn(String msg) {
    if (logLevel.toInteger() <= 3) log.warn "${device.displayName} ${msg.uncapitalize()}"
}
private void log_error(String msg) {
    log.error "${device.displayName} ${msg.uncapitalize()}"
}