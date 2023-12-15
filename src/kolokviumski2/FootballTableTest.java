package kolokviumski2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Partial exam II 2016/2017
 */
public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

class FootballTable{
    Map <String, Team> teams;

    public FootballTable() {
        teams = new HashMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teams.putIfAbsent(homeTeam, new Team(homeTeam));
        teams.putIfAbsent(awayTeam, new Team(awayTeam));
        teams.get(homeTeam).goalsMade += homeGoals;
        teams.get(homeTeam).goalsTaken+=awayGoals;
        teams.get(awayTeam).goalsMade += awayGoals;
        teams.get(awayTeam).goalsTaken+=homeGoals;
        teams.get(homeTeam).played += 1;
        teams.get(awayTeam).played+=1;
        if (homeGoals>awayGoals) {
            teams.get(homeTeam).wins += 1;
            teams.get(awayTeam).losses+=1;
        }
        else if (homeGoals<awayGoals){
            teams.get(homeTeam).losses+=1;
            teams.get(awayTeam).wins+=1;
        }
        else{
            teams.get(homeTeam).draws+=1;
            teams.get(awayTeam).draws+=1;
        }
    }
    public void printTable(){
        List<Team> teamList = teams.values().stream()
                        .sorted(Comparator.comparing(Team::points).thenComparing(Team::goalDifference).reversed().thenComparing(Team::getName))
                .collect(Collectors.toList());
        IntStream.range(0,teamList.size()).forEach(i->System.out.printf("%2d. %s\n",i+1,teamList.get(i)));
    }
}
class Team{
    String name;
    int played;
    int wins;
    int draws;
    int losses;
    int goalsMade;
    int goalsTaken;

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int points(){
        return wins*3+draws;
    }
    public int goalDifference(){
        return goalsMade-goalsTaken;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d",name,played,wins,draws,losses,points());
    }
}