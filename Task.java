import java.time.LocalDate;

public class Task {

    public String title;
    public int priority;
    public LocalDate date;
    public boolean completed;

    public Task(String title, int priority, LocalDate date) {
        this.title = title;
        this.priority = priority;
        this.date = date;
        this.completed = false;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }
}