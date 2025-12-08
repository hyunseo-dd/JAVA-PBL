import java.time.LocalDateTime;

public class TaskRecord {
    private String taskName;
    private LocalDateTime dateTime; // 언제 집중했는지
    private int durationSec;        // 몇 초 집중했는지
    private String evaluation;      // 평가 (기쁨/보통/슬픔)
    private boolean completed;      // 완료 여부

    public TaskRecord(String taskName, int durationSec, String evaluation, boolean completed) {
        this.taskName = taskName;
        this.durationSec = durationSec;
        this.evaluation = evaluation;
        this.completed = completed;
        this.dateTime = LocalDateTime.now(); // 생성 시점 시간 기록
    }

    // --- Getter ---
    public String getTaskName() { return taskName; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getDurationSec() { return durationSec; }
    public String getEvaluation() { return evaluation; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        return String.format("[%s] %d초 집중 (%s)", taskName, durationSec, dateTime.toString());
    }
}
