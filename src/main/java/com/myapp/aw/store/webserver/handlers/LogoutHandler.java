package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;

public class LogoutHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public LogoutHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<String> sessionIdOpt = sessionManager.getSessionId(exchange);
        if (sessionIdOpt.isPresent()) {
            sessionManager.removeSession(sessionIdOpt.get());
        }
        sessionManager.clearSessionCookie(exchange);
        HandlerUtils.redirect(exchange, "/");
    }
}
