public class DefaultProductFactory implements ProductFactory {

    @Override
    public Product createProduct(String title, double price, int stock, String category, String manufacturer) {
        return new Product(title, price, stock, category, manufacturer);
    }
}