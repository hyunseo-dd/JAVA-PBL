import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Serializable {

    private static final long serialVersionUID = 3L;

    // --- 캘린더/일정 필드 (Tassk.java 및 TaskService에서 사용) ---
    private UUID id;
    private String title; // Tassk.java의 title
    private int priority; // Tassk.java의 priority
    private LocalDate dueDate; // Tassk.java의 date 필드와 유사
    private boolean isCompleted; // Tassk.java의 completed 필드와 유사
    
    // --- 통계/루틴 필드 (DataRepository 및 TaskService에서 사용) ---
    private int durationSec; 
    private String evaluation; 
    private LocalDateTime recordDateTime;
    private String cycleFrequency = "NONE";

    // ✅ [통합 생성자 1]: 일정 등록용 (Tassk.java와 동일 형태)
    public Task(String title, int priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.durationSec = 0;
    }

    // ✅ [통합 생성자 2]: 통계 기록용
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
    
    // --- Getter/Setter (기존 Task.java + Tassk.java getter 통합) ---
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public int getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    
    public boolean isCompleted() { return isCompleted; } // Tassk.completed 대신 isCompleted() 사용
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    // 통계 관련 Getter/Setter
    public int getDurationSec() { return durationSec; }
    public LocalDateTime getRecordDateTime() { return recordDateTime; }
    public String getEvaluation() { return evaluation; }
    public String getCycleFrequency() { return cycleFrequency; }
    
    // 기타 필요한 Setter (생략)
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public void setEvaluation(String evaluation) { this.evaluation = evaluation; }
    public void setCycleFrequency(String cycleFrequency) { this.cycleFrequency = cycleFrequency; }
    
    @Override
    public String toString() {
        return String.format("%s [%d] %s (~%s)", isCompleted ? "✅" : "🔲", priority, title, dueDate);
    }
}
