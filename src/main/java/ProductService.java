import java.util.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ProductService {

    private ProductFactory factory;
    private static ProductService instance;
    private MongoCollection<Document> collection;

    private ProductService() {
        MongoDatabase db = MongoDBConnection.getInstance().getDatabase();
        collection = db.getCollection("products");
        factory = new DefaultProductFactory();
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    public List<Product> getProducts() {

        List<Product> products = new ArrayList<>();

        for (Document doc : collection.find()) {
            String title = doc.getString("title");
            double price = ((Number) doc.get("price")).doubleValue();
            int stock = ((Number) doc.get("stock")).intValue();

            products.add(factory.createProduct(title, price, stock));
        }
        return products;
    }//end of getProducts

    public void updateStock(String title, int newStock) {
        collection.updateOne(
                new Document("title", title),
                new Document("$set", new Document("stock", newStock))
        );
    }
}