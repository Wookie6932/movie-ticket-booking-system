package Presentation;
 
import Authentication.AuthenticationService;
import Authentication.Result;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
public class CreateAccountPanel extends JPanel {
    private final MainFrame mainFrame;
    private final AuthenticationService authenticationService;
 
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JButton createButton;
 
    public CreateAccountPanel(MainFrame mainFrame) {
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
 
        JLabel titleLabel = new JLabel("Create Account");
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
        JLabel emailLabel = new JLabel("Email address:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(emailLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 6, 8);
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(260, 28));
        add(emailField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 8, 2, 8);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(passwordLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 6, 8);
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(260, 28));
        add(passwordField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 8, 2, 8);
        JLabel confirmLabel = new JLabel("Confirm password:");
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(confirmLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 6, 8);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(260, 28));
        add(confirmPasswordField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 4, 8);
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(errorLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 8, 10, 8);
        createButton = new JButton("Create Account");
        createButton.setPreferredSize(new Dimension(260, 32));
        createButton.setFont(new Font("Arial", Font.PLAIN, 13));
        createButton.addActionListener(e -> attemptCreateAccount());
        add(createButton, gbc);
 
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 8, 8);
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setBackground(Color.WHITE);
 
        JLabel promptLabel = new JLabel("Already have an account?");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 12));
 
        JLabel loginLink = new JLabel("Log in");
        loginLink.setFont(new Font("Arial", Font.BOLD, 12));
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                clearFields();
                mainFrame.showLogin();
            }
        });
 
        linkPanel.add(promptLabel);
        linkPanel.add(loginLink);
        add(linkPanel, gbc);
    }
 
    private void attemptCreateAccount() {
 
        String username        = usernameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
 
        createButton.setEnabled(false);
        errorLabel.setText("");
 
        new SwingWorker<Result, Void>() {
 
            @Override
            protected Result doInBackground() {
                return authenticationService.createAccount(
                        username, email, password, confirmPassword);
            }
 
            @Override
            protected void done() {
                try {
                    Result result = get();
 
                    if (result.isSuccess()) {
                        clearFields();
                        
                        JOptionPane.showMessageDialog(
                                CreateAccountPanel.this,
                                result.getMessage(),
                                "Account Created",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        mainFrame.showLogin();
                    } else {
                        errorLabel.setText(result.getMessage());
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                    }
 
                } catch (Exception e) {
                    errorLabel.setText("A database error occurred. Please try again.");
                } finally {
                    createButton.setEnabled(true);
                }
            }
 
        }.execute();
    }
 
    private void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorLabel.setText("");
    }
}