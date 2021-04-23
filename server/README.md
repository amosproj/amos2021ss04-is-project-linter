# Getting Started


## Building
If youre on windows, use gradlew.bat instead
- See tasks: `./gradlew tasks`
- Build: `./gradlew build`

## Docker
Erst Java Projekt builden (siehe oben), dann folgendes ausführen:

Bauen: `docker build -t amoslinter/server:staging .`

Ausführen: `docker run -d --rm amoslinter/server:staging . `

## Ohne Docker Ausführen
```shell
./gradlew bootRun
```
