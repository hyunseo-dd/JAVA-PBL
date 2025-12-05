import java.time.LocalDate;
public class Task extends TodoItem {
    public Task(String name, int priority, LocalDate dueDate) {
        super(name, priority, dueDate);
    }
}