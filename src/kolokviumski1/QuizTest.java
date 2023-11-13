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
            questions.add(new TFQuestion(parts[1],Integer.parseInt(parts[2]),parts[3]));
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
            if (answers.get(i).equals(questions.get(i).getAnswer())){
                sum+=questions.get(i).getPoints();
                printWriter.printf(String.format("%d. %.2f\n",i+1, (float)questions.get(i).getPoints()));
            }
            else if (questions.get(i).getType().equals(Type.TF)){
                printWriter.printf(String.format("%d. 0.00\n",i+1));
            }
            else {
                printWriter.printf(String.format("%d. %.2f\n",i+1,-0.2*questions.get(i).getPoints()));
                sum-=0.2*questions.get(i).getPoints();
            }
        }
        printWriter.printf(String.format("Total points: %.2f",sum));
        printWriter.flush();
    }
}
abstract class Question implements Comparable<Question>{
    Type type;
    private String text;
    private int points;
    private String answer;
    public Question(String text, int points, String answer) {
        this.text = text;
        this.points = points;
        this.answer = answer;
    }
    public String getAnswer(){
        return answer;
    }
    public int getPoints() {
        return points;
    }
    public Type getType(){
        return type;
    }
    @Override
    public String toString() {
        if (type.equals(Type.TF))
            return String.format("True/False Question: %s Points: %d Answer: %s\n",text, points,answer);
        return String.format("Multiple Choice Question: %s Points %d Answer: %s\n",text, points,answer);
    }

    @Override
    public int compareTo(Question o) {
        return points-o.points;
    }
}
class TFQuestion extends Question{
    public TFQuestion(String text, int points, String answer) {
        super(text, points, answer);
        this.type = Type.TF;
    }

}
class MCQuestion extends Question{
    public MCQuestion(String text, int points, String answer) {
        super(text, points, answer);
        this.type = Type.MC;
    }
}
enum Type{
    TF, MC
}
class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}