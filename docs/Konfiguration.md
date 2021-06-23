
## Umgebungsvariablen (Environment variables)
Setzen Sie diese Umgebungsvariablen in einer .env Datei in der Wurzel des Repositories (Datei ist neu zu erstellen).
Umgebungsvariablen werden sowohl von Frontend als auch Backend verwendet.

- `DB_PASSWORD`: Postgres Passwort
- `DB_USER`: Postgres Username
- `DB_NAME`: Postgres Datenbankname
- `GITLAB_ACCESS_TOKEN`: Authentifizierungstoken für die GitLab API
- `ENVIRONMENT`: Umgebung - überlicherweise `staging` oder `stable`
- `HOST`: Hostname/Domain auf dem das System laufen soll
 
## Beispiel `.env`-Datei
```env
DB_PASSWORD=tollepasswort
DB_USER=amos
DB_NAME=AmosLinter
GITLAB_ACCESS_TOKEN=viu43usvERrfv$
ENVIRONMENT=staging
HOST=localhost
```
