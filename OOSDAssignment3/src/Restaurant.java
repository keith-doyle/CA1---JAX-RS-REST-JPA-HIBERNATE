
import java.util.List;

// Restaurant class manages the chef and waiter threads and handles order processing
class Restaurant {
    private Chef chef;
    private Waiter waiter;

    public Restaurant() {
        this.chef = new Chef(this); // Initialize chef thread with a reference to the restaurant
        this.waiter = new Waiter(this); // Initialize waiter thread with a reference to the restaurant
        this.chef.start(); // Start the chef thread
        this.waiter.start(); // Start the waiter thread
    }

    // Method to receive an order from a specific table
    public synchronized void receiveOrder(String tableNumber, List<String> orders) {
        chef.prepareDishes(tableNumber, orders); // Chef prepares the ordered dishes
    }

    // Method to serve a dish to a specific table
    public synchronized void serveDish(String tableNumber, String dish) {
        waiter.serveDish(tableNumber, dish); // Waiter serves the prepared dish to the table
    }
}
