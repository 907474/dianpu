package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class HtmlOrderFormHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public HtmlOrderFormHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HandlerUtils.sendResponse(exchange, 405, "", null);
            return;
        }

        try {
            List<Product> products = productRepository.findAll();
            String htmlResponse = generateOrderFormPage(products);
            HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "<h1>Error</h1><p>Could not fetch products for order form.</p>";
            HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
        }
    }

    private String generateOrderFormPage(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Create Order</title><style>");
        sb.append("body { font-family: sans-serif; margin: 2em; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 1em; }");
        sb.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        sb.append("th { background-color: #f2f2f2; }");
        sb.append("a { color: #007BFF; text-decoration: none; }");
        sb.append(".search-container { margin-bottom: 1em; }");
        sb.append("#searchInput, #searchBy { padding: 8px; margin-right: 5px; box-sizing: border-box; }");
        sb.append(".quantity-input { width: 60px; padding: 5px; }");
        sb.append(".action-btn { display: inline-block; text-align: center; text-decoration: none; color: white; padding: 15px 25px; border: none; border-radius: 5px; font-size: 1.2em; cursor: pointer; margin-bottom: 1em; margin-right: 10px; }");
        sb.append(".cart-btn { background-color: #17a2b8; }");
        sb.append(".action-btn:hover { opacity: 0.8; }");
        sb.append("</style></head><body>");
        sb.append("<h1>Create a New Order</h1>");
        sb.append("<p><a href='/'>&larr; Back to Homepage</a></p>");

        sb.append("<div>");
        sb.append("<button onclick='proceedToCheckout()' class='action-btn cart-btn'>View Cart</button>");
        sb.append("</div>");

        sb.append("<div class='search-container'>");
        sb.append("  <label for='searchBy'>Filter by:</label>");
        sb.append("  <select id='searchBy' onchange='filterTable()'>");
        sb.append("    <option value='2'>Name</option>");
        sb.append("    <option value='1'>SKU</option>");
        sb.append("  </select>");
        sb.append("  <input type='text' id='searchInput' onkeyup='filterTable()' placeholder='Type to filter products...'>");
        sb.append("</div>");

        sb.append("<form id='order-form'>");
        sb.append(convertProductListToOrderFormTableWithEvents(products));
        sb.append("</form>");

        sb.append("<script>");
        sb.append("const CART_KEY = 'shoppingCart';");

        sb.append("function saveCart() {");
        sb.append("  const inputs = document.querySelectorAll('.quantity-input');");
        sb.append("  const cart = {};");
        sb.append("  inputs.forEach(input => {");
        sb.append("    const quantity = parseInt(input.value);");
        sb.append("    if (quantity > 0) {");
        sb.append("      const productId = input.name.split('_')[1];");
        sb.append("      cart[productId] = quantity;");
        sb.append("    }");
        sb.append("  });");
        sb.append("  localStorage.setItem(CART_KEY, JSON.stringify(cart));");
        sb.append("}");

        sb.append("function loadCart() {");
        sb.append("  const cart = JSON.parse(localStorage.getItem(CART_KEY) || '{}');");
        sb.append("  for (const productId in cart) {");
        sb.append("    const input = document.querySelector(`input[name='quantity_${productId}']`);");
        sb.append("    if (input) {");
        sb.append("      input.value = cart[productId];");
        sb.append("    }");
        sb.append("  }");
        sb.append("}");

        sb.append("window.addEventListener('DOMContentLoaded', loadCart);");

        sb.append("function filterTable() {");
        sb.append("  var input, filter, table, tr, td, i, txtValue, searchColumn;");
        sb.append("  input = document.getElementById('searchInput');");
        sb.append("  filter = input.value.toUpperCase();");
        sb.append("  table = document.querySelector('table');");
        sb.append("  tr = table.getElementsByTagName('tr');");
        sb.append("  searchColumn = document.getElementById('searchBy').value;");
        sb.append("  for (i = 1; i < tr.length; i++) {");
        sb.append("    td = tr[i].getElementsByTagName('td')[searchColumn];");
        sb.append("    if (td) {");
        sb.append("      txtValue = td.textContent || td.innerText;");
        sb.append("      if (txtValue.toUpperCase().indexOf(filter) > -1) {");
        sb.append("        tr[i].style.display = '';");
        sb.append("      } else {");
        sb.append("        tr[i].style.display = 'none';");
        sb.append("      }");
        sb.append("    }");
        sb.append("  }");
        sb.append("}");

        sb.append("function proceedToCheckout() {");
        sb.append("  saveCart();");
        sb.append("  const cart = JSON.parse(localStorage.getItem(CART_KEY) || '{}');");
        sb.append("  let queryParams = '';");
        sb.append("  for (const productId in cart) {");
        sb.append("    queryParams += `&quantity_${productId}=${cart[productId]}`;");
        sb.append("  }");
        sb.append("  if (queryParams) {");
        sb.append("    window.location.href = '/checkout?' + queryParams.substring(1);");
        sb.append("  } else {");
        sb.append("    alert('Please add at least one item to the order.');");
        sb.append("  }");
        sb.append("}");
        sb.append("</script>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String convertProductListToOrderFormTableWithEvents(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>SKU</th><th>Name</th><th>Price</th><th>Stock</th><th>Quantity to Order</th></tr>");
        for (Product p : products) {
            sb.append("<tr>");
            sb.append("<td>").append(p.getId()).append("</td>");
            sb.append("<td>").append(HandlerUtils.escapeHtml(p.getSku())).append("</td>");
            sb.append("<td>").append(HandlerUtils.escapeHtml(p.getName())).append("</td>");
            sb.append("<td>").append(String.format("$%.2f", p.getPrice())).append("</td>");
            sb.append("<td>").append(p.getStock()).append("</td>");
            sb.append("<td><input type='number' class='quantity-input' name='quantity_").append(p.getId()).append("' min='0' placeholder='0' oninput='saveCart()'></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
}
