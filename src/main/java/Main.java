
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
            String html = "<h2>Products</h2>";

            for (Product p : products) {
                html += "<p>" + p.getTitle() + " - €" + p.getPrice() + "</p>";
            }

            return html;
        });
    }//end of main method
}//end of class

//http://localhost:8080
