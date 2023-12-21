package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

// vashiot kod ovde
class Names{
    Map<String,Integer> countNames;

    public Names() {
        countNames = new HashMap<>();
    }

    public void addName(String name) {
        countNames.putIfAbsent(name, 0);
        countNames.replace(name, countNames.get(name)+1);
    }

    public void printN(int n) {
        countNames.entrySet().stream().filter(entry->entry.getValue()>=n).sorted(Map.Entry.comparingByKey()).forEach(entry->System.out.printf("%s (%d) %d\n",entry.getKey(),entry.getValue(),uniqueLetters(entry.getKey())));
    }

    public String findName(int len, int index) {
        List <String> names = countNames.keySet().stream().filter(name->name.length()<len).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        return names.get(index%names.size());
    }
    public static int uniqueLetters(String name){
        Set<Character> letters = new HashSet<>();
        for (char c : name.toCharArray()){
            letters.add(Character.toLowerCase(c));
        }
        return letters.size();
    }
}
