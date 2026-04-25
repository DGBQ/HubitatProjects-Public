# Legacy Source Archive

This directory serves as a historical archive for the original **Xiaomi Aqara Mijia Sensors** universal driver and the **IKEA Parasoll** driver that inspired the structure. These files are preserved here to provide a baseline for the feature stripping, health check additions, and logic simplifications found in the current project root.

---

### Original Source Information
* **Universal Driver Author:** Jonathan Michaelsen (waytotheweb)
* **Last Known Update:** v0.18 (circa 2021–2022)
* **Original Repository:** No longer available (the author's GitHub presence has been removed)
* **Reason for Deprecation:** The original driver is no longer listed on GitHub, and the universal codebase has become outdated for modern Hubitat environments.

* **IKEA Driver Author:** Dan Danache (dandanache)
* **Last Known Update:** v5.8.0 (2024–2025)
* **Original Repository:** [codeberg.org/dan-danache/hubitat (IKEA Parasoll E2013)](https://codeberg.org/dan-danache/hubitat/src/branch/main/ikea-zigbee-drivers/Ikea_E2013.groovy)

### Why is this folder separate?
These drivers have not been adapted for a **single‑purpose contact sensor** and include many capabilities (motion, vibration, weather, buttons, water leak, IAS zones, device binding, firmware update) that are irrelevant for a simple door/window contact. While they provided the foundational structure for this integration, they are considered **deprecated for contact‑only use** for the following reasons:

* **Excessive Capabilities:** The universal driver supports over a dozen device types, adding complexity and potential parsing conflicts.
* **No Health Monitoring:** Neither driver includes a `healthStatus` or automated offline detection specifically tuned for battery‑powered sensors.
* **No Contact State Inversion:** The universal driver lacks a preference to swap open/closed when the sensor is installed backwards.
* **No Ping Command:** Neither driver exposes a manual reachability test command.
* **Verbose Logging Structure:** Both drivers use boolean flags (infoLogging/debugLogging) rather than a tiered log‑level preference with auto‑revert.
* **Original Unavailability:** The original Xiaomi universal driver is no longer listed on GitHub, making this maintained driver the only actively supported option for Xiaomi contact sensors on Hubitat.

### Usage
**These files are for reference only.** If you are looking for a lean, stable, health‑aware driver for your **Xiaomi Aqara Contact Sensor (MCCGQ11LM / AS006CNW01)** or **Mijia Door/Window Sensor (lumi.sensor_magnet)**, please use the driver located in the **root directory** of this repository.

The current driver (`XiaomiAqaraContactSensor-DGBQ.groovy`) retains only the necessary Zigbee parsing logic (standard + proprietary) and adds:
- Health check with `online`/`offline`/`unknown` status
- `ping` command for manual reachability tests
- Log verbosity preference (Debug, Info, Warning, Error) with auto‑revert from Debug to Info
- Contact state inversion preference
- Network rejoin counter for mesh diagnostics

All unrelated fingerprints, capabilities, and code paths have been removed.

---
*Maintained by DGBQ. Legacy Archive curated April 2026.*