import auth.Auth;
import auth.User;
import product.Product;
import product.ProductCatalog;
import cart.ShoppingCart;
import cart.CheckOut;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Auth auth = new Auth();
        ProductCatalog catalog = new ProductCatalog();
        ShoppingCart cart = new ShoppingCart();
        CheckOut checkout = new CheckOut();

        // Register or Login
        System.out.println("Welcome to the E-Commerce Platform!");
        System.out.println("Do you have an account? (yes/no)");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("no")) {
            System.out.print("Enter a username: ");
            String username = scanner.nextLine();
            System.out.print("Enter a password: ");
            String password = scanner.nextLine();
            auth.register(username, password);
        }

        // Login attempt with retry logic
        int loginAttempts = 0;
        User user = null;
        while (loginAttempts < 3) {  // Allow up to 3 login attempts
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            user = auth.login(username, password);

            if (user != null) {
                break;  // Login successful, exit loop
            } else {
                loginAttempts++;
                System.out.println("Login failed. Invalid credentials. Attempt " + loginAttempts + " of 3.");
            }
        }

        if (user == null) {
            System.out.println("Too many failed login attempts. Exiting...");
            scanner.close(); // Close scanner before exiting
            return;  // Exit the program after 3 failed attempts
        }

        // Show product catalog
        System.out.println("Available products:");
        for (Product product : catalog.getProducts()) {
            System.out.println(product.getName() + " - $" + product.getPrice() + " - " + product.getStockQuantity() + " in stock");
        }

        // Shopping Cart
        while (true) {
            System.out.print("Enter product name to add to cart (or 'checkout' to finish): ");
            String productName = scanner.nextLine();
            if (productName.equalsIgnoreCase("checkout")) {
                checkout.checkout(cart);
                break;
            }

            Product product = catalog.getProductByName(productName);
            if (product != null) {
                System.out.print("Enter quantity: ");
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a valid number.");
                    continue;  // Prompt for input again
                }

                // Ensure sufficient stock
                if (quantity > product.getStockQuantity()) {
                    System.out.println("Insufficient stock. Please enter a smaller quantity.");
                } else {
                    cart.addToCart(product, quantity);
                    catalog.reduceStock(productName, quantity);  // Reduce stock after adding to cart
                }
            } else {
                System.out.println("Product not found.");
            }

            // Display current cart
            cart.displayCart();

            // Ask if the user wants to continue shopping or checkout
            System.out.print("Would you like to continue shopping? (yes/no): ");
            String continueShopping = scanner.nextLine();
            if (continueShopping.equalsIgnoreCase("no")) {
                checkout.checkout(cart);
                break;
            }
        }

        scanner.close();
    }
}
