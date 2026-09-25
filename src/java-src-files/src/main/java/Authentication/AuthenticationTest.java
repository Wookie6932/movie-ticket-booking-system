package Authentication;

public class AuthenticationTest {

    public static void main(String[] args) {

        AuthenticationService authService =
                new AuthenticationService();

        String result = authService.createAccount(
                "testuser",
                "test@gmail.com",
                "superstrongpassword",
                "superstrongpassword"
        );

        System.out.println(result);
    }
}