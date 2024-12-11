package com.example.Chat.app;

import com.example.Chat.app.Admin.AdminUI;
import com.example.Chat.app.Users.userchatapp.UserLogin;
import com.example.Chat.app.Users.userchatapp.UserUI;
import com.example.Chat.app.Users.userchatapp.UserSignup;
import com.mysql.cj.xdevapi.Client;

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
        JButton userButtonLogin= new JButton("User Login");
        JButton userButtonSignup = new JButton ("User Signup");
        JButton userUIButton = new JButton ("User UI");
        // Set up action listeners
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminUI().setVisible(true);
                dispose(); 
            }
        });

        userButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserLogin().setVisible(true);
                dispose(); 
            }
        });

        userUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserUI("1").setVisible(true);
                dispose(); // Close the MainUI window
            }
        });
        userButtonSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserSignup().setVisible(true);
                dispose(); 
            }
        });
        

        // Set up the layout
        JPanel panel = new JPanel();
        panel.add(adminButton);
        panel.add(userButtonLogin);
        panel.add(userButtonSignup);
        panel.add(userUIButton);
        add(panel);
    }
}
