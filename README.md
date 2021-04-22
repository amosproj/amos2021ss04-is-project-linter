# ACIDIC - Autonomous Continuous Inner-source Development and Integrity Checker

## Description
Linter for inner-source softwaree

## Architecture
TODO

## Umgebungsvariablen (Environment variables)
- `POSTGRES_PASSWORD`: Postgres Passwort
- `POSTGRES_USER`: Postgres username
- `POSTGRES_DB`: Postgres database name

## Ausführen (mit docker-compose)
Datei `.env` erstellen (hier in der repo-root) und die Umgebungsvariablen vom vorherigen Kapitel einsetzen.

Gesamte Software-Architektur starten:
```shell
docker-compose --env-file .env up
```
