<a href="https://github.com/amosproj/amos-ss2021-is-project-linter">
  <p align="center">
    <img height=150 src="https://raw.githubusercontent.com/amosproj/amos-ss2021-is-project-linter/main/assets/header.svg"/>
  </p>
</a>

<p align="center">
  <strong>Autonomous Continuous Inner-source Development and Integrity Checker</strong>
</p>

<h3 align="center">
  <a href="./docs/README.md#installation">Installation</a>
  <span> · </span>
  <a href="./docs/Architektur.md">Architektur</a>
  <span> · </span>
  <a href="./docs/README.md#nutzung">Nutzung</a>
</h3>

---

## Beschreibung

Linter für Inner-Source-Software

Die ACIDIC Software analysiert und gibt Tips zum Verbessern von Softwareprojekten auf einer GitLab Instanz. Sie überprüft diese Projekte auf (siehe <a href="./docs/Checks.md">Wiki</a>):

- Korrekte Einstellungen
- Existenz von benötigten Dateien
- Existenz von Links innerhalb der benötigten Dateien

Implementiert ist die Software als Web-App mit:

- <a href="https://www.java.com/">Java</a> und <a href="https://spring.io/projects/spring-boot">Spring Boot</a> im Backend
- <a href="https://www.nginx.com/">nginx</a> und <a href="https://angular.io/">Angular</a> im Frontend
- <a href="https://www.postgresql.org/">PostgreSQL</a> als Datenbank
- <a href="https://www.docker.com/">Docker</a> für einfaches Deployment

## Releases

Alle bisherigen Release-Versionen sind unter <a href="https://github.com/amosproj/amos-ss2021-is-project-linter/releases">Releases</a> zu finden.

## Weitere Informationen zum Projekt

Dieses Projekt entsteht im Rahmen des AMOS-Projekts an der Friedrich-Alexander-Universität Erlangen-Nürnberg in Kooperation mit dem Industriepartner DATEV.
