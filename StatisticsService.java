import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class StatisticsService {

    // DataRepository의 새 이름 함수 사용
    private final DataRepository repo = DataRepository.getInstance();

    // TaskRecord 대신 Task 사용
    public List<Task> getTodayRecords() {
        return repo.getTasksByDate(LocalDate.now());
    }

    // TaskRecord 대신 Task 사용
    public int getTodayTotalFocusSec() {
        return getTodayRecords().stream()
                .mapToInt(Task::getDurationSec) // Task의 DurationSec 사용
                .sum();
    }

    // TaskRecord 대신 Task 사용
    public double getTodayAchievementRate() {
        List<Task> today = getTodayRecords();
        if (today.isEmpty()) return 0;

        // Task의 isCompleted 사용
        long completed = today.stream().filter(Task::isCompleted).count();
        return (completed * 100.0) / today.size();
    }

    // TaskRecord 대신 Task 사용
    public List<Task> getThisWeekRecords() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return repo.getTasksByWeek(startOfWeek, endOfWeek);
    }

    // TaskRecord 대신 Task 사용
    public int getWeeklyTotalFocusSec() {
        return getThisWeekRecords().stream()
                .mapToInt(Task::getDurationSec) // Task의 DurationSec 사용
                .sum();
    }

    // TaskRecord 대신 Task 사용
    public String getMostFocusedTask() {
        List<Task> all = repo.getAllTasks(); // Task 리스트 가져오기
        if (all.isEmpty()) return "데이터 없음";

        // Task의 getName 사용
        return all.stream()
                .map(Task::getName)
                .distinct()
                .max((a, b) -> Long.compare(
                        all.stream().filter(r -> r.getName().equals(a)).count(),
                        all.stream().filter(r -> r.getName().equals(b)).count()
                ))
                .orElse("데이터 없음");
    }

    // TaskRecord 대신 Task 사용
    public double getWeeklyAverageEvaluationScore() {
        List<Task> list = getThisWeekRecords();

        int sum = 0;
        int count = 0;

        for (Task t : list) {
            switch (t.getEvaluation()) {
                case "기쁨": sum += 3; count++; break;
                case "보통": sum += 2; count++; break;
                case "슬픔": sum += 1; count++; break;
            }
        }

        if (count == 0) return 0;
        return (double) sum / count;
    }
}