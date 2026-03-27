import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// System State (Inventory + Booking History)
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> bookings;

    public SystemState(Map<String, Integer> inventory, List<Reservation> bookings) {
        this.inventory = inventory;
        this.bookings = bookings;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state to file
    public void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // Load state from file
    public SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Corrupted data. Starting with clean state.");
        }

        return null; // safe fallback
    }
}

// Booking System
class BookingSystem {
    Map<String, Integer> inventory = new HashMap<>();
    List<Reservation> bookings = new ArrayList<>();

    public BookingSystem() {
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 2);
    }

    public void createBooking(String id, String guest, String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available <= 0) {
            System.out.println("No rooms available for " + roomType);
            return;
        }

        inventory.put(roomType, available - 1);
        bookings.add(new Reservation(id, guest, roomType));

        System.out.println("Booking confirmed: " + id);
    }

    public void displayState() {
        System.out.println("\n--- Current State ---");
        System.out.println("Inventory: " + inventory);
        System.out.println("Bookings:");
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }

    public SystemState getState() {
        return new SystemState(inventory, bookings);
    }

    public void restoreState(SystemState state) {
        this.inventory = state.inventory;
        this.bookings = state.bookings;
    }
}

// Main Application
public class BookMyStay {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();
        BookingSystem system = new BookingSystem();

        // STEP 1: Load previous state (Recovery)
        SystemState loadedState = persistence.load();
        if (loadedState != null) {
            system.restoreState(loadedState);
        }

        system.displayState();

        // STEP 2: Perform operations
        system.createBooking("RES401", "Arun", "Standard");
        system.createBooking("RES402", "Priya", "Deluxe");

        system.displayState();

        // STEP 3: Save state (Persistence)
        persistence.save(system.getState());

        System.out.println("\n--- Restart the application to see recovery ---");
    }
}