package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Word vectors test
 */
public class WordVectorsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] words = new String[n];
        List<List<Integer>> vectors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            words[i] = parts[0];
            List<Integer> vector = Arrays.stream(parts[1].split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            vectors.add(vector);
        }
        n = scanner.nextInt();
        scanner.nextLine();
        List<String> wordsList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            wordsList.add(scanner.nextLine());
        }
        WordVectors wordVectors = new WordVectors(words, vectors);
        wordVectors.readWords(wordsList);
        n = scanner.nextInt();
        List<Integer> result = wordVectors.slidingWindow(n);
        System.out.println(result.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        scanner.close();
    }
}
class WordVectors{
    Map<String,Vector> wordsMap;
    List<Vector> vectorsList;

    public WordVectors(String[] words, List<List<Integer>> vectors) {
        wordsMap = new TreeMap<>();
        IntStream.range(0,words.length).forEach(i->wordsMap.put(words[i], new Vector (vectors.get(i))));
        vectorsList = new ArrayList<>();
    }

    public void readWords(List<String> words){
        vectorsList = words.stream().map(word->wordsMap.getOrDefault(word,Vector.DEFAULT)).collect(Collectors.toList());
    }
    public List<Integer> slidingWindow(int n){
        return IntStream.range(0,vectorsList.size()-n+1).mapToObj(i->vectorsList.subList(i,i+n).stream().reduce(Vector.IDENTITY,Vector::sum))
                .map(Vector::max).collect(Collectors.toList());
    }
}
class Vector{
    static final Vector DEFAULT = new Vector(Arrays.asList(5, 5, 5, 5, 5));
    static final Vector IDENTITY = new Vector(Arrays.asList(0, 0, 0, 0, 0));
    List<Integer> numbers;

    public Vector(List<Integer> numbers) {
        this.numbers = numbers;
    }
    public Vector sum(Vector v2) {
        return new Vector(IntStream.range(0, v2.numbers.size())
                .map(i -> numbers.get(i) + v2.numbers.get(i))
                .boxed()
                .collect(Collectors.toList()));
    }
    public int max(){
        return numbers.stream().mapToInt(i->i).max().orElse(0);
    }
}

