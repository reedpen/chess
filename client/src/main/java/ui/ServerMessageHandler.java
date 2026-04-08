package ui;

import webSocketMessages.Notification;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage notification);
}