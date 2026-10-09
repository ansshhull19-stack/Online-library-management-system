package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.BorrowingDAO;
import com.library.dao.UserDAO;
import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fetch featured catalog items
        List<Book> featuredBooks = bookDAO.getFeaturedBooks(6);

        // Fetch high-level statistics for display
        int totalTitles = bookDAO.countTotalTitles();
        int totalCopies = bookDAO.countTotalCopies();
        int availableCopies = bookDAO.countAvailableCopies();
        int registeredStudents = userDAO.countStudents();
        int activeLoans = borrowingDAO.countActiveLoans();

        req.setAttribute("featuredBooks", featuredBooks);
        req.setAttribute("totalTitles", totalTitles);
        req.setAttribute("totalCopies", totalCopies);
        req.setAttribute("availableCopies", availableCopies);
        req.setAttribute("registeredStudents", registeredStudents);
        req.setAttribute("activeLoans", activeLoans);

        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
