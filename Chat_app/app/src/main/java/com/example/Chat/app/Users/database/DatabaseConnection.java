package com.example.Chat.app.Users.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import com.example.Chat.app.Users.datastructure.Message;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connect;
    private static DatabaseConnection instance;

    // Path to the configuration file
    private static final String CONFIG_FILE = "src/main/java/com/example/Chat/app/dbconfig.properties";

    /**
     * Reads the database connection properties from a file and establishes the connection.
     * 
     * @return Connection object or null if the connection fails
     */
    public static Connection getConnection() {
        try {
            if (connect == null || connect.isClosed()) {
                // Load properties from the configuration file
                Properties properties = new Properties();
                try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
                    properties.load(input);
                }

                // Get database properties
                String JDBC_DRIVER = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                String DB_URL = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/chat_app");
                String USER = properties.getProperty("db.user", "admin");
                String PASSWORD = properties.getProperty("db.password", "Phuongnam2312");

                // Load the JDBC driver
                Class.forName(JDBC_DRIVER);

                // Establish the database connection
                connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading configuration file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "JDBC Driver not found: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return connect;
    }

    /**
     * Returns the singleton instance of the DatabaseConnection class.
     * 
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Optional getters and setters
    public Connection getConnect() {
        return getConnection();
    }

    public void setConnect(Connection connect) {
        DatabaseConnection.connect = connect;
    }

    public String checkPassword(String usernameOrEmail, String password) {
        String sql = "SELECT user_id,`lock` FROM users WHERE (username = ? OR email = ?) AND password = ?";

        ResultSet rs = null;
        try {
            if (connect == null) {
                connect = getConnection();
            }
            PreparedStatement stmt = connect.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);
            rs = stmt.executeQuery();
            if (rs.first()) {
                String userID = rs.getString("user_id");
                int lock = rs.getInt("lock");
                if (lock == 1) {
                    return "Account is locked";
                }
                return userID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Integer> getUsersThatReceivedMessages(int senderId) {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                "CASE " +
                "WHEN cg.is_chat_with_user = 1 THEN mv.user_id " +
                "WHEN cg.is_chat_with_user = 0 THEN NULL " +
                "END AS user_id " +
                "FROM message m " +
                "INNER JOIN chat_group cg ON m.group_id = cg.group_id " +
                "LEFT JOIN group_members gm ON m.group_id = gm.group_id " +
                "WHERE m.sender_id = ? " +
                "AND (cg.is_chat_with_user = 1 OR cg.is_chat_with_user = 0)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer userId = rs.getInt("user_id");
                if (userId != 0) {
                    userIds.add(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    /*public List<String> getUsernamesThatReceivedMessages(int senderId) {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT DISTINCT u.username " +
                "FROM message m " +
                "INNER JOIN chat_group cg ON m.group_id = cg.group_id " +
                "LEFT JOIN group_members gm ON m.group_id = gm.group_id " +
                "LEFT JOIN users u ON u.user_id = gm.user_id " +
                "WHERE m.sender_id = ? " +
                "AND (cg.is_chat_with_user = 1 OR cg.is_chat_with_user = 0) " +
                "AND u.user_id != m.sender_id";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                if (username != null) {
                    usernames.add(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }*/
    public List<String> getGroupNamesByUserId(int userId) {
        List<String> groupNames = new ArrayList<>();
        String query = "SELECT DISTINCT cg.group_name " +
                       "FROM group_members gm " +
                       "INNER JOIN chat_group cg ON gm.group_id = cg.group_id " +
                       "LEFT JOIN users_friend uf " +
                       "ON (cg.is_chat_with_user = 1 AND " +
                       "    ((uf.user_id = gm.user_id AND uf.friend_id = ?) " +
                       "     OR (uf.friend_id = gm.user_id AND uf.user_id = ?))) " +
                       "WHERE gm.user_id = ? " +
                       "AND (cg.is_chat_with_user = 0 " +
                       "     OR (cg.is_chat_with_user = 1 AND " +
                       "         (uf.friendship IS NULL OR uf.friendship != 'blocked')))";
                       //System.out.println(query);
                       try (PreparedStatement stmt = connect.prepareStatement(query)) {
            

            stmt.setInt(1, userId); 
            stmt.setInt(2, userId); 
            stmt.setInt(3, userId); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String groupName = rs.getString("group_name");
                if (groupName != null) {
                    groupNames.add(groupName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupNames;
    }
    
    
    

    public String getGroupIdByGroupName(String groupName) {
        String groupId = "-1";
        String query = "SELECT group_id FROM chat_group WHERE group_name = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                groupId = rs.getString("group_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupId;
    }

    
    public void sendMessage(Message message) {
        String query = "INSERT INTO message (sender_id, group_id, message_content, sent_at) " +
                       "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
    
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getGroupId());
            stmt.setString(3, message.getMessageContent());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int isChatWithUser(String groupId) {
        int result = -1;
        String query = "SELECT is_chat_with_user FROM chat_group WHERE group_id = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, groupId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt("is_chat_with_user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<Message> getMessagesUser(int userId, int groupId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.message_id, m.sender_id, m.group_id, m.message, m.sent_at " +
                       "FROM message m " +
                       "JOIN group_members gm ON m.group_id = gm.group_id " +
                       "JOIN message_visibility mv ON m.message_id = mv.message_id " +
                       "WHERE m.group_id = ? AND gm.user_id = ? AND mv.user_id = ? AND mv.visible_status = 'existed' " +
                       "ORDER BY m.sent_at ASC";
    
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, groupId);  
            stmt.setInt(2, userId);   
            stmt.setInt(3, userId);   
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderId = rs.getInt("sender_id");
                int groupIdFromDb = rs.getInt("group_id");
                String messageContent = rs.getString("message");
                LocalDateTime sentAt = rs.getTimestamp("sent_at").toLocalDateTime(); // Đọc thời gian gửi tin nhắn
    
                Message message = new Message(senderId, groupIdFromDb, messageContent);
                message.setMessageId(messageId); // Gán message_id vào Message object nếu cần
                message.setSentAt(sentAt); // Cập nhật thời gian gửi
    
                if (senderId == userId) {
                    message.setSenderIsUser(true); // Thêm thông tin người gửi là userId
                } else {
                    message.setSenderIsUser(false); // Người gửi là người khác trong nhóm
                }
    
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
/* 
    public List<Message> getMessagesUser(int userId, int groupId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.sender_id, m.group_id, m.message, m.sent_at " +
                       "FROM message m " +
                       "JOIN group_members gm ON m.group_id = gm.group_id " +
                       "WHERE m.group_id = ? AND gm.user_id = ? " +
                       "ORDER BY m.sent_at ASC";
    
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, groupId);  // Lọc theo groupId
            stmt.setInt(2, userId);   // Lọc theo userId
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                int groupIdFromDb = rs.getInt("group_id");
                String messageContent = rs.getString("message");
                LocalDateTime sentAt = rs.getTimestamp("sent_at").toLocalDateTime(); // Đọc thời gian gửi tin nhắn
    
                // Tạo đối tượng Message và thêm vào danh sách
                Message message = new Message(senderId, groupIdFromDb, messageContent);
                message.setSentAt(sentAt); // Cập nhật thời gian gửi
    
                // Kiểm tra nếu senderId trùng với userId
                if (senderId == userId) {
                    message.setSenderIsUser(true); // Thêm thông tin người gửi là userId
                } else {
                    message.setSenderIsUser(false); // Người gửi là người khác trong nhóm
                }
    
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }    
        */
    /* 
    public boolean saveMessage(int userId, int groupId, String messageContent, LocalDateTime sentAt) {
        String query = "INSERT INTO message (sender_id, group_id, message, sent_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, userId); // sender_id
            stmt.setInt(2, groupId); // group_id
            stmt.setString(3, messageContent); // message
            stmt.setTimestamp(4, Timestamp.valueOf(sentAt)); // sent_at
            stmt.executeUpdate();
            return true; // Lưu thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Lỗi khi lưu
        }
    }*/
    public boolean saveMessage(int userId, int groupId, String messageContent, LocalDateTime sentAt) {
        String insertMessageQuery = "INSERT INTO message (sender_id, group_id, message, sent_at) VALUES (?, ?, ?, ?)";
        String selectGroupMembersQuery = "SELECT user_id FROM group_members WHERE group_id = ?";
        String insertVisibilityQuery = "INSERT INTO message_visibility (message_id, user_id, visible_status) VALUES (?, ?, ?)";
    
        try (
            PreparedStatement messageStmt = connect.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement groupMembersStmt = connect.prepareStatement(selectGroupMembersQuery)
        ) {
            // Step 1: Insert message into the `message` table
            messageStmt.setInt(1, userId);
            messageStmt.setInt(2, groupId);
            messageStmt.setString(3, messageContent);
            messageStmt.setTimestamp(4, Timestamp.valueOf(sentAt));
            messageStmt.executeUpdate();
    
            // Get the generated `message_id`
            ResultSet generatedKeys = messageStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve message_id.");
            }
            int messageId = generatedKeys.getInt(1);
    
            // Step 2: Get group members from `group_members` table
            groupMembersStmt.setInt(1, groupId);
            ResultSet groupMembers = groupMembersStmt.executeQuery();
    
            // Step 3: Insert into `message_visibility`
            try (PreparedStatement visibilityStmt = connect.prepareStatement(insertVisibilityQuery)) {
                while (groupMembers.next()) {
                    int memberId = groupMembers.getInt("user_id");
                    visibilityStmt.setInt(1, messageId);
                    visibilityStmt.setInt(2, memberId);
                    visibilityStmt.setString(3, "existed");
                    visibilityStmt.executeUpdate();
                }
            }
    
            return true;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    public String getNamebyid (int id) {
        String query = "SELECT username FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
  }

  /* 
  public List<Message> getGroupMessages(int userId, int groupId) {
    List<Message> messages = new ArrayList<>();
    String query = "SELECT m.message_id, m.sender_id, u.fullname AS sender_name, " +
                   "m.group_id, g.group_name, m.message, m.sent_at " +
                   "FROM message m " +
                   "JOIN users u ON m.sender_id = u.user_id " +
                   "JOIN chat_group g ON m.group_id = g.group_id " +
                   "JOIN group_members gm ON g.group_id = gm.group_id " +
                   "WHERE g.group_id = ? AND gm.user_id = ? " +
                   "ORDER BY m.sent_at ASC";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, groupId);
        stmt.setInt(2, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int messageId = rs.getInt("message_id");
            int senderId = rs.getInt("sender_id");
            String senderName = rs.getString("sender_name");
            int groupIdFromDb = rs.getInt("group_id");
            //String groupName = rs.getString("group_name");
            String messageContent = rs.getString("message");
            LocalDateTime sentAt = rs.getTimestamp("sent_at").toLocalDateTime();

            Message message = new Message(senderId, groupIdFromDb, messageContent);
            message.setMessageId(messageId);
            message.setSenderName(senderName);
            //message.setGroupName(groupName);
            message.setSentAt(sentAt);

            messages.add(message);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return messages;
}
    */
    public List<Message> getGroupMessages(int userId, int groupId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.message_id, m.sender_id, u.fullname AS sender_name, " +
                       "m.group_id, g.group_name, m.message, m.sent_at " +
                       "FROM message m " +
                       "JOIN users u ON m.sender_id = u.user_id " +
                       "JOIN chat_group g ON m.group_id = g.group_id " +
                       "JOIN group_members gm ON g.group_id = gm.group_id " +
                       "JOIN message_visibility mv ON m.message_id = mv.message_id " +
                       "WHERE g.group_id = ? AND gm.user_id = ? AND mv.user_id = ? AND mv.visible_status = 'existed' " +
                       "ORDER BY m.sent_at ASC";
    
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, groupId);  // Lọc theo groupId
            stmt.setInt(2, userId);   // Kiểm tra quyền thành viên của userId trong nhóm
            stmt.setInt(3, userId);   // Kiểm tra quyền hiển thị của userId trong message_visibility
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderId = rs.getInt("sender_id");
                String senderName = rs.getString("sender_name");
                int groupIdFromDb = rs.getInt("group_id");
                // String groupName = rs.getString("group_name");
                String messageContent = rs.getString("message");
                LocalDateTime sentAt = rs.getTimestamp("sent_at").toLocalDateTime();
    
                Message message = new Message(senderId, groupIdFromDb, messageContent);
                message.setMessageId(messageId);
                message.setSenderName(senderName);
                // message.setGroupName(groupName);
                message.setSentAt(sentAt);
    
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
public List<String> getFriendshipStatuses(int userId, int groupId) {
    List<String> friendshipStatuses = new ArrayList<>();
    String query = "SELECT " +
                   "    gm.user_id AS member_id, " +
                   "    CASE " +
                   "        WHEN uf.friendship = 'blocked' THEN 'blocked' " +
                   "        WHEN uf.friendship IS NULL THEN 'no relationship' " +
                   "        ELSE uf.friendship " +
                   "    END AS friendship_status " +
                   "FROM group_members gm " +
                   "LEFT JOIN users_friend uf " +
                   "    ON (uf.user_id = ? AND uf.friend_id = gm.user_id) " +
                   "    OR (uf.friend_id = ? AND uf.user_id = gm.user_id) " +
                   "WHERE gm.group_id = ? AND gm.user_id != ?";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, userId);
        stmt.setInt(3, groupId);
        stmt.setInt(4, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String friendshipStatus = rs.getString("friendship_status");
            friendshipStatuses.add(friendshipStatus);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return friendshipStatuses;
}
public void reportSpamUsers(int userId, int groupId) {
    String selectMembersQuery = "SELECT user_id FROM group_members WHERE group_id = ? AND user_id != ?";
    String insertSpamQuery = "INSERT INTO spam_list (report_id, report_by, report_user, report_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
    String getMaxReportIdQuery = "SELECT MAX(report_id) FROM spam_list";

    try (PreparedStatement selectStmt = connect.prepareStatement(selectMembersQuery);
         PreparedStatement getMaxReportIdStmt = connect.prepareStatement(getMaxReportIdQuery)) {

        // Truy vấn các thành viên trong nhóm
        selectStmt.setInt(1, groupId);
        selectStmt.setInt(2, userId);
        
        ResultSet rs = selectStmt.executeQuery();

        ResultSet maxReportIdRs = getMaxReportIdStmt.executeQuery();
        int nextReportId = 1; // Mặc định là 1 nếu không có bản ghi nào
        if (maxReportIdRs.next()) {
            nextReportId = maxReportIdRs.getInt(1) + 1;  // Tăng giá trị report_id
        }

        try (PreparedStatement insertStmt = connect.prepareStatement(insertSpamQuery)) {
            // Chèn các thành viên vào bảng spam_list
            while (rs.next()) {
                int reportUserId = rs.getInt("user_id");

                // Chèn dữ liệu vào bảng spam_list với report_id mới
                insertStmt.setInt(1, nextReportId);    // report_id tự tăng
                insertStmt.setInt(2, userId);          // report_by là userId
                insertStmt.setInt(3, reportUserId);    // report_user là thành viên trong nhóm
                insertStmt.executeUpdate();
                
                nextReportId++; // Tăng giá trị report_id cho lần chèn tiếp theo
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public boolean deleteChatHistory(int userId, int groupId) {
    String query = "UPDATE message_visibility " +
                   "SET visible_status = 'hidden' " +
                   "WHERE user_id = ? AND message_id IN (" +
                   "    SELECT m.message_id " +
                   "    FROM message m " +
                   "    WHERE m.group_id = ?" +
                   ")";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, userId); // user_id
        stmt.setInt(2, groupId); // group_id

        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0; // Trả về true nếu có bản ghi được cập nhật
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Trả về false nếu có lỗi
    }
}
public List<Integer> getGroupIdsByMessage(int userId, String message) {
    List<Integer> groupIds = new ArrayList<>();
    String query = "SELECT DISTINCT g.group_id " +
                   "FROM chat_group g " +
                   "JOIN group_members gm ON g.group_id = gm.group_id " +
                   "JOIN message m ON g.group_id = m.group_id " +
                   "JOIN message_visibility mv ON m.message_id = mv.message_id " +
                   "WHERE gm.user_id = ? " +
                   "  AND mv.user_id = ? " +
                   "  AND mv.visible_status = 'existed' " +
                   "  AND m.message LIKE ?";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, userId);
        stmt.setString(3, "%" + message + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            groupIds.add(rs.getInt("group_id"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return groupIds;
}

public List<String> getGroupNamesByGroupId(int groupId) {
    List<String> groupNames = new ArrayList<>();
    String query = "SELECT group_name FROM chat_group WHERE group_id = ?";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, groupId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            groupNames.add(rs.getString("group_name"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return groupNames;
}

public List<String> getGroupNamesByMessageAndUser(int userId, String searchText) {
    List<String> groupNames = new ArrayList<>();
    String query = "SELECT DISTINCT g.group_name " +
                   "FROM message m " +
                   "JOIN chat_group g ON m.group_id = g.group_id " +
                   "JOIN message_visibility mv ON m.message_id = mv.message_id " +
                   "WHERE mv.user_id = ? AND mv.visible_status = 'existed' AND m.message LIKE ?";

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setString(2, "%" + searchText + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String groupName = rs.getString("group_name");
            groupNames.add(groupName);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return groupNames;
}
public void updateAllUsersStatusToInactive() {
    String query = "UPDATE users SET status = 'inactive'";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        int rowsUpdated = stmt.executeUpdate();
        System.out.println("Rows updated: " + rowsUpdated);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
