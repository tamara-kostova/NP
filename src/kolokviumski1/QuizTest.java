package kolokviumski1;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            }
            catch (InvalidOperationException ex){
                System.out.println(ex.getMessage());
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            try {
                quiz.answerQuiz(answers, System.out);
            }
            catch (InvalidOperationException ex){
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
class Quiz{
    List<Question> questions;

    public Quiz() {
        questions = new ArrayList<>();
    }

    public void addQuestion(String questionData) throws InvalidOperationException{
        String [] parts = questionData.split(";");
        if (parts[0].equals("MC")) {
            if (!parts[3].equals("A") && !parts[3].equals("B") && !parts[3].equals("C") && !parts[3].equals("D") && !parts[3].equals("E"))
                throw new InvalidOperationException(String.format("%s is not allowed option for this question", parts[3]));
            questions.add(new MCQuestion(parts[1],Integer.parseInt(parts[2]),parts[3]));
        }
        else
            questions.add(new TFQuestion(parts[1],Integer.parseInt(parts[2]),Boolean.parseBoolean(parts[3])));
    }
    public void printQuiz(OutputStream os){
        Collections.sort(questions,Collections.reverseOrder());
        PrintWriter printWriter = new PrintWriter(os);
        for (Question q : questions)
            printWriter.printf(q.toString());
        printWriter.flush();
    }
    public void answerQuiz (List<String> answers, OutputStream os) throws InvalidOperationException {
        if (answers.size()!=questions.size())
            throw new InvalidOperationException("Answers and questions must be of same length!");
        PrintWriter printWriter = new PrintWriter(os);
        float sum = 0;
        for (int i=0; i<answers.size(); i++){
            float points = questions.get(i).answerQuestion(answers.get(i));
            sum+=points;
            printWriter.printf(String.format("%d. %.2f\n",i+1,points));
        }
        printWriter.printf(String.format("Total points: %.2f",sum));
        printWriter.flush();
    }
}
abstract class Question implements Comparable<Question>{
    private String text;
    private int points;
    public Question(String text, int points) {
        this.text = text;
        this.points = points;
    }
    public int getPoints() {
        return points;
    }
    public String getText(){
        return text;
    }

    @Override
    public int compareTo(Question o) {
        return points-o.points;
    }
    abstract float answerQuestion(String studentAnswer);
}
class TFQuestion extends Question{
    boolean answer;
    public TFQuestion(String text, int points, boolean answer) {
        super(text, points);
        this.answer = answer;
    }
    @Override
    public String toString() {
        return String.format("True/False Question: %s Points: %d Answer: %s\n",getText(), getPoints(),answer?"true":"false");
    }
    @Override
    float answerQuestion(String studentAnswer) {
        if ((studentAnswer.equals("true")&&answer)||(studentAnswer.equals("false")&&!answer))
            return getPoints();
        return 0;
    }
}
class MCQuestion extends Question{
    String answer;
    public MCQuestion(String text, int points, String answer) {
        super(text, points);
        this.answer = answer;
    }
    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s\n",getText(), getPoints(),answer);
    }
    @Override
    float answerQuestion(String studentAnswer) {
        return answer.equals(studentAnswer)? getPoints(): -(float)0.2*getPoints();
    }
}
class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}