package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.repository.OrderRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class HtmlOrdersHandler implements HttpHandler {
    private final OrderRepository orderRepository;

    public HtmlOrdersHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                List<Order> orders = orderRepository.findAll();
                String htmlResponse = HandlerUtils.convertOrderListToHtmlTable(orders);
                HandlerUtils.sendResponse(exchange, 200, htmlResponse, "text/html");
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "<h1>Error</h1><p>Could not fetch orders from database.</p>";
                HandlerUtils.sendResponse(exchange, 500, errorResponse, "text/html");
            }
        } else {
            HandlerUtils.sendResponse(exchange, 405, "", null);
        }
    }
}
