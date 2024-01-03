package kolokviumski2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

interface ILocation{
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
class StopCoronaApp{
    Map<String, User> usersById;
    Map<User, Map<User, Integer>> directContacts;

    public StopCoronaApp() {
        usersById = new HashMap<>();
        directContacts = new TreeMap<>(Comparator.comparing(User::getTimeInfected).thenComparing(User::getId));
    }

    public void addUser(String name, String id) throws UserAlreadyExistException{
        if (usersById.containsKey(id))
            throw new UserAlreadyExistException(id);
        usersById.put(id,new User(name,id));
    }
    public void addLocations (String id, List<ILocation> iLocations){
        usersById.get(id).addLocations(iLocations);
    }
    public void detectNewCase (String id, LocalDateTime timestamp){
        User infected = usersById.get(id);
        infected.setTimeInfected(timestamp);
        infected.setInfected(true);
    }
    public Map<User, Integer> getDirectContacts (User u){
        return directContacts.get(u).entrySet().stream().filter(element->element.getValue()!=0).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }
    public Collection<User> getIndirectContacts (User u){
        Set <User> indirect = new TreeSet<>(Comparator.comparing(User::getName).thenComparing(User::getId));
        Map<User,Integer> direct = getDirectContacts(u);
        direct.keySet().stream().flatMap(user -> getDirectContacts(user).keySet().stream()
                .filter(user1 -> !indirect.contains(user1)&&!direct.containsKey(user1)&&!user1.equals(u))).forEach(indirect::add);
        return indirect;
    }
    public void createReport (){
        usersById.values().forEach(
                user -> {
                    usersById.values().forEach(
                            user1 -> {
                                if(user1!=user){
                                    directContacts.putIfAbsent(user,new TreeMap<>(Comparator.comparing(User::getTimeInfected).thenComparing(User::getId)));
                                    directContacts.get(user).putIfAbsent(user1,0);
                                    directContacts.get(user).replace(user1,directContacts.get(user).get(user1)+LocationUtils.dangerContacts(user1,user));
                                }
                            }
                    );
                }
        );
        List<Integer> direct = new ArrayList<>();
        List<Integer> indirect = new ArrayList<>();
        directContacts.keySet().forEach(
                key->{
                    if (key.infected){
                        System.out.println(key.userToString());
                        System.out.println("Direct contacts:");
                        Map<User, Integer> directcontacts = getDirectContacts(key);
                        directcontacts.entrySet().stream().sorted(comparingByValue(Comparator.reverseOrder()))
                                .forEach(entry->System.out.println(String.format("%s %s",entry.getKey().userHidden(),entry.getValue())));
                        int count = directcontacts.values().stream().mapToInt(i->i).sum();
                        System.out.println(String.format("Count of direct contacts: %d", count));
                        direct.add(count);
                        Collection<User> indirectcontacts = getIndirectContacts(key);
                        System.out.println("Indirect contacts:");
                        indirectcontacts.forEach(user -> System.out.println(user.userHidden()));
                        System.out.println(String.format("Count of indirect contacts: %d", indirectcontacts.size()));
                        indirect.add(indirectcontacts.size());
                    }
                }
        );
        System.out.printf("Average direct contacts: %.4f\n", direct.stream().mapToInt(i -> i).average().getAsDouble());
        System.out.printf("Average indirect contacts: %.4f", indirect.stream().mapToInt(i -> i).average().getAsDouble());
    }
}
class User{
    private String name;
    private String id;
    List<ILocation> locations;
    boolean infected;
    LocalDateTime timeInfected;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        locations = new ArrayList<>();
        infected = false;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimeInfected() {
        return timeInfected != null ? timeInfected : LocalDateTime.MAX;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public void setTimeInfected(LocalDateTime timeInfected) {
        this.timeInfected = timeInfected;
    }

    public void addLocations(List<ILocation> iLocations) {
        locations.addAll(iLocations);
    }
    public String userToString(){
        return String.format("%s %s %s",name,id,timeInfected);
    }
    public String userHidden(){
        return String.format("%s %s***",name,id.substring(0,4));
    }
}
class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String id) {
        super(String.format("User with id %s already exists", id));
    }
}
class LocationUtils{
    public static double distance(ILocation l1, ILocation l2){
        return Math.sqrt(Math.pow(l1.getLatitude()-l2.getLatitude(),2)+Math.pow(l1.getLongitude()-l2.getLongitude(),2));
    }
    public static double time(ILocation l1, ILocation l2){
        return Math.abs(Duration.between(l1.getTimestamp(),l2.getTimestamp()).getSeconds());
    }
    public static boolean isDanger(ILocation l1, ILocation l2){
        return distance(l1,l2)<2.0 && time(l1,l2)<=300;
    }
    public static int dangerContacts(User u1, User u2){
        int counter = 0;
        for(ILocation location : u1.locations){
            for (ILocation location1 : u2.locations){
                if(isDanger(location1,location))
                    counter++;
            }
        }
        return counter;
    }
}