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
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                List<Product> products = productRepository.findAll();
                String jsonResponse = HandlerUtils.convertProductListToJson(products);
                HandlerUtils.sendResponse(exchange, 200, jsonResponse, "application/json");
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "{\"error\":\"Could not fetch products from database.\"}";
                HandlerUtils.sendResponse(exchange, 500, errorResponse, "application/json");
            }
        } else {
            HandlerUtils.sendResponse(exchange, 405, "", null);
        }
    }
}
