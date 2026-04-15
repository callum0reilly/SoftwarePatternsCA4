
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
                res.redirect("/ShoppingHomePage.html");
                return null ;
            } else {
                return "Invalid username or password";
            }

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
                        "<p>" + p.getTitle() + " - €" + p.getPrice() + "</p>" +
                        "<button type='submit'>Add to Cart</button>" +
                        "</form>";
            }

            return html;
        });

        post("/add-to-cart", (req, res) -> {

            String title = req.queryParams("title");

            ProductService ps = ProductService.getInstance();
            CartService cs = CartService.getInstance();

            for (Product p : ps.getProducts()) {
                if (p.getTitle().equals(title)) {
                    cs.add(p);
                    break;
                }
            }

            res.redirect("/products");
            return null;
        });

        get("/cart", (req, res) -> {

            List<Product> cart = CartService.getInstance().getCart();

            String html = "<h1>Cart</h1>";

            for (Product p : cart) {
                html += "<p>" + p.getTitle() + " - €" + p.getPrice() + "</p>";
            }

            return html;
        });
    }//end of main method
}//end of class

//http://localhost:8080
