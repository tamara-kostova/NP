package kolokviumski2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

// vashiot kod ovde
class EventCalendar{
    private int year;
    Map<Integer, Set<Event>> eventsbyDay;
    Map<Integer, Integer> eventsinMonth;
    public EventCalendar(int year) {
        this.year = year;
        eventsbyDay = new HashMap<>();
        eventsinMonth = new HashMap<>();
        for (int i=1; i<=12; i++)
            eventsinMonth.put(i,0);
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException{
        if (getYear(date)!=year)
            throw new WrongDateException(date);

        eventsbyDay.putIfAbsent(getDay(date),new TreeSet<>(Comparator.comparing(Event::getDate).thenComparing(Event::getName)));
        eventsbyDay.get(getDay(date)).add(new Event(name,location,date));
        eventsinMonth.replace(getMonth(date)+1,eventsinMonth.get(getMonth(date)+1)+1);

    }

    public void listEvents(Date date) {
        Set<Event> events = eventsbyDay.get(getDay(date));
        if(events==null)
            System.out.println("No events on this day!");
        else
            events.stream().forEach(System.out::println);
    }

    public void listByMonth() {
        for (int i=1; i<=12; i++)
            System.out.printf("%d : %d\n",i,eventsinMonth.get(i));
    }
    static int getDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
    static int getMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
    static int getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
class Event{
    private String name;
    private String location;
    private Date date;

    public Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd MMM, YYY HH:mm");
        return String.format("%s at %s, %s",df.format(date),location,name);
    }
}
class WrongDateException extends Exception{
    public WrongDateException(Date date) {
        super(String.format("Wrong date: %s",date));
    }
}