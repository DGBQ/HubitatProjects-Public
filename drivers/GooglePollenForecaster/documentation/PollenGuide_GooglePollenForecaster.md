# 🌱 Pollen Guide – Google Pollen Forecaster
### _Driver for Hubitat_

## 🔢 Version
**Document Version:** 1.20  
**Date:** 2025-12-15  
**Author:** David Ball-Quenneville  

---

## 📑 Table of Contents  
<details>  
<summary>Click to expand Table of Contents</summary>  

- 🧭[Overview](#overview--)  
- 💡 [Why It Matters](#why-it-matters--)  
- 🧩[Google Pollen API](#google-pollen-api--)  
- 🌿 [Available Pollen Types & Specific Plants ](#available-pollen-types--specific-plants)
  - ↔️[Type-to-Plant Relationship](#type-to-plant-relationship--)  
  - 🌾 [Pollen Types](#pollen-types--)  
  - 🌻 [Specific Plants](#specific-plants--)
- 🌍 [Supported Regions](#supported-regions--)  
- ⚙️ [Driver Features & Capabilities](#driver-features--capabilities--)  
  - 🔘 [Master & Individual Pollen Toggles](#master--individual-pollen-toggles--)  
      - 📊 [Status & Attributes](#status--attributes--)
          - 📏 [Pollen Index Attributes](#pollen-index-attributes)
          - 🌡️[Pollen Level Attributes](#pollen-level-attributes)
          - 🩺 [Health Recommendation Attributes](#health-recommendation-attributes)
          -  ⭐ [Overall & Dominant Pollen Attributes](#overall--dominant-pollen-attributes)
          - 🕰️ [Blockout & Seasonal Behavior](#blockout--seasonal-behavior)  
- 📝 [Summary](#summary)  
- 🛡️ [Disclaimers](#disclaimers)  
- 🔗 [Reference / External Links](#reference--external-links)
- 📝 [Revision History](#revision-history)  

</details>  

---

<h2 id="overview">🧭 Overview</h2>

The Google Pollen Forecaster Driver for Hubitat leverages the Google Pollen API to deliver hyper-local pollen forecasts, helping you manage allergies or enhance your smart home with health-focused automations (e.g., triggering rule or piston warnings when ragweed levels spike). 

This document outlines the pollen types and specific plants supported by the API, their availability by country or region, and how this data is integrated within the Google Pollen Forecaster Driver. It also provides guidance on handling seasonal variations, off-season data, and region-specific availability to ensure your dashboards and automations remain accurate and meaningful. 

The driver translates API data into actionable Current States, enabling Hubitat users to monitor overall pollen trends, track specific plant-level pollen indices, and apply health recommendations for each pollen type.

---

<h2 id="why-it-matters">💡 Why It Matters</h2>

Understanding and using pollen data can help Hubitat users in three primary ways:  

- **Allergy Management:** Track pollen levels for specific allergens such as ragweed or birch, allowing proactive steps like closing windows or using air purifiers before high pollen exposure.  
- **Smart Automations:** Enable Hubitat automations based on pollen data. For example, turning on air purifiers, adjusting HVAC settings, or triggering notifications when `grassPollenLevel` is “high.”  
- **Regional Relevance:** Knowing which pollen types are available in your region helps you avoid irrelevant monitoring, keeping your driver’s Current States clean, dashboards relevant, and automations precise.

---

<h2 id="google-pollen-api">🧩 Google Pollen API</h2>

The Google Pollen Forecaster Driver for Hubitat relies on the Google Pollen API to provide accurate, hyper-local pollen once-a-day forecasts. This API offers detailed information for multiple pollen types and specific plants, making it ideal for both allergy management and health-focused smart home automations.

**Benefits for Hubitat Users**  
By leveraging this API, the driver allows users to:

- Monitor major pollen types — Trees, Grass, and Weeds — for broad allergen awareness.  
- Track specific plants like ragweed, birch, or alder for precise allergen tracking.  
- Maintain regional relevance, displaying only pollen types and plants available for your country, keeping dashboards and Current States accurate.  
- Rely on daily forecasts to prevent stale or misleading data from affecting automations.

---

<h2 id="available-pollen-types-specific-plants">🌿 Available Pollen Types & Specific Plants</h2>

The Google Pollen API organizes pollen into broad categories called Pollen Types, each containing multiple Specific Plants. This hierarchy allows the driver to provide both high-level pollen trends and detailed plant-specific data, ensuring accurate monitoring and automation triggers.

<h3 id="type-to-plant-relationship">↔️ Type-to-Plant Relationship</h3>

Each pollen type encompasses a collection of specific plants. The driver tracks both overall pollen trends for each type and individual plant pollen levels, enabling granular monitoring. This ensures automations respond to relevant allergen spikes while reducing irrelevant data clutter.

| Pollen Type | Typical Season        | Specific Plants (All Supported)                                                                                             |
|-------------|-----------------------|-----------------------------------------------------------------------------------------------------------------------------|
| Trees       | Spring → Early Summer | Alder, Ash, Birch, Cottonwood, Cypress Pine, Elm, Hazel, Japanese Cedar, Japanese Cypress, Juniper, Maple, Oak, Olive, Pine |
| Grass       | Late Spring → Summer  | Graminales                                                                                                                  |
| Weeds       | Late Summer → Fall    | Mugwort, Ragweed                                                                                                            |

<h3 id="pollen-types">🌾 Pollen Types</h3>

The API categorizes pollen into three primary types, which are available as driver attributes and form the foundation for calculating overall and dominant pollen levels:

- **Trees:** Covers pollen from multiple tree species, typically active in spring to early summer.  
- **Grass:** Includes common grass pollen species, peaking in late spring through summer.  
- **Weeds:** Encompasses weed pollen such as ragweed, often active in late summer and fall.  

🟡 **Note:** Not all pollen types are available in every country, and off-season data may be absent (e.g., tree pollen in winter).

<h3 id="specific-plants">🌻 Specific Plants</h3>

For granular monitoring, the API provides data at the plant level. These plants can be enabled individually via Preferences → Pollen Type Display Settings. Attributes include numeric indices, qualitative levels, and health recommendations.

**Currently Supported Plants include:**
Alder, Ash, Birch, Cottonwood, Cypress Pine, Elm, Graminales, Grass, Hazel, Japanese Cedar, Japanese Cypress, Juniper, Maple, Mugwort, Oak, Olive, Pine, Ragweed

---

<h2 id="supported-regions">🌍 Supported Regions</h2>

The driver provides pollen data for a wide range of countries and regions worldwide. While all regions support at least one of the main pollen types, availability of specific plants may vary.

| Region         | Pollen Types           | Representative Plants                            |
|----------------|------------------------|--------------------------------------------------|
| North America  | Trees, Grass, Weeds    | Maple, Birch, Ragweed, Grasses                   |
| Europe         | Trees, Grass, Weeds    | Hazel, Alder, Birch, Ragweed, Grasses            |
| Asia           | Trees, Grass           | Japanese Cedar, Japanese Cypress, Grasses       |
| Australia & Oceania | Trees, Grass         | Cypress Pine, Grasses                            |
| South America  | Grass                  | Grasses                                          |
| Africa         | Grass, Trees           | Grasses, Birch                                   |

✅ **Best practice:** Check the official [Google Pollen API Supported Countries & Plants](https://www.google.com/search?q=Google+pollen+API+supported+countries+plants) for the most current information.

---

<h2 id="driver-features-capabilities">⚙️ Driver Features & Capabilities</h2>

The driver provides actionable Current States and integrations for Hubitat dashboards, Rule Machine, and WebCoRE automations.
* **Master and Individual Pollen Toggles** – Control which pollen categories (Trees, Grass, Weeds) and specific types (e.g., Ragweed, Birch, Alder) are monitored. Only enabled attributes appear on your dashboard.
* **Pollen Level & Index Tracking** – Provides numerical indices (1–5) and descriptive levels (“Low,” “Moderate,” “High”) for each active pollen type.
* **Health Recommendations** – Contextual guidance generated from Google’s API for allergy management.
* **Dominant Pollen Resolver**– Smart logic selects the most relevant pollen type when multiple types peak at the same level.
* **Blockout Management**– Seasonal off-periods automatically suspend updates and display consistent “Suspended” states, preventing stale data from triggering automations.
* **Dashboard & Automation Ready **– Attributes are designed for easy integration with Hubitat dashboards, Rule Machine, and WebCoRE automations.
* **Customizable Display** – Users can choose which types to monitor and hide irrelevant data to reduce dashboard clutter.


<h3 id="master-individual-pollen-toggles">🔘 Master & Individual Pollen Toggles</h3>

* Allows users to enable or disable entire pollen categories: **Trees, Grass, Weeds.**
* Individual pollen types (e.g., Ragweed, Birch, Alder) can be toggled within each category.
* Only enabled types create attributes in the system, keeping dashboards and automations clean.

---

<h4 id="status-attributes">📊 Status & Attributes</h3>

This subsection summarizes all pollen-related attributes and their behaviors under different conditions: normal API reporting, dormant (off-season), unavailable (regional/API limitation), and blockout periods. These tables help you understand what each attribute represents and how it behaves for automations, dashboards, or health monitoring.

<h4 id="pollen-index-attributes">📏Pollen Index Attributes</h3>

The **Pollen Index Attributes** provide numeric values that indicate the severity of pollen for each type, helping track allergy levels throughout the season. Overall and Dominant indices summarize the highest active pollen levels, highlighting which type is currently most impactful.

| **Attribute**           | **Type**     | **API Values**                   | **System States**                     | **Description**                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|-----------------------------------------------------------------------|
| General Pollen Index     | Numeric      | 1 → 5                             | 0 → Dormant, N/A → No data, Index Off → Disabled | Severity of pollen for each type. Dormant = out-of-season; N/A = unavailable; Index Off = tracking disabled. |
| Overall Pollen Index     | Calculated   | From active indices               | 0 → Dormant, N/A → No data, Suspended → Blockout | Highest active pollen index across types; ties resolved by Grass → Trees → Weeds → Alphabetical. |
| Dominant Pollen Index    | Calculated   | Same as Overall                   | Same as Overall                       | Currently dominant pollen type index; ties resolved by priority logic. |

🟡**Note:** Dormant or unavailable types do not affect Overall or Dominant Pollen Index calculations.

<h4 id="pollen-level-attributes">🌡️ Pollen Level Attributes</h3>

The **Pollen Level Attributes** indicate the qualitative severity of pollen, ranging from None to Very High, for each type throughout the season. Overall and Individual levels summarize the highest active pollen levels and provide detailed insight for each plant type.

| **Attribute**           | **Type**     | **API Values**                   | **System States**                     | **Description**                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|-----------------------------------------------------------------------|
| General Pollen Level     | Qualitative  | None, Low, Moderate, High, Very High | Dormant → Off-season, N/A → No data, Level Off → Disabled | Severity level for each pollen type. |
| Overall Pollen Level     | Calculated   | From active levels                | Dormant / N/A / Suspended             | Highest active level across enabled types.                           |
| Individual Pollen Level  | Qualitative  | None → Very High                  | Dormant / N/A / Level Off             | Level for each enabled plant.                                         |

🟡**Note:** Dormant or unavailable types are ignored in Overall calculations.

<h4 id="health-recommendation-attributes">🩺 Health Recommendation Attributes</h3>

The **Health Recommendation Attributes** provide precautionary guidance based on pollen levels, offering advice for general, overall, and individual plant types. Overall and Individual recommendations summarize or specify guidance for dominant and enabled pollen types, helping users manage allergy exposure.

| **Attribute**           | **Type**     | **API Values**                   | **System States**                     | **Description**                                                       |
|-------------------------|--------------|-----------------------------------|---------------------------------------|-----------------------------------------------------------------------|
| General Health Recommendation | Dynamic     | API string                         | N/A → No data, Health Off → Disabled  | Precautionary guidance; not a substitute for medical advice.          |
| Overall Health Recommendation | Calculated   | From active types                 | N/A / Health Off / Suspended          | Summarizes guidance for dominant pollen type.                        |
| Individual Health Recommendation | Dynamic     | API string                         | N/A / Health Off                      | Specific guidance for each enabled pollen type.                       |

<h4 id="overall-dominant-pollen-attributes">⭐Overall & Dominant Pollen Attributes</h3>

The **Overall & Dominant Pollen Attributes** summarize the highest pollen activity for the current day, highlighting overall severity across all types. The Dominant attribute identifies which pollen type is currently most prevalent, using a defined tie-breaking priority when levels are equal.

| Attribute Type   | API Values  | System States              | Description                                                                                     |
|-----------------|------------|----------------------------|-------------------------------------------------------------------------------------------------|
| Overall Pollen  | Calculated | Dormant / N/A / Suspended | Displays the dominant pollen level across all types for the current day. Suspended during blockout periods. |
| Dominant Pollen | Calculated | Dormant / N/A / Suspended | Identifies which pollen type is currently dominant. Tie-breaking: Grass → Trees → Weeds → Alphabetical. |

<h4 id="blockout-seasonal-behavior">🕰️ Blockout & Seasonal Behavior</h3>

When Use Blockout Dates is ON, all attributes are set to Suspended or placeholders to prevent stale data or unwanted automation triggers. Attributes automatically revert to normal once blockout ends and the next API poll occurs.

| Attribute                         | Blockout State |
|----------------------------------|----------------|
| apiStatus                         | Blockout       |
| blockoutActive                     | true           |
| todayPollenLevel                   | Suspended      |
| tomorrowPollenLevel                | Suspended      |
| day3PollenLevel                    | Suspended      |
| day4PollenLevel                    | Suspended      |
| day5PollenLevel                    | Suspended      |
| overallPollenLevel                 | Suspended      |
| dominantPollen                     | Suspended      |
| healthRecommendation               | N/A            |
| tomorrowHealthRecommendation       | N/A            |
| grassPollenIndex                   | 0              |
| grassPollenLevel                   | Suspended      |
| grassHealthRecommendation          | N/A            |
| treePollenIndex                    | 0              |
| treePollenLevel                    | Suspended      |
| treeHealthRecommendation           | N/A            |
| weedPollenIndex                    | 0              |
| weedPollenLevel                    | Suspended      |
| weedHealthRecommendation           | N/A            |
| [individual type]PollenIndex       | 0              |
| [individual type]PollenLevel       | Suspended      |
| [individual type]HealthRecommendation | N/A        |

---
<h2 id="summary">📝 Summary</h2>

The Google Pollen Forecaster Driver for Hubitat transforms Google Pollen API data into actionable, region-aware Current States. Users can monitor both overall pollen trends and specific plant pollen levels, receive health guidance, and integrate this data into Hubitat dashboards and automations. Seasonal blockouts ensure stale data does not trigger actions, and individual toggles allow a clean and customizable experience.

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

.\⚠️ Important: Always back up your Hubitat setup and test automation changes incrementally. This repository is intended for educational and experimental purposes only. Users should exercise professional judgment when deploying in their own environments.

**Documentation Authorship Note:** The content of this documentation was created by the project maintainer. AI was used to assist with editorial tasks, including grammar, spelling, readability, formatting consistency, and overall clarity.

**Provided As-Is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup.

**User Responsibility:** The user assumes full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.

**Google API Usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Users should ensure compliance, monitor their usage, and are responsible for any charges or fees resulting from API calls.

**Community-Developed / Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with Google LLC, Hubitat Inc., or any other company mentioned.

**Information Accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.

**AI-Assisted Development:** All drivers were developed with AI assistance. While the code has been tested, AI-generated outputs may include quirks, non-standard patterns, or unexpected behavior. Users are responsible for testing, monitoring, and verifying the performance and safety of these drivers in their own environments.

**External Dependencies:** Any third-party libraries, dashboards, or tools referenced are maintained by their respective developers. Users should verify compatibility and assume responsibility for their integration.

---

<h2 id="reference-/-external-links">🔗 Reference / External Links</h2>

📘 [Google Pollen API Documentation](https://developers.google.com/pollen)  
📘 [Google Pollen API Supported Countries & Plants](https://www.google.com/search?q=Google+pollen+API+supported+countries+plants)


---

<h2 id="revision-history"> 📜 Revision History</h2>

| Version | Date       | Author | Changes                                                                |
|---------|------------|--------|------------------------------------------------------------------------|
| 1.00    | 2025-12-15 | DBQ    | Intial Release of: Pollen Guide – Google Pollen Forecaster             |
| 0.30    | 2025-12-15 | DBQ    | Fix ToC links, Formating, Content updates                              |
| 0.20    | 2025-11-25 | DBQ    | Adding missing content and updated fromating                           |
| 0.10    | 2025-11-25 | DBQ    | Initial release of Google Pollen Forecaster End User Guide for Hubitat |
