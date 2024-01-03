//package kolokviumski2;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class LabExercisesTest {
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        LabExercises labExercises = new LabExercises();
//        while (sc.hasNextLine()) {
//            String input = sc.nextLine();
//            String[] parts = input.split("\\s+");
//            String index = parts[0];
//            List<Integer> points = Arrays.stream(parts).skip(1)
//                    .mapToInt(Integer::parseInt)
//                    .boxed()
//                    .collect(Collectors.toList());
//
//            labExercises.addStudent(new Student(index, points));
//        }
//
//        System.out.println("===printByAveragePoints (ascending)===");
//        labExercises.printByAveragePoints(true, 100);
//        System.out.println("===printByAveragePoints (descending)===");
//        labExercises.printByAveragePoints(false, 100);
//        System.out.println("===failed students===");
//        labExercises.failedStudents().forEach(System.out::println);
//        System.out.println("===statistics by year");
//        labExercises.getStatisticsByYear().entrySet().stream()
//                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
//                .forEach(System.out::println);
//
//    }
//}
//class Student {
//    private String index;
//    private List<Integer> points;
//
//    public Student(String index, List<Integer> points) {
//        this.index = index;
//        this.points = points;
//    }
//
//    public String getIndex() {
//        return index;
//    }
//    public int getYear(){
//        return 20-Integer.parseInt(index.substring(0,2));
//    }
//    public double averagePoints(){
//        return points.stream().mapToInt(p->p).sum()/10.0;
//    }
//    public boolean failed(){
//        return points.size()<8;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("%s %s %.2f",index,failed()?"NO":"YES",averagePoints());
//    }
//}
//class LabExercises {
//    Map<Integer,List<Double>> averageByYear;
//    Map<String, Student> students;
//    Comparator<Student> studentComparator = Comparator.comparing(Student::averagePoints).thenComparing(Student::getIndex);
//    public LabExercises() {
//        averageByYear = new TreeMap<>();
//        students = new TreeMap<>();
//    }
//
//    public void addStudent (Student student){
//        students.put(student.getIndex(),student);
//        if (!student.failed()) {
//            averageByYear.putIfAbsent(student.getYear(), new ArrayList<>());
//            averageByYear.get(student.getYear()).add(student.averagePoints());
//        }
//    }
//    public void printByAveragePoints (boolean ascending, int n){
//        if (!ascending)
//            studentComparator = studentComparator.reversed();
//        students.entrySet().stream().sorted(Map.Entry.comparingByValue(studentComparator)).
//                map(Map.Entry::getValue).limit(n).forEach(System.out::println);
//    }
//    public List<Student> failedStudents (){
//        Comparator<Student> comparator = Comparator.comparing(Student::getIndex).thenComparing(Student::averagePoints);
//        return students.values().stream().filter(Student::failed).sorted(comparator).collect(Collectors.toList());
//    }
//    public Map<Integer,Double> getStatisticsByYear(){
//        Map<Integer, Double> statisticsByYear = new HashMap<>();
//        averageByYear.entrySet().stream().forEach(year->{
//            statisticsByYear.put(year.getKey(),year.getValue().stream().mapToDouble(l->l).average().orElse(0.0));
//        });
//        return statisticsByYear;
//    }
//}
