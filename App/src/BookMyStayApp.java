import java.util.*;

// Reservation (Booking Request)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Booking Queue (FIFO)
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // dequeue
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Inventory Service
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void reduceAvailability(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, current - 1);
    }

    public void displayInventory() {
        System.out.println("\n=== Inventory Status ===");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }
}

// Booking Service (Allocation Logic)
class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> roomAllocations = new HashMap<>();
    private int idCounter = 1;

    public void processBookings(BookingQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {
            Reservation request = queue.getNextRequest();

            String roomType = request.getRoomType();
            String guest = request.getGuestName();

            System.out.println("\nProcessing request for " + guest + " (" + roomType + ")");

            // Check availability
            if (inventory.getAvailability(roomType) <= 0) {
                System.out.println("❌ No rooms available for " + roomType);
                continue;
            }

            // Generate unique room ID
            String roomId;
            do {
                roomId = roomType.replace(" ", "").toUpperCase() + "-" + idCounter++;
            } while (allocatedRoomIds.contains(roomId));

            // Store ID in Set (prevents duplicates)
            allocatedRoomIds.add(roomId);

            // Map room type → allocated IDs
            roomAllocations.putIfAbsent(roomType, new HashSet<>());
            roomAllocations.get(roomType).add(roomId);

            // Reduce inventory (atomic step)
            inventory.reduceAvailability(roomType);

            // Confirm booking
            System.out.println("✅ Booking Confirmed!");
            System.out.println("Guest: " + guest);
            System.out.println("Room Type: " + roomType);
            System.out.println("Allocated Room ID: " + roomId);
        }
    }

    // Display all allocations
    public void displayAllocations() {
        System.out.println("\n=== Room Allocations ===");
        for (Map.Entry<String, Set<String>> entry : roomAllocations.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Main Class
public class BookMyStayApp {
    public static void main(String[] args) {

        // Initialize queue
        BookingQueue queue = new BookingQueue();

        // Add booking requests (FIFO)
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room")); // will fail
        queue.addRequest(new Reservation("David", "Suite Room"));

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Booking service
        BookingService bookingService = new BookingService();

        // Process bookings
        bookingService.processBookings(queue, inventory);

        // Show results
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}