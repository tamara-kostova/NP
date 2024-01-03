package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}
interface Employee{
    double salary();
    String getLevel();
    double getOvertime();
    int getTicketsCount();
    double getBonus();

    void updateBonus(double bonus);
}
abstract class EmployeeSimple implements Employee{
    String id;
    String level;
    double rate;
    double totalBonus;
    public EmployeeSimple(String id, String level, double rate) {
        this.id = id;
        this.level = level;
        this.rate = rate;
        totalBonus = 0d;
    }

    @Override
    public String getLevel() {
        return level;
    }
    @Override
    public double getBonus() {
        return totalBonus;
    }
    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, salary() + totalBonus);
    }

    @Override
    public void updateBonus(double bonus) {
        this.totalBonus+=bonus;
    }
}
class HourlyEmployee extends EmployeeSimple{
    double hours;
    double overtime;
    double regular;

    public HourlyEmployee(String id, String level, double rate, double hours) {
        super(id, level, rate);
        this.hours = hours;
        overtime = hours>40? hours-40 : 0;
        regular = hours - overtime;
    }

    @Override
    public double salary() {
        return rate*(regular+1.5*overtime);
    }

    @Override
    public double getOvertime() {
        return overtime;
    }

    @Override
    public int getTicketsCount() {
        return -1;
    }
    @Override
    public String toString() {
        return super.toString() + String.format(" Regular hours: %.2f Overtime hours: %.2f", regular, overtime);
    }
}
class FreelanceEmployee extends EmployeeSimple{
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, double rate, List<Integer> ticketPoints) {
        super(id, level, rate);
        this.ticketPoints = ticketPoints;
    }

    @Override
    public double salary() {
        return ticketPoints.stream().mapToInt(i->i).sum()*rate;
    }

    @Override
    public double getOvertime() {
        return -1;
    }

    @Override
    public int getTicketsCount() {
        return ticketPoints.size();
    }
    @Override
    public String toString() {
        return super.toString() + String.format(" Tickets count: %d Tickets points: %d",
                ticketPoints.size(),
                ticketPoints.stream().mapToInt(i -> i).sum()
        );
    }
}
abstract class BonusDecorator implements Employee{
    Employee employee;

    public BonusDecorator(Employee employee) {
        this.employee = employee;
    }

    @Override
    public double salary() {
        return employee.salary();
    }

    @Override
    public String getLevel() {
        return employee.getLevel();
    }

    @Override
    public double getOvertime() {
        return employee.getOvertime();
    }

    @Override
    public int getTicketsCount() {
        return employee.getTicketsCount();
    }

    @Override
    public double getBonus() {
        return employee.getBonus();
    }
    @Override
    public String toString() {
        return employee.toString() + String.format(" Bonus: %.2f", getBonus());
    }
}
class FixedBonus extends BonusDecorator{
    double fixedBonus;

    public FixedBonus(Employee employee, double fixedBonus) {
        super(employee);
        this.fixedBonus = fixedBonus;
        employee.updateBonus(fixedBonus);
    }

    @Override
    public double salary() {
        return employee.salary()+fixedBonus;
    }

    @Override
    public double getBonus() {
        return fixedBonus;
    }
    @Override
    public void updateBonus(double bonus) {
        this.employee.updateBonus(bonus);
    }
}
class PercentageBonus extends BonusDecorator{
    double percent;

    public PercentageBonus(Employee employee, double percent) {
        super(employee);
        this.percent = percent;
        employee.updateBonus(employee.salary()*percent/100.0);
    }

    @Override
    public double salary() {
        return employee.salary()+percent* employee.salary()/100.0;
    }

    @Override
    public double getBonus() {
        return employee.salary()*percent/100.0;
    }

    @Override
    public void updateBonus(double bonus) {
        this.employee.updateBonus(bonus);
    }
}
class EmployeeFactory{
    public static Employee createEmployee(String line, Map<String, Double> hourlyRate, Map<String, Double> ticketRate) throws BonusNotAllowedException{
        String [] parts = line.split(" ");
        Employee e = EmployeeFactory.createSimpleEmployee(parts[0],hourlyRate,ticketRate);
        if (parts.length>1){
            if (parts[1].contains("%")){
                double percentage = Double.parseDouble(parts[1].substring(0,parts[1].length()-1));
                if (percentage>20)
                    throw new BonusNotAllowedException(parts[1]);
                e = new PercentageBonus(e, percentage);
            }
            else {
                double bonus = Double.parseDouble(parts[1]);
                if (bonus>1000)
                    throw new BonusNotAllowedException(parts[1]+"$");
                e = new FixedBonus(e, bonus);
            }
        }
        return e;
    }
    public static Employee createSimpleEmployee(String line, Map<String, Double> hourlyRate, Map<String, Double> ticketRate) throws BonusNotAllowedException {
        String [] parts = line.split(";");
        if(parts[0].equals("H"))
            return new HourlyEmployee(parts[1],parts[2],hourlyRate.get(parts[2]),Double.parseDouble(parts[3]));
        else {
            List<Integer> points = new ArrayList<>();
            IntStream.range(3,parts.length).forEach(i->points.add(Integer.parseInt(parts[i])));
            return new FreelanceEmployee(parts[1], parts[2], ticketRate.get(parts[2]),points);
        }
    }
}
class PayrollSystem {
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;
    List<Employee> employees;
    public PayrollSystem(Map<String,Double> hourlyRateByLevel, Map<String,Double> ticketRateByLevel){
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        employees = new ArrayList<>();
    }
    public Employee createEmployee (String line) throws BonusNotAllowedException {
        Employee toadd = EmployeeFactory.createEmployee(line,hourlyRateByLevel,ticketRateByLevel);
        employees.add(toadd);
        return toadd;
    }
    public Map<String, Double> getOvertimeSalaryForLevels (){
        Map<String, Double> res = employees.stream().collect(Collectors.groupingBy(Employee::getLevel,Collectors.summingDouble(Employee::getOvertime)));
        List<String> invalid = res.keySet().stream().filter(key->res.get(key)==-1).collect(Collectors.toList());
        invalid.forEach(key->res.remove(key));
        return res;
    }
    public void printStatisticsForOvertimeSalary (){
        DoubleSummaryStatistics dss = employees.stream().filter(e->e.getOvertime()!=-1).mapToDouble(Employee::getOvertime).summaryStatistics();
        System.out.printf("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f", dss.getMin(), dss.getAverage(), dss.getMax(), dss.getSum());
    }
    public Map<String, Integer> ticketsDoneByLevel(){
        return employees.stream().filter(e->e.getTicketsCount()!=-1).collect(Collectors.groupingBy(Employee::getLevel,Collectors.summingInt(Employee::getTicketsCount)));
    }
    public Collection<Employee> getFirstNEmployeesByBonus (int n){
        return employees.stream().sorted(Comparator.comparing(Employee::getBonus).reversed()).limit(n).collect(Collectors.toList());
    }
}
class BonusNotAllowedException extends Exception{
    public BonusNotAllowedException(String bonus) {
        super(String.format("Bonus of %s is not allowed",bonus));
    }
}