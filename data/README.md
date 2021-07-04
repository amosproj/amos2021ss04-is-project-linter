# Test Data Generator

## Voraussetzungen

### Python

Es wird python3 benötigt mit diesen Paketen:
`json, csv, datetime, random`

### Json Datei mit Referenzprojekten

Es wird eine Datei `projects_ref.json` in diesem Ordner benötigt.
Diese Datei enthält eine JSON-Formatierte liste an Projekten, für die Test-Daten Generiert werden.

Beispiel mit einem Projekt:
`projects_ref.json`

```json
[
	{
		"id": 1,
		"description": "Test repo where we want everything to be true when linted.",
		"fork_count": 0,
		"gitlab_project_id": 19386,
		"last_commit": "2021-06-16 21:11:21.597000",
		"name": "AllChecksTrue",
		"name_space": "uv59uxut",
		"url": "https://gitlab.cs.fau.de/uv59uxut/allcheckstrue"
	}
]
```

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
