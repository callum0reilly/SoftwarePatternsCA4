public class DefaultProductFactory implements ProductFactory {

    @Override
    public Product createProduct(String title, double price, int stock, String category, String manufacturer) {
        return new ProductBuilder()
                .setTitle(title)
                .setPrice(price)
                .setStock(stock)
                .setCategory(category)
                .setManufacturer(manufacturer)
                .build();
    }
}