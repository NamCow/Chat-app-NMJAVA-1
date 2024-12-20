package com.example.Chat.app;

import com.example.Chat.app.Admin.MainAdmin;
import com.example.Chat.app.Users.MainUser;
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
        JButton userUIButton = new JButton ("User UI");
        // Set up action listeners
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainAdmin().setVisible(true);
                dispose(); 
            }
        });


        userUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainUser().setVisible(true);
                dispose(); 
            }
        });
        

        // Set up the layout
        JPanel panel = new JPanel();
        panel.add(adminButton);
        panel.add(userUIButton);
        add(panel);
    }
}
