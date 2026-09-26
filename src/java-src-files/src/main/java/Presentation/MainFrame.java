package Presentation;

import Account.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
        private final CardLayout cardLayout;
    private final JPanel cardPanel;

        public MainFrame() {
        setTitle("Movie Ticket Booking System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
 
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
 
        cardPanel.add(new LoginPanel(this), "LOGIN");
        cardPanel.add(new CreateAccountPanel(this), "CREATE_ACCOUNT");
 
        add(cardPanel);
        setVisible(true);
 
        cardLayout.show(cardPanel, "LOGIN");
    }
    
        public void showDashboard(User user) {
 
        if (user.getRole().equals("ADMIN")) {
            cardPanel.add(new AdminDashboard(this, user), "ADMIN");
            cardLayout.show(cardPanel, "ADMIN");
        } else {
            cardPanel.add(new CustomerDashboard(this, user), "CUSTOMER");
            cardLayout.show(cardPanel, "CUSTOMER");
        }
 
        setSize(1280, 720);
        setResizable(true);
        setLocationRelativeTo(null);
    }
     
        public void showLogin() {
 
        for (Component component : cardPanel.getComponents()) {
            String name = component.getName();
            if ("ADMIN".equals(name) || "CUSTOMER".equals(name)) {
                cardPanel.remove(component);
            }
        }
 
        setSize(1280, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, "LOGIN");
    }
 
    public void showCreateAccount() {
        cardLayout.show(cardPanel, "CREATE_ACCOUNT");
    }
}