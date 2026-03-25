# 💨 Google Air Quality Tracker Plus
### _driver for Hubitat_  

## 🚧 **_COMING SOON_ – in pre-beta testing** 🚧

## 🔢 Version
**Documentation Version:** 0.0.1
**Date:** 2026-03-24  
**Author:** David Ball-Quenneville  

---

## 📑Table of Contents
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
- [🔗 References / External Links](#-references--external-links)
- 🛡️ [Disclaimers](#-disclaimers)  
- 📝 [Revision History](#-revision-history)  

---

</details>

<h2 id="overview">🧭 Overview\</h2>

The Google Air Quality Tracker Plus Driver is a custom Hubitat Elevation driver that fetches hyper-local air quality data using the Google Air Quality API, delivering real-time and forecasted conditions including the **Air Quality Index (AQI)** and key pollutants such as **Fine Particulate Matter (PM2.5)**, **Coarse Particulate Matter (PM10)**, **Nitrogen Dioxide (NO₂)**, **Ozone (O₃)**, and **Carbon Monoxide (CO)**. Designed for Hubitat smart-home enthusiasts, especially those mindful of environmental conditions or respiratory health, it provides actionable insights and seamless integration with Hubitat automations and dashboards.

**Why Use This Driver?**

- **Hyper-Local Accuracy:** ~500 × 500 meter resolution for highly precise, location-based air quality monitoring, with hourly forecasts up to 96 hours (4 days).  
- **Smart Automations:** Trigger devices or automations based on AQI or pollutant thresholds.  
- **Health Awareness:** Monitor conditions that may impact breathing, comfort, and outdoor activity.  
- **Open Source:** Community-driven and fully customizable to fit your setup.  

---
<h2 id="background-motivation">📜 Background & Motivation</h2>

This driver started as an AI-assisted learning experiment by a Software Implementation Project Manager (with very limited coding experience) exploring prompt engineering and Hubitat development. I knew a professional developer could likely have built it more efficiently, but for me, this was a hands-on way to learn the dos and don’ts, and experience the occasional “why on earth did you do that?” moments in AI-driven development.

The main goal was practical: to track **air quality** for household members so they could make informed daily decisions, and to display forecasts on my [HD+ Android dashboard](https://community.hubitat.com/t/release-hd-android-dashboard/41674) using [Tile Builder](https://community.hubitat.com/t/release-tile-builder-build-beautiful-dashboards/118822) features. While it may not qualify as a production-grade Hubitat driver, this project involved systematic research, testing, and plenty of trial-and-error refinement. In the end, it delivered a surprisingly useful **air quality monitoring solution**—along with a few “why isn’t this updating?” moments that kept things interesting. The process was both personally fulfilling and educational.

---

<h2 id="quick-start">🚀 Quick Start</h2>

The Quick Start section covers the essentials for installing, configuring, and using the **Google Air Quality Tracker Plus Driver** in just a few minutes. You can install it using the Hubitat Package Manager (HPM) or manually by adding the driver code and creating a virtual device. Once installed, you’ll add your Google API key, adjust preferences, and perform your first test poll.

For complete instructions on installation, setup, and configuration, see the full driver documentation in the 📚 Documentation Library.

<h3 id="hubitat-package-manager-hpm-install">🧰 Hubitat Package Manager (HPM) Install</h3>

🟡 Note: The driver is not yet available in the Hubitat Package Manager. Check back soon for updates or use the Manual Install method below.

1. Go to Apps → open Hubitat Package Manager (HPM).  
2. Use Search by Keyword and enter: **Google Air Quality Tracker Plus**.  
3. Select the driver and follow the installation prompts.  
4. Once installed, proceed to Driver Configuration to complete setup.

<h3 id="manual-install">🛠️ Manual Install</h3>

<h4 id="driver-code-install">💾 Driver Code Install</h4>

1. In Hubitat, go to Drivers Code → click + New Driver.  
2. Copy the full code from `googleAirQualityTrackerPlus.groovy` or import the raw code from this link:  
```
https://github.com/DGBQ/HubitatProjects-Public/tree/main/drivers/GoogleEnvironmentalServices/GoogleAirQualityTrackerPlus
```
3. Click Save to install the driver.

<h4 id="virtual-device-install">🪄 Virtual Device Install</h4>

1. Go to Devices → + Add Device → select Virtual.  
2. In the Type dropdown, choose **Google Air Quality Tracker Plus Driver**.  
3. Name your new device (e.g., Air Quality – Home).  
4. Click Save Device, then proceed to Driver Configuration to complete setup.

<h3 id="driver-configuration">⚙️ Driver Configuration</h3>

1. Open the virtual device driver you created, for example `Air Quality – Home`.  
2. Select the Preferences tab.  
3. Add your Google Air Quality API Key — see [Getting Your Google API Key](#-getting-your-google-api-key) for details.  
4. Adjust optional settings such as:  
   - Primary / Secondary Update Times  
   - Custom Coordinates  
   - Displayed Pollutants  
5. Click Save Preferences.  
6. Go to the Commands tab and select **Poll** to fetch your first forecast.  

💡 Tip: If Current States don’t refresh immediately, close and reopen the device page — this is a known Hubitat UI behavior.

---
<h2 id="getting-your-google-api-key">🔑 Getting Your Google API Key</h2>

To use the **Google Air Quality Tracker Plus Driver**, you’ll need an active Google API Key. Follow these steps to quickly create and secure your key in the Google Cloud Console:

1. Go to the Google Cloud Console.  
2. Create a new project (or select an existing one).  
3. Enable the **Air Quality API** under APIs & Services → Library.  
4. Set up a billing account if prompted — required for API access.  
5. Generate a new API key under APIs & Services → Credentials → + Create Credentials → API Key.  
6. Add key restrictions for security, such as HTTP referrers (websites) or IP addresses.  
7. Set usage limits (optional but recommended) to control daily API calls.  
8. Copy your API key and paste it into the Google API Key field under the driver’s Preferences in Hubitat.

📄 Detailed instructions: [Get Google API Key](documents/GetGoogleAPIKey_GoogleAirQualityTrackerPlus.md)

---
<h2 id="example-use-cases">🧠 Example Use Cases</h2>

Explore practical ways to automate your environment with **air quality data**, from health notifications to daily routines.

- **Air Quality Alerts:** Trigger a notification when **PM2.5**, **PM10**, **NO₂**, **O₃**, or **CO** levels reach elevated or unhealthy thresholds.  
- **Smart Home Control:** Automate devices based on changes in AQI or individual pollutant levels.  
- **Health Dashboards:** Display current **AQI** and key pollutant levels on a Hubitat dashboard for quick reference.  
- **Seasonal Automation:** Suppress API calls during periods of low activity or known low pollution to conserve API usage.

---

<h2 id="supported-pollutants">💨 Supported Pollutants</h2>

The driver tracks key air quality metrics provided by the Google Air Quality API, including:

- **Particulate Matter:** Fine Particulate Matter (**PM2.5**), Coarse Particulate Matter (**PM10**)  
- **Gases:** Nitrogen Dioxide (**NO₂**), Ozone (**O₃**), Carbon Monoxide (**CO**)  
- **Air Quality Index:** Overall **AQI** for a quick reference to general air quality levels  

Quick summary: Lists all pollutants and metrics tracked by the Google Air Quality API, reflected in driver attributes for monitoring, dashboards, and automations.  

Full list:  
- 📄 [Supported Pollutants](https://developers.google.com/maps/documentation/air-quality/overview#pollutants)  
- 📄 [Supported Air Quality Indexes (AQIs)](https://developers.google.com/maps/documentation/air-quality/overview#air-quality-indexes)

---
<h2 id="troubleshooting">🔍 Troubleshooting</h2>

Common issues and quick fixes for the driver.

| Issue | Possible Cause | Solution |
|-------|----------------|---------|
| No Data Displayed | API key missing or invalid | Check your API key in Preferences and re-save. |
| Error 403 / Access Denied | Missing billing or key restrictions too strict | Enable billing, or adjust key restrictions in the Google Cloud Console. |
| Quota Exceeded | Too many API calls | Review usage and set usage limits; reduce polling frequency. |
| Old/Outdated Data | Driver not polling | Click Poll or verify scheduled updates. |

---
<h2 id="repository-structure">📦 Repository Structure</h2>

The repository is organized to make it easy to locate drivers, documentation, and supporting files for Hubitat projects. Key directories separate drivers by category, and each driver contains its own documentation and code.

```
HubitatProjects-Public
	├─ drivers
    ├─ GoogleEnvironmentalServices
      ├─ GoogleAirQualityTrackerPlus
        ├─ documentation\
			    │ ...\
      ├─ CHANGELOG.md
      ├─ README.md
      └─ googlePollenForecaster.groovy
```

---

<h2 id="reporting-bugs">🐞 Reporting Bugs</h2>

For issues and bugs, please use the [GitHub Issues](https://github.com/your-repo/google-pollen-forecaster/issues) page rather than the Hubitat Community forums. This ensures issues are tracked, discussed, and resolved in an organized manner.

- Include Hubitat firmware version, driver version (if available), and exact steps to reproduce.  
- Attach relevant logs and screenshots. Mask any sensitive data.  
- Search existing issues first to avoid duplicates.

💬 Kindly avoid posting bug reports directly in the Hubitat Community thread — it helps keep discussions organized and ensures your report is properly tracked and resolved.  

---

<h2 id="contributing">🤝 Contributing</h2>

If you’re interested in extending or enhancing the driver, you’re encouraged to fork the repository and create your own version. This approach supports creativity and allows each variation to evolve independently. Pull requests with improvements are also welcome, but the primary goal is to inspire new builds.

<h3 id="how-to-contribute">How to Contribute</h3>

- Fork the repository and create a feature branch.  
- Submit pull requests with clear descriptions and testing notes.  
- Follow Hubitat conventions for drivers and include well-commented code.  
- See 📄 Contributing Guide for process and style rules.

🟡Please note that response times for reviews or merges may vary due to personal  and professional commitments, but all thoughtful contributions will be read and appreciated.

💡 Tip: Respecting attribution helps keep open-source projects like this thriving, and it’s always fun to see how others experiment and build upon these drivers.

---

<h2 id="license-versioning">⚖️ License & Versioning</h2>

- **License:** All projects in this repository are shared under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), which allows you to freely use, modify, and distribute the code while providing proper attribution.  
- **Versioning:** Each driver or app maintains its own `CHANGELOG.md` for version history. Please refer to the project-specific changelog for detailed release notes and updates.

---

<h2 id="references-/-external-links">🔗 References / External Links</h2>

**Changelog and Contribution** 
📘 [Change log](CHANGELOG.md)
📘 [Contribution](CONTRIBUTING.md)

**Installation and Setup**
📘[Getting Your Google API Key](documents/)

**Utilities and Troubleshooting**
📘[Checking Your Data](documents/CheckYourData_GooglePollenForecaster.md)

**Reference and Information**
📘*Content TBD – coming in a future update.*

**Miscellaneous / Other**
📘*Content TBD – coming in a future update.*

---

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

- **Provided as-is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup. Use at your own risk.  
- **User responsibility:** You assume full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.  
- **Google API usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Ensure compliance and monitor your usage accordingly.  
- **Community-developed / Non-affiliation:** This repository is independently developed and maintained. It is **not affiliated with Google LLC**, Hubitat Inc., or any other company mentioned.  
- **Information accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.  
- **AI-assisted development:** All drivers were developed with the assistance of AI to my specifications. While I have tested and refined the code, AI-assisted outputs may include quirks, non-standard patterns, or unexpected behavior. Use caution and review thoroughly before deploying in critical systems.  
- **External dependencies:** Any third-party libraries, dashboards, or tools referenced in this repository are the responsibility of the respective developers. Users should ensure compatibility and review documentation before integrating.  

⚠️ Important: Always backup your Hubitat setup and test automation changes  incrementally. This repository is intended for educational and experimental purposes; professional judgment is required when deploying in your home.

---

<h2 id="revision-history">📝 Revision History</h2>

| Version | Date       | Author | Description                                                                                                     |
|---------|------------|--------|-----------------------------------------------------------------------------------------------------------------|
| 0.0.1   | 2026-03-25 | DBQ    | Initial draft of README for the Google Air Quality Tracker Plus Driver.                                         |