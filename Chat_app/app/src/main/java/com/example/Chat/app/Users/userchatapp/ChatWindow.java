package com.example.Chat.app.Users.userchatapp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class ChatWindow extends JFrame {
    private String senderId;
    private String receiverId;
    
    // Các thành phần giao diện
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatWindow(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        
        initComponents();
    }

    private void initComponents() {
        setTitle("Chat with User " + receiverId);
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
                sendMessage();
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
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // Hiển thị tin nhắn trong chatArea (Có thể thay thế bằng việc gửi tin nhắn qua Socket hoặc Database)
            chatArea.append("You: " + message + "\n");
            messageField.setText(""); // Xóa nội dung trong ô nhập tin nhắn

            // Gửi tin nhắn qua database hoặc socket ở đây
            // Gọi hàm gửi tin nhắn vào cơ sở dữ liệu hoặc qua socket
            // sendToServer(senderId, receiverId, message);
        }
    }
}
