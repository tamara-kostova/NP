package kolokviumski1;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
class ArchiveStore {
    private ArrayList<Archive> archives;
    private StringBuilder log;

    public ArchiveStore() {
        archives = new ArrayList<>();
        log = new StringBuilder();
    }

    public void archiveItem(Archive item, LocalDate date){
        item.archive(date);
        archives.add(item);
        log.append(String.format("Item %d archived at %s\n",item.getId(),date));
    }
    public void openItem(int id, LocalDate date) throws NonExistingItemException{
        if (!archives.stream().map(Archive::getId).anyMatch(a->a==id))
            throw new NonExistingItemException(id);
        try{
            Archive toArchive =  archives.stream().filter(item->item.getId()==id).findFirst().orElse(null);
            toArchive.openItem(date);
        }catch (CannotBeOpenedException ex){
            log.append(ex.getMessage());
            log.append("\n");
            return;
        }
        log.append(String.format("Item %d opened at %s\n",id,date));
    }
    public String getLog(){
        return log.toString();
    }
}
abstract class Archive {
    protected int id;
    protected LocalDate dateArchived;
    public int getId(){
        return id;
    }
    public void archive(LocalDate date) {
        this.dateArchived = date;
    }
    abstract void openItem(LocalDate date) throws CannotBeOpenedException;
}
class LockedArchive extends Archive{
    private LocalDate dateToOpen;
    public LockedArchive(int id, LocalDate dateToOpen) {
        this.id = id;
        this.dateToOpen = dateToOpen;
    }
    @Override
    void openItem(LocalDate date) throws CannotBeOpenedException {
        if (date.isBefore(dateToOpen))
            throw new CannotBeOpenedException(String.format("Item %d cannot be opened before %s",id, dateToOpen));
    }
}
class SpecialArchive extends Archive{
    private int maxOpen;
    private int count;
    public SpecialArchive(int id, int maxOpen) {
        this.id = id;
        count = 0;
        this.maxOpen = maxOpen;
    }

    @Override
    void openItem(LocalDate date) throws CannotBeOpenedException {
        if (count >= maxOpen)
            throw new CannotBeOpenedException(String.format("Item %d cannot be opened more than %d times",id, maxOpen));
        count++;
    }
}
class NonExistingItemException extends Exception{
    public NonExistingItemException(int id) {
        super(String.format("Item with id %d doesn't exist",id));
    }
}
class CannotBeOpenedException extends Exception{
    public CannotBeOpenedException(String message) {
        super(message);
    }
}