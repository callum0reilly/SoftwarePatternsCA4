
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        port(8080);

        //connect to HTML files
        staticFiles.location("/public");

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

        });
    }//end of main method
}//end of class

//http://localhost:8080
