# AngularFrontend

Wurde mit [Angular CLI](https://github.com/angular/angular-cli) version 11.2.10 erstellt.

## Setup

NPM-Pakete installieren

```
npm install
```

## Bauen

`ng build`

Flag `--prod` setzen für production build.

## Docker

Erst bauen (siehe oben) dann folgendes Ausführen: `docker build -t amoslinter/frontend:<TAG> .` (tag z.B staging oder release)

## Local Dev-Server

`ng serve` für einen Lokalen Server zum Testen.
Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.
