package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Account.User;

public class UserDAO {

    private static final int MYSQL_DUPLICATE_ENTRY = 1062;

    public void createUser(User user) throws DuplicateException, SQLException {

        String sql = """
                INSERT INTO user (username, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());

            statement.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_ENTRY) {
                throw new DuplicateException(
                        "An account with that username or email already exists.");
            }
            throw e;
        }
    }

    public User findUser(String username) throws SQLException {
 
        String sql = """
                SELECT user_id, username, email, password_hash, role
                FROM user
                WHERE username = ? AND password_hash = ?
                """;
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setString(1, username);
 
            ResultSet resultSet = statement.executeQuery();
 
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("role")
                );
            }
 
            return null;
        }
    }
}
