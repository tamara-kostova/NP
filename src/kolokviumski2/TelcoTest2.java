package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}


public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}
class TelcoApp {
    Map<String, List<Call>> callsByNymber;
    Map<String, List<Call>> callsByPair;
    Map<String, Call> callsByuuid;

    public TelcoApp() {
        callsByuuid = new HashMap<>();
        callsByNymber = new HashMap<>();
        callsByPair = new HashMap<>();
    }

    public void addCall(String uuid, String dialer, String receiver, long timestamp) {
        Call call = new Call(uuid,dialer,receiver,timestamp);
        callsByuuid.put(uuid,call);
        callsByNymber.putIfAbsent(dialer, new ArrayList<>());
        callsByNymber.get(dialer).add(call);
        callsByNymber.putIfAbsent(receiver, new ArrayList<>());
        callsByNymber.get(receiver).add(call);
        callsByPair.putIfAbsent(dialer+" "+receiver,new ArrayList<>());
        callsByPair.get(dialer+" "+receiver).add(call);
    }

    public void updateCall(String uuid, long timestamp, String action) {
        callsByuuid.get(uuid).updateCall(timestamp,action);
    }

    public void printChronologicalReport(String phoneNumber) {
        List<Call> callsByNumber = callsByNymber.get(phoneNumber);
        for (Call call : callsByNumber) {
            if (call.getDialer().equals(phoneNumber))
                System.out.println(call.printDialer());
            else
                System.out.println(call.printReceiver());
        }
    }

    public void printReportByDuration(String phoneNumber) {
        List<Call> callsByNumber = callsByNymber.get(phoneNumber).stream().sorted(Comparator.comparing(Call::getDuration).thenComparing(Call::getTimestampStart).reversed()).collect(Collectors.toList());
        for (Call call : callsByNumber) {
            if (call.getDialer().equals(phoneNumber))
                System.out.println(call.printDialer());
            else
                System.out.println(call.printReceiver());
        }
    }

    public void printCallsDuration() {
        Map<String,Integer> durations = new HashMap<>();
        for(String pair: callsByPair.keySet()){
            durations.put(pair,callsByPair.get(pair).stream().mapToInt(Call::getDuration).sum());
        }
        durations.keySet().stream().sorted((l,r)->Integer.compare(durations.get(r),durations.get(l))).forEach(
                pair -> System.out.printf("%s <-> %s : %s\n",pair.split(" ")[0],pair.split(" ")[1],DurationConverter.convert(durations.get(pair)))
        );
    }
}
class Call{
    String uuid;
    String dialer;
    String receiver;
    long timestamp;
    long timestampStart;
    long timestampEnd;
    boolean hold = false;
    boolean answered = false;
    long holdTimeStamp;
    int timeHeld;
    public Call(String uuid, String dialer, String receiver, long timestamp) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.timestamp = timestamp;
        timestampStart = timestamp;
        timestampEnd = 0;
        timeHeld = 0;
    }

    public String getDialer() {
        return dialer;
    }

    public long getTimestampStart() {
        return timestampStart;
    }

    public void updateCall(long timestamp, String action) {
        if (action.equals("ANSWER")){
            timestampStart = timestamp;
            answered = true;
        }
        if (action.equals("END")){
            if (hold)
                timeHeld+=(int) (timestamp-holdTimeStamp);
            if(!answered)
                timestampStart = timestamp;
            timestampEnd = timestamp;
        }
        if (action.equals("HOLD")) {
            hold = true;
            holdTimeStamp = timestamp;
        }
        if (action.equals("RESUME")){
            hold = false;
            timeHeld+= (int) (timestamp-holdTimeStamp);
        }

    }
    public String printDialer(){
        if (!answered)
            return String.format("D %s %s MISSED CALL 00:00",receiver,timestampStart);
        int seconds = (int) (timestampEnd-timestampStart-timeHeld);
        int minutes = seconds/60;
        seconds%=60;
        return String.format("D %s %s %s %02d:%02d",receiver,timestampStart,timestampEnd,minutes,seconds);
    }
    public String printReceiver(){
        if (!answered)
            return String.format("R %s %s MISSED CALL 00:00",dialer,timestampStart);
        int seconds = (int) (timestampEnd-timestampStart-timeHeld);
        int minutes = seconds/60;
        seconds%=60;
        return String.format("R %s %s %s %02d:%02d",dialer,timestampStart,timestampEnd,minutes,seconds);
    }

    public int getDuration(){
        if (answered)
            return (int) (timestampEnd - timestampStart-timeHeld);
        return 0;
    }
}