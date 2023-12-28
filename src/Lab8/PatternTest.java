package Lab8;

import java.util.ArrayList;
import java.util.List;

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

//Vasiot kod ovde
class MP3Player{
    List<Song> songList;
    int currentSong;
    State play;
    State pause;
    State stop;
    State fwd;
    State rew;
    State currentState;
    public MP3Player(List<Song> songs) {
        this.songList = songs;
        currentSong = 0;
        createState();
    }
    final void createState(){
        play = new PLayState(this);
        pause = new PauseState(this);
        stop = new StopState(this);
        fwd = new FwdState(this);
        rew = new RewState(this);
        currentState = stop;
    }

    public State getPlay() {
        return play;
    }

    public State getPause() {
        return pause;
    }

    public State getStop() {
        return stop;
    }

    public State getFwd() {
        return fwd;
    }

    public State getRew() {
        return rew;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void pressPlay() {
        currentState.pressPlay();
    }

    public void printCurrentSong() {
        System.out.println(songList.get(currentSong));
    }

    public void pressREW() {
        currentState.pressREW();
    }

    public void pressFWD() {
        currentState.pressFWD();
    }

    public void pressStop() {
        currentState.pressStop();
    }

    public int getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }

    @Override
    public String toString() {
        return "MP3Player{" +
                "currentSong = " + currentSong +
                ", songList = " + songList;
    }

    public void songFWD(){
        currentSong = (currentSong+1)%songList.size();
    }
    public void songREW(){
        currentSong = (currentSong+songList.size()-1)%songList.size();
    }
}
interface State{
    void pressPlay();
    void pressStop();
    void pressFWD();
    void pressREW();
    void forward();
    void reward();
}
abstract class AbstractState implements State{
    MP3Player mp3Player;

    public AbstractState(MP3Player mp3Player) {
        this.mp3Player = mp3Player;
    }
}
class PLayState extends AbstractState{

    public PLayState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop() {
        System.out.println("Song "+mp3Player.getCurrentSong()+" is paused");
        mp3Player.setCurrentState(mp3Player.getPause());
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.setCurrentState(mp3Player.getFwd());
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.setCurrentState(mp3Player.getRew());
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void reward() {
        System.out.println("Illegal action");
    }
}
class StopState extends AbstractState{

    public StopState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song "+mp3Player.getCurrentSong()+" is playing");
        mp3Player.setCurrentState(mp3Player.getPlay());
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.setCurrentState(mp3Player.getFwd());
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.setCurrentState(mp3Player.getRew());
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void reward() {
        System.out.println("Illegal action");
    }
}
class RewState extends AbstractState{
    public RewState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressStop() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressFWD() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressREW() {
        System.out.println("Illegal action");
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void reward() {
        mp3Player.songREW();
        mp3Player.setCurrentState(mp3Player.getPause());
    }
}
class FwdState extends AbstractState{
    public FwdState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressStop() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressFWD() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressREW() {
        System.out.println("Illegal action");
    }

    @Override
    public void forward() {
        mp3Player.songFWD();
        mp3Player.setCurrentState(mp3Player.getPause());
    }

    @Override
    public void reward() {
        System.out.println("Illegal action");
    }
}
class PauseState extends AbstractState{
    public PauseState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + mp3Player.getCurrentSong() + " is playing");
        mp3Player.setCurrentState(mp3Player.getPlay());
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        mp3Player.setCurrentSong(0);
        mp3Player.setCurrentState(mp3Player.getStop());
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.setCurrentState(mp3Player.getFwd());
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.setCurrentState(mp3Player.getRew());
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void reward() {
        System.out.println("Illegal action");
    }
}
class Song{
    String title;
    String artist;
    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", artist=" + artist + '}';
    }
}