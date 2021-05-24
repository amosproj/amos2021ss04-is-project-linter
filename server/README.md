# Getting Started

Wir nutzen Java 11 für dieses Projekt, es sollte aber auch java 12, 13, 14 und 15 unterstützt sein.

## Bauen

Auf Windows, nutze `gradlew.bat`

- Build: `./gradlew build`

## Docker

Erst Java Projekt bauen (siehe oben), dann folgendes ausführen:

Bauen: `docker build -t amoslinter/server:staging .`

Ausführen: `docker run -d --rm amoslinter/server:staging .`

## Ohne Docker Ausführen

```shell
./gradlew bootRun
```

## Tests ausführen

```shell
./gradlew test
```
