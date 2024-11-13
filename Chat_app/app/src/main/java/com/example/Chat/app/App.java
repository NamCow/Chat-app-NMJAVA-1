package com.example.Chat.app;

public class App {
    public static void main(String[] args) {
        // Create and display the MainUI form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }
}
