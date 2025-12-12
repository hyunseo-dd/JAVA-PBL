import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Serializable {

    private static final long serialVersionUID = 3L;

    // 일정 관리 필드 (Calendar / Schedule)
    private UUID id;
    private String name;
    private int priority;
    private LocalDate dueDate;
    
    // 기록 및 완료 상태 필드 (Pomodoro / Statistics)
    private boolean isCompleted;
    private int durationSec; // 해당 작업에 집중한 시간 (초)
    private String evaluation; // 완료 시 감정 평가 (예: 기쁨, 보통, 슬픔)
    private LocalDateTime recordDateTime; // 해당 작업이 완료된 시점 기록

    // 생성자 (일정 등록용)
    public Task(String name, int priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.durationSec = 0;
    }

    // 생성자 (통계 기록용 - 로직에서 사용)
    public Task(String name, int durationSec, String evaluation, boolean completed) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.priority = 0; // 통계 기록에는 우선순위 불필요
        this.dueDate = LocalDate.now();
        this.isCompleted = completed;
        this.durationSec = durationSec;
        this.evaluation = evaluation;
        this.recordDateTime = LocalDateTime.now();
    }

    // --- Getter/Setter (모두 포함) ---
    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return isCompleted; }
    public int getDurationSec() { return durationSec; }
    public String getEvaluation() { return evaluation; }
    public LocalDateTime getRecordDateTime() { return recordDateTime; }

    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public void setEvaluation(String evaluation) { this.evaluation = evaluation; }
    public void setRecordDateTime(LocalDateTime recordDateTime) { this.recordDateTime = recordDateTime; }

    @Override
    public String toString() {
        return String.format("%s [%d] %s (~%s)", isCompleted ? "✅" : "🔲", priority, name, dueDate);
    }
}