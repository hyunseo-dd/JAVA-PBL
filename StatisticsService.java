import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {

    private final DataRepository repo = DataRepository.getInstance();

    // 1. 오늘 기록 가져오기
    public List<TaskRecord> getTodayRecords() {
        return repo.getRecordsByDate(LocalDate.now());
    }

    // 2. 오늘 총 집중 시간(초)
    public int getTodayTotalFocusSec() {
        return getTodayRecords().stream()
                .mapToInt(TaskRecord::getDurationSec)
                .sum();
    }

    // 3. 오늘 달성률 (완료된 것 / 전체 시도)
    public double getTodayAchievementRate() {
        List<TaskRecord> today = getTodayRecords();
        if (today.isEmpty()) return 0.0;

        long completedCount = today.stream().filter(TaskRecord::isCompleted).count();
        return (double) completedCount / today.size() * 100.0;
    }

    // 4. 이번 주 기록 가져오기
    public List<TaskRecord> getThisWeekRecords() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return repo.getRecordsByWeek(startOfWeek, endOfWeek);
    }

    // 5. 이번 주 총 집중 시간(초)
    public int getWeeklyTotalFocusSec() {
        return getThisWeekRecords().stream()
                .mapToInt(TaskRecord::getDurationSec)
                .sum();
    }

    // 6. 가장 많이 한 작업 찾기 (빈도수 기준)
    public String getMostFocusedTask() {
        List<TaskRecord> all = repo.getAllRecords();
        if (all.isEmpty()) return "-";

        // 작업 이름별로 그룹화해서 개수 세기
        Map<String, Long> countMap = all.stream()
                .collect(Collectors.groupingBy(TaskRecord::getTaskName, Collectors.counting()));

        // 개수가 제일 많은 것 찾기
        return countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
    }
}
