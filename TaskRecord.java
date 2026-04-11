// TaskRecord.java 파일 내용

import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskName;
    private LocalDateTime dateTime;
    private int durationSec;
    private String evaluation;
    private boolean completed;
    
    // 이 생성자는 PomodoroPopup에서 사용됩니다. (PomodoroPopup의 finishPomodoro 함수에 맞게 수정 필요)
    /*
    public TaskRecord(String taskName, int durationSec, String evaluation, boolean completed) {
        this.taskName = taskName;
        this.durationSec = durationSec;
        this.evaluation = evaluation;
        this.completed = completed;
        this.dateTime = LocalDateTime.now();
    }
    */
    
    // PomodoroPopup에 맞춘 임시 생성자 (TodayTasksPopup의 finishPomodoro에서 사용)
    public TaskRecord(LocalDate date, int focusMin, int repeatCount) {
        this.taskName = "Pomodoro Session";
        this.durationSec = focusMin * 60 * repeatCount;
        this.evaluation = "기쁨"; // 임시값
        this.completed = true;
        this.dateTime = LocalDateTime.now();
    }


    public String getTaskName() { return taskName; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getDurationSec() { return durationSec; }
    public String getEvaluation() { return evaluation; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        return String.format("Record{task='%s', date=%s, duration=%ds}",
                taskName, dateTime.toString(), durationSec);
    }
}