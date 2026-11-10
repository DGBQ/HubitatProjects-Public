# 🗺️ Rollease Acmeda Hubitat Integration – Roadmap & Phased Plan

### _Working Planning Document – Not For Publication_

***

## 🔢 Version Control

| Item                       | Details                                      |
| -------------------------- | -------------------------------------------- |
| **Document Version**       | 1.3.0                                        |
| **Document Status**        | Living Document – Updated as phases progress |
| **Audience**               | DGBQ (Project Manager) + AI Code Master      |
| **Project Name**           | Rollease Acmeda Hubitat Integration (DGBQ)   |
| **Current Driver Version** | Hub v3.3.26 / Shade v2.5.9                   |
| **Next Target Version**    | N/A (Maintenance Mode)                       |

> **📌 Note:** This document is a **working planning tool** between the Project Manager and the AI Code Master. It is intentionally **not published** to GitHub and **not linked** from the README. Its purpose is to keep a live view of where we are, what's next, and what was deliberately left out — and to preserve the reasoning behind those decisions.

***

## 🗺️ Roadmap Overview

| Phase         | Name                                              | Status           | Version      |
| ------------- | ------------------------------------------------- | ---------------- | ------------ |
| **Phase 1**   | Critical Bug Fixes (Parent Hub)                   | ✅ **COMPLETE**   | Hub v3.3.24  |
| **Hot Fix 1** | Battery Voltage Formula Recalibration             | ✅ **COMPLETE**   | Shade v2.5.4 |
| **Phase 2A**  | Retry Logic + Logging UX (Child Shade)            | ✅ **COMPLETE**   | Shade v2.5.5 |
| **Hot Fix 2** | Exponential Backoff & Repeater Protection         | ✅ **COMPLETE**   | Shade v2.5.7 |
| **Hot Fix 3** | Smart Stateful Retry (Heartbeat Check)            | ✅ **COMPLETE**   | Shade v2.5.8 |
| **Phase 2B**  | Logging UX Overhaul + Initialize Fix (Parent Hub) | ✅ **COMPLETE**   | Hub v3.3.25  |
| **Hot Fix 4** | Refresh Guard (Parent Hub)                        | ✅ **COMPLETE**   | Hub v3.3.26  |
| **Phase 3B**  | Actionable Event Tracking (Child Shade)           | ✅ **COMPLETE**   | Shade v2.5.9 |
| **Phase 3A**  | Health Check & Auto-Recovery (Parent Hub)         | ⏸️ **DEFERRED**  | TBD          |
| **Phase 4**   | Documentation & Release (ongoing)                 | ⏳ **CONTINUOUS** | TBD          |

***

## ✅ Phase 1 – Critical Bug Fixes (Parent Hub)

**Status:** COMPLETE — Hub v3.3.24 (2026-09-11)\
**Scope:** Fix the three bugs that caused device corruption and address mismatches during the XDG recovery incident.

### What was actually done

Three targeted fixes to the Parent Hub driver. All were identified as root causes of the recent accidental deletion of a working shade.

| #   | Change                                                                                       | Rationale                                                                                                          |
| --- | -------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| 1.1 | Fix `ShadeAdd()` – add `child.initialize()` after setting `motorAddress`                     | Ensures new child devices are initialized immediately and receive the correct address from the start.              |
| 1.2 | Fix `parse()` – add `child.initialize()` and address validation for existing children        | Prevents the "default 000" bug. Auto-corrects mismatched `motorAddress` when parent/child state diverges.          |
| 1.3 | Fix `ShadeRemove()` – add safety check comparing child's `motorAddress` against requested ID | Prevents accidental deletion of the wrong device. Deletion is aborted with a warning if the address doesn't match. |

### Key outcomes

* Accidental wrong-device deletion is now impossible from `ShadeRemove`.

* New children always receive the correct motor address on creation.

* Existing children auto-correct their address if the parent detects a mismatch.

***

## ✅ Hot Fix 1 – Battery Voltage Formula Recalibration

**Status:** COMPLETE — Shade v2.5.4 (2026-09-11)\
**Scope:** Fix battery percentage reporting so it matches the Rollease app within a reasonable tolerance.

### What was done

Production testing across eight shades revealed a consistent gap between the driver's reported percentage and the Rollease app's percentage. The formula was recalibrated to use **9.5V = 0%** and **12.6V = 100%**. Testing after the change showed all eight shades within **±5%** of the app.

***

## ✅ Phase 2A – Retry Logic + Logging UX (Child Shade)

**Status:** COMPLETE — Shade v2.5.5 (2026-09-16)\
**Scope:** Fix the false retry/jitter warnings observed on working shades, fix two latent retry state bugs, and unify the logging UI.

### What was actually done

Added `Position Tolerance` (default 2) and intermediate position reset. Fixed `unschedule()` method names. Added `state.commandId` stale-callback guard. Replaced logging toggles with `Log Verbosity` dropdown.

***

## ✅ Hot Fix 2 – Exponential Backoff & Repeater Protection (Child Shade)

**Status:** COMPLETE — Shade v2.5.7 (2026-10-23)\
**Scope:** Fix the RF repeater flooding caused by the retry logic during prolonged shade failures.

### What was actually done

In late October 2026, a failing shade (XDG) caused the driver to send 5 RF commands in 60 seconds. This flooded the RF repeater's internal buffer, causing it to lock up. The fix was to implement a 50% exponential backoff and a hard cap on retries (dropdown 0-3).

***

## ✅ Hot Fix 3 – Smart Stateful Retry (Child Shade)

**Status:** COMPLETE — Shade v2.5.8 (2026-10-28)\
**Scope:** Implement a Heartbeat Check to differentiate between a shade that missed the RF packet (Deaf), a shade that is moving (Moving), and a shade that is physically jammed (Stuck).

### What was actually done

Added an 8-second Heartbeat Check. If the shade is silent, it fires the jitter. If it's moving, it resets the timer. If it's stuck, it aborts. This eliminated unnecessary retries and kept the logs clean.

***

## ✅ Phase 2B – Logging UX Overhaul + Initialize Fix (Parent Hub)

**Status:** COMPLETE — Hub v3.3.25 (2026-10-28)\
**Scope:** Apply the same Log Verbosity dropdown to the Parent Hub driver, remove the no-op Refresh button, and fix the `initialize()` blocking issue.

### What was done

Replaced the Parent Hub logging toggles with the `Log Verbosity` dropdown. Removed the `capability "Refresh"`. Moved the child refresh loop to a scheduled method (`refreshAllChildren`) to prevent `initialize()` from blocking the UI for 140 seconds.

***

## ✅ Hot Fix 4 – Refresh Guard (Parent Hub)

**Status:** COMPLETE — Hub v3.3.26 (2026-11-04)\
**Scope:** Fix the remaining blocking issue in `refreshAllChildren`.

### What was actually done

In v3.3.25, `refreshAllChildren` was moved to a scheduled method to prevent `initialize()` from blocking. However, the method itself would still hang for 160 seconds if the Telnet connection was dead. A connection state guard was added: if the state is not `connected` or `idle`, the method exits immediately.

***

## ✅ Phase 3B – Actionable Event Tracking (Child Shade)

**Status:** COMPLETE — Shade v2.5.9 (2026-11-04)\
**Scope:** Add per-shade `lastEvent` and `lastUpdate` attributes for advanced automation triggers.

### What was done

Added two new attributes: `lastEvent` (enum: `idle`, `command_sent`, `position_confirmed`, `retry`, `jitter`, `failed`, `stopped`, `battery_report`) and `lastUpdate` (timestamp). This allows users to write precise automations (e.g., "If XDG's lastEvent becomes 'failed', send me a push notification").

***

## ⏸️ Phase 3A – Health Check & Auto-Recovery (Parent Hub) – DEFERRED

**Status:** DEFERRED (Only if connection becomes unstable)\
**Scope:** Implement proactive connection monitoring and auto-recovery.

**Why deferred:** The driver's current connection handling is sufficient for stable networks. The Health Check adds a new scheduling loop and recovery logic that is not currently needed.

***

## ⏳ Phase 4 – Documentation & Release (Continuous)

**Status:** CONTINUOUS — updated as each phase lands\
**Scope:** Keep all documentation in sync with the driver across every version.

***

## 📊 Summary Table

| Phase                                      | Status           | Version      | Key Changes                                                             |
| ------------------------------------------ | ---------------- | ------------ | ----------------------------------------------------------------------- |
| **Phase 1 – Critical Bug Fixes**           | ✅ **COMPLETE**   | Hub v3.3.24  | 3 fixes: ShadeAdd init, parse validation, ShadeRemove safety            |
| **Hot Fix 1 – Battery Voltage Formula**    | ✅ **COMPLETE**   | Shade v2.5.4 | 1 fix: 9.5V–12.6V range brings battery within ±5% of app                |
| **Phase 2A – Retry Logic + Logging UX**    | ✅ **COMPLETE**   | Shade v2.5.5 | 7 changes: tolerance, intermediate reset, unschedule fix, Log Verbosity |
| **Hot Fix 2 – Repeater Protection**        | ✅ **COMPLETE**   | Shade v2.5.7 | 5 changes: new defaults, dropdown retry count, exponential backoff      |
| **Hot Fix 3 – Smart Stateful Retry**       | ✅ **COMPLETE**   | Shade v2.5.8 | 5 changes: 8-second Heartbeat Check, Deaf/Moving/Stuck logic            |
| **Phase 2B – Logging UX + Initialize Fix** | ✅ **COMPLETE**   | Hub v3.3.25  | 6 changes: Log Verbosity dropdown, remove Refresh capability            |
| **Hot Fix 4 – Refresh Guard**              | ✅ **COMPLETE**   | Hub v3.3.26  | 1 fix: connection guard in `refreshAllChildren`                         |
| **Phase 3B – Event Tracking**              | ✅ **COMPLETE**   | Shade v2.5.9 | Added `lastEvent` and `lastUpdate` attributes                           |
| **Phase 3A – Health Check**                | ⏸️ **DEFERRED**  | TBD          | Proactive connection monitoring                                         |
| **Phase 4 – Documentation & Release**      | ⏳ **CONTINUOUS** | N/A          | Ongoing doc sync across all versions                                    |

***

## 🧠 Design Deep-Dive Reference

Decisions we analysed and **deliberately did not implement**. These are preserved so we don't re-litigate the same questions in future phases.

| Considered                                                     | Reason Not Implemented                                                                                                                                                        |
| -------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Full `state.phase` state machine (6 states)                    | Existing booleans plus the `commandId` guard track state well enough.                                                                                                         |
| Reordering retries vs jitter (retries first, jitter last)      | The current order proved correct in production. The chatty logs were caused by exact-match confirmation, not order.                                                           |
| New `RF Jitter Wait Time` preference                           | Hardcoded 3s is adequate. Adding a preference increases UI clutter.                                                                                                           |
| Changing default `Command Retry Count` from 2 to 3             | User can adjust manually. Current default works.                                                                                                                              |
| Hard cap on retry count (5)                                    | Preference range (0–3) already enforces this.                                                                                                                                 |
| Try/catch wrapper on every callback                            | Existing `parse()` already handles the risky parts.                                                                                                                           |
| Unique `commandId` as a full transaction ID                    | A simple incrementing counter is sufficient as a stale-callback guard.                                                                                                        |
| Reset `lastReportedPosition` on every new command              | Handled naturally by the intermediate-position logic.                                                                                                                         |
| Diagnostic logging for XDG failures                            | Not needed after Phase 2A – XDG is responding cleanly. If it regresses, revisit.                                                                                              |
| Position tolerance as a **hardcoded** value                    | Real-world motor and shade-type variance requires user adjustability.                                                                                                         |
| Non-linear battery voltage curve                               | The app likely uses proprietary algorithms. A linear formula with per-shade offset is simpler and gets within ±5%.                                                            |
| `lastCommand` attribute on child devices                       | Raw ARC strings have no user value. The Parent already logs the last global command.                                                                                          |
| State-machine refactor of the retry workflow                   | The observed problems were solved by tolerance and intermediate-reset. A state machine would add complexity without addressing a real issue.                                  |
| **Command Lock (ignoring/queueing new commands while moving)** | If a user or automation sends a command, the driver should execute it. A lock would block security automations ("Open the blinds NOW") and cause a nightmare to troubleshoot. |

**These can be revisited in a future phase if user feedback or new hardware demonstrates a real need.**

***

## 🚦 Current Status

**As of 2026-11-04:**

* **Phase 1 (Hub v3.3.24) is complete and production-validated.** ShadeAdd, parse, and ShadeRemove safety checks confirmed working.

* **Battery hotfix (Shade v2.5.4) is complete.** All eight shades now read within ±5% of the Rollease app.

* **Phase 2A (Shade v2.5.5) is complete and validated.**

* **Hot Fix 2 (Shade v2.5.7) is complete.** The repeater flooding issue is resolved.

* **Hot Fix 3 (Shade v2.5.8) is complete.** The Smart Stateful Retry logic is live.

* **Phase 2B (Hub v3.3.25) is complete.** The Initialize blocking issue is fixed, and the logging UI is unified.

* **Hot Fix 4 (Hub v3.3.26) is complete.** The `refreshAllChildren` guard prevents blocking during Hubitat reboots.

* **Phase 3B (Shade v2.5.9) is complete.** Actionable event tracking (`lastEvent` and `lastUpdate`) is live.

* **Phase 3A is deferred indefinitely** – no current need.

* **All project documents are current for Hub v3.3.26 / Shade v2.5.9.**

* **The project is officially in Maintenance Mode.**

**Next action:** Monitor the system. Enjoy the shades!

***

## 📝 Change Log (this document)

| Version   | Date       | Changes                                                                                                                                                                                                                                           |
| --------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1.3.0** | 2026-11-04 | **Added Hot Fix 4 (Refresh Guard) and Phase 3B (Event Tracking) to the roadmap.** Updated versions to Hub v3.3.26 / Shade v2.5.9. Moved Phase 3B from Deferred to Complete. Added the "Command Lock" rejection to the Design Deep-Dive Reference. |
| 1.2.0     | 2026-10-28 | Added Hot Fix 3 (Smart Stateful Retry). Moved Phase 2B to COMPLETE. Updated Current Status to Maintenance Mode.                                                                                                                                   |
| 1.1.0     | 2026-10-23 | Added Hot Fix 2 (Exponential Backoff). Moved Phase 3A and 3B to DEFERRED. Expanded Phase 2B to include Initialize optimization.                                                                                                                   |
| 1.0.0     | 2026-09-16 | Initial creation.                                                                                                                                                                                                                                 |

***

**Insuo Periculo** – Enter at your own risk. 🚀

***

## 📝 Summary of Updates Applied to RoadmapPhasedPlan.md (v1.2.0 → v1.3.0)

**Version Control:**

* Updated Current Driver Version to **`Hub v3.3.26 / Shade v2.5.9`**.

* Updated Document Version to **`1.3.0`**.

**Roadmap Overview:**

* Added **Hot Fix 4** (Refresh Guard) as ✅ COMPLETE.

* Moved **Phase 3B** (Event Tracking) from DEFERRED to ✅ COMPLETE.

**New Sections:**

* **Hot Fix 4:** Documented the `refreshAllChildren` connection guard fix.

* **Phase 3B:** Documented the addition of `lastEvent` and `lastUpdate`.

**Design Deep-Dive Reference:**

* Added the **Command Lock** rejection: explained why we do not block commands based on movement state.

**Summary Table:**

* Added Hot Fix 4 and Phase 3B rows. Updated Phase 3A to reflect it is the only deferred item.

**Current Status:**

* Updated to declare the project officially in Maintenance Mode, with all requested features complete.

***

