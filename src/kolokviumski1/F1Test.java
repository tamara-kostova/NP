package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}

class F1Race {
    // vashiot kod ovde
    private ArrayList<Driver> drivers;
    public F1Race(){
        drivers = new ArrayList<>();
    }
    public void readResults(InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String [] parts = line.split(" ");
            int [] laps = new int [3];
            for (int i=1; i<=3; i++) {
                String [] t = parts[i].split(":");
                laps[i-1]=Integer.parseInt(t[0])*60000+Integer.parseInt(t[1])*1000+Integer.parseInt(t[2]);
            }
            Driver driver = new Driver(parts[0],laps);
            drivers.add(driver);
        }
        scanner.close();
    }
    public void printSorted(OutputStream outputStream){
        Collections.sort(drivers);
        PrintWriter printWriter = new PrintWriter(outputStream);
        int i=1;
        for (Driver driver : drivers){
            printWriter.println(String.format("%d. %s",i,driver));
            i++;
        }
        printWriter.close();
    }
}
class Driver implements Comparable<Driver>{
    private String Name;
    private int lap[];
    private int best;

    public Driver(String name, int[] lap) {
        Name = name;
        this.lap = lap;
        best = Arrays.stream(lap).min().orElse(0);
    }

    public int getBest() {
        return best;
    }

    @Override
    public int compareTo(Driver o) {
        if (getBest()>o.getBest())
            return 1;
        if (getBest()<o.getBest())
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%-10s%10s",Name,lapToString(best));
    }
    public static String lapToString(int time){
        int minutes = time/60000;
        time-=(time/60000)*60000;
        int seconds = time/1000;
        time-=(time/1000)*1000;
        return String.format("%d:%02d:%03d",minutes,seconds,time);
    }
}