public class DefaultProductFactory implements ProductFactory {

    @Override
    public Product createProduct(String title, double price, int stock) {
        return new Product(title, price, stock);
    }
}