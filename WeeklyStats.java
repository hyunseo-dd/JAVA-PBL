import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class WeeklyStats {

    // 날짜별 Pomodoro 횟수 저장
    private static Map<LocalDate, Integer> dailyCount = new HashMap<>();

    // 세션 1회 완료 → 오늘 날짜 카운트 증가
    public static void addPomodoroSession() {
        LocalDate today = LocalDate.now();
        dailyCount.put(today, dailyCount.getOrDefault(today, 0) + 1);
    }

    // 이번 주의 데이터만 추출
    public static Map<DayOfWeek, Integer> getWeeklyStats() {

        Map<DayOfWeek, Integer> result = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            int count = dailyCount.getOrDefault(d, 0);
            result.put(d.getDayOfWeek(), count);
        }

        return result;
    }
}
