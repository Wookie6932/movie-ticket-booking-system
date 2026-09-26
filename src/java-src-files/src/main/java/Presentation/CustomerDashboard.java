package Presentation;
 
import Account.User;
 
import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JPanel {
    private final MainFrame mainFrame;
    private final User currentUser;
 
    public CustomerDashboard(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        buildUI();
    }
 
    private void buildUI() {
        setLayout(new BorderLayout());
 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.GRAY);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
 
        JLabel titleLabel = new JLabel("Movie Ticket Booking System - Customer Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        topBar.add(titleLabel, BorderLayout.WEST);
 
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
 
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        welcomeLabel.setForeground(Color.BLACK);
        rightPanel.add(welcomeLabel);
 
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 15));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> mainFrame.showLogin());
        rightPanel.add(logoutButton);
 
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
 
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
 
        JLabel placeholderLabel = new JLabel("Temporary Customer Dashboard");
        placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        placeholderLabel.setForeground(Color.BLACK);
        centerPanel.add(placeholderLabel);
 
        add(centerPanel, BorderLayout.CENTER);
    }
}