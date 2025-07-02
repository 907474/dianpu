package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.webserver.handlers.HandlerUtils;
import com.myapp.aw.store.service.ProductService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UpdateProductHandler implements HttpHandler {
    private final ProductService productService;

    public UpdateProductHandler(ProductService productService) {
        this.productService = productService;
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

            long productId = Long.parseLong(params.get("productId"));
            String sku = params.get("sku");
            String name = params.get("name");
            double price = Double.parseDouble(params.get("price"));
            int stock = Integer.parseInt(params.get("stock"));

            productService.updateProduct(productId, sku, name, price, stock);
            HandlerUtils.redirect(exchange, "/restock");

        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Error updating product: " + e.getMessage(), "text/html");
        }
    }
}
