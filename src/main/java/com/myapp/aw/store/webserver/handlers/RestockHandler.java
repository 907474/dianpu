package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.service.ProductService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RestockHandler implements HttpHandler {
    private final ProductService productService;

    public RestockHandler(ProductService productService) {
        this.productService = productService;
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

            long productId = Long.parseLong(params.get("productId"));
            int amount = Integer.parseInt(params.get("amount"));

            productService.restockProduct(productId, amount);

            exchange.getResponseHeaders().set("Location", "/restock");
            exchange.sendResponseHeaders(302, -1);

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage(), "text/plain");
        }
    }
}
