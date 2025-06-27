package com.myapp.aw.store.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;


public class Order {
    private long id;
    private long userId;
    private List<OrderItem> items;
    private double totalPrice;
    private LocalDateTime orderDate;
    private String status; // e.g., "COMPLETED", "CANCELLED"


    public Order(long userId, List<OrderItem> items) {
        this.userId = userId;
        this.items = items;
        this.orderDate = LocalDateTime.now();
        this.status = "COMPLETED"; // Default status
        this.totalPrice = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public Order(long id, long userId, double totalPrice, LocalDateTime orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.items = new ArrayList<>();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; } // Needed for loading from DB
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{id=").append(id)
                .append(", userId=").append(userId)
                .append(", totalPrice=$").append(String.format("%.2f", totalPrice))
                .append(", date=").append(orderDate.toLocalDate())
                .append(", status='").append(status).append("'\n");
        items.forEach(item -> sb.append(item.toString()).append("\n"));
        return sb.toString();
    }// h
}
