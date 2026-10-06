package Authentication;

import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import Account.User;
import Database.DuplicateException;
import Database.UserDAO;

public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

        public Result createAccount(String username, String email,
                                String password, String confirmPassword) {

        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            return new Result(Result.Status.VALIDATION_ERROR,
                    "All fields are required.");
        }
        
        if (!isValidEmail(email)) {
            return new Result(Result.Status.VALIDATION_ERROR,
                    "Please enter a valid email address.");
        }

        if (!password.equals(confirmPassword)) {
            return new Result(Result.Status.VALIDATION_ERROR,
                    "Passwords do not match.");
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
 
        User user = new User(username, email, passwordHash, "CUSTOMER");
 
        try {
            userDAO.createUser(user);
            return new Result(Result.Status.SUCCESS,
                    "Account created successfully. Please log in.");
 
        } catch (DuplicateException e) {
            return new Result(Result.Status.DUPLICATE_ERROR,
                    "An account with that username or email already exists.");
 
        } catch (SQLException e) {
            return new Result(Result.Status.DATABASE_ERROR,
                    "A database error occurred. Please try again later.");
        }
    }

    public User login(String username, String password) throws SQLException {
 
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return null;
        }
 
        User user = userDAO.findUser(username);
 
        if (user == null) {
            return null;
        }
 
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }
 
        return null;
    }
 
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}