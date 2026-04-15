import java.util.*;


public class ProductService {

    private static ProductService instance;
    private List<Product> products;

    private ProductService() {
        products = new ArrayList<>();
        products.add(new Product("Tshirt", 25));
        products.add(new Product("Hoodie", 50));
        products.add(new Product("Jacket", 120));
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    public List<Product> getProducts() {
        return products;
    }
}