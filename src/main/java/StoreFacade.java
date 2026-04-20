import java.util.List;

public class StoreFacade {

    private static StoreFacade instance;

    private ProductService productService;
    private CartService cartService;
    private ReviewService reviewService;
    private OrderService orderService;
    private UserService userService;

    private StoreFacade() {
        productService = ProductService.getInstance();
        cartService = CartService.getInstance();
        reviewService = ReviewService.getInstance();
        orderService = OrderService.getInstance();
        userService = UserService.getInstance();
    }

    public static StoreFacade getInstance() {
        if (instance == null) {
            instance = new StoreFacade();
        }
        return instance;
    }

    // Products
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    public void addProduct(String title, double price, int stock, String category, String manufacturer) {
        productService.addProduct(title, price, stock, category, manufacturer);
    }

    public void deleteProduct(String title) {
        productService.deleteProduct(title);
    }

    public void updateStock(String title, int stock) {
        productService.updateStock(title, stock);
    }

    // Cart
    public void addToCart(Product product) {
        cartService.add(product);
    }

    public List<Product> getCart() {
        return cartService.getCart();
    }

    // Reviews
    public void addReview(String title, int rating, String comment, String username) {
        reviewService.addReview(title, rating, comment, username);
    }

    public List<Review> getReviewsForProduct(String title) {
        return reviewService.getReviewsForProduct(title);
    }

    public double getAverageRating(String title) {
        return reviewService.getAverageRating(title);
    }

    // Orders
    public void saveOrder(String username, List<Product> cart) {
        orderService.saveOrder(username, cart);
    }

    public List<org.bson.Document> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Users
    public User findUser(String username) {
        return userService.findUser(username);
    }

    public void registerUser(String username, String password, String address, String payment) {
        userService.registerUser(username, password, address, payment);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}