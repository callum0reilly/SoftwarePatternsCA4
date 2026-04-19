public class User {
    private String username;
    private String password;
    private String role;
    private String address;
    private String payment;

    public User(String username, String password, String role, String address, String payment) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.address = address;
        this.payment = payment;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public String getPayment() {
        return payment;
    }
}