package Lab2;

import java.util.*;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);
            try {
                if (Integer.parseInt(parts[0]) == 0) { //point
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } else { //circle
                    int radius = Integer.parseInt(parts[5]);
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                }
            }
            catch (MovableObjectNotFittableException exception){
                System.out.println(exception.getMessage());
            }
        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}
interface Movable{
    void moveUp() throws ObjectCanNotBeMovedException;
    void moveLeft() throws ObjectCanNotBeMovedException;
    void moveRight() throws ObjectCanNotBeMovedException;
    void moveDown() throws ObjectCanNotBeMovedException;
    int getCurrentXPosition();
    int getCurrentYPosition();
    boolean canBeAdded (int xmax, int ymax);
    String getException();
    TYPE getType();
}
class MovablePoint implements Movable{
    private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }
    @Override
    public void moveUp() throws ObjectCanNotBeMovedException{
        if (y+ySpeed>MovablesCollection.y_MAX)
            throw new ObjectCanNotBeMovedException(x,y+ySpeed);
        y+=ySpeed;
    }
    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException{
        if (x-xSpeed<0)
            throw new ObjectCanNotBeMovedException(x-xSpeed,y);
        x-=xSpeed;
    }
    @Override
    public void moveRight() throws ObjectCanNotBeMovedException{
        if (x+xSpeed>MovablesCollection.x_MAX)
            throw new ObjectCanNotBeMovedException(x+xSpeed,y);
        x+=xSpeed;
    }
    @Override
    public void moveDown() throws ObjectCanNotBeMovedException{
        if (y-ySpeed<0)
            throw new ObjectCanNotBeMovedException(x,y-ySpeed);
        y-=ySpeed;
    }
    public int getCurrentXPosition(){
        return x;
    }
    public int getCurrentYPosition(){
        return y;
    }

    @Override
    public String toString() {
        return String.format("Movable point with coordinates (%d,%d)",x,y);
    }

    @Override
    public boolean canBeAdded(int xmax, int ymax) {
        return xmax>=x && ymax>=y && x>=0 && y>=0;
    }
    @Override
    public String getException() {
        return String.format("Movable point with coordinates (%d,%d) can not be fitted into the collection", x, y);
    }

    @Override
    public TYPE getType() {
        return TYPE.POINT;
    }
}
class MovableCircle implements Movable{
    private int radius;
    private MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }
    @Override
    public void moveUp() throws ObjectCanNotBeMovedException{
        center.moveUp();
    }
    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException{
        center.moveLeft();
    }
    @Override
    public void moveRight() throws ObjectCanNotBeMovedException{
        center.moveRight();
    }
    @Override
    public void moveDown() throws ObjectCanNotBeMovedException{
        center.moveDown();
    }

    @Override
    public int getCurrentXPosition() {
        return center.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return center.getCurrentYPosition();
    }

    @Override
    public String toString() {
        return String.format("Movable circle with center coordinates (%d,%d) and radius %d",center.getCurrentXPosition(),center.getCurrentYPosition(),radius);
    }
    @Override
    public boolean canBeAdded(int xmax, int ymax) {
        return xmax>=center.getCurrentXPosition()+radius && ymax>=center.getCurrentYPosition()+radius && center.getCurrentXPosition()-radius>=0 && center.getCurrentYPosition()-radius>=0;
    }
    @Override
    public String getException() {
        return String.format("Movable circle with center (%d,%d) and radius %d can not be fitted into the collection", center.getCurrentXPosition(),center.getCurrentYPosition(),radius);
    }
    @Override
    public TYPE getType() {
        return TYPE.CIRCLE;
    }
}
class ObjectCanNotBeMovedException extends Exception{
    public ObjectCanNotBeMovedException(int x, int y) {
        super(String.format("Point (%d,%d) is out of bounds",x,y));
    }
}
class MovableObjectNotFittableException extends Exception{
    public MovableObjectNotFittableException(String message) {
        super(message);
    }
}
class MovablesCollection {
    static int x_MAX;
    static int y_MAX;
    ArrayList<Movable> movable;

    MovablesCollection(int x_MAX, int y_MAX) {
        this.x_MAX = x_MAX;
        this.y_MAX = y_MAX;
        this.movable = new ArrayList<>();
    }

    public static void setxMax(int x_MAX) {
        MovablesCollection.x_MAX = x_MAX;
    }

    public static void setyMax(int y_MAX) {
        MovablesCollection.y_MAX = y_MAX;
    }

    void addMovableObject(Movable m) throws MovableObjectNotFittableException{
        if (!m.canBeAdded(x_MAX,y_MAX))
            throw new MovableObjectNotFittableException(m.getException());
        movable.add(m);

    }
    void moveObjectsFromTypeWithDirection (TYPE type, DIRECTION direction){
        movable.stream().filter(m->m.getType().equals(type)).forEach(
                el -> {
                    try{
                        switch (direction){
                            case UP: {
                                el.moveUp();
                                break;
                            }
                            case DOWN: {
                                el.moveDown();
                                break;
                            }
                            case LEFT: {
                                el.moveLeft();
                                break;
                            }
                            case RIGHT: {
                                el.moveRight();
                                break;
                            }
                        }
                    }
                    catch(ObjectCanNotBeMovedException exception){
                        System.out.println(exception.getMessage());
                    }
                }
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Collection of movable objects with size %d:\n",movable.size()));
        movable.forEach(m->sb.append(m.toString()).append("\n"));
        return sb.toString();
    }
}