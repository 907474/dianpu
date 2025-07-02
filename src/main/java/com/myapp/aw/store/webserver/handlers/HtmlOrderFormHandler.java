package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HtmlOrderFormHandler implements HttpHandler {
    private final ProductRepository productRepository;
    private final SessionManager sessionManager;

    public HtmlOrderFormHandler(ProductRepository productRepository, SessionManager sessionManager) {
        this.productRepository = productRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<User> userOpt = sessionManager.getSessionUser(exchange);

        if (!userOpt.isPresent()) {
            HandlerUtils.redirect(exchange, "/"); // Redirect to login if not logged in
            return;
        }

        if (!"GET".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "", null);
            return;
        }

        try {
            List<Product> products = productRepository.findAll();
            String htmlResponse = generateOrderFormPage(products, userOpt.get());
            HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "<h1>Error</h1><p>Could not fetch products for order form.</p>";
            HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
        }
    }

    private String generateOrderFormPage(List<Product> products, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Create Order</title><style>");
        sb.append("body { font-family: sans-serif; margin: 2em; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 1em; }");
        sb.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        sb.append(".welcome-msg { text-align: right; }");
        sb.append(".action-btn { display: inline-block; text-align: center; text-decoration: none; color: white; padding: 15px 25px; border: none; font-size: 1.2em; cursor: pointer; margin-bottom: 1em; }");
        sb.append(".cart-btn { background-color: #17a2b8; }");
        sb.append("</style></head><body>");
        sb.append("<div class='welcome-msg'>Logged in as: <strong>").append(HandlerUtils.escapeHtml(user.getUsername())).append("</strong> | <a href='/logout'>Logout</a></div>");
        sb.append("<h1>Create a New Order</h1>");
        sb.append("<button onclick='proceedToCheckout()' class='action-btn cart-btn'>View Cart & Checkout</button>");
        sb.append("<div style='margin:1em 0;'><input type='text' id='searchInput' onkeyup='filterTable()' placeholder='Type to filter products...'></div>");
        sb.append("<form id='order-form'>");
        sb.append(HandlerUtils.convertProductListToOrderFormTable(products));
        sb.append("</form>");
        sb.append("<script>");
        sb.append("const CART_KEY = 'shoppingCart';");
        sb.append("function saveCart() { const inputs = document.querySelectorAll('.quantity-input'); const cart = {}; inputs.forEach(input => { const q = parseInt(input.value); if (q > 0) { cart[input.name.split('_')[1]] = q; } }); localStorage.setItem(CART_KEY, JSON.stringify(cart)); }");
        sb.append("function loadCart() { const cart = JSON.parse(localStorage.getItem(CART_KEY) || '{}'); for (const pid in cart) { const input = document.querySelector(`input[name='quantity_${pid}']`); if (input) { input.value = cart[pid]; } } }");
        sb.append("window.addEventListener('DOMContentLoaded', loadCart);");
        sb.append("function filterTable() { var input = document.getElementById('searchInput'), filter = input.value.toUpperCase(), table = document.querySelector('table'), tr = table.getElementsByTagName('tr'); for (var i = 1; i < tr.length; i++) { var td = tr[i].getElementsByTagName('td')[2]; if (td) { tr[i].style.display = (td.textContent || td.innerText).toUpperCase().indexOf(filter) > -1 ? '' : 'none'; } } }");
        sb.append("function proceedToCheckout() { saveCart(); const cart = JSON.parse(localStorage.getItem(CART_KEY) || '{}'); let queryParams = ''; for (const pid in cart) { queryParams += `&quantity_${pid}=${cart[pid]}`; } if (queryParams) { window.location.href = '/checkout?' + queryParams.substring(1); } else { alert('Please add at least one item to the order.'); } }");
        sb.append("</script></body></html>");
        return sb.toString();
    }
}
