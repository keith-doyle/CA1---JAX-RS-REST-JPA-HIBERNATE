
// Waiter class represents a thread responsible for serving dishes
class Waiter extends Thread {
    private Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant; // Reference to the restaurant
    }

    // Method to serve a dish to a specific table
    public void serveDish(String tableNumber, String dish) {
        System.out.println("Serving " + dish + " to Table " + tableNumber); // Print serving message (for demonstration)
    }
}