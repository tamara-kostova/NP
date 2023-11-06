package kolokviumski1;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();

        System.out.println(risk.processAttacksData(System.in));

    }
}
class Risk{
    public int processAttacksData(InputStream is){
        Scanner scanner = new Scanner(is);
        int win = 0;
        while (scanner.hasNext()) {
            String[] attackers = scanner.nextLine().split(";");
            String attacks1[] = attackers[0].split(" ");
            int [] nattacks1 = Arrays.stream(attacks1).mapToInt(a->Integer.parseInt(a)).sorted().toArray();
            String attacks2[] = attackers[1].split(" ");
            int [] nattacks2 = Arrays.stream(attacks2).mapToInt(a->Integer.parseInt(a)).sorted().toArray();
            boolean flag = true;
            for (int i = 0; i < 3; i++) {
                if (nattacks1[i]<=nattacks2[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                win++;
        }
        scanner.close();
        return win;
    }
}