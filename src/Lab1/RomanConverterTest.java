package Lab1;

import java.util.Scanner;
import java.util.stream.IntStream;

public class RomanConverterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        IntStream.range(0, n)
                .forEach(x -> System.out.println(RomanConverter.toRoman(scanner.nextInt())));
        scanner.close();
    }
}


class RomanConverter {
    /**
     * Roman to decimal converter
     *
     * @param n number in decimal format
     * @return string representation of the number in Roman numeral
     */
    public static String toRoman(int n) {
        String roman = "";
        while (n>=1000){
            n-=1000;
            roman+="M";
        }
        if (n>=900){
            n-=900;
            roman+="CM";
        }
        while (n>=500){
            n-=500;
            roman+="D";
        }
        if (n>=400){
            n-=400;
            roman+="CD";
        }
        while (n>=100){
            n-=100;
            roman+="C";
        }
        if (n>=90){
            n-=90;
            roman+="XC";
        }
        while (n>=50){
            n-=50;
            roman+="L";
        }
        if (n>=40){
            n-=40;
            roman+="XL";
        }
        while (n>=10){
            n-=10;
            roman+="X";
        }
        if (n==9){
            n-=9;
            roman+="IX";
        }
        if (n>=5){
            n-=5;
            roman+="V";
        }
        if (n==4){
            n-=4;
            roman+="IV";
        }
        while (n>=1){
            n--;
            roman+="I";
        }
        return roman;
    }

}
