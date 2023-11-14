package kolokviumski1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}

// Вашиот код овде
class Subtitles{
    List<SubtitleLine> subtitleLines;
    public Subtitles() {
        subtitleLines = new ArrayList<>();
    }

    public int loadSubtitles(InputStream in) {
        int counter = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = br.lines().collect(Collectors.toList());
        for (int i=0; i<lines.size()-1; ){
            int number = Integer.parseInt(lines.get(i++));
            String time = lines.get(i++);
            StringBuilder text = new StringBuilder();
            while (true) {
                if(i==lines.size())
                    break;
                String line = lines.get(i++);
                if (line.trim().length() == 0)
                    break;
                text.append(line);
                text.append("\n");
            }
            subtitleLines.add(new SubtitleLine(number,time,text.toString()));
            counter++;
        }
        return counter;
    }

    public void print() {
        for (SubtitleLine line : subtitleLines)
            System.out.printf(line.toString());
    }

    public void shift(int shift) {
        for (SubtitleLine line : subtitleLines)
            line.shift(shift);
    }
}
class SubtitleLine {
    public int number;
    public String line;
    public int from;
    public int to;
    public String timeFrom;
    public String timeTo;

    public SubtitleLine(int number, String time, String line) {
        this.number = number;
        this.line = line;
        timeFrom = time.split("-->")[0].trim();
        timeTo = time.split("-->")[1].trim();
        from = toIntTime(timeFrom);
        to = toIntTime(timeTo);
    }
    static int toIntTime(String time){
        //00:01:53,468
        String [] parts = time.split(",");
        int ms = Integer.parseInt(parts[1]);
        String [] hms = parts[0].split(":");
        int hours = Integer.parseInt(hms[0]);
        int minutes = Integer.parseInt(hms[1]);
        int seconds = Integer.parseInt(hms[2]);
        return hours*3600000+minutes*60000+seconds*1000+ms;
    }

    static String toStringTime(int miliseconds){
        //00:01:53,468
        int hours = miliseconds / 3600000;
        miliseconds %= 3600000;
        int minutes = miliseconds / 60000;
        miliseconds %= 60000;
        int seconds = miliseconds / 1000;
        miliseconds %= 1000;
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, miliseconds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(number).append("\n");
        sb.append(toStringTime(from)).append(" --> ").append(toStringTime(to)).append("\n");
        sb.append(line).append("\n");
        return sb.toString();
    }

    public void shift(int shift) {
        from+=shift;
        to+=shift;
    }
}