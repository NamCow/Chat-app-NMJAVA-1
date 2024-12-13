package com.example.Chat.app;
import com.example.Chat.app.Users.chatservice.Server;
public class App {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null); 
            }
        }).start();

        // Create and display the MainUI form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }
}
