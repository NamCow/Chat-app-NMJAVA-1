package com.example.Chat.app.Users.userchatapp;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import com.example.Chat.app.Users.datastructure.Message;
import com.example.Chat.app.Users.database.DatabaseConnection;
import java.net.Socket;
import java.time.LocalDateTime;
public class ChatGroup extends JFrame {
    private String userID;
    private String groupID;
    private PrintWriter out;
    private BufferedReader in;
    private DatabaseConnection db = DatabaseConnection.getInstance();
    Socket socket;
    // Các thành phần giao diện
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatGroup(String userID, String groupID, Socket socket) {
        this.userID = userID;
        this.groupID = groupID;
        this.socket = socket;
        initComponents();
        connectToServer();
        loadMessages();
    }

    private void initComponents() {
        setTitle("Group Chat - Group " + groupID);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = messageField.getText();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);  
                    messageField.setText("");    
                }
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);

        // Thêm các panel vào cửa sổ
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Cài đặt kích thước cửa sổ
        pack();
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình
        setVisible(true);

        //loadMessages();
    }

    private void loadMessages() {
        int senderIDInt = Integer.parseInt(userID); // Chuyển senderId từ String sang int
        int groupIdInt = Integer.parseInt(groupID); // Chuyển groupId từ String sang int

        // Gọi phương thức getGroupMessages để lấy danh sách tin nhắn
        List<Message> messages = db.getGroupMessages(senderIDInt, groupIdInt);

        // Hiển thị tin nhắn trong chatArea
        for (Message message : messages) {
            if (message.getSenderId() == senderIDInt) { // Nếu người gửi là chính người dùng
                chatArea.append("You: " + message.getMessageContent() + "\n");
            } else { // Nếu người gửi là thành viên khác trong nhóm
                chatArea.append(message.getSenderName() + ": " + message.getMessageContent() + "\n");
            }
        }
    }
    private void connectToServer() {
        try {
            // Kết nối đến server
            socket = new Socket("localhost", 12345);  // Thay localhost bằng IP của server nếu cần
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
                        chatArea.append(message + "\n");
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
        
        chatArea.append("You: " + messageContent + "\n");
        
        int userIdInt = Integer.parseInt(userID);
        int groupIdInt = Integer.parseInt(groupID);
        LocalDateTime sentAt = LocalDateTime.now().plusHours(7); 
        boolean success = db.saveMessage(userIdInt, groupIdInt, messageContent, sentAt);

        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to save message to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
