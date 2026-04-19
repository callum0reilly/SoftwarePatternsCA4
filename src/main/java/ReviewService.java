import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private static ReviewService instance;
    private MongoCollection<Document> collection;

    private ReviewService() {
        MongoDatabase db = MongoDBConnection.getInstance().getDatabase();
        collection = db.getCollection("reviews");
    }

    public static ReviewService getInstance() {
        if (instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    public void addReview(String productTitle, int rating, String comment) {
        Document doc = new Document("productTitle", productTitle)
                .append("rating", rating)
                .append("comment", comment);

        collection.insertOne(doc);
    }

    public List<Review> getReviewsForProduct(String productTitle) {

        List<Review> reviews = new ArrayList<>();

        for (Document doc : collection.find(new Document("productTitle", productTitle))) {
            reviews.add(new Review(
                    doc.getString("productTitle"),
                    doc.getInteger("rating"),
                    doc.getString("comment")
            ));
        }

        return reviews;
    }
}