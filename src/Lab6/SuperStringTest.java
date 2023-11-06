package Lab6;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class SuperStringTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (  k == 0 ) {
            SuperString s = new SuperString();
            while ( true ) {
                int command = jin.nextInt();
                if ( command == 0 ) {//append(String s)
                    s.append(jin.next());
                }
                if ( command == 1 ) {//insert(String s)
                    s.insert(jin.next());
                }
                if ( command == 2 ) {//contains(String s)
                    System.out.println(s.contains(jin.next()));
                }
                if ( command == 3 ) {//reverse()
                    s.reverse();
                }
                if ( command == 4 ) {//toString()
                    System.out.println(s);
                }
                if ( command == 5 ) {//removeLast(int k)
                    s.removeLast(jin.nextInt());
                }
                if ( command == 6 ) {//end
                    break;
                }
            }
        }
    }

}
class SuperString{
    private LinkedList<String> list;
    private LinkedList<String> last;
    public SuperString() {
        list = new LinkedList<>();
        last = new LinkedList<>();
    }
    public void append(String s){
        list.add(s);
        last.add(s);
    }
    public void insert(String s){
        list.addFirst(s);
        last.add(s);
    }
    public boolean contains(String s){
        return toString().contains(s);
    }
    public void reverse(){
        Collections.reverse(list);
        reverseStrings(list);
        reverseStrings(last);
    }
    public void reverseStrings(LinkedList<String> strings){
        for (String s: strings){
            StringBuilder sb = new StringBuilder();
            sb.append(s);
            sb.reverse();
            list.add(sb.toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : list)
            sb.append(s);
        return sb.toString();
    }
    public void removeLast(int k){
        for (int i=0; i<k; i++) {
            String toremove = last.remove(last.size()-1);
            list.remove(toremove);
        }
    }
}