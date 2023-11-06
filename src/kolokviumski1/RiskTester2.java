package kolokviumski1;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class RiskTester2 {
    public static void main(String[] args) {
        Risk2 risk = new Risk2();
        risk.processAttacksData(System.in);
    }
}
class Risk2{
    public void processAttacksData(InputStream is){
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()) {
            int soldiers1=0, soldiers2=0;
            String[] attackers = scanner.nextLine().split(";");
            String attacks1[] = attackers[0].split(" ");
            int [] nattacks1 = Arrays.stream(attacks1).mapToInt(a->Integer.parseInt(a)).sorted().toArray();
            String attacks2[] = attackers[1].split(" ");
            int [] nattacks2 = Arrays.stream(attacks2).mapToInt(a->Integer.parseInt(a)).sorted().toArray();
            for (int i = 0; i < 3; i++) {
                if (nattacks1[i]<=nattacks2[i]) {
                    soldiers2++;
                }
                else
                    soldiers1++;
            }
            System.out.println(String.format("%d %d",soldiers1,soldiers2));
        }
        scanner.close();
    }
}