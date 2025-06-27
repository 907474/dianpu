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
        sb.append("tr:nth-child(even) { background-color: #f9f9f9; }");
        sb.append("a { color: #007BFF; text-decoration: none; }");
        sb.append(".search-container { margin-bottom: 1em; }");
        sb.append("#searchInput, #searchBy { padding: 8px; margin-right: 5px; box-sizing: border-box; }");
        sb.append("#userIdInput { padding: 8px; margin-bottom: 1em; width: 100%; max-width: 200px; }");
        sb.append(".quantity-input { width: 60px; padding: 5px; }");
        sb.append(".place-order-btn { background-color: #28a745; color: white; padding: 15px 25px; border: none; border-radius: 5px; font-size: 1.2em; cursor: pointer; margin-bottom: 1em; }");
        sb.append(".place-order-btn:hover { background-color: #218838; }");
        sb.append("</style></head><body>");
        sb.append("<h1>Create a New Order</h1>");
        sb.append("<p><a href='/'>&larr; Back to Dashboard</a></p>");

        sb.append("<form id='order-form' action='/api/orders/add' method='post'>");
        sb.append("  <input type='submit' value='Place Order' class='place-order-btn'><br>");
        sb.append("  <label for='userIdInput'><strong>User ID for this order:</strong></label><br>");
        sb.append("  <input type='number' id='userIdInput' name='userId' placeholder='Enter User ID' required><br>");

        sb.append("  <div class='search-container'>");
        sb.append("    <label for='searchBy'>Filter by:</label>");
        sb.append("    <select id='searchBy' onchange='filterTable()'>");
        sb.append("      <option value='2'>Product Name</option>");
        sb.append("      <option value='1'>SKU</option>");
        sb.append("      <option value='0'>Product ID</option>");
        sb.append("    </select>");
        sb.append("    <input type='text' id='searchInput' onkeyup='filterTable()' placeholder='Type to filter products...'>");
        sb.append("  </div>");

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
        sb.append("</script>");

        sb.append("</body></html>");
        return sb.toString();
    }
}
