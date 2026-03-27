# 🔑Getting Your Google API Key - Google Environment Drivers

### *for the Hubitat Elevation drivers*

## 🔢 Version

**Document Version:** 1.30
**Date:** 2026-03-27
**Author:** David Ball-Quenneville

---

## 📑 Table of Contents

<details>
<summary>Click to expand</summary>

- [🧭 Overview](#overview)
- [🔑 Why You Need an API Key](#why-you-need-an-api-key)
- [🛠️ Step-by-Step Instructions](#step-by-step-instructions)
  - [🪜 Step 1 – Sign Up for a Google Cloud Account](#step-1--sign-up-for-a-google-cloud-account)
  - [🪜 Step 2 – Create or Select a Project](#step-2--create-or-select-a-project)
  - [🪜 Step 3 – Set Up Billing (Mandatory)](#step-3--set-up-billing-mandatory)
  - [🪜 Step 4 – Enable the Environment APIs (Weather, Pollen, Air Quality)](#step-4--enable-the-environment-apis)
  - [🪜 Step 5 – Create an API Key](#step-5--create-an-api-key)
  - [🪜 Step 5.5 – Naming the Google Auth Platform](#step-5-5--naming-the-google-auth-platform)
  - [🪜 Step 6 – Restrict the API Key](#step-6--restrict-the-api-key)
  - [🪜 Step 7 – Quotas and Limits (Cost Control)](#step-7--quotas-and-limits-cost-control)
- [💡 Tips for Safe Usage](#tips-for-safe-usage)
- [🔍 Troubleshooting](#troubleshooting)
- [📝 Summary](#summary)
- [🔗 References / External Links](#references--external-links)
- [🛡️ Disclaimers](#-disclaimers)
- [📜 Revision History](#-revision-history)

</details>

---

<h2 id="overview">🧭 Overview</h2>

The Google Environment Drivers require a Google API key to fetch local data, such as `ragweedPollenLevel`, `currentTemperature`, and `airQualityIndex` for your Hubitat hub. This beginner-friendly guide walks you through obtaining an API key from the Google Cloud Console (GCC), setting up billing (required even for the free tier), and securing the key with usage limits. The process takes 10–15 minutes for new users, with clear steps to avoid errors like **"Billing not linked"** or **"API not enabled."**

> 🟡 **Important:** A billing account is required, but typical use (polling every 15-30 minutes) stays well within Google’s free tier, ensuring zero charges with proper quotas.

Next Steps: After obtaining your API key, follow the **Installation Guide** to install the driver and the **Setup & Configuration Guide** to configure it.

---

<h2 id="why-you-need-an-api-key">🔑 Why You Need an API Key</h2>

The API key authenticates your Hubitat driver to Google’s servers, enabling secure access to environmental forecasts. Google requires billing for all APIs, but the free tier (e.g., 10,000 requests/month for Pollen Essentials) covers typical usage.

- **Purpose:** Unlocks weather, pollen, and air quality data for automations, dashboards, and health insights.
- **Free Tier:** Sufficient for residential polling intervals (15+ minutes).
- **Cost:** Zero for typical use; pay-as-you-go beyond free tier with volume discounts.  
💡**Tip:** Set quotas and alerts to stay within the free tier, as described in **Step 7 – Quotas and Limits**.

---

<h2 id="step-by-step-instructions">🛠️ Step-by-Step Instructions</h2>

Follow these steps if you are starting fresh. Each step is broken into clear, detailed instructions to minimize mistakes and ensure your API key is created, secured, and correctly configured. You can expand or collapse each step to skip sections you already completed, such as having an existing Google account or project. Verify each step before proceeding to avoid errors like **billing not linked**, **API not enabled**, or incorrect restrictions.

> **⚠️ Important Notice**  
This guide is based on the information and interface provided by Google at the time of writing. Google may update features, menus, terminology, or required steps at any time. Users should consult the official documentation for the latest guidance: [References / External Links](#references--external-links).

<h3 id="step-1-sign-up-for-a-google-cloud-account">🪜 Step 1 – Sign Up for a Google Cloud Account</h3>

**Purpose:** To access the Google Cloud Console (GCC), you must have a valid Google Account, which will be associated with all your projects and billing.

- If you already have a Google account: Log in and skip to **Step 2 – Create or Select a Project**.

**Instructions:**

1. Navigate to Google Cloud Console.  
2. Click **Sign in** (top right) and log in with your Google account. If needed, click **Create account**.  
3. Agree to the terms of service if prompted.  
4. Verify you see the GCC Dashboard with a project selector dropdown.

<h3 id="step-2-create-or-select-a-project">🪜 Step 2 – Create or Select a Project</h3>

**Purpose:** A Project acts as the required container for your API key, its security restrictions, and its quota limits. It isolates your resources for better organization.

- If you already have a project: Select it from the dropdown and skip to **Step 3 – Set Up Billing**.

**Instructions:**

1. Click the project dropdown in the top bar.  
2. Click **NEW PROJECT**.  
3. Enter a Project name (e.g., Hubitat Environmental Drivers).  
4. **⚠️ Critical Note on Project ID:** Below the name field, you will see a "Project ID" (e.g., `pollen-tracker-plus`). **This ID is permanent and cannot be changed after creation.** If you wish to customize it, click **EDIT** now before clicking Create.  
5. Click **CREATE** and wait 10–30 seconds.  
6. Select the new project from the dropdown.  
7. Verify your project name appears prominently in the top bar.

<h3 id="step-3-set-up-billing-mandatory">🪜 Step 3 – Set Up Billing (Mandatory)</h3>

**Purpose:** Billing must be linked to a project because environmental APIs are pay-as-you-go services. This is required to activate the API, even if your usage falls within the free monthly tier.

- If you already have a billing account: Link it to your project and skip to **Step 4 – Enable the Environment APIs**.

**Instructions:**

1. Navigate to the left menu → **Billing**.  
2. Click **Create billing account** or **Link a billing account**. Provide payment details as prompted.  
3. Link the billing account to your selected project.  
4. Verify under Billing → Overview that the status shows **“Billing account linked.”**

<h3 id="step-4-enable-the-environment-apis">🪜 Step 4 – Enable the Environment APIs</h3>

**Purpose:** You must explicitly enable the API services for your project to allow it to receive requests for Weather, Pollen, and Air Quality.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Library**.  
2. Search for and **ENABLE** each of the following:
   - **Pollen API**
   - **Air Quality API**
   - **Weather API** (Google Maps Platform)
3. For each, verify the button changes from ENABLE to **MANAGE**.

<h3 id="step-5-create-an-api-key">🪜 Step 5 – Create an API Key</h3>

**Purpose:** The API key is the unique token used by your Hubitat driver to authenticate every request to Google's servers.

- If you already have a key: Copy it from Credentials and skip to **Step 6 – Restrict the API Key**.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Credentials**.  
2. Click **+ Create Credentials → API Key**.  
3. Copy the key immediately; this is the only time the full key is displayed.  
4. Click **RESTRICT KEY** to proceed to Step 6.

<h3 id="step-5-5-naming-the-google-auth-platform">🪜 Step 5.5 – Naming the Google Auth Platform</h3>

**Purpose:** To clear system warnings and professionalize your account, you may need to provide a simple "App Name" in the Google Auth Platform.

**Instructions:**

1. If you see a warning stating **"Google auth platform not configured yet,"** click **Get Started**.  
2. **App Name:** Enter a clean name like **"Hubitat Environment Services"**.  
3. **User Support Email:** Select your email from the dropdown.  
4. Click **Save** or **Create**. (Note: You do NOT need to add logos or authorized domains for private Hubitat use).

<h3 id="step-6-restrict-the-api-key">🪜 Step 6 – Restrict the API Key</h3>

**Purpose:** Restricting the key is the most important security measure. It ensures the key can only be used for your specified APIs and blocks fraudulent charges if the key is accidentally exposed.

- If already restricted: Ensure all three APIs are included and skip to **Step 7 – Quotas and Limits**.

**Instructions:**

1. Navigate to **APIs & Services → Credentials**. Click your API key name.  
2. Under **API restrictions**, select **Restrict key**.  
3. In the **Select APIs** dropdown, select:
   - **Google Maps Pollen API**
   - **Air Quality API**
   - **Weather API**
4. Click **SAVE**.  
5. Verify Key restrictions shows **“3 API restrictions.”**

<h3 id="step-7-quotas-and-limits-cost-control">🪜 Step 7 – Quotas and Limits (Cost Control)</h3>

**Purpose:** Setting a quota limit acts as a hard cap on your spending. If you exceed the set limit, Google will return an error instead of charging you for unexpected usage.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Quotas**.  
2. Use the filter/search bar to find the specific API (e.g., Weather API).  
3. Locate **"Requests per day"** or **"Read requests"** and click the pencil icon (✏️) to edit.  
4. Enter a limit well above your expected daily usage, but below the monthly free tier (e.g., 100 requests per day).  
5. Click **SAVE (or SUBMIT REQUEST)**.  
6. Verify the Quotas page shows your new custom limit.  

- **Tip:** Polling every 15 minutes equals 96 calls/day, which is a safe residential setting.

---

<h2 id="tips-for-safe-usage">💡 Tips for Safe Usage</h2>

- Monitor usage in **APIs & Services → Dashboard**.  
- Restrict the key to specific APIs only (Step 6).  
- Rename your **Billing Account** and **API Key** in their respective settings to avoid confusion.  
- Set your Hubitat polling interval to 15 or 30 minutes to stay safely within the free tier.

---

<h2 id="troubleshooting">🔍 Troubleshooting</h2>

- **API Not Found:** Ensure billing is linked (Step 3).  
- **Billing Errors:** Verify payment in Billing → Payment methods.  
- **Key Not Working:** Check that the specific API (Weather vs Pollen) is checked in Step 6.  
- **Quota Exceeded:** Check Quotas and ensure you aren't polling every 30 seconds by mistake.  
- **Hubitat Errors:** See logs in Hubitat and refer to Troubleshooting & Verification Guide.

---

<h2 id="summary">📝 Summary</h2>

This guide provides complete, step-by-step instructions for obtaining and securing a Google API key for Hubitat environmental drivers. By following Steps 1–7, users create an account, set up a project, link billing, enable the APIs, create a key, apply restrictions, and configure quotas to prevent unexpected charges. Following these steps ensures safe, secure, and efficient API usage within Google’s free tier.

---

<h2 id="references--external-links">🔗 References / External Links</h2>

**Google Cloud General Setup**  
📘 [Get started with Google Cloud](https://docs.cloud.google.com/docs/get-started)  
📘 [Create a Google Cloud project](https://developers.google.com/workspace/guides/create-project)  

**Setup Billing**  
📘 [Cloud Billing documentation](https://cloud.google.com/billing/docs)  
📘 [APIs and billing](https://support.google.com/googleapi/answer/6158867?hl=en-GB)  

**API Keys and Quotas**  
📘 [Manage API keys](https://docs.cloud.google.com/docs/authentication/api-keys)  
📘 [API Keys Overview](https://docs.cloud.google.com/api-keys/docs/overview)  
📘 [View and manage quotas](https://docs.cloud.google.com/docs/quotas/view-manage)  

**Official Google Maps Platform Pricing Documentation**  
📘 Google Maps Platform Pricing Changes Document  

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

<h2 id="revision-history">📜 Revision History</h2>

| Version | Date       | Author | Changes                                                                                              |
|---------|------------|--------|------------------------------------------------------------------------------------------------------|
| **1.30**| **2026-03-27** | **DBQ** | **Expanded for Weather/Air Quality, added Project ID permanence warning, and Auth Platform naming.** |
| 1.00    | 2025-12-15 | DBQ    | Initial release of: Getting Your Google API Key - Google Pollen Forecaster                           |
| 0.20    | 2025-12-15 | DBQ    | ToC links fixed and Content Updates                                                                  |
| 0.10    | 2025-11-12 | DBQ    | Added references, updated formatting, expanded instructions, improved clarity and step descriptions. |