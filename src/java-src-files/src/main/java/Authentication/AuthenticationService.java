package Authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Account.User;
import Database.UserDAO;

public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public String createAccount(String username, String email,
                                String password, String confirmPassword) {

        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            return "All fields are required";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords don't match.";
        }

        String passwordHash = hashPassword(password);

        User user = new User(
                username,
                email,
                passwordHash,
                "CUSTOMER"
        );

        boolean success = userDAO.createUser(user);

        if (success) {
            return "Account created successfully";
        }
        return "Failed to create account. Potetnially using existing username or email";
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to hash password", e);
        }
    }
}