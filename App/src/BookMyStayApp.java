package src; //// Abstract Class
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

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq ft");
        System.out.println("Price: $" + price + " per night");
    }
}

// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}

// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}

// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 500, 300);
    }
}

// Main Class
public class BookMyStayApp {
    public static void main(String[] args) {

//        // Polymorphism
        Room single = new SingleRoom();
        Room doub = new DoubleRoom();
        Room suite = new SuiteRoom();
//
        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;
//
        System.out.println("=== Room Details ===\n");
//
        single.displayDetails();
        System.out.println("Available: " + singleAvailable);
        System.out.println("----------------------");
//
        doub.displayDetails();
        System.out.println("Available: " + doubleAvailable);
        System.out.println("----------------------");
//
       suite.displayDetails();
        System.out.println("Available: " + suiteAvailable);
    }
}