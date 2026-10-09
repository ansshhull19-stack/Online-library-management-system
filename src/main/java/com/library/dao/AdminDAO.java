package com.library.dao;

import com.library.config.DBConnection;
import com.library.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDAO {

    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());

    public Admin findByUserId(int userId) {
        String sql = "SELECT admin_id, user_id, department, employee_code, access_level, created_at " +
                     "FROM admins WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUserId(rs.getInt("user_id"));
                    admin.setDepartment(rs.getString("department"));
                    admin.setEmployeeCode(rs.getString("employee_code"));
                    admin.setAccessLevel(rs.getString("access_level"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding admin record for userId: " + userId, e);
        }
        return null;
    }

    public boolean createAdminProfile(Admin admin) {
        String sql = "INSERT INTO admins (user_id, department, employee_code, access_level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, admin.getUserId());
            stmt.setString(2, admin.getDepartment());
            stmt.setString(3, admin.getEmployeeCode());
            stmt.setString(4, admin.getAccessLevel() != null ? admin.getAccessLevel() : "SUPER_ADMIN");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting admin record", e);
        }
        return false;
    }
}
