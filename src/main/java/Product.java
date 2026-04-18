public class Product {
    private String title;
    private double price;
    private int stock;

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
}//end of class