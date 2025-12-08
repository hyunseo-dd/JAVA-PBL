import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TodoItem {
    private UUID id;
    private String name;
    private int priority;
    private LocalDate dueDate;
    private boolean isCompleted;
    private LocalDateTime createdAt;

    public TodoItem(String name, int priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
    }
    public void markAsCompleted() {
        this.isCompleted = true;
        System.out.printf("'%s' (ID: %s) 완료 성공!\n", this.name, this.id.toString().substring(0, 4));
    }
    public void updateItem(String newName, Integer newPriority, LocalDate newDueDate) {
        if (newName != null) {
            this.name = newName;
        }
        if (newPriority != null) {
            this.priority = newPriority;
        }
        if (newDueDate != null) {
            this.dueDate = newDueDate;
        }
        System.out.printf("'%s' (ID: %s) 할 일이 업데이트 되었습니다!\n", this.name, this.id.toString().substring(0, 4));
    }
    @Override
    public String toString() {
        String status = isCompleted? "✅" : "🔲";
        String dueDateStr = (dueDate != null)? "~" + dueDate.toString() : "기한 없음";
        return String.format("%s [%d] %s (%s)", status, priority, name, dueDateStr);
    }
    public UUID getId() {return id;}
    public String getName() {return name;}
    public int getPriority() {return priority;}
    public LocalDate getDueDate() {return dueDate;}
    public boolean isCompleted() {return isCompleted;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setName(String name) {this.name = name;}
    public void setPriority(int priority) {this.priority = priority;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}
    public void setCompleted(boolean completed) {isCompleted = completed;}
}