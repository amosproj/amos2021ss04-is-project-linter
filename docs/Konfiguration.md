
## Umgebungsvariablen (Environment variables)
Setzen Sie diese Umgebungsvariablen in einer .env Datei in der Projektwurzel.
Umgebungsvariablen werden sowohl von Frontend als auch Backend verwendet.

- `DB_PASSWORD`: Postgres Passwort
- `DB_USER`: Postgres Username
- `DB_NAME`: Postgres Datenbankname
- `PORT`: Der Port, auf dem das System erreichbar sein wird (frontend UND api) | **ACHTUNG** momentan funktioniert nur 6969 als Port
- `GITLAB_ACCESS_TOKEN`: Authentifizierungstoken für die GitLab API
- `ENVIRONMENT`: Umgebung - überlicherweise `staging` oder `stable`
- `HOST`: Hostname/Domain auf dem das System laufen soll
 
## Beispiel `.env`-Datei
```env
DB_PASSWORD=tollepasswort
DB_USER=amos
DB_NAME=AmosLinter
PORT=3434
GITLAB_ACCESS_TOKEN=viu43usvERrfv$
ENVIRONMENT=staging
HOST=localhost
```
