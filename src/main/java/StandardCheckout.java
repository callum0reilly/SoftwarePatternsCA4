public class StandardCheckout extends CheckoutTemplate {

    @Override
    protected void processPayment() {
        System.out.println("Processing standard payment");
    }
}