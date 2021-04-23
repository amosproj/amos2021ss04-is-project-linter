# ACIDIC - Autonomous Continuous Inner-source Development and Integrity Checker

## Description
Linter for inner-source software

## Architecture
TODO

## Umgebungsvariablen (Environment variables)
- `DB_PASSWORD`: Postgres Passwort
- `DB_USER`: Postgres username
- `DB_NAME`: Postgres database name
- `PORT`: Der Port auf dem das System erreichbar sein wird (frontend UND api)
 

## Ausführen (mit docker-compose)
Datei `.env` erstellen (hier in der repo-root) und die Umgebungsvariablen vom vorherigen Kapitel einsetzen.

Gesamte Software-Architektur starten:
```shell
docker-compose --env-file .env up
```
