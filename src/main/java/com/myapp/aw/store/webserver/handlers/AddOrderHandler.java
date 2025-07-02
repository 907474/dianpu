package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.User;
import com.myapp.aw.store.service.OrderService;
import com.myapp.aw.store.webserver.SessionManager;
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
    private final SessionManager sessionManager;

    public AddOrderHandler(OrderService orderService, SessionManager sessionManager) {
        this.orderService = orderService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<User> userOpt = sessionManager.getSessionUser(exchange);
        if (!userOpt.isPresent()) {
            HandlerUtils.sendResponse(exchange, 401, "Unauthorized", "text/plain");
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        try {
            String formData = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).readLine();
            Map<String, String> params = HandlerUtils.parseUrlEncodedFormData(formData);
            String orderData = params.get("orderData");

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

            orderService.createOrder(userOpt.get().getId(), productQuantities);

            // Redirect to the new confirmation page
            HandlerUtils.redirect(exchange, "/order-confirmed");

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage(), "text/plain");
        }
    }
}
