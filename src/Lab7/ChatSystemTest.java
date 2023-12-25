package Lab7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }
    }

}
class ChatRoom{
    String name;
    Set<String> users;
    public ChatRoom(String name){
        this.name = name;
        users = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        if (users.isEmpty())
            sb.append("EMPTY\n");
        else
            users.forEach(user-> sb.append(user).append("\n"));
        return sb.toString();
    }

    public int numUsers(){
        return users.size();
    }
}
class ChatSystem{
    Map<String, ChatRoom> chatrooms;
    Set<String> allUsers;
    public ChatSystem() {
        chatrooms = new TreeMap<>();
        allUsers = new HashSet<>();
    }

    public void addRoom(String roomName){
        chatrooms.put(roomName,new ChatRoom(roomName));
    }
    public void removeRoom(String roomName){
        chatrooms.remove(roomName);
    }
    public ChatRoom getRoom(String roomName) throws NoSuchRoomException{
        if (chatrooms.get(roomName)==null)
            throw new NoSuchRoomException(roomName);
        return chatrooms.get(roomName);
    }
    public void register(String userName){
        allUsers.add(userName);
        Optional<ChatRoom> minusers = chatrooms.values().stream()
                .sorted(Comparator.comparing(ChatRoom::numUsers).thenComparing(ChatRoom::getName)).findFirst();
        if (minusers.isPresent())
            minusers.get().addUser(userName);
    }
    public void registerAndJoin(String userName, String roomName){
        allUsers.add(userName);
        chatrooms.get(roomName).addUser(userName);
    }
    public void joinRoom(String userName, String roomName) throws NoSuchRoomException,NoSuchUserException{
        if (!chatrooms.containsKey(roomName))
            throw new NoSuchRoomException(roomName);
        if (!allUsers.contains(userName))
            throw new NoSuchUserException(userName);
        chatrooms.get(roomName).addUser(userName);
    }
    public void leaveRoom(String username, String roomName) throws NoSuchRoomException,NoSuchUserException{
        if (!chatrooms.containsKey(roomName))
            throw new NoSuchRoomException(roomName);
        if (!allUsers.contains(username))
            throw new NoSuchUserException(username);
        chatrooms.get(roomName).removeUser(username);
    }
    public void followFriend(String username, String friend_username) throws NoSuchUserException{
        if (!allUsers.contains(friend_username))
            throw new NoSuchUserException(friend_username);
        chatrooms.values().stream().filter(cr->cr.hasUser(friend_username)).forEach(room->room.addUser(username));
    }
}
class NoSuchRoomException extends Exception{
    public NoSuchRoomException(String roomName) {
        super(String.format("Room %s doesn't exist",roomName));
    }
}
class NoSuchUserException extends Exception{
    public NoSuchUserException(String username) {
        super(String.format("User %s doesn't exist",username));
    }
}