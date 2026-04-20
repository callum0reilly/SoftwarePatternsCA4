public class DiscountCheckout extends CheckoutTemplate {

    @Override
    protected void processPayment() {
        System.out.println("Processing discounted payment");
    }
}