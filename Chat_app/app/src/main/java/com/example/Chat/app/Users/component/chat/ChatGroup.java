/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.example.Chat.app.Users.component.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import com.example.Chat.app.Users.database.DatabaseConnection;
import com.example.Chat.app.Users.datastructure.Message;


public class ChatGroup extends javax.swing.JPanel {
    private String userID;
    private String groupID;
    private PrintWriter out;
    private BufferedReader in;
    private DatabaseConnection db = DatabaseConnection.getInstance();
    Socket socket;

    /**
     * Creates new form ChatGroup
     */
    public ChatGroup(String userID, String groupID, Socket socket) {
        this.userID = userID;
        this.groupID = groupID;
        this.socket = socket;
        initComponents();
        connectToServer();
        loadMessages();
        addMouseListenerToButton();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        jButton1.setText("Send");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void addMouseListenerToButton() {
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
    }

    private void loadMessages() {
        int senderIDInt = Integer.parseInt(userID); 
        int groupIdInt = Integer.parseInt(groupID); 

        // Gọi phương thức getGroupMessages để lấy danh sách tin nhắn
        List<Message> messages = db.getGroupMessages(senderIDInt, groupIdInt);

        // Hiển thị tin nhắn trong chatArea
        for (Message message : messages) {
            if (message.getSenderId() == senderIDInt) { // Nếu người gửi là chính người dùng
                jTextArea1.append("You: " + message.getMessageContent() + "\n");
            } else {
                 // Nếu người gửi là thành viên khác trong nhóm
                 String senderName = db.getNamebyid(message.getSenderId());
                jTextArea1.append(senderName + ": " + message.getMessageContent() + "\n");
            }
        }
    }

    private void connectToServer() {
        try {
            // Kết nối đến server
            socket = new Socket("localhost", 12345);  
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi userID tới server để đăng ký
            out.println(userID);

            // Lắng nghe tin nhắn từ server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        // Hiển thị tin nhắn trong chatArea
                        jTextArea1.append(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to connect to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   private void sendMessage(String messageContent) {
        // Định dạng tin nhắn: "senderId|groupId|messageContent"
        String message = userID + "|" + groupID + "|" + messageContent;
        out.println(message);  // Gửi tin nhắn tới server
        
        jTextArea1.append("You: " + messageContent + "\n");
        
        int userIdInt = Integer.parseInt(userID);
        int groupIdInt = Integer.parseInt(groupID);
        LocalDateTime sentAt = LocalDateTime.now().plusHours(7); 
        boolean success = db.saveMessage(userIdInt, groupIdInt, messageContent, sentAt);

        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to save message to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String messageContent = jTextField1.getText();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);  
                jTextField1.setText("");    
            }
        
    }

    public void highlightMessage(String searchText) {
        System.out.println("highlightMessage called with searchText: " + searchText);
        String chatText = jTextArea1.getText();
        int startIndex = chatText.indexOf(searchText);
    
        if (startIndex >= 0) {
            int endIndex = startIndex + searchText.length();
            Highlighter highlighter = jTextArea1.getHighlighter();
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    
            try {
                highlighter.addHighlight(startIndex, endIndex, painter);
                jTextArea1.setCaretPosition(startIndex);
                System.out.println("Highlight added from " + startIndex + " to " + endIndex);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Text not found: " + searchText);
        }
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
