package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
class AdvancedProgrammingCourse{
    Map<String, Student> studentsById;
    Map<Integer, Integer> gradeDistribution;

    public AdvancedProgrammingCourse() {
        studentsById = new HashMap<>();
        gradeDistribution = new HashMap<>();
        IntStream.range(5,11).forEach(i->gradeDistribution.put(i,0));
    }

    public void addStudent(Student student) {
        studentsById.put(student.getId(),student);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        try {
            studentsById.get(idNumber).updateActivity(activity,points);
        } catch (InvalidActivityException e) {
        }
    }

    public List<Student> getFirstNStudents(int n) {
        return studentsById.values().stream().sorted(Comparator.comparing(Student::totalPoints).reversed()).limit(n).collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        studentsById.values().forEach(student -> gradeDistribution.replace(student.getGrade(),gradeDistribution.get(student.getGrade())+1));
        return gradeDistribution;
    }
    public long numPassed(){
        return studentsById.values().stream().filter(student -> student.isPassed()).count();
    }
    public float minPoints(){
        return (float) studentsById.values().stream().filter(Student::isPassed).mapToDouble(Student::totalPoints).min().orElse(0.0);
    }
    public float maxPoints(){
        return (float) studentsById.values().stream().filter(Student::isPassed).mapToDouble(Student::totalPoints).max().orElse(0.0);
    }
    public float averagePoints(){
        return (float) studentsById.values().stream().filter(Student::isPassed).mapToDouble(Student::totalPoints).average().orElse(0.0);
    }
    public void printStatistics() {
        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f",numPassed(),minPoints(),averagePoints(),maxPoints());
    }
}
class Student{
    String id;
    String name;
    int firstPoints;
    int secondPoints;
    int labPoints;
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void updateActivity(String activity, int points) throws InvalidActivityException {
        if (activity.equals("midterm1"))
            firstPoints+=points;
        else if (activity.equals("midterm2"))
            secondPoints+=points;
        else if (activity.equals("labs"))
            labPoints+=points;
        else
            throw new InvalidActivityException();
    }
    public float totalPoints(){
        return (float) (0.45*(firstPoints+secondPoints)+labPoints);
    }
    public int getGrade(){
        return Math.max((int) Math.ceil(totalPoints()/10), 5);
    }
    public boolean isPassed(){
        return totalPoints()>50;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                id,name,firstPoints,secondPoints,labPoints,totalPoints(),getGrade());
    }
}
class InvalidActivityException extends Exception{
    public InvalidActivityException() {
    }
}