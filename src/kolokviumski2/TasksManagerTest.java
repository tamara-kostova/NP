package kolokviumski2;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
class TaskFactory{
    public static ITask createTask (String line) throws DeadlineNotValidException{
        //School,NP,lab 1 po NP,2020-06-23T23:59:59.000,1
        String [] parts = line.split(",");
        SimpleTask simpleTask = new SimpleTask(parts[0], parts[1], parts[2]);
        if (parts.length==3)
            return simpleTask;
        else if(parts.length==4){
            try{
                int priority = Integer.parseInt(parts[3]);
                return new PriorityTaskDecorator(simpleTask,priority);
            }catch (Exception e){
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                checkDeadline(deadline);
                return new TimeTaskDecorator(simpleTask, deadline);
            }
        }else{
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            checkDeadline(deadline);
            int priority = Integer.parseInt(parts[4]);
            return new PriorityTaskDecorator(new TimeTaskDecorator(simpleTask,deadline), priority);
        }
    }
    private static void checkDeadline(LocalDateTime deadline) throws DeadlineNotValidException {
        if (deadline.isBefore(LocalDateTime.of(2020, Month.JUNE,2,23,59)))
            throw new DeadlineNotValidException(deadline);
    }
}
class TaskManager{
    Map<String, List<ITask>> tasks;

    public TaskManager() {
        tasks = new TreeMap<>();
    }

    public void readTasks (InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        tasks = bufferedReader.lines().map(line->{
            try{
                return TaskFactory.createTask(line);
            }catch (DeadlineNotValidException exception){
                System.out.println(exception.getMessage());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.groupingBy(
                ITask::getCategory,
                TreeMap::new,
                Collectors.toList()
        ));
    }
    public void addTask(ITask task){
        tasks.putIfAbsent(task.getCategory(), new ArrayList<>());
        tasks.get(task.getCategory()).add(task);
    }
    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory){
        PrintWriter printWriter = new PrintWriter(os);
        Comparator<ITask> priority = Comparator.comparing(ITask::getPriority).thenComparing(task-> Duration.between(LocalDateTime.now(),task.getDeadline()));
        Comparator<ITask> simple = Comparator.comparing(task-> Duration.between(LocalDateTime.now(),task.getDeadline()));
        if (includeCategory){
            tasks.entrySet().forEach(
                    entry ->{
                        printWriter.println(entry.getKey().toUpperCase());
                        entry.getValue().stream().sorted(includePriority?priority:simple).forEach(printWriter::println);
                    }
            );
        }else{
            tasks.values().stream().flatMap(Collection::stream).sorted(includePriority?priority:simple).forEach(printWriter::println);
        }
        printWriter.flush();
    }
}
class SimpleTask implements ITask{
    String category;
    String name;
    String description;

    public SimpleTask(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getCategory() {
        return category;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Task{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'').append('}');
        return sb.toString();
    }
}
abstract class TaskDecorator implements ITask{
    ITask iTask;

    public TaskDecorator(ITask iTask) {
        this.iTask = iTask;
    }
}
class TimeTaskDecorator extends TaskDecorator {
    LocalDateTime deadline;

    public TimeTaskDecorator(ITask iTask, LocalDateTime deadline) {
        super(iTask);
        this.deadline = deadline;
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public int getPriority() {
        return iTask.getPriority();
    }

    @Override
    public String getCategory() {
        return iTask.getCategory();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(iTask.toString(),0,iTask.toString().length()-1);
        sb.append(", deadline=").append(deadline).append("}");
        return sb.toString();
    }
}
class PriorityTaskDecorator extends TaskDecorator {
    int priority;

    public PriorityTaskDecorator(ITask iTask, int priority) {
        super(iTask);
        this.priority = priority;
    }

    @Override
    public LocalDateTime getDeadline() {
        return iTask.getDeadline();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getCategory() {
        return iTask.getCategory();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(iTask.toString(),0,iTask.toString().length()-1);
        sb.append(", priority=").append(priority).append("}");
        return sb.toString();
    }
}
interface ITask{
    LocalDateTime getDeadline();
    int getPriority();
    String getCategory();
}
class DeadlineNotValidException extends Exception{
    public DeadlineNotValidException(LocalDateTime deadline) {
        super(String.format("The deadline %s has already passed",deadline));
    }
}