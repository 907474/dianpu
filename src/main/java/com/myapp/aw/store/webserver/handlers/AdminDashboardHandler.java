package com.myapp.aw.store.webserver.handlers;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Optional;
public class AdminDashboardHandler implements HttpHandler {
    private final SessionManager sessionManager;
    public AdminDashboardHandler(SessionManager sessionManager) { this.sessionManager = sessionManager; }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<User> userOpt = sessionManager.getSessionUser(exchange);
        if (!userOpt.isPresent()) { HandlerUtils.redirect(exchange, "/admin-login"); return; }
        String response = "<html><head><title>Admin Dashboard</title><style>" + "body{font-family:sans-serif;margin:2em}h1{text-align:center}.nav-container{text-align:center;margin-bottom:2em}.nav-link{display:inline-block;background-color:#007BFF;color:white;padding:10px 15px;margin:5px;border-radius:5px;text-decoration:none;font-size:1.1em}.nav-link-restock{background-color:#ffc107;color:#212529}.nav-link-logout{background-color:#dc3545}.nav-link:hover{opacity:0.8}.welcome-msg{text-align:center;margin-bottom:1em;font-size:1.2em}</style></head><body>" + "<h1>Store Management Dashboard</h1><div class='welcome-msg'>Welcome, <strong>" + HandlerUtils.escapeHtml(userOpt.get().getUsername()) + "</strong>! (<a href='/logout'>Logout</a>)</div>" + "<div class='nav-container'><a href='/restock' class='nav-link nav-link-restock'>Restock Products</a><a href='/users' class='nav-link'>View All Users</a><a href='/orders' class='nav-link'>View All Orders</a></div></body></html>";
        HandlerUtils.sendResponse(exchange, 200, response, "text/html");
    }
}