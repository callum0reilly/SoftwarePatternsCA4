public class AdminProxy {

    private ProductService productService;
    private User user;

    public AdminProxy(User user) {
        this.user = user;
        this.productService = ProductService.getInstance();
    }

    private boolean isAdmin() {
        return user != null && user.getRole().equals("admin");
    }

    public void addProduct(String title, double price, int stock, String category, String manufacturer) {
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: admins only");
        }
        productService.addProduct(title, price, stock, category, manufacturer);
    }

    public void deleteProduct(String title) {
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: admins only");
        }
        productService.deleteProduct(title);
    }

    public void updateStock(String title, int stock) {
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: admins only");
        }
        productService.updateStock(title, stock);
    }
}