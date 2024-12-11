package com.example.Chat.app.Users.datastructure;
import java.io.Serializable;
import java.time.LocalDateTime;
public class Message implements Serializable {
    private int messageId;
    private int senderId;
    private int groupId;
    private String messageContent;
    private LocalDateTime sentAt;
    private boolean isChatWithUser; // Để kiểm tra loại tin nhắn (nhóm hay người dùng)

    // Constructor
    public Message(int senderId, int groupId, String messageContent, boolean isChatWithUser) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.messageContent = messageContent;
        this.isChatWithUser = isChatWithUser;
        this.sentAt = LocalDateTime.now(); // Gán thời gian gửi tin nhắn
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
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
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

    // Phương thức chuyển đối tượng Message thành chuỗi để gửi qua socket
    @Override
    public String toString() {
        // Nếu tin nhắn là giữa người dùng với người dùng, chúng ta sẽ thêm thông tin groupId và senderId
        return senderId + "|" + groupId + "|" + messageContent + "|" + isChatWithUser;
    }

    // Phương thức chuyển từ chuỗi (nhận qua socket) thành đối tượng Message
    public static Message fromString(String messageString) {
        // Định dạng chuỗi: senderId|groupId|messageContent|isChatWithUser
        String[] parts = messageString.split("\\|");
        int senderId = Integer.parseInt(parts[0]);
        int groupId = Integer.parseInt(parts[1]);
        String messageContent = parts[2];
        boolean isChatWithUser = Boolean.parseBoolean(parts[3]);

        return new Message(senderId, groupId, messageContent, isChatWithUser);
    }
}