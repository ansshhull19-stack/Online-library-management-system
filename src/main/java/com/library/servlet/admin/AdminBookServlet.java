package com.library.servlet.admin;

import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "AdminBookServlet", urlPatterns = {"/admin/books"})
public class AdminBookServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        String category = req.getParameter("category");

        List<Book> books = bookDAO.searchBooks(query, category, false, 0, 100);
        List<String> categories = bookDAO.getAllCategories();

        req.setAttribute("books", books);
        req.setAttribute("categories", categories);
        req.setAttribute("searchQuery", query != null ? query : "");
        req.setAttribute("selectedCategory", category != null ? category : "ALL");

        req.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equalsIgnoreCase(action)) {
            handleAddBook(req, resp);
        } else if ("edit".equalsIgnoreCase(action)) {
            handleEditBook(req, resp);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDeleteBook(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/books");
        }
    }

    private void handleAddBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String isbn = req.getParameter("isbn");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String category = req.getParameter("category");
        String publisher = req.getParameter("publisher");
        String yearStr = req.getParameter("publicationYear");
        String totalCopiesStr = req.getParameter("totalCopies");
        String shelf = req.getParameter("locationShelf");
        String description = req.getParameter("description");
        String coverUrl = req.getParameter("coverImageUrl");

        // Server-side validation
        if (isbn == null || isbn.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            author == null || author.trim().isEmpty() ||
            category == null || category.trim().isEmpty()) {
            redirectWithError(req, resp, "ISBN, Title, Author, and Category are mandatory fields.");
            return;
        }

        // Check if ISBN already exists
        if (bookDAO.findByIsbn(isbn.trim()) != null) {
            redirectWithError(req, resp, "A book with ISBN '" + isbn.trim() + "' already exists.");
            return;
        }

        int totalCopies = 1;
        try {
            if (totalCopiesStr != null && !totalCopiesStr.trim().isEmpty()) {
                totalCopies = Math.max(1, Integer.parseInt(totalCopiesStr.trim()));
            }
        } catch (NumberFormatException ignored) {}

        Integer pubYear = null;
        try {
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                pubYear = Integer.parseInt(yearStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        Book book = new Book();
        book.setIsbn(isbn.trim());
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setCategory(category.trim());
        book.setPublisher(publisher != null ? publisher.trim() : "");
        book.setPublicationYear(pubYear);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);
        book.setLocationShelf(shelf != null ? shelf.trim() : "");
        book.setDescription(description != null ? description.trim() : "");
        book.setCoverImageUrl(coverUrl != null ? coverUrl.trim() : "");

        boolean success = bookDAO.addBook(book);
        if (success) {
            redirectWithSuccess(req, resp, "Book '" + book.getTitle() + "' successfully added to library catalog.");
        } else {
            redirectWithError(req, resp, "Failed to add book due to a database error.");
        }
    }

    private void handleEditBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            redirectWithError(req, resp, "Missing book ID.");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdStr.trim());
        } catch (NumberFormatException e) {
            redirectWithError(req, resp, "Invalid book ID.");
            return;
        }

        Book existing = bookDAO.findById(bookId);
        if (existing == null) {
            redirectWithError(req, resp, "Book not found.");
            return;
        }

        String isbn = req.getParameter("isbn");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String category = req.getParameter("category");
        String publisher = req.getParameter("publisher");
        String yearStr = req.getParameter("publicationYear");
        String totalCopiesStr = req.getParameter("totalCopies");
        String availableCopiesStr = req.getParameter("availableCopies");
        String shelf = req.getParameter("locationShelf");
        String description = req.getParameter("description");
        String coverUrl = req.getParameter("coverImageUrl");

        if (title == null || title.trim().isEmpty() ||
            author == null || author.trim().isEmpty() ||
            category == null || category.trim().isEmpty()) {
            redirectWithError(req, resp, "Title, Author, and Category are mandatory.");
            return;
        }

        int totalCopies = existing.getTotalCopies();
        int availableCopies = existing.getAvailableCopies();

        try {
            if (totalCopiesStr != null) {
                totalCopies = Math.max(0, Integer.parseInt(totalCopiesStr.trim()));
            }
            if (availableCopiesStr != null) {
                availableCopies = Math.max(0, Integer.parseInt(availableCopiesStr.trim()));
            }
        } catch (NumberFormatException ignored) {}

        if (availableCopies > totalCopies) {
            availableCopies = totalCopies;
        }

        Integer pubYear = existing.getPublicationYear();
        try {
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                pubYear = Integer.parseInt(yearStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        existing.setIsbn(isbn != null ? isbn.trim() : existing.getIsbn());
        existing.setTitle(title.trim());
        existing.setAuthor(author.trim());
        existing.setCategory(category.trim());
        existing.setPublisher(publisher != null ? publisher.trim() : "");
        existing.setPublicationYear(pubYear);
        existing.setTotalCopies(totalCopies);
        existing.setAvailableCopies(availableCopies);
        existing.setLocationShelf(shelf != null ? shelf.trim() : "");
        existing.setDescription(description != null ? description.trim() : "");
        existing.setCoverImageUrl(coverUrl != null ? coverUrl.trim() : "");

        boolean success = bookDAO.updateBook(existing);
        if (success) {
            redirectWithSuccess(req, resp, "Book '" + existing.getTitle() + "' updated successfully.");
        } else {
            redirectWithError(req, resp, "Failed to update book.");
        }
    }

    private void handleDeleteBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            redirectWithError(req, resp, "Missing book ID.");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdStr.trim());
        } catch (NumberFormatException e) {
            redirectWithError(req, resp, "Invalid book ID.");
            return;
        }

        boolean success = bookDAO.deleteBook(bookId);
        if (success) {
            redirectWithSuccess(req, resp, "Book removed from library catalog.");
        } else {
            redirectWithError(req, resp, "Cannot remove book. It currently has active borrowed loans or does not exist.");
        }
    }

    private void redirectWithSuccess(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/books?success=1&msg=" +
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/books?error=1&msg=" +
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }
}
