package com.myapp.aw.store.webserver;

import com.myapp.aw.store.model.*;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;
import com.myapp.aw.store.service.ProductService;
import com.myapp.aw.store.webserver.handlers.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WebServer {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public WebServer(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository, OrderService orderService, ProductService productService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/admin-login", new AdminLoginHandler());
        server.createContext("/admin-dashboard", new AdminDashboardHandler());
        server.createContext("/users", new HtmlUsersHandler(userRepository));
        server.createContext("/orders", new HtmlOrdersHandler(orderRepository));
        server.createContext("/restock", new HtmlRestockHandler(productRepository));
        server.createContext("/order-form", new HtmlOrderFormHandler(productRepository));
        server.createContext("/checkout", new HtmlCheckoutHandler(productRepository));

        server.createContext("/api/login", new LoginHandler(userRepository));
        server.createContext("/api/orders/add", new AddOrderHandler(orderService, userRepository));
        server.createContext("/api/products/restock", new RestockHandler(productService));

        server.setExecutor(null);
        server.start();
    }

    // --- Handlers as Inner Classes ---

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><head><title>Welcome</title><style>" + "body{font-family:sans-serif;text-align:center;padding-top:5em}h1{color:#333}.order-btn{display:inline-block;background-color:#28a745;color:white;padding:20px 40px;margin:20px;border-radius:5px;text-decoration:none;font-size:1.5em}.order-btn:hover{background-color:#218838}.admin-login-link{display:block;margin-top:3em}</style></head><body>" + "<h1>Welcome to Our Store!</h1><a href='/order-form' class='order-btn'>Place an Order</a><br><a href='/admin-login' class='admin-login-link'>Sign in as Admin</a></body></html>";
            sendResponse(exchange, 200, response, "text/html");
        }
    }

    static class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><head><title>Admin Login</title><style>" + "body{font-family:sans-serif;display:flex;justify-content:center;align-items:center;height:100vh;background-color:#f4f4f4}form{background-color:white;padding:2em;border:1px solid #ddd;border-radius:5px;box-shadow:0 2px 4px rgba(0,0,0,0.1)}h1{text-align:center}input{width:100%;padding:8px;margin-bottom:10px;border:1px solid #ccc;border-radius:4px;box-sizing:border-box}input[type='submit']{background-color:#007BFF;color:white;cursor:pointer}</style></head><body>" + "<form action='/api/login' method='post'><h1>Admin/Employee Login</h1><label for='username'>Username:</label><br><input type='text' id='username' name='username' required><br><label for='password'>Password:</label><br><input type='password' id='password' name='password' required><br><br><input type='submit' value='Login'></form></body></html>";
            sendResponse(exchange, 200, response, "text/html");
        }
    }

    static class AdminDashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><head><title>Admin Dashboard</title><style>" + "body{font-family:sans-serif;margin:2em}h1{color:#333;text-align:center}.nav-container{text-align:center;margin-bottom:2em}.nav-link{display:inline-block;background-color:#007BFF;color:white;padding:10px 15px;margin:5px;border-radius:5px;text-decoration:none;font-size:1.1em}.nav-link-restock{background-color:#ffc107;color:#212529}.nav-link:hover{opacity:0.8}</style></head><body>" + "<h1>Store Management Dashboard</h1><div class='nav-container'><a href='/restock' class='nav-link nav-link-restock'>Restock Products</a><a href='/users' class='nav-link'>View All Users</a><a href='/orders' class='nav-link'>View All Orders</a></div></body></html>";
            sendResponse(exchange, 200, response, "text/html");
        }
    }

    static class LoginHandler implements HttpHandler {
        private final UserRepository userRepository;
        LoginHandler(UserRepository userRepository) { this.userRepository = userRepository; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) { sendResponse(exchange, 405, "", "text/plain"); return; }
            try {
                String formData = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).readLine();
                Map<String, String> params = parseUrlEncodedFormData(formData);
                Optional<User> userOpt = userRepository.findByUsername(params.get("username"));
                if (userOpt.isPresent() && userOpt.get().getPassword() != null && userOpt.get().getPassword().equals(params.get("password"))) {
                    redirect(exchange, "/admin-dashboard");
                } else {
                    sendResponse(exchange, 401, "<h1>Login Failed</h1><p>Invalid username or password.</p><a href='/admin-login'>Try again</a>", "text/html");
                }
            } catch (Exception e) { e.printStackTrace(); sendResponse(exchange, 500, "Internal Server Error", "text/plain"); }
        }
    }

    static class HtmlUsersHandler implements HttpHandler {
        private final UserRepository userRepository;
        HtmlUsersHandler(UserRepository userRepository) { this.userRepository = userRepository; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                sendResponse(exchange, 200, convertUserListToHtmlTable(userRepository.findAll()), "text/html");
            } catch (Exception e) { e.printStackTrace(); sendResponse(exchange, 500, "<h1>Error</h1>", "text/html"); }
        }
    }

    static class HtmlOrdersHandler implements HttpHandler {
        private final OrderRepository orderRepository;
        HtmlOrdersHandler(OrderRepository orderRepository) { this.orderRepository = orderRepository; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                sendResponse(exchange, 200, convertOrderListToHtmlTable(orderRepository.findAll()), "text/html");
            } catch (Exception e) { e.printStackTrace(); sendResponse(exchange, 500, "<h1>Error</h1>", "text/html"); }
        }
    }

    // ... other handlers like HtmlRestockHandler, HtmlOrderFormHandler, etc. would follow the same pattern ...

    // --- Utility Methods moved inside WebServer ---

    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        if (contentType != null) {
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=" + StandardCharsets.UTF_8.name());
        }
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private static Map<String, String> parseUrlEncodedFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;
        for (String pair : formData.split("&")) {
            int idx = pair.indexOf("=");
            map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return map;
    }

    private static String convertUserListToHtmlTable(List<User> users) {
        StringBuilder sb = new StringBuilder("<html><head><title>All Users</title><style>body{font-family:sans-serif;margin:2em}table{width:100%;border-collapse:collapse}th,td{border:1px solid #ddd;padding:8px;text-align:left}th{background-color:#f2f2f2}a{color:#007BFF;text-decoration:none}</style></head><body><h1>All Users</h1><p><a href='/'>&larr; Back to Dashboard</a></p><table><tr><th>ID</th><th>Username</th><th>Role</th></tr>");
        for (User u : users) {
            sb.append("<tr><td>").append(u.getId()).append("</td><td>").append(escapeHtml(u.getUsername())).append("</td><td>").append(u.getRole()).append("</td></tr>");
        }
        return sb.append("</table></body></html>").toString();
    }

    private static String convertOrderListToHtmlTable(List<Order> orders) {
        StringBuilder sb = new StringBuilder("<html><head><title>All Orders</title><style>body{font-family:sans-serif;margin:2em}.order-card{border:1px solid #ccc;border-radius:5px;margin-bottom:1em;padding:1em}.order-header{font-weight:bold;margin-bottom:.5em}table{width:100%;border-collapse:collapse;margin-top:.5em}th,td{border:1px solid #ddd;padding:8px;text-align:left}th{background-color:#f2f2f2}a{color:#007BFF;text-decoration:none}</style></head><body><h1>All Orders</h1><p><a href='/'>&larr; Back to Dashboard</a></p>");
        if (orders.isEmpty()) sb.append("<p>No orders found.</p>");
        else {
            for (Order o : orders) {
                sb.append("<div class='order-card'><div class='order-header'>Order #").append(o.getId()).append(" | User ID: ").append(o.getUserId()).append(" | Total: ").append(String.format("$%.2f", o.getTotalPrice())).append(" | Date: ").append(o.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("</div><table><tr><th>Product Name</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>");
                for (OrderItem item : o.getItems()) {
                    sb.append("<tr><td>").append(escapeHtml(item.getProductName())).append("</td><td>").append(item.getQuantity()).append("</td><td>").append(String.format("$%.2f", item.getPriceAtPurchase())).append("</td><td>").append(String.format("$%.2f", item.getSubtotal())).append("</td></tr>");
                }
                sb.append("</table></div>");
            }
        }
        return sb.append("</body></html>").toString();
    }

    private static String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
