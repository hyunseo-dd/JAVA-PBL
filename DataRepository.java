import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private static DataRepository instance;
    private final String FILE_NAME = "task_records_v2.dat"; // ✅ 파일명 변경 (충돌 방지)

    private List<Task> taskList;

    private DataRepository() {
        this.taskList = loadTasks();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) instance = new DataRepository();
        return instance;
    }

    // --- CRUD ---
    public void addTask(Task task) {
        taskList.add(task);
        saveTasks(); // 저장 즉시 수행
        System.out.println("✅ [Repository] 데이터 추가됨. 현재 총 개수: " + taskList.size());
    }

    public List<Task> getAllTasks() {
        // 리스트가 비어있으면 파일에서 다시 한 번 읽어오기 시도
        if (taskList.isEmpty()) {
            taskList = loadTasks();
        }
        return taskList;
    }

    public List<Task> getTasksByDate(LocalDate date) {
        // 최신 상태 유지를 위해 전체 목록 사용
        List<Task> all = getAllTasks();
        List<Task> result = new ArrayList<>();
        for (Task t : all) {
            // 날짜 비교 로직 강화
            if (t.getRecordDateTime() != null && t.getRecordDateTime().toLocalDate().isEqual(date)) {
                result.add(t);
            }
        }
        System.out.println("🔍 [Repository] " + date + " 조회 결과: " + result.size() + "건");
        return result;
    }

    public List<Task> getTasksByWeek(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<Task> all = getAllTasks();
        List<Task> result = new ArrayList<>();
        for (Task t : all) {
            if (t.getRecordDateTime() != null) {
                LocalDate d = t.getRecordDateTime().toLocalDate();
                if (!d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    // --- I/O 로직 ---
    private void saveTasks() {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(taskList); 
            System.out.println("💾 [File] 파일 저장 완료: " + FILE_NAME);
        } catch (Exception e) {
            System.err.println("❌ [File] 저장 실패: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Task> loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("⚠ [File] 데이터 파일이 없습니다. 새로 시작합니다.");
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<Task> loaded = (List<Task>) ois.readObject();
            System.out.println("📂 [File] 로드 성공: " + loaded.size() + "건");
            return loaded;
        } catch (Exception e) {
            System.err.println("❌ [File] 로드 실패, 초기화합니다: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}