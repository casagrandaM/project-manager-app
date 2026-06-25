# Task and Project Manager Web App

Diese Web-Applikation wurde im Zuge der Lehrveranstaltung KT Communications Engineering an der JKU entwickelt. Sie ermöglicht die Erstellung und Verwaltung von Projekten sowie den dazugehörigen Aufgaben.

## Voraussetzungen

Vor dem Setup sollten folgende Komponenten installiert sein:

* Docker
* Java (passende Version gemäß Projektkonfiguration)
* Maven
* Node.js und npm

Nach dem Klonen des Repositories sollte sichergestellt werden, dass das Modul `backend` von der IDE als Maven-/Spring-Modul erkannt wurde. Falls dies nicht automatisch erfolgt, kann das Modul über die enthaltene `pom.xml` importiert werden.

## Einrichtung

### Datenbank

Für den Betrieb der Datenbank muss Docker laufen.

1. Datenbank starten:

   ```bash
   docker compose up -d
   ```

2. Alle Skripte im Verzeichnis `db/init` in der vorgesehenen Reihenfolge auf dem Schema `appdb` ausführen.

Die benötigten Datenbank-Zugangsdaten sind bereits im `docker-compose.yml` hinterlegt und müssen nicht manuell gesetzt werden.

### Backend

Abhängigkeiten herunterladen und das Projekt kompilieren:

```bash
cd backend
mvn compile
```

Falls das Backend nicht über die bereitgestellte Run Configuration gestartet wird, müssen die benötigten Umgebungsvariablen in der Datei `.env` gesetzt werden. Diese werden von der Sicherheitskonfiguration verwendet.

### Frontend

Abhängigkeiten installieren:

```bash
cd frontend
npm install
```

## Anwendung starten

### Empfohlene Variante: Run Configurations

Für die lokale Entwicklung wird empfohlen, die bereitgestellten Run Configurations zu verwenden (falls möglich):

* Backend: `BackendApplication`
* Frontend: `Angular Application`

Insbesondere beim Backend entfällt dadurch die manuelle Konfiguration der Umgebungsvariablen.

### Backend

Voraussetzung: Der Datenbank-Container läuft.

Das Backend kann auf folgende Arten gestartet werden:

#### Über die Run Configuration

`BackendApplication`

#### Über die Main-Klasse

```text
backend/src/main/java/at/jku/app/BackendApplication.java
```

#### Über Maven

```bash
cd backend
mvn spring-boot:run
```

Anschließend ist das Backend unter http://localhost:8080 erreichbar.

### Frontend

Das Frontend kann auf folgende Arten gestartet werden:

#### Über die Run Configuration

`Angular Application`

#### Über npm

```bash
cd frontend
npm run start
```

Anschließend ist das Frontend unter http://localhost:4200 erreichbar.

### Frontend und Backend gemeinsam starten

Optional kann sowohl das Frontend als auch das Backend gleichzeitig über ein Startskript im Root-Verzeichnis gestartet werden:

```bash
npm install
npm run start
```

Dabei werden Frontend und Backend gemeinsam gestartet. In diesem Fall müssen die benötigten Umgebungsvariablen für das Backend bereits gesetzt sein.

Da mittlerweile Run Configurations vorhanden sind, wird diese Variante nur noch eingeschränkt empfohlen.
