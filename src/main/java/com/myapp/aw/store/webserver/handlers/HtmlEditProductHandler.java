
package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class HtmlEditProductHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public HtmlEditProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = HandlerUtils.parseGetQuery(exchange.getRequestURI().getQuery());
            long productId = Long.parseLong(params.get("id"));
            Optional<Product> productOpt = productRepository.findById(productId);

            if (productOpt.isPresent()) {
                String htmlResponse = HandlerUtils.generateEditProductForm(productOpt.get());
                HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");
            } else {
                HandlerUtils.sendResponse(exchange, 404, "<h1>Product Not Found</h1>", "text/html");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "<h1>Internal Server Error</h1>", "text/html");
        }
    }
}
