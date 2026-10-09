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
        try {
            userDAO.createUser(testUser);
            System.out.println("User created successfully");
        } catch (DuplicateException e) {
            System.out.println("Duplicate user: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("User creation failed");
            e.printStackTrace();
        }
    }
}