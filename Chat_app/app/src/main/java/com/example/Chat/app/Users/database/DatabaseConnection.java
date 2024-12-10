package com.example.Chat.app.Users.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
            // Load JDBC driver
            Class.forName(JDBC_DRIVER);
            // Establish connection
            connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
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
        String sql = "SELECT userID FROM users WHERE (username = ? OR email = ?) AND password = ?";
        ResultSet rs = null;
        try {
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);
            rs = stmt.executeQuery();
            if (rs.first()) {
                return rs.getString("userID");
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
}