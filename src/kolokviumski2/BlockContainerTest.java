package kolokviumski2;

import java.util.*;

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}

// Вашиот код овде
class BlockContainer<T extends Comparable<T>>{
    List<Set<T>> blocks;
    int n;
    public BlockContainer(int n){
        this.n = n;
        blocks = new ArrayList<Set<T>>();
    }
    public void add(T a){
        if(blocks.size()==0){
            Set<T> first = new TreeSet<T>();
            first.add(a);
            blocks.add(first);
        }else{
            if (blocks.get(blocks.size()-1).size()<n){
                blocks.get(blocks.size()-1).add(a);
            }else{
                Set <T> newSet = new TreeSet<T>();
                newSet.add(a);
                blocks.add(newSet);
            }
        }
    }
    public boolean remove(T a){
        boolean res = false;
        if (blocks.size()>0){
            Set<T> s = blocks.get(blocks.size()-1);
            res = s.remove(a);
            if (s.size()==0){
                blocks.remove(blocks.size()-1);
            }
         }
        return res;
    }
    public void sort(){
        List<T> elements = new ArrayList<>();
        blocks.stream().forEach(set->set.stream().forEach(el->elements.add(el)));
        Collections.sort(elements);
        blocks = new ArrayList<Set<T>>();
        elements.forEach(el->add(el));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); ++i) {
            sb.append(blocks.get(i).toString());
            if (i < blocks.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}



