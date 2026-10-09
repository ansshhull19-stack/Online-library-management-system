package com.library.servlet;

import com.library.dao.BorrowingDAO;
import com.library.model.Borrowing;
import com.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MyLoansServlet", urlPatterns = {"/loans"})
public class MyLoansServlet extends HttpServlet {

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=login_required");
            return;
        }

        // Refresh overdue statuses
        borrowingDAO.updateOverdueStatuses();

        List<Borrowing> allLoans = borrowingDAO.getUserBorrowings(loggedUser.getUserId());
        List<Borrowing> activeLoans = new ArrayList<>();
        List<Borrowing> pastLoans = new ArrayList<>();
        BigDecimal totalFines = BigDecimal.ZERO;

        for (Borrowing loan : allLoans) {
            if ("RETURNED".equalsIgnoreCase(loan.getStatus())) {
                pastLoans.add(loan);
            } else {
                activeLoans.add(loan);
            }
            if (loan.getFineAmount() != null) {
                totalFines = totalFines.add(loan.getFineAmount());
            }
        }

        req.setAttribute("activeLoans", activeLoans);
        req.setAttribute("pastLoans", pastLoans);
        req.setAttribute("totalFines", totalFines);
        req.setAttribute("activeLoanCount", activeLoans.size());

        req.getRequestDispatcher("/WEB-INF/views/my-loans.jsp").forward(req, resp);
    }
}
