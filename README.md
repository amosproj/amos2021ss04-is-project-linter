# ACIDIC - Autonomous Continuous Inner-source Development and Integrity Checker

## Beschreibung
Linter für Inner-Source-Software

## Architektur
![Softwarearchitektur](assets/architektur.png)
![Datenbankschema](assets/database.png)

## Umgebungsvariablen (Environment variables)
Setzen Sie diese Umgebungsvariablen in einer .env datei in der projektwurzel.
Umgebungsvariablen werden sowohl von Frontend als auch Backend verwendet.

- `DB_PASSWORD`: Postgres Passwort
- `DB_USER`: Postgres username
- `DB_NAME`: Postgres database name
- `PORT`: Der Port auf dem das System erreichbar sein wird (frontend UND api)
- `GITLAB_ACCESS_TOKEN`: Zugriffstoken für eine gitlab Instanz
 

## Ausführen (mit docker-compose)
Datei `.env` erstellen (hier in der repo-root) und die Umgebungsvariablen vom vorherigen Kapitel einsetzen.

Gesamte Software-Architektur starten:
```shell
docker-compose --env-file .env up
```
