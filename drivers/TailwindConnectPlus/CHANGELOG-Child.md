# Tailwind Connect Plus (Child) – CHANGELOG

#### v1.1.3 - 2026-08-31 - David Ball-Quenneville

* **Changes:**

  * **Version synchronized** with parent driver (v1.2.4).

  * **No functional changes** – Child driver is a lightweight proxy.

#### v1.1.2 - 2026-08-31 - David Ball-Quenneville

* **New Features:**

  * **lastOpen / lastClosed Attributes** – Added to child devices to track when doors last opened or closed. These attributes are updated by the parent driver whenever a status change is detected.

#### v1.1.1 - 2026-08-18 - David Ball-Quenneville

* **Changes:**

  * **Version synchronized** with parent driver (v1.1.1).

#### v1.1.0 - 2026-08-18 - David Ball-Quenneville

* **New Features:**

  * **Version synchronized** with parent driver (Phase 2).

  * **No functional changes** – Child driver is a lightweight proxy.

#### v1.0.0 - 2026-08-12 - David Ball-Quenneville

* **New Features:**

  * **Initial Fork** – Community fork of the `dbadge` / `Gelix` Tailwind Garage Door Child driver.

  * **Driver Header** – Added full copyright, Apache 2.0 license, and revision history header.

  * **Version Constant** – Added `@Field static final String DRIVER_VERSION = "v1.0.0"`.

* **Changes:**

  * **Driver Renamed** – Now called **Tailwind Connect Plus - Child**.

  * **Namespace Changed** – Updated to `DGBQ`.

  * **Author Updated** – Now maintained by David Ball-Quenneville.

  * **Import URLs Updated** – Points to the new DGBQ repository.

#### v0.0.0 - Pre-Fork - dbadge / Gelix

* **Original Driver Details:**

  * Represents an individual garage door.

  * Lightweight proxy that forwards commands to the parent driver.

  * Capabilities: `GarageDoorControl`, `Actuator`, `ContactSensor`, `Sensor`.
