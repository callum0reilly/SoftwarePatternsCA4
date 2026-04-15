import java.util.ArrayList;
import java.util.List;

public class CartService {

    private static CartService instance;
    private List<Product> cart;

    private CartService() {
        cart = new ArrayList<>();
    }

    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    public void add(Product product) {
        cart.add(product);
    }

    public List<Product> getCart() {
        return cart;
    }
}