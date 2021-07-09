# Test Data Generator

Erstellt eine Menge an Daten, die Sie nutzen können um sofort anzufangen die Software zu nutzen oder zum weiterentwickeln.

# Voraussetzungen

## Python

Es wird python3 benötigt mit diesen Paketen:
`json, csv, datetime, random, requests, sqlalchemy`

Automatisch installierbar per: `pip install -r requirements.txt`

## Konfigurationsdatei

Die Testdaten werden zum teil aus `../config.json` geholt. Stellen Sie sicher, dass dort der `gitLabHost` gesetzt ist.

## Datenbank

Es muss eine Posgres-Datenbank existieren mit den Relationen `check_result`, `project` und `linting_result`.
Dafür genügt es, die Java-Applikation zu starten. Diese generiert automatisch die Relationen mit den benötigten Attributen.

# Daten generieren

Sie haben zwei Möglichkeiten was mit den generierten Daten passiert:

## 1. Direkt in die Datenbank eingefügt

Führen sie `python3 insert.py` aus und folgen sie den Anweisungen.
Die Daten werden automatisch in die Datenbank eingefügt.

## 2. als CSV generiert

Mit dem Kommando `python3 generate.py` werden die Daten generiert.

Sie erhalten folgende Dateien:

- `projects.csv` für das Projekt-Schema
- `linting_results.csv` für das LintingResult-Schema
- `check_results.csv` für das CheckResult-Schema

Die könen sie auf folgende Arten importieren:

- In der IDE IntelliJ können sie die Daten importieren.
  Folgen sie dieser Anleitung und importieren sie die entsprechende Json-Datei für jedes Schema. [Siehe hier](https://www.jetbrains.com/help/idea/import-data.html#import_csv)
- mit `sh copy.sh` können sie ein script nutzen welches automatisch die daten in die Datenbank einfügt.
  Geben sie einfach Datenbanknamen-, nutzernamen und passwort nach Aufforderung ein.
