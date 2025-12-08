import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private int priority;
    private String dueDate; // yyyy-MM-dd
    private boolean completed;
    private String cycleFrequency; // NONE, DAILY, WEEKLY, MONTHLY
    private LocalDateTime createdDateTime;
    private LocalDateTime lastCompletedDateTime;

    // 생성자
    public Task(String title, int priority, String dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
        this.cycleFrequency = "NONE";
        this.createdDateTime = LocalDateTime.now();
        this.lastCompletedDateTime = null;
    }

    // 파일 로드용 생성자 (선택 사항, 필요시 추가)
    public Task(String id, String title, int priority, String dueDate, boolean completed, String cycleFrequency, LocalDateTime createdDateTime, LocalDateTime lastCompletedDateTime) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
        this.cycleFrequency = cycleFrequency;
        this.createdDateTime = createdDateTime;
        this.lastCompletedDateTime = lastCompletedDateTime;
    }

    // (U) 내용 수정 로직
    public void updateItem(String newTitle, Integer newPriority, String newDueDate, String newCycle) {
        if (newTitle != null && !newTitle.isEmpty()) {
            this.title = newTitle;
        }
        if (newPriority != null) {
            this.priority = newPriority;
        }
        if (newDueDate != null && !newDueDate.isEmpty()) {
            this.dueDate = newDueDate;
        }
        if (newCycle != null && !newCycle.isEmpty()) {
            this.cycleFrequency = newCycle;
        }
    }

    // --- Getter & Setter ---
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public int getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.lastCompletedDateTime = LocalDateTime.now();
        }
    }

    public String getCycleFrequency() {
        return cycleFrequency;
    }

    public void setCycleFrequency(String cycleFrequency) {
        this.cycleFrequency = cycleFrequency;
    }
    
    // toString 오버라이딩 (디버깅/출력용)
    @Override
    public String toString() {
        String cycle = cycleFrequency.equals("NONE") ? "" : " (반복: " + cycleFrequency + ")";
        String status = completed ? "✅ 완료" : "🔴 미완료";
        return String.format("[%s] %s (P%d) 마감: %s%s %s", 
            id.substring(0, 4), title, priority, dueDate, cycle, status);
    }
}