## Abhängigkeiten
Docker, Docker-Compose
- Windows: Installieren Sie am besten [Docker Desktop](https://docs.docker.com/docker-for-windows/install/) (beinhaltet docker und docker-compose) 
- Linux: Hier müssen Sie docker und docker-compose seperat installieren. Folgen Sie den jeweiligen Anweisungen für Ihre Distribution: [Docker](https://docs.docker.com/engine/install/), [Docker-Compose](https://docs.docker.com/compose/install/).


## Docker Images
Überlicherweise werden die Docker Images automatisch von unserem [DockerHub](https://hub.docker.com/u/amoslinter/) gepullt.
Wollen Sie jedoch selbst bauen, schauen Sie sich folgende Anleitungen an:
* [Für das FrontEnd Image](https://github.com/amosproj/amos-ss2021-is-project-linter/blob/main/frontend/README.md)
* [Für das BackEnd Image](https://github.com/amosproj/amos-ss2021-is-project-linter/blob/main/server/README.md)

## Ausführen (mit docker-compose)
[Konfiguration](Konfiguration.md) durchführen und die wie im vorherigen Kapitel einsetzen.

Gesamte Software-Architektur starten:
```shell
docker-compose pull # für die neuesten Images (optional)
docker-compose --env-file .env up
```

## Zugriff
Folgende Endpoints sind nun erreichbar:
* FrontEnd Website: `http://localhost:<PORT>/*`
* BackEnd Rest-API: `http://localhost:<PORT>/api/*`

Die Api Endpoints finden sie [hier](API.md)
