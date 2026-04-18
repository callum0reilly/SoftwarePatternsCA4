import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private static MongoDBConnection instance;
    private MongoDatabase database;

    private MongoDBConnection() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        database = client.getDatabase("shopDB");
    }

    public static MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}