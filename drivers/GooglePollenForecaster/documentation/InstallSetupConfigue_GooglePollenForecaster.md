# ⚙️ Install, Setup, Configure – Google Pollen Forecaster
### _Driver for Hubitat_

## 🔢 Version
**Document Version:** 0.11 (Draft)
**Date:** 2025-12-10  
**Managing Author:** David Ball-Quenneville  
**Associate Author:** ChatGPT and Gemini

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- 🧭 [Overview](#overview)
- ⬇️ [Installation](#installation)
- 🛠️ [Setup](#setup)
- ⚖️ [Configuration](#configuration)
  - ➡️ [Commands](#commands)
    - 🧮 [Poll](#polling-button)
  - 🎛️ [Preferences](#preferences)
    - 🔑 [Google Pollen API Key](#google-pollen-api-key)
    - ⏰ [Primary Daily Poll](#primary-daily-poll)
    - ⏰ [Secondary Daily Poll](#secondary-daily-poll)
    - 🛑 [Max API Calls Per Day](#max-api-calls-per-day)
    - 🗺️ [Use Custom Coordinates](#use-custom-coordinates)
    - 🚫 [Use Blockout Dates?](#use-blockout-dates)
    - 🔄 [Auto-Reset Errors at Midnight](#auto-reset-errors-at-midnight)
    - 🛡️ [Advanced API Retry Settings](#advanced-api-retry-settings)
    - 🌱 [Pollen Type Group Selection](#pollen-type-group-selection)
    - 🧬[Individual Pollen Species (Granular Data)](#individual-pollen-species-granular-data)
- 🏷️ [Current States (Device Attributes)](#current-states-device-attributes)
    - 🔬 [Summary Pollen Data](#summary-pollen-data)
    - 📊[Driver Status and Metadata](#driver-status-and-metadata)
    - 🌿 [Granular Pollen Data (Conditional)](#granular-pollen-data-conditional)    
- 📝 [Summary](#summary)
- 🛡️ [Disclaimers](#disclaimers)
- 🔗 [Reference / External Links](#reference--external-links)
- 📜 [Revision History](#revision-history)

</details>

---

## 🧭 Overview

This document provides a complete walkthrough for installing, setting up, and configuring the **Google Pollen Forecaster** driver on Hubitat. It begins with the steps required to add the driver to your hub, enter your Google Pollen API key, and confirm the initial connection. From there, it guides you through all available configuration options, including location settings, polling schedules, blockout periods, quota protection controls, species-level data toggles, and troubleshooting tools.

Each section is organized to mirror the driver’s workflow:  
1) **Install** the driver code,  
2) **Set up** your API access and basic settings, and  
3) **Configure** advanced options to tailor the driver’s behavior to your environment.

By the end, you’ll understand how the driver operates, how data is retrieved and updated, and how to use the reported attributes in dashboards, rules, and automations. This overview serves as a roadmap for the detailed instructions and reference material that follow.

---

## ⬇️ Installation

> 🚧 **Documentation Scope:** The detailed explanation for this section is currently being finalized.  
> Please check back in a future Beta release for the complete guide. Thank you for your patience!

---

## 🛠️ Setup

Getting started with the driver is simple: just enter your **Google Pollen API key** and save the change, and the driver will begin retrieving data using its default settings. While it works out-of-the-box with minimal setup, reviewing the [**Configuration**](#configurationsection) section is recommended to unlock its full potential—fine-tune polling schedules, enable species-level tracking, manage blockout periods, and adjust other settings for more accurate and reliable pollen forecasts.


---

## ⚖️ Configuration

The configuration section guides you through all the adjustable settings that control how the **Google Pollen Forecaster** driver behaves on Hubitat. Here you’ll set your preferred polling schedules, manage blockout periods, enable or disable species-level data, and choose options that help protect your Google API quota. You can also customize location handling, notification behaviour, and other advanced features to match your environment. Each option is explained clearly so you can understand what it does and how it affects data updates, status reporting, and driver performance.


### ➡️ Commands

The Commands section explains the manual actions you can trigger directly from the device page to control how the Google Pollen Forecaster retrieves data. These commands are useful for testing, troubleshooting, and forcing immediate updates outside the scheduled polling routine.

#### 🧮 Polling (Button)

> **Description:** Immediately forces a manual request to the Google Pollen API to fetch the very latest forecast data.

##### 🎯 Purpose and Use
This command is primarily used to:
* **Manually Force an Update:** Bypass the regular scheduled polling intervals to retrieve new data immediately.
* **Troubleshoot:** Quickly test the API key and location settings to verify connectivity.

##### 📌 Options and Parameters
| Parameter | Description | Valid Options |
| :--- | :--- | :--- |
| **(None)** | This command is designed for manual execution and accepts no additional parameters. | N/A |

##### 💡 Hints and Best Practices
* **API Call Consumption:** Because the driver does **not** check a data cache before execution, clicking the **Poll** button will **always** consume one API call from your daily quota. Use this command sparingly.
* **Blockout Constraint:** If the `Use Blockout Dates?` preference is enabled in **Preferences** and the current date/time falls within a blockout period, the poll will be prevented.
* **Blockout Feedback:** If the poll is blocked, you will see a message in your Hubitat Logs (`Logs` tab), and the device's `apiStatus` attribute will change to **Blockout** until the blockout period ends.

---

### 🎛️ Preferences

The Preferences section covers all configurable options that control how the Google Pollen Forecaster operates. It explains each setting, from API key entry and location selection to polling schedules, blockout periods, and species-level data, helping you tailor the driver to your environment and ensure reliable, accurate pollen forecasts.

#### 🔑 Google Pollen API Key

> **Description:** The unique security key provided by Google Cloud, which authenticates your Hubitat driver and authorizes data access to the Google Pollen API.

##### 🎯 Purpose and Use
This is a **mandatory prerequisite** for the driver. Without a valid API key, the driver cannot establish a connection, request, or receive *any* pollen forecast data. This field is the sole requirement for securing access to the Google API service.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Text Input** | Full alphanumeric string provided by Google Cloud | (Empty) |

* **Note:** The key must be entered exactly as generated by Google Cloud.

##### 💡 Hints and Best Practices
* **Mandatory Field:** This field must be filled out for the driver to function.
* **Security:** Treat your API key as a password. It should never be shared publicly (e.g., in forum posts or shared screenshots), as it is linked directly to your Google Cloud billing account and quota.
* **Activation:** Ensure that the Google Pollen API service is enabled within your Google Cloud project for the provided key to be valid.

#### ⏰ Primary Daily Poll

> **Description:** Sets the mandatory, optimal time each day for the driver to attempt to fetch the latest Google Pollen Forecast data. This is the primary attempt to capture the new daily forecast data as soon as it is released.

##### 🎯 Purpose and Use
This setting is crucial for ensuring your device receives the newest daily forecast data as early as possible. It is intended to run at a time when the new forecast is typically available from the Google API, ensuring data freshness for the entire day.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Time Selector** | Hour, Minute, AM/PM Pick List | 6:00 AM |

* **Example:** Setting this to **6:00 AM** means the driver will schedule its main daily retrieval job for 6:00 AM every morning.

##### 💡 Hints and Best Practices
* **Required Field:** This is a required field, as the forecast data release is daily.
* **API Impact:** This setting guarantees **one API call per day** (at the specified time) dedicated to fetching the new daily forecast.
* **Timing:** The default of 6:00 AM is recommended as it balances early retrieval with typical hub schedules.

#### ⏰ Secondary Daily Poll

> **Description:** Sets a backup time each day for the driver to attempt to fetch the latest Google Pollen Forecast data. This is a fail-safe to ensure the new forecast data is captured if the Primary Daily Poll fails.

##### 🎯 Purpose and Use
This setting is essential for **driver reliability** and is used to:
* **Backup:** Act as a catch-all if the Primary Daily Poll failed due to a temporary network blip, an API delay, or if the Hubitat hub was briefly offline during the primary scheduled time.
* **Guarantee:** Ensure that regardless of temporary failures, the new daily forecast is captured later in the morning.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Time Selector** | Hour, Minute, AM/PM Pick List | 10:00 AM |

* **Example:** Setting this to **10:00 AM** means the driver will schedule its backup retrieval job four hours after the default Primary Poll.

##### 💡 Hints and Best Practices
* **Required Field:** This is a required field to maintain daily reliability.
* **API Impact:** This setting guarantees a **second API call per day** (at the specified time). Together, the Primary and Secondary polls ensure two dedicated API calls per 24 hours for the new forecast.
* **Spacing:** It is strongly recommended to set the Secondary Poll time **3 to 4 hours after** the Primary Poll time to allow enough time for transient issues to resolve.

#### 🛑 Max API Calls Per Day

> **Description:** Defines the maximum number of times the driver is permitted to communicate with the Google Pollen API within a 24-hour window (midnight to midnight UTC/Hub time). This limit applies to all scheduled and manual poll attempts.

##### 🎯 Purpose and Use
This setting is essential for proactive cost and quota management. It is designed to:
* **Prevent Overages:** Ensure you do not accidentally exceed your free-tier or paid quota with Google Cloud by enforcing a strict daily limit.
* **Disable Manual Polling:** Automatically disable the **Poll (Button)** once the limit is reached, regardless of whether a scheduled poll still exists.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Number Input** | 1 to 100+ (or as defined by API Quota) | 20 |

* **Note:** The minimum required value is 2 (for the Primary and Secondary Daily Polls to complete).

##### 💡 Hints and Best Practices
* **Optimization:** The default of 20 is suitable for initial testing. Once configuration and testing are complete, it is strongly recommended to **lower this value** (e.g., to 2 or 3) to minimize API usage and potential billing.
* **Daily Reset:** The call count automatically resets to zero at the beginning of the next 24-hour period.
* **Driver Feedback:** If you attempt to poll manually after the limit is exceeded, the device's **`apiStatus`** attribute will update to indicate that the daily maximum poll limit has been reached, preventing further attempts until the reset time.
* **API Impact:** Every execution of a Primary, Secondary, or Manual poll counts as one consumption against this limit.

#### 🗺️ Use Custom Coordinates

> **Description:** A toggle that determines the source of the geographical coordinates used for fetching the pollen forecast. When **Off**, the driver uses the location data pre-configured in your Hubitat Hub settings. When **On**, it allows for the entry of custom latitude and longitude values. **If switched Off, the coordinates immediately revert to the Hub's saved location data and will be used on the next scheduled poll or manual polling.**

##### 🎯 Purpose and Use
This setting is essential when the Hubitat Hub's physical location (Latitude and Longitude) is incorrect, or if you wish to monitor pollen data for a different location. It gives the user the ability to switch back and forth between the custom location and the Hub's default location with a single action.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Toggle Switch** | On / Off | Off (Use Hub Coordinates) |

When the toggle is **On**, the following three fields become visible for user input:

* **Location Name (Text Input):** A reference field for the user to label the custom location (e.g., "The Cottage," "Vacation Home," "Work"). *This field is optional and not required by the API.*
* **Custom Latitude (Decimal Input):** The north-south position of the custom location.
* **Custom Longitude (Decimal Input):** The east-west position of the custom location.

##### 💡 Hints and Best Practices
* **Mandatory Refresh:** After changing this toggle's state or entering new coordinates, you **must close and reopen the device driver page** in the Hubitat interface to display or hide the custom coordinate fields correctly.
* **Validation:** While the input fields will accept any numerical data, the Google Pollen API will only return data if it can resolve the provided latitude and longitude. Using non-relevant or highly inaccurate coordinates will result in errors appearing in the Hubitat Logs and the device's **`apiStatus`** attribute.
* **Multi-Location Setup:** To monitor pollen for two different places (e.g., your Home Hub location and a Cottage), you can install the driver twice, using the custom coordinate option in the second installation.
* **Polling:** Changing the coordinates does **not** automatically trigger a poll. After setting new custom coordinates, you must use the **Poll (Button)** to immediately fetch the new location's forecast data.

#### 🚫 Use Blockout Dates?

> **Description:** A toggle that allows the user to define an annual period during which the driver is prevented from making *any* calls to the Google Pollen API. This feature helps prevent unnecessary API usage and saves quota during the pollen off-season.

##### 🎯 Purpose and Use
When enabled, the driver temporarily stops all API interaction—this includes all scheduled polls (Primary and Secondary) and prevents manual polling via the **Poll (Button)**. All related Current States will update to reflect that the blockout period is in effect, providing clear user feedback.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Toggle Switch** | On / Off | Off |

When the toggle is **On**, the following two fields become visible for user input:

* **Blockout Start Date (Hubitat Date Picker):** The date when the driver should begin blocking API polls.
* **Blockout End Date (Hubitat Date Picker):** The date when the driver should resume normal polling operations.

##### 💡 Hints and Best Practices
* **Mandatory Refresh:** After changing this toggle's state or setting the dates, you **must close and reopen the device driver page** in the Hubitat interface to display or hide the date picker fields correctly.
* **Annual Roll-Forward:** This is an automated convenience feature. Once the driver successfully exits an active blockout period, it will automatically calculate and set the new **Blockout Start Date** and **Blockout End Date** one year into the future. This prepares the driver for the next off-season without further manual intervention.
* **Polling Resumption:** If the toggle is switched **Off** while a blockout is active, the driver will immediately use the Hub's scheduler to resume normal polling operations on the next scheduled time.
* **Quota Savings:** This feature is highly recommended for reducing API consumption when you know the monitored pollen are not relevant in your area (e.g., winter months).

#### 🔄 Auto-Reset Errors at Midnight

> **Description:** A toggle that, when enabled, automatically resets certain temporary error states and daily counters at the start of each new day (midnight, based on your Hubitat Hub's timezone).

##### 🎯 Purpose and Use
This setting is designed to ensure a **clean slate** for the driver every 24 hours. This is especially useful for:
* **Troubleshooting:** If the driver hits a temporary API error or exceeds the **Max API Calls Per Day** limit, this toggle ensures those status messages are cleared automatically at midnight.
* **Data Hygiene:** It prevents error states related to daily limits or transient issues from persisting into the next day, ensuring that the **`apiStatus`** attribute accurately reflects the current day's operational state.

##### 📌 Options and Values
| Field Type | Value Range | Default Value |
| :--- | :--- | :--- |
| **Toggle Switch** | On / Off | (Usually **Off** for initial stability; users should set this to **On**.) |

##### 💡 Hints and Best Practices
* **Recommended Setting:** It is highly recommended to set this toggle to **On**. This guarantees that daily-based constraints (like the API call limit) and transient error messages are refreshed, preventing them from interfering with the next day's scheduled polling.
* **Daily Reset:** The daily API call counter will reset regardless of this setting, but enabling this toggle ensures that the corresponding device attributes (`apiStatus`) are also cleared, allowing the driver to look operational at the start of the day.
* **Testing:** When testing the **Max API Calls Per Day** feature, keeping this toggle **On** allows you to reliably test the limit failure and then automatically reset for the next day's trial.

#### 🛡️ Advanced API Retry Settings

> **Description:** A set of advanced preferences that configure the driver's built-in error handling mechanism. This feature enables the driver to automatically retry API calls when a temporary connection or timeout failure occurs, using an **Exponential Backoff** strategy.

##### 🎯 Purpose and Use
These settings are generally only necessary for users who experience frequent, intermittent polling failures due to poor network stability, hub resource saturation, or transient API server load. **For most users, leaving the defaults will provide sufficient reliability.**

##### 📌 Options and Values (Grouped)
| Field Type | Value Range | Default Value | Description |
| :--- | :--- | :--- | :--- |
| **Toggle** | On / Off | Off | **Enable Advanced Retries:** Activates the retry logic. When set to **Off**, the driver reverts to its default, stable retry behavior, and the values below are ignored. |
| **Number** | 1 to 10 | 3 | **Max API Retry Attempts:** Maximum number of times the driver will attempt to re-send the API request before logging a final failure. |
| **Number (seconds)** | 1 to 60 | 5 | **Max Backoff Delay (seconds):** The largest time delay (in seconds) the driver will wait between retry attempts. |

* **Dependency Logic:** If the **Enable Advanced Retries** toggle is set to **Off**, the driver uses its default, stable retry logic. The values entered in **Max API Retry Attempts** and **Max Backoff Delay (seconds)** are preserved in the settings but are **not used** until the feature is re-enabled.

##### 💡 Hints and Best Practices
* **Use Case:** Only enable **Advanced Retries** if you are actively experiencing issues where the driver logs repeated connection errors or timeouts when trying to contact the Google API.
* **Default Sufficiency:** The driver's default behavior is designed for high reliability. If you do enable retries, the default attempts (3) and maximum backoff (5 seconds) are usually more than sufficient.
* **Exponential Backoff:** The driver uses an exponential backoff strategy, meaning the time delay between retries increases with each failed attempt (up to the defined **Max Backoff Delay**) to avoid overwhelming the API server.

#### 🌱 Pollen Type Group Selection

> **Description:** These three toggles control which specific metrics (Index, Level, or Health Tips) are reported for the various available pollen types (e.g., Alder, Birch, Grass). Enabling a toggle reveals a dynamic selection list below it, allowing the user to choose which specific pollen types to track.

##### 🎯 Purpose and Use
By default, the driver only reports high-level summary indices (`totalPollenIndex`, `treePollenIndex`, etc.). This group of preferences allows users to **filter and publish granular, individual plant data** as Current States, which is essential for users creating targeted automations or dashboard displays based on specific plant allergies.

##### 📌 Options and Values (Grouped)
| Preference Field | Field Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| **Show Index by Pollen Type** | Toggle | Off | Reports the numerical index value for selected plant types (e.g., `birchPollenIndex: 5`). |
| **Show Level by Pollen Type** | Toggle | Off | Reports the categorical level (e.g., Low, Moderate) for selected plant types (e.g., `birchPollenLevel: Moderate`). |
| **Show Health Tips by Pollen Type** | Toggle | Off | Reports the specific health recommendation for selected plant types (e.g., `birchHealthTip: Limit Outdoor Activity`). |

##### 💡 Hints and Best Practices
* **Dynamic Fields:** When you toggle **On** any of the "Show..." preferences, a new conditional field will appear immediately below it, titled "Select Pollen Types to Track".
* **Conditional Visibility:** If one Pollen Group (e.g., **Show Index**) is already **On** and exposing the plant type selection list, turning **On** another group (e.g., **Show Level**) will **not** expose a redundant selection list. The original list is used to determine which plants are tracked across *all* enabled metrics.
* **Current State Output Example:**
    * If you enable **Show Index by Pollen Type** and select **Birch**, the driver will publish a Current State like: `birchPollenIndex: 5`
    * If you also enable **Show Level by Pollen Type** and keep **Birch** selected, the driver will publish an additional state: `birchPollenLevel: Moderate`
* **Data Persistence and Removal:** Once a Current State is published (e.g., `birchPollenIndex`), it will remain on the device page. If you later **toggle Off** the corresponding group (e.g., **Show Index by Pollen Type**) or **de-select a plant type**, the driver will update the relevant Current State with a value like **`Inactive`** or **`Disabled`** to signal that the data is no longer being actively polled or displayed.
* **Saving Changes:** Remember that after changing the toggles or the plant type selections, you must click **Save Preferences** for the changes to take effect and for the driver to fetch the new data types on the next poll.

#### 🧬 Individual Pollen Species (Granular Data)

> **Description:** This section explains the nature of the specific plant data (e.g., Ragweed, Alder, Juniper) that becomes available for tracking when you enable one of the **🌱 Pollen Type Group Selection** toggles.

##### 🎯 Purpose and Use
Most public weather forecasts only provide a broad category (Tree, Grass, or Weed). This driver exposes the underlying, **granular data** from the Google API, which is essential for users with specific, diagnosed allergies.
* **Targeted Automation:** Instead of reacting to a high 'Tree' index (which could be anything), you can create specific rules, such as "If **Ragweed** pollen index is above 5, turn on the air purifier."
* **Precision Tracking:** Allows users to track species that are prevalent in their specific geographical area but may not be included in generic, national-level forecasts.

##### 📌 Granular Data Naming Convention
When you select a species in the preferences, the driver creates up to three corresponding Current States on your device page using a standardized naming structure:

| Data Type | Example Species | Resulting Attribute Name | Example Value |
| :--- | :--- | :--- | :--- |
| **Index** | `ragweed` | `ragweedPollenIndex` | `12` |
| **Level** | `alder` | `alderPollenLevel` | `Moderate` |
| **Health Tip** | `oak` | `oakHealthTip` | `Limit Outdoor Activity` |

##### 💡 Hints and Best Practices
* **Relevance is Key:** The list of available individual species is determined entirely by the Google API based on the **Geographic Location** you have set in your preferences. If a species is not relevant to your coordinates, the API will not provide data for it.
* **Enable Only What You Need:** Only select the specific species that you or members of your household are allergic to. This keeps your device Current States list clean and focused on actionable data.
* **Index vs. Level:** Remember to use the **Index** (number) for precision in rules and the **Level** (word) for user-friendly notifications and dashboards.

---

## 🏷️ Current States (Device Attributes)

**Description:**  
This section details all the data attributes (Current States) that the Google Pollen Forecaster driver reports to Hubitat. These values are updated after every successful poll and are the core data points used for dashboard tiles, rules, and custom automations.

### 🔬 Summary Pollen Data

These attributes represent the overall forecast for the three main biological categories (Tree, Grass, Weed) and the combined total. These are reported regardless of the 🌱 *Pollen Type Group Selection* preferences.

| **Attribute Name**   | **Data Type** | **Value Range**       | **Description** |
|----------------------|---------------|------------------------|-----------------|
| `totalPollenIndex`   | Number        | 0 – 100+               | The combined numerical score of all pollen types in the forecast. |
| `totalPollenLevel`   | String        | Low, Moderate, High…   | The categorical severity of the combined total pollen index. |
| `treePollenIndex`    | Number        | 0 – 100+               | The numerical score for all tree pollen types. |
| `treePollenLevel`    | String        | Low, Moderate, High…   | The categorical severity of the tree pollen index. |
| `grassPollenIndex`   | Number        | 0 – 100+               | The numerical score for all grass pollen types. |
| `grassPollenLevel`   | String        | Low, Moderate, High…   | The categorical severity of the grass pollen index. |
| `weedPollenIndex`    | Number        | 0 – 100+               | The numerical score for all weed pollen types. |
| `weedPollenLevel`    | String        | Low, Moderate, High…   | The categorical severity of the weed pollen index. |

### 📊 Driver Status and Metadata

These attributes provide essential information regarding the driver's operation, scheduling, and API usage.

| **Attribute Name**    | **Data Type** | **Value Range**                     | **Description** |
|-----------------------|---------------|--------------------------------------|-----------------|
| `driverVersion`       | String        | e.g., `2.5.37 - Alpha`               | The current version of the Groovy driver code. |
| `apiStatus`           | String        | OK, Error, Blockout, Limit Reached   | The last reported operational status of the driver's API interaction. |
| `lastPoll`            | String        | Custom Date/Time                     | The exact date and time of the last successful data retrieval. |
| `nextPoll`            | String        | Custom Date/Time                     | The scheduled date and time for the next automatic poll attempt. |
| `dailyApiCallCount`   | Number        | 0 to Max API Calls                   | The number of API calls made since the last midnight reset. |
| `latitude`            | String        | Decimal Number                       | The latitude used for the API request (Hub or Custom Coordinates). |
| `longitude`           | String        | Decimal Number                       | The longitude used for the API request (Hub or Custom Coordinates). |
| `locationName`        | String        | User-defined label                   | The friendly name assigned to the current location. |

### 🌿 Granular Pollen Data (Conditional)

These attributes are only published if you enable the corresponding toggles in the 🌱 *Pollen Type Group Selection* preferences. The attributes reported will depend entirely on which specific species (e.g., *ragweed*, *birch*) you have selected.

| **Attribute Name Pattern**   | **Data Type**        | **Value Range**                | **Description** |
|------------------------------|-----------------------|--------------------------------|-----------------|
| `[species]PollenIndex`       | Number / String       | 0 – 100+ / Inactive            | The numerical index for a specific species (e.g., `birchPollenIndex`). |
| `[species]PollenLevel`       | String                | Low, Moderate, High, Inactive  | The categorical level for that species (e.g., `ragweedPollenLevel`). |
| `[species]HealthTip`         | String                | Text / Inactive                | The health recommendation for that species (e.g., `oakHealthTip`). |

**Inactive State:**  
If a species attribute was previously reported but is now deselected, its value will change to **Inactive** or **Disabled** to indicate it is no longer being tracked.

---

## 📝 Summary

This document gives you a clear, step-by-step guide to installing, setting up, and configuring the **Google Pollen Forecaster** driver on Hubitat. It covers adding the driver, entering your API key, choosing location options, and adjusting features like polling schedules, blockout periods, quota protection, and species-level data.

You’ll learn how the polling system works, how status updates are reported, and how to use the device attributes in dashboards and automations. The guide also includes troubleshooting tips, best practices, and a full reference of all settings and reported data.

Together, these sections provide everything needed to install the driver confidently and tailor it to your environment.

---

## 🛡️ Disclaimers

⚠️ Important: Always backup your Hubitat setup and test automation changes incrementally. This repository is intended for educational and experimental purposes only. Professional judgment is required when deploying in your home.

**Provided As-Is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup. Use at your own risk.

**User Responsibility:** You assume full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.

**Google API Usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Ensure compliance and monitor your usage.

**Community-Developed / Non-Affiliation:** This repository is independently developed and maintained. It is not affiliated with Google LLC, Hubitat Inc., or any other company mentioned.

**Information Accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.

**AI-Assisted Development:** All drivers were developed with AI assistance. While code has been tested, AI-generated outputs may include quirks, non-standard patterns, or unexpected behavior.

**External Dependencies:** Any third-party libraries, dashboards, or tools referenced are the responsibility of the respective developers. Verify compatibility before integrating.

---

## 🔗 Reference / External Links

📘 [Google Pollen API Documentation](https://developers.google.com/pollen)  
📘 [Google Pollen API Supported Countries & Plants](https://www.google.com/search?q=Google+pollen+API+supported+countries+plants)

---

## 📜 Revision History
| Version | Date       | Author | Changes                                    |
|---------|------------|--------|--------------------------------------------|
| 0.11    | 2025-12-10 | DBQ    | Concept Changes, Formating, adding content |
| 0.1     | 2025-12-08 | DBQ    | Draft of Document                          |
