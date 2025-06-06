/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.example.Chat.app.Users.component;

import java.sql.*;
import java.util.Properties;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.example.Chat.app.Users.database.DatabaseConnection;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


public class UserInfor extends javax.swing.JPanel {
    private int userId;
    DatabaseConnection db = DatabaseConnection.getInstance();
    Connection conn = DatabaseConnection.getConnection();
    
    public UserInfor() {
        initComponents();
    }

    public void setId(String UserId) {
        this.userId = Integer.parseInt(UserId);
        loadUserInformation();
    }


    private void loadUserInformation() {
        String query = "SELECT username, email, address, fullname, birthday, gender, role, status FROM users WHERE user_id = ?";
        try (
                PreparedStatement pstmt = conn != null ? conn.prepareStatement(query) : null) {

            if (pstmt == null) {
                JOptionPane.showMessageDialog(this, "Database connection is not available.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setInt(1, userId); // Set the user ID in the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Populate UI components with data
                    jTextField1.setText(rs.getString("username")); // Username
                    jTextField2.setText(rs.getString("fullname")); // Email
                    jTextField3.setText(rs.getString("email")); // Address
                    jTextField4.setText(rs.getString("birthday"));// Fullname
                    jTextField5.setText(rs.getString("address"));// Birthday
                    jCheckBox2.setSelected("male".equalsIgnoreCase(rs.getString("gender"))); // Gender Male
                    jCheckBox3.setSelected("female".equalsIgnoreCase(rs.getString("gender"))); // Gender Female
                } else {
                    JOptionPane.showMessageDialog(this, "No user found with ID: " + userId, "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel1.setText("Username");

        jLabel2.setText("Fullname");

        jLabel3.setText("Email");

        jLabel4.setText("Birthday");

        jLabel5.setText("Address");

        jLabel6.setText("Gender");

        jCheckBox2.setText("male");

        jCheckBox3.setText("female");

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        jTextField3.setText("jTextField3");

        jTextField4.setText("jTextField4");

        jTextField5.setText("jTextField5");

        jButton1.setText("Update Information");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Update Password");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Reset Password");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 135,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50,
                                                        Short.MAX_VALUE)
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 135,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel6)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5))
                                                .addGap(23, 23, 23)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jCheckBox2)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jCheckBox3))
                                                        .addGroup(jPanel1Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                .addComponent(jTextField1,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE, 170,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jTextField2)
                                                                .addComponent(jTextField3)
                                                                .addComponent(jTextField4))
                                                        .addComponent(jTextField5,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 170,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addGap(97, 97, 97)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jCheckBox2)
                                                .addComponent(jCheckBox3)))
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2)
                                        .addComponent(jButton3))
                                .addContainerGap(47, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 361, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 9, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 8, Short.MAX_VALUE))));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 14, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 15, Short.MAX_VALUE))));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "No user is loaded for updating.", "No User Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection is not available.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm update with the user
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to update this user's information?", "Confirm Update",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Prepare the UPDATE query
            String updateQuery = "UPDATE users SET username = ?, email = ?, address = ?, fullname = ?, birthday = ?, gender = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                // Gather data from UI components
                pstmt.setString(1, jTextField1.getText().trim()); // Username
                pstmt.setString(2, jTextField3.getText().trim()); // Email
                pstmt.setString(3, jTextField5.getText().trim().isEmpty() ? null : jTextField5.getText().trim()); // Address
                pstmt.setString(4, jTextField2.getText().trim().isEmpty() ? null : jTextField2.getText().trim()); // Full
                                                                                                                  // Name
                try {
                    pstmt.setDate(5,
                            jTextField4.getText().trim().isEmpty() ? null : Date.valueOf(jTextField4.getText().trim())); // Birthday
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Invalid birthday format. Please use YYYY-MM-DD.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                pstmt.setString(6, jCheckBox2.isSelected() ? "male" : (jCheckBox3.isSelected() ? "female" : null)); // Gender
                pstmt.setInt(7, userId); // User ID

                // Execute the update
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User information updated successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadUserInformation(); // Reload the updated user data
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user information.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user information: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "No user is selected for password update.", "No User Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection is not available.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch the current password of the selected user
            String query = "SELECT password FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String currentPassword = rs.getString("password");

                        // Create text fields for old and new passwords
                        JPasswordField oldPasswordField = new JPasswordField(20);
                        JPasswordField newPasswordField = new JPasswordField(20);

                        // Create the dialog to enter old and new passwords
                        Object[] message = {
                                "Enter Old Password:", oldPasswordField,
                                "Enter New Password:", newPasswordField
                        };

                        int option = JOptionPane.showConfirmDialog(this, message, "Update Password",
                                JOptionPane.OK_CANCEL_OPTION);

                        if (option == JOptionPane.OK_OPTION) {
                            // Get the entered passwords
                            String enteredOldPassword = new String(oldPasswordField.getPassword());
                            String newPassword = new String(newPasswordField.getPassword());

                            // Check if the old password matches
                            if (!enteredOldPassword.equals(currentPassword)) {
                                JOptionPane.showMessageDialog(this, "Old password does not match.",
                                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Check if the new password is empty
                            if (newPassword.trim().isEmpty()) {
                                JOptionPane.showMessageDialog(this, "New password cannot be empty.", "Input Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Proceed to update the password in the database
                            String updatePasswordQuery = "UPDATE users SET password = ? WHERE user_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery)) {
                                updateStmt.setString(1, newPassword);
                                updateStmt.setInt(2, userId);

                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed to update password.", "Error",
                                            JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error updating password: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset the password.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (conn == null)
                return;

            // Fetch the email of the selected user
            String query = "SELECT email FROM users WHERE user_id = ?";
            String userEmail = null;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
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
                updateStmt.setInt(2, userId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully. Sending email...", "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Send the email with the new password
                    if (sendEmail(userEmail, "Password Reset", "Your new password is: " + newPassword)) {
                        JOptionPane.showMessageDialog(this, "Email sent successfully to " + userEmail, "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to send email.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reset password.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
