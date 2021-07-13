# REST-API

Die API ist prinzipiell unter `http://<HOST>/6969/api` erreichtbar.

## GET - `/projects` - Übersicht aller Projekte

- Query:
  - `delta=true|false` Soll nach dem Delta (der letzten 30 Tage) oder dem letzten Ergebnis sortiert werden?
  - `name=String` Der partielle oder ganze Name des Projekts oder Namespaces, nach dem gesucht werden soll. Groß-Kleinschreibung wird nicht beachtet.
  - `page` z.B. 2
  - `size` z.B. 10
  - `sort` z.B. Benutzerfreundlichkeit
- Body: nichts
- Response: `Json(Page(ProjectSchema))`

## GET - `/projects/allTags` - Übersicht über die zeitliche Entwicklung der Anzahl der Projekte, die alle Checks eines Tags bestanden haben

- Query:
  - `type=percentage|absolute` Liefert je nach `type` die Prozentzahl oder die absolute Anzahl der Projekte, die alle Checks eines Tags bestanden haben. Wird ein anderer Parameter als `percentage` oder `absolute` übergeben, wird nichts zurückgegeben.
- Body: nichts
- Response: `Json(TreeMap<LocalDateTime, HashMap<String, number>>)`

## GET - `/projects/top` - Übersicht über die zeitliche Entwicklung der Anzahl der Projekte, die alle der Top 3, 5 und 10 wichtigsten Checks bestanden haben

- Query:
  - `type=percentage|absolute` Liefert je nach `type` die Prozentzahl oder die absolute Anzahl der Projekte, die alle der wichtigsten Checks bestanden haben. Wird ein anderer Parameter als `percentage` oder `absolute` übergeben, wird nichts zurückgegeben.
- Body: nichts
- Response: `Json(TreeMap<LocalDateTime, TreeMap<number, number>>)`

## GET - `/project/{Id}` - Ergebnisse für das Projekt mit der Id

- Hinweis: Es handelt sich bei der Id um eine interne Benennung der Datenbank, nicht die GitLab Project-Id.
- Body: nichts
- Response: `Json(ProjectSchema)`

## GET - `/crawler` - aktuellen Crawler Status abfragen

- Body: nichts
- Response: `Json(CrawlerStatusSchema)`

## POST - `/crawler` - Crawler manuell anstoßen

- Body: nichts
- Response:
  - `200` falls crawler gestartet wurde
  - `429` falls crawler bereits aktiv ist.

## GET - `/export/csv` - Exportiert Ergebnisse Als CSV-Datei

- Body: nichts
- Response: Eine CSV Datei, direkt zum Download
- Hinweis: Am besten per `<a download href="http://<HOST>/6969/api/export/csv">download</a>` einbetten.

## GET - `/config` - Holt die Config Datei

- Body: nichts
- Response: Eine JSON antwort mit der config.

# Schemas

### CrawlerStatusSchema

```jsonc
{
	"status": "Linting the projects", // Der aktuelle Status des Crawlers
	"lastError": "", // Die letzte Errornachricht
	"errorTime": null, // Die Zeit, bei dem der letzte Fehler stattgefunden hat
	"crawlerActive": true, // Zeigt an, ob der crawler gerade aktiv ist oder nicht
	"size": 1024, // Anzahl aller gefundenen Projekte
	"lintingProgress": 128, // Anzahl bereits gelinteter Projekte
	"lintingTime": 0 // Anzahl der Sekunden, die während des letzen Crawlingvorgangs vergeangen sind
}
```

### ProjectSchema

```jsonc
{
	"name": "Amos Test", // Der Name des Projekts
	"url": "https://gitlab.domain.de/user/amos-test",
	// Die URL zum Repository
	"gitlabProjectId": 4711, // Die Projekt-ID auf der GitLab Instanz
	"nameSpace": "be15piel", // Der Namespace des Projekts
	"description": "", // Die Beschreibung des Projekts
	"forkCount": 1, // Anzahl der Forks des Projekts
	"lastCommit": "2021-05-29T00:10:11.411+00:00",
	// Zeitstempel der letzten Aktivität auf dem Projekt
	"lintingResults": [], // Array von LintingResultSchemas
	"latestPassedByTag": {
		// Anzahl der Checks pro Tag, die erfolgreich waren
		"Entwicklerfreundlichkeit": 7,
		"Maintainerfreundlichkeit": 1,
		"Benutzerfreundlichkeit": 10
	},
	"passedByTag30DaysAgo": {
		// Anzahl der Checks pro Tag, die vor 30 Tagen erfolgreich waren
		"Entwicklerfreundlichkeit": 7,
		"Maintainerfreundlichkeit": 1,
		"Benutzerfreundlichkeit": 10
	},
	"latestPassedTotal": 17, // Anzahl der Checks, die erfolgreich waren und nach denen sortiert werden sollen
	"delta": 0, // Das Delta zwischen dem Ergebnis vor 30 Tagen und dem neuestem
	"id": 1 // Die ACIDIC-Interne ID
}
```

### LintingResultSchema

```jsonc
{
	"lintTime": "2021-06-01T20:18:22.811963",
	// Zeitstempel, zu dem der Lint-Vorgang angestoßen wurde
	"checkResults": [], // Array von CheckResultSchemas
	"id": 2 // Die ACIDIC-Interne ID
}
```

### CheckResultSchema

```jsonc
{
	"checkName": "checkReadmeExistence", // Name des Checks und der Methode, die die Überprüfung übernimmt
	"result": true, // Das Ergebnis des Checks
	"category": "file_checks", // Die Kategorie des Checks
	"severity": "HIGH", // Wie wichtig der Check ist
	"description": "Überprüft, ob eine README Datei existiert.",
	// Die Beschreibung des Checks
	"message": "Keine README Datei gefunden!",
	// Fehlermeldung, die angezeigt werden soll, falls Check fehlgeschlagen ist
	"fix": "Legen Sie eine README Datei in der Projektwurzel an.",
	// Vorgeschlagener Weg, den Fehlgeschlagenen Check zu beheben
	"tag": "Benutzerfreundlichkeit", // Tag, nach dem sortiert werden soll
	"priority": 100 // Priorität, nach der sortiert werden soll
}
```
