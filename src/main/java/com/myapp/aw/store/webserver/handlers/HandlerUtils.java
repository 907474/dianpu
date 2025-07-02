package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HandlerUtils {

    private HandlerUtils() {}

    public static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    public static Map<String, String> parseGetQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
                map.put(key, value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static String convertProductListToHtmlTable(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>SKU</th><th>Name</th><th>Price</th><th>Stock</th></tr>");
        for (Product p : products) {
            sb.append("<tr>");
            sb.append("<td>").append(p.getId()).append("</td>");
            sb.append("<td>").append(escapeHtml(p.getSku())).append("</td>");
            sb.append("<td>").append(escapeHtml(p.getName())).append("</td>");
            sb.append("<td>").append(String.format("$%.2f", p.getPrice())).append("</td>");
            sb.append("<td>").append(p.getStock()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static String convertProductListToOrderFormTable(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>SKU</th><th>Name</th><th>Price</th><th>Stock</th><th>Quantity to Order</th></tr>");
        for (Product p : products) {
            sb.append("<tr>");
            sb.append("<td>").append(p.getId()).append("</td>");
            sb.append("<td>").append(escapeHtml(p.getSku())).append("</td>");
            sb.append("<td>").append(escapeHtml(p.getName())).append("</td>");
            sb.append("<td>").append(String.format("$%.2f", p.getPrice())).append("</td>");
            sb.append("<td>").append(p.getStock()).append("</td>");
            sb.append("<td><input type='number' class='quantity-input' name='quantity_").append(p.getId()).append("' min='0' placeholder='0'></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static String convertUserListToHtmlTable(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>All Users</h1>");
        sb.append("<p><a href='/admin-dashboard'>&larr; Back to Admin Dashboard</a></p>");
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>Username</th><th>Role</th></tr>");
        for (User u : users) {
            sb.append("<tr>");
            sb.append("<td>").append(u.getId()).append("</td>");
            sb.append("<td>").append(escapeHtml(u.getUsername())).append("</td>");
            sb.append("<td>").append(u.getRole()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static String convertOrderListToHtmlTable(List<Order> orders) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>All Orders</h1>");
        sb.append("<p><a href='/admin-dashboard'>&larr; Back to Admin Dashboard</a></p>");

        if (orders.isEmpty()) {
            sb.append("<p>No orders found.</p>");
        } else {
            for (Order o : orders) {
                sb.append("<div class='order-card'>");
                sb.append("<div class='order-header'>Order #").append(o.getId())
                        .append(" | User ID: ").append(o.getUserId())
                        .append(" | Total: ").append(String.format("$%.2f", o.getTotalPrice()))
                        .append(" | Date: ").append(o.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .append("</div>");
                sb.append("<table>");
                sb.append("<tr><th>Product Name</th><th>Quantity</th><th>Price at Purchase</th><th>Subtotal</th></tr>");
                for (OrderItem item : o.getItems()) {
                    sb.append("<tr>");
                    sb.append("<td>").append(escapeHtml(item.getProductName())).append("</td>");
                    sb.append("<td>").append(item.getQuantity()).append("</td>");
                    sb.append("<td>").append(String.format("$%.2f", item.getPriceAtPurchase())).append("</td>");
                    sb.append("<td>").append(String.format("$%.2f", item.getSubtotal())).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</table></div>");
            }
        }
        return sb.toString();
    }

    public static String convertProductListToJson(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            sb.append("{");
            sb.append("\"id\":").append(p.getId()).append(",");
            sb.append("\"sku\":\"").append(escapeJson(p.getSku())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
            sb.append("\"price\":").append(p.getPrice()).append(",");
            sb.append("\"stock\":").append(p.getStock());
            sb.append("}");
            if (i < products.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static Map<String, String> parseUrlEncodedFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) {
            return map;
        }
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            map.put(key, value);
        }
        return map;
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        if (contentType != null) {
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=" + StandardCharsets.UTF_8.name());
        }
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    public static String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public static String escapeJson(String value) {
        if (value == null) return "null";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
