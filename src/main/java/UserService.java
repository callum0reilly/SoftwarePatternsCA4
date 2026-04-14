import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static UserService instance;

    private List<User> users;

    private UserService() {
        users = new ArrayList<>();
        users.add(new User("Callum", "123456"));
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}