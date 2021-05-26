# Getting Started

Wir nutzen Java 11 für dieses Projekt, es sollte aber auch java 12, 13, 14 und 15 unterstützt sein.

## Bauen

* Linux: `./gradlew build -x test`
* Windows: `gradlew.bat build -x test`

## Docker

Erst Java Projekt bauen (siehe oben), dann folgendes ausführen:

Bauen: `docker build -t amoslinter/server:<TAG> .` (tag z.B staging oder release)

## Tests ausführen

```shell
./gradlew test
```
