import java.util.List;

public class CheckoutCommand implements Command {

    private List<Product> cart;

    public CheckoutCommand(List<Product> cart) {
        this.cart = cart;
    }

    public void execute() {

        //check if the product is in stock first
        for (Product p : cart) {
            if (p.getStock() <= 0) {
                throw new RuntimeException("Item out of stock: " + p.getTitle());
            }
        }

        //if there is stock take away whatever they ordered
        for (Product p : cart) {
            if (p.getStock() > 0) {
                p.setStock(p.getStock() - 1);
                p.notifyObservers();
            }
        }

        cart.clear();
    }
}