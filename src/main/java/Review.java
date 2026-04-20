public class Review {

    private String productTitle;
    private int rating;
    private String comment;
    private String username;

    public Review(String productTitle, int rating, String comment, String username) {
        this.productTitle = productTitle;
        this.rating = rating;
        this.comment = comment;
        this.username = username;
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

    public String getUsername() {
        return username;
    }
}