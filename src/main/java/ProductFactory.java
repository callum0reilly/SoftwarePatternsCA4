public interface ProductFactory {
    Product createProduct(String title, double price, int stock, String category, String manufacturer);
}