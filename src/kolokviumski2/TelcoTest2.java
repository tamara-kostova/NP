package kolokviumski2;

import java.time.Duration;
import java.time.LocalDateTime;
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
class TelcoApp{
    Map<String, Call> callsByUuid;
    Map<String, List<Call>> callsByNymber;
    Map<String, List<Call>> callsByPair;

    public TelcoApp() {
        callsByUuid = new HashMap<>();
        callsByNymber = new HashMap<>();
        callsByPair = new HashMap<>();
    }

    public void addCall (String uuid, String dialer, String receiver, long timestamp){
        Call call = new Call(uuid,dialer,receiver,timestamp);
        callsByUuid.put(uuid,call);
        callsByNymber.putIfAbsent(dialer, new ArrayList<>());
        callsByNymber.get(dialer).add(call);
        callsByNymber.putIfAbsent(receiver, new ArrayList<>());
        callsByNymber.get(receiver).add(call);
        callsByPair.putIfAbsent(dialer+" "+receiver,new ArrayList<>());
        callsByPair.get(dialer+" "+receiver).add(call);
    }
    public void updateCall (String uuid, long timestamp, String action){
        callsByUuid.get(uuid).updateCall(timestamp,action);
    }
    public void printChronologicalReport(String phoneNumber) {
        List<Call> callsByNumber = callsByNymber.get(phoneNumber);
        callsByNumber.forEach(call -> {
            if(call.dialer.equals(phoneNumber)) {
                if (call.getDuration()==0)
                    System.out.println(String.format("D %s %d MISSED CALL %s", call.receiver, call.timeStampStarted, DurationConverter.convert(call.getDuration())));
                else
                    System.out.println(String.format("D %s %d %d %s", call.receiver, call.timeStampStarted, call.timeStampEnded, DurationConverter.convert(call.getDuration())));
            }
            else {
                if (call.getDuration()==0)
                    System.out.println(String.format("R %s %d MISSED CALL %s", call.dialer, call.timeStampStarted, DurationConverter.convert(call.getDuration())));
                else
                    System.out.println(String.format("R %s %d %d %s", call.dialer, call.timeStampStarted, call.timeStampEnded, DurationConverter.convert(call.getDuration())));
            }
        });
    }
    public void printReportByDuration(String phoneNumber){
        List<Call> callsByNumber = callsByNymber.get(phoneNumber).stream().sorted(Comparator.comparing(Call::getDuration).thenComparing(Call::getTimeStampStarted).reversed()).collect(Collectors.toList());
        callsByNumber.forEach(call -> {
            if(call.dialer.equals(phoneNumber)) {
                if (call.getDuration()==0)
                    System.out.println(String.format("D %s %d MISSED CALL %s", call.receiver, call.timeStampStarted, DurationConverter.convert(call.getDuration())));
                else
                    System.out.println(String.format("D %s %d %d %s", call.receiver, call.timeStampStarted, call.timeStampEnded, DurationConverter.convert(call.getDuration())));
            }
            else {
                if (call.getDuration()==0)
                    System.out.println(String.format("R %s %d MISSED CALL %s", call.dialer, call.timeStampStarted, DurationConverter.convert(call.getDuration())));
                else
                    System.out.println(String.format("R %s %d %d %s", call.dialer, call.timeStampStarted, call.timeStampEnded, DurationConverter.convert(call.getDuration())));
            }
        });
    }
    public void printCallsDuration(){
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
    long timeStampCalled;
    long timeStampStarted;
    long timeStampEnded;
    long holdStarted = -1;
    int totalTimeInHold = 0;
    CallState callState = new CallStartedState(this);

    public Call(String uuid, String dialer, String receiver, long timeStampCalled) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.timeStampCalled = timeStampCalled;
    }

    public long getTimeStampStarted() {
        return timeStampStarted;
    }

    public void updateCall(long timeStamp, String action){
        if (action.equals("ANSWER"))
            answer(timeStamp);
        if (action.equals("END"))
            end(timeStamp);
        if (action.equals("HOLD"))
            hold(timeStamp);
        if (action.equals("RESUME"))
            resume(timeStamp);
    }
    public int getDuration(){
        return (int) (timeStampEnded-timeStampStarted-totalTimeInHold);
    }
    public void answer(long timeStamp){
        callState.answer(timeStamp);
    }
    public void end(long timeStamp){
        callState.end(timeStamp);
    }
    public void hold(long timeStamp){
        callState.hold(timeStamp);
    }
    public void resume(long timeStamp){
        callState.resume(timeStamp);
    }
}
interface ICallState{
    void answer(long timestamp);
    void hold (long timestamp);
    void resume (long timestamp);
    void end (long timestamp);
}
abstract class CallState implements ICallState{
    Call call;

    public CallState(Call call) {
        this.call = call;
    }
}
class CallStartedState extends CallState{
    public CallStartedState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        call.timeStampStarted = timestamp;
        call.callState = new InProgressCallState(call);
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void end(long timestamp) {
        call.timeStampEnded = timestamp;
        call.timeStampStarted = timestamp;
        call.callState = new TerminatedCallState(call);
    }
}
class TerminatedCallState extends CallState{
    public TerminatedCallState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void end(long timestamp) {
        throw new RuntimeException();
    }
}
class InProgressCallState extends CallState{
    public InProgressCallState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        call.holdStarted = timestamp;
        call.callState = new PausedCallState(call);
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void end(long timestamp) {
        call.timeStampEnded = timestamp;
        call.callState = new TerminatedCallState(call);
    }
}
class PausedCallState extends CallState{
    public PausedCallState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        call.totalTimeInHold+=(timestamp-call.holdStarted);
        call.callState = new InProgressCallState(call);
    }

    @Override
    public void end(long timestamp) {
        call.totalTimeInHold+=(timestamp-call.holdStarted);
        call.timeStampEnded = timestamp;
        call.callState = new TerminatedCallState(call);
    }
}