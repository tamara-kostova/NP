package kolokviumski1;

import java.util.Scanner;

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}
class MinMax<T extends Comparable<T>>{
    private T min;
    private T max;
    private int number;
    private int minNumber;
    private int maxNumber;
    public MinMax() {
        number = 0;
        minNumber = 0;
        maxNumber = 0;
    }
    public void update(T element) {
        if (number == 0){
            min = element;
            max= element;
        }
        number++;
        if (element.compareTo(max)>0) {
            maxNumber = 1;
            max = element;
        }
        else if (element.compareTo(max)==0)
            maxNumber++;
        if (element.compareTo(min)<0) {
            min = element;
            minNumber = 1;
        }
        else if (element.compareTo(min)==0)
            minNumber++;
    }
    public T max() {
        return max;
    }
    public T min(){
        return min;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d\n",min,max,number-minNumber-maxNumber);
    }
}