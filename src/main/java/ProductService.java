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
            String category = doc.getString("category");
            String manufacturer = doc.getString("manufacturer");

            Product p = factory.createProduct(title, price, stock,category,manufacturer);
            p.addObserver(new LowStockObserver());
            p.notifyObservers();
            products.add(p);
        }
        return products;
    }//end of getProducts

    public void updateStock(String title, int newStock) {
        collection.updateOne(
                new Document("title", title),
                new Document("$set", new Document("stock", newStock))
        );

        for (Product p : getProducts()) {
            if (p.getTitle().equals(title)) {
                p.notifyObservers();
                break;
            }
        }
    }
}