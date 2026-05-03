/**
 *  Open-Meteo UV Index and Illuminance Tracker Plus
 *  Type: Standalone Driver
 *  Purpose: Creates an illuminance sensor from Open‑Meteo solar radiation data
 *           and reports UV index (current + today + 5 future days) for the hub's
 *           location. Supports custom location, day/night polling intervals,
 *           API call limits, advanced retries, and auto‑reset.
 *
 *  Original code by: Jon Wallace
 *  Modified and maintained by: David Ball-Quenneville
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
 *  v2.1.8 - 2026-05-02 - David Ball-Quenneville
 *    - Added user‑friendly descriptions to all commands and preferences.
 *    - Included default values and required markers.
 *    - No functional changes.
 *  v2.1.7 - 2026-05-02 - Removed misleading "Forecast Days" preference, hardcoded 6‑day forecast.
 *  v2.1.6 - 2026-05-02 - Extended max forecast days to 16 (preference only)
 *  v2.1.5 - 2026-05-02 - Added forecast risk text attributes and rounding fix
 *  v2.1.4 - 2026-05-02 - Removed syncDebugState and forecast auto‑fallback
 *  v2.1.3 - 2026-05-02 - Added syncDebugState and forced state init
 *  v2.1.2 - 2026-05-01 - Fixed NPEs and metadata access
 *  v2.1.1 - 2026-05-01 - Added debug state variables
 *  v2.1.0 - 2026-05-01 - Renamed forecast attributes, date format, resetApiCounters
 *  v2.0.0 - 2026-05-01 - Major enhancement: forecast UV, anchored scheduling, custom location
 *  v1.1.0 - 2026-05-01 - Fixed API fields, timezone parsing, scheduling bugs (David Ball-Quenneville)
 *  v1.0.0 - 2026-04-30 - Initial release (Jon Wallace)
 */

import groovy.transform.Field

@Field static final String DRIVER_VERSION = "2.1.8"

metadata {
    definition(
        name: "Open-Meteo UV Index and Illuminance Tracker Plus",
        namespace: "DGBQ",
        author: "David Ball-Quenneville",
        version: DRIVER_VERSION,
        vid: "generic-illuminance"
    ) {
        capability "IlluminanceMeasurement"
        capability "Sensor"
        capability "Refresh"

        command "refresh", [[name: "Refresh Now", description: "Manual Sync: Triggers an immediate update of current UV, illuminance, and forecast data. Does not affect the anchored day/night schedule."]]
        command "resetDefaults", [[name: "Reset Defaults", description: "Factory Reset: Restores all driver preferences to their original default values. Use this to start over if settings become misconfigured."]]
        command "resetApiCounters", [[name: "Reset API Counters", description: "Reset API Counter: Sets today's API call count back to zero without changing any other settings. Useful when you've increased the daily limit mid‑day."]]

        // Core attributes
        attribute "uvIndex", "number"
        attribute "uvRisk", "string"
        attribute "uvIndexClearSky", "number"
        attribute "solarRadiation", "number"
        attribute "illuminance", "number"
        attribute "lastUpdated", "string"

        // Status & management
        attribute "apiStatus", "string"
        attribute "nextUpdate", "string"
        attribute "lastPollDuration", "number"
        attribute "apiCallsToday", "number"

        // Forecast UV (numeric) – always today + 5 future days
        attribute "forecastUvToday", "number"
        attribute "forecastUvTomorrow", "number"
        attribute "forecastUvDay2", "number"
        attribute "forecastUvDay3", "number"
        attribute "forecastUvDay4", "number"
        attribute "forecastUvDay5", "number"

        // Forecast UV risk (text)
        attribute "forecastUvRiskToday", "string"
        attribute "forecastUvRiskTomorrow", "string"
        attribute "forecastUvRiskDay2", "string"
        attribute "forecastUvRiskDay3", "string"
        attribute "forecastUvRiskDay4", "string"
        attribute "forecastUvRiskDay5", "string"
    }

    preferences {
        section("<b>Location Configuration</b>") {
            input name: "useCustomLocation", type: "bool", title: "Use Custom Location?", description: "Enable to manually specify a location instead of using the hub's built‑in coordinates. This allows multiple driver instances for different places (e.g., home, cottage). (Default: false)", defaultValue: false, submitOnChange: true
            if (useCustomLocation) {
                input name: "customLat", type: "text", title: "Latitude", description: "*Required* when Use Custom Location is enabled. Latitude in decimal degrees (e.g., 43.6532). Positive for north, negative for south.", required: true
                input name: "customLong", type: "text", title: "Longitude", description: "*Required* when Use Custom Location is enabled. Longitude in decimal degrees (e.g., -79.3832). Positive for east, negative for west.", required: true
                input name: "customLocName", type: "text", title: "Location Name", description: "Optional friendly name for this location (appears in logs and state variables). Helps distinguish multiple driver instances. (Default: \"Custom\" if left blank)", required: false
            }
        }

        section("<b>Polling Schedule</b>") {
            input name: "daytimeStart", type: "time", title: "Daytime Start", description: "*Required* The time of day when daytime polling intervals begin. At this time, the driver forces an immediate poll and switches to the daytime interval. (Default: 06:00)", defaultValue: "06:00", required: true
            input name: "nighttimeStart", type: "time", title: "Nighttime Start", description: "*Required* The time of day when nighttime polling intervals begin. At this time, the driver forces an immediate poll and switches to the nighttime interval. (Default: 22:00)", defaultValue: "22:00", required: true
            input name: "dayInterval", type: "enum", title: "Daytime Poll Interval", description: "*Required* How often the driver polls the API during daytime hours. Shorter intervals give fresher data but consume more API calls. (Default: 30 minutes)", defaultValue: "30", options: [
                "10":"10 min","15":"15 min","30":"30 min","60":"1 hour","120":"2 hours","180":"3 hours",
                "240":"4 hours","300":"5 hours","360":"6 hours","420":"7 hours","480":"8 hours","540":"9 hours"
            ], required: true
            input name: "nightInterval", type: "enum", title: "Nighttime Poll Interval", description: "*Required* How often the driver polls the API during nighttime hours. Longer intervals save API calls when UV is low. (Default: 2 hours)", defaultValue: "120", options: [
                "10":"10 min","15":"15 min","30":"30 min","60":"1 hour","120":"2 hours","180":"3 hours",
                "240":"4 hours","300":"5 hours","360":"6 hours","420":"7 hours","480":"8 hours","540":"9 hours"
            ], required: true
        }

        section("<b>Usage Limits & Error Handling</b>") {
            input name: "maxCallsPerDay", type: "number", title: "Max API Calls Per Day", description: "*Required* Safety cap on the total number of API calls allowed in a 24‑hour period. Counter resets at midnight. (Default: 25)", defaultValue: 25, range: "1..200", required: true
            input name: "autoResetMidnight", type: "bool", title: "Auto‑Reset at Midnight", description: "When enabled, automatically clears error counters and the API call count at midnight. Keeps the driver fresh for the next day. (Default: true)", defaultValue: true
            input name: "enableAdvancedRetries", type: "bool", title: "Enable Advanced Retries", description: "Enable this to customise how many times the driver retries a failed API call and how long it waits between retries. (Default: false)", defaultValue: false, submitOnChange: true
            if (enableAdvancedRetries) {
                input name: "maxRetriesPref", type: "number", title: "Max Retries", description: "(Only if advanced retries enabled) Number of times to retry a failed API call before giving up. (Default: 3)", defaultValue: 3, range: "1..10"
                input name: "maxBackoffPref", type: "number", title: "Max Backoff (seconds)", description: "(Only if advanced retries enabled) Maximum wait time in seconds between retries. Exponential backoff builds up to this limit. (Default: 3600 seconds / 1 hour)", defaultValue: 3600, range: "60..86400"
            }
        }

        section("<b>Display Settings</b>") {
            input name: "dateTimeFormat", type: "enum", title: "Date/Time Format", description: "*Required* Choose how dates and times appear in “Last Updated”, “Next Update”, and other timestamp attributes. (Default: MM/DD/YYYY AM/PM)", defaultValue: "MM/DD/YYYY AM/PM", options: [
                "MM/DD/YYYY AM/PM", "YYYY-MM-DD (24-hour)", "YYYY-MM-DD AM/PM",
                "MM/DD/YYYY (24-hour)", "DD/MM/YYYY (24-hour)", "DD/MM/YYYY AM/PM",
                "Mon DD, YYYY hh:MM AM/PM", "Day, Mon DD, YYYY hh:MM AM/PM"
            ], required: true
        }

        section("<b>Logging & Debugging</b>") {
            input name: "logVerbosity", type: "enum", title: "Log Verbosity", description: "*Required* Controls how much detail is written to the log. “ERROR” shows only critical issues, “DEBUG” shows everything. (Default: INFO)", defaultValue: "INFO", options: ["ERROR","WARN","INFO","DEBUG"], required: true
            input name: "autoRevertDebug", type: "bool", title: "Auto‑Revert Debug", description: "When enabled, automatically changes logging back from DEBUG to INFO after 30 minutes to prevent excessive log entries. (Default: true)", defaultValue: true
        }

        section("<b>Advanced (Thresholds)</b>") {
            input name: "luxConversionFactor", type: "number", title: "Lux per W/m²", description: "Estimated lux per W/m² of solar radiation. The default (120) works well for outdoor conditions. Lower values reduce estimated illuminance. (Default: 120)", defaultValue: 120, range: "1..200"
            input name: "minIlluminanceChange", type: "number", title: "Min illuminance change (lux)", description: "Minimum change in lux required to trigger a new illuminance event. Reduces unnecessary updates when illuminance fluctuates slightly. (Default: 100)", defaultValue: 100, range: "0..100000"
            input name: "minUvIndexChange", type: "number", title: "Min UV index change", description: "Minimum change in UV index required to trigger a new UV event. Prevents constant updates when UV changes by less than this amount. (Default: 0.1)", defaultValue: 0.1, range: "0..20"
        }
    }
}

// ====== Lifecycle ======

def installed() {
    log.info "${metadata.definition.name} v${DRIVER_VERSION} installed."
    initState()
    updateStateWithSettings()
    initialize()
}

def updated() {
    log.info "Settings updated."
    unschedule()
    def oldMax = state.previousMaxCalls ?: 25
    def newMax = safeToInteger(settings.maxCallsPerDay, 25)
    if (newMax > oldMax) {
        logInfo "Max calls increased from ${oldMax} to ${newMax}. Resetting call counter."
        state.apiCallsToday = 0
        sendEvent(name: "apiCallsToday", value: 0)
    }
    state.previousMaxCalls = newMax

    if (settings.logVerbosity == "DEBUG" && settings.autoRevertDebug) runIn(1800, "revertDebug")

    sendEvent(name: "apiStatus", value: "Initialized")
    sendEvent(name: "apiCallsToday", value: state.apiCallsToday ?: 0)

    updateStateWithSettings()
    scheduleTransitionJobs()
    refresh()
}

def initialize() {
    sendEvent(name: "apiStatus", value: "Initialized")
    checkMidnightReset()
    updateStateWithSettings()
    scheduleTransitionJobs()
    refresh()
}

private void initState() {
    if (state.driverVersion == null) state.driverVersion = DRIVER_VERSION
    if (state.apiCallsToday == null) state.apiCallsToday = 0
    if (state.consecutiveErrors == null) state.consecutiveErrors = 0
    if (state.currentRetry == null) state.currentRetry = 0
    if (state.lastResetDate == null) state.lastResetDate = new Date().format("yyyy-MM-dd", location.timeZone)
    if (state.previousMaxCalls == null) state.previousMaxCalls = safeToInteger(settings.maxCallsPerDay, 25)
    
    // Debug state variables
    if (state.currentLat == null) state.currentLat = null
    if (state.currentLong == null) state.currentLong = null
    if (state.locationName == null) state.locationName = ""
    if (state.maxRetries == null) state.maxRetries = 3
    if (state.maxBackoff == null) state.maxBackoff = 3600
    if (state.preferencesValid == null) state.preferencesValid = false
}

def revertDebug() {
    log.info "Auto-reverting log verbosity to INFO."
    device.updateSetting("logVerbosity", [value: "INFO", type: "enum"])
}

def resetDefaults() {
    logInfo "Resetting all preferences to defaults."
    device.updateSetting("useCustomLocation", [value: false, type: "bool"])
    device.updateSetting("customLat", [value: "", type: "text"])
    device.updateSetting("customLong", [value: "", type: "text"])
    device.updateSetting("customLocName", [value: "", type: "text"])
    device.updateSetting("daytimeStart", [value: "06:00", type: "time"])
    device.updateSetting("nighttimeStart", [value: "22:00", type: "time"])
    device.updateSetting("dayInterval", [value: "30", type: "enum"])
    device.updateSetting("nightInterval", [value: "120", type: "enum"])
    device.updateSetting("maxCallsPerDay", [value: 25, type: "number"])
    device.updateSetting("autoResetMidnight", [value: true, type: "bool"])
    device.updateSetting("enableAdvancedRetries", [value: false, type: "bool"])
    device.updateSetting("maxRetriesPref", [value: 3, type: "number"])
    device.updateSetting("maxBackoffPref", [value: 3600, type: "number"])
    device.updateSetting("dateTimeFormat", [value: "MM/DD/YYYY AM/PM", type: "enum"])
    device.updateSetting("logVerbosity", [value: "INFO", type: "enum"])
    device.updateSetting("autoRevertDebug", [value: true, type: "bool"])
    device.updateSetting("luxConversionFactor", [value: 120, type: "number"])
    device.updateSetting("minIlluminanceChange", [value: 100, type: "number"])
    device.updateSetting("minUvIndexChange", [value: 0.1, type: "number"])
    state.apiCallsToday = 0
    state.consecutiveErrors = 0
    state.currentRetry = 0
    sendEvent(name: "apiCallsToday", value: 0)
    updateStateWithSettings()
    unschedule()
    scheduleTransitionJobs()
    refresh()
}

def resetApiCounters() {
    logInfo "Manual reset of API call counters."
    state.apiCallsToday = 0
    sendEvent(name: "apiCallsToday", value: 0)
}

def refresh() {
    logDebug "Manual refresh triggered."
    pollOpenMeteo()
}

// ====== Helper to update state variables for debugging ======

private void updateStateWithSettings() {
    state.driverVersion = DRIVER_VERSION
    def coords = getCoordinates()
    if (coords) {
        state.currentLat = coords.lat
        state.currentLong = coords.lon
    } else {
        state.currentLat = null
        state.currentLong = null
    }
    state.locationName = settings.useCustomLocation ? (settings.customLocName ?: "Custom") : (location.name ?: "Hubitat Hub")
    state.maxRetries = settings.enableAdvancedRetries ? (safeToInteger(settings.maxRetriesPref, 3)) : 3
    state.maxBackoff = settings.enableAdvancedRetries ? (safeToInteger(settings.maxBackoffPref, 3600)) : 3600
    state.preferencesValid = (coords != null)
}

private int safeToInteger(value, int defaultValue) {
    if (value == null) return defaultValue
    try {
        return value.toString().toInteger()
    } catch (e) {
        return defaultValue
    }
}

// ====== Scheduling (anchored) ======

def scheduleTransitionJobs() {
    unschedule("startDaytimePolling")
    unschedule("startNighttimePolling")
    if (settings.daytimeStart) schedule(settings.daytimeStart, "startDaytimePolling")
    if (settings.nighttimeStart) schedule(settings.nighttimeStart, "startNighttimePolling")
}

def startDaytimePolling() {
    logInfo "Daytime period started – forcing poll and resetting schedule."
    pollOpenMeteo()
    scheduleNextPoll()
}

def startNighttimePolling() {
    logInfo "Nighttime period started – forcing poll and resetting schedule."
    pollOpenMeteo()
    scheduleNextPoll()
}

private Date getCurrentPeriodStart(Date now) {
    boolean isDay = isDaytime(now)
    if (isDay) return timeToday(settings.daytimeStart, location.timeZone)
    Date todayDayStart = timeToday(settings.daytimeStart, location.timeZone)
    if (now < todayDayStart) return timeToday(settings.nighttimeStart, location.timeZone) - 1
    return timeToday(settings.nighttimeStart, location.timeZone)
}

private Date getNextPollTime(Date periodStart, int intervalMinutes, Date now) {
    long intervalMillis = intervalMinutes * 60 * 1000L
    long diff = now.time - periodStart.time
    long intervalsPassed = diff / intervalMillis
    long nextMillis = periodStart.time + (intervalsPassed + 1) * intervalMillis
    if (nextMillis <= now.time) nextMillis = now.time + intervalMillis
    return new Date(nextMillis)
}

def scheduleNextPoll() {
    unschedule("pollOpenMeteo")
    Date now = new Date()
    int interval = (isDaytime(now) ? safeToInteger(settings.dayInterval, 30) : safeToInteger(settings.nightInterval, 120))
    Date periodStart = getCurrentPeriodStart(now)
    Date nextRun = getNextPollTime(periodStart, interval, now)
    logDebug "Scheduling next poll at ${formatDateTime(nextRun)}"
    runOnce(nextRun, "pollOpenMeteo")
    sendEvent(name: "nextUpdate", value: formatDateTime(nextRun))
}

// ====== Main Polling Logic ======

def pollOpenMeteo() {
    logDebug "Polling Open‑Meteo API."
    checkMidnightReset()

    def coords = getCoordinates()
    if (!coords) {
        logError "No valid coordinates."
        sendEvent(name: "apiStatus", value: "ERROR_NO_COORDS")
        scheduleNextPoll()
        return
    }

    int callsToday = state.apiCallsToday ?: 0
    int maxCalls = safeToInteger(settings.maxCallsPerDay, 25)
    if (callsToday >= maxCalls) {
        logWarn "Daily API call limit (${maxCalls}) reached. Poll skipped."
        sendEvent(name: "apiStatus", value: "API_LIMIT_REACHED")
        scheduleNextPoll()
        return
    }

    // Hardcoded to request 6 days (today + 5 future days)
    int reqDays = 6
    String url = buildApiUrl(coords.lat, coords.lon, reqDays)

    def startTime = now()
    try {
        httpGet([uri: url, contentType: "application/json", timeout: 15]) { resp ->
            def duration = (now() - startTime) / 1000.0
            sendEvent(name: "lastPollDuration", value: Math.round(duration * 10) / 10)

            if (resp.status == 200) {
                state.consecutiveErrors = 0
                state.currentRetry = 0
                state.apiCallsToday = (state.apiCallsToday ?: 0) + 1
                sendEvent(name: "apiCallsToday", value: state.apiCallsToday)
                sendEvent(name: "apiStatus", value: "SUCCESS")
                sendEvent(name: "lastUpdated", value: formatDateTime(new Date()))

                processResponse(resp.data, reqDays)
            } else {
                handleApiError(resp, reqDays)
            }
            scheduleNextPoll()
        }
    } catch (Exception e) {
        logError "HTTP GET exception: ${e.message}"
        handleException(e)
        scheduleNextPoll()
    }
}

private String buildApiUrl(lat, lon, int forecastDays) {
    return "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=uv_index,uv_index_clear_sky,shortwave_radiation&forecast_days=${forecastDays}&timezone=auto"
}

def processResponse(data, int requestedDays) {
    def uvIndex = getClosestHourlyValue(data, "uv_index")
    def uvIndexClearSky = getClosestHourlyValue(data, "uv_index_clear_sky")
    def solarRadiation = getClosestHourlyValue(data, "shortwave_radiation")

    updateUvIndex(uvIndex)
    updateUvIndexClearSky(uvIndexClearSky)
    updateIlluminance(solarRadiation)

    def dailyMaxUv = computeDailyMaxUv(data)
    // requestedDays = 6, we want to display today + 5 future days = 6 total
    updateForecastAttributes(dailyMaxUv, requestedDays)
}

def getClosestHourlyValue(data, variableName) {
    def times = data?.hourly?.time
    def values = data?.hourly?.get(variableName)
    if (!times || !values) return null
    def now = new Date()
    def closestValue = null, closestDiff = null
    values.eachWithIndex { val, idx ->
        if (val != null && idx < times.size()) {
            def sampleTime = parseOpenMeteoTime(times[idx])
            if (sampleTime) {
                def diff = Math.abs(sampleTime.time - now.time)
                if (closestDiff == null || diff < closestDiff) {
                    closestDiff = diff
                    closestValue = val
                }
            }
        }
    }
    return closestValue
}

def parseOpenMeteoTime(String timeStr) {
    try { return Date.parse("yyyy-MM-dd'T'HH:mm", timeStr) } catch(e) { return null }
}

def updateUvIndex(val) {
    if (val == null) return
    BigDecimal newVal = new BigDecimal(val.toString()).setScale(1, BigDecimal.ROUND_HALF_UP)
    def current = device.currentValue("uvIndex")
    BigDecimal threshold = (settings.minUvIndexChange != null) ? new BigDecimal(settings.minUvIndexChange.toString()) : new BigDecimal("0.1")
    if (current == null || (newVal - new BigDecimal(current.toString())).abs() >= threshold) {
        sendEvent(name: "uvIndex", value: newVal)
        sendEvent(name: "uvRisk", value: getUvRisk(newVal))
    }
}

def updateUvIndexClearSky(val) {
    if (val == null) return
    sendEvent(name: "uvIndexClearSky", value: new BigDecimal(val.toString()).setScale(1, BigDecimal.ROUND_HALF_UP))
}

def updateIlluminance(solarRadiation) {
    if (solarRadiation == null) return
    BigDecimal radiation = new BigDecimal(solarRadiation.toString()).setScale(1, BigDecimal.ROUND_HALF_UP)
    BigDecimal factor = (settings.luxConversionFactor != null) ? new BigDecimal(settings.luxConversionFactor.toString()) : new BigDecimal("120")
    BigDecimal illuminance = (radiation * factor).setScale(0, BigDecimal.ROUND_HALF_UP)
    def current = device.currentValue("illuminance")
    BigDecimal threshold = (settings.minIlluminanceChange != null) ? new BigDecimal(settings.minIlluminanceChange.toString()) : new BigDecimal("100")
    if (current == null || (illuminance - new BigDecimal(current.toString())).abs() >= threshold) {
        sendEvent(name: "illuminance", value: illuminance, unit: "lux")
    }
    sendEvent(name: "solarRadiation", value: radiation, unit: "W/m²")
}

def getUvRisk(uv) {
    BigDecimal u = uv instanceof BigDecimal ? uv : new BigDecimal(uv.toString())
    if (u < 3) return "low"
    if (u < 6) return "moderate"
    if (u < 8) return "high"
    if (u < 11) return "very high"
    return "extreme"
}

def computeDailyMaxUv(data) {
    def times = data?.hourly?.time
    def uvValues = data?.hourly?.uv_index
    if (!times || !uvValues) return [:]
    def dailyMax = [:]
    def dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
    dateFormatter.setTimeZone(location.timeZone ?: TimeZone.getTimeZone("UTC"))
    times.eachWithIndex { timeStr, idx ->
        if (idx < uvValues.size() && uvValues[idx] != null) {
            def date = parseOpenMeteoTime(timeStr)
            if (date) {
                String localDate = dateFormatter.format(date)
                double uv = uvValues[idx] as double
                if (!dailyMax.containsKey(localDate) || uv > dailyMax[localDate]) dailyMax[localDate] = uv
            }
        }
    }
    return dailyMax
}

def updateForecastAttributes(dailyMaxMap, int totalDays) {
    // totalDays is the number of days we have in the map (including today)
    def sortedDates = dailyMaxMap.keySet().sort()
    def todayStr = new Date().format("yyyy-MM-dd", location.timeZone)
    def todayIndex = sortedDates.findIndexOf { it >= todayStr }
    if (todayIndex < 0) {
        clearForecastAttributes()
        return
    }
    def forecastList = []
    for (int i = todayIndex; i < Math.min(todayIndex + totalDays, sortedDates.size()); i++) {
        forecastList << dailyMaxMap[sortedDates[i]]
    }
    // Pad with null if fewer days than expected
    while (forecastList.size() < totalDays) forecastList << null

    // Helper to send both numeric (rounded) and risk attributes
    def sendForecast = { String suffix, BigDecimal value ->
        def rounded = (value != null) ? Math.round(value * 10) / 10 : null
        sendEvent(name: "forecastUv${suffix}", value: rounded != null ? rounded : "N/A")
        sendEvent(name: "forecastUvRisk${suffix}", value: rounded != null ? getUvRisk(rounded) : "N/A")
    }

    sendForecast("Today", forecastList.size() > 0 ? forecastList[0] : null)
    sendForecast("Tomorrow", forecastList.size() > 1 ? forecastList[1] : null)
    sendForecast("Day2", forecastList.size() > 2 ? forecastList[2] : null)
    sendForecast("Day3", forecastList.size() > 3 ? forecastList[3] : null)
    sendForecast("Day4", forecastList.size() > 4 ? forecastList[4] : null)
    sendForecast("Day5", forecastList.size() > 5 ? forecastList[5] : null)
}

def clearForecastAttributes() {
    def clearOne = { String suffix ->
        sendEvent(name: "forecastUv${suffix}", value: "N/A")
        sendEvent(name: "forecastUvRisk${suffix}", value: "N/A")
    }
    clearOne("Today")
    clearOne("Tomorrow")
    clearOne("Day2")
    clearOne("Day3")
    clearOne("Day4")
    clearOne("Day5")
}

// ====== Error Handling & Retries ======

def handleApiError(response, int requestedDays) {
    logError "API error ${response.status}: ${response.errorMessage ?: 'No message'}"
    sendEvent(name: "apiStatus", value: "ERROR_${response.status}")

    if (settings.enableAdvancedRetries) {
        int maxRetries = safeToInteger(settings.maxRetriesPref, 3)
        int current = state.currentRetry ?: 0
        if (current < maxRetries) {
            state.currentRetry = current + 1
            int backoff = calculateBackoff(state.currentRetry)
            logInfo "Retry ${state.currentRetry}/${maxRetries} in ${backoff}s."
            runIn(backoff, "pollOpenMeteo")
            return
        } else {
            logError "Max retries reached."
            state.currentRetry = 0
            state.consecutiveErrors = (state.consecutiveErrors ?: 0) + 1
        }
    } else {
        state.consecutiveErrors = (state.consecutiveErrors ?: 0) + 1
    }
}

def handleException(Exception e) {
    logError "Exception: ${e.message}"
    sendEvent(name: "apiStatus", value: "EXCEPTION")
    if (settings.enableAdvancedRetries) {
        int maxRetries = safeToInteger(settings.maxRetriesPref, 3)
        int current = state.currentRetry ?: 0
        if (current < maxRetries) {
            state.currentRetry = current + 1
            int backoff = calculateBackoff(state.currentRetry)
            runIn(backoff, "pollOpenMeteo")
            return
        }
    }
    state.currentRetry = 0
    state.consecutiveErrors = (state.consecutiveErrors ?: 0) + 1
}

int calculateBackoff(int attempt) {
    int delay = 30 * (Math.pow(2, attempt - 1) as int)
    int maxBack = safeToInteger(settings.maxBackoffPref, 3600)
    return Math.min(delay, maxBack)
}

// ====== Midnight Reset ======

def checkMidnightReset() {
    String today = new Date().format("yyyy-MM-dd", location.timeZone)
    if (state.lastResetDate != today) midnightMaintenance()
}

def midnightMaintenance() {
    logInfo "Midnight maintenance – resetting API call counter."
    state.apiCallsToday = 0
    sendEvent(name: "apiCallsToday", value: 0)
    if (settings.autoResetMidnight) {
        state.consecutiveErrors = 0
        state.currentRetry = 0
    }
    state.lastResetDate = new Date().format("yyyy-MM-dd", location.timeZone)
    scheduleNextPoll()
}

// ====== Helpers ======

private Map getCoordinates() {
    if (settings.useCustomLocation) {
        if (settings.customLat && settings.customLong) {
            return [lat: settings.customLat.toDouble(), lon: settings.customLong.toDouble()]
        }
        return null
    }
    def lat = location?.latitude
    def lon = location?.longitude
    return (lat != null && lon != null) ? [lat: lat, lon: lon] : null
}

private boolean isDaytime(Date now = new Date()) {
    if (!settings.daytimeStart || !settings.nighttimeStart) return true
    Date dayStart = timeToday(settings.daytimeStart, location.timeZone)
    Date nightStart = timeToday(settings.nighttimeStart, location.timeZone)
    if (dayStart < nightStart) return now >= dayStart && now < nightStart
    return now >= dayStart || now < nightStart
}

String formatDateTime(Date date) {
    if (!date) return "N/A"
    String fmt = settings.dateTimeFormat ?: "MM/DD/YYYY AM/PM"
    String pattern
    switch(fmt) {
        case "MM/DD/YYYY AM/PM": pattern = "MM/dd/yyyy hh:mm a"; break
        case "YYYY-MM-DD (24-hour)": pattern = "yyyy-MM-dd HH:mm"; break
        case "YYYY-MM-DD AM/PM": pattern = "yyyy-MM-dd hh:mm a"; break
        case "MM/DD/YYYY (24-hour)": pattern = "MM/dd/yyyy HH:mm"; break
        case "DD/MM/YYYY (24-hour)": pattern = "dd/MM/yyyy HH:mm"; break
        case "DD/MM/YYYY AM/PM": pattern = "dd/MM/yyyy hh:mm a"; break
        case "Mon DD, YYYY hh:MM AM/PM": pattern = "MMM dd, yyyy hh:mm a"; break
        case "Day, Mon DD, YYYY hh:MM AM/PM": pattern = "EEEE, MMM dd, yyyy hh:mm a"; break
        default: pattern = "MM/dd/yyyy hh:mm a"
    }
    return date.format(pattern, location.timeZone)
}

// ====== Logging ======

def logDebug(msg) { if (settings.logVerbosity == "DEBUG") log.debug "${device.label}: ${msg}" }
def logInfo(msg) { if (settings.logVerbosity in ["DEBUG","INFO"]) log.info "${device.label}: ${msg}" }
def logWarn(msg) { if (settings.logVerbosity in ["DEBUG","INFO","WARN"]) log.warn "${device.label}: ${msg}" }
def logError(msg) { log.error "${device.label}: ${msg}" }