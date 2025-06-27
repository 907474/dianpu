package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    public Order createOrder(long userId, Map<Long, Integer> productQuantities) throws Exception {
        List<OrderItem> orderItems = new ArrayList<>();

        // Step 1: Validate stock and build the list of order items
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Validation failed: Product with ID " + productId + " not found."));

            if (product.getStock() < quantity) {
                throw new Exception("Validation failed: Insufficient stock for '" + product.getName() +
                        "'. Requested: " + quantity + ", Available: " + product.getStock());
            }
            orderItems.add(new OrderItem(product, quantity));
        }

        Order newOrder = new Order(userId, orderItems);

        orderRepository.save(newOrder);

        for (OrderItem item : newOrder.getItems()) {
            Product productToUpdate = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Data consistency error: Product with ID " + item.getProductId() + " was not found after validation."));

            productToUpdate.setStock(productToUpdate.getStock() - item.getQuantity());
            productRepository.save(productToUpdate);
        }

        return newOrder;
    }
}
