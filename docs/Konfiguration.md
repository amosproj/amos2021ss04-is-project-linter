## Umgebungsvariablen (Environment variables)

Setzen Sie diese Umgebungsvariablen in einer .env Datei in der Wurzel des Repositories (Datei ist neu zu erstellen).
Umgebungsvariablen werden sowohl von Frontend als auch Backend verwendet.

- `DB_PASSWORD`: Postgres Passwort
- `DB_USER`: Postgres Username
- `DB_NAME`: Postgres Datenbankname
- `GITLAB_ACCESS_TOKEN`: Authentifizierungstoken für die GitLab API
- `ENVIRONMENT`: Umgebung - überlicherweise `staging` oder `stable`
- `HOST` [optional, default localhost]: Hostname/Domain auf dem das System laufen soll
- `NGINX_CONFIG`: Pfad zur nginx config
- `SSL_CERTIFICATE` [optional]: Pfad zum SSL Zertifikat
- `SSL_CERTIFICATE_KEY` [optional]: Pfad zum SSL Key

Hinweis: Mit Pfad ist immer der Pfad der Host-Maschine gemeint

Hinweis: Sie finden einige mögliche nginx-Konfigurationen in der Wurzel des Repos. Unter anderen liegen dort `nginx.conf` für einen typtische HTTP Server und `nginx_secure.conf` für eine Konfiguration die HTTPS nutzen soll.

## Beispiel `.env`-Datei

```env
DB_PASSWORD=tollepasswort
DB_USER=amos
DB_NAME=AmosLinter
GITLAB_ACCESS_TOKEN=viu43usvERrfv$
ENVIRONMENT=staging
HOST=localhost
NGINX_CONFIG=./nginx_secure.conf
SSL_CERTIFICATE=/etc/letsencrypt/live/domain/fullchain.pem
SSL_CERTIFICATE_KEY=/etc/letsencrypt/live/domain/privkey.pem
```
