package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class HtmlRestockHandler implements HttpHandler {
    private final ProductRepository productRepository;

    public HtmlRestockHandler(ProductRepository productRepository) {
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
            String htmlResponse = generatePageWithLiveSearch(products);
            HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "<h1>Error</h1><p>Could not fetch products.</p>";
            HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
        }
    }

    private String generatePageWithLiveSearch(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Restock Products</title><style>");
        sb.append("body { font-family: sans-serif; margin: 2em; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 1em; }");
        sb.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        sb.append("th { background-color: #f2f2f2; }");
        sb.append("tr:nth-child(even) { background-color: #f9f9f9; }");
        sb.append("a { color: #007BFF; text-decoration: none; }");
        sb.append(".search-container { margin-bottom: 1em; }");
        sb.append("#searchInput { padding: 8px; margin-right: 5px; box-sizing: border-box; }");
        sb.append(".restock-input { width: 80px; }");
        sb.append(".restock-btn { background-color: #17a2b8; color: white; border: none; padding: 5px 10px; cursor: pointer; }");
        sb.append("</style></head><body>");
        sb.append("<h1>Restock Products</h1>");
        sb.append("<p><a href='/'>&larr; Back to Dashboard</a></p>");

        sb.append("<div class='search-container'>");
        sb.append("    <input type='text' id='searchInput' onkeyup='filterTable()' placeholder='Type to filter products by name...'>");
        sb.append("</div>");

        sb.append("<table><thead><tr><th>ID</th><th>SKU</th><th>Name</th><th>Current Stock</th><th>Add Stock</th><th>Action</th></tr></thead><tbody>");
        for (Product p : products) {
            sb.append("<tr>");
            sb.append("<td>").append(p.getId()).append("</td>");
            sb.append("<td>").append(HandlerUtils.escapeHtml(p.getSku())).append("</td>");
            sb.append("<td>").append(HandlerUtils.escapeHtml(p.getName())).append("</td>");
            sb.append("<td>").append(p.getStock()).append("</td>");
            sb.append("<td><form action='/api/products/restock' method='post' style='margin:0;padding:0;display:inline;'>");
            sb.append("<input type='hidden' name='productId' value='").append(p.getId()).append("'>");
            sb.append("<input type='number' name='amount' class='restock-input' min='1' required>");
            sb.append("</td><td>");
            sb.append("<button type='submit' class='restock-btn'>Restock</button></form></td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        sb.append("<script>");
        sb.append("function filterTable() {");
        sb.append("  var input, filter, table, tr, td, i, txtValue;");
        sb.append("  input = document.getElementById('searchInput');");
        sb.append("  filter = input.value.toUpperCase();");
        sb.append("  table = document.querySelector('table');");
        sb.append("  tr = table.getElementsByTagName('tr');");
        sb.append("  for (i = 1; i < tr.length; i++) {");
        sb.append("    td = tr[i].getElementsByTagName('td')[2];"); // Column 2 is Product Name
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
