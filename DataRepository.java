import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private static DataRepository instance;
    private final String FILE_NAME = "task_records.dat";

    private List<TaskRecord> recordList;

    private DataRepository() {
        this.recordList = loadRecords();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) instance = new DataRepository();
        return instance;
    }

    public void addRecord(TaskRecord record) {
        recordList.add(record);
        saveRecords();
    }

    public List<TaskRecord> getAllRecords() {
        return recordList;
    }

    public List<TaskRecord> getRecordsByDate(LocalDate date) {
        List<TaskRecord> result = new ArrayList<>();
        for (TaskRecord r : recordList) {
            if (r.getDateTime().toLocalDate().equals(date)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<TaskRecord> getRecordsByWeek(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<TaskRecord> result = new ArrayList<>();
        for (TaskRecord r : recordList) {
            LocalDate d = r.getDateTime().toLocalDate();
            if (!d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)) {
                result.add(r);
            }
        }
        return result;
    }

    private void saveRecords() {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(recordList);
            System.out.println("✅ TaskRecord 저장 완료.");
        } catch (Exception e) {
            System.out.println("❌ TaskRecord 저장 실패: " + e.getMessage());
        }
    }

    private List<TaskRecord> loadRecords() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("⚠ TaskRecord 파일 없음 → 새 리스트 생성");
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<TaskRecord>) ois.readObject();
        } catch (Exception e) {
            System.out.println("❌ TaskRecord 로드 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
