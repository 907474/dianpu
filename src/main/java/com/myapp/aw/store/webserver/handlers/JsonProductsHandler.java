package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class JsonProductsHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public JsonProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "", null);
            return;
        }

        try {
            List<Product> products = productRepository.findAll();
            // FIX: The JSON conversion logic is now inside this class.
            String jsonResponse = convertProductListToJson(products);
            HandlerUtils.sendResponse(exchange, 200, jsonResponse, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "{\"error\":\"Could not fetch products from database.\"}";
            HandlerUtils.sendResponse(exchange, 500, errorResponse, "application/json");
        }
    }

    /**
     * Converts a list of Product objects to a JSON array string.
     * This method is now self-contained within this handler.
     * @param products The list of products to convert.
     * @return A JSON formatted string.
     */
    private String convertProductListToJson(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            sb.append("{");
            sb.append("\"id\":").append(p.getId()).append(",");
            sb.append("\"sku\":\"").append(escapeJson(p.getSku())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
            sb.append("\"price\":").append(p.getPrice()).append(",");
            sb.append("\"stock\":").append(p.getStock());
            sb.append("}");
            if (i < products.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Escapes characters in a string to be JSON-safe.
     */
    private String escapeJson(String value) {
        if (value == null) return "null";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
