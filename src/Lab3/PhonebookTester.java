package Lab3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class PhonebookTester {
    public static void main(String[] args) throws Exception {
        Scanner jin = new Scanner(System.in);
        String line = jin.nextLine();
        switch( line ) {
            case "test_contact":
                testContact(jin);
                break;
            case "test_phonebook_exceptions":
                testPhonebookExceptions(jin);
                break;
            case "test_usage":
                testUsage(jin);
                break;
        }
    }

    private static void testFile(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() )
            phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
        String text_file = "phonebook.txt";
        PhoneBook.saveAsTextFile(phonebook,text_file);
        PhoneBook pb = PhoneBook.loadFromTextFile(text_file);
        if ( ! pb.equals(phonebook) ) System.out.println("Your file saving and loading doesn't seem to work right");
        else System.out.println("Your file saving and loading works great. Good job!");
    }

    private static void testUsage(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() ) {
            String command = jin.nextLine();
            switch ( command ) {
                case "add":
                    phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
                    break;
                case "remove":
                    phonebook.removeContact(jin.nextLine());
                    break;
                case "print":
                    System.out.println(phonebook.numberOfContacts());
                    System.out.println(Arrays.toString(phonebook.getContacts()));
                    System.out.println(phonebook.toString());
                    break;
                case "get_name":
                    System.out.println(phonebook.getContactForName(jin.nextLine()));
                    break;
                case "get_number":
                    System.out.println(Arrays.toString(phonebook.getContactsForNumber(jin.nextLine())));
                    break;
            }
        }
    }

    private static void testPhonebookExceptions(Scanner jin) {
        PhoneBook phonebook = new PhoneBook();
        boolean exception_thrown = false;
        try {
            while ( jin.hasNextLine() ) {
                phonebook.addContact(new Contact(jin.nextLine()));
            }
        }
        catch ( InvalidNameException e ) {
            System.out.println(e.name);
            exception_thrown = true;
        }
        catch ( Exception e ) {}
        if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw InvalidNameException");
        /*
		exception_thrown = false;
		try {
		phonebook.addContact(new Contact(jin.nextLine()));
		} catch ( MaximumSizeExceddedException e ) {
			exception_thrown = true;
		}
		catch ( Exception e ) {}
		if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw MaximumSizeExcededException");
        */
    }

    private static void testContact(Scanner jin) throws Exception {
        boolean exception_thrown = true;
        String names_to_test[] = { "And\nrej","asd","AAAAAAAAAAAAAAAAAAAAAA","Ð�Ð½Ð´Ñ€ÐµÑ˜A123213","Andrej#","Andrej<3"};
        for ( String name : names_to_test ) {
            try {
                new Contact(name);
                exception_thrown = false;
            } catch (InvalidNameException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNameException");
        }
        String numbers_to_test[] = { "+071718028","number","078asdasdasd","070asdqwe","070a56798","07045678a","123456789","074456798","073456798","079456798" };
        for ( String number : numbers_to_test ) {
            try {
                new Contact("Andrej",number);
                exception_thrown = false;
            } catch (InvalidNumberException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNumberException");
        }
        String nums[] = new String[10];
        for ( int i = 0 ; i < nums.length ; ++i ) nums[i] = getRandomLegitNumber();
        try {
            new Contact("Andrej",nums);
            exception_thrown = false;
        } catch (MaximumSizeExceddedException e) {
            exception_thrown = true;
        }
        if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw a MaximumSizeExceddedException");
        Random rnd = new Random(5);
        Contact contact = new Contact("Andrej",getRandomLegitNumber(rnd),getRandomLegitNumber(rnd),getRandomLegitNumber(rnd));
        System.out.println(contact.getName());
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
    }

    static String[] legit_prefixes = {"070","071","072","075","076","077","078"};
    static Random rnd = new Random();

    private static String getRandomLegitNumber() {
        return getRandomLegitNumber(rnd);
    }

    private static String getRandomLegitNumber(Random rnd) {
        StringBuilder sb = new StringBuilder(legit_prefixes[rnd.nextInt(legit_prefixes.length)]);
        for ( int i = 3 ; i < 9 ; ++i )
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }


}
class PhoneBook{
    private Contact [] contacts;
    private int size;
    public PhoneBook() {
        contacts = new Contact[250];
        size = 0;
    }
    public void addContact(Contact contact) throws InvalidNameException, MaximumSizeExceddedException {
        if (getContactForName(contact.getName())!=null)
            throw new InvalidNameException(contact.getName());
        if (size==250)
            throw new MaximumSizeExceddedException();
        contacts[size++]=contact;
    }
    public Contact getContactForName(String name){
        int index = getContactIndex(name);
        return index==-1?null:contacts[index];
    }
    public int numberOfContacts(){
        return size;
    }
    public Contact [] getContacts(){
        Contact [] res = Arrays.copyOf(contacts,size);
        Arrays.sort(res);
        return res;
    }
    private int getContactIndex(String name) {
        for (int i=0 ; i < size; ++i )
            if (contacts[i] != null)
                if ( contacts[i].getName().equals(name))
                    return i;
        return -1;
    }
    public boolean removeContact(String name){
        int index = getContactIndex(name);
        if (index != -1) {
            contacts[index] = contacts[size-1];
            contacts[size--] = null;
        }
        return index!=-1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Contact c : getContacts())
            sb.append(c).append("\n");
        return sb.toString();
    }
    public static boolean saveAsTextFile(PhoneBook phonebook,String path){
        try(PrintWriter out = new PrintWriter(new File(path))){
            out.println(phonebook.toString());
        }
        catch (IOException ex){
            return false;
        }
        return true;
    }
    public static PhoneBook loadFromTextFile(String path) throws IOException, InvalidFormatException{
        try(Scanner in = new Scanner(new File(path))){
            PhoneBook phoneBook = new PhoneBook();
            StringBuilder sb = new StringBuilder();
            while (in.hasNextLine()){
                String line = in.nextLine();
                if (line.length()==0){
                    if(sb.toString().length()>1){
                        phoneBook.addContact(Contact.valueOf(sb.toString()));
                        sb = new StringBuilder();
                    }
                    else {
                        sb.append(line).append("\n");
                    }
                }
            }
            return phoneBook;
        }
        catch (InvalidNameException|MaximumSizeExceddedException e){
            throw new InvalidFormatException();
        }
    }
    public Contact [] getContactsForNumber(String number_prefix){
        Contact [] res = new Contact[size];
        int j = 0;
        for (int i =0; i<size; i++){
            if (contacts[i].NumberThatStartsWith(number_prefix))
                res[j++]=contacts[i];
        }
        res = Arrays.copyOf(res,j);
        return res;
    }
}
class Contact implements Comparable<Contact>{
    private String name;
    private String[] phonenumbers;
    private int size;
    public Contact(String name, String ... phonenumbers) throws InvalidNumberException,InvalidNameException,MaximumSizeExceddedException{
        if ( name.length() <= 4 || name.length() >= 10 )
            throw new InvalidNameException(name);
        for (int i=0; i<name.length(); ++i)
            if (!Character.isLetterOrDigit((name.charAt(i))))
                throw new InvalidNameException(name);
        this.name = name;
        this.phonenumbers = new String[5];
        size = 0;
        for (String number : phonenumbers)
            addNumber(number);
    }

    public String getName() {
        return name;
    }

    public String[] getNumbers() {
        String [] res = Arrays.copyOf(phonenumbers,size);
        Arrays.sort(res);
        return res;
    }
    public void addNumber(String phonenumber) throws InvalidNumberException, MaximumSizeExceddedException{
        String prefix = phonenumber.substring(0,3);
        if ( phonenumber == null || phonenumber.length() != 9)
            throw new InvalidNumberException();
        if (!prefix.equals("070")&&!prefix.equals("071")&&!prefix.equals("072")&&!prefix.equals("075")&&!prefix.equals("076")&&!prefix.equals("077")&!prefix.equals("078"))
            throw new InvalidNumberException();
        for ( int i = 3 ; i < phonenumber.length() ; ++i )
            if ( ! Character.isDigit(phonenumber.charAt(i)) )
                throw new InvalidNumberException();
        if (size==5)
            throw new MaximumSizeExceddedException();
        phonenumbers[size++]=phonenumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('\n');
        sb.append(size).append('\n');
        for (String number : getNumbers())
            sb.append(number).append('\n');
        return sb.toString();
    }
    public static Contact valueOf(String s) throws InvalidFormatException{
        String [] lines = s.split("\n");
        try{
            return new Contact(lines[0],Arrays.copyOfRange(lines,2,lines.length));
        }
        catch (InvalidNameException | InvalidNumberException | MaximumSizeExceddedException ex){
            throw new InvalidFormatException();
        }
    }

    public boolean NumberThatStartsWith(String numberPrefix) {
        for (int i=0; i<size; i++)
            if (phonenumbers[i].startsWith(numberPrefix))
                return true;
        return false;
    }

    @Override
    public int compareTo(Contact o) {
        return name.compareTo(o.getName());
    }

}
class InvalidNameException extends Exception{
    String name;
    public InvalidNameException(String name) {
        this.name = name;
    }
}
class InvalidNumberException extends Exception{
    public InvalidNumberException() {
        super("Invalid Number Exception");
    }
}
class MaximumSizeExceddedException extends Exception{
    public MaximumSizeExceddedException() {
        super("Maximum Size Exceeded Exception");
    }
}
class InvalidFormatException extends Exception{
    public InvalidFormatException(){
        super("Invalid Format Exception");
    }
}