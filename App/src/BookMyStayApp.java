import java.util.*;

// Represents an Add-On Service
class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Manages Add-On Services for Reservations
class AddOnServiceManager {

    // Map: Reservation ID -> List of Services
    private Map<String, List<Service>> reservationServicesMap;

    public AddOnServiceManager() {
        reservationServicesMap = new HashMap<>();
    }

    // Add service to a reservation
    public void addService(String reservationId, Service service) {
        reservationServicesMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Get services for a reservation
    public List<Service> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total additional cost
    public double calculateTotalCost(String reservationId) {
        List<Service> services = reservationServicesMap.get(reservationId);
        if (services == null) return 0.0;

        double total = 0.0;
        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<Service> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Services for Reservation ID: " + reservationId);
        for (Service s : services) {
            System.out.println("- " + s);
        }
    }
}

// Demo class
public class BookMyStayApp {

    public static void main(String[] args) {

        AddOnServiceManager manager = new AddOnServiceManager();

        String reservationId = "RES123";

        // Guest selects services
        manager.addService(reservationId, new Service("Breakfast", 500));
        manager.addService(reservationId, new Service("Airport Pickup", 1200));
        manager.addService(reservationId, new Service("Spa Access", 2000));

        // Display selected services
        manager.displayServices(reservationId);

        // Calculate total cost
        double totalCost = manager.calculateTotalCost(reservationId);
        System.out.println("Total Add-On Cost: ₹" + totalCost);

        // Another reservation (to show one-to-many mapping)
        String reservationId2 = "RES456";
        manager.addService(reservationId2, new Service("Dinner", 800));

        System.out.println("\n--- Another Reservation ---");
        manager.displayServices(reservationId2);
        System.out.println("Total Add-On Cost: ₹" + manager.calculateTotalCost(reservationId2));
    }
}