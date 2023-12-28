package kolokviumski2;

import java.io.*;
import java.util.*;

class CosineSimilarityCalculator {
    public static double cosineSimilarity (Collection<Integer> c1, Collection<Integer> c2) {
        int [] array1;
        int [] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1=0, down2=0;

        for (int i=0;i<c1.size();i++) {
            up+=(array1[i] * array2[i]);
        }

        for (int i=0;i<c1.size();i++) {
            down1+=(array1[i]*array1[i]);
        }

        for (int i=0;i<c1.size();i++) {
            down2+=(array2[i]*array2[i]);
        }

        return up/(Math.sqrt(down1)*Math.sqrt(down2));
    }
}

public class TextProcessorTest {

    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();

        textProcessor.readText(System.in);

        System.out.println("===PRINT VECTORS===");
        textProcessor.printTextsVectors(System.out);

        System.out.println("PRINT FIRST 20 WORDS SORTED ASCENDING BY FREQUENCY ");
        textProcessor.printCorpus(System.out,  20, true);

        System.out.println("PRINT FIRST 20 WORDS SORTED DESCENDING BY FREQUENCY");
        textProcessor.printCorpus(System.out, 20, false);

        System.out.println("===MOST SIMILAR TEXTS===");
        textProcessor.mostSimilarTexts(System.out);
    }
}
class TextProcessor{
    Map<String, Integer> frequencies;
    List<Map<String, Integer>> frequenciesByText;
    List<String> originals;
    public TextProcessor() {
        frequencies = new TreeMap<>();
        originals = new ArrayList<>();
        frequenciesByText = new ArrayList<>();
    }

    public void readText (InputStream is){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        bufferedReader.lines().forEach(
                line->{
                    Map<String, Integer> wordsForMap = new TreeMap<>();
                    originals.add(line);
                    line = line.replaceAll("[^A-Za-z\\s+]", "");
                    line = line.toLowerCase();
                    String [] words = line.split("\\s+");
                    Arrays.stream(words).forEach(
                            word-> {
                                frequencies.putIfAbsent(word,0);
                                frequencies.replace(word,frequencies.get(word)+1);
                                wordsForMap.putIfAbsent(word,0);
                                wordsForMap.replace(word,wordsForMap.get(word)+1);
                            }
                    );
                    frequenciesByText.add(wordsForMap);
                }
        );
        frequencies.keySet().forEach(word -> {
            frequenciesByText.forEach(map -> {
                map.putIfAbsent(word, 0);
            });
        });
    }
    public void printTextsVectors (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        frequenciesByText.stream().map(i->i.values()).forEach(printWriter::println);
        printWriter.flush();

    }
    public void printCorpus(OutputStream os, int n, boolean ascending){
        PrintWriter printWriter = new PrintWriter(os);
        frequencies.entrySet().stream().sorted(ascending?Map.Entry.comparingByValue(Comparator.naturalOrder()):Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(n).forEach(entry->printWriter.println(String.format("%s : %d",entry.getKey(),entry.getValue())));
        printWriter.flush();
    }
    public void mostSimilarTexts (OutputStream os){
        PrintWriter printWriter = new PrintWriter(os);
        double maxs = 0;
        int iMax=0, jMax = 0;

        printWriter.flush();
        for (int i=0; i<originals.size(); i++){
            for (int j=0; j<originals.size(); j++){
                if (i!=j){
                    double similarity = CosineSimilarityCalculator.cosineSimilarity(
                            frequenciesByText.get(i).values(),frequenciesByText.get(j).values()
                    );
                    if (similarity>maxs){
                        maxs = similarity;
                        iMax = i;
                        jMax = j;
                    }
                }
            }
        }
        printWriter.println(originals.get(iMax));
        printWriter.println(originals.get(jMax));
        printWriter.printf("%.10f",maxs);
        printWriter.flush();
    }
}
