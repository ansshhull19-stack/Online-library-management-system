package com.library.servlet;

import com.google.gson.Gson;
import com.library.dao.BorrowingDAO;
import com.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ReturnServlet", urlPatterns = {"/return"})
public class ReturnServlet extends HttpServlet {

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=login_required");
            return;
        }

        String borrowIdParam = req.getParameter("borrowId");
        if (borrowIdParam == null || borrowIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/loans?error=invalid_loan");
            return;
        }

        int borrowId;
        try {
            borrowId = Integer.parseInt(borrowIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/loans?error=invalid_loan_id");
            return;
        }

        boolean success = borrowingDAO.returnBook(borrowId, loggedUser.getUserId(), loggedUser.isAdmin());

        String format = req.getParameter("format");
        boolean isAjax = "json".equalsIgnoreCase(format) ||
                         "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        if (isAjax) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "Book successfully returned!" : "Failed to return book. It may have already been returned.");
            resp.getWriter().write(gson.toJson(result));
            return;
        }

        String source = req.getParameter("source");
        String returnRedirect = "admin".equalsIgnoreCase(source) ? "/admin/borrowings" : "/loans";

        if (success) {
            resp.sendRedirect(req.getContextPath() + returnRedirect + "?return_success=1&msg=" +
                    URLEncoder.encode("Book successfully returned and inventory restored.", StandardCharsets.UTF_8));
        } else {
            resp.sendRedirect(req.getContextPath() + returnRedirect + "?return_error=1&msg=" +
                    URLEncoder.encode("Could not process book return. Please verify the loan record.", StandardCharsets.UTF_8));
        }
    }
}
