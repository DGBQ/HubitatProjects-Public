# 🌿 Getting Your Google API Key - Google Pollen Forecaster
### _for the  driver for Hubitat_

## 🔢 Version
**Document Version:** 1.0.1  
**Date:** 2025-11-12 
**Managing Author:** David Ball-Quenneville  
**Associate Author:** ChatGPT  

---

## 📑 Table of Contents
<details> <summary>Click to expand</summary> 
- [🧭 Overview](#%F0%9F%A7%AD-overview)
- [🔑 Why You Need an API Key](#%F0%9F%94%91-why-you-need-an-api-key)
- [🛠️ Step-by-Step Instructions](#%F0%9F%9B%A0-step-by-step-instructions)
  - [🪜 Step 1 – Sign Up for a Google Cloud Account](#%F0%9F%AA%9C-step-1--sign-up-for-a-google-cloud-account)
  - [🪜 Step 2 – Create or Select a Project](#%F0%9F%AA%9C-step-2--create-or-select-a-project)
  - [🪜 Step 3 – Set Up Billing (Mandatory)](#%F0%9F%AA%9C-step-3--set-up-billing-mandatory)
  - [🪜 Step 4 – Enable the Pollen API](#%F0%9F%AA%9C-step-4--enable-the-pollen-api)
  - [🪜 Step 5 – Create an API Key](#%F0%9F%AA%9C-step-5--create-an-api-key)
  - [🪜 Step 6 – Restrict the API Key](#%F0%9F%AA%9C-step-6--restrict-the-api-key)
  - [🪜 Step 7 – Quotas and Limits (Cost Control)](#%F0%9F%AA%9C-step-7--quotas-and-limits-cost-control)
- [💡 Tips for Safe Usage](#%F0%9F%92%A1-tips-for-safe-usage)
- [🔍 Troubleshooting](#%F0%9F%94%8D-troubleshooting)
- [📝 Summary](#%F0%9F%93%9D-summary)
- [🔗 References / External Links](#%F0%9F%94%97-references--external-links)
- [🛡️ Disclaimers](#%F0%9F%9B%A1%EF%B8%8F-disclaimers)
- [📜 Revision History](#%F0%9F%93%9C-revision-history)
</details> 

---

## 🧭 Overview
The Google Pollen Forecaster Driver requires a Google Pollen API key to fetch pollen data, such as `ragweedPollenLevel` and health recommendations, for your Hubitat hub. This beginner-friendly guide walks you through obtaining an API key from the Google Cloud Console (GCC), setting up billing (required even for the free tier), and securing the key with usage limits. The process takes 10–15 minutes for new users, with clear steps to avoid errors like **"Billing not linked"** or **"API not enabled."**

>🟡 **Important:** A billing account is required, but typical use (2 API calls/day) stays well within Google’s free tier, ensuring zero charges with proper quotas.

Next Steps: After obtaining your API key, follow the **Installation Guide** to install the driver and the **Setup & Configuration Guide** to configure it.

---

## 🔑 Why You Need an API Key
The API key authenticates your Hubitat driver to Google’s servers, enabling secure access to pollen forecasts. Google requires billing for all APIs, but the free tier (10,000 requests/month) covers typical usage (~60 requests/month for 2 calls/day).

- **Purpose:** Unlocks pollen data for automations, dashboards, and health insights.  
- **Free Tier:** 10,000 requests/month (Essentials SKU), sufficient for daily use.  
- **Cost:** Zero for typical use; pay-as-you-go beyond free tier with volume discounts.  
💡**Tip:** Set quotas and alerts to stay within the free tier, as described in **Step 7 – Quotas and Limits**.

---

## 🛠️ Step-by-Step Instructions
Follow these steps if you are starting fresh. Each step is broken into clear, detailed instructions to minimize mistakes and ensure your API key is created, secured, and correctly configured. You can expand or collapse each step to skip sections you already completed, such as having an existing Google account or project. Verify each step before proceeding to avoid errors like **billing not linked**, **API not enabled**, or incorrect restrictions.

> **⚠️ Important Notice**  
This guide is based on the information and interface provided by Google at the time of writing. Google may update features, menus, terminology, or required steps at any time. Users should consult the official documentation for the latest guidance: [References / External Links](#%F0%9F%94%97-references--external-links).


### 🪜 Step 1 – Sign Up for a Google Cloud Account
<details> <summary>Click to expand the section</summary> 

**Purpose:** To access the Google Cloud Console (GCC), you must have a valid Google Account, which will be associated with all your projects and billing.

- If you already have a Google account: Log in and skip to **Step 2 – Create or Select a Project**.

**Instructions:**

1. Navigate to Google Cloud Console.  
2. Click **Sign in** (top right) and log in with your Google account. If needed, click **Create account**.  
3. Agree to the terms of service if prompted.  
4. Verify you see the GCC Dashboard with a project selector dropdown.

</details>

### 🪜 Step 2 – Create or Select a Project
<details> <summary>Click to expand the section</summary> 

**Purpose:** A Project acts as the required container for your Pollen API key, its security restrictions, and its quota limits. It isolates your resources for better organization.

- If you already have a project: Select it from the dropdown and skip to **Step 3 – Set Up Billing**.

**Instructions:**

1. Click the project dropdown in the top bar.  
2. Click **NEW PROJECT**.  
3. Enter a Project name (e.g., Pollen Forecaster Hubitat).  
4. Click **CREATE** and wait 10–30 seconds.  
5. Select the new project from the dropdown.  
6. Verify your project name appears prominently in the top bar.

</details>

### 🪜 Step 3 – Set Up Billing (Mandatory)
<details> <summary>Click to expand the section</summary> 

**Purpose:** Billing must be linked to a project because the Pollen API is a pay-as-you-go service. This is required to activate the API, even if your usage falls within the free monthly tier.

- If you already have a billing account: Link it to your project and skip to **Step 4 – Enable the Pollen API**.

**Instructions:**

1. Navigate to the left menu → **Billing**.  
2. Click **Create billing account** or **Link a billing account**. Provide payment details as prompted.  
3. Link the billing account to your selected project.  
4. Verify under Billing → Overview that the status shows **“Billing account linked.”**

</details>

### 🪜 Step 4 – Enable the Pollen API
<details> <summary>Click to expand the section</summary> 

**Purpose:** You must explicitly enable the API service for your project to allow it to receive requests.

- If already enabled: Skip to **Step 5 – Create an API Key**.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Library**.  
2. Use the search bar for **Pollen API**.  
3. Click the **Google Maps Pollen API** result.  
4. Click the large blue **ENABLE** button.  
5. Verify the button changes from ENABLE to **MANAGE**.

</details>

### 🪜 Step 5 – Create an API Key
<details> <summary>Click to expand the section</summary> 

**Purpose:** The API key is the unique token used by your Hubitat driver to authenticate every request to Google's servers.

- If you already have a key: Copy it from Credentials and skip to **Step 6 – Restrict the API Key**.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Credentials**.  
2. Click **+ Create Credentials → API Key**.  
3. Copy the key immediately; this is the only time the full key is displayed.  
4. Click **RESTRICT KEY** to proceed to Step 6.

</details>

### 🪜 Step 6 – Restrict the API Key
<details> <summary>Click to expand the section</summary> 

**Purpose:** Restricting the key is the most important security measure. It ensures the key can only be used for the Pollen API and blocks fraudulent charges if the key is accidentally exposed.

- If already restricted: Ensure Pollen API is included and skip to **Step 7 – Quotas and Limits**.

**Instructions:**

1. Navigate to **APIs & Services → Credentials**. Click your API key name.  
2. Under **API restrictions**, select **Restrict key**.  
3. In the **Select APIs** dropdown, select **Google Maps Pollen API**.  
4. Click **SAVE**.  
5. Verify Key restrictions shows **“1 API restriction.”**

</details>

### 🪜 Step 7 – Quotas and Limits (Cost Control)
<details> <summary>Click to expand the section</summary> 

**Purpose:** Setting a quota limit acts as a hard cap on your spending. If you exceed the set limit, Google will return an error instead of charging you for unexpected usage.

**Instructions:**

1. Navigate to left menu → **APIs & Services → Quotas**.  
2. Use the filter/search bar to find **Pollen API**.  
3. Locate "Requests per day" metric and click the pencil icon (✏️) to edit.  
4. Enter a limit well above your expected daily usage, but below the monthly free tier (e.g., 50 requests).  
5. Click **SAVE (or SUBMIT REQUEST)**.  
6. Verify the Quotas page shows your new custom limit.  

- **Tip:** The driver’s default 2 calls/day (~60/month) is well below the free tier.

</details>

---

## 💡 Tips for Safe Usage
- Monitor usage in **APIs & Services → Dashboard**.  
- Restrict the key to Pollen API only (Step 6).  
- Check Pollen API availability at **Pollen API Coverage**.  
- Set Max API Calls Per Day to 2 in **Setup & Configuration Guide** to conserve quota.

---

## 🔍 Troubleshooting
- **API Not Found:** Ensure billing is linked (Step 3).  
- **Billing Errors:** Verify payment in Billing → Payment methods.  
- **Key Not Working:** Unrestrict temporarily, test, then re-restrict.  
- **Quota Exceeded:** Check Quotas and request an increase if needed.  
- **Hubitat Errors:** See logs in Hubitat and refer to Troubleshooting & Verification Guide.

---

## 📝 Summary
This guide provides complete, step-by-step instructions for obtaining and securing a Google Pollen API key for the Hubitat driver. By following Steps 1–7, users create an account, set up a project, link billing, enable the API, create a key, apply restrictions, and configure quotas to prevent unexpected charges. Following these steps ensures safe, secure, and efficient API usage within Google’s free tier.

---

## 🔗 References / External Links

**Google Cloud General Setup**  
📘 [Get started with Google Cloud](https://docs.cloud.google.com/docs/get-started)  
📘 [Create a Google Cloud project](https://developers.google.com/workspace/guides/create-project)  

**Setup Billing**  
📘 [Cloud Billing documentation](https://cloud.google.com/billing/docs)  
📘 [APIs and billing](https://support.google.com/googleapi/answer/6158867?hl=en)  

**API Keys and Quotas**  
📘 [Manage API keys](https://docs.cloud.google.com/docs/authentication/api-keys)  
📘 [API Keys Overview](https://docs.cloud.google.com/api-keys/docs/overview)  
📘 [View and manage quotas](https://docs.cloud.google.com/docs/quotas/view-manage)  

**Official Google Maps Platform Pricing Documentation**  
📘 Google Maps Platform Pricing Changes Document  

---

## 🛡️ Disclaimers
- **Provided as-is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup. Use at your own risk.
- **User responsibility:** You assume full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.  
- **Google API usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Ensure compliance and monitor your usage accordingly.  
- **Community-developed / Non-affiliation:** This repository is independently developed and maintained. It is not affiliated with Google LLC, Hubitat Inc., or any other company mentioned.  
- **Information accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.  
- **AI-assisted development:** All drivers were developed with the assistance of AI to my specifications. While I have tested and refined the code, AI-assisted outputs may include quirks, non-standard patterns, or unexpected behavior. Use caution and review thoroughly before deploying in critical systems.  
- **External dependencies:** Any third-party libraries, dashboards, or tools referenced in this repository are the responsibility of the respective developers. Users should ensure compatibility and review documentation before integrating.  

⚠️ **Important:** Always backup your Hubitat setup and test automation changes incrementally. This repository is intended for educational and experimental purposes; professional judgment is required when deploying in your home.

---

## 📜 Revision History
| Date | Author | Version | Changes |
|------|--------|--------|---------|
| 2025-11-12 | DBQ | 1.2.1 | Added references, updated formatting, expanded instructions, improved clarity and step descriptions. |
| 2025-10-12 | DBQ | 1.0.0 | Initial guide with basic API key setup steps. |
