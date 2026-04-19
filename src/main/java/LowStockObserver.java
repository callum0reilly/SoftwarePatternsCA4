public class LowStockObserver implements StockObserver {

    @Override
    public void update(Product product) {
        if (product.getStock() <= 2) {
            product.setLowStock(true);
        } else {
            product.setLowStock(false);
        }
    }
}