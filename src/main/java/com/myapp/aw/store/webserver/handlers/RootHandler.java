package com.myapp.aw.store.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><style>" +
                "body { font-family: sans-serif; margin: 2em; }" +
                "h1 { color: #333; text-align: center; }" +
                ".nav-container { text-align: center; margin-bottom: 2em; }" +
                ".nav-link { display: inline-block; background-color: #007BFF; color: white; padding: 10px 15px; margin: 5px; border-radius: 5px; text-decoration: none; font-size: 1.1em; }" +
                ".nav-link-order { background-color: #28a745; }" +
                ".nav-link:hover { background-color: #0056b3; }" +
                ".nav-link-order:hover { background-color: #218838; }" +
                "</style></head><body>" +
                "<h1>Store Management Dashboard</h1>" +
                "<div class='nav-container'>" +
                "<a href='/order-form' class='nav-link nav-link-order'>Create New Order</a>" +
                "<a href='/products' class='nav-link'>View All Products</a>" +
                "<a href='/users' class='nav-link'>View All Users</a>" +
                "<a href='/orders' class='nav-link'>View All Orders</a>" +
                "</div>" +
                "</body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
