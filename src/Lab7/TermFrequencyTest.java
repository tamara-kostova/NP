package Lab7;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde
class TermFrequency{
    Map<String, Integer> wordsFrequency;
    public TermFrequency(InputStream inputStream, String[] stopWords){
        wordsFrequency = new TreeMap<>();
        Set<String> stopSet = new HashSet<>();
        Arrays.stream(stopWords).forEach(word->stopSet.add(word));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        bufferedReader.lines().forEach(
                line -> {
                    line = line.trim();
                    if(line.length()>0){
                        Arrays.stream(line.split(" ")).forEach(
                                word->{
                                    word = word.toLowerCase().replace(",", "").replace(".", "").trim();
                                    if(!stopSet.contains(word)&&!word.isEmpty()){
                                        wordsFrequency.putIfAbsent(word,0);
                                        wordsFrequency.replace(word, wordsFrequency.get(word)+1);
                                    }
                                }
                        );
                    }
                }
        );
    }
    public int countTotal(){
        return wordsFrequency.values().stream().mapToInt(i->i).sum();
    }
    public int countDistinct(){
        return wordsFrequency.keySet().size();
    }
    public List<String> mostOften(int n){
        return wordsFrequency.keySet().stream()
                .sorted((l,r)->Integer.compare(wordsFrequency.get(r),wordsFrequency.get(l))).limit(n).collect(Collectors.toList());
    }
}