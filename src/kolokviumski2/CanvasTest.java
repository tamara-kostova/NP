package kolokviumski2;

import java.io.*;
import java.util.*;

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}
class Canvas{
    Map<String, Set<Shape>> shapesByUserId;
    Set <Shape> allShapes;
    public Canvas() {
        shapesByUserId = new TreeMap<>();
        allShapes = new TreeSet<>(Comparator.comparing(Shape::getArea));
    }
    public void readShapes (InputStream is) throws InvalidDimensionException{
       Scanner scanner = new Scanner(is);
       while (scanner.hasNextLine()){
            String line = scanner.nextLine();
                try {
                    String shapeId = line.split(" ")[1];
                    Shape newShape = ShapeFactory.createShape(line);
                    allShapes.add(newShape);
                    shapesByUserId.putIfAbsent(shapeId, new TreeSet<>(Comparator.comparing(Shape::getPerimeter)));
                    shapesByUserId.get(shapeId).add(newShape);
                } catch (InvalidIDException e) {
                    System.out.println(e.getMessage());
                }
            }
    }
    public void scaleShapes (String userID, double coef){
        shapesByUserId.getOrDefault(userID,new HashSet<>()).forEach(shape -> shape.scale(coef));
    }
    public void printAllShapes (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        allShapes.forEach(printWriter::println);
        printWriter.flush();
    }
    public void printByUserId (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        Comparator<Map.Entry<String,Set<Shape>>> comparator = Comparator.comparingInt(l -> l.getValue().size());
        shapesByUserId.entrySet().stream().sorted(comparator.reversed().thenComparing(entry->entry.getValue().stream().mapToDouble(Shape::getArea).sum())).forEach(
                entry->{
                    System.out.println(String.format("Shapes of user: %s",entry.getKey()));
                    entry.getValue().forEach(System.out::println);
                }
        );
        printWriter.flush();
    }
    public void statistics (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        DoubleSummaryStatistics dss = allShapes.stream().mapToDouble(Shape::getArea).summaryStatistics();
        printWriter.println(String.format("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f", dss.getCount(), dss.getSum(), dss.getMin(), dss.getAverage(), dss.getMax()));
        printWriter.flush();
    }
}
class ShapeFactory{
    public static Shape createShape(String line) throws InvalidIDException, InvalidDimensionException {
        String[] parts = line.split(" ");
        if (invalidId(parts[1]))
            throw new InvalidIDException(parts[1]);
        if (parts[0].equals("1")) {
            if (Double.parseDouble(parts[2]) == 0)
                throw new InvalidDimensionException();
            return new Circle(Double.parseDouble(parts[2]));
        } else if (parts[0].equals("2")) {
            if (Double.parseDouble(parts[2]) == 0)
                throw new InvalidDimensionException();
            return new Square(Double.parseDouble(parts[2]));
        }
        else {
            if (Double.parseDouble(parts[2]) == 0 || Double.parseDouble(parts[3])==0)
                throw new InvalidDimensionException();
            return new Rectangle(Double.parseDouble(parts[2]),Double.parseDouble(parts[3]));
        }
    }
    static boolean invalidId(String id){
        if (id.length()!=6)
            return true;
        for (Character c : id.toCharArray())
            if (!Character.isLetterOrDigit(c))
                return true;
        return false;
    }
}
interface Shape{
double getArea();
void scale(double coef);
double getPerimeter();
}
class Circle implements Shape{
    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI*radius*radius;
    }

    @Override
    public void scale(double coef) {
        radius*=coef;
    }

    @Override
    public double getPerimeter() {
        return 2*radius*Math.PI;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f",radius,getArea(),getPerimeter());
    }
}
class Square implements Shape{
    double a;

    public Square(double a) {
        this.a = a;
    }

    @Override
    public double getArea() {
        return a*a;
    }

    @Override
    public void scale(double coef) {
        a*=coef;
    }

    @Override
    public double getPerimeter() {
        return 4*a;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f",a,getArea(),getPerimeter());
    }
}
class Rectangle implements Shape{
    double a;
    double b;

    public Rectangle(double a, double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double getArea() {
        return a*b;
    }
    @Override
    public void scale(double coef) {
        a*=coef;
        b*=coef;
    }

    @Override
    public double getPerimeter() {
        return 2*a+2*b;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f",a,b,getArea(),getPerimeter());
    }
}
class InvalidDimensionException extends Exception{
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}
class InvalidIDException extends Exception{
    public InvalidIDException(String id) {
        super(String.format("ID %s is not valid",id));
    }
}