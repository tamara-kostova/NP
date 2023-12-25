package Lab7;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Map<String, List<String>> anagramsByWord = new TreeMap<>();
        List<String> order = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        bufferedReader.lines().forEach(
                word ->{
                    char[] wordChars = word.toCharArray();
                    Arrays.sort(wordChars);
                    String sorted = new String(wordChars);
                    if (anagramsByWord.containsKey(sorted)){
                        anagramsByWord.get(sorted).add(word);
                    }
                    else {
                        anagramsByWord.put(sorted, new ArrayList<>());
                        anagramsByWord.get(sorted).add(word);
                        order.add(sorted);
                    }
                }
        );
        order.forEach(
                key -> {
                    anagramsByWord.get(key).forEach(word -> System.out.printf(word + " "));
                    System.out.println();
                }
        );
    }
}
