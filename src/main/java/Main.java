
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

                if (user.getRole().equals("admin")) {
                    res.redirect("/admin");
                } else {
                    res.redirect("/ShoppingHomePage.html");
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
                products.removeIf(p ->
                        !p.getTitle().toLowerCase().contains(search.toLowerCase())
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

            html += "<h1>Home page</h1>";

            html += "<a href='/cart'>View Cart</a><br><br>";

            html += "<form action='/products' method='get'>" +
                    "<input type='text' name='search' placeholder='Search products'>" +
                    "<select name='sort'>" +
                    "<option value='none'>No Sort</option>" +
                    "<option value='asc'>Price Asc</option>" +
                    "<option value='desc'>Price Desc</option>" +
                    "</select>" +
                    "<button type='submit'>Search</button>" +
                    "</form>";

            html += "<h2>Products</h2>";

            for (Product p : products) {
                html += "<form action='/add-to-cart' method='post'>" +
                        "<input type='hidden' name='title' value='" + p.getTitle() + "'>" +
                        "<p>" + p.getTitle() + " - €" + p.getPrice() + " (Stock: " + p.getStock() +")" +"</p>";

            if (p.getStock() > 0) {
                    html += "<button type='submit'>Add to Cart</button>";
                } else {
                    html += "<button disabled>Out of Stock</button>";
                }

                html += "</form>";
            }

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

            try {
                checkout.execute();
                return "<h1>Purchase successful</h1><a href='/products'>Continue</a>";
            } catch (RuntimeException e) {
                return "<h1>" + e.getMessage() + "</h1><a href='/cart'>Back to Cart</a>";
            }
        });

        get("/admin", (req, res) -> {

            List<Product> products = ProductService.getInstance().getProducts();

            String html = "<h1>Admin Panel</h1>";
            html += "<a href='/products'>Go to shop</a><br><br>";

            for (Product p : products) {
                html += "<form action='/update-stock' method='post'>" +
                        "<input type='hidden' name='title' value='" + p.getTitle() + "'>" +
                        "<p>" + p.getTitle() + " - Stock: " + p.getStock() + "</p>" +
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
    }//end of main method
}//end of class

//http://localhost:8080
