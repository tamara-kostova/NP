package Lab5;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.LinkedList;
@SuppressWarnings("unchecked")
public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if ( test == 0 ) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while ( jin.hasNextInt() ) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if ( test == 1 ) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for ( int i = 0 ; i < 4 ; ++i ) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if ( test == 2 ) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while ( jin.hasNextInt() ) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if ( a.sum() > 100 )
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if ( test == 3 ) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for ( int w = 0 ; w < 500 ; ++w ) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k =  2000;
                int t =  1000;
                for ( int i = 0 ; i < k ; ++i ) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for ( int i = 0 ; i < t ; ++i ) {
                    a.removeElement(k-i-1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}
@SuppressWarnings("unchecked")
class ResizableArray<T> {
    private T [] array;
    private int size;
    public ResizableArray() {
        array = (T[]) new Object[1];
        size = 0;
    }
    public void addElement(T element){
        if ( size == array.length )
            array = Arrays.copyOf(array, size<<1);
        array[size++] = element;
    }
    public boolean removeElement(T element){
        List<T> list = Arrays.asList(array);
        int index = list.indexOf(element);
        if (index==-1)
            return false;
        if (array.length == 1) {
            size = 0;
            array = (T[])new Object[1];
            return true;
        }
        array[index]=array[--size];
        return true;
    }
    public boolean contains(T element){
        List<T> list = Arrays.asList(array);
        int index = list.indexOf(element);
        return index!=-1;
    }
    public Object [] toArray(){
        return Arrays.copyOf(array,size);
    }
    public boolean isEmpty(){
        return size==0;
    }
    public int count(){
        return size;
    }
    public T elementAt(int idx){
        return array[idx];
    }
    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src){
        int count = src.count();
            for ( int k = 0 ; k < count ; ++k ) dest.addElement(src.elementAt(k));
    }
}
@SuppressWarnings("unchecked")
class IntegerArray extends ResizableArray<Integer>{
    public IntegerArray() {
        super();
    }

    public double sum(){
        int sum=0;
        Object [] a = toArray();
        for (Object obj : a){
            sum+=(Integer)obj;
        }
        return sum;
    }
    public double mean(){
        return sum()/count();
    }
    public int countNonZero(){
        int counter = 0;
        Object a[] = toArray();
        for (Object obj : a){
            if ((Integer)obj!=0)
                counter++;
        }
        return counter;
    }
    public IntegerArray distinct(){
        IntegerArray result = new IntegerArray();
        Object a[] = toArray();
        Arrays.sort(a);
        for (int i=0; i<a.length; i++) {
            for (; i < a.length - 1 && a[i] == a[i + 1]; ++i) ;
            result.addElement((Integer) a[i]);
        }
        return result;
    }
    public IntegerArray increment(int offset){
        IntegerArray result = new IntegerArray();
        Object a[] = toArray();
        for (Object obj : a)
            result.addElement((Integer)obj+offset);
        return result;
    }
}