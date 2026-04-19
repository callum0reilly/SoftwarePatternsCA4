import java.util.ArrayList;
import java.util.List;

public class Product {
    private String title;
    private double price;
    private int stock;
    private boolean lowStock = false;
    private List<StockObserver> observers = new ArrayList<>();

    public Product(String title, double price,int stock) {
        this.title = title;
        this.price = price;
        this.stock= stock;
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
}//end of class