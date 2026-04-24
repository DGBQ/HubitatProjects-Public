# Master Functional Specifications: RETR-Shade-Logic

**File Name:** RETR-Shade-Logic.md (Rollease Engineering Technical Reference Shade Logic)
**Project Branch:** The Surgical Observer  
**Integration Logic:** Child Device Architecture with Proactive State-Engine  
**Document Status:** Final Production Specification

---

## 1. Project Context & Technical Philosophy

### 1.1 The Role of the Surgical Observer (Child)
The **RETR-Shade-Logic** driver serves as the terminal interface for individual Rollease Acmeda motors within the Hubitat ecosystem. While the Parent Hub (RETR-Hub-Logic) manages the "Burst-and-Close" socket lifecycle, the Child Driver is tasked with the high-fidelity translation of that raw data into actionable telemetry and real-time state management.

### 1.2 The "Alexa Timeout" & Proactive Response Engine
A critical limitation identified during the "Surgical Observer" audit was the latency inherent in RF mesh networks. Cloud-to-cloud integrations like **Amazon Alexa** require a state-change acknowledgement within a narrow 1–3 second window.
* **The Original Limitation:** The legacy driver waited for the Hub to complete its RF transmission and send back a confirmation burst before updating the Hubitat UI. This often exceeded the timeout, resulting in "Device Not Responding" errors despite successful movement.
* **The Proactive Solution:** This branch implements a **Proactive State-Engine**. Upon receiving a command (`open`, `close`, `setPosition`), the child driver immediately broadcasts its target state to Hubitat. 
* **The Result:** Instant visual feedback for the user and immediate "Success" handshakes for Alexa/Google Home, with the Hub’s eventual status burst acting as a background "Surgical Correction" to ensure 100% accuracy.

---

## 2. Full Command API (Methods)

The following commands represent the "DGBQ" optimized interface, ensuring protocol-compliant communication with the Parent Hub.

### 2.1 Standard Movement Commands
* **`open()`**: Initiates a full ascent. **Proactive Action:** Immediately sets `windowShade` to `opening` and `position` to `100`.
* **`close()`**: Initiates a full descent. **Proactive Action:** Immediately sets `windowShade` to `closing` and `position` to `0`.
* **`setPosition(level)`**: Moves the shade to a specific percentage. 
    * **Optimization:** Includes an "Already-at-Target" check. If the requested position matches the current state, the driver suppresses the RF command to reduce Hub congestion while still refreshing the Hubitat state.
* **`stop()`**: Interrupts current movement. Resets the `moving` flag to `false` and sets the state to `partially open`.
* **`toggle()`**: A convenience method that cycles through `open` -> `stop` -> `close` logic based on the `lastDirection` variable.

### 2.2 Compatibility Aliases
* **`on()` / `off()`**: Required for `Switch` capability; maps directly to `open()` and `close()`.
* **`setLevel(level)`**: Required for `Switch Level` capability; maps to `setPosition()`.

---

## 3. User Preferences & Configuration

These inputs define the child device's unique identity on the ARC mesh and its logging behavior.

| Preference | Format | Default | Description |
| :--- | :--- | :--- | :--- |
| **Motor Address** | String | 000 | The unique 3-character alphanumeric ID (e.g., `A1C`) assigned by the Hub. |
| **Enable Debug Logging** | Boolean | False | High-verbosity traces; **Auto-disables after 30 minutes** to preserve Hubitat performance. |
| **Enable Description Logs**| Boolean | True | Human-readable activity logs (e.g., "Kitchen Blind is opening"). |

---

## 4. Telemetry & Current States (Attributes)

The "Surgical Observer" branch significantly expands the telemetry captured from the Hub's data bursts.

### 4.1 Primary Motion Attributes
* **`windowShade`**: The primary status (`open`, `closed`, `opening`, `closing`, `partially open`).
* **`position` / `level`**: Real-time integer value (0–100) of the shade's height.
* **`moving`**: A boolean flag used to trigger high-frequency dashboard updates.

### 4.2 Power & Signal Telemetry
* **`battery`**: Calculated percentage based on a linear mapping of the voltage curve.
* **`batteryVoltage`**: The decoded decimal voltage (e.g., `12.04V`).
* **`voltage`**: The raw integer value received from the Hub burst (e.g., `1204`).
* **`rssi`**: The signal strength indicator (e.g., `R75`), allowing for mesh health diagnostics.

---

## 5. Internal Logic & State Variables

* **`state.lastDirection`**: Tracks the most recent movement (`opening` or `closing`) to ensure the `toggle()` command operates intuitively.
* **Attribute Seeding:** Upon installation or initialization, the driver "seeds" default values (Position 0, Closed) to ensure Alexa has a baseline state before the first Hub report is received.

---

## 6. Logic Guardrails (The DNA of Reliability)

### 6.1 Data Validation & Sanitization
The driver employs a "Surgical" approach to incoming Hub data:
* **Zero-Padding:** All outgoing position commands are automatically padded to three digits (e.g., `50` becomes `050`) to prevent ARC protocol rejection.
* **Type Casting:** Advanced handling for `String` vs `Integer` inputs in `setPosition` to prevent "GroovyCast" exceptions.
* **Voltage Mapping:** Battery percentage is calculated using a calibrated 10.8V (0%) to 12.6V (100%) scale, ensuring the reported battery level reflects actual motor performance.

### 6.2 Intelligent Parsing
The `parse()` method is designed to handle multi-part data bursts. It splits the raw `pVc` string into discrete components, stripping leading zeros and applying "Try-Catch" blocks to prevent the driver from crashing on malformed Hub reports.

---

## 7. Versioning & Lineage
* **Maintainer:** David Ball-Quenneville (DGBQ)
* **Lineage:** Optimized fork of the Younes Oughla (arcautomate) original branch.
* **Version:** 2.3.1 (Aligned with Hub Driver v2.2.x)