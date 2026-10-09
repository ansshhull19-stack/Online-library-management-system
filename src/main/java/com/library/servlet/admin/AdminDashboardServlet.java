package com.library.servlet.admin;

import com.library.dao.BookDAO;
import com.library.dao.BorrowingDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Refresh overdue statuses
        borrowingDAO.updateOverdueStatuses();

        int totalTitles = bookDAO.countTotalTitles();
        int totalCopies = bookDAO.countTotalCopies();
        int availableCopies = bookDAO.countAvailableCopies();
        int activeLoans = borrowingDAO.countActiveLoans();
        int overdueLoans = borrowingDAO.countOverdueLoans();
        int totalStudents = userDAO.countStudents();

        List<Borrowing> recentLoans = borrowingDAO.getAllBorrowings("ALL");
        if (recentLoans.size() > 8) {
            recentLoans = recentLoans.subList(0, 8);
        }

        req.setAttribute("totalTitles", totalTitles);
        req.setAttribute("totalCopies", totalCopies);
        req.setAttribute("availableCopies", availableCopies);
        req.setAttribute("activeLoans", activeLoans);
        req.setAttribute("overdueLoans", overdueLoans);
        req.setAttribute("totalStudents", totalStudents);
        req.setAttribute("recentLoans", recentLoans);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
