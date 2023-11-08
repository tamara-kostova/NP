package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}
class TimeTable {
    List<Time> times;
    public TimeTable() {
        times = new ArrayList<>();
    }
    public void readTimes(InputStream inputStream) throws UnsupportedFormatException, InvalidTimeException {
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            for (String p : parts) {
                times.add(new Time(p));
            }
        }
        scanner.close();
    }
    public void writeTimes(OutputStream outputStream, TimeFormat format){
        PrintWriter printWriter = new PrintWriter(outputStream);
        Collections.sort(times);
        if (format==TimeFormat.FORMAT_24){
            for (Time t : times)
                printWriter.println(t);
        }
        else{
            for (Time t : times)
                printWriter.println(t.toAMPMFormat());
        }
        printWriter.flush();
    }
}
class Time implements Comparable<Time>{
    private int hour;
    private int minutes;

    public Time(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    public Time(String line) throws InvalidTimeException, UnsupportedFormatException{
        String [] parts = line.split("\\.");
        if (parts.length==1)
            parts = line.split(":");
        if (parts.length==1)
            throw new UnsupportedFormatException(line);
        hour = Integer.parseInt(parts[0]);
        minutes = Integer.parseInt(parts[1]);
        if (hour<0 || hour>23 || minutes<0 || minutes>59)
            throw new InvalidTimeException(line);
    }

    public String toAMPMFormat(){
        int h = hour;
        String suffix = "AM";
        if (hour==0)
            h=hour+12;
        else if (hour==12){
            suffix = "PM";
        }
        else if (hour>=13&&hour<=23){
            h-=12;
            suffix = "PM";
        }
        return String.format("%2d:%02d %s",h,minutes,suffix);
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d",hour,minutes);
    }

    @Override
    public int compareTo(Time o) {
        if (hour == o.hour)
            return minutes-o.minutes;
        return hour-o.hour;
    }
}
class UnsupportedFormatException extends Exception{
    public UnsupportedFormatException(String line) {
        super(line);
    }
}
class InvalidTimeException extends Exception{
    public InvalidTimeException(String line) {
        super(line);
    }
}