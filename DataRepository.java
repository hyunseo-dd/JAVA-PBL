import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private static DataRepository instance;
    private final String FILE_NAME = "task_records.dat";

    // TaskRecord 대신 통합된 Task 리스트 사용
    private List<Task> taskList;

    private DataRepository() {
        this.taskList = loadTasks();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) instance = new DataRepository();
        return instance;
    }

    // --- CRUD (C: Task 추가, R: Task 목록 가져오기) ---
    
    // (이름 변경) addRecord 대신 addTask 사용
    public void addTask(Task task) {
        taskList.add(task);
        saveTasks();
    }

    // (이름 변경) getAllRecords 대신 getAllTasks 사용
    public List<Task> getAllTasks() {
        return taskList;
    }

    // --- 통계 필터링 함수 (TaskRecord -> Task로 타입 변경) ---

    // (이름 변경) getRecordsByDate 대신 getTasksByDate 사용
    public List<Task> getTasksByDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task t : taskList) {
            // 기록된 날짜를 기준으로 필터링
            if (t.getRecordDateTime() != null && t.getRecordDateTime().toLocalDate().equals(date)) {
                result.add(t);
            }
        }
        return result;
    }

    // (이름 변경) getRecordsByWeek 대신 getTasksByWeek 사용
    public List<Task> getTasksByWeek(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<Task> result = new ArrayList<>();
        for (Task t : taskList) {
            if (t.getRecordDateTime() != null) {
                LocalDate d = t.getRecordDateTime().toLocalDate();
                if (!d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    // --- I/O 로직 (TaskRecord 리스트 대신 Task 리스트 저장) ---

    private void saveTasks() {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // taskList 저장
            oos.writeObject(taskList); 
            System.out.println("✅ Task 리스트 저장 완료.");
        } catch (Exception e) {
            System.out.println("❌ Task 리스트 저장 실패: " + e.getMessage());
        }
    }

    private List<Task> loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("⚠ Task 파일 없음 → 새 리스트 생성");
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Task 리스트 로드
            return (List<Task>) ois.readObject();
        } catch (Exception e) {
            System.out.println("❌ Task 리스트 로드 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}