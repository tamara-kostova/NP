package Lab3;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class PizzaOrderTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }

}
enum ExtraType{
    Coke,
    Ketchup
}
enum PizzaType{
    Standard,
    Pepperoni,
    Vegetarian
}
interface Item{
    String getType();
    int getPrice();
    void setCount(int count);
    int getCount ();
}
class ExtraItem implements Item{
    private ExtraType type;
    private int count;
    public ExtraItem(String type) throws InvalidExtraTypeException{
        try{
            this.type = ExtraType.valueOf(type);
            this.count=1;
        }catch (IllegalArgumentException e){
            throw new InvalidExtraTypeException(String.format("%s is not a valid type",type));
        }
    }
    @Override
    public String getType() {
        return type.name();
    }

    @Override
    public int getPrice() {
        if (type.name().equals("Coke"))
            return 5*count;
        if (type.name().equals("Ketchup"))
            return 3*count;
        return 0;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }
    @Override
    public int getCount() {
        return count;
    }
}
class PizzaItem implements Item{
    private PizzaType type;
    private int count;
    public PizzaItem(String type) throws InvalidPizzaTypeException{
        try{
            this.type = PizzaType.valueOf(type);
            this.count=1;
        }catch (IllegalArgumentException e){
            throw new InvalidPizzaTypeException(String.format("%s is not a valid type",type));
        }
    }

    @Override
    public String getType() {
        return type.name();
    }
    @Override
    public int getPrice() {
        if (type.name().equals("Standard"))
            return 10*count;
        if (type.name().equals("Pepperoni"))
            return 12*count;
        if (type.name().equals("Vegetarian"))
            return 8*count;
        return 0;
    }
    @Override
    public void setCount(int count) {
        this.count = count;
    }
    @Override
    public int getCount() {
        return count;
    }
}
class Order{
    private ArrayList<Item> items;
    private boolean locked;
    public Order() {
        items = new ArrayList<>();
        locked=false;
    }

    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException{
        if (locked)
            throw new OrderLockedException("Order is locked");
        if (count>10)
            throw new ItemOutOfStockException(String.format("%s is out of stock",item.getType()));
        Optional<Item> existing = items.stream().filter(element->element.getType().equals(item.getType())).findFirst();
        if (existing.isPresent()){
            existing.ifPresent(it->it.setCount(count));
            return;
        }
        item.setCount(count);
        items.add(item);
    }
    public int getPrice(){
        return items.stream().mapToInt(item->item.getPrice()).sum();
    }
    public void displayOrder(){
        for (int i=0; i<items.size(); i++)
            System.out.printf("%3d.%-15sx%2d%5d$\n",i+1,items.get(i).getType(),items.get(i).getCount(),items.get(i).getPrice());
        System.out.printf("%-22s%5d$\n","Total:", getPrice());

    }
    public void removeItem(int idx) throws ArrayIndexOutOfBoundsException, OrderLockedException{
        if (locked)
            throw new OrderLockedException("Order is locked");
        if (idx>=items.size())
            throw new ArrayIndexOutOfBoundsException(idx);
        items.remove(idx);
    }
    public void lock() throws EmptyOrder{
        if (items.size()==0)
            throw new EmptyOrder("EmptyOrder");
        locked = true;
    }
}
class InvalidExtraTypeException extends Exception{
    public InvalidExtraTypeException(String message) {
        super(message);
    }
}
class InvalidPizzaTypeException extends Exception{
    public InvalidPizzaTypeException(String message) {
        super(message);
    }
}
class ItemOutOfStockException extends Exception{
    public ItemOutOfStockException(String message) {
        super(message);
    }
}
class ArrayIndexOutOfBоundsException extends Exception{
    public ArrayIndexOutOfBоundsException(String message) {
        super(message);
    }
}
class EmptyOrder extends Exception{
    public EmptyOrder(String message) {
        super(message);
    }
}
class OrderLockedException extends Exception{
    public OrderLockedException(String message) {
        super(message);
    }
}