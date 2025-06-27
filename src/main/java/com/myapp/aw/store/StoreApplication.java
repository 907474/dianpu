package com.myapp.aw.store;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.webserver.WebServer;

public class StoreApplication {

    public static void main(String[] args) {
        System.out.println("--- Store Management System Initializing ---");

        DatabaseManager.initializeDatabase();

        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        try {
            setupInitialData(userRepository, productRepository);
        } catch (Exception e) {
            System.err.println("Error during initial data setup: " + e.getMessage());
        }

        try {
            WebServer server = new WebServer(productRepository, userRepository, orderRepository);
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
            productRepository.save(new Product("FRT-APL-001", "Apple", 0.79, 250));
            productRepository.save(new Product("FRT-BAN-001", "Banana", 0.25, 500));
            productRepository.save(new Product("VEG-CAR-001", "Carrots", 1.29, 150));
            productRepository.save(new Product("DRY-BRD-001", "Bread", 3.49, 75));
            productRepository.save(new Product("DRY-PAS-001", "Pasta", 1.99, 200));
            productRepository.save(new Product("DAI-MLK-001", "Milk", 4.19, 60));
            productRepository.save(new Product("DAI-EGG-001", "Eggs", 3.29, 100));
            productRepository.save(new Product("DAI-CHS-001", "Cheese", 4.99, 80));
            productRepository.save(new Product("MEA-CKN-001", "Chicken", 5.99, 50));
            productRepository.save(new Product("MEA-GRB-001", "Beef", 6.49, 45));
            productRepository.save(new Product("DRY-CER-001", "Cereal", 4.79, 90));
            productRepository.save(new Product("SNC-CHP-001", "Potato", 4.29, 120));
            productRepository.save(new Product("BEV-SDA-001", "Soda", 2.19, 150));
            productRepository.save(new Product("BEV-JUC-001", "Orange", 3.89, 70));
            productRepository.save(new Product("FRZ-PIZ-001", "Pizza", 6.99, 40));
            productRepository.save(new Product("FRZ-VEG-001", "Peas", 2.49, 110));
            productRepository.save(new Product("CND-SOUP-001", "Tomato", 1.79, 200));
            productRepository.save(new Product("CND-TNA-001", "Tuna", 1.29, 180));
            productRepository.save(new Product("BAK-FLR-001", "Flour", 3.99, 50));
            productRepository.save(new Product("BAK-SGR-001", "Sugar", 3.49, 60));
            productRepository.save(new Product("HOU-PPR-001", "Paper", 8.99, 30));
        }
    }
}
