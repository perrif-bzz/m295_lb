# Name des Projektes
Meine API Projekt

## Beschreibung
Dies ist ein Beispielprojekt für eine API, die verschiedene Endpunkte zur Verfügung stellt, um Daten zu verwalten und abzurufen. Die API ist mit einem robusten Validierungssystem und einer umfangreichen Dokumentation ausgestattet, um Entwicklern die Integration zu erleichtern.

### Endpunkte
Endpunkte....

## Visuals
### Datenbankdiagramm
![Datenbankdiagramm](./images/CarDB_ERD.png)

### Klassendiagramm
![Klassendiagramm](./images/CarDB_Class_Diagram.png)

### Screenshot der Testdurchführung
![Screenshot der Testdurchführung](./images/***)

## Validierungsregeln
- Alle Eingabefelder müssen ausgefüllt sein.
- E-Mail-Adressen müssen ein gültiges Format haben.
- Passwörter müssen mindestens 8 Zeichen lang sein und eine Kombination aus Buchstaben und Zahlen enthalten.
- Benutzernamen dürfen keine Sonderzeichen enthalten.

## Berechtigungsmatrix
| Rolle  | Aktion                 | Endpunkt |
|--------|------------------------|----------|
| Admin  | GET, POST, PUT, DELETE | /car     |
| TENANT | GET, POST, PUT         | /car     |
| ALLE   | GET                    | /car     |

## OpenAPI Dokumentation der Services (Resourcen)
Die vollständige OpenAPI-Dokumentation ist unter folgendem Link verfügbar: [OpenAPI Dokumentation](./docs/openapi.yaml)

## Autor
- Name: Perri Federico
- E-Mail: perrif@bzz.ch
- GitHub: [MaxMustermann](https://github.com/MaxMustermann)

## Zusammenfassung
Dieses Projekt demonstriert die Erstellung und Dokumentation einer API mit verschiedenen Endpunkten zur Datenverwaltung. Die API umfasst umfassende Validierungsregeln, eine klare Berechtigungsmatrix und eine vollständige OpenAPI-Dokumentation, um die Integration und Nutzung für Entwickler zu erleichtern.
