package com.myapp.aw.store.webserver;

import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;
import com.myapp.aw.store.service.ProductService;
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

    public WebServer(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository, OrderService orderService, ProductService productService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Page handlers
        server.createContext("/", new RootHandler());
        server.createContext("/users", new HtmlUsersHandler(this.userRepository));
        server.createContext("/orders", new HtmlOrdersHandler(this.orderRepository));
        server.createContext("/order-form", new HtmlOrderFormHandler(this.productRepository));
        server.createContext("/checkout", new HtmlCheckoutHandler(this.productRepository));
        server.createContext("/restock", new HtmlRestockHandler(this.productRepository));

        // API handlers
        server.createContext("/api/orders/add", new AddOrderHandler(this.orderService, this.userRepository));
        server.createContext("/api/products/restock", new RestockHandler(this.productService));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080. Press Ctrl+C to stop.");
    }
}
