-- Teams
INSERT INTO teams (id, name, description, created_at) VALUES
    (1, 'Quarkus Development Team', 'Team für die Entwicklung der Task Board Anwendung', CURRENT_TIMESTAMP),
    (2, 'Mobile App Development', 'Team für iOS und Android App Entwicklung', CURRENT_TIMESTAMP),
    (3, 'DevOps & Infrastructure', 'Team für Cloud-Infrastruktur und CI/CD Pipelines', CURRENT_TIMESTAMP);

-- Members
INSERT INTO members (id, team_id, name, email, role, created_at) VALUES
                                                                     -- Quarkus Team
                                                                     (1, 1, 'Max Mustermann', 'max@example.com', 'LEAD', CURRENT_TIMESTAMP),
                                                                     (2, 1, 'Anna Schmidt', 'anna@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (3, 1, 'Sarah Weber', 'sarah@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (4, 1, 'Tom Fischer', 'tom@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (5, 1, 'Lisa Müller', 'lisa@example.com', 'DESIGNER', CURRENT_TIMESTAMP),
                                                                     -- Mobile Team
                                                                     (6, 2, 'Julia Hoffmann', 'julia@example.com', 'LEAD', CURRENT_TIMESTAMP),
                                                                     (7, 2, 'Marco Klein', 'marco@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (8, 2, 'Nina Becker', 'nina@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (9, 2, 'Paul Wagner', 'paul@example.com', 'DESIGNER', CURRENT_TIMESTAMP),
                                                                     -- DevOps Team
                                                                     (10, 3, 'Michael König', 'michael@example.com', 'LEAD', CURRENT_TIMESTAMP),
                                                                     (11, 3, 'Sandra Braun', 'sandra@example.com', 'DEVELOPER', CURRENT_TIMESTAMP),
                                                                     (12, 3, 'David Schulz', 'david@example.com', 'DEVELOPER', CURRENT_TIMESTAMP);

-- Projects
INSERT INTO projects (id, team_id, name, description, status, created_at) VALUES
    (1, 1, 'Quarkus Training Vorbereitung', 'Entwicklung einer Task Board Anwendung für das Quarkus Training', 'ACTIVE', CURRENT_TIMESTAMP),
    (2, 2, 'E-Commerce Mobile App', 'Native iOS und Android App für Online-Shopping Platform', 'ACTIVE', CURRENT_TIMESTAMP),
    (3, 3, 'Cloud Migration Projekt', 'Migration der Legacy-Systeme in AWS Cloud mit Kubernetes', 'ACTIVE', CURRENT_TIMESTAMP);

-- Sample Tasks
INSERT INTO tasks (id, project_id, assigned_to, title, description, status, priority, due_date, created_at, updated_at) VALUES
                                                                                                                            -- Quarkus Training Projekt (ID: 1)
                                                                                                                            (1, 1, 1, 'REST API Endpoints erstellen', 'CRUD Operationen für Tasks implementieren', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (2, 1, 2, 'Hibernate ORM Setup', 'Postgres Datenbank konfigurieren und Entitäten definieren', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (3, 1, 5, 'UI Design verfeinern', 'Bootstrap Layout optimieren und responsive machen', 'TODO', 'LOW', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (4, 1, 3, 'WebSocket Integration', 'Live-Updates für Kanban Board implementieren', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (5, 1, 4, 'Datenbank Schema', 'Entitäten und Relationen final definieren', 'IN_PROGRESS', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (6, 1, 1, 'Docker Setup', 'Postgres Container konfiguriert und getestet', 'REVIEW', 'MEDIUM', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (7, 1, 2, 'Projekt Setup', 'Quarkus Projekt initialisiert und Dependencies hinzugefügt', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (8, 1, NULL, 'Team Meeting', 'Kickoff Meeting und Requirements Workshop durchgeführt', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            -- E-Commerce Mobile App Projekt (ID: 2)
                                                                                                                            (9, 2, 6, 'iOS App Setup', 'Xcode Projekt einrichten und Basis-Navigation implementieren', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (10, 2, 7, 'Android App Setup', 'Android Studio Projekt mit Kotlin einrichten', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (11, 2, 8, 'Produktkatalog Screen', 'Liste und Detail-Ansicht für Produkte entwickeln', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (12, 2, 7, 'Warenkorb Funktionalität', 'Shopping Cart Logic und Persistent Storage', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (13, 2, 9, 'UI/UX Design System', 'Einheitliches Design für iOS und Android definieren', 'IN_PROGRESS', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (14, 2, 8, 'API Integration', 'REST API Anbindung für Backend-Services', 'REVIEW', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (15, 2, 6, 'Projekt Initialisierung', 'Entwicklungsumgebung und CI/CD Setup abgeschlossen', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            -- Cloud Migration Projekt (ID: 3)
                                                                                                                            (16, 3, 10, 'AWS Account Setup', 'AWS Umgebungen und IAM Rollen konfigurieren', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (17, 3, 11, 'Kubernetes Cluster Design', 'EKS Cluster Architektur planen und dokumentieren', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (18, 3, 12, 'CI/CD Pipeline Setup', 'GitHub Actions für automatische Deployments', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '8 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (19, 3, 11, 'Legacy System Analyse', 'Bestehende Anwendungen analysieren und Dependencies mappen', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (20, 3, 12, 'Docker Container Strategy', 'Containerization Plan für alle Services entwickeln', 'IN_PROGRESS', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '6 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (21, 3, 10, 'Monitoring und Logging', 'Prometheus, Grafana und ELK Stack einrichten', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                            (22, 3, NULL, 'Stakeholder Workshop', 'Requirements und Timeline mit Management besprochen', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sequence Updates (PostgreSQL)
ALTER SEQUENCE teams_id_seq RESTART WITH 4;
ALTER SEQUENCE members_id_seq RESTART WITH 13;
ALTER SEQUENCE projects_id_seq RESTART WITH 4;
ALTER SEQUENCE tasks_id_seq RESTART WITH 23;