Die API ist prinzipiell unter `http://<HOST>/6969/api` erreichtbar.

### GET - `/projects` - Übersicht aller Projekte
* Query: 
    * `extended=true|false` liefert auf Wunsch auch die Ergebnisse von allen Lint-Durchläufen der letzten 30 Tage.
    * `tag=String` Sortiert die Projekte direkt nach dem übergebenen Tag in Absteigender Reihenfolge
* Body: nichts
* Response: Json(List(ProjectSchema))

### GET - `/projects/allTags` - Übersicht über die zeitliche Entwicklung der Anzahl der Projekte, die alle Checks eines Tags bestanden haben
* Query:
    * `type=percentage|absolute` Liefert je nach `type` die Prozentzahl oder die absolute Anzahl der Projekte, die alle Checks eines Tags bestanden haben. Wird ein anderer Parameter als `percentage` oder `absolute` übergeben, wird nichts zurückgegeben.
* Body: nichts
* Response: Json(TreeMap<LocalDateTime, HashMap<String, Long>>) 

### POST - `/projects` - Linted ein einzelnes Repo
* Body: url des zu lintenden Repos
* Response: 'ok' falls ok, HTTP Fehlercode sonst

### GET - `/project/{Id}` - Ergebnisse für das Projekt mit der Id
* Hinweis: Es handelt sich bei der Id um eine interne Benennung der Datenbank, nicht die GitLab Project-Id.
* Body: nichts
* Response: Json(ProjectSchema)

### GET - `/project/{Id}/lastMonth` - Lint Ergebnisse des Letzten Monats für ein Projekt
* Body: nichts
* Response: Json(ProjectSchema)

### GET - `/crawler` - aktuellen Crawler Status abfragen
* Body: nichts
* Response: Json(CrawlerStatusSchema)

### POST - `/crawler` - Crawler manuell anstoßen
* Body: nichts
* Response: 
  * `Http:OK` falls crawler gestartet wurde
  * `Http:Too_Many_Requests` falls crawler bereits aktiv war, bevor der Aufruf ankam

### GET - `/export/csv` - Exportiert Ergebnisse Als CSV-Datei
* Body: nichts
* Response: Eine CSV Datei, direkt zum Download, also am besten per `<a download href="http://<HOST>/6969/api/export/csv">download</a>` einbetten

### GET - `/config` - Holt die Config Datei
* Body: nichts
* Response: Eine JSON antwort mit der config.

# Schemas
#### CrawlerStatusSchema
```jsonc
{
    "status": "Linting the projects",   // Der aktuelle Status des Crawlers
    "lastError": "",                    // Die letzte Errornachricht
    "errorTime": null,                  // Die Zeit, bei dem der letzte Fehler stattgefunden hat
    "crawlerActive": true,              // Zeigt an, ob der crawler gerade aktiv ist oder nicht
    "size": 1024,                       // Anzahl aller gefundenen Projekte
    "lintingProgress": 128,             // Anzahl bereits gelinteter Projekte
    "lintingTime": 0                    // Anzahl der Sekunden, die während des letzen Crawlingvorgangs vergeangen sind
}
```

#### ProjectSchema
```jsonc
{
    "name": "Amos Test",                // Der Name des Projekts
    "url": "https://gitlab.domain.de/user/amos-test", 
                                        // Die URL zum Repository
    "gitlabProjectId": 4711,            // Die Projekt-ID auf der GitLab Instanz
    "gitlabInstance": "https://gitlab.domain.de", 
                                        // Die URL der GitLab Instanz
    "description": "",                  // Die Beschreibung des Projekts
    "forkCount": 1,                     // Anzahl der Forks des Projekts
    "lastCommit": "2021-05-29T00:10:11.411+00:00", 
                                        // Zeitstempel der letzten Aktivität auf dem Projekt
    "lintingResults": [],               // Array von LintingResultSchemas
    "id": 1                             // Die ACIDIC-Interne ID
}
```

#### LintingResultSchema
```jsonc
{
    "lintTime": "2021-06-01T20:18:22.811963", 
                                        // Zeitstempel, zu dem der Lint-Vorgang angestoßen wurde
    "checkResults": [],                 // Array von CheckResultSchemas
    "id": 2                             // Die ACIDIC-Interne ID
}
```

#### CheckResultSchema
```jsonc
{
    "checkName": "checkReadmeExistence",// Name des Checks und der Methode, die die Überprüfung übernimmt
    "result": true,                     // Das Ergebnis des Checks
    "category": "file_checks",          // Die Kategorie des Checks
    "severity": "HIGH",                 // Wie wichtig der Check ist
    "description": "Überprüft, ob eine README Datei existiert.", 
                                        // Die Beschreibung des Checks
    "message": "Keine README Datei gefunden!", 
                                        // Fehlermeldung, die angezeigt werden soll, falls Check fehlgeschlagen ist
    "fix": "Legen Sie eine README Datei in der Projektwurzel an.", 
                                        // Vorgeschlagener Weg, den Fehlgeschlagenen Check zu beheben 
    "tag": "Benutzerfreundlichkeit",    // Tag, nach dem sortiert werden soll
    "priority": 100                     // Priorität, nach der sortiert werden soll
}
```





