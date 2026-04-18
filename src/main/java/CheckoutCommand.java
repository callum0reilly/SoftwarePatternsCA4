import java.util.List;

public class CheckoutCommand implements Command {

    private List<Product> cart;

    public CheckoutCommand(List<Product> cart) {
        this.cart = cart;
    }

    public void execute() {
        for (Product p : cart) {
            if (p.getStock() > 0) {
                p.setStock(p.getStock() - 1);
            }
        }

        cart.clear();
    }
}