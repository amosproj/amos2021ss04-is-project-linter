**Einstellungsdatei**. Befindet sich momentan in `server/src/main/resources/config.json`

## Settings
* `gitLabHost`: Base Url der GitLab Instanz (z.B "https://gitlab.com")
* `readMeLinks`: Array mit Links, die in der Readme vorkommen sollen
* `crawler`: Alle Settings, die mit dem Crawler zusammen hängen, sind hier zu finden
  * `scheduler`: CRON Ausdruck, der angibt, wie oft der scheduler ausgeführt werden soll. Format: `sec min h d m wochentag` |
  **ACHTUNG**: Der Prozess dauert 1-3 Sekunden pro Repository. Bei 2000 Projekten also etwa eine Stunde. Prozess also besser nicht öfter als alle 2 Stunden laufen lassen.
  * `status`: Die Statusmeldungen des Crawlers
    * `init`: Der Status, der während des Initialisierens (also holen der Projekte) angezeigt wird
    * `active`: Der Status, der angezeigt wird, während der Crawler läuft
    * `inactive`: Der Status, der angezeigt wird, wenn der Crawlingprozess abgeschlossen wurde
  * `maxProjects`: Die Maximale Anzahl der Projekte, die beim Crawlingprozess gelintet werden sollen.

## Checks
Jedes Check Objekt hat folgende Parameter:
* `name`: Name des Checks (als Key)
* `enabled`: Soll der Check ausgeführt werden?
* `severity`: Wie schwerwiegend ist der Check (HIGH | MEDIUM | LOW)
* `category`: interne Kategorie, die nur für die Ausführung relevant ist
* `tag`: Bezeichner des Checks für Gruppierung (ein Wort)
* `description`: Beschreibung des Checks
* `message`: Nachricht im Fehlerfall
* `fix`: Tipp zur Behebung des Fehlers
* `priority`: Gewichtung für Sortierung im FrontEnd

## Beispieldatei
```json
{
  "settings": {
    "gitLabHost": "https://gitlab.domain.de",
    "readMeLinks": [
      "https://this.website.com/doesntexist",
      "https://that.doesnt.either"
    ],
    "crawler": {
      "scheduler": "0 0 0 * * ?",
      "status": {
        "init": "GitLab API wird angefragt",
        "active": "Crawler läuft",
        "inactive": "Crawler ist inaktiv"
      },
      "maxProjects": 10
    }
  },
  "checks": {
    "checkReadmeExistence": {
      "category": "file_checks",
      "enabled": true,
      "severity": "HIGH",
      "description": "Überprüft, ob eine README Datei existiert.",
      "message": "Keine README Datei gefunden!",
      "fix": "Legen Sie eine README Datei in der Projektwurzel an.",
      "tag": "Benutzerfreundlichkeit",
      "priority": 100
    },
    "checkContributingExistence": {
      "category": "file_checks",
      "enabled": true,
      "severity": "MEDIUM",
      "description": "Überprüft, ob eine CONTRIBUTING Datei existiert.",
      "message": "Keine CONTRIBUTING Datei gefunden!",
      "fix": "Legen Sie eine CONTRIBUTING Datei in der Projektwurzel an.",
      "tag": "Benutzerfreundlichkeit",
      "priority": 100
    }
}
```