package com.myapp.aw.store.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><style>" +
                "body { font-family: sans-serif; margin: 2em; }" +
                "h1, h2 { color: #333; text-align: center; }" +
                "a { text-decoration: none; color: #007BFF; }" +
                ".nav-container { text-align: center; margin-bottom: 2em; }" +
                ".nav-link { display: inline-block; background-color: #007BFF; color: white; padding: 10px 15px; margin: 5px; border-radius: 5px; text-decoration: none; }" +
                ".nav-link:hover { background-color: #0056b3; }" +
                "form { background-color: #f9f9f9; padding: 1.5em; border: 1px solid #ddd; border-radius: 5px; max-width: 350px; margin: auto; }" +
                "input[type='text'], input[type='password'], select { width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }" +
                "input[type='submit'] { background-color: #28a745; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; width: 100%; }" +
                "input[type='submit']:hover { background-color: #218838; }" +
                "</style></head><body>" +
                "<h1>Store Management Dashboard</h1>" +
                "<div class='nav-container'>" +
                "<a href='/products' class='nav-link'>View & Search Products</a>" +
                "<a href='/users' class='nav-link'>View All Users</a>" +
                "<a href='/orders' class='nav-link'>View All Orders</a>" +
                "</div>" +
                "<hr>" +
                "<div>" +
                "<h2>Add a New User</h2>" +
                "<form action='/api/users/add' method='post'>" +
                "  <label for='username'>Username:</label><br>" +
                "  <input type='text' id='username' name='username' required><br>" +
                "  <label for='password'>Password:</label><br>" +
                "  <input type='password' id='password' name='password' required><br>" +
                "  <label for='role'>Role:</label><br>" +
                "  <select id='role' name='role'>" +
                "    <option value='EMPLOYEE'>Employee</option>" +
                "    <option value='ADMIN'>Admin</option>" +
                "  </select><br><br>" +
                "  <input type='submit' value='Create User'>" +
                "</form>" +
                "</div>" +
                "</body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
