package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class HtmlProductsHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public HtmlProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            List<Product> products = productRepository.findAll();
            // This method call requires the corresponding method in HandlerUtils
            String tableHtml = HandlerUtils.convertProductListToHtmlTable(products);

            // Assemble the full page
            String htmlResponse = "<html><head><title>All Products</title><style>" +
                    "body { font-family: sans-serif; margin: 2em; }" +
                    "table { width: 100%; border-collapse: collapse; }" +
                    "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                    "th { background-color: #f2f2f2; }" +
                    "a { color: #007BFF; text-decoration: none; }" +
                    "</style></head><body>" +
                    "<h1>All Products</h1>" +
                    "<p><a href='/admin-dashboard'>&larr; Back to Admin Dashboard</a></p>" +
                    tableHtml +
                    "</body></html>";

            HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "<h1>Error</h1><p>Could not fetch products.</p>";
            HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
        }
    }
}
