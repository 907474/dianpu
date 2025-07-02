package com.myapp.aw.store;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;
import com.myapp.aw.store.service.ProductService;
import com.myapp.aw.store.webserver.WebServer;

public class StoreApplication {

    public static void main(String[] args) {
        System.out.println("--- Store Management System Initializing ---");

        DatabaseManager.initializeDatabase();

        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        OrderService orderService = new OrderService(orderRepository, productRepository);
        ProductService productService = new ProductService(productRepository);

        try {
            setupInitialData(userRepository, productRepository);
        } catch (Exception e) {
            System.err.println("Error during initial data setup: " + e.getMessage());
        }

        try {
            WebServer server = new WebServer(productRepository, userRepository, orderRepository, orderService, productService);
            server.start();
            System.out.println("Server started on port 8080. Press Ctrl+C to stop.");
            System.out.println("Homepage running at: http://localhost:8080/");
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupInitialData(UserRepository userRepository, ProductRepository productRepository) throws Exception {
        System.out.println("[SETUP] Checking for initial data...");
        if (!userRepository.findByUsername("admin").isPresent()) {
            userRepository.save(new User("admin", "admin", Role.ADMIN));
            System.out.println("Created default 'admin' user with password 'admin'.");
        }

        if (productRepository.findAll().isEmpty()) {
            System.out.println("Populating initial product inventory...");
            productRepository.save(new Product("FRT-001", "Apples", 0.79, 250));
            productRepository.save(new Product("FRT-002", "Bananas", 0.25, 500));
            productRepository.save(new Product("VEG-001", "Carrots", 1.29, 150));
            productRepository.save(new Product("DRY-001", "Bread", 3.49, 75));
        }
    }
}
