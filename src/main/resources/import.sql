-- Teams
INSERT INTO teams (id, name, description, created_at) VALUES
    (1, 'Quarkus Development Team', 'Team für die Entwicklung der Task Board Anwendung', CURRENT_TIMESTAMP);

-- Members
INSERT INTO members (id, team_id, name, email, role, created_at) VALUES
                                                                     (1, 1, 'Max Mustermann', 'max@example.com', 'LEAD', CURRENT_TIMESTAMP),
                                                                     (2, 1, 'Anna Schmidt', 'anna@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (3, 1, 'Sarah Weber', 'sarah@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (4, 1, 'Tom Fischer', 'tom@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (5, 1, 'Lisa Müller', 'lisa@example.com', 'DESIGNER', CURRENT_TIMESTAMP);

-- Projects
INSERT INTO projects (id, team_id, name, description, status, created_at) VALUES
    (1, 1, 'Quarkus Training Vorbereitung', 'Entwicklung einer Task Board Anwendung für das Quarkus Training', 'ACTIVE', CURRENT_TIMESTAMP);

-- Sample Tasks
INSERT INTO tasks (id, project_id, assigned_to, title, description, status, priority, due_date, created_at, updated_at) VALUES
                                                                                                                            (1, 1, 1, 'REST API Endpoints erstellen', 'CRUD Operationen für Tasks implementieren', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (2, 1, 2, 'Hibernate Reactive Setup', 'Postgres Datenbank konfigurieren und Entitäten definieren', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (3, 1, 5, 'UI Design verfeinern', 'Bootstrap Layout optimieren und responsive machen', 'TODO', 'LOW', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (4, 1, 3, 'WebSocket Integration', 'Live-Updates für Kanban Board implementieren', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (5, 1, 4, 'Datenbank Schema', 'Entitäten und Relationen final definieren', 'IN_PROGRESS', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (6, 1, 1, 'Docker Setup', 'Postgres Container konfiguriert und getestet', 'REVIEW', 'MEDIUM', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (7, 1, 2, 'Projekt Setup', 'Quarkus Projekt initialisiert und Dependencies hinzugefügt', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (8, 1, NULL, 'Team Meeting', 'Kickoff Meeting und Requirements Workshop durchgeführt', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sequence Updates (PostgreSQL)
ALTER SEQUENCE teams_id_seq RESTART WITH 2;
ALTER SEQUENCE members_id_seq RESTART WITH 6;
ALTER SEQUENCE projects_id_seq RESTART WITH 2;
ALTER SEQUENCE tasks_id_seq RESTART WITH 9;