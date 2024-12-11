package com.example.Chat.app.Users.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_app?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "admin";
    private static final String PASSWORD = "Phuongnam2312";
    private static Connection connect;
    private static DatabaseConnection instance;

    public static Connection getConnection() {
        try {
            if (connect == null || connect.isClosed()) {
                Class.forName(JDBC_DRIVER);
                connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "JDBC Driver not found: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return connect;
    }

    public Connection getConnect() {
        return getConnection();
    }

    public void setConnect(Connection connect) {
        DatabaseConnection.connect = connect;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public String checkPassword(String usernameOrEmail, String password) {
        String sql = "SELECT user_id,`lock` FROM users WHERE (username = ? OR email = ?) AND password = ?";

        ResultSet rs = null;
        try {
            if (connect == null) {
                connect = getConnection();
            }
            PreparedStatement stmt = connect.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);
            rs = stmt.executeQuery();
            if (rs.first()) {
                String userID = rs.getString("user_id");
                int lock = rs.getInt("lock");
                if (lock == 1) {
                    return "Account is locked";
                }
                return userID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Integer> getUsersThatReceivedMessages(int senderId) {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                       "CASE " +
                       "WHEN cg.is_chat_with_user = 1 THEN mv.user_id " +
                       "WHEN cg.is_chat_with_user = 0 THEN NULL " +
                       "END AS user_id " +
                       "FROM message m " +
                       "INNER JOIN chat_group cg ON m.group_id = cg.group_id " +
                       "LEFT JOIN group_members gm ON m.group_id = gm.group_id " +
                       "WHERE m.sender_id = ? " +
                       "AND (cg.is_chat_with_user = 1 OR cg.is_chat_with_user = 0)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer userId = rs.getInt("user_id");
                if (userId != 0) {
                    userIds.add(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }
    public List<String> getUsernamesThatReceivedMessages(int senderId) {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT DISTINCT u.username " +
                       "FROM message m " +
                       "INNER JOIN chat_group cg ON m.group_id = cg.group_id " +
                       "LEFT JOIN group_members gm ON m.group_id = gm.group_id " +
                       "LEFT JOIN users u ON u.user_id = gm.user_id " +
                       "WHERE m.sender_id = ? " +
                       "AND (cg.is_chat_with_user = 1 OR cg.is_chat_with_user = 0)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                if (username != null) {
                    usernames.add(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }
    public int getUserIdByUsername(String username) {
        int userId = -1;
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
}
