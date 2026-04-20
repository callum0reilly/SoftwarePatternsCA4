import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;
import java.util.ArrayList;

public class OrderService {

    private static OrderService instance;
    private MongoCollection<Document> collection;

    private OrderService() {
        collection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("orders");
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public void saveOrder(String username, List<Product> cart) {

        List<String> items = new ArrayList<>();

        for (Product p : cart) {
            items.add(p.getTitle());
        }

        Document doc = new Document("username", username)
                .append("items", items);

        collection.insertOne(doc);
    }
}