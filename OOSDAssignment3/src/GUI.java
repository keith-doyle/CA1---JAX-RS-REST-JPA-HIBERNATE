import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame implements ActionListener {
    private JTextField tableField;
    private JLabel servedLabel;
    private JTextArea servedTextArea;
    private JButton btnSubmit, btnFinish;
    private Map<String, List<String>> currentOrders;
    private Restaurant restaurant;

    // Dish names corresponding to each dish button
    private static final String[] dishNames = {"Pizza", "Chicken Burger", "Chicken Nuggets", "Hot Dogs", "Fries", "Water", "Coke", "Fanta"};

    public GUI() {
        super("Restaurant Order System");
        this.setSize(800, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Initialize components

        // Panel for table input
        JPanel panelTable = new JPanel();
        panelTable.setBorder(new TitledBorder("Table"));
        JLabel tableLabel = new JLabel("Table: ");
        tableField = new JTextField(10);
        panelTable.add(tableLabel);
        panelTable.add(tableField);
        this.add(panelTable, BorderLayout.NORTH);

        // Panel for order list and summary
        JPanel panelOrderList = new JPanel(new BorderLayout());
        this.add(panelOrderList, BorderLayout.CENTER);

        // Panel for displaying order summary
        JPanel panelServedOrders = new JPanel(new BorderLayout());
        servedLabel = new JLabel("Order Summary:");
        panelServedOrders.add(servedLabel, BorderLayout.NORTH);
        servedTextArea = new JTextArea(10, 30);
        servedTextArea.setEditable(false);
        JScrollPane servedScrollPane = new JScrollPane(servedTextArea);
        panelServedOrders.add(servedScrollPane, BorderLayout.CENTER);
        this.add(panelServedOrders, BorderLayout.EAST);

        // Panel for buttons (Submit and Finish)
        JPanel panelFinishAndClear = new JPanel();
        panelFinishAndClear.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.add(panelFinishAndClear, BorderLayout.SOUTH);

        // Submit button
        btnSubmit = new JButton("Submit");
        panelFinishAndClear.add(btnSubmit);
        btnSubmit.addActionListener(this); // Register submit button action listener

        // Finish button
        btnFinish = new JButton("Finish");
        panelFinishAndClear.add(btnFinish);
        btnFinish.addActionListener(this); // Register finish button action listener

        // Initialize map to store current orders
        currentOrders = new HashMap<>();

        // Create a new Restaurant instance
        restaurant = new Restaurant();

        // Create buttons for each dish
        JPanel panelOrderButtons = new JPanel(new GridLayout(0, 4)); // Grid layout for dish buttons
        panelOrderButtons.setBorder(new TitledBorder("Order"));
        panelOrderList.add(panelOrderButtons, BorderLayout.CENTER);

        // Load dish images and create buttons for each dish
        ImageIcon[] dishIcons = new ImageIcon[8];
        for (int i = 0; i < 8; i++) {
            String imagePath = "images/images" + (i + 1) + ".jpg";
            ImageIcon icon = new ImageIcon(imagePath);
            dishIcons[i] = new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)); // Resize image

            JButton dishButton = new JButton(dishNames[i], dishIcons[i]); // Create dish button
            dishButton.setVerticalTextPosition(SwingConstants.BOTTOM); // Set vertical text alignment
            dishButton.setHorizontalTextPosition(SwingConstants.CENTER); // Set horizontal text alignment
            dishButton.addActionListener(this); // Register dish button action listener
            panelOrderButtons.add(dishButton); // Add dish button to panel
        }

        this.setVisible(true); // Make the GUI visible
    }

    // ActionListener implementation for handling button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFinish) {
            // Handle Finish button action
            dispose(); // Close the GUI
        } else if (e.getSource() == btnSubmit) {
            // Handle Submit button action
            String tableNumberStr = tableField.getText().trim();
            if (!tableNumberStr.isEmpty()) {
                String tableNumber = tableNumberStr;
                List<String> orders = currentOrders.getOrDefault(tableNumber, new ArrayList<>());
                restaurant.receiveOrder(tableNumber, orders); // Send order to the restaurant for processing
                currentOrders.remove(tableNumber); // Remove processed order from current orders
                updateOrderSummary(); // Update the order summary display
                tableField.setText(""); // Clear table number field after submitting order
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid table number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() instanceof JButton) {
            JButton sourceButton = (JButton) e.getSource();
            // Handle dish button actions
            String dish = sourceButton.getText(); // Get the dish name from the button text
            String tableNumber = tableField.getText().trim(); // Get the table number from the input field
            if (!tableNumber.isEmpty()) {
                List<String> orders = currentOrders.getOrDefault(tableNumber, new ArrayList<>());
                orders.add(dish); // Add the selected dish to the order list
                currentOrders.put(tableNumber, orders); // Update the current orders map
                updateOrderSummary(); // Update the order summary display
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a table number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to update the order summary display
    private void updateOrderSummary() {
        servedTextArea.setText(""); // Clear the order summary display

        // Iterate over current orders and display them in the order summary
        for (Map.Entry<String, List<String>> entry : currentOrders.entrySet()) {
            String tableNumber = entry.getKey();
            List<String> orders = entry.getValue();
            for (String order : orders) {
                servedTextArea.append("Table " + tableNumber + ": " + order + "\n"); // Append order details to the display
            }
        }
    }

    // Method to append text to the order summary display (not currently used)
    public void appendTextToServedArea(String message) {
        servedTextArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new); // Create and show the GUI on the event dispatch thread
    }
}

	