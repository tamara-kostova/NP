package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * January 2016 Exam problem 2
 */
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

// your code here
@SuppressWarnings("unchecked")
class Cluster<T extends Clusterable> {
    Map<Long, T> items;

    public Cluster() {
        items = new HashMap<>();
    }

    public void addItem(T element){
        items.put(element.id(),element);
    }
    public void near(long id, int top){
        T element = items.get(id);
        List <T> result = items.values().stream().sorted(Comparator.comparingDouble(l -> l.distance(element))).limit(top).collect(Collectors.toList());
        IntStream.range(1,result.size()).forEach(
                i->System.out.printf("%d. -> %d: %.3f\n", i, result.get(i).id(), result.get(i).distance(element))
        );
    }
}
interface Clusterable<T>{
    long id();
    double distance(T other);
}
class Point2D implements Clusterable<Point2D>{
    long id;
    float x;
    float y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public double distance(Point2D other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }
}