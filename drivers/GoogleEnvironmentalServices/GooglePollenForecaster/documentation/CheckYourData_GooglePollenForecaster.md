# 🌿 Checking Your Data - Google Pollen Forecaster
### _driver for Hubitat_

## 🔢 Version
**Document Version:** 1.0.2  
**Date:** 2025-12-15  
**Author:** David Ball-Quenneville  

---

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

-  [🧭Overview](#overview)
- [💡 Why You Would Use This Guide](#why-you-would-use-this-guide)
- [🧾  Viewing & Exploring API Data](#viewing--exploring-api-data)
- [🧠Visual Quick Reference: JSON Structure](#visual-quick-reference-json-structure)
- [🔢Understanding Pollen Data](#understanding-pollen-data)
- [💡Example Output](#example-output)
- [⚙️ Optional: Excel or Google Sheets Visualization](#optional-excel-or-google-sheets-visualization)
- [🔗 References / External Links](#references--external-links)
- [📝 Summary](#summary)
- [🛡️ Disclaimers](#disclaimers)
- [📜 Revision History](#revision-history)

</details>

---

<h2 id="overview">🧭 Overview</h2>

This guide provides a **high-level outline** for reviewing Google Pollen API data used by the *Google Pollen Forecaster* driver in Hubitat. It is designed for **intermediate to advanced users with a technical background**, such as those comfortable working with APIs or structured JSON data. By following this guide, you can confirm that your Google Pollen Forecaster driver is functioning correctly and spot potential issues in the data it receives.

> ⚠️ **Note:** This document is not meant as a step-by-step installation or configuration guide. Instead, it helps you quickly understand the API output, identify unusual or missing data, and verify that your Hubitat automations rely on accurate pollen information.

---

<h2 id="why-you-would-use-this-guide">💡 Why You Would Use This Guide</h2>

Use this guide to troubleshoot or confirm your Google Pollen Forecaster driver in the following situations:

1. **Unexpected Data:** When the driver returns pollen values that don’t match what you expect in your location.  
2. **Missing Data:** When no pollen data is returned for a day or region.  
3. **Partial Data:** When only some pollen types (e.g., tree, grass, weed) are reported.

> ⚠️ **Note:** Occasionally, there may be no data due to seasonal changes or API limitations. Always consider the time of year when evaluating pollen forecasts.

---

<h2 id="viewing-exploring-api-data">🧾 Viewing & Exploring API Data\</h2>

You can quickly explore Google Pollen API data without developer-level tools:

### 1. Copy Your API URL

Replace your key and location parameters as shown:
```
https://pollen.googleapis.com/v1/forecast:lookup?key=YOUR\_API\_KEY\&location.latitude=YOUR\_LAT\&location.longitude=YOUR\_LONG\&days=5
```

* Replace `YOUR_API_KEY` with your actual Google API key.  
* Latitude and longitude are for your location (example: Toronto, Ontario).  
* `days=5` requests a 5-day forecast.

### 2. View Data in a JSON Viewer

Recommended viewers:

📘 [JSON Viewer (stack.hu)](https://jsonviewer.stack.hu)  
📘 [JSON Formatter (curiousconcept.com)](https://jsonformatter.curiousconcept.com)

**Steps:**

1. Paste the full API URL or copied JSON response into the viewer.  
2. Click **Format** or **View**.  
3. Expand the tree to navigate the data easily.

### 3. Locate Key Sections

Focus on these parts of the JSON response:

* **Root Object:** Contains `regionCode` and `dailyInfo` array.  
* **`dailyInfo`** → Array with forecast for each requested day (`DayInfo`).  
* **`pollenTypeInfo`** → Nested in each day, lists pollen categories and their ratings.  
* **`indexInfo`** → Inside `pollenTypeInfo`, shows the Universal Pollen Index (UPI) for that pollen type.

---

<h2 id="visual-quick-reference-json-structure">🧠 Visual Quick Reference: JSON Structure</h2>

```
Root Object\
│\
├─ regionCode: "CA" ← Country/region code\
├─ dailyInfo \[ ] ← Array of forecast days\
│ ├─ date ← Forecast date\
│ ├─ pollenTypeInfo \[ ] ← Array of pollen types\
│ │ ├─ code ← Pollen type code (TREE, GRASS, WEED)\
│ │ ├─ displayName ← Human-readable name\
│ │ └─ indexInfo ← UPI info\
│ │ ├─ category ← Severity (NONE → VERY HIGH)\
│ │ └─ value ← Numeric UPI (0–5)\
│ └─ ... (other optional fields)\
└─ ... (other root-level fields)

```

💡 **Tip:** Focus on **`dailyInfo` → `pollenTypeInfo` → `indexInfo`**. Missing days, empty arrays, or unusual values here are usually the first sign of issues.

---

<h2 id="understanding-pollen-data">🔢 Understanding Pollen Data</h2>

The Universal Pollen Index (UPI) uses a **0–5 scale**:

| UPI Value | Category      | Description |
| :---: | :--- | :--- |
| 0     | **None**       | No pollen activity. |
| 1     | **Very Low**   | Minimal pollen, unlikely to affect most people. |
| 2     | **Low**        | Low pollen levels. |
| 3     | **Moderate**   | Noticeable levels; may trigger allergy symptoms. |
| 4     | **High**       | Significant pollen; likely to cause symptoms. |
| 5     | **Very High**  | Severe levels; limit outdoor activity. |

Forecast day structure:

| Field               | Description |
|--------------------|-------------|
| `date`             | Forecast date |
| `pollenTypeInfo`   | Array of pollen categories (Tree, Grass, Weed) |
| `indexInfo`        | UPI info for that pollen type |
| `category`         | Severity (e.g., MODERATE) |
| `value`            | Numeric UPI (0–5) |

---

<h2 id="example-output">💡 Example Output</h2>

Simplified 5-day Toronto forecast:

```json
{
  "regionCode": "CA",
  "dailyInfo": [
    {
      "date": "2025-10-23",
      "pollenTypeInfo": [
        {
          "code": "GRASS",
          "displayName": "Grass",
          "indexInfo": { "category": "LOW", "value": 1 }
        },
        {
          "code": "TREE",
          "displayName": "Tree",
          "indexInfo": { "category": "MODERATE", "value": 3 }
        },
        {
          "code": "WEED",
          "displayName": "Weed",
          "indexInfo": { "category": "LOW", "value": 1 }
        }
      ]
    }
  ]
}
```
### Interpretation

**Region:** Canada (`regionCode`: "CA")  **Date:** 2025-10-23  
**Tree pollen:** Moderate (UPI 3)  
**Grass pollen:** Low (UPI 1)  
**Weed pollen:** Low (UPI 1)  

> 🟡 **Note:** This example is for reference; actual output may vary.

---

<h2 id="optional-excel-or-google-sheets-visualization">⚙️Optional: Excel or Google Sheets Visualization</h2>

1. Copy the JSON response from the API.  
2. Use the **Import JSON** feature or add-on in Excel/Google Sheets.  
3. Columns for each pollen type, category, and value will be created.  
4. Filter or graph data to identify daily trends.

---

<h2 id="references-/-external-links">🔗 References / External Links</h2>

📘 [Google Pollen API Documentation](https://developers.google.com/pollen)  
📘 [JSON Viewer (stack.hu)](https://jsonviewer.stack.hu)  
📘 [JSON Formatter (curiousconcept.com)](https://jsonformatter.curiousconcept.com)

---

<h2 id="summary">📝 Summary</h2>

This high-level guide helps **intermediate to advanced users** review and understand Google Pollen API data. By focusing on `pollenTypeInfo` and `indexInfo`, you can quickly identify missing, unusual, or partial data and confirm that your Hubitat automations rely on accurate pollen information.

---

<h2 id="disclaimers">🛡️ Disclaimers</h2>

>⚠️ Important: Always back up your Hubitat setup and test automation changes incrementally. This repository is intended for educational and experimental purposes only. Users should exercise professional judgment when deploying in their own environments.

**Documentation Authorship Note:** The content of this documentation was created by the project maintainer. AI was used to assist with editorial tasks, including grammar, spelling, readability, formatting consistency, and overall clarity.

**Provided As-Is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup.

**User Responsibility:** The user assumes full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.

**Google API Usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Users should ensure compliance, monitor their usage, and are responsible for any charges or fees resulting from API calls.

**Community-Developed / Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with Google LLC, Hubitat Inc., or any other company mentioned.

**Information Accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.

**AI-Assisted Development:** All drivers were developed with AI assistance. While the code has been tested, AI-generated outputs may include quirks, non-standard patterns, or unexpected behavior. Users are responsible for testing, monitoring, and verifying the performance and safety of these drivers in their own environments.

**External Dependencies:** Any third-party libraries, dashboards, or tools referenced are maintained by their respective developers. Users should verify compatibility and assume responsibility for their integration.

---

<h2 id="revision-history"> 📜 Revision History</h2>

| Version | Date       | Author | Changes                                                                                                           |
|---------|------------|--------|-------------------------------------------------------------------------------------------------------------------|
| 1.00    | 2025-12-15 | DBQ    | Intial Release of Checking Your Data - Google Pollen Forecaster                                                   |
| 0.12    | 2025-12-10 | DBQ    | Clean up some Content, fix ToC links                                                                              |
| 0.11    | 2025-11-11 | DBQ    | Updated Overview for intermediate/advanced users, added high-level JSON visualization, and fixed Disclaimers.     |
| 0.10    | 2025-10-23 | DBQ    | Initial release — created detailed guide for viewing and understanding Google Pollen API data in Hubitat testing. |
