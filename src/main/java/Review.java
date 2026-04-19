public class Review {

    private String productTitle;
    private int rating;
    private String comment;

    public Review(String productTitle, int rating, String comment) {
        this.productTitle = productTitle;
        this.rating = rating;
        this.comment = comment;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}