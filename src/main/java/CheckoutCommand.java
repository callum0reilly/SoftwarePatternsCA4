import java.util.List;

public class CheckoutCommand implements Command {

    private List<Product> cart;
    private CheckoutTemplate checkout;

    public CheckoutCommand(List<Product> cart, CheckoutTemplate checkout) {
        this.cart = cart;
        this.checkout = checkout;
    }

    public void execute() {
        checkout.process(cart);
    }
}