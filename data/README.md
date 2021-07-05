# Test Data Generator

## Voraussetzungen

### Python

Es wird python3 benötigt mit diesen Paketen:
`json, csv, datetime, random, requests`

### Konfigurationsdatei

Die Testdaten werden zum teil aus `../config.json` geholt. Stellen sie sicher dass dort ein `gitLabHost` gesetzt ist.

## Daten generieren

Mit dem Kommando `python3 generate.py` werden die Daten generiert.

Sie erhalten folgende Dateien:

- `projects.csv` für das Projekt-Schema
- `linting_results.csv` für das LintingResult-Schema
- `check_results.csv` für das CheckResult-Schema

## Daten importieren

### IntelliJ

In der IDE IntelliJ können sie die Daten importieren.
Folgen sie dieser Anleitung und importieren sie die entsprechende Json-Datei für jedes Schema: https://www.jetbrains.com/help/idea/import-data.html#import_csv

### Bash

mit `sh copy.sh` können sie ein script nutzen welches automatisch die daten in die Datenbank einfügt.
Geben sie einfach Datenbanknamen-, nutzernamen und passwort nach Aufforderung ein.
