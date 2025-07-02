package com.myapp.aw.store.webserver.handlers;
import com.myapp.aw.store.webserver.handlers.HandlerUtils;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.UserRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
public class LoginHandler implements HttpHandler {
    private final UserRepository userRepository;
    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }
        try {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String, String> params = HandlerUtils.parseUrlEncodedFormData(formData);
            String username = params.get("username");
            String password = params.get("password");
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent() && userOpt.get().getPassword() != null && userOpt.get().getPassword().equals(password)) {
                HandlerUtils.redirect(exchange, "/admin-dashboard");
            } else {
                String response = "<html><body><h1>Login Failed</h1><p>Invalid username or password.</p><a href='/admin-login'>Try again</a></body></html>";
                HandlerUtils.sendResponse(exchange, 401, response, "text/html");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Internal Server Error", "text/plain");
        }
    }
}
