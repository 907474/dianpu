package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.service.OrderService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddOrderHandler implements HttpHandler {
    private final OrderService orderService;

    public AddOrderHandler(OrderService orderService) {
        this.orderService = orderService;
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

            long userId = Long.parseLong(params.get("userId"));

            Map<Long, Integer> productQuantities = new HashMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.startsWith("quantity_") && value != null && !value.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(value);
                        if (quantity > 0) {
                            long productId = Long.parseLong(key.substring("quantity_".length()));
                            productQuantities.put(productId, quantity);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }

            if (productQuantities.isEmpty()) {
                HandlerUtils.sendResponse(exchange, 400, "Bad Request: No products with a valid quantity were selected.", "text/plain");
                return;
            }

            orderService.createOrder(userId, productQuantities);

            exchange.getResponseHeaders().set("Location", "/orders");
            exchange.sendResponseHeaders(302, -1);

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage(), "text/plain");
        }
    }
}
