import java.time.LocalDate;
import java.time.LocalDateTime;

public class Routine extends TodoItem {
    private String interval;
    private LocalDateTime lastCompleted;
    public Routine(String name, String interval, int priority, LocalDate dueDate) {
        super(name, priority, dueDate);
        this.interval = interval;
        this.lastCompleted = null;
    }
    @Override
    public void markAsCompleted() {
        super.markAsCompleted();
        this.lastCompleted = LocalDateTime.now();
        System.out.printf("다음 할일은 '%s' 주기에 따라 생성될 예정입니다.\n", this.interval);
    }
    @Override
    public String toString() {
        String baseStr = super.toString();
        return String.format("%s [반복: %s]", baseStr, interval);
    }
    public String getInterval() {return interval;}
    public LocalDateTime  getLastCompleted() {return lastCompleted;}
    
}