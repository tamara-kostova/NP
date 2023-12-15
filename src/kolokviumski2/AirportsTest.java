package kolokviumski2;

import java.util.*;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

// vashiot kod ovde

class Airports{
    Map <String, Airport> airports;
    List <Flight> flights;
    public Airports() {
        airports = new HashMap<>();
        flights = new ArrayList<>();
    }

    public void addAirport(String name, String country, String code, int passengers){
        airports.put(code, new Airport(name,country,code, passengers));
    }
    public void addFlights(String from, String to, int time, int duration){
        Airport airport = airports.get(from);
        airport.addFlight(from,to,time,duration);
        flights.add(new Flight(from,to,time,duration));
    }
    public void showFlightsFromAirport(String code){
        Airport airport = airports.get(code);
        System.out.println(airport.toString());
        int i=1;
        for (Flight flight : airport.getFlights()){
            System.out.printf("%d. %s\n",i,flight.toString());
            i++;
        }
    }
    public void showDirectFlightsFromTo(String from, String to){
        flights.stream().filter(flight -> flight.getFrom().equals(from)&&flight.getTo().equals(to)).forEach(System.out::println);
        if (flights.stream().noneMatch(flight -> flight.getFrom().equals(from)&&flight.getTo().equals(to)))
            System.out.printf("No flights from %s to %s\n",from, to);
    }
    public void showDirectFlightsTo(String to){
        flights.stream().filter(flight -> flight.getTo().equals(to)).sorted(Comparator.comparing(Flight::getTime).thenComparing(Flight::getFrom)).forEach(System.out::println);
    }
}
class Airport{
    private String name;
    private String country;
    private String code;
    private int passengers;
    private Set<Flight> flights;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        flights = new TreeSet<>(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime));
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void addFlight(String from, String to, int time, int duration) {
        flights.add(new Flight(from,to,time,duration));
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d",name,code,country,passengers);
    }
}
class Flight implements Comparable<Flight>{
    private String from;
    private String to;
    private int time;
    private int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        int days = (duration+time)/(60*24);
        duration%=(60*24);
        int hours = duration/60;
        int minutes = duration%60;
        int end = time+duration;
        end%=(60*24);
        return String.format("%s-%s %02d:%02d-%02d:%02d%s %dh%02dm",from,to,time/60,time%60,end/60, end%60,days > 0 ? " +1d" : "",hours,minutes);
    }

    @Override
    public int compareTo(Flight o) {
        return Comparator.comparing(Flight::getTime).thenComparing(Flight::getFrom).compare(this,o);
    }
}