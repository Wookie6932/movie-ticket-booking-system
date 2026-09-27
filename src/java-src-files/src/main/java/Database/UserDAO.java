package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Account.User;

public class UserDAO {

    public boolean createUser(User user) {

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

            int rowsAffected = statement.executeUpdate();

            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findUser(String username, String passwordHash) {
 
        String sql = """
                SELECT user_id, username, email, password_hash, role
                FROM user
                WHERE username = ? AND password_hash = ?
                """;
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setString(1, username);
            statement.setString(2, passwordHash);
 
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
 
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
