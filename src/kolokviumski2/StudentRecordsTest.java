package kolokviumski2;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * January 2016 Exam problem 1
 */
public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here
class StudentRecords{
    Map <String, List<Student>> byDepartment;
    Map<String, Map<Integer, Integer>> gradesPerDepartment;

    public StudentRecords() {
        byDepartment = new TreeMap<>();
        gradesPerDepartment = new HashMap<>();
    }

    public int readRecords(InputStream in) {
        int n = 0;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        bufferedReader.lines().forEach(
                line -> {
                    String department = line.split(" ")[1];
                    byDepartment.putIfAbsent(department,new ArrayList<>());
                    Student newStudent = Student.createStudent(line);
                    byDepartment.get(department).add(newStudent);
                    gradesPerDepartment.putIfAbsent(department,new HashMap<>());
                    newStudent.grades.forEach(
                            grade -> {
                                gradesPerDepartment.get(department).putIfAbsent(grade, 0);
                                int oldgrade = gradesPerDepartment.get(department).get(grade);
                                gradesPerDepartment.get(department).replace(grade,oldgrade+1);
                            }
                    );
                }
        );
        return byDepartment.values().stream().mapToInt(l->l.size()).sum();
    }

    public void writeTable(PrintStream out) {
        PrintWriter printWriter = new PrintWriter(out);
        byDepartment.keySet().forEach(
                key -> {
                    printWriter.println(key);
                    byDepartment.get(key).stream().sorted(Comparator.comparing(Student::getAverage).reversed()
                                    .thenComparing(Student::getCode))
                            .forEach(printWriter::println);
                }
        );
        printWriter.flush();
    }

    public void writeDistribution(PrintStream out) {
        PrintWriter printWriter = new PrintWriter(out);
        gradesPerDepartment.keySet().stream()
                .sorted((l,r)->Integer.compare(gradesPerDepartment.get(r).get(10),gradesPerDepartment.get(l).get(10)))
                .forEach(
                key -> {
                    printWriter.println(key);
                    gradesPerDepartment.get(key).keySet().forEach(
                            key2 -> {
                                printWriter.printf("%2d | ",key2);
                                for (int i = 0; i < gradesPerDepartment.get(key).get(key2); i+=10)
                                    printWriter.print("*");
                                printWriter.printf("(%d)\n",gradesPerDepartment.get(key).get(key2));
                            }
                    );
                }
        );
        printWriter.flush();
    }
}
class Student{
    String code;
    String department;
    List<Integer> grades;

    public Student(String code, String department, List<Integer> grades) {
        this.code = code;
        this.department = department;
        this.grades = grades;
    }
    public static Student createStudent(String line){
        String [] parts = line.split(" ");
        List<Integer> grades = new ArrayList<>();
        IntStream.range(2,parts.length).forEach(i->grades.add(Integer.parseInt(parts[i])));
        return new Student(parts[0],parts[1],grades);
    }

    public String getCode() {
        return code;
    }

    public double getAverage(){
        return grades.stream().mapToInt(i->i).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f",code,getAverage());
    }
}