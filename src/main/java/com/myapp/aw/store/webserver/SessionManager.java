package com.myapp.aw.store.webserver;

import com.myapp.aw.store.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final String SESSION_COOKIE_NAME = "STORE_SESSION_ID";
    private final Map<String, User> activeSessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, user);
        return sessionId;
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public Optional<User> getSessionUser(HttpExchange exchange) {
        return getSessionId(exchange)
                .map(activeSessions::get);
    }

    public Optional<String> getSessionId(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies == null) {
            return Optional.empty();
        }

        for (String cookieStr : cookies) {
            String[] cookiePairs = cookieStr.split(";");
            for (String pair : cookiePairs) {
                String[] keyValue = pair.trim().split("=");
                if (keyValue.length == 2 && SESSION_COOKIE_NAME.equals(keyValue[0])) {
                    return Optional.of(keyValue[1]);
                }
            }
        }
        return Optional.empty();
    }

    public void setSessionCookie(HttpExchange exchange, String sessionId) {
        // Set a long Max-Age (e.g., 1 year) to make the cookie persistent
        String cookieValue = String.format("%s=%s; path=/; Max-Age=%d", SESSION_COOKIE_NAME, sessionId, 31536000);
        exchange.getResponseHeaders().add("Set-Cookie", cookieValue);
    }

    public void clearSessionCookie(HttpExchange exchange) {
        // Set Max-Age to 0 to instruct the browser to delete the cookie
        exchange.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE_NAME + "=; path=/; Max-Age=0");
    }
}
