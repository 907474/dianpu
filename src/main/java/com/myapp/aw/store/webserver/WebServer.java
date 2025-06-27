package com.myapp.aw.store.webserver;

import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.webserver.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public WebServer(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/products", new HtmlProductsHandler(this.productRepository));
        server.createContext("/users", new HtmlUsersHandler(this.userRepository));
        server.createContext("/orders", new HtmlOrdersHandler(this.orderRepository));
        server.createContext("/api/products", new JsonProductsHandler(this.productRepository));
        server.createContext("/api/users/add", new UserAddHandler(this.userRepository));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080. Press Ctrl+C to stop.");
    }
}
