package kolokviumski2;

import java.util.*;

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

// Вашиот код овде

class PhoneBook{
    HashSet<String> allNumbers;
    Map<String, TreeSet<Contact>> contactsByNameMap;
    Map<String, TreeSet<Contact>> contactsByNumberMap;

    public PhoneBook() {
        allNumbers = new HashSet<>();
        contactsByNameMap = new HashMap<>();
        contactsByNumberMap = new HashMap<>();
    }

    private List<String> getSubStrings(String number){
        List <String> result = new ArrayList<>();
        for (int len = 3; len<=number.length();len++){
            for (int j=0; j<=number.length()-len;j++){
                result.add(number.substring(j,j+len));
            }
        }
        return result;
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if (allNumbers.contains(number)){
            throw new DuplicateNumberException(String.format("Duplicate number: %s",number));
        }
        contactsByNameMap.putIfAbsent(name,new TreeSet<>(Comparator.comparing(Contact::getName).thenComparing(Contact::getNumber)));
        Contact toadd = new Contact(name,number);
        List<String> substrings = getSubStrings(number);
        for (String substring : substrings) {
            contactsByNumberMap.putIfAbsent(substring,new TreeSet<>(Comparator.comparing(Contact::getName).thenComparing(Contact::getNumber)));
            contactsByNumberMap.get(substring).add(toadd);
        }
        contactsByNameMap.get(name).add(toadd);
        allNumbers.add(number);
    }

    public void contactsByNumber(String number) {
        if (!contactsByNumberMap.containsKey(number))
            System.out.println("NOT FOUND");
        else
            contactsByNumberMap.get(number).forEach(contact -> System.out.println(contact));
    }

    public void contactsByName(String name) {
        if (!contactsByNameMap.containsKey(name))
            System.out.println("NOT FOUND");
        else
            contactsByNameMap.get(name).forEach(contact -> System.out.println(contact));
    }
}
class Contact{
    private String name;
    private String number;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return String.format("%s %s",name,number);
    }
}
class DuplicateNumberException extends Exception{
    public DuplicateNumberException(String message) {
        super(message);
    }
}