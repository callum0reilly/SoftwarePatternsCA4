public class DiscountDecorator extends PriceDecorator {

    public DiscountDecorator(PriceComponent component) {
        super(component);
    }

    @Override
    public double getPrice() {
        return component.getPrice() * 0.9; // 10% discount
    }
}