package de.javamark.taskboard.boundary.websocket;

import de.javamark.taskboard.control.ConnectionManager;
import io.quarkus.logging.Log;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.PathParam;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;

import jakarta.inject.Inject;
import java.time.LocalDateTime;

@WebSocket(path = "/ws/board/{projectId}")
public class TaskBoardWebSocket {

    @Inject
    ConnectionManager connectionManager;

    @OnOpen
    public void onOpen(WebSocketConnection connection, @PathParam String projectId) {
        Long projectIdLong = Long.parseLong(projectId);
        connectionManager.addConnection(projectIdLong, connection);

        String welcomeMessage = String.format("""
            {
                "type": "connected",
                "projectId": %s,
                "message": "Connected to project board",
                "timestamp": "%s"
            }
            """, projectId, LocalDateTime.now());

        connection.sendText(welcomeMessage)
                .subscribe().with(
                        success -> {
                            String joinMessage = String.format("""
                        {
                            "type": "user_joined",
                            "message": "Ein Benutzer ist dem Board beigetreten",
                            "activeUsers": %d,
                            "timestamp": "%s"
                        }
                        """, connectionManager.getConnectionCount(projectIdLong), LocalDateTime.now());

                            connectionManager.broadcastToProject(projectIdLong, joinMessage);
                        },
                        failure -> Log.error("Failed to send welcome message: " + failure.getMessage())
                );
    }

    @OnTextMessage
    public void onTextMessage(String message, @PathParam String projectId, WebSocketConnection connection) {
        Long projectIdLong = Long.parseLong(projectId);
        Log.info("Received message from client: " + message);

        try {
            if (message.startsWith("{")) {
                if (message.contains("\"type\":\"ping\"")) {
                    String pongMessage = "{\"type\":\"pong\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
                    connection.sendText(pongMessage)
                            .subscribe().with(
                                    success -> { /* Pong sent */ },
                                    failure -> Log.error("Failed to send pong: " + failure.getMessage())
                            );
                } else if (message.contains("\"type\":\"typing\"")) {
                    connectionManager.broadcastToProject(projectIdLong, message);
                }
            }
        } catch (Exception e) {
            Log.error("Error processing WebSocket message: " + e.getMessage());

            String errorMessage = String.format("""
                {
                    "type": "error",
                    "message": "Nachricht konnte nicht verarbeitet werden",
                    "timestamp": "%s"
                }
                """, LocalDateTime.now());

            connection.sendText(errorMessage)
                    .subscribe().with(
                            success -> { /* Error message sent */ },
                            failure -> Log.error("Failed to send error message: " + failure.getMessage())
                    );
        }
    }

    @OnClose
    public void onClose(WebSocketConnection connection, @PathParam String projectId) {
        Long projectIdLong = Long.parseLong(projectId);
        connectionManager.removeConnection(connection);

        String leaveMessage = String.format("""
            {
                "type": "user_left",
                "message": "Ein Benutzer hat das Board verlassen",
                "activeUsers": %d,
                "timestamp": "%s"
            }
            """, connectionManager.getConnectionCount(projectIdLong), LocalDateTime.now());

        connectionManager.broadcastToProject(projectIdLong, leaveMessage);
    }
}