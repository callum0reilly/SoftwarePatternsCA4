
import static spark.Spark.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        port(8080);

        //connect to HTML files
        staticFiles.location("/public");

        get("/", (req, res) -> {
            res.redirect("/login.html");
            return null;
        });

        post("/login",(req,res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            UserService userService = UserService.getInstance();

            User user = userService.findUser(username);

            if (user != null && user.getPassword().equals(password)) {
                req.session().attribute("user", user);

                if (user.getRole().equals("admin")) {
                    res.redirect("/admin");
                } else {
                    res.redirect("/products");
                }
                return null;
            }
            return "Invalid username or password";

        });//end of /login

        get("/products", (req, res) -> {

            String search = req.queryParams("search");
            String sort = req.queryParams("sort");

            List<Product> products = new ArrayList<>(ProductService.getInstance().getProducts());

            //search
            if (search != null && !search.isEmpty()) {
                String s = search.toLowerCase();
                products.removeIf(p ->
                        !(
                                        p.getTitle().toLowerCase().contains(s) ||
                                        p.getCategory().toLowerCase().contains(s) ||
                                        p.getManufacturer().toLowerCase().contains(s)
                        )
                );
            }

            //sort using strategy pattern
            SortStrategy strategy = null;

            if ("asc".equals(sort)) {
                strategy = new SortAsc();
            } else if ("desc".equals(sort)) {
                strategy = new SortDesc();
            }

            if (strategy != null) {
                strategy.sort(products);
            }

            //display them
            String html = "<html><head><link rel='stylesheet' href='style.css'></head><body>";

            html += "<div class='page-top'>";
            html += "<h1>Home page</h1>";
            html += "<a href='/cart'>View Cart</a>";
            html += "</div>";

            html += "<div class='search-box'>";
            html += "<form action='/products' method='get'>" +
                    "<input type='text' name='search' placeholder='Search products'>" +
                    "<select name='sort'>" +
                    "<option value='none'>No Sort</option>" +
                    "<option value='asc'>Price Asc</option>" +
                    "<option value='desc'>Price Desc</option>" +
                    "</select>" +
                    "<button type='submit'>Search</button>" +
                    "</form>";
            html += "</div>";

            html += "<h2>Products</h2>";
            html += "<div class='product-list'>";

            for (Product p : products) {

                String warning = p.isLowStock() ? "<span class='low-stock'>Low stock</span>" : "";

                html += "<div class='product-card'>";

                html += "<div class='product-top'>";
                html += "<h3>" + p.getTitle() + "</h3>";
                html += "<p class='price'>€" + p.getPrice() + "</p>";
                html += "<p>Category: " + p.getCategory() + "</p>";
                html += "<p>Brand: " + p.getManufacturer() + "</p>";
                html += "<p>Stock: " + p.getStock() + " " + warning + "</p>";
                html += "</div>";

                html += "<form action='/add-to-cart' method='post' class='cart-form'>";
                html += "<input type='hidden' name='title' value='" + p.getTitle() + "'>";

                if (p.getStock() > 0) {
                    html += "<button type='submit'>Add to Cart</button>";
                } else {
                    html += "<button type='submit' disabled>Out of Stock</button>";
                }

                html += "</form>";

                html += "<form action='/add-review' method='post' class='review-form'>";
                html += "<input type='hidden' name='title' value='" + p.getTitle() + "'>";
                html += "<label for='rating'>Rating</label>";
                html += "<select name='rating'>";
                html += "<option value='1'>1</option>";
                html += "<option value='2'>2</option>";
                html += "<option value='3'>3</option>";
                html += "<option value='4'>4</option>";
                html += "<option value='5'>5</option>";
                html += "</select>";
                html += "<input type='text' name='comment' placeholder='Write a review'>";
                html += "<button type='submit'>Submit Review</button>";
                html += "</form>";

                List<Review> reviews = ReviewService.getInstance().getReviewsForProduct(p.getTitle());
                double avg = ReviewService.getInstance().getAverageRating(p.getTitle());

                if (!reviews.isEmpty()) {
                    html += "<p><strong>Average Rating: " + String.format("%.1f", avg) + " / 5</strong></p>";
                }

                html += "<div class='reviews'>";
                if (reviews.isEmpty()) {
                    html += "<p class='no-reviews'>No reviews yet</p>";
                } else {
                    for (Review r : reviews) {
                        html += "<div class='review-item'>";
                        html += "<strong>" + r.getUsername() + ": " + r.getRating() + "/5</strong>";
                        html += "<p>" + r.getComment() + "</p>";
                        html += "</div>";
                    }
                }
                html += "</div>";

                html += "</div>";
            }

            html += "</div>";
            html += "</body></html>";
            return html;
        });

        post("/add-to-cart", (req, res) -> {

            String title = req.queryParams("title");

            ProductService ps = ProductService.getInstance();
            CartService cs = CartService.getInstance();

            for (Product p : ps.getProducts()) {
                if (p.getTitle().equals(title)) {
                    if (p.getStock() > 0) {
                        cs.add(p);
                    }
                    break;
                }
            }

            res.redirect("/products");
            return null;
        });

        get("/cart", (req, res) -> {

            List<Product> cart = CartService.getInstance().getCart();

            String html = "<h1>Cart</h1>";
            html += "<a href='/products'>Back</a><br><br>";

            for (Product p : cart) {
                html += "<p>" + p.getTitle() + " - €" + p.getPrice() + "</p>";
            }

            html += "<form action='/checkout' method='post'>" +
                    "<button type='submit'>Checkout</button>" +
                    "</form>";

            return html;
        });

        post("/checkout", (req, res) -> {

            CartService cs = CartService.getInstance();

            Command checkout = new CheckoutCommand(cs.getCart());
            User user = req.session().attribute("user");
            OrderService.getInstance().saveOrder(user.getUsername(), cs.getCart());
            try {
                checkout.execute();
                return "<h1>Purchase successful</h1><a href='/products'>Continue</a>";
            } catch (RuntimeException e) {
                return "<h1>" + e.getMessage() + "</h1><a href='/cart'>Back to Cart</a>";
            }
        });

        post("/add-review", (req, res) -> {

            String title = req.queryParams("title");
            int rating = Integer.parseInt(req.queryParams("rating"));
            String comment = req.queryParams("comment");
            User user = req.session().attribute("user");
            String username = (user != null) ? user.getUsername() : "Guest";

            ReviewService.getInstance().addReview(title, rating, comment, username);

            res.redirect("/products");
            return null;
        });//end of add review

        get("/admin", (req, res) -> {

            List<Product> products = ProductService.getInstance().getProducts();

            String html = "<h1>Admin Panel</h1>";
            html += "<a href='/products'>Go to shop</a><br><br>";

            for (Product p : products) {

                String warning = p.isLowStock() ? " - LOW STOCK!" : "";

                html += "<form action='/update-stock' method='post'>" +
                        "<input type='hidden' name='title' value='" + p.getTitle() + "'>" +
                        "<p>" + p.getTitle() + " - Stock: " + p.getStock() + warning + "</p>" +
                        "<input type='number' name='stock' placeholder='New stock'>" +
                        "<button type='submit'>Update</button>" +
                        "</form>";
            }

            return html;
        }); //end of admin

        post("/update-stock", (req, res) -> {

            String title = req.queryParams("title");
            int stock = Integer.parseInt(req.queryParams("stock"));

            ProductService.getInstance().updateStock(title, stock);

            res.redirect("/admin");
            return null;
        });//end of update stock

        post("/register", (req, res) -> {

            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String address = req.queryParams("address");
            String payment = req.queryParams("payment");

            UserService.getInstance().registerUser(username, password, address, payment);

            res.redirect("/login.html");
            return null;
        });//end of register
    }//end of main method
}//end of class

//http://localhost:8080
