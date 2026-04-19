public class DefaultUserFactory implements UserFactory {

    @Override
    public User createUser(String username, String password, String role, String address, String payment) {
        return new User(username, password, role, address, payment);
    }
}