# 🌤️ Google Air Quality Tracker Plus

### *for Hubitat Elevation*

## 🚧 **COMING SOON – In Pre-Beta Testing** 🚧

**Version:** 1.0  
**Date:** 2025-10-10  
**Author:** David Ball-Quenneville  
**License:** Apache 2.0

---

## 📑 Table of Contents

<details>
<summary>Click to expand Table of Contents</summary>

- 🧠 [Overview](#overview)
- 📜 [Background & Motivation](#background-motivation)
- 🚀 [Quick Start](#quick-start)
    - 🧰 [Hubitat Package Manager (HPM) Install](#hubitat-package-manager-hpm-install)
    - 🛠️ [Manual Install](#manual-install)
    - ⚙️ [Driver Configuration](#driver-configuration)
- 🔑 [Getting Your Google API Key](#getting-your-google-api-key)
- 💡 [Example Use Cases](#example-use-cases)
- 🌿 [Supported Pollutants](#supported-pollutants)
- 🔍 [Troubleshooting](#troubleshooting)
- 📚 [Documentation Library](#documentation-library)
- 🗂️ [Repository Structure](#repository-structure)
- 🐞 [Reporting Bugs](#reporting-bugs)
- 🤝 [Contributing](#contributing)
- [⚖️ License & Versioning](#license-versioning)
- 🛡️ [Disclaimers](#disclaimers)
- 📝 [Revision History](#revision-history)

</details>

---

<h2 id="overview">🧠 Overview</h2>

The **Google Air Quality Tracker Plus** is a custom **Hubitat Elevation** driver that fetches hyper-local current Air Quality data and forecasts using the **Google Air Quality API**. It delivers daily and multi-day forecast data for the **Universal Air Quality Index (UAQI)**, specific pollutant concentrations, and health-based recommendations. Designed for **Hubitat Elevation** smart-home enthusiasts who are susceptible to poor air quality, it provides actionable health insights and seamless integration with **Hubitat Elevation** automations and **Dashboards**.

### Key Features & Capabilities

*   **Hyper-Local Accuracy:** Utilizes a 500 m² resolution for precise, location-specific forecasts (supporting up to 4 days of data).
*   **Live Environmental Data:** Retrieves live **UAQI** and pollutant readings directly from the **Google Cloud** environmental data feed.
*   **Location Flexibility:** Supports the native **Hubitat Elevation** hub location or **Custom Coordinates** for secondary monitoring (e.g., “Home” and “Cottage”).
*   **Smart Automations:** Seamlessly triggers **Devices**, **Dashboards**, or automation logic when air quality levels change.
*   **Quota & Cost Management:** Includes adjustable **Google API** call limits and integrated retry logic to help manage billing quotas and minimize unnecessary API expenses.
*   **Global Reliability & Scale:** Leverages the robust **Google Cloud** environmental infrastructure, ensuring high availability and a diverse range of data points across global regions.
*   **Community-Driven Integration:** An open-source driver architecture that is free to customize, extend, or integrate into larger **Hubitat Elevation** projects.
*   **Robust Diagnostics:** Features detailed error handling and structured logging visible within the **Hubitat Elevation** **Logs** for rapid troubleshooting.
*   **Local Execution:** Fully local driver logic—all processing and API parsing occurs directly on the **Hubitat Elevation** hub.

---

<h2 id="background-motivation">📜 Background & Motivation</h2>

This driver initiated as an AI-assisted learning experiment by a Software Implementation Project Manager exploring prompt engineering and **Hubitat Elevation** development. While a professional developer might have constructed the logic more efficiently, this project served as a hands-on method to identify platform best practices and navigate the nuances of AI-driven development.

The primary objective was practical: tracking Air Quality data to assist in daily decision-making and displaying forecasts on an **HD+ Android Dashboard** utilizing **Tile Builder** features. While this may not be a production-grade driver, the project involved systematic research, testing, and iterative refinement. The process delivered a functional Air Quality monitoring solution and significant educational insight into the **Hubitat Elevation** environment.

---

<h2 id="quick-start">🚀 Quick Start</h2>

The Quick Start section covers the essentials for installing, configuring, and using the **Google Air Quality Tracker Plus** driver. Installation is available via the **Hubitat Package Manager (HPM)** or manually by adding the driver code and creating a **Virtual Device**. Following installation, users must provide a **Google API Key**, adjust **Preferences**, and execute initial **Commands**.

For complete instructions on installation, setup, and configuration, refer to the full driver documentation in the **Documentation Library**.

<h3 id="hubitat-package-manager-hpm-install">🧰 Hubitat Package Manager (HPM) Install</h3>

> **Note:** The driver is not yet available in the **Hubitat Package Manager**. Check back soon for updates or use the **Manual Install** method below.

1.  Navigate to **Hubitat Menu > Apps > Hubitat Package Manager**.
2.  Select **Search by Keyword** and enter: `Google Air Quality Tracker Plus`.
3.  Select the driver and follow the installation prompts.
4.  Once installed, proceed to **Driver Configuration** to complete setup.

<h3 id="manual-install">🛠️ Manual Install</h3>

#### 💾 Driver Code Install

1.  In **Hubitat Elevation**, navigate to **Hubitat Menu > Drivers Code > + New Driver**.
2.  Copy the full code from `googleAirQualityTrackerPlus.groovy` or import the raw code from this link:
    `https://github.com/DGBQ/HubitatProjects-Public/tree/main/drivers/GoogleEnvironmentalServices/GoogleAirQualityTrackerPlus`
3.  Click **Save** to install the driver.

#### 🪄 Virtual Device Install

1.  Navigate to **Hubitat Menu > Devices > + Add Device > Virtual**.
2.  In the **Type** dropdown, select `Google Air Quality Tracker Plus`.
3.  Name the new device (e.g., `Google Air Quality Tracker Plus – Home`).
4.  Click **Save Device**, then proceed to **Driver Configuration** to complete setup.

---

<h3 id="driver-configuration">⚙️ Driver Configuration</h3>

1.  Open the **Device Detail Page** for the created device (e.g., `Google Air Quality Tracker Plus – Home`).
2.  Select the **Preferences** section.
3.  Add the **Google Air Quality API Key** — refer to [Getting Your Google API Key](#getting-your-google-api-key) for details.
4.  Adjust optional **Preferences** such as:
    *   **Day Poll Start (Current & Forecast)** / **Night Poll Start (Current & Forecast)**
    *   **Current Daytime Poll Interval** / **Current Nighttime Poll Interval**
    *   **Forecast Daytime Poll Interval** / **Forecast Nighttime Poll Interval**
    *   **Custom Coordinates**
5.  Click **Save Preferences**.
6.  Navigate to the **Commands** section and select `Force Current Poll` and `Force Forecast Poll` to fetch the initial data.

> **Note:** This driver uses two separate endpoints for Current and Forecast data to optimize API usage and ensure data accuracy for each specific request type.

> **Note:** If **Attributes** do not refresh immediately, refresh the browser page for the **Device Detail Page**—this is a known **Hubitat Elevation** UI behavior.

---

<h2 id="getting-your-google-api-key">🔑 Getting Your Google API Key</h2>

To use the **Google Air Quality Tracker Plus** driver, an active **Google API Key** is required. Follow these steps to create and secure a key in the **Google Cloud Console**:

1.  Navigate to the **Google Cloud Console**.
2.  Create a new project (or select an existing project).
3.  Enable the **Air Quality API** under **APIs & Services > Library**.
4.  Configure a billing account if prompted (required for API access).
5.  Generate a new API key under **APIs & Services > Credentials > + Create Credentials > API Key**.
6.  Apply key restrictions for security, such as HTTP referrers or IP addresses.
7.  Set usage limits to control daily API calls.
8.  Copy the API key and paste it into the **Google API Key** field under the driver **Preferences** in **Hubitat Elevation**.

📄 Detailed instructions: [Get Google API Key](documents/GetGoogleAPIKey_GoogleEnvironmentalSuite.md)

---

<h2 id="example-use-cases">🧠 Example Use Cases</h2>

Practical methods to automate the environment with air quality data:

*   **Automated Air Purification:** Trigger an air purifier or HVAC fan when the `uaqi` level moves into "Poor" or "Unhealthy" categories.
*   **Window Management:** Send a notification to close windows if the `dominantPollutant` indicates high levels of PM2.5 or O3.
*   **Health Alerts:** Use **Webcore** to announce health recommendations via smart speakers if the air quality index reaches a hazardous threshold.
*   **Visual Indicators:** Change the color of an RGB smart bulb (Green, Yellow, Red) based on the current `aqi` value on your **Dashboard**.

---

<h2 id="supported-pollutants">🌿 Supported Pollutants</h2>

The driver provides data for major pollutants tracked by the **Google Air Quality API**, including:

*   **Particulate Matter:** PM2.5 and PM10 (measured in µg/m³).
*   **Gaseous Pollutants:** Nitrogen Dioxide (NO2), Carbon Monoxide (CO), Sulfur Dioxide (SO2), and Ozone (O3).
*   **Indices:** The **Universal Air Quality Index (UAQI)** which standardizes air quality on a 1–100 scale across different regions.

Full list and explanation: 📄 [Air Quality Pollutants Supported](documents/PollutantsSupported_GoogleAirQuality.md)

---

<h2 id="troubleshooting">🔍 Troubleshooting</h2>

Common issues and resolutions for the driver.

| Issue | Possible Cause | Solution |
| :--- | :--- | :--- |
| **No Data Displayed** | API key missing or invalid | Verify **Google API Key** in **Preferences** and re-save. |
| **Error 403 / Access Denied** | Missing billing or strict key restrictions | Enable billing or adjust key restrictions in the **Google Cloud Console**. |
| **Quota Exceeded** | Excessive API calls | Review usage and set limits; reduce polling frequency in **Preferences**. |
| **Outdated Data** | Driver not polling | Execute the `Force Current Poll` command or verify scheduled update **Preferences**. |
| **Persistent Attributes** | Attribute mapping unchanged | Reinstall driver to reset **Attributes** (use with caution). |

---

<h2 id="documentation-library">📚 Documentation Library</h2>

Browse related guides, reference materials, and driver updates.

*   📄 No Documentation has been added yet. This section will be updated in a future release.

---

<h2 id="repository-structure">📦 Repository Structure</h2>

```
HubitatProjects-Public
├─ drivers
│  └─ GoogleEnvironmentalServices
│     ├─ GettingYourGoogleAPIKey_GoogleEnvironmentalSuite.md
│     └─ GoogleAirQualityTrackerPlus
│        ├─ documentation
│        ├─ CHANGELOG.md
│        ├─ README.md
│        └─ googleAirQualityTrackerPlus.groovy

```
[
<h2 id="reporting-bugs">🐞 Reporting Bugs</h2>

For issues and bugs, utilize the **GitHub Issues** page rather than the **Hubitat Elevation** Community forums. This ensures issues are tracked and resolved systematically.

*   Include **Hubitat Elevation** firmware version, driver version, and exact steps to reproduce.
*   Attach relevant **Logs** and screenshots. Mask any sensitive data.
*   Search existing issues first to avoid duplicates.

> **Warning:** Avoid posting bug reports directly in the **Hubitat Elevation** Community thread to ensure your report is properly tracked and resolved.

<h2 id="contributing">🤝 Contributing</h2>

To extend or enhance the driver, fork the repository and create a unique version. Pull requests with improvements are welcome.

<h3 id="how-to-contribute">How to Contribute</h3>

*   Fork the repository and create a feature branch.
*   Submit pull requests with clear descriptions and testing notes.
*   Follow **Hubitat Elevation** conventions for drivers and include well-commented code.
*   See 📄 **Contributing Guide** for process and style rules.

> **Note:** Response times for reviews or merges may vary due to personal and professional commitments.

<h2 id="license-versioning">⚖️ License & Versioning</h2>

*   **License:** All projects in this repository are shared under the **Apache License 2.0**.
*   **Versioning:** Each driver maintains its own `CHANGELOG.md`. Refer to the project-specific changelog for release notes.

<h2 id="disclaimers">🛡️ Disclaimers</h2>

> ⚠️ **Important:** Always perform a **Backup and Restore** of your **Hubitat Elevation** hub before making significant configuration changes. Test all automation logic incrementally. This repository is provided for educational and experimental purposes. Users must exercise professional judgment when deploying these tools in a production environment.

### Documentation and Development Standards

*   **Documentation Authorship:** This documentation was created by the project maintainer. AI tools were utilized to assist with editorial tasks, including grammar, technical prose clarity, and formatting consistency.
*   **AI-Assisted Development:** All drivers and **Apps** were developed with AI assistance. While the code has undergone functional testing, AI-generated outputs may contain non-standard patterns or unexpected behaviors. Users are responsible for testing, monitoring, and verifying performance within their specific **Hubitat Elevation** environment.

### Liability and Risk

*   **Provided As-Is:** All drivers and **Apps** in this repository are provided "as-is" without warranty or guarantee of suitability for any specific hardware configuration or use case.
*   **User Responsibility:** The user assumes full responsibility for all automation decisions, **Device** actions, or system outcomes resulting from the installation of these files. Always verify logic in a controlled environment before full deployment.
*   **External Dependencies:** Any third-party libraries, **Dashboards**, or external tools referenced are maintained by their respective developers. Users must verify compatibility and assume all risks associated with third-party integrations.

### Integration and API Usage

*   **Google API Usage:** Access to **Google APIs** (including Pollen, Air Quality, and Weather) is governed by Google’s terms of service, usage quotas, and billing requirements. Users are solely responsible for ensuring compliance and monitoring any charges or fees incurred from API calls.
*   **Information Accuracy:** Development was performed using the best available resources at the time of release. **Google APIs**, **Hubitat Elevation** firmware, or integration methods may change, which may impact the functionality of these drivers.
*   **Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with, endorsed by, or supported by **Google LLC**, **Hubitat Inc.**, or any other mentioned entities.

<h2 id="revision-history">📝 Revision History</h2>

| Version | Date | Changes |
| :--- | :--- | :--- |
| 1.0 | 2025-10-10 | Initial public release for beta testing |
| 0.9 | 2025-10-05 | Internal build for functional validation |
