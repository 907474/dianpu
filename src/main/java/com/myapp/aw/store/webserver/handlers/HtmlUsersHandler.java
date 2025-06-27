package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.UserRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class HtmlUsersHandler implements HttpHandler {
    private final UserRepository userRepository;

    public HtmlUsersHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                List<User> users = userRepository.findAll();
                String htmlResponse = HandlerUtils.convertUserListToHtmlTable(users);
                HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "<h1>Error</h1><p>Could not fetch users from database.</p>";
                HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
            }
        } else {
            HandlerUtils.sendResponse(exchange, 405, "", null);
        }
    }
}
