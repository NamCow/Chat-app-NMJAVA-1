package com.example.Chat.app.Users.datastructure;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private int messageId;
    private int senderId;
    private int groupId;
    private String message;
    private LocalDateTime sentAt;
    private boolean isChatWithUser;
    private boolean senderIsUser;  // Thêm thuộc tính senderIsUser
    private String senderName;
    // Constructor
    public Message(int senderId, int groupId, String message) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }

    // Getter và Setter
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMessageContent() {
        return message;
    }

    public void setMessageContent(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isChatWithUser() {
        return isChatWithUser;
    }

    public void setChatWithUser(boolean chatWithUser) {
        isChatWithUser = chatWithUser;
    }

    public boolean isSenderIsUser() {
        return senderIsUser;
    }

    public void setSenderIsUser(boolean senderIsUser) {
        this.senderIsUser = senderIsUser;
    }
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    // Phương thức chuyển đối tượng Message thành chuỗi để gửi qua socket
    @Override
    public String toString() {
        // Nếu tin nhắn là giữa người dùng với người dùng, chúng ta sẽ thêm thông tin groupId, senderId và senderIsUser
        return senderId + "|" + groupId + "|" + message + "|" + senderIsUser;  // Cập nhật để thêm senderIsUser
    }

    // Phương thức chuyển từ chuỗi (nhận qua socket) thành đối tượng Message
    public static Message fromString(String messageString) {
        // Định dạng chuỗi: senderId|groupId|messageContent|senderIsUser
        String[] parts = messageString.split("\\|");
        int senderId = Integer.parseInt(parts[0]);
        int groupId = Integer.parseInt(parts[1]);
        String messageContent = parts[2];
        boolean senderIsUser = Boolean.parseBoolean(parts[3]); // Đọc thông tin senderIsUser từ chuỗi

        Message message = new Message(senderId, groupId, messageContent);
        message.setSenderIsUser(senderIsUser);  // Cập nhật giá trị senderIsUser

        return message;
    }
}
