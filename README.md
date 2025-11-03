# Team Task Board

Eine einfache Task-Management-Anwendung mit Kanban-Board, Live-Updates und KI-Unterstützung mit Quarkus und Vue.js 3.

## Features

- **Kanban Board**: Drag & Drop Funktionalität für Task-Management
- **Live Updates**: WebSocket-Integration für Echtzeit-Synchronisation
- **KI-Chat-Assistent**: Interaktive Hilfe für Projektmanagement
- **Statistiken**: Umfassende Projekt- und Team-Übersicht
- **Responsive Design**: Moderne UI mit Bootstrap 5
- **Task-Management**: CRUD-Operationen für Tasks mit Prioritäten und Deadlines

## Technologie-Stack

- **Frontend**: Vue.js 3 (Composition API)
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **Build Tool**: Vite
- **Styling**: Bootstrap 5 + Font Awesome
- **HTTP Client**: Axios
- **Backend**: Quarkus

## Start Project

### Backend

```shell script
./mvnw quarkus:dev
```

### Frontend

```shell
cd src/main/frontend
npm install
npm run dev
```

Im Browser http://localhost:3000 öffnen

Happy Tasking :)