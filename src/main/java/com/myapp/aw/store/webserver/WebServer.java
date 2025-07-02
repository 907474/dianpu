package com.myapp.aw.store.webserver;
import com.myapp.aw.store.repository.*;
import com.myapp.aw.store.service.*;
import com.myapp.aw.store.webserver.handlers.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
public class WebServer {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final SessionManager sessionManager;
    public WebServer(ProductRepository pr, UserRepository ur, OrderRepository or, OrderService os, ProductService ps, SessionManager sm) {
        this.productRepository = pr; this.userRepository = ur; this.orderRepository = or;
        this.orderService = os; this.productService = ps; this.sessionManager = sm;
    }
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/api/login", new LoginHandler(userRepository, sessionManager));
        server.createContext("/logout", new LogoutHandler(sessionManager));
        server.createContext("/admin-dashboard", new AdminDashboardHandler(sessionManager));
        server.createContext("/order-form", new HtmlOrderFormHandler(productRepository, sessionManager));
        server.createContext("/checkout", new HtmlCheckoutHandler(productRepository));
        server.createContext("/api/orders/add", new AddOrderHandler(orderService, sessionManager));
        server.createContext("/orders", new HtmlOrdersHandler(orderRepository));
        server.createContext("/restock", new HtmlRestockHandler(productRepository));
        server.createContext("/users", new HtmlUsersHandler(userRepository));
        server.createContext("/api/products/restock", new RestockHandler(productService));
        server.setExecutor(null);
        server.start();
    }
}
