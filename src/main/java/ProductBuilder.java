public class ProductBuilder {

    private String title;
    private double price;
    private int stock;
    private String category;
    private String manufacturer;

    public ProductBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ProductBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setStock(int stock) {
        this.stock = stock;
        return this;
    }

    public ProductBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public ProductBuilder setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public Product build() {
        return new Product(title, price, stock, category, manufacturer);
    }
}