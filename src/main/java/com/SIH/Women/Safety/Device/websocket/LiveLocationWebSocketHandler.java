package com.SIH.Women.Safety.Device.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveLocationWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Optional client-to-server ping/metadata. REST location updates are broadcast by the service.
        try {
            session.sendMessage(new TextMessage("{\"type\":\"ack\"}"));
        } catch (Exception ignored) {}
    }

    public void broadcast(Object payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try { session.sendMessage(new TextMessage(json)); } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
    }
}