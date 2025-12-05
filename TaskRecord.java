import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskName;
    private LocalDateTime dateTime;
    private int durationSec;
    private String evaluation;
    private boolean completed;

    public TaskRecord(String taskName, int durationSec, String evaluation, boolean completed) {
        this.taskName = taskName;
        this.durationSec = durationSec;
        this.evaluation = evaluation;
        this.completed = completed;
        this.dateTime = LocalDateTime.now();
    }

    public String getTaskName() { return taskName; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getDurationSec() { return durationSec; }
    public String getEvaluation() { return evaluation; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        return String.format("TaskRecord{task='%s', date=%s, duration=%ds, eval=%s, completed=%s}",
                taskName, dateTime.toString(), durationSec, evaluation, completed);
    }
}
