package com.myapp.aw.store.webserver.handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
public class AdminLoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><head><title>Admin Login</title><style>" + "body{font-family:sans-serif;display:flex;justify-content:center;align-items:center;height:100vh;background-color:#f4f4f4}form{background-color:white;padding:2em;border:1px solid #ddd;border-radius:5px;box-shadow:0 2px 4px rgba(0,0,0,0.1)}h1{text-align:center}input{width:100%;padding:8px;margin-bottom:10px;border:1px solid #ccc;border-radius:4px;box-sizing:border-box}input[type='submit']{background-color:#007BFF;color:white;cursor:pointer}</style></head><body>" + "<form action='/api/login' method='post'><h1>Admin/Employee Login</h1><label for='username'>Username:</label><br><input type='text' id='username' name='username' required><br><label for='password'>Password:</label><br><input type='password' id='password' name='password' required><br><br><input type='submit' value='Login'></form></body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}
