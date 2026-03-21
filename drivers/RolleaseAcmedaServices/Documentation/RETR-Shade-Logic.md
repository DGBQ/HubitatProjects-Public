# 📄 Master Functional Specifications: Rollease Acmeda Shade (Child Driver)  
**Driver Branch:** The Surgical Observer  
**Integration Logic:** Proactive State Updates with Alexa‑Optimised Command Handling

---

## 1. Project Context & Evolution

> ### **The “Rollease Shade” Project Branch**
> This child driver works in conjunction with the **Rollease Acmeda Pulse 2 Hub** parent driver. It represents a single shade motor, handling commands from the parent, parsing hub responses, and maintaining the shade’s state. Originally developed by **Younes Oughla**, it has been refined to work reliably with the hub’s burst‑and‑close behaviour.

> ### **The “Alexa Timeout” Challenge**
> Amazon Alexa requires a state change acknowledgement within a few seconds of a command. The original driver waited for the hub’s confirmation, which sometimes exceeded Alexa’s timeout. This led to “not responding” errors even though the shade moved.

> ### **The Proactive State‑Update Solution**
> The driver now immediately updates its state when a command is issued (`open`, `close`, `setPosition`). This gives Alexa an instant acknowledgement while the hub command is still in flight. The hub’s eventual confirmation later corrects any discrepancy, but in practice the proactive state matches the final position.

> ### **Logging & Preference Alignment**
> To maintain consistency with the parent driver, the child now uses `logEnable` and `txtEnable` preferences, and debug logging auto‑disables after 30 minutes. The subscription to the parent’s `status` attribute was removed because drivers cannot subscribe to other drivers’ attributes in Hubitat.

---

## 2. Commands (Functional Methods)

These methods are available to the system and are typically called by automations or Alexa.

| Method | Description |
|--------|-------------|
| `open()` | Sends command to fully open the shade. Proactively sets `switch` to `on`, `level` to 100, `position` to 100, `windowShade` to `opening`, and `moving` to `true`. |
| `close()` | Sends command to fully close the shade. Sets `switch` to `off`, `level` to 0, `position` to 0, `windowShade` to `closing`, and `moving` to `true`. |
| `setPosition(level)` | Moves shade to a specific percentage (0–100). Accepts integer or string input. Proactively sets `switch`, `level`, `position` to the target value, and updates `windowShade` and `moving` accordingly. |
| `stop()` | Stops the shade. Sets `windowShade` to `partially open` and `moving` to `false`. |
| `toggle()` | Toggles the shade between open, closed, or stop based on current state and last direction. |
| `refresh()` | Requests current status from the hub by sending a `r?` command. |

> **Note on Alexa Integration:** The `on` and `off` commands are aliases for `open()` and `close()` respectively. They are required by the `Switch` capability, which Alexa uses for voice commands like “turn on the shade”. Similarly, `setLevel` handles percentage commands (“set shade to 50%”). The `toggle` command is a custom convenience that stops, opens, or closes the shade based on its current state and last direction.

All commands funnel through `parent.sendTelnetCommand()` to ensure single‑path communication.

---

## 3. Preferences (User‑Configurable)

| Preference | Type | Description |
|------------|------|-------------|
| `motorAddress` | string | 3‑character motor ID assigned by the hub (e.g., `I39`). Must match the hub’s ID. |
| `logEnable` | bool | Enables detailed debug logs; automatically turns off after 30 minutes. |
| `txtEnable` | bool | Enables human‑readable activity logs (INFO level). |

---

## 4. Current States (Dashboard Attributes)

These attributes are displayed on the device page and are used by Alexa and automations.

| Attribute | Type | Description |
|-----------|------|-------------|
| `open` | bool | True when shade is fully open (position = 100). |
| `closed` | bool | True when shade is fully closed (position = 0). |
| `position` | int | Shade position as percentage (0–100). |
| `moving` | bool | True when the shade is in motion (proactively set or confirmed by hub). |
| `voltage` | int | Raw voltage reported by the hub (e.g., `1204` = 12.04 V). |
| `batteryVoltage` | decimal | Voltage in volts, parsed from `voltage`. |
| `battery` | int | Battery percentage calculated from voltage (10.8 V = 0%, 12.6 V = 100%). |
| `rssi` | string | Signal strength in dBm (e.g., `R75`). |
| `switch` | string | `on` when position > 0, `off` when position = 0. Used by Alexa for on/off commands. |
| `level` | int | Alias for `position` for the `Switch Level` capability. Used by Alexa for percentage commands. |
| `windowShade` | string | Standard Hubitat attribute with values `open`, `closed`, `partially open`, `opening`, `closing`. |

---

## 5. State Variables (Internal Logic Tracking)

| Variable | Type | Purpose |
|----------|------|---------|
| `state.lastDirection` | string | Remembers the last movement direction (`opening` or `closing`) for the `toggle` command. |

No other state variables are used; the driver maintains no connection state (handled by parent).

---

## 6. Operational Logic (The Proactive Engine)

> ### **Immediate State Acknowledgement**
> When a command is received, the driver immediately sends attribute events for `switch`, `level`, `position`, `windowShade`, and `moving`. This gives Alexa an instant response, preventing timeouts.

> ### **Already‑at‑Target Optimisation**
> If a `setPosition` command is received for the current position, the driver sends the state events but does **not** send a command to the hub. This avoids unnecessary RF traffic while still satisfying Alexa’s expectation.

> ### **Hub Response Parsing**
> The `parse` method processes incoming hub messages:
> - `!<ID>r` messages update the final position.
> - `!<ID>m` messages indicate movement initiation (used for direction tracking).
> - `!<ID>pVc` messages provide voltage and RSSI data for battery reporting.

> ### **Battery & RSSI Parsing**
> The voltage string (e.g., `1204`) is converted to volts, then mapped to a percentage based on the linear range 10.8 V (0%) to 12.6 V (100%). RSSI is stored as a string without further conversion.

---

## 7. Parent/Child Interaction

- The child **never** opens its own telnet socket.
- All commands are sent via `parent.sendTelnetCommand()`.
- The child does **not** subscribe to parent attributes (drivers cannot subscribe in Hubitat).
- The child’s `parse` method receives messages that match its motor address.

---

## 8. Logging & Diagnostics

Logging follows the same four‑tier pattern as the parent:

| Level | Condition | Example |
|-------|-----------|---------|
| **DEBUG** | Enabled by `logEnable`; auto‑off after 30 minutes | `Office Blind [DEBUG]: Set Position: 75` |
| **INFO** | Enabled by `txtEnable`; human‑readable activity | `Kitchen Blind [INFO]: Position Updated: 50` |
| **WARN** | Always shown for non‑critical issues | `Bedroom Blind [WARN]: Failed to parse voltage string 'XYZ'` |
| **ERROR** | Always shown for critical failures | `Living Room Blind [ERROR]: Invalid motor ID` |

Log messages include the shade name (`device.name`) to easily identify which blind is reporting.

---

## 9. Developer Summary Table

| Component | Value / Constraint | Notes |
|-----------|-------------------|-------|
| **Core Protocol** | Telnet (ARC) via parent | Uses parent’s connection. |
| **Command Format** | `!<motorAddress>m<pos>` | Position inverted (100 – target), zero‑padded to three digits. |
| **Proactive Events** | `switch`, `level`, `position`, `windowShade`, `moving` | Sent immediately on command. |
| **Battery Calculation** | 10.8 V = 0%, 12.6 V = 100% | Linear interpolation, clamped 0–100. |
| **Logging** | `logEnable` (debug), `txtEnable` (info) | Auto‑disable debug after 30 minutes. |
| **State Variables** | Only `lastDirection` | Used by `toggle` command. |
| **Attributes** | `open`, `closed`, `position`, `moving`, `voltage`, `batteryVoltage`, `battery`, `rssi`, `switch`, `level`, `windowShade` | All maintained in real time. |

---

## 10. Evolution from Original to Final (v2.3.1)

| Original (2020.06.08.01) | Final (v2.3.1) |
|--------------------------|----------------|
| Waited for hub confirmation before updating state | Proactive state updates on command. |
| No battery/RSSI reporting | Full battery percentage and RSSI parsing. |
| No `switch` or `level` attributes | Added for Alexa compatibility. |
| Single `debug` preference | Split into `logEnable` and `txtEnable` with auto‑disable. |
| `status` attribute and subscription to parent | Removed (drivers cannot subscribe). |
| Basic position parsing | Robust handling of string‑to‑int conversions, fallbacks. |
| No initialisation of default values | Seeded default attributes on first start to give Alexa a baseline. |

This child driver is now fully aligned with the parent’s logging philosophy and provides the proactive feedback needed for reliable voice control, while retaining the core reliability of the original.