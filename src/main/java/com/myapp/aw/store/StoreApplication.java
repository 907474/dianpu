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
            System.out.println("Dashboard is running at: http://localhost:8080/");
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupInitialData(UserRepository userRepository, ProductRepository productRepository) throws Exception {
        System.out.println("[SETUP] Checking for initial data...");
        if (userRepository.findAll().isEmpty()) {
            userRepository.save(new User("admin", "admin123", Role.ADMIN));
            System.out.println("Created default 'admin' user.");
        }
        if (productRepository.findAll().isEmpty()) {
            System.out.println("Populating initial product inventory...");
            productRepository.save(new Product("FRT-001", "Apples", 0.79, 250));
            productRepository.save(new Product("FRT-002", "Bananas", 0.25, 500));
            productRepository.save(new Product("VEG-001", "Carrots", 1.29, 150));
            productRepository.save(new Product("DRY-001", "Bread", 3.49, 75));
            productRepository.save(new Product("DRY-002", "Pasta", 1.99, 200));
            productRepository.save(new Product("DAI-001", "Milk", 4.19, 60));
            productRepository.save(new Product("DAI-002", "Eggs", 3.29, 100));
            productRepository.save(new Product("DAI-003", "Cheese", 4.99, 80));
            productRepository.save(new Product("MEA-001", "Chicken", 5.99, 50));
            productRepository.save(new Product("MEA-002", "Beef", 6.49, 45));
            productRepository.save(new Product("DRY-003", "Cereal", 4.79, 90));
            productRepository.save(new Product("SNC-001", "Chips", 4.29, 120));
            productRepository.save(new Product("BEV-001", "Soda", 2.19, 150));
            productRepository.save(new Product("BEV-002", "Juice", 3.89, 70));
            productRepository.save(new Product("FRZ-001", "Pizza", 6.99, 40));
            productRepository.save(new Product("FRZ-002", "Peas", 2.49, 110));
            productRepository.save(new Product("CND-001", "Soup", 1.79, 200));
            productRepository.save(new Product("CND-002", "Tuna", 1.29, 180));
            productRepository.save(new Product("BAK-001", "Flour", 3.99, 50));
            productRepository.save(new Product("BAK-002", "Sugar", 3.49, 60));
            productRepository.save(new Product("HOU-001", "Paper Towels", 8.99, 30));
            productRepository.save(new Product("VEG-002", "Lettuce", 2.29, 65));
            productRepository.save(new Product("VEG-003", "Onions", 1.49, 120));
            productRepository.save(new Product("FRT-003", "Oranges", 0.99, 200));
            productRepository.save(new Product("DRY-004", "Rice", 7.99, 80));
            productRepository.save(new Product("DAI-004", "Yogurt", 1.19, 150));
            productRepository.save(new Product("SNC-002", "Cookies", 3.79, 100));
            productRepository.save(new Product("BEV-003", "Water", 5.99, 200));
            productRepository.save(new Product("HOU-002", "Soap", 2.99, 75));
            productRepository.save(new Product("PET-001", "Cat Food", 15.99, 25));
        }
    }
}
