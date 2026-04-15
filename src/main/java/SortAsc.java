import java.util.List;

public class SortAsc implements SortStrategy {
    public void sort(List<Product> products) {
        products.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
    }
}