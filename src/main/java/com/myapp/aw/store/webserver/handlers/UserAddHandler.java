package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.UserRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserAddHandler implements HttpHandler {
    private final UserRepository userRepository;

    public UserAddHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        try {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String, String> params = HandlerUtils.parseUrlEncodedFormData(formData);

            String username = params.get("username");
            String password = params.get("password");
            String roleStr = params.get("role");

            if (username == null || password == null || roleStr == null || username.isEmpty() || password.isEmpty()) {
                HandlerUtils.sendResponse(exchange, 400, "Bad Request: Missing required form fields.", "text/plain");
                return;
            }

            if (userRepository.findByUsername(username).isPresent()) {
                HandlerUtils.sendResponse(exchange, 409, "Conflict: User with this username already exists.", "text/plain");
                return;
            }

            Role role = Role.valueOf(roleStr.toUpperCase());
            User newUser = new User(username, password, role);
            userRepository.save(newUser);

            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(302, -1);

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error", "text/plain");
        }
    }
}
