import java.time.LocalDate;
import java.util.*;

public class TaskService {

    private final Map<LocalDate, List<Task>> taskMap = new HashMap<>();

    /** 날짜에 해당하는 일정 리스트(없으면 새로 생성) */
    public List<Task> getTasks(LocalDate date) {
        return taskMap.computeIfAbsent(date, d -> new ArrayList<>());
    }

    /** 일정 추가 */
    public void addTask(LocalDate date, Task task) {
        taskMap.computeIfAbsent(date, d -> new ArrayList<>()).add(task);
    }

    /** 일정 삭제 */
    public void removeTask(LocalDate date, Task task) {
        List<Task> list = taskMap.get(date);
        if (list != null) list.remove(task);
    }

    /** 완료 여부 변경 */
    public void setTaskDone(LocalDate date, int index, boolean done) {
        var list = taskMap.get(date);
        if (list == null) return;
        if (index < 0 || index >= list.size()) return;

        list.get(index).done = done;
    }
}
