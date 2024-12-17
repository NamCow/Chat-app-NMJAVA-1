package com.example.Chat.app.Admin;

import com.example.Chat.app.Admin.Login.AdminLogin;
import com.example.Chat.app.Admin.Login.AdminSignup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAdmin extends JFrame {

    public MainAdmin() {
        
        // Set up the frame
        setTitle("Choose Interface");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton adminButtonLogin= new JButton("Admin Login");
        JButton adminButtonSignup = new JButton ("Admin Signup");
        // Set up action listeners

        adminButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLogin().setVisible(true);
                dispose(); 
            }
        });

        adminButtonSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new  AdminSignup().setVisible(true);
                dispose(); 
            }
        });
        

        // Set up the layout
        JPanel panel = new JPanel();
        panel.add(adminButtonLogin);
        panel.add(adminButtonSignup);
        add(panel);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainAdmin().setVisible(true);
            }
        });
    }
}
