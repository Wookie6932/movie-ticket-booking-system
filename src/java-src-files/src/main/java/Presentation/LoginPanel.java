package Presentation;
 
import Account.User;
import Authentication.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private final AuthenticationService authenticationService;
 
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton;
 
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authenticationService = new AuthenticationService();
        buildUI();
    }
 
    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
 
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 12, 8);
        add(titleLabel, gbc);
 
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(6, 8, 2, 8);
        JLabel usernameLabel = new JLabel("User Name:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(usernameLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 6, 8);
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(260, 28));
        add(usernameField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 8, 2, 8);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(passwordLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 6, 8);
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(260, 28));
        add(passwordField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 4, 8);
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(errorLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 8, 10, 8);
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(260, 32));
        loginButton.setFont(new Font("Arial", Font.PLAIN, 13));
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        add(loginButton, gbc);
 
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 8, 8);
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setBackground(Color.WHITE);
 
        JLabel promptLabel = new JLabel("Don't have an account?");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 12));
 
        JLabel createLink = new JLabel("Create Account");
        createLink.setFont(new Font("Arial", Font.BOLD, 12));
        createLink.setForeground(Color.BLUE);
        createLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mainFrame.showCreateAccount();
            }
        });
 
        linkPanel.add(promptLabel);
        linkPanel.add(createLink);
        add(linkPanel, gbc);
    }
 
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
 
        loginButton.setEnabled(false);
        errorLabel.setText("");
 
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws SQLException {
                return authenticationService.login(username, password);
            }
 
            @Override
            protected void done() {
                try {
                    User user = get();
 
                    if (user != null) {
                        usernameField.setText("");
                        passwordField.setText("");
                        mainFrame.showDashboard(user);
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        passwordField.setText("");
                    }
 
                } catch (Exception e) {
                    errorLabel.setText("A database error occurred. Please try again.");
                } finally {
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }
}