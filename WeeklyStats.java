public class WeeklyStats {
    private static int count = 0;

    public static void addPomodoroSession() {
        count++;
    }

    public static int getCount() {
        return count;
    }
}
