/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.example.Chat.app.Admin;
import java.sql.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class AdminUI extends javax.swing.JFrame {

    //Manage UserList table 1
    private int selectedUserId = -1; // Initialize as no user selected
    private String currentFilterQuery1 = "";  // Stores the current WHERE clause
    private String currentSortQuery1 = "";    // Stores the current ORDER BY clause  

    //UserList table 7
    private String currentFilterQuery7 = "";  // Stores the current WHERE clause
    private String currentSortQuery7 = "";    // Stores the current ORDER BY clause  

    /**
     * Creates new form TestUI
     */
    public AdminUI() {
        initComponents();
        loadDataTable1();
        loadDataTable2(null);
        loadDataTable7();
    }

    /**
     * Establishes a connection to the database.
     * 
     * @return a Connection object or null if the connection fails
     */
    private Connection setupConnection() {
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/chat_app"; 
        String USER = "root";
        String PASSWORD = "hoang123";
        try {
            // Load JDBC driver
            Class.forName(JDBC_DRIVER);
            // Establish connection
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
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
        // Base query with placeholders for WHERE and HAVING clauses
        String query = """
            SELECT 
                u.username, 
                COUNT(uf.friend_id) AS num_user_friends,
                COALESCE(SUM((SELECT COUNT(*) 
                              FROM users_friend uf2 
                              WHERE uf2.user_id = uf.friend_id)), 0) AS num_friends_of_friends
            FROM 
                users u
            LEFT JOIN 
                users_friend uf ON u.user_id = uf.user_id
            """;
    
        // Append the WHERE clause if a filter is applied to columns from the users table
        if (currentFilterQuery7.startsWith("WHERE")) {
            query += " " + currentFilterQuery7;
        }
    
        // Add GROUP BY clause
        query += " GROUP BY u.user_id, u.username";
    
        // Append the HAVING clause if a filter is applied to aggregated data
        if (currentFilterQuery7.startsWith("HAVING")) {
            query += " " + currentFilterQuery7;
        }
    
        // Append the ORDER BY clause if sorting is applied
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
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(98, 98, 98)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(jButton9))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton10)
                    .addComponent(jButton12)
                    .addComponent(jButton11))
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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jButton18.setText(" Sort by");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Account's create-time" }));
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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton19)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jButton21.setText(" Sort by");

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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable4);

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

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Time", "Username" }));
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });

        jButton25.setText(" Sort by");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Time", "Username" }));
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable5);

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

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Activity number" }));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        jButton28.setText(" Sort by");

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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
                        .addGap(39, 39, 39)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(47, 47, 47))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(jButton27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(77, 77, 77)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

        jButton2.setText("Create Chart for Active Users by years: ");

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
        JTextField userIdField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField fullnameField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField roleField = new JTextField();
    
        Object[] formFields = {
            "User ID (Required):", userIdField,
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
                if (userIdField.getText().isEmpty() || usernameField.getText().isEmpty() ||
                    passwordField.getPassword().length == 0 || emailField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Validate numeric User ID
                int userId;
                try {
                    userId = Integer.parseInt(userIdField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "User ID must be a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
    
                String query = "INSERT INTO users (user_id, username, password, email, address, fullname, birthday, gender, status, role, created_at, `lock`) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'inactive', ?, NOW(), false)";

    
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, usernameField.getText());
                    pstmt.setString(3, new String(passwordField.getPassword()));
                    pstmt.setString(4, emailField.getText());
                    pstmt.setString(5, addressField.getText().isEmpty() ? null : addressField.getText());
                    pstmt.setString(6, fullnameField.getText().isEmpty() ? null : fullnameField.getText());
                    pstmt.setDate(7, birthdayField.getText().isEmpty() ? null : Date.valueOf(birthdayField.getText()));
                    pstmt.setString(8, gender);
                    pstmt.setString(9, role);
    
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
            String query = "SELECT uf.friend_id, u.username, uf.friendship " +
                           "FROM users_friend uf " +
                           "JOIN users u ON uf.friend_id = u.user_id " +
                           "WHERE uf.user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedUserId);
    
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
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            // Assuming user_id is in the first column
            Object userIdObject = jTable1.getValueAt(selectedRow, 0); 
            int userId = Integer.parseInt(userIdObject.toString());
    
            // Assuming 'lock' is in a specific column index, e.g., column 4 (update this index as per your table structure)
            int lockColumnIndex = 4; // Replace with the actual column index for 'lock'
    
            Object lockObject = jTable1.getValueAt(selectedRow, lockColumnIndex);
            boolean currentLockStatus = Boolean.parseBoolean(lockObject.toString());
    
            // Toggle lock status
            boolean newLockStatus = !currentLockStatus;
    
            // Update in database
            try (Connection conn = setupConnection()) {
                if (conn == null) return;
    
                String query = "UPDATE users SET `lock` = ? WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setBoolean(1, newLockStatus);
                    pstmt.setInt(2, userId);
    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Update the table view
                        jTable1.setValueAt(newLockStatus, selectedRow, lockColumnIndex);
    
                        String statusMessage = newLockStatus ? "User locked successfully." : "User unlocked successfully.";
                        JOptionPane.showMessageDialog(null, statusMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update user lock status.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid User ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox8ActionPerformed

    private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox9ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox10ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox11ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox12ActionPerformed

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
            case "Number of Friends":
                try {
                    int numFriends = Integer.parseInt(filterValue); // Ensure the input is numeric
                    currentFilterQuery7 = "HAVING COUNT(uf.friend_id) = " + numFriends;
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
