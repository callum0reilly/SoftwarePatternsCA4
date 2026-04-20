import java.util.List;

public abstract class CheckoutTemplate {

    public void process(List<Product> cart) {
        validate(cart);
        processPayment();
        updateStock(cart);
    }

    protected abstract void processPayment();

    private void validate(List<Product> cart) {
        for (Product p : cart) {
            if (p.getStock() <= 0) {
                throw new RuntimeException("Item out of stock: " + p.getTitle());
            }
        }
    }

    private void updateStock(List<Product> cart) {
        for (Product p : cart) {
            if (p.getStock() > 0) {
                p.setStock(p.getStock() - 1);
                p.notifyObservers();
            }
        }
        cart.clear();
    }
}