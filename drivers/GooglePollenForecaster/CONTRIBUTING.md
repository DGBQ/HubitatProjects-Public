# 🤝 Contributing - Google Pollen Forecaster
### _driver for Hubitat_  

## 🔢 Version
**Version:** 1.0.1  
**Date:** 2025-10-31  
**Managing Author:** David Ball-Quenneville  
**Associate Author:** ChatGPT

---

## 📑 Table of Contents
<details>
<summary>Click to expand Table of Contents</summary>

- 🧭 [Overview](#-overview) 
- 📜 [Project Philosophy](#project-philosophy)  
- 🤝 [How to Contribute](#how-to-contribute)
  - 🔃 [Fork & Branching](#fork--branching)  
  - 🔀 [Pull Request Requirements](#pull-request-requirements)
- 🧪 [Testing & QA](#testing--qa)
- 🐞 [Reporting Bugs](#reporting-bugs)
- 📄 [Formatting & Documentation  ](#formatting--documentation)
- 📘 [AI & Responsible Development](#ai--responsible-development)
- 👥 [Community & Code of Conduct](#community--code-of-conduct)
- 🎓 [Acknowledgements](#acknowledgements)
- 📝[Revision History](#revision-history) 

</details>

---

## 🧭 Overview

Thank you for your interest in contributing. This project began as an AI-assisted learning experiment and is intended to be a helpful, community-friendly Hubitat driver. Contributions that improve reliability, readability, and usefulness are welcome — but if you plan a major rewrite or new direction, consider forking the repository so your version can evolve independently.

---

## 📜 Project Philosophy

This repository started as a practical, hands-on experiment: a Software Implementation Project Manager exploring prompt engineering and Hubitat development. It’s meant to be both useful and instructive.

If you’re interested in extending or enhancing the driver, you’re encouraged to fork and evolve your own version. Pull requests are welcome, but the primary aim is to inspire new builds and learning.

---

## 🤝 How to Contribute

### 🔃Fork & Branching

- Fork the repository and create a feature branch:  
  `git checkout -b feature/your-feature-name`  
- Keep branches focused and limited in scope (one feature or fix per branch).

### 🔀Pull Request Requirements

- Open a PR from your branch to `main` with a descriptive title and a clear description of the change.  
- Include testing notes: how you tested, what to watch for, and any relevant Hubitat logs or screenshots.  
- Keep commits small and meaningful. Use clear commit messages (what changed and why).  
- If your change touches user-facing behavior, request an update of the README or relevant docs with usage notes or screenshots.

---

## 🧪 Testing & QA

Please test thoroughly before submitting a PR. Best-practice checks include:

- Verify no errors or warnings appear in Hubitat logs during initialization and typical operation.  
- Confirm attributes and events update as expected in Current States.  
- Test the main workflows (e.g., Poll, Initialize) and edge cases (invalid API key, network failure).  
- Avoid introducing excessive polling or unstable scheduling that could hit API quotas.  
- Provide reproducible test steps and logs in your PR description.  
- If you have automated tests or scripts, include them or link to instructions on how to run them.

---

## 🐞 Reporting Bugs

Please use GitHub Issues (not forum posts) to report bugs — that helps track and resolve problems in a single place.

When opening an issue:

- Include Hubitat firmware version, driver version (if available), and exact steps to reproduce.  
- Attach relevant logs and screenshots. Mask any sensitive data.  
- Search existing issues first to avoid duplicates.

💬 Kindly avoid posting bug reports directly in the Hubitat Community thread — it helps keep discussions organized and ensures your report is properly tracked and resolved. 

---

## 📄 Formatting & Documentation

Follow the project’s documentation standards to keep things consistent and readable:

- Use the Markdown Style Guide for Hubitat Drivers and App Documentation for formatting, ToC placement, icons, and heading hierarchy.  
- Keep headings visible and linkable; include a Table of Contents for longer docs.  
- Use icons and callouts to surface important notes (e.g., ⚠️, 💡, 📘).  
- Preserve any provided code examples exactly in fenced code blocks.  
- If you update documentation, keep the tone clear and consistent with the project’s voice.

---

## 📘 AI & Responsible Development

This project was created with AI-assisted prompts and iterative human review. When contributing:

- Acknowledge if AI-generated code or text was used in your submission.  
- Validate and test AI-assisted contributions carefully — do not assume they are production-ready.  
- Where appropriate, add comments explaining decisions an AI may have influenced.

This helps maintain transparency and improves the project’s learning value.

---

## 👥 Community & Code of Conduct

- This project follows Hubitat’s community standards. Be respectful, constructive, and professional in all communications.  
- If you’re unsure about tone or wording, lean on clarity and kindness. Persistent abuse or hostile behaviour will not be tolerated.

---

## 🎓 Acknowledgements

Thanks to the Hubitat Community for inspiration, the contributors who test and improve the project, and the tools (including AI) that made experimentation faster and more accessible.

---

## 📝 Revision History

| Version | Date       | Author      | Changes                                                               |
|---------|------------|-------------|-----------------------------------------------------------------------|
| 1.0.1   | 2025-11-10 | DBQ/ChatGpt | Refinements to format and phrasing                                    |
| 1.0.0   | 2025-10-31 | DBQ/ChatGpt | Initial CONTRIBUTING.md aligned with Hubitat Markdown & Voice Guides. |
