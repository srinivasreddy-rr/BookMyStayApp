import java.util.*;

// Booking Request Model
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// Thread-safe Inventory Manager
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
    }

    // Critical Section (Thread-safe)
    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            System.out.println(Thread.currentThread().getName()
                    + " allocating " + roomType);

            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(roomType, available - 1);
            return true;
        } else {
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// Worker Thread (Concurrent Booking Processor)
class BookingProcessor extends Thread {
    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // Fetch request safely
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) break;

            boolean success = inventory.allocateRoom(request.roomType);

            if (success) {
                System.out.println(getName() + " SUCCESS: "
                        + request.guestName + " booked " + request.roomType);
            } else {
                System.out.println(getName() + " FAILED: "
                        + request.guestName + " - No " + request.roomType + " available");
            }
        }
    }
}

// Main Application
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests (Concurrent input)
        queue.addRequest(new BookingRequest("Arun", "Standard"));
        queue.addRequest(new BookingRequest("Priya", "Standard"));
        queue.addRequest(new BookingRequest("Karthik", "Standard")); // extra request
        queue.addRequest(new BookingRequest("Divya", "Deluxe"));
        queue.addRequest(new BookingRequest("Rahul", "Deluxe")); // extra request

        // Create multiple threads (simulate concurrent users)
        Thread t1 = new BookingProcessor(queue, inventory, "Thread-1");
        Thread t2 = new BookingProcessor(queue, inventory, "Thread-2");
        Thread t3 = new BookingProcessor(queue, inventory, "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state (consistent)
        System.out.println("\n=== FINAL STATE ===");
        inventory.displayInventory();
    }
}