package kolokviumski2;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * I partial exam 2016
 */
public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}

// Vashiot kod ovde
class DailyTemperatures{
    List<Day> days;

    public DailyTemperatures() {
        days = new ArrayList<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        days = bufferedReader.lines().map(line->DayFactory.createDay(line)).collect(Collectors.toList());
    }

    public void writeDailyStats(OutputStream outputStream, char scale) {
        PrintWriter printWriter = new PrintWriter(outputStream);
        days.stream().sorted(Comparator.comparing(Day::getDay)).forEach(day-> System.out.println(day.summary(scale)));
        printWriter.flush();
    }
}
class Day{
    int day;
    List<Double> measurements;

    public Day(int day, List<Double> measurements) {
        this.day = day;
        this.measurements = measurements;
    }
    static double toFahrenheit(double celsius){
        return celsius* 9/5 + 32;
    }
    static double toCelsius(double fahrenheit){
        return (fahrenheit-32)* 5/9;
    }
    public String summary(char c){
        DoubleSummaryStatistics dss = measurements.stream().collect(Collectors.summarizingDouble(i->i));
        double min = dss.getMin();
        double max = dss.getMax();
        double average = dss.getAverage();
        if (c=='F'){
            min = toFahrenheit(min);
            max = toFahrenheit(max);
            average = toFahrenheit(average);
        }
        return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",day,dss.getCount(),min, c, max, c,average, c);
    }

    public int getDay() {
        return day;
    }
}
class DayFactory{
    public static Day createDay(String line){
        String [] parts = line.split(" ");
        int day = Integer.parseInt(parts[0]);
        List<Double> measurements = new ArrayList<>();
        IntStream.range(1,parts.length).forEach(i-> {
            if (parts[i].endsWith("F"))
                measurements.add(Day.toCelsius(Double.parseDouble(parts[i].substring(0, parts[i].length() - 1))));
            else
                measurements.add(Double.parseDouble(parts[i].substring(0, parts[i].length() - 1)));
        });
        return new Day(day,measurements);
    }
}