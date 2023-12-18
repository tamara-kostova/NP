package kolokviumski2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}
class QuizProcessor{

    public static Map<String, Double> processAnswers(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return br.lines().map(line-> {
            try {
                return Quiz.createQuiz(line);
            } catch (InvalidNumberOfAnswers e) {
                System.out.println(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Quiz::getStudentId,Quiz::getPoints,Double::sum,TreeMap::new));

    }
}
class Quiz{
    String studentId;
    List<String> correctAnswers;
    List<String> studentAnswers;
    private double points;
    public Quiz(String studentId, List<String> correctAnswers, List<String> studentAnswers) {
        this.studentId = studentId;
        this.correctAnswers = correctAnswers;
        this.studentAnswers = studentAnswers;
        IntStream.range(0,studentAnswers.size()).forEach(i->{
            if (studentAnswers.get(i).equals(correctAnswers.get(i))) points++;
            else points-=0.25;
        });
    }

    public String getStudentId() {
        return studentId;
    }

    public double getPoints() {
        return points;
    }

    public static Quiz createQuiz(String line) throws InvalidNumberOfAnswers{
        //200000;C, D, D, D, A, C, B, D, D;C, D, D, D, D, B, C, D, A
        List <String> correctAnswers = new ArrayList<>(List.of(line.split(";")[1].split(",")));
        List <String> studentAnswers = new ArrayList<>(List.of(line.split(";")[2].split(",")));
        if (correctAnswers.size()!=studentAnswers.size())
            throw new InvalidNumberOfAnswers("A quiz must have same number of correct and selected answers");
        return new Quiz(line.split(";")[0],correctAnswers,studentAnswers);
    }
}
class InvalidNumberOfAnswers extends Exception{
    public InvalidNumberOfAnswers(String message) {
        super(message);
    }
}