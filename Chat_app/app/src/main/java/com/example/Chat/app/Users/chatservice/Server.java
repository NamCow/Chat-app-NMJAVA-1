package com.example.Chat.app.Users.chatservice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server {
    private static final int PORT = 12345; 
    private static Map<String, PrintWriter> clients = new HashMap<>(); 
    public static void main(String[] args) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String userID;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Nhận userID từ client khi vừa kết nối
                userID = in.readLine();
                synchronized (clients) {
                    clients.put(userID, out); // Lưu client vào danh sách
                }

                System.out.println(userID + " has connected.");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from " + userID + ": " + message);
                    String[] parts = message.split(":", 2); // Tách receiverID và nội dung tin nhắn
                    if (parts.length == 2) {
                        String receiverID = parts[0];
                        String chatMessage = parts[1];
                        broadcastToUser(receiverID, userID, chatMessage); // Chuyển tiếp tin nhắn
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Xử lý khi client ngắt kết nối
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clients) {
                    clients.remove(userID); // Xóa client khỏi danh sách
                }
                System.out.println(userID + " has disconnected.");
            }
        }

        private void broadcastToUser(String receiverID, String senderID, String message) {
            synchronized (clients) {
                PrintWriter receiverOut = clients.get(receiverID); 
                if (receiverOut != null) {
                    receiverOut.println(senderID + ": " + message); 
                } else {
                    System.out.println("User " + receiverID + " is offline.");
                }
            }
        }
    }

}
