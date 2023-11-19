package kolokviumski1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Color {
    RED, GREEN, BLUE
}
public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}

class Canvas {
    private List<Shape> shapes;

    public Canvas() {
        shapes = new ArrayList<>();
    }

    public void add(String id, Color color, float radius){
        Circle toadd = new Circle(id,color,radius);
        shapes.add(place(toadd.weight()),toadd);
    }
    public void add(String id, Color color, float width, float height){
        Rectangle toadd = new Rectangle(id,color,width,height);
        shapes.add(place(toadd.weight()),toadd);
    }
    public int place(float weight){
        for (int i =0; i<shapes.size();i++){
            if (shapes.get(i).weight()<weight)
                return i;
        }
        return shapes.size();
    }
    public void scale(String id, float scaleFactor){
        Shape temp = null;
        for (int i=0; i<shapes.size(); i++){
            if (shapes.get(i).getId().equals(id)){
                temp = shapes.get(i);
                break;
            }
        }
        shapes.remove(temp);
        temp.scale(scaleFactor);
        int index = place(temp.weight());
        shapes.add(index,temp);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Shape s: shapes){
            sb.append(s.toString()).append("\n");
        }
        return sb.toString();
    }
}
class Circle extends Shape{
    private float radius;

    public Circle(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }
    @Override
    public float weight() {
        return (float)(radius*radius*Math.PI);
    }
    @Override
    public void scale(float scaleFactor) {
        radius*=scaleFactor;
    }
    @Override
    public String toString() {
        return String.format("C: %-5s%-10s%10.2f",id,color,weight());
    }
}
class Rectangle extends Shape{
    private float a;
    private float b;

    public Rectangle(String id, Color color, float a, float b) {
        super(id, color);
        this.a = a;
        this.b = b;
    }
    @Override
    public float weight() {
        return a*b;
    }
    @Override
    public void scale(float scaleFactor) {
        a*=scaleFactor;
        b*=scaleFactor;
    }

    @Override
    public String toString() {
        return String.format("R: %-5s%-10s%10.2f",id,color,weight());
    }
}
abstract class Shape implements Scalable,Stackable{
    String id;
    Color color;
    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }
}
interface Scalable {
    void scale(float scaleFactor);
}
interface Stackable {
    float weight();
}