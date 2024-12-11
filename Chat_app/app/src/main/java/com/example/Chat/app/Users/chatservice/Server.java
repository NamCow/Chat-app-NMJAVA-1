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
    private static Map<Integer, PrintWriter> clients = new HashMap<>(); // Danh sách các client

    public static void main(String[] args) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private int userID;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // Đọc userID từ client
                userID = Integer.parseInt(in.readLine());
                synchronized (clients) {
                    clients.put(userID, out);
                }

                // Nhận và phát tin nhắn
                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(message, userID);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Loại bỏ client khi ngắt kết nối
                synchronized (clients) {
                    clients.remove(userID);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message, int senderId) {
            synchronized (clients) {
                for (Map.Entry<Integer, PrintWriter> client : clients.entrySet()) {
                    if (client.getKey() != senderId) { // Không gửi lại cho người gửi
                        client.getValue().println(message);
                    }
                }
            }
        }
    }

}
