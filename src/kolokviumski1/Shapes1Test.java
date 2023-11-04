package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}
class ShapesApplication {
    private ArrayList<Canvas> canvases;

    public ShapesApplication() {
        canvases = new ArrayList<>();
    }

    public int readCanvases (InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String [] parts = line.split(" ");
            ArrayList<Square> squares = new ArrayList<>();
            for (int i=1; i<parts.length; i++)
                squares.add(new Square(Integer.parseInt(parts[i])));
            canvases.add(new Canvas(parts[0],squares));
        }
        return canvases.stream().map(c->c.getSize()).mapToInt(Integer::intValue).sum();
    }
    public void printLargestCanvasTo (OutputStream outputStream){
        Canvas largest = canvases.stream().max(Comparator.comparing(Canvas::totalPerimeter)).orElse(null);
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(largest);
        printWriter.close();
    }
}
class Canvas{
    private String id;
    private ArrayList<Square> squares;

    public Canvas(String id, ArrayList<Square>squares) {
        this.id = id;
        this.squares=squares;
    }
    public int getSize(){
        return squares.size();
    }
    public int totalPerimeter(){
        return squares.stream().map(s->s.getPerimeter()).mapToInt(Integer::intValue).sum();
    }
    @Override
    public String toString() {
        return String.format("%s %d %d",id,getSize(),totalPerimeter());
    }
}
class Square{
    private int a;

    public Square(int a) {
        this.a = a;
    }

    public int getPerimeter() {
        return 4*a;
    }
}