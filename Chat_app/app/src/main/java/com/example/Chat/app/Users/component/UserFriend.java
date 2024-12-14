/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.example.Chat.app.Users.component;

import java.sql.*;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.example.Chat.app.Users.database.DatabaseConnection;

/**
 *
 * @author ASUS
 */
public class UserFriend extends javax.swing.JPanel {
    private int userId;
    private String selectedUsername;//Table 1 Friend List
    private String selectedUsername2;//Table 2 Friend Request

    private String currentFilterQuery1 = ""; // Stores the current WHERE clause
    private String currentFilterQuery2 = ""; // Stores the current WHERE clause
    DatabaseConnection db = DatabaseConnection.getInstance();
    Connection conn = DatabaseConnection.getConnection();
    /**
     * Creates new form UserFriend
     */
    public UserFriend() {
        initComponents();
    }

    public void setId(String userId) {
        this.userId = Integer.parseInt(userId);
        loadDataTable1();
        loadDataTable2();
    }

    private void loadDataTable1() {
        // Query to fetch friends with the specified conditions
        String query = """
                SELECT u.username,
                       CASE
                           WHEN u.status = 'active' THEN 'online'
                           ELSE 'offline'
                       END AS status
                FROM users_friend uf
                JOIN users u ON 
                    (uf.friend_id = u.user_id AND uf.user_id = ?)
                    OR (uf.user_id = u.user_id AND uf.friend_id = ?)
                WHERE uf.friendship = 'friends'
            """;
    
        // Append the current filter query (WHERE clause) if it exists
        if (!currentFilterQuery1.isEmpty()) {
            if (query.contains("WHERE")) {
                query += " AND " + currentFilterQuery1.substring(6); // Remove "WHERE" from time filter query
            } else {
                query += " " + currentFilterQuery1;
            }
        }
    
        // Check the checkbox state and filter by status
        if (jCheckBox1.isSelected()) {
            query += " AND u.status = 'active'"; // Filter for online users
        }
    
        try (
                PreparedStatement pstmt = conn != null ? conn.prepareStatement(query) : null) {
    
            if (pstmt == null) {
                JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Set the current user ID for both cases
            pstmt.setInt(1, userId); // Current user as initiator
            pstmt.setInt(2, userId); // Current user as recipient
    
            try (ResultSet rs = pstmt.executeQuery()) {
    
                // Get the table model
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    
                // Clear any existing data
                model.setRowCount(0);
    
                // Populate the table with data
                while (rs.next()) {
                    String username = rs.getString("username");
                    String status = rs.getString("status");
    
                    // Add data to the table
                    model.addRow(new Object[] { username, status });
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void loadDataTable2() {
        // Query to fetch friends with the specified conditions
        String query = """
                    SELECT u.username
                    FROM users_friend uf
                    JOIN users u ON uf.friend_id = u.user_id
                    WHERE uf.user_id = ? AND uf.friendship = 'pending'
                """;

        if (!currentFilterQuery2.isEmpty()) {
            if (query.contains("WHERE")) {
                query += " AND " + currentFilterQuery2.substring(6); // Remove "WHERE" from time filter query
            } else {
                query += " " + currentFilterQuery2;
            }
        }

        try (
                PreparedStatement pstmt = conn != null ? conn.prepareStatement(query) : null) {

            if (pstmt == null) {
                JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Set the current user ID
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null) {
                    JOptionPane.showMessageDialog(this, "No friends found.", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Get the table model
                DefaultTableModel model = (DefaultTableModel) jTable2.getModel();

                // Clear any existing data
                model.setRowCount(0);

                // Populate the table with data
                while (rs.next()) {
                    String username = rs.getString("username");

                    // Add data to the table
                    model.addRow(new Object[] { username });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton20 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField7 = new javax.swing.JTextField();
        jButton19 = new javax.swing.JButton();

        jPanel4.setBackground(new java.awt.Color(204, 102, 0));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 0)));

        jLabel1.setText("List of friends");

        jTextField6.setText("Enter name to search");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jButton14.setText("Search");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Unfriend");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Block");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Online");
        jCheckBox1.addActionListener(evt -> loadDataTable1());

        jButton20.setText("Create group");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Username", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow >= 0) {
                    // Assuming Username is in the first column of the table
                    Object usernameValue = jTable1.getValueAt(selectedRow, 0);
                    if (usernameValue != null) {
                        selectedUsername = usernameValue.toString();
                    } else {
                        selectedUsername = null; // Reset if User ID is not valid
                    }
                }
            }
        });


        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14)
                    .addComponent(jButton15)
                    .addComponent(jButton16)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton20))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addGap(78, 78, 78)
                        .addComponent(jButton20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 102, 0));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 0)));

        jLabel2.setText("List add friends");

        jButton17.setText("Decline");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Accept");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Username"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(jTable2);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable2.getSelectedRow();
                if (selectedRow >= 0) {
                    // Assuming Username is in the first column of the table
                    Object usernameValue = jTable2.getValueAt(selectedRow, 0);
                    if (usernameValue != null) {
                        selectedUsername2 = usernameValue.toString();
                    } else {
                        selectedUsername2 = null; // Reset if User ID is not valid
                    }
                }
            }
        });

        jTextField7.setText("Enter name to search");
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jButton19.setText("Search");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton19)
                            .addComponent(jButton18)
                            .addComponent(jButton17))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton19)
                        .addGap(101, 101, 101)
                        .addComponent(jButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane10)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField6ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField6ActionPerformed
        String filterValue = jTextField6.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            currentFilterQuery1 = ""; // Clear the filter query if input is empty
            loadDataTable1(); // Reload without any filter
            return;
        }

        currentFilterQuery1 = "WHERE username LIKE '%" + filterValue + "%'";

        // Reload the data with the updated filter and current sort
        loadDataTable1();
    }

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to unfriend.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for confirmation before unfriending the user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to unfriend " + selectedUsername + "?", "Confirm Unfriend", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String deleteQuery = """
                DELETE FROM users_friend
                WHERE 
                    (user_id = ? AND friend_id = (SELECT user_id FROM users WHERE username = ?))
                    OR 
                    (friend_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?))
            """;
    
            try (
                 PreparedStatement pstmt = conn != null ? conn.prepareStatement(deleteQuery) : null) {
    
                if (pstmt == null) {
                    JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Set parameters for the query
                pstmt.setInt(1, userId); // Current user's ID as user_id
                pstmt.setString(2, selectedUsername); // Friend's username for first condition
                pstmt.setInt(3, userId); // Current user's ID as friend_id
                pstmt.setString(4, selectedUsername); // Friend's username for reciprocal condition
    
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Friend removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataTable1(); // Refresh the table after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove friend. The user might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error unfriending user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to block.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for confirmation before blocking the user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to block " + selectedUsername + "?", "Confirm Block", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String updateQuery = """
                UPDATE users_friend
                SET friendship = 'blocked'
                WHERE 
                    (user_id = ? AND friend_id = (SELECT user_id FROM users WHERE username = ?))
                    OR 
                    (friend_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?))
            """;
    
            try (
                 PreparedStatement pstmt = conn != null ? conn.prepareStatement(updateQuery) : null) {
    
                if (pstmt == null) {
                    JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Set parameters for the query
                pstmt.setInt(1, userId); // Current user's ID as user_id
                pstmt.setString(2, selectedUsername); // Selected username
                pstmt.setInt(3, userId); // Current user's ID as friend_id
                pstmt.setString(4, selectedUsername); // Selected username again for the reciprocal check
    
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User blocked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataTable1(); // Refresh the table after updating
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to block the user. The user might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error blocking user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private int getUserId(String username) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1; // User not found
    }

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to create a group.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for the new group name
        String groupName = JOptionPane.showInputDialog(this, "Enter the name of the new group:", "Create Group", JOptionPane.PLAIN_MESSAGE);
        if (groupName == null || groupName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Group creation canceled or invalid group name entered.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String insertGroupQuery = "INSERT INTO chat_group (group_name, created_by, is_chat_with_user) VALUES (?, ?, 0)";
        String insertMemberQuery = "INSERT INTO group_members (group_id, user_id, is_admin) VALUES (?, ?, ?)";
    
        try {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            conn.setAutoCommit(false); // Begin transaction
    
            // Insert into chat_group
            int groupId;
            try (PreparedStatement pstmtGroup = conn.prepareStatement(insertGroupQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmtGroup.setString(1, groupName);
                pstmtGroup.setInt(2, userId); // Created by this user
                pstmtGroup.executeUpdate();
    
                // Retrieve the generated group_id
                try (ResultSet generatedKeys = pstmtGroup.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        groupId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve group_id.");
                    }
                }
            }
    
            // Insert the current user as admin
            try (PreparedStatement pstmtMember = conn.prepareStatement(insertMemberQuery)) {
                pstmtMember.setInt(1, groupId);
                pstmtMember.setInt(2, userId); // Current user's ID
                pstmtMember.setString(3, "yes"); // Admin
                pstmtMember.executeUpdate();

                int selectedUserId = getUserId(selectedUsername);
                // Insert the selected user as a member
                pstmtMember.setInt(1, groupId);
                pstmtMember.setInt(2, selectedUserId); // Selected user's ID
                pstmtMember.setString(3, "no"); // Not admin
                pstmtMember.executeUpdate();
            }
    
            conn.commit(); // Commit transaction
    
            JOptionPane.showMessageDialog(this, "Group created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDataTable1(); // Refresh table to reflect the new group
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating group: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException resetEx) {
                resetEx.printStackTrace();
            }
        }
    }
    

    //Add Friend List

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUsername2 == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to Decline friend request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for confirmation before unfriending the user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Decline friend request " + selectedUsername2 + "?", "Confirm Decline", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String deleteQuery = """
                DELETE FROM users_friend
                WHERE user_id = ? AND friend_id = (
                    SELECT user_id FROM users WHERE username = ?
                )
            """;
    
            try (
                 PreparedStatement pstmt = conn != null ? conn.prepareStatement(deleteQuery) : null) {
    
                if (pstmt == null) {
                    JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Set parameters for the query
                pstmt.setInt(1, userId); // Current user's ID
                pstmt.setString(2, selectedUsername2); // Friend's username
    
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Friend request declined successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataTable2(); // Refresh the table after decline
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to decline friend request. The user might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error decline user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedUsername2 == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to Accept friend request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Prompt for confirmation before blocking the user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Accept friend request " + selectedUsername2 + "?", "Confirm Accept", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String updateQuery = """
                UPDATE users_friend
                SET friendship = 'friends'
                WHERE user_id = ? AND friend_id = (
                    SELECT user_id FROM users WHERE username = ?
                )
            """;
    
            try (
                 PreparedStatement pstmt = conn != null ? conn.prepareStatement(updateQuery) : null) {
    
                if (pstmt == null) {
                    JOptionPane.showMessageDialog(this, "Failed to establish a database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Set parameters for the query
                pstmt.setInt(1, userId); // Current user's ID
                pstmt.setString(2, selectedUsername2); // Friend's username
    
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Request Accept successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataTable1(); // Refresh the table after accepting
                    loadDataTable2();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Accept the user. The user might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error accepting friend requst user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField6ActionPerformed
        String filterValue = jTextField7.getText().trim(); // Get the filter value from the text field
        if (filterValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to search.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            currentFilterQuery2 = ""; // Clear the filter query if input is empty
            loadDataTable2(); // Reload without any filter
            return;
        }

        currentFilterQuery2 = "WHERE username LIKE '%" + filterValue + "%'";

        // Reload the data with the updated filter and current sort
        loadDataTable2();
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
