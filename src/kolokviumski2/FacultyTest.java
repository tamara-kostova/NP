package kolokviumski2;

import java.util.*;
import java.util.stream.IntStream;


abstract class Student{
    String id;
    Map<Integer, List<Integer>> gradesBySemester;
    Set<String> courses;
    public Student(String id) {
        this.id = id;
        gradesBySemester = new TreeMap<>();
        courses = new TreeSet<>();
    }

    public String getId() {
        return id;
    }

    public void addGrade(int term, String courseName, int grade) throws OperationNotAllowedException{
        if (!gradesBySemester.containsKey(term))
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, id));
        if (gradesBySemester.get(term).size()==3)
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", id, term));
        gradesBySemester.get(term).add(grade);
        courses.add(courseName);
    }
    public double average(){
        return gradesBySemester.values().stream().flatMap(Collection::stream).mapToInt(i->i).average().orElse(5.0);
    }

    public abstract boolean isGraduated();

    public abstract String getLog();

    public String getDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student: %s\n",id));
        gradesBySemester.keySet().forEach(semester->sb.append(getSemesterLog(semester)).append("\n"));
        sb.append(String.format("Average grade: %.2f\nCourses attended: %s",average(),String.join(",",courses)));
        return sb.toString();
    }

    private String getSemesterLog(int semester) {
        return String.format("Term %d\nCourses: %d\nAverage grade for term: %.2f",semester,gradesBySemester.get(semester).size(),averageGradeSemester(semester));
    }

    private double averageGradeSemester(int semester) {
        return gradesBySemester.get(semester).stream().mapToInt(i->i).average().orElse(0.0);
    }
    public int passedCourses(){
        return gradesBySemester.values().stream().mapToInt(l->l.size()).sum();
    }

    public String getShortReport() {
        return String.format("Student: %s Courses passed: %s Average grade: %.2f",id,passedCourses(),average());
    }
}
class ThreeYearStudent extends Student{
    public ThreeYearStudent(String id) {
        super(id);
        IntStream.range(1,7).forEach(i->gradesBySemester.put(i,new ArrayList<>()));
    }

    @Override
    public boolean isGraduated() {
        return gradesBySemester.values().stream().mapToInt(c->c.size()).sum()==18;
    }

    @Override
    public String getLog() {
        return String.format("Student with ID %s graduated with average grade %.2f in 3 years.", id, average());
    }
}
class FourYearStudent extends Student{
    public FourYearStudent(String id) {
        super(id);
        IntStream.range(1,9).forEach(i->gradesBySemester.put(i,new ArrayList<>()));
    }
    @Override
    public boolean isGraduated() {
        return gradesBySemester.values().stream().mapToInt(c->c.size()).sum()==24;
    }
    @Override
    public String getLog() {
        return String.format("Student with ID %s graduated with average grade %.2f in 4 years.", id, average());
    }
}
class Course{
    String name;
    List<Integer> grades;

    public Course(String name) {
        this.name = name;
        grades = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    void addGrade(int grade){
        grades.add(grade);
    }
    int count(){
        return grades.size();
    }
    double average(){
        return grades.stream().mapToInt(g->g).average().orElse(5.0);
    }
    @Override
    public String toString() {
        return String.format("%s %d %.2f",name,count(),average());
    }
}
class Faculty {
    Map<String, Student> students;
    Map<String, Course> courses;
    StringBuilder log;
    public Faculty() {
        students = new HashMap<>();
        courses = new TreeMap<>(Comparator.reverseOrder());
        log = new StringBuilder();
    }

    void addStudent(String id, int yearsOfStudies) {
        Student toadd = null;
        if (yearsOfStudies == 3)
            toadd = new ThreeYearStudent(id);
        else
            toadd = new FourYearStudent(id);
        students.put(id, toadd);
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        Student student = students.get(studentId);
        student.addGrade(term,courseName,grade);
        if (student.isGraduated()){
            log.append(student.getLog()).append("\n");
            students.remove(studentId);
        }
        courses.putIfAbsent(courseName, new Course(courseName));
        courses.get(courseName).addGrade(grade);
    }

    String getFacultyLogs() {
        return log.toString().substring(0,log.toString().length()-1);
    }

    String getDetailedReportForStudent(String id) {
        return students.get(id).getDetailedReport();
    }

    void printFirstNStudents(int n) {
        students.values().stream().
                sorted(Comparator.comparing(Student::passedCourses).thenComparing(Student::average).thenComparing(Student::getId).reversed())
                .limit(n).forEach(student->System.out.println(student.getShortReport()));

    }

    void printCourses() {
        courses.values().stream().sorted(Comparator.comparing(Course::count).thenComparing(Course::average).thenComparing(Course::getName)).forEach(System.out::println);
    }
}
class OperationNotAllowedException extends Exception{
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
