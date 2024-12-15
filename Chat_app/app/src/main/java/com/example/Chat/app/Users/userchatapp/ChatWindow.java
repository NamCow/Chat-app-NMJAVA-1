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
import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ChatWindow extends JFrame {
    private String userID;
    private String groupID;
    DatabaseConnection db = DatabaseConnection.getInstance();
    // Các thành phần giao diện
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JButton spamButton;
    private JButton deleteHistoryButton;

    public ChatWindow(String userID, String groupID, Socket socket) {
        this.userID = userID;
        this.groupID = groupID;

        this.socket = socket;
        initComponents();
        connectToServer();
        loadMessages();
    }

    private void initComponents() {
        setTitle("Chat with User " + groupID);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        /*
         * sendButton.addActionListener((ActionEvent e) -> {
         * String messageContent = messageField.getText();
         * if (!messageContent.isEmpty()) {
         * sendMessage(messageContent);
         * messageField.setText("");
         * }
         * });
         */

        spamButton = new JButton("Spam");
        spamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int userIdInt = Integer.parseInt(userID);
                int groupIdInt = Integer.parseInt(groupID);

                // Call the reportSpamUsers method when Spam button is clicked
                db.reportSpamUsers(userIdInt, groupIdInt);
                JOptionPane.showMessageDialog(null, "Spam reports have been sent.");
            }
        });

        deleteHistoryButton = new JButton("Delete Chat History");
        deleteHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int userIdInt = Integer.parseInt(userID);
                int groupIdInt = Integer.parseInt(groupID);

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete your chat history?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = db.deleteChatHistory(userIdInt, groupIdInt);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Chat history deleted successfully.");
                        chatArea.setText(""); // Xóa giao diện chat
                        loadMessages(); // Tải lại tin nhắn
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete chat history.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        sendButton.addActionListener((ActionEvent e) -> {
            String messageContent = messageField.getText();
            int userIdInt = Integer.parseInt(userID);
            int groupIdInt = Integer.parseInt(groupID);
            List<String> friendshipStatuses = db.getFriendshipStatuses(userIdInt, groupIdInt);

            // Kiểm tra nếu có bất kỳ trạng thái nào là "Blocked"
            if (friendshipStatuses.contains("blocked")) {
                // Nếu có trạng thái "Blocked", không cho phép gửi tin nhắn và hiển thị thông
                // báo
                JOptionPane.showMessageDialog(null, "You are blocked and cannot send messages.");
            } else if (!messageContent.isEmpty()) {
                // Nếu không bị Blocked, tiếp tục gửi tin nhắn
                sendMessage(messageContent);
                messageField.setText("");
            }
        });

        JTextField searchField = new JTextField(20); // Ô tìm kiếm
        JButton searchButton = new JButton("Search"); // Nút tìm kiếm
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (!searchText.isEmpty()) {
                searchInChatArea(searchText); // Gọi phương thức tìm kiếm
            }
        });

        JPanel searchPanel = new JPanel(); // Tạo panel chứa ô tìm kiếm và nút
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        bottomPanel.add(spamButton);
        bottomPanel.add(deleteHistoryButton);
        // Thêm các panel vào cửa sổ
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);
        // Cài đặt kích thước cửa sổ
        pack();
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình
        setVisible(true);
    }

    private void connectToServer() {
        try {
            // Kết nối đến server
            socket = new Socket("localhost", 12345); // Thay localhost bằng IP của server nếu cần
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi userID tới server để đăng ký
            out.println(userID);

            // Lắng nghe tin nhắn từ server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {

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
        out.println(message); // Gửi tin nhắn tới server

        chatArea.append("You: " + messageContent + "\n");

        int userIdInt = Integer.parseInt(userID);
        int groupIdInt = Integer.parseInt(groupID);
        LocalDateTime sentAt = LocalDateTime.now().plusHours(7);
        boolean success = db.saveMessage(userIdInt, groupIdInt, messageContent, sentAt);

        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to save message to database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMessages() {
        int userIdInt = Integer.parseInt(userID);
        int groupIdInt = Integer.parseInt(groupID);
        // Lấy danh sách tin nhắn
        List<Message> messages = db.getMessagesUser(userIdInt, groupIdInt);

        // Duyệt qua các tin nhắn và thêm vào chatArea
        for (Message message : messages) {
            if (message.isSenderIsUser()) {
                // Tin nhắn của user, hiển thị ở bên phải
                chatArea.append("You: " + message.getMessageContent() + "\n");
            } else {
                // Tin nhắn từ người khác, hiển thị ở bên trái

                String senderName = db.getNamebyid(message.getSenderId());
                chatArea.append(senderName + ": " + message.getMessageContent() + "\n");
            }
        }
    }

    private void searchInChatArea(String searchText) {
        String content = chatArea.getText();
        int index = content.indexOf(searchText);

        if (index != -1) {
            try {

                chatArea.setCaretPosition(index);
                chatArea.requestFocusInWindow();

                chatArea.select(index, index + searchText.length());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Text not found.");
        }
    }

}
