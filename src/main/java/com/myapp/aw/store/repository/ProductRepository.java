package com.myapp.aw.store.repository;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (id, sku, name, price, stock) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET sku=excluded.sku, name=excluded.name, price=excluded.price, stock=excluded.stock";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (product.getId() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setLong(1, product.getId());
            }
            pstmt.setString(2, product.getSku());
            pstmt.setString(3, product.getName());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStock());
            pstmt.executeUpdate();

            if (product.getId() == 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
        }
        return product;
    }

    public Optional<Product> findById(long id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Product product = new Product(
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                product.setId(rs.getLong("id"));
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public List<Product> findByNameContaining(String name) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                product.setId(rs.getLong("id"));
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                product.setId(rs.getLong("id"));
                products.add(product);
            }
        }
        return products;
    }
}
