import java.util.HashMap;
import java.util.Map;

// Abstract Room Class (Domain Model)
abstract class Room {
    private String type;
    private int beds;
    private double size;
    private double price;

    public Room(String type, int beds, double size, double price) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq ft");
        System.out.println("Price: $" + price + " per night");
    }
}

// Concrete Room Types
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 500, 300);
    }
}

// Centralized Inventory Class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    // Constructor initializes inventory (Single Source of Truth)
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Get availability (O(1))
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability (controlled update)
    public void updateAvailability(String roomType, int change) {
        int current = inventory.getOrDefault(roomType, 0);
        int updated = current + change;

        if (updated < 0) {
            System.out.println("Cannot reduce below 0 for " + roomType);
            return;
        }

        inventory.put(roomType, updated);
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("\n=== Current Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

// Main Application
public class BookMyStayApp {
    public static void main(String[] args) {

        // Create Room Objects (Polymorphism)
        Room single = new SingleRoom();
        Room doub = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();

        // Display Room Details with Availability
        System.out.println("=== Room Details ===\n");

        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getType()));
        System.out.println("----------------------");

        doub.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(doub.getType()));
        System.out.println("----------------------");

        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getType()));
        System.out.println("----------------------");

        // Simulate booking
        System.out.println("\nBooking 1 Single Room...");
        inventory.updateAvailability("Single Room", -1);

        // Simulate cancellation
        System.out.println("Cancelling 1 Double Room...");
        inventory.updateAvailability("Double Room", +1);

        // Show updated inventory
        inventory.displayInventory();
    }
}