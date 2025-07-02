package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements HttpHandler {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public LoginHandler(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        try {
            String formData = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).readLine();
            Map<String, String> params = HandlerUtils.parseUrlEncodedFormData(formData);
            String loginType = params.get("login_type");
            String username = params.get("username");

            if ("admin".equals(loginType)) {
                handleAdminLogin(exchange, params);
            } else {
                handleCustomerLogin(exchange, username);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error", "text/plain");
        }
    }

    private void handleAdminLogin(HttpExchange exchange, Map<String, String> params) throws Exception {
        String username = params.get("username");
        String password = params.get("password");
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword() != null && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.EMPLOYEE) {
                String sessionId = sessionManager.createSession(user);
                sessionManager.setSessionCookie(exchange, sessionId);
                HandlerUtils.redirect(exchange, "/admin-dashboard");
            } else {
                sendLoginError(exchange, "Not an admin account.");
            }
        } else {
            sendLoginError(exchange, "Invalid username or password.");
        }
    }

    private void handleCustomerLogin(HttpExchange exchange, String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            sendLoginError(exchange, "Username cannot be empty.");
            return;
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = new User(username, "", Role.CUSTOMER);
            userRepository.save(user);
        }

        String sessionId = sessionManager.createSession(user);
        sessionManager.setSessionCookie(exchange, sessionId);
        HandlerUtils.redirect(exchange, "/order-form");
    }

    private void sendLoginError(HttpExchange exchange, String message) throws IOException {
        String response = "<html><body><h1>Login Failed</h1><p>" + HandlerUtils.escapeHtml(message) + "</p><a href='/'>Try again</a></body></html>";
        HandlerUtils.sendResponse(exchange, 401, response, "text/html");
    }
}
