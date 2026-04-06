package websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // map with game id to set of sessions per game
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void saveSession(int gameId, Session session) {
        connections.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(int gameId, Session session) {
        Set<Session> sessions =  connections.get(gameId);

        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                connections.remove(gameId);
            }
        }
    }

    public void broadcast(int gameId, Session excludeSession, Notification notification) throws IOException {
        Set<Session> sessions = connections.get(gameId);

        if (sessions != null) {
            String msg = new Gson().toJson(notification);
            for (Session session : sessions) {
                if (session.isOpen()) {
                    if (!session.equals(excludeSession)) {
                        session.getRemote().sendString(msg);
                    }
                }
            }
        }
    }
}