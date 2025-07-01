package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AddOrderHandler implements HttpHandler {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public AddOrderHandler(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
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
            String orderData = params.get("orderData");

            if (orderData == null || orderData.isEmpty()) {
                HandlerUtils.sendResponse(exchange, 400, "Bad Request: Order data is missing.", "text/plain");
                return;
            }

            if (username == null || username.trim().isEmpty()) {
                username = "guest-" + System.currentTimeMillis();
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                user = new User(username, "", Role.CUSTOMER);
                userRepository.save(user);
            }

            Map<String, String> orderParams = HandlerUtils.parseGetQuery(orderData);
            Map<Long, Integer> productQuantities = new HashMap<>();
            for (Map.Entry<String, String> entry : orderParams.entrySet()) {
                if (entry.getKey().startsWith("quantity_")) {
                    long productId = Long.parseLong(entry.getKey().substring("quantity_".length()));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        productQuantities.put(productId, quantity);
                    }
                }
            }

            if (productQuantities.isEmpty()) {
                HandlerUtils.sendResponse(exchange, 400, "Bad Request: No products in order.", "text/plain");
                return;
            }

            orderService.createOrder(user.getId(), productQuantities);

            exchange.getResponseHeaders().set("Location", "/orders");
            exchange.sendResponseHeaders(302, -1);

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage(), "text/plain");
        }
    }
}
