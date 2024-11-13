package com.example.Chat.app;

import com.example.Chat.app.Admin.AdminUI;
import com.example.Chat.app.Users.UserLogin;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame {

    public MainUI() {
        // Set up the frame
        setTitle("Choose Interface");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create buttons for Admin and User interfaces
        JButton adminButton = new JButton("Admin UI");
        JButton userButton = new JButton("User Login");

        // Set up action listeners
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminUI().setVisible(true);
                dispose(); // Close the MainUI window
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserLogin().setVisible(true);
                dispose(); // Close the MainUI window
            }
        });

        // Set up the layout
        JPanel panel = new JPanel();
        panel.add(adminButton);
        panel.add(userButton);
        add(panel);
    }
}
