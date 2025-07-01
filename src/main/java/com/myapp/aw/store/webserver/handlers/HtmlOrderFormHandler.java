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
        sb.append(".proceed-btn { background-color: #28a745; }");
        sb.append(".cancel-btn { background-color: #6c757d; }");
        sb.append(".action-btn:hover { opacity: 0.8; }");
        sb.append("</style></head><body>");
        sb.append("<h1>Create a New Order</h1>");
        sb.append("<p><a href='/'>&larr; Back to Dashboard</a></p>");

        sb.append("<div>");
        sb.append("<button onclick='proceedToCheckout()' class='action-btn proceed-btn'>Proceed to Checkout</button>");
        sb.append("<a href='/' class='action-btn cancel-btn'>Cancel Order</a>");
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
        sb.append(HandlerUtils.convertProductListToOrderFormTable(products));
        sb.append("</form>");

        sb.append("<script>");
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
        sb.append("  const form = document.getElementById('order-form');");
        sb.append("  const inputs = form.querySelectorAll('.quantity-input');");
        sb.append("  let queryParams = '';");
        sb.append("  inputs.forEach(input => {");
        sb.append("    if (input.value && parseInt(input.value) > 0) {");
        sb.append("      queryParams += `&${input.name}=${input.value}`;");
        sb.append("    }");
        sb.append("  });");
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
}
