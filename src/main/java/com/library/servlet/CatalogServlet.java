package com.library.servlet;

import com.google.gson.Gson;
import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CatalogServlet", urlPatterns = {"/catalog"})
public class CatalogServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final Gson gson = new Gson();
    private static final int PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        String category = req.getParameter("category");
        String availableParam = req.getParameter("available");
        boolean availableOnly = "1".equals(availableParam) || "true".equalsIgnoreCase(availableParam);

        String pageParam = req.getParameter("page");
        int page = 1;
        if (pageParam != null) {
            try {
                page = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        int offset = (page - 1) * PAGE_SIZE;

        List<Book> books = bookDAO.searchBooks(query, category, availableOnly, offset, PAGE_SIZE);
        int totalResults = bookDAO.countSearchResults(query, category, availableOnly);
        int totalPages = (int) Math.ceil((double) totalResults / PAGE_SIZE);
        List<String> categories = bookDAO.getAllCategories();

        // AJAX live search JSON support
        String format = req.getParameter("format");
        if ("json".equalsIgnoreCase(format)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("books", books);
            jsonMap.put("totalResults", totalResults);
            jsonMap.put("page", page);
            jsonMap.put("totalPages", totalPages);
            resp.getWriter().write(gson.toJson(jsonMap));
            return;
        }

        // Single book detail request
        String detailId = req.getParameter("detailId");
        if (detailId != null) {
            try {
                Book detailBook = bookDAO.findById(Integer.parseInt(detailId));
                if (detailBook != null && "json".equalsIgnoreCase(format)) {
                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(detailBook));
                    return;
                }
                req.setAttribute("detailBook", detailBook);
            } catch (NumberFormatException ignored) {}
        }

        req.setAttribute("books", books);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", category != null ? category : "ALL");
        req.setAttribute("searchQuery", query != null ? query : "");
        req.setAttribute("availableOnly", availableOnly);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalResults", totalResults);

        req.getRequestDispatcher("/WEB-INF/views/catalog.jsp").forward(req, resp);
    }
}
