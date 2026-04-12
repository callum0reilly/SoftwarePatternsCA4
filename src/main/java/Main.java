
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        port(8080);

        //connect to HTML files
        staticFiles.location("/public");

        get("/hello", (req, res) -> "Hello from backend");

        post("/submit", (req, res) -> {
            String name = req.queryParams("name");
            return "Hello " + name;
        });
    }
}

//http://localhost:8080
