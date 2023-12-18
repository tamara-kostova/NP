package kolokviumski2;

import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
class DeliveryApp {
    String appName;
    Map<String, User> users;
    Map<String, DeliveryPerson> deliverypeople;
    Map<String, Restaurant> restaurants;

    public DeliveryApp(String appName) {
        this.appName = appName;
        users = new HashMap<>();
        deliverypeople = new HashMap<>();
        restaurants = new HashMap<>();
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id,name));
    }

    public void registerDeliveryPerson(String id, String name, Location location) {
        deliverypeople.put(id,new DeliveryPerson(id,name,location));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id,new Restaurant(id,name,location));
    }

    public void addAddress(String id, String name, Location location) {
        users.get(id).addAddress(name,location);
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Address address = user.addresses.get(userAddressName);
        Restaurant restaurant = restaurants.get(restaurantId);
        user.orderFood(cost);
        restaurant.orderFood(cost);
        DeliveryPerson deliveryPerson = deliverypeople.values().stream().
                min((l,r)->l.compareDistance(r,restaurant.getLocation())).get();
        int distance = deliveryPerson.getLocation().distance(restaurant.location);
        deliveryPerson.orderFood(distance,address.getLocation());
    }

    public void printUsers() {
        users.values().stream().sorted(Comparator.comparing(User::totalSum).thenComparing(User::getId).reversed()).forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream().sorted(Comparator.comparing(Restaurant::average).thenComparing(Restaurant::getId).reversed()).forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliverypeople.values().stream().sorted(Comparator.comparing(DeliveryPerson::totalSum).thenComparing(DeliveryPerson::getId).reversed()).forEach(System.out::println);
    }
}
class User{
    String id;
    String name;
    Map<String, Address> addresses;
    List<Float> orders;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        addresses = new HashMap<>();
        orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addAddress(String name, Location location) {
        addresses.put(name, new Address(name,location));
    }
    float totalSum(){
        return (float) orders.stream().mapToDouble(o->o).sum();
    }
    float average(){
        return (float) orders.stream().mapToDouble(o->o).average().orElse(0.0);
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",id,name,orders.size(),totalSum(),average());
    }

    public void orderFood(float cost) {
        orders.add(cost);
    }
}
class Address{
    String name;
    Location location;

    public Address(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
class DeliveryPerson{
    String id;
    String name;
    Location location;
    List<Float> orders;

    public DeliveryPerson(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    float totalSum(){
        return (float) orders.stream().mapToDouble(o->o).sum();
    }
    float average(){
        return (float) orders.stream().mapToDouble(o->o).average().orElse(0.0);
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",id,name,orders.size(),totalSum(),average());
    }

    public int compareDistance(DeliveryPerson other, Location restaurantLocation) {
        int currentDistance = location.distance(restaurantLocation);
        int otherDistance = other.location.distance(restaurantLocation);
        if (currentDistance==otherDistance)
            return Integer.compare(this.orders.size(),other.orders.size());
        return currentDistance-otherDistance;
    }

    public void orderFood(int distance, Location location) {
        orders.add((float) (90+(distance/10)*10));
        this.location = location;
    }
}
class Restaurant{
    String id;
    String name;
    Location location;
    List<Float> orders;
    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    float totalSum(){
        return (float) orders.stream().mapToDouble(o->o).sum();
    }
    float average(){
        return (float) orders.stream().mapToDouble(o->o).average().orElse(0.0);
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",id,name,orders.size(),totalSum(),average());
    }

    public void orderFood(float cost) {
        orders.add(cost);
    }

}