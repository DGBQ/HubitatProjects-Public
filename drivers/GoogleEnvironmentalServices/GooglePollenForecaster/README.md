# 🌿 Google Pollen Forecaster
### _driver for Hubitat Elevation_  

## 🚧 **COMING SOON – In Pre-Beta Testing** 🚧

## 🔢 Version
**Documentation Version:** 1.0.3  
**Date:** 2026-03-25  
**Author:** David Ball-Quenneville  


---

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>
    
- 🧠 [Overview](#-overview)  
- 📜 [Background & Motivation](#-background--motivation)  
- 🚀 [Quick Start](#-quick-start)  
  - 🧰 [Hubitat Package Manager (HPM) Install](#-hubitat-package-manager-hpm-install)  
  - 🛠️ [Manual Install](#-manual-install)  
  - ⚙️ [Driver Configuration](#-driver-configuration)  
- 🔑 [Getting Your Google API Key](#-getting-your-google-api-key)  
- 💡 [Example Use Cases](#-example-use-cases)  
- 🌿 [Supported Pollen Types](#-supported-pollen-types)  
- 🔍 [Troubleshooting](#-troubleshooting)  
- 📚 [Documentation Library](#-documentation-library)  
- 🗂️ [Repository Structure](#-repository-structure)  
- 🐞 [Reporting Bugs](#-reporting-bugs)  
- 🤝 [Contributing](#-contributing)  
- [⚖️ License & Versioning](#-license--versioning)
- 🛡️ [Disclaimers](#-disclaimers)  
- 📝 [Revision History](#-revision-history)  

</details>

---

<h2 id="overview">🧭 Overview</h2>

The Google Pollen Forecaster Driver is a custom **Hubitat Elevation** driver that fetches hyper-local pollen forecasts using the Google Pollen API, delivering daily and multi-day forecast data for trees, grasses, weeds, and specific allergens like ragweed and birch. Designed for **Hubitat Elevation** smart-home enthusiasts who may be allergy sufferers, it provides actionable health recommendations and seamless integration with **Hubitat Elevation** automations and **Dashboards**.

### Key Features & Capabilities

*   **Hyper-Local Accuracy:** Utilizes a 1 km² resolution for precise, location-specific forecasts (supporting up to 5 days of data).
*   **Location Flexibility:** Supports the native Hubitat Elevation hub location or custom coordinates for secondary monitoring (e.g., “Home” and “Cottage”).
*   **Smart Automations:** Seamlessly triggers Devices, Dashboards, or automation logic when air quality or pollen levels change.
*   **Quota & Cost Management:** Includes adjustable Google API call limits and integrated retry logic to help manage billing quotas and minimize unnecessary API expenses.
*   **Global Reliability & Scale:** Leverages the robust Google Cloud environmental infrastructure, ensuring high availability and a diverse range of data points across global regions.
*   **Personalized Tracking:** Allows users to focus on specific pollutants or allergens most relevant to their health requirements and geographic region.
  *   **Community-Driven Integration**: An open-source driver architecture that is free to customize, extend, or integrate into larger Hubitat Elevation projects.
*   **Robust Diagnostics:** Features detailed error handling and structured logging visible within the Hubitat Elevation logs for rapid troubleshooting.
*   ** Local Execution:** Fully local driver logic—all processing and API parsing occurs directly on the Hubitat Elevation hub.

---

<h2 id="background-motivation">📜 Background & Motivation</h2>

This driver initiated as an AI-assisted learning experiment by a Software Implementation Project Manager exploring prompt engineering and **Hubitat Elevation** development. While a professional developer might have constructed the logic more efficiently, this project served as a hands-on method to identify platform best practices and navigate the nuances of AI-driven development.

The primary objective was practical: tracking pollen data to assist in daily decision-making and displaying forecasts on an **HD+ Android Dashboard** utilizing **Tile Builder** features. While this may not be a production-grade driver, the project involved systematic research, testing, and iterative refinement. The process delivered a functional pollen monitoring solution and significant educational insight into the **Hubitat Elevation** environment.

---

<h2 id="quick-start">🚀 Quick Start</h2>

The Quick Start section covers the essentials for installing, configuring, and using the Google Pollen Forecaster Driver. Installation is available via the **Hubitat Package Manager** (HPM) or manually by adding the driver code and creating a **Virtual Device**. Following installation, users must provide a Google API key, adjust **Preferences**, and execute an initial **Poll** command.

For complete instructions on installation, setup, and configuration, refer to the full driver documentation in the **Documentation Library**.

<h3 id="hubitat-package-manager-hpm-install">🧰 Hubitat Package Manager (HPM) Install</h3>

> **Note:** The driver is not yet available in the **Hubitat Package Manager**. Check back soon for updates or use the Manual Install method below.

1. Navigate to **Hubitat Menu > Apps > Hubitat Package Manager**.  
2. Select **Search by Keyword** and enter: `Google Pollen Forecaster`.  
3. Select the driver and follow the installation prompts.  
4. Once installed, proceed to **Driver Configuration** to complete setup.

<h3 id="manual-install">🛠️ Manual Install</h3>

<h4 id="driver-code-install">💾 Driver Code Install</h4>

1. In **Hubitat Elevation**, navigate to **Hubitat Menu > Drivers Code > + New Driver**.  
2. Copy the full code from `googlePollenForecaster.groovy` or import the raw code from this link:
```
https://github.com/DGBQ/HubitatProjects-Public/tree/main/drivers/GoogleEnvironmentalServices/GooglePollenForecaster
```
3. Click **Save** to install the driver.

<h4 id="virtual-device-install">🪄 Virtual Device Install</h4>

1. Navigate to **Hubitat Menu > Devices > + Add Device > Virtual**.  
2. In the **Type** dropdown, select `Google Pollen Forecaster Driver`.  
3. Name the new device (e.g., `Pollen Forecast – Home`).  
4. Click **Save Device**, then proceed to **Driver Configuration** to complete setup.

<h3 id="driver-configuration">⚙️ Driver Configuration</h3>

1. Open the **Device Detail Page** for the created device (e.g., `Pollen Forecast – Home`).  
2. Select the **Preferences** section.  
3. Add the **Google Pollen API Key** — refer to [Getting Your Google API Key](#-getting-your-google-api-key) for details.  
4. Adjust optional **Preferences** such as:  
   - Primary / Secondary Update Times  
   - Custom Coordinates  
   - Displayed Pollen Types  
5. Click **Save Preferences**.  
6. Navigate to the **Commands** section and select **Poll** to fetch the initial forecast.  

> **Note:** If **Attributes** do not refresh immediately, refresh the browser page for the **Device Detail Page**—this is a known **Hubitat Elevation** UI behavior.

---

<h2 id="getting-your-google-api-key">🔑 Getting Your Google API Key</h2>

To use the Google Pollen Forecaster Driver, an active Google API Key is required. Follow these steps to create and secure a key in the Google Cloud Console:

1. Navigate to the **Google Cloud Console**.  
2. Create a new project (or select an existing project).  
3. Enable the **Pollen API** under **APIs & Services > Library**.  
4. Configure a billing account if prompted (required for API access).  
5. Generate a new API key under **APIs & Services > Credentials > + Create Credentials > API Key**.  
6. Apply key restrictions for security, such as HTTP referrers or IP addresses.  
7. Set usage limits to control daily API calls.  
8. Copy the API key and paste it into the **Google API Key** field under the driver **Preferences** in **Hubitat Elevation**.

📄 Detailed instructions: [Get Google API Key](documents/GetGoogleAPIKey_GooglePollenForecaster.md)

---

<h2 id="example-use-cases">🧠 Example Use Cases</h2>

Practical methods to automate the environment with pollen data:

- **Allergy Alerts:** Trigger a notification when `ragweedPollenLevel` reaches elevated or extreme levels.  
- **Smart Home Control:** Automate HVAC or air purifiers based on changes in pollen levels.  
- **Health Dashboards:** Display `todayPollenLevel` and `healthRecommendation` on a **Dashboard**.  
- **Seasonal Automation:** Suppress API calls during winter using the **Seasonal Blockout** feature.

---

<h2 id="supported-pollen-types">🌿 Supported Pollen Types</h2>

The driver supports 20 pollen types, including:

- **Grasses:** Grass, Graminales  
- **Trees:** Alder, Ash, Birch, Cottonwood, Cypress Pine, Elm, Hazel, Japanese Cedar, Japanese Cypress, Juniper, Maple, Oak, Olive, Pine, Tree  
- **Weeds:** Mugwort, Ragweed, Weed

Quick summary: Lists all pollen types tracked by the Google API, including tree, grass, and weed categories used in driver **Attributes**.  

Full list: 📄 [Pollen Types Supported](documents/PollenTypesSupported_GooglePollenForecaster.md).

---

<h2 id="troubleshooting">🔍 Troubleshooting</h2>

Common issues and resolutions for the driver.

| Issue | Possible Cause | Solution |
|-------|----------------|---------|
| No Data Displayed | API key missing or invalid | Verify API key in **Preferences** and re-save. |
| Error 403 / Access Denied | Missing billing or strict key restrictions | Enable billing or adjust key restrictions in the Google Cloud Console. |
| Quota Exceeded | Excessive API calls | Review usage and set limits; reduce polling frequency in **Preferences**. |
| Outdated Data | Driver not polling | Execute the **Poll** command or verify scheduled update **Preferences**. |
| Persistent Attributes | Attribute mapping unchanged | Reinstall driver to reset **Attributes** (use with caution). |

---

<h2 id="documentation-library">📚 Documentation Library</h2>

Browse related guides, reference materials, and driver updates.

- 📄 No Documentation has been added yet. This section will be updated in a future release.

---

<h2 id="repository-structure">📦 Repository Structure</h2>

```
HubitatProjects-Public
├─ drivers
│  └─ GoogleEnvironmentalServices
│     ├─ GettingYourGoogleAPIKey_GoogleEnvironmentalSuite.md
│     └─ GooglePollenForecaster
│        ├─ documentation
│        ├─ CHANGELOG.md
│        ├─ README.md
│        └─ googlePollenForecaster.groovy
```

---

<h2 id="reporting-bugs">🐞 Reporting Bugs</h2>

For issues and bugs, utilize the [GitHub Issues](https://github.com/your-repo/google-pollen-forecaster/issues) page rather than the **Hubitat Elevation** Community forums. This ensures issues are tracked and resolved systematically.

- Include **Hubitat Elevation** firmware version, driver version, and exact steps to reproduce.  
- Attach relevant logs and screenshots. Mask any sensitive data.  
- Search existing issues first to avoid duplicates.

> **Warning:** Avoid posting bug reports directly in the **Hubitat Elevation** Community thread to ensure your report is properly tracked and resolved.  

---

<h2 id="contributing">🤝 Contributing</h2>

To extend or enhance the driver, fork the repository and create a unique version. Pull requests with improvements are welcome.

<h3 id="how-to-contribute">How to Contribute</h3>

- Fork the repository and create a feature branch.  
- Submit pull requests with clear descriptions and testing notes.  
- Follow **Hubitat Elevation** conventions for drivers and include well-commented code.  
- See 📄 Contributing Guide for process and style rules.

> **Note:** Response times for reviews or merges may vary due to personal and professional commitments.

---

<h2 id="license-versioning">⚖️ License & Versioning</h2>

- **License:** All projects in this repository are shared under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).  
- **Versioning:** Each driver maintains its own `CHANGELOG.md`. Refer to the project-specific changelog for release notes.

---
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

---

<h2 id="revision-history">📝 Revision History</h2>

| Version | Date        | Author | Description                                                                                                                   |
|---------|------------|--------|-------------------------------------------------------------------------------------------------------------------------------|
| 1.0.3   | 2026-03-25 | DBQ    | Information updates, formatting.                                                                                              |
| 1.0.2   | 2026-02-08 | DBQ    | Formatting.                                                                                                                   |
| 1.0.1   | 2025-10-30 | DBQ    | Added Background & Motivation, Reporting Bugs, Quick Start, and API Key instructions; updated ToC and doc metadata.           |
| 1.0     | 2025-10-15 | DBQ    | Initial document creation.                                                                                                    |