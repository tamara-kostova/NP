package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

    }
}
class MojDDV{
    List<Record> records;

    public MojDDV() {
        records = new ArrayList<>();
    }

    void readRecords (InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()){
            Record toAdd = null;
            boolean flag = true;
            try {
                toAdd = Record.createRecord(scanner.nextLine());
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
                flag = false;
            }
            if (flag)
                records.add(toAdd);
        }
        scanner.close();
    }
    void printTaxReturns (OutputStream outputStream){
        PrintWriter printWriter = new PrintWriter(outputStream);
        for (Record r : records){
            printWriter.printf(r.toString());
        }
        printWriter.flush();
    }
}
class Record{
    private String id;
    private List<Item> items;

    public Record(String id, List<Item> items) {
        this.id = id;
        this.items = items;
    }
    public static Record createRecord (String line) throws AmountNotAllowedException{
        String [] parts = line.split(" ");
        String id = parts[0];
        List<Item> items = new ArrayList<>();
        for (int i=1; i<parts.length-1; i+=2){
            items.add(new Item(Integer.parseInt(parts[i]),parts[i+1]));
        }
        int sum = items.stream().mapToInt(i->i.getPrice()).sum();
        if (sum>30000)
            throw new AmountNotAllowedException(sum);
        return new Record(id,items);
    }
    public int getSum(){
        return items.stream().mapToInt(i -> i.getPrice()).sum();
    }
    public double getTaxReturn(){
        return items.stream().mapToDouble(i-> i.getTaxReturn()).sum();
    }
    @Override
    public String toString() {
        return  String.format("%s %d %.2f\n",id,getSum(),getTaxReturn());
    }
}
class Item{
    private int price;
    private String taxType;

    public Item(int price, String taxType) {
        this.price = price;
        this.taxType = taxType;
    }
    public int getPrice(){
        return price;
    }
    public double getTaxReturn(){
        if (taxType.equals("A"))
            return price*0.18*0.15;
        if (taxType.equals("B"))
            return price*0.05*0.15;
        return 0;
    }
}
class AmountNotAllowedException extends Exception{
    public AmountNotAllowedException(int sum) {
        super(String.format("Receipt with amount %d is not allowed to be scanned",sum));
    }
}