package com.myapp.aw.store.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><title>Login</title><style>" +
                "body { font-family: sans-serif; background-color: #f4f4f4; margin: 0; padding: 2em; }" +
                "h1 { text-align: center; color: #333; }" +
                ".login-container { display: flex; justify-content: space-around; align-items: flex-start; flex-wrap: wrap; }" +
                ".form-wrapper { background-color: white; padding: 2em; border: 1px solid #ddd; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); width: 100%; max-width: 400px; margin: 1em; }" +
                "h2 { text-align: center; margin-top: 0; }" +
                "input { width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }" +
                "input[type='submit'] { color: white; cursor: pointer; border: none; }" +
                ".customer-btn { background-color: #28a745; }" +
                ".admin-btn { background-color: #007BFF; }" +
                "</style></head><body>" +
                "<h1>Welcome to the Store</h1>" +
                "<div class='login-container'>" +

                "<div class='form-wrapper'>" +
                "<h2>Customer Login or Sign Up</h2>" +
                "<form action='/api/login' method='post'>" +
                "  <input type='hidden' name='login_type' value='customer'>" +
                "  <label for='cust_username'>Enter Username:</label><br>" +
                "  <input type='text' id='cust_username' name='username' required><br><br>" +
                "  <input type='submit' value='Login / Register to Order' class='customer-btn'>" +
                "</form>" +
                "</div>" +

                "<div class='form-wrapper'>" +
                "<h2>Admin / Employee Login</h2>" +
                "<form action='/api/login' method='post'>" +
                "  <input type='hidden' name='login_type' value='admin'>" +
                "  <label for='admin_username'>Username:</label><br>" +
                "  <input type='text' id='admin_username' name='username' required><br>" +
                "  <label for='password'>Password:</label><br>" +
                "  <input type='password' id='password' name='password' required><br><br>" +
                "  <input type='submit' value='Login' class='admin-btn'>" +
                "</form>" +
                "</div>" +

                "</div></body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
