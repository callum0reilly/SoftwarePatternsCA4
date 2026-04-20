import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class UserService {

    private static UserService instance;
    private MongoCollection<Document> collection;
    private UserFactory factory;

    private UserService() {
        MongoDatabase db = MongoDBConnection.getInstance().getDatabase();
        collection = db.getCollection("users");
        factory = new DefaultUserFactory();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User findUser(String username) {

        Document doc = collection.find(new Document("username", username)).first();

        if (doc == null) return null;

        return factory.createUser(
                doc.getString("username"),
                doc.getString("password"),
                doc.getString("role"),
                doc.getString("address"),
                doc.getString("payment")
        );
    }//end of findUser

    public void registerUser(String username, String password, String address, String payment) {

        Document doc = new Document("username", username)
                .append("password", password)
                .append("role", "customer")
                .append("address", address)
                .append("payment", payment);

        collection.insertOne(doc);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            users.add(factory.createUser(
                    doc.getString("username"),
                    doc.getString("password"),
                    doc.getString("role"),
                    doc.getString("address"),
                    doc.getString("payment")
            ));
        }
        return users;
    }
}