import java.util.List;

// Chef class represents a thread responsible for preparing dishes
class Chef extends Thread {
    private Restaurant restaurant;
    private String tableNumber;
    private List<String> orders;

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant; // Reference to the restaurant
    }

    // Method for the waiter to notify the chef to prepare dishes for a specific table
    public synchronized void prepareDishes(String tableNumber, List<String> orders) {
        this.tableNumber = tableNumber; // Set the table number for which dishes are being prepared
        this.orders = orders; // Set the list of orders (dishes) to be prepared
        notify(); // Notify the chef thread to start preparing dishes
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    wait(); // Chef thread waits until notified to prepare dishes
                }
                // Simulate dish preparation (e.g., by sleeping for some time)
                Thread.sleep(2000); // Sleep for 2 seconds to simulate dish preparation time
                System.out.println("Dishes prepared for Table " + tableNumber);
                for (String dish : orders) {
                    restaurant.serveDish(tableNumber, dish); // Serve each prepared dish to the table
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}