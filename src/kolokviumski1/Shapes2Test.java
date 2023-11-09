package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        try {
            shapesApplication.readCanvases(System.in);
        }
        catch (IrregularCanvasException ex){
            throw new RuntimeException();
        }
        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}
class ShapesApplication {
    private double maxArea;
    private ArrayList<Canvas> canvases;
    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        canvases = new ArrayList<>();
    }
    public void readCanvases (InputStream inputStream) throws IrregularCanvasException{
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNext()){
            try{
                Canvas toadd = Canvas.createCanvas(scanner.nextLine(),maxArea);
                canvases.add(toadd);
            }
            catch (IrregularCanvasException ex){
                System.out.println(ex.getMessage());
            }
        }
        scanner.close();
    }
    public List<Canvas> sorted(){
        Collections.sort(canvases,Collections.reverseOrder());
        return canvases;
    }
    public void printCanvases (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        for (Canvas c : sorted()){
            printWriter.printf(c.toString());
        }
        printWriter.flush();
    }
}
class Canvas implements Comparable<Canvas>{
    private String id;
    private List<Shape> shapes;

    public Canvas(String id, List<Shape> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    public static Canvas createCanvas(String line, double maxArea) throws IrregularCanvasException{
        String [] parts = line.split(" ");
        String canvasID = parts[0];
        List<Shape> shapes = new ArrayList<>();
        for (int i=1; i<parts.length-1; i+=2) {
            if (parts[i].equals("C")) {
                Circle toAdd = new Circle(Integer.parseInt(parts[i + 1]));
                if (toAdd.getArea() > maxArea)
                    throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f", canvasID, maxArea));
                shapes.add(toAdd);
            } else {
                Square toAdd = new Square(Integer.parseInt(parts[i + 1]));
                if (toAdd.getArea() > maxArea)
                    throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f", canvasID, maxArea));
                shapes.add(toAdd);
            }
        }
        return new Canvas(canvasID,shapes);
    }
    public int numberOfShapes(){
        return shapes.size();
    }
    public double minArea(){
        return shapes.stream().mapToDouble(c->c.getArea()).min().orElse(Double.MAX_VALUE);
    }
    public double maxArea(){
        return shapes.stream().mapToDouble(c->c.getArea()).max().orElse(Double.MIN_VALUE);
    }
    public double averageArea(){
        return shapes.stream().mapToDouble(c->c.getArea()).average().orElse(0);
    }
    public double totalArea(){
        return shapes.stream().mapToDouble(c->c.getArea()).sum();
    }
    public int numberCircles(){
        return shapes.stream().map(s->s.getClass()).filter(c->c==new Circle(0).getClass()).collect(Collectors.toList()).size();
    }
    public int numberSquares(){
        return shapes.stream().map(s->s.getClass()).filter(c->c==new Square(0).getClass()).collect(Collectors.toList()).size();
    }
    @Override
    public String toString() {
        return String.format("%s %d %d %d %.2f %.2f %.2f\n",id, numberOfShapes(),numberCircles(),numberSquares(),minArea(),maxArea(),averageArea());
    }

    @Override
    public int compareTo(Canvas o) {
        if (totalArea()>o.totalArea())
            return 1;
        if (totalArea()<o.totalArea())
            return -1;
        return 0;
    }
}
abstract class Shape{
    int size;

    public Shape(int size) {
        this.size = size;
    }
    abstract double getArea();
}
class Square extends Shape{

    public Square(int side) {
        super(side);
    }
    public double getArea(){
        return size*size;
    }
}
class Circle extends Shape{

    public Circle(int radius) {
        super(radius);
    }
    public double getArea(){
        return size*size*Math.PI;
    }
}
class IrregularCanvasException extends Exception{
    public IrregularCanvasException(String message) {
        super(message);
    }
}