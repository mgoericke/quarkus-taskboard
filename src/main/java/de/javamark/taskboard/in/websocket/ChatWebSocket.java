package de.javamark.taskboard.in.websocket;

import de.javamark.taskboard.logic.ChatService;
import de.javamark.taskboard.logic.ConnectionManager;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.PathParam;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.time.LocalDateTime;
import java.util.UUID;

@WebSocket(path = "/ws/chat/{projectId}")
public class ChatWebSocket {

    @Inject
    ChatService chatService;

    @Inject
    ConnectionManager connectionManager;

    @OnOpen
    public void onOpen(WebSocketConnection connection, @PathParam String projectId) {
        Long projectIdLong = Long.parseLong(projectId);
        connectionManager.addConnection(projectIdLong, connection);

        String welcomeMessage = Json.createObjectBuilder()
                .add("type", "bot_message")
                .add("message", "Hallo! Ich bin dein Task-Assistent. Frag mich gerne nach Tasks, Statistiken oder Team-Mitgliedern!")
                .add("timestamp", LocalDateTime.now().toString())
                .build()
                .toString();

        connection.sendText(welcomeMessage)
                .subscribe().with(
                        success -> { /* Welcome message sent */ },
                        failure -> System.err.println("Failed to send welcome message: " + failure.getMessage())
                );
    }

    @OnTextMessage
    public void onTextMessage(String message, @PathParam String projectId, WebSocketConnection connection) {
        Long projectIdLong = Long.parseLong(projectId);

        try {
            JsonObject jsonMessage = Json.createReader(new java.io.StringReader(message)).readObject();

            if ("user_message".equals(jsonMessage.getString("type"))) {
                String question = jsonMessage.getString("message");
                String sessionId = jsonMessage.getString("sessionId", UUID.randomUUID().toString());

                // Send typing indicator
                String typingMessage = Json.createObjectBuilder()
                        .add("type", "bot_typing")
                        .add("message", "Bot tippt...")
                        .add("timestamp", LocalDateTime.now().toString())
                        .build()
                        .toString();

                connection.sendText(typingMessage)
                        .subscribe().with(
                                success -> {
                                    // Process question with AI (blocking call in separate thread)
                                    new Thread(() -> {
                                        try {
                                            String answer = chatService.processQuestion(question, sessionId, projectIdLong);

                                            String responseMessage = Json.createObjectBuilder()
                                                    .add("type", "bot_message")
                                                    .add("message", answer)
                                                    .add("timestamp", LocalDateTime.now().toString())
                                                    .add("sessionId", sessionId)
                                                    .build()
                                                    .toString();

                                            connection.sendText(responseMessage)
                                                    .subscribe().with(
                                                            responseSuccess -> { /* Response sent */ },
                                                            responseFailure -> System.err.println("Failed to send bot response: " + responseFailure.getMessage())
                                                    );
                                        } catch (Exception e) {
                                            String errorMessage = Json.createObjectBuilder()
                                                    .add("type", "bot_error")
                                                    .add("message", "Entschuldigung, ich konnte deine Frage nicht beantworten.")
                                                    .add("timestamp", LocalDateTime.now().toString())
                                                    .build()
                                                    .toString();

                                            connection.sendText(errorMessage)
                                                    .subscribe().with(
                                                            errorSuccess -> { /* Error message sent */ },
                                                            errorFailure -> System.err.println("Failed to send error message: " + errorFailure.getMessage())
                                                    );
                                            System.err.println("Chat service error: " + e.getMessage());
                                        }
                                    }).start();
                                },
                                failure -> System.err.println("Failed to send typing indicator: " + failure.getMessage())
                        );
            }
        } catch (Exception e) {
            System.err.println("Error processing chat message: " + e.getMessage());

            String errorMessage = Json.createObjectBuilder()
                    .add("type", "error")
                    .add("message", "Nachricht konnte nicht verarbeitet werden")
                    .add("timestamp", LocalDateTime.now().toString())
                    .build()
                    .toString();

            connection.sendText(errorMessage)
                    .subscribe().with(
                            success -> { /* Error message sent */ },
                            failure -> System.err.println("Failed to send error message: " + failure.getMessage())
                    );
        }
    }

    @OnClose
    public void onClose(WebSocketConnection connection, @PathParam String projectId) {
        connectionManager.removeConnection(connection);
    }
}