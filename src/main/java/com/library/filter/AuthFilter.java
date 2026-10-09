package com.library.filter;

import com.library.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Security filter for authentication and role-based authorization.
 * Protects admin routes (/admin/*) and student routes (/loans, /borrow, /return).
 * Ensures uniform UTF-8 character encoding across all requests and responses.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Set UTF-8 encoding
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        // Skip static resources
        if (path.startsWith("/assets/") || path.startsWith("/css/") || path.startsWith("/js/") ||
            path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        // 1. Role-based check for Admin paths
        if (path.startsWith("/admin/")) {
            if (loggedUser == null) {
                String target = URLEncoder.encode(path + (req.getQueryString() != null ? "?" + req.getQueryString() : ""), StandardCharsets.UTF_8);
                resp.sendRedirect(contextPath + "/login?error=login_required&redirect=" + target);
                return;
            }

            if (!loggedUser.isAdmin()) {
                resp.sendRedirect(contextPath + "/home?error=admin_forbidden");
                return;
            }
        }

        // 2. Authentication check for protected Student actions
        if (path.equals("/loans") || path.equals("/borrow") || path.equals("/return")) {
            if (loggedUser == null) {
                String target = URLEncoder.encode(path + (req.getQueryString() != null ? "?" + req.getQueryString() : ""), StandardCharsets.UTF_8);
                resp.sendRedirect(contextPath + "/login?error=login_required&redirect=" + target);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
