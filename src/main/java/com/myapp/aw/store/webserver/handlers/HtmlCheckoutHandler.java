package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HtmlCheckoutHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public HtmlCheckoutHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = HandlerUtils.parseGetQuery(query);
        Map<Product, Integer> orderItems = new HashMap<>();
        double totalPrice = 0;

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("quantity_")) {
                    long productId = Long.parseLong(entry.getKey().substring("quantity_".length()));
                    int quantity = Integer.parseInt(entry.getValue());
                    Optional<Product> productOpt = productRepository.findById(productId);
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        orderItems.put(product, quantity);
                        totalPrice += product.getPrice() * quantity;
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            HandlerUtils.sendResponse(exchange, 500, "Error processing order items.", "text/plain");
            return;
        }

        String htmlResponse = generateCheckoutPage(orderItems, totalPrice, query);
        HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");
    }

    private String generateCheckoutPage(Map<Product, Integer> orderItems, double totalPrice, String originalQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Checkout</title><style>");
        sb.append("body { font-family: sans-serif; margin: 2em; }");
        sb.append("table { width: 100%; max-width: 600px; border-collapse: collapse; margin-top: 1em; }");
        sb.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        sb.append("th { background-color: #f2f2f2; }");
        sb.append(".total-row td { font-weight: bold; font-size: 1.2em; }");
        sb.append("form { margin-top: 2em; }");
        sb.append("input[type='text'] { padding: 8px; width: 300px; margin-bottom: 1em; }");
        sb.append(".action-btn { display: inline-block; text-decoration: none; color: white !important; padding: 10px 15px; border: none; cursor: pointer; border-radius: 4px; margin-right: 10px; font-size: 1em; }");
        sb.append(".confirm-btn { background-color: #28a745; }");
        sb.append(".edit-btn { background-color: #ffc107; color: #212529 !important; }");
        sb.append(".action-btn:hover { opacity: 0.8; }");
        sb.append("</style></head><body>");
        sb.append("<h1>Confirm Your Order</h1>");
        sb.append("<h3>Order Summary</h3>");

        sb.append("<table><tr><th>Product</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>");
        for(Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
            Product p = entry.getKey();
            int q = entry.getValue();
            sb.append("<tr>");
            sb.append("<td>").append(HandlerUtils.escapeHtml(p.getName())).append("</td>");
            sb.append("<td>").append(q).append("</td>");
            sb.append("<td>").append(String.format("$%.2f", p.getPrice())).append("</td>");
            sb.append("<td>").append(String.format("$%.2f", p.getPrice() * q)).append("</td>");
            sb.append("</tr>");
        }
        sb.append("<tr class='total-row'><td colspan='3'>Total</td><td>").append(String.format("$%.2f", totalPrice)).append("</td></tr>");
        sb.append("</table>");

        sb.append("<form action='/api/orders/add' method='post'>");
        sb.append("<input type='hidden' name='orderData' value='").append(HandlerUtils.escapeHtml(originalQuery)).append("'>");
        sb.append("<label for='username'>Enter your Username (optional, leave blank for guest checkout):</label><br>");
        sb.append("<input type='text' name='username' id='username'><br><br>");
        sb.append("<div>");
        sb.append("<button type='submit' class='action-btn confirm-btn'>Confirm and Place Order</button>");
        sb.append("<a href='/order-form' class='action-btn edit-btn'>Back to Edit Order</a>");
        sb.append("</div>");
        sb.append("</form>");

        sb.append("</body></html>");
        return sb.toString();
    }
}
