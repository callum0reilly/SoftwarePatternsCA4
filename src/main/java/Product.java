import java.util.ArrayList;
import java.util.List;

public class Product implements PriceComponent{
    private String title;
    private double price;
    private int stock;
    private boolean lowStock = false;
    private String category;
    private String manufacturer;
    private List<StockObserver> observers = new ArrayList<>();

    public Product(String title, double price, int stock, String category, String manufacturer) {
        this.title = title;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.manufacturer = manufacturer;
    }//end of constructore

    public String getTitle() {
        return title;
    }//end of getTitle

    public double getPrice() {
        return price;
    }//end of getPrice

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(this);
        }
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public String getCategory() {
        return category;
    }

    public String getManufacturer() {
        return manufacturer;
    }
}//end of class