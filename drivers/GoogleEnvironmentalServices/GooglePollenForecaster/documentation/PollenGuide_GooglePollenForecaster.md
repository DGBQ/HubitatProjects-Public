# 🌱 Pollen Guide – Google Pollen Forecaster
### *for the driver for Hubitat Elevation*

## 🔢 Version
**Document Version:** 1.20  
**Date:** 2025-12-15  
**Author:** David Ball-Quenneville  

---

## 📑 Table of Contents  
<details>  
<summary>Click to expand Table of Contents</summary>  

- [🧭 Overview](#overview)  
- [💡 Why It Matters](#why-it-matters)  
- [🧩 Google Pollen API](#google-pollen-api)  
- [🌿 Available Pollen Types & Specific Plants](#available-pollen-types-specific-plants)
  - [↔️ Type-to-Plant Relationship](#type-to-plant-relationship)  
  - [🌾 Pollen Types](#pollen-types)  
  - [🌻 Specific Plants](#specific-plants)
- [🌍 Supported Regions](#supported-regions)  
- [⚙️ Driver Features & Capabilities](#driver-features-capabilities)  
  - [🔘 Master & Individual Pollen Toggles](#master-individual-pollen-toggles)  
  - [📊 Status & Attributes](#status-attributes)
    - [📏 Pollen Index Attributes](#pollen-index-attributes)
    - [🌡️ Pollen Level Attributes](#pollen-level-attributes)
    - [🩺 Health Recommendation Attributes](#health-recommendation-attributes)
    - [⭐ Overall & Dominant Pollen Attributes](#overall-dominant-pollen-attributes)
  - [🕰️ Blockout & Seasonal Behavior](#blockout-seasonal-behavior)  
- [📝 Summary](#summary)  
- [🛡️ Disclaimers](#disclaimers)  
- [🔗 Reference / External Links](#reference-external-links)
- [📜 Revision History](#revision-history)  

</details>  

---

<h2 id="overview">🧭 Overview</h2>

The *Google Pollen Forecaster* driver for **Hubitat Elevation** leverages the Google Pollen API to deliver hyper-local pollen forecasts. This integration assists in managing allergies and enhancing smart home environments with health-focused automations, such as triggering **Rules** or **Webcore** pistons when specific allergen levels (e.g., ragweed) spike. 

This document outlines the pollen types and specific plants supported by the API, their availability by country or region, and how this data is integrated within the driver. It also provides guidance on handling seasonal variations, off-season data, and region-specific availability to ensure **Dashboards** and automations remain accurate. 

The driver translates API data into actionable **Current States**, enabling users to monitor overall pollen trends, track specific plant-level **Attributes**, and apply health recommendations for each pollen type.

---

<h2 id="why-it-matters">💡 Why It Matters</h2>

Utilizing pollen data assists **Hubitat Elevation** users in three primary ways:  

- **Allergy Management:** Track pollen levels for specific allergens such as ragweed or birch, allowing proactive steps like closing windows or activating air purifiers before high exposure.  
- **Smart Automations:** Enable automations based on pollen data. For example, adjusting HVAC settings or triggering notifications when `grassPollenLevel` reaches "High."  
- **Regional Relevance:** Understanding which pollen types are available in your region prevents irrelevant monitoring, keeping **Current States** clean and **Dashboards** precise.

---

<h2 id="google-pollen-api">🧩 Google Pollen API</h2>

The *Google Pollen Forecaster* driver relies on the Google Pollen API to provide accurate, hyper-local daily forecasts. This API offers detailed information for multiple pollen types and specific plants, ideal for health-focused smart home integration.

**Benefits for Hubitat Elevation Users**  
By leveraging this API, the driver allows users to:

- Monitor major pollen types—Trees, Grass, and Weeds—for broad allergen awareness.  
- Track specific plants like ragweed, birch, or alder for precise allergen tracking.  
- Maintain regional relevance by displaying only pollen types and plants available for your specific country.  
- Rely on daily forecasts to prevent stale or misleading data from affecting automations.

---

<h2 id="available-pollen-types-specific-plants">🌿 Available Pollen Types & Specific Plants</h2>

The Google Pollen API organizes pollen into broad categories called Pollen Types, each containing multiple Specific Plants. This hierarchy allows the driver to provide both high-level trends and detailed plant-specific data.

<h3 id="type-to-plant-relationship">↔️ Type-to-Plant Relationship</h3>

Each pollen type encompasses a collection of specific plants. The driver tracks both overall trends and individual plant levels.

| Pollen Type | Typical Season        | Specific Plants (All Supported)                                                                                             |
|-------------|-----------------------|-----------------------------------------------------------------------------------------------------------------------------|
| Trees       | Spring → Early Summer | Alder, Ash, Birch, Cottonwood, Cypress Pine, Elm, Hazel, Japanese Cedar, Japanese Cypress, Juniper, Maple, Oak, Olive, Pine |
| Grass       | Late Spring → Summer  | Graminales                                                                                                                  |
| Weeds       | Late Summer → Fall    | Mugwort, Ragweed                                                                                                            |

<h3 id="pollen-types">🌾 Pollen Types</h3>

The API categorizes pollen into three primary types, which are available as driver **Attributes**:

- **Trees:** Typically active in spring to early summer.  
- **Grass:** Peaks in late spring through summer.  
- **Weeds:** Often active in late summer and fall.  

🟡 **Note:** Not all pollen types are available in every country, and off-season data may be absent (e.g., tree pollen in winter).

<h3 id="specific-plants">🌻 Specific Plants</h3>

For granular monitoring, the API provides data at the plant level. These plants can be enabled individually via **Preferences** → **Pollen Type Display Settings**. **Attributes** include numeric indices, qualitative levels, and health recommendations.

**Currently Supported Plants include:**  
Alder, Ash, Birch, Cottonwood, Cypress Pine, Elm, Graminales, Grass, Hazel, Japanese Cedar, Japanese Cypress, Juniper, Maple, Mugwort, Oak, Olive, Pine, Ragweed.

---

<h2 id="supported-regions">🌍 Supported Regions</h2>

The driver provides pollen data for various regions worldwide. While all regions support at least one main pollen type, specific plant availability varies.

| Region             | Pollen Types           | Representative Plants                            |
|--------------------|------------------------|--------------------------------------------------|
| North America      | Trees, Grass, Weeds    | Maple, Birch, Ragweed, Grasses                   |
| Europe             | Trees, Grass, Weeds    | Hazel, Alder, Birch, Ragweed, Grasses            |
| Asia               | Trees, Grass           | Japanese Cedar, Japanese Cypress, Grasses        |
| Australia & Oceania| Trees, Grass           | Cypress Pine, Grasses                            |
| South America      | Grass                  | Grasses                                          |
| Africa             | Grass, Trees           | Grasses, Birch                                   |

✅ **Best practice:** Refer to the official Google Pollen API documentation for the most current regional support data.

---

<h2 id="driver-features-capabilities">⚙️ Driver Features & Capabilities</h2>

The driver provides actionable **Current States** for **Dashboards**, **Rule Machine**, and **Webcore**.

*   **Master and Individual Pollen Toggles:** Control which pollen categories and specific types are monitored. Only enabled **Attributes** appear in the device state.
*   **Pollen Level & Index Tracking:** Provides numerical indices (1–5) and descriptive levels (e.g., "Low," "Moderate," "High").
*   **Health Recommendations:** Contextual guidance generated from the API for allergy management.
*   **Dominant Pollen Resolver:** Smart logic selects the most relevant pollen type when multiple types peak at the same level.
*   **Blockout Management:** Seasonal off-periods automatically suspend updates and display "Suspended" states to prevent stale data triggers.
*   **Customizable Display:** Hide irrelevant data to reduce **Dashboard** clutter.

<h3 id="master-individual-pollen-toggles">🔘 Master & Individual Pollen Toggles</h3>

*   Enable or disable entire categories: **Trees, Grass, Weeds.**
*   Toggle individual types (e.g., Ragweed, Birch, Alder) within those categories.
*   Disabled types do not create **Attributes**, keeping the **Device Detail Page** clean.

---

<h3 id="status-attributes">📊 Status & Attributes</h3>

This section summarizes **Attributes** and their behaviors under different conditions: normal reporting, dormant (off-season), unavailable (regional/API limitation), and blockout periods.

<h4 id="pollen-index-attributes">📏 Pollen Index Attributes</h4>

| **Attribute**           | **Type**     | **API Values**                   | **System States**                      | **Description**                                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|---------------------------------------------------------------------------------------|
| General Pollen Index    | Numeric      | 1 → 5                             | 0 → Dormant, N/A → No data, Index Off → Disabled | Severity of pollen for each type.                                                     |
| Overall Pollen Index    | Calculated   | From active indices               | 0 → Dormant, N/A → No data, Suspended → Blockout | Highest active pollen index. Ties resolved by: Grass → Trees → Weeds → Alphabetical. |
| Dominant Pollen Index   | Calculated   | Same as Overall                   | Same as Overall                       | Current dominant pollen type index.                                                   |

<h4 id="pollen-level-attributes">🌡️ Pollen Level Attributes</h4>

| **Attribute**           | **Type**     | **API Values**                   | **System States**                      | **Description**                                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|---------------------------------------------------------------------------------------|
| General Pollen Level    | Qualitative  | None, Low, Moderate, High, Very High | Dormant → Off-season, N/A → No data, Level Off → Disabled | Qualitative severity level for each pollen type.                                     |
| Overall Pollen Level    | Calculated   | From active levels                | Dormant / N/A / Suspended              | Highest active level across enabled types.                                           |
| Individual Pollen Level | Qualitative  | None → Very High                  | Dormant / N/A / Level Off              | Level for each enabled specific plant.                                               |

<h4 id="health-recommendation-attributes">🩺 Health Recommendation Attributes</h4>

| **Attribute**           | **Type**     | **API Values**                   | **System States**                      | **Description**                                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|---------------------------------------------------------------------------------------|
| General Health Recommendation | Dynamic      | API string                        | N/A → No data, Health Off → Disabled  | Precautionary guidance; not a substitute for medical advice.                         |
| Overall Health Recommendation | Calculated   | From active types                 | N/A / Health Off / Suspended          | Summarizes guidance for the dominant pollen type.                                    |
| Individual Health Recommendation | Dynamic      | API string                        | N/A / Health Off                      | Specific guidance for each enabled plant type.                                       |

<h4 id="overall-dominant-pollen-attributes">⭐ Overall & Dominant Pollen Attributes</h4>

| Attribute Type   | API Values | System States               | Description                                                                                             |
|-----------------|------------|----------------------------|---------------------------------------------------------------------------------------------------------|
| Overall Pollen  | Calculated | Dormant / N/A / Suspended  | Displays dominant pollen level across all types. Suspended during blockout periods.                     |
| Dominant Pollen | Calculated | Dormant / N/A / Suspended  | Identifies the dominant pollen type. Tie-breaking: Grass → Trees → Weeds → Alphabetical.                |

<h3 id="blockout-seasonal-behavior">🕰️ Blockout & Seasonal Behavior</h3>

When **Use Blockout Dates** is enabled in **Preferences**, all **Attributes** are set to "Suspended" or placeholders to prevent stale data from triggering automations.

| Attribute                             | Blockout State |
|---------------------------------------|----------------|
| `apiStatus`                           | Blockout       |
| `blockoutActive`                      | true           |
| `todayPollenLevel`                    | Suspended      |
| `tomorrowPollenLevel`                 | Suspended      |
| `overallPollenLevel`                  | Suspended      |
| `dominantPollen`                      | Suspended      |
| `healthRecommendation`                | N/A            |
| `grassPollenIndex`                    | 0              |
| `grassPollenLevel`                    | Suspended      |
| `treePollenIndex`                     | 0              |
| `treePollenLevel`                     | Suspended      |
| `weedPollenIndex`                     | 0              |
| `weedPollenLevel`                     | Suspended      |
| `[individual type]PollenIndex`        | 0              |
| `[individual type]PollenLevel`        | Suspended      |
| `[individual type]HealthRecommendation`| N/A            |

---

<h2 id="summary">📝 Summary</h2>

The *Google Pollen Forecaster* driver transforms API data into actionable, region-aware **Current States**. Users can monitor overall trends and specific plant levels, receive health guidance, and integrate data into **Dashboards** and automations. Seasonal blockouts ensure stale data does not trigger actions, while individual toggles provide a customizable user experience.

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

<h2 id="reference-external-links">🔗 Reference / External Links</h2>

📘 [Google Pollen API Documentation](https://developers.google.com/pollen)  
📘 [Google Pollen API Supported Countries & Plants](https://developers.google.com/maps/documentation/pollen/coverage)

---

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date       | Author | Changes                                                                |
|---------|------------|--------|------------------------------------------------------------------------|
| 1.20    | 2025-12-15 | DBQ    | Audited for terminology consistency and Markdown structure integrity.  |
| 1.00    | 2025-12-15 | DBQ    | Initial Release of: Pollen Guide – Google Pollen Forecaster           |
| 0.30    | 2025-12-15 | DBQ    | Fix ToC links, Formatting, Content updates                             |
| 0.20    | 2025-11-25 | DBQ    | Adding missing content and updated formatting                         |
| 0.10    | 2025-11-25 | DBQ    | Initial release of Google Pollen Forecaster End User Guide for Hubitat |