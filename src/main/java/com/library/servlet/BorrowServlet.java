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

@WebServlet(name = "BorrowServlet", urlPatterns = {"/borrow"})
public class BorrowServlet extends HttpServlet {

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

        String bookIdParam = req.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/catalog?error=invalid_book");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/catalog?error=invalid_book_id");
            return;
        }

        BorrowingDAO.BorrowOperationStatus status = borrowingDAO.borrowBook(loggedUser.getUserId(), bookId, 14);

        // Check if AJAX request
        String format = req.getParameter("format");
        boolean isAjax = "json".equalsIgnoreCase(format) ||
                         "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        if (isAjax) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Map<String, Object> result = new HashMap<>();
            result.put("success", status.isSuccess());
            result.put("message", status.message());
            if (status.getDueDate() != null) {
                result.put("dueDate", status.getDueDate().toString());
            }
            resp.getWriter().write(gson.toJson(result));
            return;
        }

        if (status.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/loans?borrow_success=1&msg=" +
                    URLEncoder.encode(status.message(), StandardCharsets.UTF_8));
        } else {
            resp.sendRedirect(req.getContextPath() + "/catalog?borrow_error=1&msg=" +
                    URLEncoder.encode(status.message(), StandardCharsets.UTF_8));
        }
    }
}
