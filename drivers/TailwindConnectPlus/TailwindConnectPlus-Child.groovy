/**
 *  Tailwind Connect Plus - Child
 *  Type: Child Device Driver
 *  Purpose: Individual door control for each garage door.
 *           Created automatically by the parent driver.
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
 *  v1.1.3 - 2026-08-31 - David Ball-Quenneville
 *    - Version synchronized with parent driver (v1.2.1)
 *    - No functional changes
 *  v1.1.2 - 2026-08-31 - David Ball-Quenneville
 *    - Phase 3: Added lastOpen and lastClosed attributes
 *  v1.1.1 - 2026-08-18 - David Ball-Quenneville
 *    - Version synchronized with parent driver v1.1.1
 *  v1.1.0 - 2026-08-18 - David Ball-Quenneville
 *    - Version synchronized with parent driver (Phase 2)
 *  v1.0.0 - 2026-08-12 - David Ball-Quenneville
 *    - Initial release
 */

import groovy.transform.Field

@Field static final String DRIVER_VERSION = "v1.1.3"

metadata {
    definition(
        name: "Tailwind Connect Plus - Child",
        namespace: "DGBQ",
        author: "David Ball-Quenneville",
        version: DRIVER_VERSION,
        importUrl: "https://raw.githubusercontent.com/YourGitHub/hubitat-tailwind-connect-plus/main/tailwind-connect-plus-child.groovy"
    ) {
        capability "GarageDoorControl"
        capability "Actuator"
        capability "ContactSensor"
        capability "Sensor"
        attribute "Status", "string"
        attribute "lastOpen", "string"
        attribute "lastClosed", "string"
        command "open"
        command "close"
    }
}

def close() {
    parent.childClose(device.deviceNetworkId[-1].toInteger())
}

def open() {
    parent.childOpen(device.deviceNetworkId[-1].toInteger())
}