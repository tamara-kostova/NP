package kolokviumski1;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
class ShoppingCart {
    List<ShoppingItem> shoppingItems;

    public ShoppingCart() {
        shoppingItems = new ArrayList<>();
    }

    public void addItem(String itemData) throws InvalidOperationException{
        String [] parts = itemData.split(";");
        if (Double.parseDouble(parts[4])<=0)
            throw new InvalidOperationException(String.format("The quantity of the product with id %s can not be 0.",parts[1]));
        shoppingItems.add(new ShoppingItem(parts[0],Integer.parseInt(parts[1]),parts[2],Double.parseDouble(parts[3]),Double.parseDouble(parts[4])));
    }
    public void printShoppingCart(OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        Collections.sort(shoppingItems,Collections.reverseOrder());
        for(ShoppingItem shoppingItem : shoppingItems){
            printWriter.printf(shoppingItem.toString());
        }
        printWriter.flush();
    }
    public void blackFridayOffer(List<Integer> discountItems, OutputStream os) throws InvalidOperationException{
        if (discountItems.isEmpty())
            throw new InvalidOperationException("There are no products with discount.");
        PrintWriter printWriter = new PrintWriter(os);
        for (ShoppingItem item : shoppingItems){
            if (discountItems.stream().anyMatch(el->el==item.productID)){
                printWriter.printf(item.discountToString());
                item.setProductPrice(item.productPrice*0.9);
            }
        }
        printWriter.flush();
    }
}
class ShoppingItem implements Comparable<ShoppingItem>{
    String type;
    int productID;
    String productName;
    double productPrice;
    double quantity;

    public ShoppingItem(String type, int productID, String productName, double productPrice, double quantity) {
        this.type = type;
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f\n",productID, getTotalPrice());
    }
    public String discountToString(){
        return String.format("%s - %.2f\n",productID, 0.1*getTotalPrice());
    }
    public double getTotalPrice(){

        if (type.equals("WS"))
            return productPrice*quantity;
        return 0.001*productPrice*quantity;
    }
    @Override
    public int compareTo(ShoppingItem o) {
        return (int) (getTotalPrice()-o.getTotalPrice());
    }
}
class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}
