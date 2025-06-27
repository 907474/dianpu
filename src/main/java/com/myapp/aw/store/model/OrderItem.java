package com.myapp.aw.store.model;


public class OrderItem {
    private long productId;
    private String productName;
    private int quantity;
    private double priceAtPurchase;

    public OrderItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.priceAtPurchase = product.getPrice();
    }

    public OrderItem(long productId, String productName, int quantity, double priceAtPurchase) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPriceAtPurchase() { return priceAtPurchase; }
    public double getSubtotal() { return quantity * priceAtPurchase; }

    @Override
    public String toString() {
        return "  - Item: " + productName +
                ", Qty: " + quantity +
                ", Price: $" + String.format("%.2f", priceAtPurchase) +
                ", Subtotal: $" + String.format("%.2f", getSubtotal());
    }
}
