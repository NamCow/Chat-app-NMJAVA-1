/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.example.Chat.app.Admin;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 *
 */
public class AdminUI extends javax.swing.JFrame {

    //Manage UserList table 1
    private int selectedUserId = -1; // Initialize as no user selected
    private String currentFilterQuery1 = "";  // Stores the current WHERE clause
    private String currentSortQuery1 = "";    // Stores the current ORDER BY clause  

    //UserList table 7
    private String currentFilterQuery7 = "";  // Stores the current WHERE clause
    private String currentSortQuery7 = "";    // Stores the current ORDER BY clause  

    //Signup List table 3
    private String currentFilterQuery3 = "";  // Stores the current WHERE clause
    private String currentSortQuery3 = "";    // Stores the current ORDER BY clause  
    private String currentTimeFilterQuery3 = ""; // Hold time-based filter

    //Group Chat List table 4
    private int selectedGroupId = -1;
    private String currentFilterQuery4 = "";  // Stores the current WHERE clause
    private String currentSortQuery4 = "";    // Stores the current ORDER BY clause

    //SpamList table 5
    private int selectedReportId = -1;
    private String currentFilterQuery5 = "";  // Stores the current WHERE clause
    private String currentSortQuery5 = "";

    //ActiveUserList table 6
    private String currentFilterQuery6 = "";  // Stores the current WHERE clause
    private String currentNameFilterQuery6 = "";  // Stores the current WHERE clause
    private String currentSortQuery6 = "";
    private String currentTimeFilterQuery6 = ""; // Hold time-based filter


    /**
     * Creates new form TestUI
     */
    public AdminUI() {
        initComponents();
        loadDataTable1();
        loadDataTable2(null);
        loadDataTable7();
        loadDataTable3();
        loadDataTable4();
        loadDataTable5();
    }

    /**
     * Establishes a connection to the database.
     * 
     * @return a Connection object or null if the connection fails
     */
    public Connection setupConnection() {
        String configFilePath = "dbconfig.properties"; // Path to the configuration file
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            // Load properties from the file
            props.load(fis);
            
            String JDBC_DRIVER = props.getProperty("jdbc.driver", "com.mysql.cj.jdbc.Driver");
            String DB_URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/chat_app");
            String USER = props.getProperty("db.user", "root");
            String PASSWORD = props.getProperty("db.password", "hoang123");

            // Load JDBC driver
            Class.forName(JDBC_DRIVER);

            // Establish connection
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading config file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "JDBC Driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // This method is for loading data with an optional query string.
    private void loadDataTable1() {
        // Base query
        String query = "SELECT user_id, fullname, username, address, birthday, gender, email FROM users";
    
        // Append the current filter query (WHERE clause) if it exists
        if (!currentFilterQuery1.isEmpty()) {
            query += " " + currentFilterQuery1;
        }
    
        // Append the current sort query (ORDER BY clause) if it exists
        if (!currentSortQuery1.isEmpty()) {
            query += " " + currentSortQuery1;
        }
    
        try (Connection conn = setupConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {
    
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Get the table model
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    
            // Clear any existing data
            model.setRowCount(0);
    
            // Loop through the result set and populate the table
            while (rs.next()) {
                String id = rs.getString("user_id");
                String name = rs.getString("fullname");
                String username = rs.getString("username");
                String address = rs.getString("address");
                Date birthday = rs.getDate("birthday");
                String gender = rs.getString("gender");
                String email = rs.getString("email");
    
                // Add data to table
                model.addRow(new Object[]{id, name, username, address, birthday, gender, email});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataTable2(String query) {
        // If no query is passed, use the default query
        if (query == null || query.isEmpty()) {
            query = "SELECT login_history.login_at, users.username, users.fullname " +
                    "FROM login_history " +
                    "JOIN users ON login_history.user_id = users.user_id " +
                    "ORDER BY login_history.login_at DESC";
        }
    
        try (Connection conn = setupConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {
    
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Get the table model
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
    
            // Clear any existing data
            model.setRowCount(0);
    
            // Loop through the result set and populate the table
            while (rs.next()) {
                Timestamp loginAt = rs.getTimestamp("login_at");
                String username = rs.getString("username");
                String fullname = rs.getString("fullname");
    
                // Add data to the table
                model.addRow(new Object[]{loginAt, username, fullname});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataTable7() {
        // Base query with placeholders for WHERE, HAVING, and ORDER BY clauses
        String query = """
            SELECT 
                u.username, 
                COUNT(DISTINCT CASE 
                                WHEN uf.user_id = u.user_id THEN uf.friend_id 
                                ELSE uf.user_id 
                            END) AS num_user_friends,
                COUNT(DISTINCT fof.friend_id) AS num_friends_of_friends
            FROM 
                users u
            LEFT JOIN 
                users_friend uf ON (uf.user_id = u.user_id OR uf.friend_id = u.user_id) AND uf.friendship = 'friends'
            LEFT JOIN (
                SELECT 
                    uf1.user_id AS friend_id
                FROM 
                    users_friend uf1
                WHERE 
                    uf1.friendship = 'friends'
            ) fof ON fof.friend_id = u.user_id
            """;
    
        // Append WHERE clause if a filter is applied to columns from the users table
        if (currentFilterQuery7.startsWith("WHERE")) {
            query += " " + currentFilterQuery7;
        }
    
        // Add GROUP BY clause
        query += " GROUP BY u.user_id, u.username";
    
        // Append HAVING clause if a filter is applied to aggregated data
        if (currentFilterQuery7.startsWith("HAVING")) {
            query += " " + currentFilterQuery7;
        }
    
        // Append ORDER BY clause if sorting is applied
        if (!currentSortQuery7.isEmpty()) {
            query += " " + currentSortQuery7;
        }
    
        try (Connection conn = setupConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {
    
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Get the table model
            DefaultTableModel model = (DefaultTableModel) jTable7.getModel();
    
            // Clear any existing data
            model.setRowCount(0);
    
            // Populate the table with the result set data
            while (rs.next()) {
                String username = rs.getString("username");
                int numUserFriends = rs.getInt("num_user_friends");
                int numFriendsOfFriends = rs.getInt("num_friends_of_friends");
    
                model.addRow(new Object[]{username, numUserFriends, numFriendsOfFriends});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void loadDataTable3() {
        // Base SQL query to fetch the sign-up list
        String query = """
            SELECT created_at, username, fullname, email
            FROM users
            """;
    
        // Append the current filter query (WHERE clause) if it exists
        if (!currentFilterQuery3.isEmpty()) {
            query += " " + currentFilterQuery3;
        }
        
        // Append the time filter query if it exists
        if (!currentTimeFilterQuery3.isEmpty()) {
            if (query.contains("WHERE")) {
                query += " AND " + currentTimeFilterQuery3.substring(6); // Remove "WHERE" from time filter query
            } else {
                query += " " + currentTimeFilterQuery3;
            }
        }
    
        // Append the current sort query (ORDER BY clause) if it exists
        if (!currentSortQuery3.isEmpty()) {
            query += " " + currentSortQuery3;
        } else {
            query += " ORDER BY created_at ASC"; // Default sorting
        }
    
        try (Connection conn = setupConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {
    
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Get the table model for jTable3
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
    
            // Clear any existing data in the table
            model.setRowCount(0);
    
            // Populate the table with the result set data
            while (rs.next()) {
                String createdAt = rs.getString("created_at");
                String username = rs.getString("username");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
    
    
                model.addRow(new Object[]{createdAt, username, fullname, email});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataTable4() {
        // Base SQL query to fetch the group-chat list
        String query = """
            SELECT created_at, group_id, group_name
            FROM chat_group
            """;
    
        // Append the current filter query (WHERE clause) if it exists
        if (!currentFilterQuery4.isEmpty()) {
            query += " " + currentFilterQuery4;
        }
    
        // Append the current sort query (ORDER BY clause) if it exists
        if (!currentSortQuery4.isEmpty()) {
            query += " " + currentSortQuery4;
        } else {
            query += " ORDER BY created_at ASC"; // Default sorting
        }
    
        try (Connection conn = setupConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {
    
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Get the table model for jTable3
            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
    
            // Clear any existing data in the table
            model.setRowCount(0);
    
            // Populate the table with the result set data
            while (rs.next()) {
                String createdAt = rs.getString("created_at");
                String GroupId = rs.getString("group_id");
                String GroupName = rs.getString("group_name");
    
                model.addRow(new Object[]{createdAt, GroupId, GroupName});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataTable5() {
        // SQL query to retrieve report details for reported users
        String query = "SELECT spam_list.report_at,spam_list.report_id, users.username, users.fullname,users.email " +
                       "FROM spam_list " +
                       "JOIN users ON spam_list.report_user = users.user_id";

        // Append the current filter query (WHERE clause) if it exists
        if (!currentFilterQuery5.isEmpty()) {
            query += " " + currentFilterQuery5;
        }
    
        // Append the current sort query (ORDER BY clause) if it exists
        if (!currentSortQuery5.isEmpty()) {
            query += " " + currentSortQuery5;
        } else {
            query += " ORDER BY spam_list.report_id DESC"; // Default sorting
        }
    
        try (Connection conn = setupConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            // Create a DefaultTableModel for jTable5
            DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
            model.setRowCount(0); // Clear existing data
    
            // Populate the table model with data from the result set
            while (rs.next()) {
                String reportTime = rs.getString("report_at");
                String spamId = rs.getString("report_id");
                String username = rs.getString("username");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                model.addRow(new Object[]{reportTime,spamId, username, fullname, email});
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading spam report data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataTable6() {
        // Base login query with time filter
        String loginQuery = """
            SELECT 
                u.user_id,
                u.username AS username, 
                COUNT(l.login_id) AS open_app_count,
                u.created_at
            FROM users u
            JOIN login_history l ON u.user_id = l.user_id
        """;
    
        // Apply the time filter if present
        if (!currentTimeFilterQuery6.isEmpty()) {
            loginQuery += " WHERE " + currentTimeFilterQuery6.replace("created_at", "login_at");
        }

        // Apply the username filter if present (this is handled separately)
        if (!currentNameFilterQuery6.isEmpty()) {
            if (loginQuery.contains("WHERE")) {
                loginQuery += " AND " + currentNameFilterQuery6.substring(6); // Remove "WHERE" from time filter query
            } else {
                loginQuery += " " + currentNameFilterQuery6;
            }
        }

        loginQuery += " GROUP BY u.user_id, u.username";

        // Apply the activity number filter (HAVING)
        if (!currentFilterQuery6.isEmpty()) {
            loginQuery += " " + currentFilterQuery6;
        }

        // Apply sorting
        if (!currentSortQuery6.isEmpty()) {
            loginQuery += " " + currentSortQuery6;
        } else {
            loginQuery += " ORDER BY u.user_id ASC";
        }

        // Default time range value
        String timeRange = "All Time";
        if (!currentTimeFilterQuery6.isEmpty()) {
            int startIndex = currentTimeFilterQuery6.indexOf("BETWEEN") + 8;
            int endIndex = currentTimeFilterQuery6.indexOf("AND") - 1;
            if (startIndex > 0 && endIndex > startIndex) {
                String startDate = currentTimeFilterQuery6.substring(startIndex, endIndex).trim();
                String endDate = currentTimeFilterQuery6.substring(currentTimeFilterQuery6.indexOf("AND") + 4).trim();
                timeRange = startDate + " to " + endDate;
            }
        }
    
        // Query for users_count with time filter
        String usersCountQuery = """
            SELECT 
                u.user_id,
                COALESCE(SUM(gm.member_count - 1), 0) AS users_count
            FROM 
                users u
            LEFT JOIN 
                (
                    SELECT DISTINCT 
                        sender_id, 
                        group_id
                    FROM 
                        message
                    WHERE 1=1
            """;
    
        if (!currentTimeFilterQuery6.isEmpty()) {
            usersCountQuery += " AND " + currentTimeFilterQuery6.replace("created_at", "sent_at");
        }
    
        usersCountQuery += """
                ) m ON u.user_id = m.sender_id
            LEFT JOIN 
                (
                    SELECT 
                        group_id,
                        COUNT(user_id) AS member_count
                    FROM 
                        group_members
                    GROUP BY 
                        group_id
                ) gm ON m.group_id = gm.group_id
            GROUP BY 
                u.user_id
        """;
    
        // Query for groups_count with time filter
        String groupsCountQuery = """
            SELECT 
                u.user_id,
                COUNT(DISTINCT CASE WHEN cg.is_chat_with_user = 0 THEN cg.group_id END) AS groups_count
            FROM 
                users u
            LEFT JOIN 
                message m ON u.user_id = m.sender_id
            LEFT JOIN 
                chat_group cg ON m.group_id = cg.group_id
            WHERE 1=1
        """;
    
        if (!currentTimeFilterQuery6.isEmpty()) {
            groupsCountQuery += " AND " + currentTimeFilterQuery6.replace("created_at", "sent_at");
        }
    
        groupsCountQuery += """
            GROUP BY 
                u.user_id
        """;
    
        try (Connection conn = setupConnection();
             Statement stmtLogin = conn.createStatement();
             Statement stmtUsersCount = conn.createStatement();
             Statement stmtGroupsCount = conn.createStatement();
             ResultSet loginResultSet = stmtLogin.executeQuery(loginQuery);
             ResultSet usersCountResultSet = stmtUsersCount.executeQuery(usersCountQuery);
             ResultSet groupsCountResultSet = stmtGroupsCount.executeQuery(groupsCountQuery)) {
    
            // Prepare table model
            DefaultTableModel model = (DefaultTableModel) jTable6.getModel();
            model.setRowCount(0);
    
            // Process results for users_count and groups_count
            Map<Integer, Integer> usersCountMap = new HashMap<>();
            while (usersCountResultSet.next()) {
                usersCountMap.put(usersCountResultSet.getInt("user_id"), usersCountResultSet.getInt("users_count"));
            }
    
            Map<Integer, Integer> groupsCountMap = new HashMap<>();
            while (groupsCountResultSet.next()) {
                groupsCountMap.put(groupsCountResultSet.getInt("user_id"), groupsCountResultSet.getInt("groups_count"));
            }
    
            // Process results for login data
            while (loginResultSet.next()) {
                int userId = loginResultSet.getInt("user_id");
                String username = loginResultSet.getString("username");
                int openAppCount = loginResultSet.getInt("open_app_count");
    
                // Fetch users_count and groups_count
                int usersCount = usersCountMap.getOrDefault(userId, 0);
                int groupsCount = groupsCountMap.getOrDefault(userId, 0);
    
                // Add row to the table
                model.addRow(new Object[]{timeRange, username, openAppCount, usersCount, groupsCount});
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

    
    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextField16 = new javax.swing.JTextField();
        jButton30 = new javax.swing.JButton();
        jComboBox12 = new javax.swing.JComboBox<>();
        jButton31 = new javax.swing.JButton();
        jComboBox13 = new javax.swing.JComboBox<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton16 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox<>();
        jTextField3 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox<>();
        jTextField4 = new javax.swing.JTextField();
        jButton18 = new javax.swing.JButton();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jButton19 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jButton20 = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox<>();
        jTextField8 = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jComboBox7 = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButton24 = new javax.swing.JButton();
        jComboBox8 = new javax.swing.JComboBox<>();
        jTextField10 = new javax.swing.JTextField();
        jButton25 = new javax.swing.JButton();
        jComboBox9 = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jButton26 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jButton27 = new javax.swing.JButton();
        jComboBox10 = new javax.swing.JComboBox<>();
        jTextField12 = new javax.swing.JTextField();
        jButton28 = new javax.swing.JButton();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jButton29 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTextField18 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jTextField19 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Username", "Status" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Account's create-time" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "UserID", "Name", "Username", "Address", "Birthday", "Gender", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow >= 0) {
                    // Assuming User ID is in the first column of the table
                    Object userIdValue = jTable1.getValueAt(selectedRow, 0);
                    if (userIdValue != null) {
                        selectedUserId = Integer.parseInt(userIdValue.toString());
                    } else {
                        selectedUserId = -1; // Reset if User ID is not valid
                    }
                }
            }
        });
        

        jButton7.setText("Add");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Update");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Delete");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Update Password");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Login History");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Friend List");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Lock/Unlock");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Filter by");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText(" Sort by");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton32.setText("Reset Password");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14)
                    .addComponent(jButton15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(jButton13))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton12)
                    .addComponent(jButton11)
                    .addComponent(jButton32))
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Manage UserList", jPanel1);

        jButton30.setText(" Sort by");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Account's create-time" }));
        jComboBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox12ActionPerformed(evt);
            }
        });

        jButton31.setText("Filter by");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Number of friends" }));
        jComboBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox13ActionPerformed(evt);
            }
        });

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Username", "Number of friends", "Numbers of friends' friends"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable7);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton31)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton30)
                        .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("UserList", jPanel5);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Time", "Username", "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jButton16.setText("Filter by");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Username", "Time" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("LoginList", jPanel2);

        jButton17.setText("Filter by");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Email" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jButton18.setText(" Sort by");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });


        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Account's create-time", "Email" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jLabel3.setText("Choose Time:");

        jLabel4.setText("From:");

        jTextField6.setText("Time1");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel5.setText("To:");

        jTextField7.setText("Time2");
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jButton19.setText("Choose");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Time", "Username", "Name", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton19)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(19, 19, 19)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(jButton18)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jTabbedPane1.addTab("SignUpList", jPanel3);

        jButton20.setText("Filter by");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });


        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jButton21.setText(" Sort by");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Account's create-time" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Create-time", "GroupId", "GroupName"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable4);
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable4.getSelectedRow();
                if (selectedRow >= 0) {
                    Object groupIdValue = jTable4.getValueAt(selectedRow, 1); // Adjust index as needed
                    if (groupIdValue != null) {
                        selectedGroupId = Integer.parseInt(groupIdValue.toString());
                    } else {
                        selectedGroupId = -1; // Reset if group_id is not valid
                    }
                }
            }
        });

        jButton22.setText("Group's User List");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("Group's Admin List");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22)
                    .addComponent(jButton23))
                .addGap(21, 21, 21))
        );

        jTabbedPane1.addTab("GroupChatList", jPanel4);

        jButton24.setText("Filter by");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Time", "Username", "Email" }));
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });

        jButton25.setText(" Sort by");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Time", "Username" }));
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Time", "Spam_Id", "Username", "Name", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable5);
        jTable5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable5.getSelectedRow();
                if (selectedRow >= 0) {
                    // Assuming User ID is in the first column of the table
                    Object reportIdValue = jTable5.getValueAt(selectedRow, 1);
                    if (reportIdValue != null) {
                        selectedReportId = Integer.parseInt(reportIdValue.toString());
                    } else {
                        selectedReportId = -1; // Reset if User ID is not valid
                    }
                }
            }
        });

        jButton26.setText("Lock user's account");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton26)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton26)
                .addGap(34, 34, 34))
        );

        jTabbedPane1.addTab("SpamList", jPanel6);

        jButton27.setText("Filter by");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name",  "Activity number < ", "Activity number = ", "Activity number > " }));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        jButton28.setText(" Sort by");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Create-time" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });

        jLabel6.setText("Choose Time:");

        jButton29.setText("Choose");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jLabel7.setText("From:");

        jTextField14.setText("Time1");
        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });

        jLabel8.setText("To:");

        jTextField15.setText("Time2");
        jTextField15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField15ActionPerformed(evt);
            }
        });

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Time", "Username", "Open app ... ", "Chat with ... users", "Chat with ... groups"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton29)
                        .addGap(77, 77, 77)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(jButton27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(127, 127, 127)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton28)
                            .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton27)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("ActiveUserList", jPanel7);

        jButton1.setText("Create Chart for new SignUp Users by years: ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        
        jButton2.setText("Create Chart for Active Users by years: ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 242, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Chart", jPanel8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    // ---------------------------------------------------ManageUserList table -------------------------------------------------
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField fullnameField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField roleField = new JTextField();
    
        Object[] formFields = {
            "Username (Required):", usernameField,
            "Password (Required):", passwordField,
            "Email (Required):", emailField,
            "Address:", addressField,
            "Full Name:", fullnameField,
            "Birthday (YYYY-MM-DD):", birthdayField,
            "Gender (male/female):", genderField,
            "Role (user/admin):", roleField
        };
    
        int option = JOptionPane.showConfirmDialog(null, formFields, "Add New User", JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = setupConnection()) {
                if (conn == null) return;
    
                // Validate required fields
                if (usernameField.getText().isEmpty() ||
                    passwordField.getPassword().length == 0 || emailField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Validate password length
                if (passwordField.getPassword().length > 20) {
                    JOptionPane.showMessageDialog(null, "Password cannot exceed 20 characters.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Validate gender
                String gender = genderField.getText().toLowerCase();
                if (gender.isEmpty() || (!gender.equals("male") && !gender.equals("female"))) {
                    JOptionPane.showMessageDialog(null, "Gender must be 'male' or 'female'.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Validate role
                String role = roleField.getText().toLowerCase();
                if (!role.equals("user") && !role.equals("admin") && !role.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Role must be 'user' or 'admin' (or leave blank).", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Validate birthday
                if (!birthdayField.getText().isEmpty() && !birthdayField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                    JOptionPane.showMessageDialog(null, "Invalid birthday format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                String query = "INSERT INTO users (username, password, email, address, fullname, birthday, gender, status, role, created_at, `lock`) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, 'inactive', ?, NOW(), false)";
    
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, usernameField.getText());
                    pstmt.setString(2, new String(passwordField.getPassword()));
                    pstmt.setString(3, emailField.getText());
                    pstmt.setString(4, addressField.getText().isEmpty() ? null : addressField.getText());
                    pstmt.setString(5, fullnameField.getText().isEmpty() ? null : fullnameField.getText());
                    pstmt.setDate(6, birthdayField.getText().isEmpty() ? null : Date.valueOf(birthdayField.getText()));
                    pstmt.setString(7, gender);
                    pstmt.setString(8, role);
    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadDataTable1();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
                 
    
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        try (Connection conn = setupConnection()) {
            if (conn == null) return;
    
            // Fetch selected user's data
            String query = "SELECT * FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Prepare form without password field
                        JTextField usernameField = new JTextField(rs.getString("username"));
                        JTextField emailField = new JTextField(rs.getString("email"));
                        JTextField addressField = new JTextField(rs.getString("address"));
                        JTextField fullnameField = new JTextField(rs.getString("fullname"));
                        JTextField birthdayField = new JTextField(rs.getString("birthday"));
                        JTextField genderField = new JTextField(rs.getString("gender"));
                        JTextField roleField = new JTextField(rs.getString("role"));
    
                        Object[] formFields = {
                            "Username (Required):", usernameField,
                            "Email (Required):", emailField,
                            "Address:", addressField,
                            "Full Name:", fullnameField,
                            "Birthday (YYYY-MM-DD):", birthdayField,
                            "Gender (male/female):", genderField,
                            "Role (user/admin):", roleField
                        };
    
                        int option = JOptionPane.showConfirmDialog(this, formFields, "Update User", JOptionPane.OK_CANCEL_OPTION);
    
                        if (option == JOptionPane.OK_OPTION) {
                            // Update user details in the database, excluding password
                            String updateQuery = "UPDATE users SET username = ?, email = ?, address = ?, fullname = ?, birthday = ?, gender = ?, role = ? WHERE user_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, usernameField.getText());
                                updateStmt.setString(2, emailField.getText());
                                updateStmt.setString(3, addressField.getText().isEmpty() ? null : addressField.getText());
                                updateStmt.setString(4, fullnameField.getText().isEmpty() ? null : fullnameField.getText());
                                try {
                                    updateStmt.setDate(5, birthdayField.getText().isEmpty() ? null : Date.valueOf(birthdayField.getText()));
                                } catch (IllegalArgumentException e) {
                                    JOptionPane.showMessageDialog(this, "Invalid birthday format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                updateStmt.setString(6, genderField.getText().isEmpty() ? null : genderField.getText());
                                updateStmt.setString(7, roleField.getText().isEmpty() ? null : roleField.getText());
                                updateStmt.setInt(8, selectedUserId);
    
                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    loadDataTable1(); // Refresh the table
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for confirmation before deleting the user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = setupConnection()) {
                if (conn == null) return;
    
                // Delete the user from the database
                String deleteQuery = "DELETE FROM users WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                    pstmt.setInt(1, selectedUserId);
    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadDataTable1(); // Refresh the table after deletion
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update the password.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        try (Connection conn = setupConnection()) {
            if (conn == null) return;
    
            // Fetch the current password of the selected user
            String query = "SELECT password FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String oldPassword = rs.getString("password");
    
                        // Create text fields for old and new passwords
                        JTextField oldPasswordField = new JTextField(20);
                        oldPasswordField.setText(oldPassword); // Set the current password (editable for visibility)
                        oldPasswordField.setEditable(false); // Make the old password field non-editable
    
                        JTextField newPasswordField = new JTextField(20); // New password field (editable)
    
                        // Create the dialog to show old and new password fields
                        Object[] message = {
                            "Old Password (Uneditable):", oldPasswordField,
                            "New Password:", newPasswordField
                        };
    
                        int option = JOptionPane.showConfirmDialog(this, message, "Update Password", JOptionPane.OK_CANCEL_OPTION);
    
                        if (option == JOptionPane.OK_OPTION) {
                            // Get the new password entered by the user
                            String newPassword = newPasswordField.getText(); // Use getText() for JTextField
    
                            // Check if the new password is empty
                            if (newPassword.trim().isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
    
                            // Proceed to update the password in the database
                            String updatePasswordQuery = "UPDATE users SET password = ? WHERE user_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery)) {
                                updateStmt.setString(1, newPassword);
                                updateStmt.setInt(2, selectedUserId);
    
                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }              
    
    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset the password.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        try (Connection conn = setupConnection()) {
            if (conn == null) return;
    
            // Fetch the email of the selected user
            String query = "SELECT email FROM users WHERE user_id = ?";
            String userEmail = null;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        userEmail = rs.getString("email");
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
    
            // Generate a random password
            String newPassword = generateRandomPassword();
    
            // Update the password in the database
            String updatePasswordQuery = "UPDATE users SET password = ? WHERE user_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, selectedUserId);
    
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully. Sending email...", "Success", JOptionPane.INFORMATION_MESSAGE);
    
                    // Send the email with the new password
                    if (sendEmail(userEmail, "Password Reset", "Your new password is: " + newPassword)) {
                        JOptionPane.showMessageDialog(this, "Email sent successfully to " + userEmail, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to send email.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reset password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to generate a random password
    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) { // Generate a 10-character password
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }
    
    // Method to send an email using JavaMail API
    private boolean sendEmail(String recipient, String subject, String content) {
        final String senderEmail = "vhoangtestmail@gmail.com"; // Replace with your email
        final String senderPassword = "elhz vuuw tbzm cfav"; // Replace with your email password
    
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP server
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    
        // Use jakarta.mail.Authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
    
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);
    
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view login history.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = setupConnection()) {
            if (conn == null) return;

            // Query to retrieve login history for the selected user
            String query = "SELECT user_id, login_at FROM login_history WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    // Prepare data for JTable
                    Vector<Vector<Object>> rowData = new Vector<>();
                    Vector<String> columnNames = new Vector<>();
                    columnNames.add("User ID");
                    columnNames.add("Login Time");

                    // Loop through the result set and populate the row data
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("user_id"));
                        row.add(rs.getDate("login_at"));
                        rowData.add(row);
                    }

                    if (rowData.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No login history found for this user.", "No History", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Create a JTable to display login history
                        JTable loginHistoryTable = new JTable(rowData, columnNames);
                        JScrollPane scrollPane = new JScrollPane(loginHistoryTable);

                        // Display the table in a dialog
                        JOptionPane.showMessageDialog(this, scrollPane, "Login History", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching login history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                         

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view their friend list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        try (Connection conn = setupConnection()) {
            if (conn == null) return;
    
            // Query to get friends for the selected user
            String query = """
                            SELECT 
                                CASE 
                                    WHEN uf.user_id = ? THEN uf.friend_id 
                                    ELSE uf.user_id 
                                END AS friend_id,
                                u.username,
                                uf.friendship
                            FROM users_friend uf
                            JOIN users u ON 
                                (uf.user_id = u.user_id AND uf.friend_id = ?)
                                OR (uf.friend_id = u.user_id AND uf.user_id = ?)
                            WHERE uf.friendship = 'friends'
                        """;
    
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);
                pstmt.setInt(2, selectedUserId);
                pstmt.setInt(3, selectedUserId);
    
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Prepare data for JTable
                    Vector<Vector<Object>> rowData = new Vector<>();
                    Vector<String> columnNames = new Vector<>();
                    columnNames.add("Friend ID");
                    columnNames.add("Username");
                    columnNames.add("Friendship Status");
    
                    // Loop through the result set and populate the row data
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("friend_id"));
                        row.add(rs.getString("username"));
                        row.add(rs.getString("friendship"));
                        rowData.add(row);
                    }
    
                    if (rowData.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No friends found for this user.", "No Friends", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Create a JTable to display the friend list
                        JTable friendListTable = new JTable(rowData, columnNames);
                        JScrollPane scrollPane = new JScrollPane(friendListTable);
    
                        // Display the table in a dialog
                        JOptionPane.showMessageDialog(this, scrollPane, "Friend List", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching friend list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
                              

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String queryCheckLock = "SELECT `lock` FROM users WHERE user_id = ?";
        String queryToggleLock = "UPDATE users SET `lock` = NOT `lock` WHERE user_id = ?"; // This toggles the lock value (0 -> 1, 1 -> 0)
    
        try (Connection conn = setupConnection();
             PreparedStatement checkStmt = conn.prepareStatement(queryCheckLock);
             PreparedStatement toggleStmt = conn.prepareStatement(queryToggleLock)) {
    
            // Check the current lock status of the user
            checkStmt.setInt(1, selectedUserId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    boolean isLocked = rs.getBoolean("lock");
    
                    // Perform the toggle (lock -> unlock or unlock -> lock)
                    toggleStmt.setInt(1, selectedUserId);
                    int rowsAffected = toggleStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Show success message based on the current lock status
                        if (isLocked) {
                            JOptionPane.showMessageDialog(this, "User account has been successfully Unlocked.", "Success UnLock", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "User account has been successfully Locked.", "Success Lock", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to toggle the lock status. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error occurred while toggling the lock status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } 
                                             

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField1.getText().trim();  // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery1 = "";  // Clear the filter query if input is empty
            loadDataTable1();  // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox1.getSelectedItem();
    
        // Construct the WHERE clause based on the selected filter
        switch (filterOption) {
            case "Name":
                currentFilterQuery1 = "WHERE fullname LIKE '%" + filterValue + "%'";
                break;
            case "Username":
                currentFilterQuery1 = "WHERE username LIKE '%" + filterValue + "%'";
                break;
            case "Status":
                if (filterValue.equalsIgnoreCase("active") || filterValue.equalsIgnoreCase("inactive")) {
                    currentFilterQuery1 = "WHERE status = '" + filterValue + "'";
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter either 'active' or 'inactive' in the state filter.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable1();
    }                                         
    

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox2.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        if (sortBy.equals("Name")) {
            currentSortQuery1 = "ORDER BY fullname";  // Sort by Name (fullname)
        } else if (sortBy.equals("Account's create-time")) {
            currentSortQuery1 = "ORDER BY created_at";  // Sort by Account's create-time (assuming you have a create_time column)
        } else {
            currentSortQuery1 = "";  // Clear the sort query if no valid option is selected
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable1();
    }                                         

    //----------------------------------------------Login List Table -------------------------------------------------

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField3.getText().trim();  // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            loadDataTable2(null);
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox3.getSelectedItem();
    
        String query = "";
    
        // Construct the SQL query based on the selected filter
        switch (filterOption) {
            case "Name":
                query = "SELECT login_history.login_at, users.username, users.fullname " +
                        "FROM login_history " +
                        "JOIN users ON login_history.user_id = users.user_id " +
                        "WHERE users.fullname LIKE '%" + filterValue + "%'";
                break;
    
            case "Username":
                query = "SELECT login_history.login_at, users.username, users.fullname " +
                        "FROM login_history " +
                        "JOIN users ON login_history.user_id = users.user_id " +
                        "WHERE users.username LIKE '%" + filterValue + "%'";
                break;
    
            case "Time":
                // Check if the filterValue is a valid date or timestamp
                if (filterValue.matches("\\d{4}-\\d{2}-\\d{2}")) { // Check for date in YYYY-MM-DD format
                    query = "SELECT login_history.login_at, users.username, users.fullname " +
                            "FROM login_history " +
                            "JOIN users ON login_history.user_id = users.user_id " +
                            "WHERE DATE(login_history.login_at) = '" + filterValue + "'";
                } else if (filterValue.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) { // Check for timestamp
                    query = "SELECT login_history.login_at, users.username, users.fullname " +
                            "FROM login_history " +
                            "JOIN users ON login_history.user_id = users.user_id " +
                            "WHERE login_history.login_at = '" + filterValue + "'";
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Please enter a valid date (YYYY-MM-DD) or timestamp (YYYY-MM-DD HH:MM:SS) for the Time filter.", 
                            "Invalid Input", 
                            JOptionPane.WARNING_MESSAGE);
                    return; // Return if invalid input is provided for Time
                }
                break;
    
            default:
                // Default case if no valid option is selected
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Call the method to load data based on the filter
        loadDataTable2(query);  // Pass the query with filter to load the data
    }
      
    //SignUp List Table 3

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField4.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery3 = ""; // Clear the filter query if input is empty
            loadDataTable3(); // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox4.getSelectedItem();
    
        // Construct the WHERE clause based on the selected filter
        switch (filterOption) {
            case "Name":
                currentFilterQuery3 = "WHERE fullname LIKE '%" + filterValue + "%'";
                break;
            case "Email":
                currentFilterQuery3 = "WHERE email LIKE '%" + filterValue + "%'";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable3();
    }
    

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox5.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        if (sortBy.equals("Name")) {
            currentSortQuery3 = "ORDER BY fullname";  // Sort by full name
        } else if (sortBy.equals("Account's create-time")) {
            currentSortQuery3 = "ORDER BY created_at";  // Sort by account's create-time
        } else if (sortBy.equals("Email")) {
            currentSortQuery3 = "ORDER BY email";  // Sort by account's create-time   
        } else {
            currentSortQuery3 = "";  // Clear the sort query if no valid option is selected
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable3();
    }    

    private boolean isValidDateOrTimestamp(String value) {
        // Regex for date in YYYY-MM-DD format
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return true;
        }
        // Regex for full timestamp in YYYY-MM-DD HH:MM:SS format
        if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return true;
        }
        return false;
    }    

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {
        // Retrieve the time values from the text fields
        String time1 = jTextField6.getText().trim(); // Time1
        String time2 = jTextField7.getText().trim(); // Time2
    
        // Validate the inputs (ensure they are not empty)
        if (time1.isEmpty() || time2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Time1 and Time2.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Validate the format of the time inputs
        if (!(isValidDateOrTimestamp(time1) && isValidDateOrTimestamp(time2))) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid time format. Use either YYYY-MM-DD or YYYY-MM-DD HH:MM:SS.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Construct the time filter query based on the time range
        if (time1.matches("\\d{4}-\\d{2}-\\d{2}") && time2.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // Both inputs are dates (YYYY-MM-DD)
            currentTimeFilterQuery3 = "WHERE DATE(created_at) BETWEEN '" + time1 + "' AND '" + time2 + "'";
        } else {
            // At least one input is a full timestamp (YYYY-MM-DD HH:MM:SS)
            currentTimeFilterQuery3 = "WHERE created_at BETWEEN '" + time1 + "' AND '" + time2 + "'";
        }
    
        // Reload the data table with the updated filter
        loadDataTable3();
    }


    //---------------------------------GroupChat List table 4-------------------------------------------

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField8.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery4 = ""; // Clear the filter query if input is empty
            loadDataTable4(); // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox6.getSelectedItem();
    
        // Construct the WHERE clause based on the selected filter
        switch (filterOption) {
            case "Name":
                currentFilterQuery4 = "WHERE group_name LIKE '%" + filterValue + "%'";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable4();
    }
    

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox7.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        if (sortBy.equals("Name")) {
            currentSortQuery4 = "ORDER BY group_name";  // Sort by full name
        } else if (sortBy.equals("Account's create-time")) {
            currentSortQuery4 = "ORDER BY created_at";  // Sort by account's create-time
        } else {
            currentSortQuery4 = "";  // Clear the sort query if no valid option is selected
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable4();
    }

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedGroupId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a group from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String query = "SELECT group_members.group_id, chat_group.group_name, " +
                       "users.user_id, users.username, users.fullname " +
                       "FROM group_members " +
                       "JOIN users ON group_members.user_id = users.user_id " +
                       "JOIN chat_group ON group_members.group_id = chat_group.group_id " +
                       "WHERE group_members.group_id = ?";
    
        try (Connection conn = setupConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedGroupId);
    
            try (ResultSet rs = stmt.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("User ID");
                model.addColumn("Username");
                model.addColumn("Fullname");
    
                String groupName = "";
                boolean firstRow = true;
                while (rs.next()) {
                    if (firstRow) {
                        groupName = rs.getString("group_name");
                        firstRow = false;
                    }
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String fullname = rs.getString("fullname");
                    model.addRow(new Object[]{userId, username, fullname});
                }
    
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
    
                JDialog dialog = new JDialog();
                dialog.setTitle("Group's User List - " + groupName);
                dialog.setSize(600, 400);
                dialog.add(scrollPane);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error fetching group members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {
        // Check if a valid group ID is selected
        if (selectedGroupId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a group from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // SQL query to fetch group admins
        String query = "SELECT group_members.group_id, chat_group.group_name, " +
                       "users.user_id, users.username, users.fullname " +
                       "FROM group_members " +
                       "JOIN users ON group_members.user_id = users.user_id " +
                       "JOIN chat_group ON group_members.group_id = chat_group.group_id " +
                       "WHERE group_members.group_id = ? AND group_members.is_admin = 'yes'";
    
        try (Connection conn = setupConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedGroupId); // Set the selected group ID
    
            try (ResultSet rs = stmt.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("User ID");
                model.addColumn("Username");
                model.addColumn("Fullname");
    
                String groupName = "";
                boolean firstRow = true;
                while (rs.next()) {
                    if (firstRow) {
                        groupName = rs.getString("group_name");
                        firstRow = false;
                    }
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String fullname = rs.getString("fullname");
                    model.addRow(new Object[]{userId, username, fullname});
                }
    
                // If no admins are found, show a message and return
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "No admins found for the selected group.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
    
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
    
                JDialog dialog = new JDialog();
                dialog.setTitle("Group's Admin List - " + groupName);
                dialog.setSize(600, 400);
                dialog.add(scrollPane);
                dialog.setLocationRelativeTo(null); // Center the dialog
                dialog.setVisible(true);
    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error fetching group admins: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-------------------------------SpamList table 5---------------------

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField10.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery5 = ""; // Clear the filter query if input is empty
            loadDataTable5(); // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox8.getSelectedItem();
    
        // Construct the WHERE clause based on the selected filter
        switch (filterOption) {
            case "Time":
                if (filterValue.matches("\\d{4}-\\d{2}-\\d{2}")) { // Check for date in YYYY-MM-DD format
                    currentFilterQuery5 = "WHERE DATE(spam_list.report_at) = '" + filterValue + "'";
                } else if (filterValue.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) { // Check for timestamp
                    currentFilterQuery5 = "WHERE spam_list.report_at = '" + filterValue + "'";
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Please enter a valid date (YYYY-MM-DD) or timestamp (YYYY-MM-DD HH:MM:SS) for the Time filter.", 
                            "Invalid Input", 
                            JOptionPane.WARNING_MESSAGE);
                    return; // Return if invalid input is provided for Time
                }
                break;
            case "Username":
                currentFilterQuery5 = "WHERE users.username LIKE '%" + filterValue + "%'";
                break;
            case "Email":
                currentFilterQuery5 = "WHERE users.email LIKE '%" + filterValue + "%'";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable5();
    }
    

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox9.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        switch (sortBy) {
            case "Username":
                currentSortQuery5 = "ORDER BY users.username";
                break;
            case "Time":
                currentSortQuery5 = "ORDER BY spam_list.report_at";
                break;
            default:
                currentSortQuery5 = ""; // Clear the sort query if no valid option is selected
                break;
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable5();
    }

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedReportId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String queryFindUserId = "SELECT report_user FROM spam_list WHERE report_id = ?";
        String queryCheckLock = "SELECT `lock` FROM users WHERE user_id = ?";
        String queryLockUser = "UPDATE users SET `lock` = 1 WHERE user_id = ?";
    
        try (Connection conn = setupConnection();
             PreparedStatement findUserStmt = conn.prepareStatement(queryFindUserId);
             PreparedStatement checkStmt = conn.prepareStatement(queryCheckLock);
             PreparedStatement lockStmt = conn.prepareStatement(queryLockUser)) {
    
            // Find the user_id linked to the selected report_id
            findUserStmt.setInt(1, selectedReportId);
            int userId;
            try (ResultSet rs = findUserStmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("report_user");
                } else {
                    JOptionPane.showMessageDialog(this, "Report not found or invalid report selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
    
            // Check if the user is already locked
            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    boolean isLocked = rs.getBoolean("lock");
                    if (isLocked) {
                        JOptionPane.showMessageDialog(this, "The account of User ID " + userId + " is already locked.", "Already Locked", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
    
            // Lock the user's account
            lockStmt.setInt(1, userId);
            int rowsAffected = lockStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User account of User ID " + userId + " has been successfully locked.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to lock the account for User ID " + userId + ". Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error occurred while locking the account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //------------------------------Active UserList table 6------------------

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField12.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery6 = ""; // Clear the filter query if input is empty
            currentNameFilterQuery6 = "";
            loadDataTable6(); // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox10.getSelectedItem();
    
        // Construct the WHERE clause based on the selected filter
        switch (filterOption) {
            case "Name":
                currentNameFilterQuery6 = "WHERE u.username LIKE '%" + filterValue + "%'";
                currentFilterQuery6 = ""; // Reset the activity filter
                break;
            case "Activity number < ":
                try {
                    int activityCount = Integer.parseInt(filterValue); // Convert to integer for comparison
                    currentFilterQuery6 = "HAVING COUNT(l.login_id) < " + activityCount;
                    currentNameFilterQuery6 = ""; // Reset the username filter
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for the filter.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            case "Activity number > ":
                try {
                    int activityCount = Integer.parseInt(filterValue); // Convert to integer for comparison
                    currentFilterQuery6 = "HAVING COUNT(l.login_id) > " + activityCount;
                    currentNameFilterQuery6 = ""; // Reset the username filter
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for the filter.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            case "Activity number = ":
                try {
                    int activityCount = Integer.parseInt(filterValue); // Convert to integer for comparison
                    currentFilterQuery6 = "HAVING COUNT(l.login_id) = " + activityCount;
                    currentNameFilterQuery6 = ""; // Reset the username filter
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for the filter.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable6();
    }
    
    
    


    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox11.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        switch (sortBy) {
            case "Name":
                currentSortQuery6 = "ORDER BY u.username";
                break;
            case "Create-time":
                currentSortQuery6 = "ORDER BY u.created_at";
                break;
            default:
                currentSortQuery6 = ""; // Clear the sort query if no valid option is selected
                break;
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable6();
    }


    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {
        // Retrieve the time values from the text fields
        String time1 = jTextField14.getText().trim(); // Time1
        String time2 = jTextField15.getText().trim(); // Time2
    
        // Validate the input for Time1 (ensure it is not empty)
        if (time1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Time1.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Validate the format of the time inputs
        if (!isValidDateOrTimestamp(time1) || (!time2.isEmpty() && !isValidDateOrTimestamp(time2))) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid time format. Use either YYYY-MM-DD or YYYY-MM-DD HH:MM:SS.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Adjust the upper bound (time2)
        if (time2.isEmpty()) {
            time2 = "NOW()"; // Use NOW() as the upper bound if Time2 is not provided
        } else if (time2.matches("\\d{4}-\\d{2}-\\d{2}")) {
            time2 += " 23:59:59"; // Append end-of-day time
        }
    
        // Adjust the lower bound (time1)
        if (time1.matches("\\d{4}-\\d{2}-\\d{2}")) {
            time1 += " 00:00:00"; // Append start-of-day time for consistency
        }
    
        // Construct the time filter query based on the time range
        if (time2.equals("NOW()")) {
            currentTimeFilterQuery6 = "created_at BETWEEN '" + time1 + "' AND " + time2;
        } else {
            currentTimeFilterQuery6 = "created_at BETWEEN '" + time1 + "' AND '" + time2 + "'";
        }
    
        // Reload the data table with the updated filter
        loadDataTable6();
    }
    
    //--------------------------------Create CHART---------------------------- 
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String year = jTextField18.getText().trim(); // Get the year from the text field
        
        // Validate the year input
        if (year.isEmpty() || !year.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid year (e.g., 2024).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Map<Integer, Integer> signUpData = new HashMap<>(); // To store month number and sign-up count
        
        // Initialize the map with 0 counts for each month (1-12)
        for (int i = 1; i <= 12; i++) {
            signUpData.put(i, 0);
        }
        
        // Corrected query with a placeholder for the year
        String query = """
            SELECT MONTH(created_at) AS month, COUNT(*) AS count 
            FROM users 
            WHERE YEAR(created_at) = ? 
            GROUP BY MONTH(created_at)
            ORDER BY MONTH(created_at);
        """;
        
        try (Connection conn = setupConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        
            // Set the year parameter in the query
            pstmt.setInt(1, Integer.parseInt(year));
            ResultSet rs = pstmt.executeQuery();
            
            // Process the result set
            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                signUpData.put(month, count); // Update count for the month
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int month = 1; month <= 12; month++) {
            dataset.addValue(signUpData.get(month), "Sign-ups", String.valueOf(month));
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "New Sign-ups by Month for Year " + year, 
            "Month", 
            "Number of Sign-ups", 
            dataset, 
            PlotOrientation.VERTICAL, 
            false, true, false);
        
        // Customize chart (optional)
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        
        // Display chart in jPanel9
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(jPanel9.getWidth(), jPanel9.getHeight()));
        
        jPanel9.removeAll(); // Clear previous content
        jPanel9.setLayout(new BorderLayout());
        jPanel9.add(chartPanel, BorderLayout.CENTER);
        jPanel9.validate(); // Refresh panel
    }

   
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        String year = jTextField19.getText().trim(); // Get the year from the text field
    
        // Validate the year input
        if (year.isEmpty() || !year.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid year (e.g., 2024).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Map<Integer, Integer> loginData = new HashMap<>(); // To store month number and login count
    
        // Initialize the map with 0 counts for each month (1-12)
        for (int i = 1; i <= 12; i++) {
            loginData.put(i, 0);
        }
    
        // Query to get unique user login counts grouped by month
        String query = """
            SELECT MONTH(login_at) AS month, COUNT(DISTINCT user_id) AS count 
            FROM login_history 
            WHERE YEAR(login_at) = ? 
            GROUP BY MONTH(login_at)
            ORDER BY MONTH(login_at);
        """;
    
        try (Connection conn = setupConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            // Set the year parameter in the query
            pstmt.setInt(1, Integer.parseInt(year));
            ResultSet rs = pstmt.executeQuery();
    
            // Process the result set
            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                loginData.put(month, count); // Update count for the month
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Create the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int month = 1; month <= 12; month++) {
            dataset.addValue(loginData.get(month), "Active Users", String.valueOf(month));
        }
    
        JFreeChart chart = ChartFactory.createBarChart(
            "Active Users by Month for Year " + year, 
            "Month", 
            "Number of Active Users", 
            dataset, 
            PlotOrientation.VERTICAL, 
            false, true, false);
    
        // Customize chart (optional)
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
    
        // Display chart in jPanel9
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(jPanel9.getWidth(), jPanel9.getHeight()));
    
        jPanel9.removeAll(); // Clear previous content
        jPanel9.setLayout(new BorderLayout());
        jPanel9.add(chartPanel, BorderLayout.CENTER);
        jPanel9.validate(); // Refresh panel
    }
    
    
    
    
    
    

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed


    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox7ActionPerformed


    private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox8ActionPerformed

    private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox9ActionPerformed


    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox10ActionPerformed


    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox11ActionPerformed


    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox12ActionPerformed

    //----------------------------User Friend List table 7----------------------
    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {
        // Get the selected sorting criteria from the combo box
        String sortBy = jComboBox12.getSelectedItem().toString();
    
        // Construct the ORDER BY clause based on the selected sort criteria
        if (sortBy.equals("Name")) {
            currentSortQuery7 = "ORDER BY u.username";  // Sort by username
        } else if (sortBy.equals("Account's create-time")) {
            currentSortQuery7 = "ORDER BY u.created_at";  // Sort by account's create-time
        } else {
            currentSortQuery7 = "";  // Clear the sort query if no valid option is selected
        }
    
        // Reload the data with the current filter and updated sort
        loadDataTable7();
    }
    

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {
        String filterValue = jTextField16.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            currentFilterQuery7 = ""; // Clear the filter query if input is empty
            loadDataTable7(); // Reload without any filter
            return;
        }
    
        // Get selected filter option from combo box
        String filterOption = (String) jComboBox13.getSelectedItem();
    
        // Construct the WHERE or HAVING clause based on the selected filter
        switch (filterOption) {
            case "Name":
                currentFilterQuery7 = "WHERE u.username LIKE '%" + filterValue + "%'";
                break;
            case "Number of friends":
                try {
                    int numFriends = Integer.parseInt(filterValue); // Ensure the input is numeric
                    currentFilterQuery7 = "HAVING COUNT(DISTINCT CASE WHEN uf.user_id = u.user_id THEN uf.friend_id ELSE uf.user_id END) = " + numFriends;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for the number of friends.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid filter option selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Reload the data with the updated filter and current sort
        loadDataTable7();
    }
    
    
    

    private void jComboBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox13ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
