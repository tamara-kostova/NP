package kolokviumski2;

import java.util.*;

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
class Sector {
    private String code;
    private Integer seats;
    Map<Integer, Integer> taken;
    Set<Integer> types;

    public Sector(String code, Integer seats) {
        this.code = code;
        this.seats = seats;
        taken = new HashMap<>();
        types = new HashSet<>();
    }

    public String getCode() {
        return code;
    }

    public int freeSeats(){
        return seats-taken.size();
    }
    public boolean isTaken(int seat){
        return taken.containsKey(seat);
    }

    public void takeSeat(int seat, int type) throws SeatNotAllowedException {
        if(types.contains(1)){
            if (type==2)
                throw new SeatNotAllowedException();
        }
        else if(types.contains(2)){
            if(type==1)
                throw new SeatNotAllowedException();
        }
        types.add(type);
        taken.put(seat,type);
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%",code,freeSeats(),seats,(seats-freeSeats())*100.0/seats);
    }
}
class Stadium{
    private String name;
    Map<String, Sector> sectors;

    public Stadium(String name) {
        this.name = name;
        sectors = new HashMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sectorSizes) {
        for (int i=0; i<sectorNames.length; i++){
            Sector toadd = new Sector(sectorNames[i],sectorSizes[i]);
            sectors.put(sectorNames[i],toadd);
        }
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatNotAllowedException, SeatTakenException{
        Sector sector = sectors.get(sectorName);
        if (sector.isTaken(seat))
            throw new SeatTakenException();
        sector.takeSeat(seat,type);
    }

    public void showSectors() {
        List<Sector> sectorList = new ArrayList<>();
        for (String sectorname : sectors.keySet()){
            sectorList.add(sectors.get(sectorname));
        }
        sectorList.stream().sorted(Comparator.comparing(Sector::freeSeats).reversed().thenComparing(Sector::getCode)).forEach(System.out::println);
    }
}
class SeatTakenException extends Exception{
    public SeatTakenException() {
        super();
    }
}
class SeatNotAllowedException extends Exception{
    public SeatNotAllowedException() {
        super();
    }
}