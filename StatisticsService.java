import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {

    private final DataRepository repo = DataRepository.getInstance();

    public List<Task> getTodayRecords() {
        List<Task> records = repo.getTasksByDate(LocalDate.now());
        System.out.println("📊 [Stats] 오늘 기록 조회: " + records.size() + "건");
        return records;
    }

    public int getTodayTotalFocusSec() {
        return getTodayRecords().stream().mapToInt(Task::getDurationSec).sum();
    }

    public double getTodayAchievementRate() {
        List<Task> today = getTodayRecords();
        if (today.isEmpty()) return 0.0;
        long completed = today.stream().filter(Task::isCompleted).count();
        return (double) completed / today.size() * 100.0;
    }

    public List<Task> getThisWeekRecords() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        List<Task> records = repo.getTasksByWeek(start, end);
        System.out.println("📊 [Stats] 주간 기록 조회 (" + start + " ~ " + end + "): " + records.size() + "건");
        return records;
    }

    public int getWeeklyTotalFocusSec() {
        return getThisWeekRecords().stream().mapToInt(Task::getDurationSec).sum();
    }

    public String getMostFocusedTask() {
        List<Task> all = repo.getAllTasks(); 
        if (all.isEmpty()) return "데이터 없음";
        
        Map<String, Long> countMap = all.stream()
                .filter(t -> t.getTitle() != null)
                .collect(Collectors.groupingBy(Task::getTitle, Collectors.counting()));

        return countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("데이터 없음");
    }
}