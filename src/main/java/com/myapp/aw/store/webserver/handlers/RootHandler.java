package com.myapp.aw.store.webserver.handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><title>Welcome</title><style>" +
                "body { font-family: sans-serif; text-align: center; padding-top: 5em; }" +
                "h1 { color: #333; }" +
                ".order-btn { display: inline-block; background-color: #28a745; color: white; padding: 20px 40px; margin: 20px; border-radius: 5px; text-decoration: none; font-size: 1.5em; }" +
                ".order-btn:hover { background-color: #218838; }" +
                ".admin-login-link { display: block; margin-top: 3em; }" +
                "</style></head><body>" +
                "<h1>Welcome to Our Store!</h1>" +
                "<a href='/order-form' class='order-btn'>Place an Order</a>" +
                "<br>" +
                "<a href='/admin-login' class='admin-login-link'>Sign in as Admin</a>" +
                "</body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
