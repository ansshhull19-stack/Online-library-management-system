package com.library.servlet.admin;

import com.library.dao.BorrowingDAO;
import com.library.model.Borrowing;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminBorrowingServlet", urlPatterns = {"/admin/borrowings"})
public class AdminBorrowingServlet extends HttpServlet {

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Update overdue statuses
        borrowingDAO.updateOverdueStatuses();

        String statusFilter = req.getParameter("status");
        if (statusFilter == null || statusFilter.trim().isEmpty()) {
            statusFilter = "ALL";
        }

        List<Borrowing> borrowings = borrowingDAO.getAllBorrowings(statusFilter);

        req.setAttribute("borrowings", borrowings);
        req.setAttribute("selectedStatus", statusFilter);
        req.setAttribute("activeLoansCount", borrowingDAO.countActiveLoans());
        req.setAttribute("overdueLoansCount", borrowingDAO.countOverdueLoans());

        req.getRequestDispatcher("/WEB-INF/views/admin/borrowings.jsp").forward(req, resp);
    }
}
