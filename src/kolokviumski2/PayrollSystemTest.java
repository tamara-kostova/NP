package kolokviumski2;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i=5;i<=10;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: "+ level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}
class PayrollSystem{
    Map<String,Double> hourlyRateByLevel;
    Map<String,Double> ticketRateByLevel;
    List<Employee> employees;
    public PayrollSystem(Map<String,Double> hourlyRateByLevel, Map<String,Double> ticketRateByLevel){
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        employees = new ArrayList<>();
    }
    public void readEmployees(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines().forEach(
                line -> {
                    String level = line.split(";")[2];
                    Employee newemp = null;
                    if (line.split(";")[0].equals("F")) {
                        newemp = FreelanceEmployee.createEmployee(line);
                        newemp.setRate(ticketRateByLevel.get(level));
                        employees.add(newemp);
                    }
                    else {
                        newemp = HourlyEmployee.createEmployee(line);
                        newemp.setRate(hourlyRateByLevel.get(level));
                        employees.add(newemp);
                    }
                }
        );
    }

    public Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels) {
        Map<String, Set<Employee>> result = employees.stream().collect(Collectors.groupingBy(
                Employee::getLevel,
                (Supplier<TreeMap<String,Set<Employee>>>) TreeMap::new,
                Collectors.toCollection(TreeSet::new)
        ));
        Set <String> keys = new HashSet<>(result.keySet());
        keys.stream().filter(k->!levels.contains(k)).forEach(result::remove);
        return result;
    }
}
abstract class Employee implements Comparable<Employee>{
    String id;
    String level;
    double rate;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }
    public void setRate(double rate) {
        this.rate = rate;
    }
    abstract double salary();

    public String getLevel() {
        return level;
    }

    @Override
    public int compareTo(Employee o) {
        return Comparator.comparing(Employee::salary).thenComparing(Employee::getLevel).reversed().compare(this,o);
    }
}
class HourlyEmployee extends Employee{
    double hours;
    double regular;
    double plus;

    public HourlyEmployee(String id, String level, double hours) {
        super(id, level);
        this.hours = hours;
        plus = (hours - 40) > 0 ? (hours - 40) : 0;
        regular = hours - plus;
    }
    static HourlyEmployee createEmployee(String line){
        String [] parts = line.split(";");
        return new HourlyEmployee(parts[1],parts[2],Double.parseDouble(parts[3]));
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                id,level,salary(),regular,plus);
    }

    @Override
    double salary() {
        return rate*(regular+1.5*plus);
    }
}
class FreelanceEmployee extends Employee{
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints) {
        super(id, level);
        this.ticketPoints = ticketPoints;
    }
    static FreelanceEmployee createEmployee(String line){
        String [] parts = line.split(";");
        List <Integer> ticketPoints = new ArrayList<>();
        IntStream.range(3,parts.length).forEach(
                i->ticketPoints.add(Integer.parseInt(parts[i]))
        );
        return new FreelanceEmployee(parts[1],parts[2],ticketPoints);
    }

    @Override
    double salary() {
        return ticketPoints.stream().mapToInt(i->i).sum()*rate;
    }
    int ticketPoints(){
        return ticketPoints.stream().mapToInt(i->i).sum();
    }
    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                id,level,salary(),ticketPoints.size(),ticketPoints());
    }
}
