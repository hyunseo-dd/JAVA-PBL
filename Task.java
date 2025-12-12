import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Serializable {

    private static final long serialVersionUID = 3L;

    // --- 캘린더/일정 필드 ---
    private UUID id;
    private String title; 
    private int priority; 
    private LocalDate dueDate; 
    private boolean isCompleted; 
    
    // --- 통계/루틴 필드 ---
    private int durationSec; 
    private String evaluation; 
    private LocalDateTime recordDateTime; 
    private String cycleFrequency = "NONE"; 

    // [통합 생성자 1]: 일정 등록용
    public Task(String title, int priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.durationSec = 0;
        this.recordDateTime = null; 
    }

    // [통합 생성자 2]: 통계 기록용 (TaskRecord 대체)
    public Task(String name, int durationSec, String evaluation, boolean completed) {
        this.id = UUID.randomUUID();
        this.title = name;
        this.priority = 0;
        this.dueDate = LocalDate.now(); 
        this.isCompleted = completed;
        this.durationSec = durationSec;
        this.evaluation = evaluation;
        this.recordDateTime = LocalDateTime.now();
    }
    
    // --- Getter ---
    public String getId() { return id.toString(); }
    public String getTitle() { return title; }
    public int getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return isCompleted; }
    
    public int getDurationSec() { return durationSec; }
    public LocalDateTime getRecordDateTime() { return recordDateTime; }
    public String getEvaluation() { return evaluation; }
    public String getCycleFrequency() { return cycleFrequency; }
    
    // --- Setter ---
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public void setEvaluation(String evaluation) { this.evaluation = evaluation; }
    public void setCycleFrequency(String cycleFrequency) { this.cycleFrequency = cycleFrequency; }
    public void setTitle(String title) { this.title = title; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    @Override
    public String toString() {
        if (recordDateTime != null) {
            // 통계용 출력
            return String.format("%s (%d초, %s)", title, durationSec, isCompleted ? "완료" : "미완료");
        }
        // 일정용 출력
        return String.format("%s [%d] %s (~%s)", isCompleted ? "✅" : "🔲", priority, title, dueDate);
    }
}