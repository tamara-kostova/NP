package kolokviumski1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}
class OnlinePayments{
    List<Stavka> stavki;
    public OnlinePayments() {
        stavki = new ArrayList<>();
    }
    public void readItems (InputStream is){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        stavki = bufferedReader.lines().map(line->PaymentFactory.createStavka(line)).collect(Collectors.toList());
    }
    public void printStudentReport (String index, OutputStream os){
        List<Stavka> filtered = stavki.stream().sorted(Comparator.reverseOrder()).filter(s->s.getIndex().equals(index)).collect(Collectors.toList());
        PrintWriter printWriter = new PrintWriter(os);
        if (filtered.size()==0) {
            printWriter.printf("Student %s not found!\n", index);
        }
        else {
            int total = getTotalForIndex(index);
            long fee = getFee(total);
            printWriter.printf("Student: %s Net: %d Fee: %d Total: %d\n", index, total, fee, total + fee);
            printWriter.printf("Items:\n");
            IntStream.range(0,filtered.size()).forEach(
                i-> printWriter.println(String.format("%d. %s %d", i + 1, filtered.get(i).getItem(), filtered.get(i).getPrice())));
        }
        printWriter.flush();
    }
    public int getTotalForIndex(String index){
        return stavki.stream().sorted(Comparator.reverseOrder()).filter(s->s.getIndex().equals(index))
                .mapToInt(Stavka::getPrice).sum();
    }
    public long getFee(int sum){
        long fee =Math.round(0.0114*sum);
        if (fee<=3)
            return 3;
        if (fee>300)
            return 300;
        return fee;
    }
}
class Stavka implements Comparable<Stavka> {
    private String index;
    private String item;
    private Integer price;
    public Stavka(String index, String item, Integer price) {
        this.index = index;
        this.item = item;
        this.price = price;
    }

    public String getIndex() {
        return index;
    }

    public String getItem() {
        return item;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s %d",item,price);
    }

    @Override
    public int compareTo(Stavka o) {
        if (o.getPrice()<price)
            return 1;
        if (o.getPrice()>price)
            return -1;
        return 0;
    }
}
class PaymentFactory{
    static Stavka createStavka(String line){
        String [] parts = line.split(";");
        return new Stavka(parts[0],parts[1],Integer.parseInt(parts[2]));
    }
}