import java.util.List;

public class Order {

    private String username;
    private List<String> items;

    public Order(String username, List<String> items) {
        this.username = username;
        this.items = items;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getItems() {
        return items;
    }
}