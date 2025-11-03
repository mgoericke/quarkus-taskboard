package de.javamark.taskboard.control;

import io.quarkus.websockets.next.WebSocketConnection;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class ConnectionManager {

    private final Map<Long, Set<WebSocketConnection>> projectConnections = new ConcurrentHashMap<>();
    private final Map<String, Long> connectionToProject = new ConcurrentHashMap<>();

    public void addConnection(Long projectId, WebSocketConnection connection) {
        projectConnections.computeIfAbsent(projectId, k -> new CopyOnWriteArraySet<>())
                .add(connection);
        connectionToProject.put(connection.id(), projectId);

        System.out.println("Client connected to project " + projectId + ". Total connections: " +
                projectConnections.get(projectId).size());
    }

    public void removeConnection(WebSocketConnection connection) {
        Long projectId = connectionToProject.remove(connection.id());
        if (projectId != null) {
            Set<WebSocketConnection> connections = projectConnections.get(projectId);
            if (connections != null) {
                connections.remove(connection);
                if (connections.isEmpty()) {
                    projectConnections.remove(projectId);
                }
                System.out.println("Client disconnected from project " + projectId);
            }
        }
    }

    public void broadcastToProject(Long projectId, String message) {
        Set<WebSocketConnection> connections = projectConnections.get(projectId);
        if (connections != null) {
            connections.forEach(connection -> {
                try {
                    connection.sendText(message)
                            .subscribe().with(
                                    success -> { /* Message sent successfully */ },
                                    failure -> {
                                        System.err.println("Failed to send message to connection: " + failure.getMessage());
                                        connections.remove(connection);
                                        connectionToProject.remove(connection.id());
                                    }
                            );
                } catch (Exception e) {
                    System.err.println("Failed to send message to connection: " + e.getMessage());
                    connections.remove(connection);
                    connectionToProject.remove(connection.id());
                }
            });

            System.out.println("Broadcasted to " + connections.size() + " connections in project " + projectId);
        }
    }

    public int getConnectionCount(Long projectId) {
        Set<WebSocketConnection> connections = projectConnections.get(projectId);
        return connections != null ? connections.size() : 0;
    }

    public Set<Long> getActiveProjects() {
        return projectConnections.keySet();
    }
}