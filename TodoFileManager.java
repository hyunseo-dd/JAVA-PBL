import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TodoFileManager {
    private final String FILE_NAME;

    public TodoFileManager(String fileName) {
        this.FILE_NAME = fileName.replace(".json", ".dat");
        System.out.println("I/O 방식: Java Serializable (.dat) 사용");
    }

    public void saveTasks(List<Task> tasks) {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(tasks); 
            System.out.println("✅ (I/O) Task 파일 저장 성공.");
        } catch (IOException e) {
            System.err.println("❌ (I/O) Task 파일 저장 실패: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Task> loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<Task> loadedTasks = (List<Task>) ois.readObject(); 
            System.out.println("✅ (I/O) Task 파일 로드 성공.");
            return loadedTasks;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ (I/O) 파일 로드 실패 (파일 초기화됨): " + e.getMessage());
            return new ArrayList<>();
        }
    }
}