package com.example.Chat.app.Users.userchatapp;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.example.Chat.app.Users.datastructure.Message;
import com.example.Chat.app.Users.database.DatabaseConnection;

public class ChatGroup extends JFrame {
    private String senderId;
    private String groupId;
    private DatabaseConnection db = DatabaseConnection.getInstance();

    // Các thành phần giao diện
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatGroup(String senderId, String groupId) {
        this.senderId = senderId;
        this.groupId = groupId;

        initComponents();
    }

    private void initComponents() {
        setTitle("Group Chat - Group " + groupId);
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
               // sendMessage();
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

    /*private void loadMessages() {
        List<Message> messages = db.getMessagesInGroup(groupId);
        for (Message message : messages) {
            if (message.getSenderId() == senderId) {
                chatArea.append("You: " + message.getMessageContent() + "\n");
            } else {
                chatArea.append("Them: " + message.getMessageContent() + "\n");
            }
        }
    }*/

   /* private void sendMessage() {
        String content = messageField.getText();
        if (!content.isEmpty()) {
            int senderIdInt = Integer.parseInt(senderId);
            int groupIdInt = Integer.parseInt(this.groupId);
            Message message = new Message(senderIdInt, groupIdInt, content, false); // groupId != 0 cho nhóm
            db.sendMessage(message);
            chatArea.append("You: " + content + "\n");
            messageField.setText("");
        }
    }*/
}
