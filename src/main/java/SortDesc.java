import java.util.List;

public class SortDesc implements SortStrategy {
    public void sort(List<Product> products) {
        products.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
    }
}