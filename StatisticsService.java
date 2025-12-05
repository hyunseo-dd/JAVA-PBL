import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class StatisticsService {

    private final DataRepository repo = DataRepository.getInstance();

    public List<TaskRecord> getTodayRecords() {
        return repo.getRecordsByDate(LocalDate.now());
    }

    public int getTodayTotalFocusSec() {
        return getTodayRecords().stream()
                .mapToInt(TaskRecord::getDurationSec)
                .sum();
    }

    public double getTodayAchievementRate() {
        List<TaskRecord> today = getTodayRecords();
        if (today.isEmpty()) return 0;

        long completed = today.stream().filter(TaskRecord::isCompleted).count();
        return (completed * 100.0) / today.size();
    }

    public List<TaskRecord> getThisWeekRecords() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return repo.getRecordsByWeek(startOfWeek, endOfWeek);
    }

    public int getWeeklyTotalFocusSec() {
        return getThisWeekRecords().stream()
                .mapToInt(TaskRecord::getDurationSec)
                .sum();
    }

    public String getMostFocusedTask() {
        List<TaskRecord> all = repo.getAllRecords();
        if (all.isEmpty()) return "데이터 없음";

        return all.stream()
                .map(TaskRecord::getTaskName)
                .distinct()
                .max((a, b) -> Long.compare(
                        all.stream().filter(r -> r.getTaskName().equals(a)).count(),
                        all.stream().filter(r -> r.getTaskName().equals(b)).count()
                ))
                .orElse("데이터 없음");
    }

    public double getWeeklyAverageEvaluationScore() {
        List<TaskRecord> list = getThisWeekRecords();

        int sum = 0;
        int count = 0;

        for (TaskRecord r : list) {
            switch (r.getEvaluation()) {
                case "기쁨": sum += 3; count++; break;
                case "보통": sum += 2; count++; break;
                case "슬픔": sum += 1; count++; break;
            }
        }

        if (count == 0) return 0;
        return (double) sum / count;
    }
}
