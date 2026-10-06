package Presentation;

import Account.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
        private final CardLayout cardLayout;
        private final JPanel cardPanel;
        private JPanel currentDashboard;
        
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
        
        if (currentDashboard != null) {
            cardPanel.remove(currentDashboard);
            currentDashboard = null;
        }
            
                if (user.getRole().equals("ADMIN")) {
            currentDashboard = new AdminDashboard(this, user);
            cardPanel.add(currentDashboard, "DASHBOARD");
        } else {
            currentDashboard = new CustomerDashboard(this, user);
            cardPanel.add(currentDashboard, "DASHBOARD");
        }
 
        cardLayout.show(cardPanel, "DASHBOARD");
        setSize(1280, 720);
        setResizable(true);
        setLocationRelativeTo(null);
    }
     
    public void showLogin() {
 
        if (currentDashboard != null) {
            cardPanel.remove(currentDashboard);
            currentDashboard = null;
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