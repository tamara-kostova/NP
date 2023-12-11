package kolokviumski2;

import java.util.*;

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}
class Participant{
    private String code;
    private String name;
    private int age;

    public Participant(String code, String name, int age) {
        this.code = code;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d",code,name,age);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return code.equals(that.code);
    }

}
class Audition{
    Map<String, TreeSet<Participant>> participantsByCity = new HashMap<>();
    Map<String, HashSet<String>> codesByCity = new HashMap<>();
    public void addParticpant(String city, String code, String name, int age) {
        participantsByCity.putIfAbsent(city, new TreeSet<>(Comparator.comparing(Participant::getName).thenComparing(Participant::getAge).thenComparing(Participant::getCode)));
        codesByCity.putIfAbsent(city, new HashSet<>());
        Participant participant = new Participant(code,name,age);
        if(!codesByCity.get(city).contains(participant.getCode()))
            participantsByCity.get(city).add(participant);
        codesByCity.get(city).add(participant.getCode());

    }
    public void listByCity(String city) {
        participantsByCity.get(city).forEach(participant -> System.out.println(participant));

    }
}
