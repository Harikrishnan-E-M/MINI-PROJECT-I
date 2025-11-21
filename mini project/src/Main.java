import java.util.*;

class Vehicle {
    String vehicleNumber;
    String type;

    Vehicle(String number, String type) {
        this.vehicleNumber = number;
        this.type = type;
    }

    @Override
    public String toString() {
        return vehicleNumber + " (" + type + ")";
    }
}

class SignalNode {
    String direction;
    Queue<Vehicle> queue;
    SignalNode next;

    SignalNode(String direction) {
        this.direction = direction;
        this.queue = new LinkedList<>();
        this.next = null;
    }

    void addVehicle(Vehicle v) {
        queue.add(v);
    }

    void clearVehicles() {
        queue.clear();
    }

    void showQueue() {
        System.out.print(direction + ": ");
        if (queue.isEmpty()) {
            System.out.println("No vehicles");
        } else {
            for (Vehicle v : queue) {
                System.out.print(v.vehicleNumber + ", ");
            }
            System.out.println();
        }
    }
}

class TrafficController {
    SignalNode head;

    TrafficController() {
        SignalNode north = new SignalNode("North");
        SignalNode east = new SignalNode("East");
        SignalNode south = new SignalNode("South");
        SignalNode west = new SignalNode("West");

        north.next = east;
        east.next = south;
        south.next = west;
        west.next = north;

        head = north;
    }

    SignalNode findDirection(String dir) {
        SignalNode temp = head;
        do {
            if (temp.direction.equalsIgnoreCase(dir)) {
                return temp;
            }
            temp = temp.next;
        } while (temp != head);
        return null;
    }

    void addVehicle(String dir, Vehicle v) {
        SignalNode node = findDirection(dir);
        if (node != null) {
            node.addVehicle(v);
        } else {
            System.out.println("Invalid direction.");
        }
    }

    void showQueues() {
        System.out.println("\n Vehicles by Direction:");
        SignalNode temp = head;
        do {
            temp.showQueue();
            temp = temp.next;
        } while (temp != head);
    }

    void deleteVehiclesInDirection(String dir) {
        SignalNode node = findDirection(dir);
        if (node != null) {
            node.clearVehicles();
            System.out.println(" All vehicles in " + dir + " direction deleted.");
        } else {
            System.out.println("Invalid direction.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TrafficController controller = new TrafficController();

        while (true) {
            System.out.println("\n=== Traffic Signal Simulation ===");
            System.out.println("1. Add Vehicle");
            System.out.println("2. Show Queues");
            System.out.println("3. Delete Vehicles in Direction");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("Enter direction (North/East/South/West): ");
                    String dir = sc.nextLine();
                    System.out.print("Enter vehicle number: ");
                    String num = sc.nextLine();
                    System.out.print("Enter vehicle type: ");
                    String type = sc.nextLine();
                    controller.addVehicle(dir, new Vehicle(num, type));
                    break;
                case 2:
                    controller.showQueues();
                    break;
                case 3:
                    System.out.print("Enter direction to delete vehicles (North/East/South/West): ");
                    String delDir = sc.nextLine();
                    controller.deleteVehiclesInDirection(delDir);
                    break;
                case 4:
                    System.out.println("Simulation Ended.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}