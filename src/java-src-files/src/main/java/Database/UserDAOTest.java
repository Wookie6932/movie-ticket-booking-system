package Database;

import Account.User;

public class UserDAOTest {

    public static void main(String[] args) {

        User testUser = new User(
                "testuser",
                "test@gmail.com",
                "passwordtest123",
                "CUSTOMER"
        );

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.createUser(testUser);

        if (success) {
            System.out.println("User created successfully");
        } else {
            System.out.println("User creation failed");
        }
    }
}