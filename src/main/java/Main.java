
import static spark.Spark.*;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class Main {
    public static void main(String[] args) {

        port(8080);

        //connect to HTML files
        staticFiles.location("/public");

        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        post("/login",(req,res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            User user = StoreFacade.getInstance().findUser(username);

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

            List<Product> products = new ArrayList<>(StoreFacade.getInstance().getProducts());

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
                double original = p.getPrice(); //original object
                double finalPrice = original;

                if (original > 50) {
                    finalPrice = new DiscountDecorator(p).getPrice();
                    html += "<p class='price'>€" + finalPrice +
                            " <span style='text-decoration:line-through;'>€" + original + "</span></p>";
                } else {
                    html += "<p class='price'>€" + original + "</p>";
                }
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

                List<Review> reviews = StoreFacade.getInstance().getReviewsForProduct(p.getTitle());
                double avg = StoreFacade.getInstance().getAverageRating(p.getTitle());

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

            StoreFacade facade = StoreFacade.getInstance();
            for (Product p : facade.getProducts()) {
                if (p.getTitle().equals(title)) {
                    if (p.getStock() > 0) {
                        facade.addToCart(p);
                    }
                    break;
                }
            }

            res.redirect("/products");
            return null;
        });

        get("/cart", (req, res) -> {

            List<Product> cart = StoreFacade.getInstance().getCart();

            String html = "<html><head><link rel='stylesheet' href='style.css'></head><body>";

            html += "<div class='page-top'>";
            html += "<h1>Cart</h1>";
            html += "<a href='/products'>Back to Shop</a>";
            html += "</div>";

            html += "<div class='product-list'>";

            if (cart.isEmpty()) {
                html += "<p style='text-align:center; color:#777;'>Your cart is empty.</p>";
            } else {
                double total = 0;

                for (Product p : cart) {
                    double price = p.getPrice();
                    if (price > 50) {
                        price = new DiscountDecorator(p).getPrice();
                    }
                    total += price;

                    html += "<div class='product-card'>";
                    html += "<p><strong>" + p.getTitle() + "</strong></p>";
                    if (p.getPrice() > 50) {
                        html += "<p class='price'>€" + price +
                                " <span style='text-decoration:line-through; color:#999;'>€" + p.getPrice() + "</span></p>";
                    } else {
                        html += "<p class='price'>€" + price + "</p>";
                    }
                    html += "</div>";
                }

                html += "<div class='product-card'>";
                html += "<p><strong>Total: €" + String.format("%.2f", total) + "</strong></p>";
                html += "</div>";
            }

            html += "</div>";

            html += "<div style='text-align:center; margin-top:20px;'>";
            html += "<form action='/checkout' method='post'>";
            html += "<button type='submit'>Checkout</button>";
            html += "</form>";
            html += "</div>";

            html += "</body></html>";
            return html;
        });

        post("/checkout", (req, res) -> {

            StoreFacade facade = StoreFacade.getInstance();

            Command checkout = new CheckoutCommand(facade.getCart(), new StandardCheckout());
            User user = req.session().attribute("user");
            List<Product> cart = facade.getCart();

            List<Double> finalPrices = new ArrayList<>();

            for (Product p : cart) {
                PriceComponent price = p;

                //apply discount if price > 50
                if (p.getPrice() > 50) {
                    price = new DiscountDecorator(price);
                }

                finalPrices.add(price.getPrice());
            }
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

            StoreFacade.getInstance().addReview(title, rating, comment, username);

            res.redirect("/products");
            return null;
        });//end of add review

        get("/admin", (req, res) -> {
            User user = req.session().attribute("user");

            if (user == null || !user.getRole().equals("admin")) {
                res.redirect("/index.html");
                return null;
            }

            List<Product> products = StoreFacade.getInstance().getProducts();
            String html = "<html><head><link rel='stylesheet' href='style.css'></head><body>";
            html += "<div class='page-top'>";
            html += "<h1>Admin Panel</h1>";
            html += "<a href='/products'>Go to shop</a>";
            html += "</div>";

            html += "<div class='admin-grid'>";
            // ADD PRODUCT
            html += "<div class='admin-section'>";
            html += "<h2>Add Product</h2>";
            html += "<form action='/add-product' method='post' class='admin-form'>";
            html += "<input type='text' name='title' placeholder='Title' required>";
            html += "<input type='number' step='0.01' name='price' placeholder='Price' required>";
            html += "<input type='number' name='stock' placeholder='Stock' required>";
            html += "<input type='text' name='category' placeholder='Category' required>";
            html += "<input type='text' name='manufacturer' placeholder='Manufacturer' required>";
            html += "<button type='submit'>Add Product</button>";
            html += "</form>";
            html += "</div>";

            // MANAGE STOCK
            html += "<div class='admin-section'>";
            html += "<h2>Manage Stock</h2>";

            for (Product p : products) {
                String warning = p.isLowStock() ? " <span class='low-stock'>LOW STOCK</span>" : "";
                html += "<div class='product-card'>";
                html += "<p><strong>" + p.getTitle() + "</strong> — Stock: " + p.getStock() + warning + "</p>";
                html += "<div class='admin-actions'>";
                html += "<form action='/update-stock' method='post' class='inline-form'>";
                html += "<input type='hidden' name='title' value='" + p.getTitle() + "'>";
                html += "<input type='number' name='stock' placeholder='New stock'>";
                html += "<button type='submit'>Update</button>";
                html += "</form>";
                html += "<form action='/delete-product' method='post' class='inline-form' onsubmit='return confirm(\"Are you sure?\")'>";
                html += "<input type='hidden' name='title' value='" + p.getTitle() + "'>";
                html += "<button type='submit' class='btn-danger'>Delete</button>";
                html += "</form>";
                html += "</div>";
                html += "</div>";
            }

            html += "</div>";
            html += "</div>"; // end admin-grid

            // CUSTOMERS
            html += "<div class='admin-section wide-section'>";
            html += "<h2>Customers</h2>";
            html += "<div class='product-list'>";

            List<User> users = StoreFacade.getInstance().getAllUsers();
            for (User u : users) {
                if (u.getRole().equals("customer")) {
                    html += "<div class='product-card'>";
                    html += "<p><strong>Username:</strong> " + u.getUsername() + "</p>";
                    html += "<p><strong>Address:</strong> " + u.getAddress() + "</p>";
                    html += "<p><strong>Payment Method:</strong> " + u.getPayment() + "</p>";
                    html += "</div>";
                }
            }

            html += "</div></div>";

            // ORDERS
            html += "<div class='admin-section wide-section'>";
            html += "<h2>Orders</h2>";
            html += "<div class='product-list'>";

            List<Document> orders = StoreFacade.getInstance().getAllOrders();
            for (Document o : orders) {
                html += "<div class='product-card'>";
                html += "<p><strong>User:</strong> " + o.getString("username") + "</p>";
                List<String> items = (List<String>) o.get("items");
                html += "<ul>";
                for (String item : items) {
                    html += "<li>" + item + "</li>";
                }
                html += "</ul>";
                html += "</div>";
            }

            html += "</div></div>";
            html += "</body></html>";
            return html;
        }); // end of admin

        post("/update-stock", (req, res) -> {

            String title = req.queryParams("title");
            int stock = Integer.parseInt(req.queryParams("stock"));

            User user = req.session().attribute("user");
            new AdminProxy(user).updateStock(title, stock);

            res.redirect("/admin");
            return null;
        });//end of update stock

        post("/register", (req, res) -> {

            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String address = req.queryParams("address");
            String payment = req.queryParams("payment");

            StoreFacade.getInstance().registerUser(username, password, address, payment);

            res.redirect("/index.html");
            return null;
        });//end of register

        post("/add-product", (req, res) -> {

            String title = req.queryParams("title");
            double price = Double.parseDouble(req.queryParams("price"));
            int stock = Integer.parseInt(req.queryParams("stock"));
            String category = req.queryParams("category");
            String manufacturer = req.queryParams("manufacturer");

            User user = req.session().attribute("user");
            new AdminProxy(user).addProduct(title, price, stock, category, manufacturer);

            res.redirect("/admin");
            return null;
        });//end of add product

        post("/delete-product", (req, res) -> {

            String title = req.queryParams("title");

            User user = req.session().attribute("user");
            new AdminProxy(user).deleteProduct(title);

            res.redirect("/admin");
            return null;
        });//end of delete product
    }//end of main method
}//end of class

//http://localhost:8080
