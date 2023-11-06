package kolokviumski1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()){
            String [] parts = scanner.nextLine().split(";");
            stavki.add(new Stavka(parts[0],parts[1],Integer.parseInt(parts[2])));
        }
        scanner.close();
    }
    public void printStudentReport (String index, OutputStream os){
        List<Stavka> filtered = filtered(index);
        PrintWriter printWriter = new PrintWriter(os);
        if (filtered.size()==0) {
            printWriter.printf("Student %s not found!\n", index);
        }
        else {
            int total = getTotalForIndex(index);
            long fee = getFee(total);
            printWriter.printf("Student: %s Net: %d Fee: %d Total: %d\n", index, total, fee, total + fee);
            printWriter.printf("Items:\n");
            for (int i = 0; i < filtered.size(); i++) {
                printWriter.println(String.format("%d. %s %d", i + 1, filtered.get(i).getItem(), filtered.get(i).getPrice()));
            }
        }
        printWriter.flush();
    }
    public int getTotalForIndex(String index){
        List<Stavka> filtered = filtered(index);
        int sum = 0;
        for (Stavka s: filtered)
            sum+=s.getPrice();
        return sum;
    }
    public List<Stavka> filtered(String index){
        List<Stavka> result = stavki.stream().filter(s->s.getIndex().equals(index)).collect(Collectors.toList());
        Collections.sort(result,Collections.reverseOrder());
        return result;
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