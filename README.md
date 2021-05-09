<a href="https://github.com/amosproj/amos-ss2021-is-project-linter">
  <p align="center">
    <img height=150 src="https://raw.githubusercontent.com/amosproj/amos-ss2021-is-project-linter/main/assets/header.svg"/>
  </p>
</a>

<p align="center">
  <strong>Autonomous Continuous Inner-source Development and Integrity Checker 🚀</strong>
</p>

<h3 align="center">
  <a href="https://github.com/amosproj/amos-ss2021-is-project-linter/wiki/Docker">Install</a>
  <span> · </span>
  <a href="https://github.com/amosproj/amos-ss2021-is-project-linter/wiki/Architecture">Architecture</a>
  <span> · </span>
  <a href="https://github.com/amosproj/amos-ss2021-is-project-linter/wiki/Config-File">Configuration</a>
</h3>

---

## Beschreibung
Linter für Inner-Source-Software
 
The software to be developed shall “lint” (analyze and suggest improvements) software projects on a GitLab instance; at a minimum, the software shall test GitLab projects for
* Correct settings (see wiki pages)
* Existence of required files
* Dead links

Beyond basic linting, students can decide on additional features; these could include, but are not limited to:
* Analysis of readability of texts and suggestions for improvement
* Visualisation, aggregation, and analysis of linting results over time In addition, the software shall have an extensible architecture with a defined plugin architecture. The software shall be implemented as a web application with
* Spring Boot in the backend
* Angular in the frontend
* PostgreSQL as the database, and
* packaged into Docker images
