package Lab2;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;



public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}
abstract class Contact implements Comparable<Contact>{
    private String date;

    public Contact(String date) {
        this.date = date;
    }
    public boolean isNewerThan(Contact c){
        return this.date.compareTo(c.date)>0;
    }
    abstract String getType();

    @Override
    public int compareTo(Contact o) {
        return o.date.compareTo(this.date);
    }
}
class EmailContact extends Contact{
    private String email;

    public EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    @Override
    public String getType(){
        return "Email";
    }

    @Override
    public String toString() {
        return String.format("\"%s\"",email);
    }
}
class PhoneContact extends Contact{
    private String phone;

    public PhoneContact(String date, String phone) {
        super(date);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public Operator getOperator() {
        String operator = phone.substring(0,3);
        if (operator.equals("070")||operator.equals("071")||operator.equals("072"))
            return Operator.TMOBILE;
        if (operator.equals("075")||operator.equals("076"))
            return Operator.ONE;
        if (operator.equals("077") || operator.equals("078")) {
            return Operator.VIP;
        }
        return null;
    }
    @Override
    public String getType(){
        return "Phone";
    }

    @Override
    public String toString() {
        return String.format("\"%s\"",phone);
    }
}
enum Operator{
    VIP,
    ONE,
    TMOBILE
}
class Student {
    String firstName;
    private String lastName;
    private String city;
    private int age;
    private long index;
    private Contact[] contacts;
    public Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        contacts = new Contact[0];
    }

    public Contact[] getPhoneContacts() {
        return Arrays.stream(contacts).filter(contact -> contact.getType().equals("Phone")).toArray(Contact[]::new);
    }
    public Contact[] getEmailContacts(){
        return Arrays.stream(contacts).filter(contact -> contact.getType().equals("Email")).toArray(Contact[]::new);
    }

    public String getFullName() {
        return String.format("%s %s", firstName.toUpperCase(),lastName.toUpperCase());
    }

    public String getCity() {
        return city;
    }

    public long getIndex() {
        return index;
    }

    public void addEmailContact(String date, String email){
        Contact [] newList = Arrays.copyOf(contacts,contacts.length+1);
        newList[contacts.length]=new EmailContact(date,email);
        contacts=newList;
    }
    public void addPhoneContact(String date, String phone){
        Contact [] newList = Arrays.copyOf(contacts,contacts.length+1);
        newList[contacts.length]=new PhoneContact(date,phone);
        contacts=newList;

    }
    public Contact getLatestContact(){
        return Arrays.stream(contacts).sorted().findFirst().orElse(null);
    }
    public int getNumberOfContacts(){
        return contacts.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"ime\":\"%s\", ",firstName));
        sb.append(String.format("\"prezime\":\"%s\", ",lastName));
        sb.append(String.format("\"vozrast\":%d, ",age));
        sb.append(String.format("\"grad\":\"%s\", ",city));
        sb.append(String.format("\"indeks\":%d, ",index));
        sb.append(String.format("\"telefonskiKontakti\":")).append(Arrays.toString(getPhoneContacts())).append(", ");
        sb.append(String.format("\"emailKontakti\":")).append(Arrays.toString(getEmailContacts()));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && index == student.index && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(city, student.city) && Arrays.equals(contacts, student.contacts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(firstName, lastName, city, age, index);
        result = 31 * result + Arrays.hashCode(contacts);
        return result;
    }
}
class Faculty{
    private String name;
    Student [] students;

    public Faculty(String name, Student[] students) {
        this.name = name;
        this.students = Arrays.copyOf(students,students.length);
    }
    public int countStudentsFromCity(String cityName){
        return (int)Arrays.stream(students).filter(student -> student.getCity().equals(cityName)).count();
    }
    public Student getStudent(long index){
        return Arrays.stream(students).filter(student -> student.getIndex()==index).findFirst().orElse(null);
    }
    public double getAverageNumberOfContacts(){
        return Arrays.stream(students).mapToInt(Student::getNumberOfContacts).average().orElse(0.0);
    }
    public Student getStudentWithMostContacts(){
        return Arrays.stream(students).sorted(Comparator.comparing(Student::getNumberOfContacts).reversed().thenComparing(Comparator.comparing(Student::getIndex).reversed())).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{\"fakultet\":\"%s\", ",name));
        sb.append(("\"studenti\":")).append(Arrays.toString(students));
        sb.append("}");
        return sb.toString();
    }
}