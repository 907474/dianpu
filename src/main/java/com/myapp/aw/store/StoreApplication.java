package com.myapp.aw.store;
import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.*;
import com.myapp.aw.store.repository.*;
import com.myapp.aw.store.service.*;
import com.myapp.aw.store.webserver.*;
public class StoreApplication {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        ProductRepository pr = new ProductRepository();
        UserRepository ur = new UserRepository();
        OrderRepository or = new OrderRepository();
        OrderService os = new OrderService(or, pr);
        ProductService ps = new ProductService(pr);
        SessionManager sm = new SessionManager();
        try {
            if (!ur.findByUsername("admin").isPresent()) {
                ur.save(new User("admin", "admin", Role.ADMIN));
            }
        } catch (Exception e) { e.printStackTrace(); }
        try {
            new WebServer(pr, ur, or, os, ps, sm).start();
            System.out.println("Server started on port 8080.");
        } catch (Exception e) { e.printStackTrace(); }
    }
}