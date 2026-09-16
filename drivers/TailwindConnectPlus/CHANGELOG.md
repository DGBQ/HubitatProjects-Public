# Tailwind Connect Plus (DGBQ Fork) – CHANGELOG

***

## v1.3.2 - 2026-09-09 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **Bug Fixes**

* **Removed LED Brightness Control** – Removed due to compatibility issues with some controller models/firmware versions. The `/led` endpoint is not available on all controllers.

### **Changes**

* **Removed "Controller Appearance" Section** – No longer needed.

* **Removed `setLedBrightness` Command** – Removed from metadata and code.

* **Parent Driver Version** – Updated to `v1.3.2`.

* **Child Driver Version** – Remains at `v1.1.3`.

***

## v1.3.1 - 2026-09-09 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed LED Brightness Auto-Apply** – LED brightness now applies immediately when preferences are saved.

### **Changes**

* **Parent Driver Version** – Updated to `v1.3.1`.

* **Child Driver Version** – Remains at `v1.1.3`.

***

## v1.3.0 - 2026-09-09 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **New Features**

* **LED Brightness Control** – Added preference and command to control LED brightness (removed in v1.3.2).

* **Controller Appearance Section** – New preferences section for LED brightness (removed in v1.3.2).

* **Identify Success Log** – Added `logInfo "Identify command completed successfully"` at INFO level.

### **Bug Fixes**

* **Fixed Identify Button Label** – Changed from `Identify Controller` back to `Identify` for consistency with other commands.

* **Fixed LED HTTP Method** – Changed from `httpGet` to `httpPost` for Identify command (v1.2.9).

### **Changes**

* **Parent Driver Version** – Updated to `v1.3.0`.

* **Child Driver Version** – Remains at `v1.1.3`.

***

## v1.2.9 - 2026-09-09 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **New Features**

* **Identify Rename** – Renamed `Identify` to `Identify Controller` for clarity (updated in v1.3.0).

* **Identify Description Update** – Clarified it identifies controllers, not individual doors.

* **Hide Internal Commands** – Removed `childOpen` and `childClose` from parent metadata (UI cleanup).

### **Bug Fixes**

* **Fixed Identify HTTP Method** – Changed from `httpGet` to `httpPost` to resolve 405 Method Not Allowed error.

### **Changes**

* **Parent Driver Version** – Updated to `v1.2.9`.

* **Child Driver Version** – Remains at `v1.1.3`.

***

## v1.2.8 - 2026-09-09 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **Changes**

* **Removed `poll` Command** – Legacy command removed; use `refresh` instead.

***

## v1.2.7 - 2026-08-31 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **New Features**

* **Command Descriptions** – Added descriptions for non-parameterized commands (refresh, clearDebug, identify, verifySetup) using Outlook driver format.

* **Date/Time Format Preference** – Added 8 display options for timestamps (default: MM/DD/YYYY AM/PM).

* **Door Status Change INFO Log** – Logs when door status changes from any source (app, remote, driver).

### **Bug Fixes**

* **Fixed Metadata Command Format** – Correctly implemented command descriptions following Outlook iCal Switch format.

* **Removed `poll` Command** – Legacy command removed; use `refresh` instead.

### **Changes**

* **Parent Driver Version** – Updated to `v1.2.7`.

* **Child Driver Version** – Updated to `v1.1.3` (synchronized).

***

## v1.2.6 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Metadata Command Format** – Continued refinement of command description format.

***

## v1.2.5 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Metadata Syntax** – Removed descriptions from parameterized commands (childOpen, childClose) due to Hubitat limitation.

***

## v1.2.4 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Metadata Command Format** – Attempted command descriptions using Outlook driver format.

***

## v1.2.3 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Metadata** – Parameterized commands (childOpen, childClose) cannot have descriptions.

* **Descriptions Retained** – Non-parameterized commands (refresh, clearDebug, identify, verifySetup) keep descriptions.

***

## v1.2.2 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Metadata Syntax Error** – Corrected command syntax to save without errors.

***

## v1.2.1 - 2026-08-31 - David Ball-Quenneville

### **Bug Fixes**

* **Added Missing Command Descriptions** – Added descriptions to all commands in metadata.

***

## v1.2.0 - 2026-08-31 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **New Features**

* **Preference Descriptions** – All preferences now have clear descriptions with default values shown in UI.

* **Command Descriptions** – All commands have descriptive text shown in UI (updated in v1.2.7).

* **Installation Verification** – Added `verifySetup()` command to test configuration and controller reachability.

* **Improved Error Messages** – Actionable guidance when IP is invalid, token is missing, or door fails to operate.

* **Improved Child Sync Log Message** – Changed from "DOESN'T match real door" to "Syncing child X from Y to Z".

* **Door Status Change INFO Log** – Logs INFO message when any status change is detected (regardless of source).

* **Date/Time Format Preference** – 8 display options for timestamps.

* **lastOpen / lastClosed Attributes** – Added to child devices (updated by parent on status change).

### **Changes**

* **Parent Driver Version** – Updated to `v1.2.0`.

* **Child Driver Version** – Updated to `v1.1.2` (added lastOpen and lastClosed attributes).

***

## v1.1.9 - 2026-08-18 - David Ball-Quenneville

### **Breaking Changes**

* None – this is a feature release.

### **New Features**

* **Simplified Command Flow** – Commands are sent once, fast polling confirms success or timeout.

* **Failure Detection** – If the door doesn't reach the desired state within `garageDoorTimeout` (default: 60 seconds), it's marked as a failure.

* **Improved User Feedback** – `driverHealth` and `lastError` attributes clearly communicate success or failure.

### **Bug Fixes**

* **Removed All Retry Logic** – Eliminated unnecessary door cycles. The command is sent once, and fast polling confirms the result.

* **Fixed Retry Method Call** – Removed `retryOpenClose` and `executeRetry` methods entirely.

* **Fixed `runIn()` Issues** – No longer attempts to pass individual arguments to `runIn()`.

### **Changes**

* **Retry Logic Removed** – The driver no longer attempts retries on command failure. This reduces unnecessary door cycles.

* **Timeout-Based Failure Detection** – The `garageDoorTimeout` setting (default: 60 seconds) is now the primary failure detection mechanism.

* **Preference Removal** – Removed `maxRetries` and `retryDelay` preferences (no longer needed).

***

## v1.1.8 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Stream Closed Error** – Command is sent once; fast polling confirms the door's state.

* **Simplified Command Flow** – Removed retry logic to prevent unnecessary door cycles.

***

## v1.1.7 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Retry Logic** – Treat `0` response as success (the controller's normal acknowledgment).

* **Improved Logging** – Shows actual vs expected response in logs.

***

## v1.1.6 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed `runIn()` Call** – Changed to use `[data: map]` format instead of individual arguments.

* **Simplified Retry Logic** – Single method with map parameter.

***

## v1.1.5 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed `MissingMethodException`** – Corrected `retryOpenClose()` call to pass arguments correctly.

***

## v1.1.4 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed `logDebug(postParams)`** – Converted Map to String before logging.

***

## v1.1.3 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed `NumberFormatException`** – Now uses `state.lastNumericStatus` instead of parsing the `Status` attribute.

* **Added `state.lastNumericStatus`** – Tracks numeric status separately from human-readable `Status` attribute.

***

## v1.1.2 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed `SimpleDateFormat` Error** – Now uses Groovy's built-in `Date.format()` method instead of `SimpleDateFormat`.

***

## v1.1.1 - 2026-08-18 - David Ball-Quenneville

### **Bug Fixes**

* **Fixed Groovy Syntax Error** – Wrapped negative map keys in parentheses: `(-1)` → `(-1)`.

* **Corrected Duplicate Key** – Changed duplicate `-1` key to `-3` in `getStatusMessage()`.

***

## v1.1.0 - 2026-08-18 - David Ball-Quenneville

### **New Features**

* **Error Handling** – Wrapped all HTTP calls in try-catch blocks to prevent crashes.

* **Connection Retry Logic** – Added configurable retries with delay.

* **Driver Health / Command Status** – Added `driverHealth` attribute to track driver status.

* **Last Error Attribute** – Added `lastError` attribute showing the most recent error.

* **Last Fetch Attribute** – Added `lastFetch` attribute showing when the last successful poll occurred.

* **Enhanced Status Messages** – Human-readable status messages instead of numeric codes.

* **Polling Watchdog** – Self-healing timer that detects and repairs broken polling chains.

* **Initialize Robustness** – Ensures polling resumes after Hubitat reboots.

* **Identify Command** – Flash controller LED for identification.

### **Changes**

* **Parent Driver Version** – Updated to `v1.1.0`.

* **Child Driver Version** – Updated to `v1.1.0` (synchronized).

* **Logging Improvements** – Added `getLogLevel()` and `debugAdd()` methods.

***

## v1.0.0 - 2026-08-12 - David Ball-Quenneville

### **Breaking Changes**

* None – this is the initial release of the DGBQ fork.

### **New Features**

* **Initial Fork** – Community fork of the `dbadge` / `Gelix` Tailwind Garage Door driver.

* **Bug Fix** – Fixed `toInteger()` error by casting HTTP response to String first.

* **Driver Header** – Added full copyright, Apache 2.0 license, and revision history header.

* **Version Constant** – Added `@Field static final String DRIVER_VERSION = "v1.0.0"`.

* **driverVersion Attribute** – Added attribute to display version in device details.

* **refresh Command** – Added manual refresh/poll command for troubleshooting.

* **Auto-Revert Debug** – Added preference to automatically disable debug logging after 2 hours.

* **Logging Helpers** – Implemented `logDebug`, `logInfo`, `logWarn`, `logError` helpers.

* **Sectioned Preferences** – Grouped preferences into collapsible sections.

### **Bug Fixes**

* **Fixed `toInteger()` Error** – Addressed `groovy.lang.MissingMethodException` caused by recent Hubitat platform updates.

### **Changes**

* **Driver Renamed** – Now called **Tailwind Connect Plus** (Parent) and **Tailwind Connect Plus - Child**.

* **Namespace Changed** – Updated to `DGBQ`.

* **Author Updated** – Now maintained by David Ball-Quenneville.

* **Import URLs Updated** – Points to the new DGBQ repository.

***

## v0.0.0 - Pre-Fork - dbadge / Gelix

### **Original Driver Details**

* Local (LAN) control via Tailwind Local API

* Supports up to 3 garage doors

* Parent-child architecture with child devices

* Capabilities: `GarageDoorControl`, `Actuator`, `ContactSensor`, `Sensor`

***

## 📊 Version History Summary

| Version    | Date       | Author         | Key Changes                                                                                                                                                                                       |
| ---------- | ---------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **v1.3.2** | 2026-09-09 | DGBQ           | **Phase 4 Complete** – Removed LED Brightness Control (not supported on all models), Parent v1.3.2, Child v1.1.3                                                                                  |
| **v1.3.0** | 2026-09-09 | DGBQ           | Added LED Brightness Control (removed in v1.3.2), Identify success log, Identify button label fix, Parent v1.3.0                                                                                  |
| **v1.2.9** | 2026-09-09 | DGBQ           | Identify rename, description update, hide childOpen/childClose, Identify httpPost fix                                                                                                             |
| **v1.2.7** | 2026-08-31 | DGBQ           | **Phase 3 Complete** – Command descriptions (Outlook format), Date/Time Format, Door Status Change INFO Log, removed poll command, metadata fixes, Child v1.1.3                                   |
| **v1.2.0** | 2026-08-31 | DGBQ           | **Phase 3 Initial Release** – Preference Descriptions, verifySetup, improved error messages, child sync log, lastOpen/lastClosed, Child v1.1.2                                                    |
| **v1.1.9** | 2026-08-18 | DGBQ           | **Phase 2 Complete** – Removed retry logic, timeout-based failure detection, driverHealth, lastError, lastFetch, Enhanced Status, Watchdog, Initialize Robustness, Identify Command, Child v1.1.1 |
| **v1.1.8** | 2026-08-18 | DGBQ           | Fixed Stream Closed error; Simplified command flow                                                                                                                                                |
| **v1.1.7** | 2026-08-18 | DGBQ           | Fixed retry logic; Treat `0` as success                                                                                                                                                           |
| **v1.1.6** | 2026-08-18 | DGBQ           | Fixed `runIn()` call                                                                                                                                                                              |
| **v1.1.5** | 2026-08-18 | DGBQ           | Fixed MissingMethodException in retryOpenClose                                                                                                                                                    |
| **v1.1.4** | 2026-08-18 | DGBQ           | Fixed logDebug(postParams) conversion                                                                                                                                                             |
| **v1.1.3** | 2026-08-18 | DGBQ           | Fixed NumberFormatException in poll                                                                                                                                                               |
| **v1.1.2** | 2026-08-18 | DGBQ           | Fixed SimpleDateFormat error                                                                                                                                                                      |
| **v1.1.1** | 2026-08-18 | DGBQ           | Fixed Groovy syntax error with negative map keys                                                                                                                                                  |
| **v1.1.0** | 2026-08-18 | DGBQ           | **Phase 2 Initial Release** – Error Handling, Retry Logic, Driver Health, Last Error, Last Fetch, Enhanced Status, Watchdog, Initialize Robustness, Identify Command                              |
| **v1.0.0** | 2026-08-12 | DGBQ           | **Initial DGBQ Fork** – Fixed `toInteger()` error; Added driver header, versioning, refresh, auto-revert debug                                                                                    |
| **v0.0.0** | Pre-Fork   | dbadge / Gelix | Original Tailwind Garage Door Driver                                                                                                                                                              |

***

## ✅ All Phases Complete

| Phase                                   | Status     | Version |
| --------------------------------------- | ---------- | ------- |
| **Phase 1 – Foundation & Bug Fix**      | ✅ COMPLETE | v1.0.0  |
| **Phase 2 – Reliability & Visibility**  | ✅ COMPLETE | v1.1.9  |
| **Phase 3 – User Experience & Logging** | ✅ COMPLETE | v1.2.7  |
| **Phase 4 – Advanced Features**         | ✅ COMPLETE | v1.3.2  |

***
