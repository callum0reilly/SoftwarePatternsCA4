public interface UserFactory {
    User createUser(String username, String password, String role, String address, String payment);
}