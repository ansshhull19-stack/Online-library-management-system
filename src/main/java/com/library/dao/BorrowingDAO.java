package com.library.dao;

import com.library.config.DBConnection;
import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.User;
import com.library.util.DateUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorrowingDAO {

    private static final Logger LOGGER = Logger.getLogger(BorrowingDAO.class.getName());

    public enum BorrowResult {
        SUCCESS,
        OUT_OF_STOCK,
        ALREADY_BORROWED,
        USER_NOT_FOUND,
        BOOK_NOT_FOUND,
        DATABASE_ERROR
    }

    public static class BorrowOperationStatus {
        private final BorrowResult result;
        private final String message;
        private final Timestamp dueDate;

        public BorrowOperationStatus(BorrowResult result, String message, Timestamp dueDate) {
            this.result = result;
            this.message = message;
            this.dueDate = dueDate;
        }

        public BorrowResult getResult() {
            return result;
        }

        public String message() {
            return message;
        }

        public Timestamp getDueDate() {
            return dueDate;
        }

        public boolean isSuccess() {
            return result == BorrowResult.SUCCESS;
        }
    }

    /**
     * Executes an atomic book loan operation.
     * Decrements available book copies and inserts a loan record within a database transaction.
     */
    public BorrowOperationStatus borrowBook(int userId, int bookId, int loanDays) {
        String checkLoanSql = "SELECT COUNT(*) FROM borrowings WHERE user_id = ? AND book_id = ? AND status = 'BORROWED'";
        String lockBookSql = "SELECT available_copies, total_copies FROM books WHERE book_id = ? FOR UPDATE";
        String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
        String insertBorrowSql = "INSERT INTO borrowings (user_id, book_id, borrow_date, due_date, status, fine_amount) " +
                                 "VALUES (?, ?, NOW(), ?, 'BORROWED', 0.00)";

        Timestamp dueDate = DateUtil.calculateDueDate(loanDays > 0 ? loanDays : DateUtil.DEFAULT_LOAN_PERIOD_DAYS);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Check if user already has an active loan of this book
            try (PreparedStatement checkStmt = conn.prepareStatement(checkLoanSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return new BorrowOperationStatus(BorrowResult.ALREADY_BORROWED,
                                "You have already borrowed this book and have not returned it yet.", null);
                    }
                }
            }

            // 2. Lock and verify book inventory
            int availableCopies = 0;
            try (PreparedStatement lockStmt = conn.prepareStatement(lockBookSql)) {
                lockStmt.setInt(1, bookId);
                try (ResultSet rs = lockStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return new BorrowOperationStatus(BorrowResult.BOOK_NOT_FOUND, "Book not found in library.", null);
                    }
                    availableCopies = rs.getInt("available_copies");
                }
            }

            if (availableCopies <= 0) {
                conn.rollback();
                return new BorrowOperationStatus(BorrowResult.OUT_OF_STOCK,
                        "Sorry, no copies of this book are currently available.", null);
            }

            // 3. Decrement available copies
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
            }

            // 4. Create borrowing record
            try (PreparedStatement insertStmt = conn.prepareStatement(insertBorrowSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, bookId);
                insertStmt.setTimestamp(3, dueDate);
                insertStmt.executeUpdate();
            }

            conn.commit(); // Transaction successful
            return new BorrowOperationStatus(BorrowResult.SUCCESS,
                    "Book successfully borrowed! Due date: " + DateUtil.formatDate(dueDate), dueDate);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during borrow transaction", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback borrow transaction", ex);
                }
            }
            return new BorrowOperationStatus(BorrowResult.DATABASE_ERROR, "A database error occurred while borrowing.", null);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }

    /**
     * Executes an atomic return operation.
     * Updates borrowing record status and increments book available copies.
     */
    public boolean returnBook(int borrowId, int userId, boolean isAdmin) {
        String selectBorrowSql = "SELECT book_id, due_date, status FROM borrowings WHERE borrow_id = ? " +
                                 (isAdmin ? "" : "AND user_id = ? ") + "FOR UPDATE";
        String updateBorrowSql = "UPDATE borrowings SET return_date = NOW(), status = 'RETURNED', fine_amount = ? WHERE borrow_id = ?";
        String updateBookSql = "UPDATE books SET available_copies = LEAST(total_copies, available_copies + 1) WHERE book_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            int bookId = 0;
            Timestamp dueDate = null;
            String status = null;

            // 1. Lock and inspect borrowing record
            try (PreparedStatement selectStmt = conn.prepareStatement(selectBorrowSql)) {
                selectStmt.setInt(1, borrowId);
                if (!isAdmin) {
                    selectStmt.setInt(2, userId);
                }
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    bookId = rs.getInt("book_id");
                    dueDate = rs.getTimestamp("due_date");
                    status = rs.getString("status");
                }
            }

            if ("RETURNED".equalsIgnoreCase(status)) {
                conn.rollback();
                return false; // Already returned
            }

            // 2. Calculate any overdue fine
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long overdueDays = DateUtil.calculateOverdueDays(dueDate, now);
            BigDecimal fine = DateUtil.calculateFine(overdueDays);

            // 3. Update borrowing record
            try (PreparedStatement updateBorrowStmt = conn.prepareStatement(updateBorrowSql)) {
                updateBorrowStmt.setBigDecimal(1, fine);
                updateBorrowStmt.setInt(2, borrowId);
                updateBorrowStmt.executeUpdate();
            }

            // 4. Increment available copies of book
            try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
            }

            conn.commit(); // Transaction successful
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during return transaction", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback return transaction", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }

    public List<Borrowing> getUserBorrowings(int userId) {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT b.borrow_id, b.user_id, b.book_id, b.borrow_date, b.due_date, b.return_date, " +
                     "b.status, b.fine_amount, b.notes, " +
                     "bk.title, bk.author, bk.isbn, bk.category, bk.location_shelf " +
                     "FROM borrowings b " +
                     "JOIN books bk ON b.book_id = bk.book_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.borrow_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Borrowing item = mapRowToBorrowing(rs);

                    Book bk = new Book();
                    bk.setBookId(rs.getInt("book_id"));
                    bk.setTitle(rs.getString("title"));
                    bk.setAuthor(rs.getString("author"));
                    bk.setIsbn(rs.getString("isbn"));
                    bk.setCategory(rs.getString("category"));
                    bk.setLocationShelf(rs.getString("location_shelf"));
                    item.setBook(bk);

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving loans for userId: " + userId, e);
        }
        return list;
    }

    public List<Borrowing> getAllBorrowings(String statusFilter) {
        List<Borrowing> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.borrow_id, b.user_id, b.book_id, b.borrow_date, b.due_date, b.return_date, " +
                "b.status, b.fine_amount, b.notes, " +
                "u.username, u.full_name, u.email, " +
                "bk.title, bk.author, bk.isbn, bk.category " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id ");

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("WHERE b.status = ? ");
        }
        sql.append("ORDER BY b.borrow_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                stmt.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Borrowing item = mapRowToBorrowing(rs);

                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    item.setUser(u);

                    Book bk = new Book();
                    bk.setBookId(rs.getInt("book_id"));
                    bk.setTitle(rs.getString("title"));
                    bk.setAuthor(rs.getString("author"));
                    bk.setIsbn(rs.getString("isbn"));
                    bk.setCategory(rs.getString("category"));
                    item.setBook(bk);

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all borrowings", e);
        }
        return list;
    }

    public int countActiveLoans() {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE status = 'BORROWED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting active loans", e);
        }
        return 0;
    }

    public int countOverdueLoans() {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE status = 'BORROWED' AND due_date < NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting overdue loans", e);
        }
        return 0;
    }

    /**
     * Updates status of overdue unreturned loans.
     */
    public void updateOverdueStatuses() {
        String sql = "UPDATE borrowings SET status = 'OVERDUE' WHERE status = 'BORROWED' AND due_date < NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error updating overdue loans status", e);
        }
    }

    private Borrowing mapRowToBorrowing(ResultSet rs) throws SQLException {
        Borrowing b = new Borrowing();
        b.setBorrowId(rs.getInt("borrow_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setBookId(rs.getInt("book_id"));
        b.setBorrowDate(rs.getTimestamp("borrow_date"));
        b.setDueDate(rs.getTimestamp("due_date"));
        b.setReturnDate(rs.getTimestamp("return_date"));
        b.setStatus(rs.getString("status"));
        b.setFineAmount(rs.getBigDecimal("fine_amount"));
        b.setNotes(rs.getString("notes"));
        return b;
    }
}
