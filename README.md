# My Hubitat Projects - Public Repository 

## 🔢 Version
- **Document Version:** 1.0.4
- **Date:** 2026-02-08  
- **Author:** David Ball-Quenneville  

---

## 📑Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- [🧭 Overview](#-overview)
- [🗂️ Repository Contents](#-repository-contents)
   - [🛠️ Current Drivers](#-current-drivers)
   - [🔮 Future Projects](#-future-projects)
- [🤝 Contribution & Support](#-contribution--support)
  - [🐞 Reporting Bugs](#-reporting-bugs)
  - [✨ Contributing](#-contributing)
- [⚖️ License & Versioning](#-license--versioning)
- 🔗 [References / External Links](#-references--external-links)
- [🛡️ Disclaimers](#-disclaimers)
- [📝 Revision History](#-revision-history)

</details>

---

\<h2 id="overview">🧭 Overview\</h2>

Welcome to **DBQ’s HubitatProjects-Public** repository, a curated collection of Hubitat drivers  and possible future apps, maintained by a semi-retired software implementation project manager and smart home enthusiast. These projects are practical, approachable, and designed to be helpful for anyone looking to expand their Hubitat experience. They also reflect a hands-on, iterative learning process.  

All projects started as personal experiments using **Artificial intelligence (AI) assisted development**. Often, these experiments were conducted under the watchful eye of my cat, **Ollie**, who might stare at my screen for a few minutes before dramatically flopping onto my desk asleep. Over time, they evolved into functional, reliable solutions serving our household needs. While not “enterprise-grade,” they are tested, refined, and ready to use.  

The code in this repository was built using AI, following my specifications. This approach allowed me to explore **prompt engineering, iterative testing, and practical development** while learning the *do's and don’ts* of Hubitat automation and AI-driven development. Though I am not a professional developer, I’ve had the pleasure of collaborating with many skilled developers, and these projects reflect the lessons learned along the way. That said, an experienced Groovy developer may find the code choices and structure a bit irregular — I expect that, given my team member names were **Deep Seek, ChatGPT, Gemini, Copilot and Gork**.  

Currently, the repository includes tools like **Google Pollen Forecaster** and may expand in the future to **Google Air Quality Tracker** and **Google Weather Forecaster**, addressing localization gaps found in other drivers. All projects include full documentation, configuration guides, and usage examples — so you can explore, experiment, and hopefully enjoy the trials and tribulations of AI-driven development alongside me.

---

\<h2 id="repository-contents">🗂️ Repository Contents\</h2>

This repository hosts a collection of **Hubitat drivers** and possible future Hubitat Applications. Each project includes installation guides, configuration instructions, and technical references.  

---

\<h3 id="current-drivers">🪛 Current Drivers\</h3>

\<h4 id="google-pollen-forecaster">🌿Google Pollen Forecaster\</h4>
- **Folder:** `drivers/GooglePollenForecaster/`  
- **Purpose:** Provides daily pollen forecasts.  
- **Files included:**  
  - `googlePollenForecastor.groovy` — driver code  
  - `README.md` — project-specific documentation  
  - `CONTRIBUTING.md` — instructions for contributing  
  - `CHANGELOG.md` — version history  
  - `documentation/` — installation guide, configuration guide, technical reference  

  ---

\<h3 id="future-projects">🔮 Future Projects\</h3>

- **Google Air Quality Tracker** — localized air quality information  
- **Google Weather Forecaster** — improved weather automation  
- Each future project will follow the same folder structure and include full documentation.

📝 Note: Installation, configuration, and usage instructions are provided in each project’s README and related documentation. Please refer to the documentation within the specific driver or app folder for complete setup guidance.

---

\<h2 id="contribution-support">🤝 Contribution & Support\</h2>

I welcome contributions, suggestions, and bug reports for all projects in this repository. Since each driver or app may have unique setup or behavior, please refer to the specific project README for detailed instructions.  

---

\<h3 id="reporting-bugs">🐞 Reporting Bugs\</h3>

For issues and bugs, please use the [GitHub Issues](https://github.com/your-repo/google-pollen-forecaster/issues) page rather than the Hubitat Community forums. This ensures issues are tracked, discussed, and resolved in an organized manner.

- Include Hubitat firmware version, driver version (if available), and exact steps to reproduce.  
- Attach relevant logs and screenshots. Mask any sensitive data.  
- Search existing issues first to avoid duplicates.

💬 Kindly avoid posting bug reports directly in the Hubitat Community thread — it helps keep discussions organized and ensures your report is properly tracked and resolved.  

---

\<h3 id="contributing">✨ Contributing\</h3>

- Fork a repository and experiment freely — this is a great way to learn and explore.  
- Pull requests are welcome if you have improvements or enhancements.  
- Follow Hubitat conventions and include clear, well-commented code.  
- Review the 📄 Contributing Guide for process and style rules.  

🟡Please note that response times for reviews or merges may vary due to personal   and professional commitments, but all thoughtful contributions will be read and appreciated. And yes, Ollie may supervise your work too—he gives it a solid 3 out of 5 dramatic flops.

💡 Tip: Respecting attribution helps keep open-source projects like this thriving, and it’s always fun to see how others experiment and build upon these drivers.

---

\<h2 id="license-versioning-license-versioning">⚖️ License & Versioning License & Versioning\</h2>

- **License:** All projects in this repository are shared under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), which allows you to freely use, modify, and distribute the code while providing proper attribution.  
- **Versioning:** Each driver or app maintains its own `CHANGELOG.md` for version history. Please refer to the project-specific changelog for detailed release notes and updates.

---

\<h2 id="references-/-external-links">🔗 References / External Links\</h2>

- [Hubitat Community Forum](https://community.hubitat.com/) — support, discussions, and tips from the community.  
- [HD+ Android Dashboard](https://community.hubitat.com/t/release-hd-android-dashboard/41674) — dashboard used with some projects.  
- [Tile Builder](https://community.hubitat.com/t/release-tile-builder-build-beautiful-dashboards/118822) — build custom tiles for your dashboards.  
- [WebCoRE for Hubitat](https://community.hubitat.com/t/webcore-2-0-released/210) — the rules engine I personally use for automation.  
- [Rule Machine for Hubitat](https://docs.hubitat.com/index.php?title=Rule_Machine) — powerful automation engine.  
  > ⚠️ Disclaimer: I haven’t personally used Rule Machine much. Please reach out to
  > the Hubitat Community for guidance if needed.  
- Google APIs:  
  - [Google Pollen API](#) — used for pollen forecasts.  
  - [Google Air Quality API](#) — future expansion.  
  - [Google Weather API](#) — future expansion.

  ---

\<h2 id="disclaimers">🛡️ Disclaimers\</h2>

- **Provided as-is:** All drivers and apps in this repository are provided as-is, without any warranty or guarantee of suitability for your particular setup. Use at your own risk.  
- **User responsibility:** You assume full responsibility for any automation decisions, device actions, or outcomes resulting from the use of these drivers or apps. Always test in a safe environment before full deployment.  
- **Google API usage:** Access to Google APIs (Pollen, Air Quality, Weather) is subject to Google’s terms of service, quotas, and billing requirements. Ensure compliance and monitor your usage accordingly.  
- **Community-developed / Non-affiliation:** This repository is independently developed and maintained. It is **not affiliated with Google LLC**, Hubitat Inc., or any other company mentioned.  
- **Information accuracy:** Development was performed using the best available knowledge and resources at the time. APIs, Hubitat functionality, or integration methods may change over time, potentially affecting functionality.  Please report any issues encountered.
- **AI-assisted development:** All drivers were developed with the assistance of AI to my specifications. While I have tested and refined the code, AI-assisted outputs may include quirks, non-standard patterns, or unexpected behavior. Use caution and review thoroughly before deploying in critical systems.  
- **External dependencies:** Any third-party libraries, dashboards, or tools referenced in this repository are the responsibility of the respective developers. Users should ensure compatibility and review documentation before integrating.  

⚠️ Important: Always backup your Hubitat setup and test automation changes  incrementally. This repository is intended for educational and experimental purposes; professional judgment is required when deploying in your home.

---

\<h2 id="revision-history">📝 Revision History\</h2>

| Version | Date       | Author | Changes                                                      |
|---------|------------|--------|--------------------------------------------------------------|
| 1.0.4   | 2026-02-08 | DBQ    | Formating changes, some wording changes                      |
| 1.0.3   | 2025-11-10 | DBQ    | Formating changes, some wording changes                      |
| 1.0.2   | 2025-11-10 | DBQ    | Formating changes, some wording changes                      |
| 1.0.1   | 2025-11-06 | DBQ    | Tweak some wording and added "coming soon"                   |
| 1.0.0   | 2025-11-05 | DBQ    | Initial release of top-level README and repository structure |
