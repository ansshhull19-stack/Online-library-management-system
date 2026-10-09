package com.library.dao;

import com.library.config.DBConnection;
import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO {

    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());

    public Book findById(int bookId) {
        String sql = "SELECT book_id, isbn, title, author, category, publisher, publication_year, " +
                     "total_copies, available_copies, location_shelf, cover_image_url, description, created_at " +
                     "FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding book by id: " + bookId, e);
        }
        return null;
    }

    public Book findByIsbn(String isbn) {
        String sql = "SELECT book_id, isbn, title, author, category, publisher, publication_year, " +
                     "total_copies, available_copies, location_shelf, cover_image_url, description, created_at " +
                     "FROM books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn != null ? isbn.trim() : "");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding book by ISBN: " + isbn, e);
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT book_id, isbn, title, author, category, publisher, publication_year, " +
                     "total_copies, available_copies, location_shelf, cover_image_url, description, created_at " +
                     "FROM books ORDER BY title ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all books", e);
        }
        return list;
    }

    public List<Book> getFeaturedBooks(int limit) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT book_id, isbn, title, author, category, publisher, publication_year, " +
                     "total_copies, available_copies, location_shelf, cover_image_url, description, created_at " +
                     "FROM books ORDER BY available_copies DESC, created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit > 0 ? limit : 6);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving featured books", e);
        }
        return list;
    }

    public List<Book> searchBooks(String query, String category, Boolean availableOnly, int offset, int limit) {
        List<Book> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT book_id, isbn, title, author, category, publisher, publication_year, " +
                "total_copies, available_copies, location_shelf, cover_image_url, description, created_at " +
                "FROM books WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?) ");
            String wildcard = "%" + query.trim() + "%";
            params.add(wildcard);
            params.add(wildcard);
            params.add(wildcard);
        }

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append("AND category = ? ");
            params.add(category.trim());
        }

        if (availableOnly != null && availableOnly) {
            sql.append("AND available_copies > 0 ");
        }

        sql.append("ORDER BY title ASC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 12);
        params.add(Math.max(offset, 0));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching books", e);
        }
        return list;
    }

    public int countSearchResults(String query, String category, Boolean availableOnly) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM books WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?) ");
            String wildcard = "%" + query.trim() + "%";
            params.add(wildcard);
            params.add(wildcard);
            params.add(wildcard);
        }

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append("AND category = ? ");
            params.add(category.trim());
        }

        if (availableOnly != null && availableOnly) {
            sql.append("AND available_copies > 0 ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting search results", e);
        }
        return 0;
    }

    public List<String> getAllCategories() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM books ORDER BY category ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
        }
        return list;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, category, publisher, publication_year, " +
                     "total_copies, available_copies, location_shelf, cover_image_url, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublisher());
            if (book.getPublicationYear() != null) {
                stmt.setInt(6, book.getPublicationYear());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, book.getTotalCopies());
            stmt.setInt(8, book.getAvailableCopies());
            stmt.setString(9, book.getLocationShelf());
            stmt.setString(10, book.getCoverImageUrl());
            stmt.setString(11, book.getDescription());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = stmt.getGeneratedKeys()) {
                    if (gk.next()) {
                        book.setBookId(gk.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding book: " + book.getTitle(), e);
        }
        return false;
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, category = ?, publisher = ?, " +
                     "publication_year = ?, total_copies = ?, available_copies = ?, location_shelf = ?, " +
                     "cover_image_url = ?, description = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublisher());
            if (book.getPublicationYear() != null) {
                stmt.setInt(6, book.getPublicationYear());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, book.getTotalCopies());
            stmt.setInt(8, book.getAvailableCopies());
            stmt.setString(9, book.getLocationShelf());
            stmt.setString(10, book.getCoverImageUrl());
            stmt.setString(11, book.getDescription());
            stmt.setInt(12, book.getBookId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating book id: " + book.getBookId(), e);
        }
        return false;
    }

    public boolean deleteBook(int bookId) {
        // First check if active loans exist for this book
        String checkSql = "SELECT COUNT(*) FROM borrowings WHERE book_id = ? AND status = 'BORROWED'";
        String deleteSql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        LOGGER.warning("Cannot delete book " + bookId + ": active loans exist.");
                        return false;
                    }
                }
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, bookId);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting book id: " + bookId, e);
        }
        return false;
    }

    public int countTotalTitles() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total book titles", e);
        }
        return 0;
    }

    public int countTotalCopies() {
        String sql = "SELECT COALESCE(SUM(total_copies), 0) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error summing total copies", e);
        }
        return 0;
    }

    public int countAvailableCopies() {
        String sql = "SELECT COALESCE(SUM(available_copies), 0) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error summing available copies", e);
        }
        return 0;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setCategory(rs.getString("category"));
        b.setPublisher(rs.getString("publisher"));
        int year = rs.getInt("publication_year");
        if (!rs.wasNull()) {
            b.setPublicationYear(year);
        }
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setLocationShelf(rs.getString("location_shelf"));
        b.setCoverImageUrl(rs.getString("cover_image_url"));
        b.setDescription(rs.getString("description"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}
