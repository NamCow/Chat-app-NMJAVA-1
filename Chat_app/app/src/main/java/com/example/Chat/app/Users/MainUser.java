package com.example.Chat.app.Users;


import com.example.Chat.app.Users.userchatapp.UserSignup;
import com.example.Chat.app.Users.chatservice.Server;
import com.example.Chat.app.Users.userchatapp.UserLogin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUser extends JFrame {

    public MainUser() {
        
        // Set up the frame
        setTitle("Choose Interface");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton userButtonLogin= new JButton("User Login");
        JButton userButtonSignup = new JButton ("User Signup");
        // Set up action listeners

        userButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserLogin().setVisible(true);
                dispose(); 
            }
        });

        userButtonSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new  UserSignup().setVisible(true);
                dispose(); 
            }
        });
        

        // Set up the layout
        JPanel panel = new JPanel();
        panel.add(userButtonLogin);
        panel.add(userButtonSignup);
        add(panel);
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null); 
            }
        }).start();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUser().setVisible(true);
            }
        });
    }
}
