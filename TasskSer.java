import java.time.LocalDate;
import java.util.*;

public class TasskSer {

    private static final TasskSer instance = new TasskSer();

    // 所有任务按照日期分类存储
    private final Map<LocalDate, List<Tassk>> taskMap = new HashMap<>();

    // 保存计时器统计数据
    private final List<TaskRecord> recordList = new ArrayList<>();

    private TasskSer() {}

    public static TasskSer getInstance() {
        return instance;
    }

    // 获取某一天的任务列表
    public synchronized List<Tassk> getTasks(LocalDate date) {
        return new ArrayList<>(taskMap.getOrDefault(date, Collections.emptyList()));
    }

    // 添加任务
    public synchronized void addTask(LocalDate date, Tassk task) {
        taskMap.computeIfAbsent(date, d -> new ArrayList<>()).add(task);
    }

    // 获取所有任务（供 TIMER 使用）
    public synchronized List<Tassk> getAllTasks() {
        List<Tassk> all = new ArrayList<>();
        for (List<Tassk> list : taskMap.values()) all.addAll(list);
        return all;
    }

    // 对任务排序：未完成在前 → 日期 → 优先级
    public List<Tassk> getSortedTasks(List<Tassk> tasks) {
        List<Tassk> copy = new ArrayList<>(tasks);
        copy.sort((a, b) -> {
            if (a.completed && !b.completed) return 1;
            if (!a.completed && b.completed) return -1;

            if (a.date != null && b.date != null) {
                int dcmp = a.date.compareTo(b.date);
                if (dcmp != 0) return dcmp;
            } else if (a.date != null) {
                return -1;
            } else if (b.date != null) {
                return 1;
            }

            return Integer.compare(a.priority, b.priority);
        });
        return copy;
    }

    // TIMER 调用保存统计数据
    public synchronized void addRecord(TaskRecord record) {
        recordList.add(record);
        System.out.println("TasskSer 已保存记录: " + record);
    }

    // 获取计时器所有记录
    public synchronized List<TaskRecord> getRecords() {
        return new ArrayList<>(recordList);
    }
}
