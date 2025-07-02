package com.myapp.aw.store.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class OrderConfirmedHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><title>Order Confirmed</title>" +
                "<meta http-equiv='refresh' content='3;url=/order-form'>" +
                "<style>" +
                "body { font-family: sans-serif; text-align: center; padding-top: 5em; }" +
                "h1 { color: #28a745; }" +
                "</style></head><body>" +
                "<h1>Order Confirmed!</h1>" +
                "<p>Your order has been placed successfully.</p>" +
                "<p>You will be redirected back to the order page shortly.</p>" +
                "<p>If you are not redirected, <a href='/order-form'>click here</a>.</p>" +
                "<script>" +
                "localStorage.removeItem('shoppingCart');" +
                "</script>" +

                "</body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
