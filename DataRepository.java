import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;

public class DataRepository {

    private static DataRepository instance;
    private final String FILE_NAME = "task_records.json"; // 확장자 json으로 변경
    private final Gson gson;
    private List<TaskRecord> recordList;

    // --- [1] LocalDateTime 어댑터 (TodoFileManager와 동일한 로직) ---
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value.format(FORMATTER));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDateTime.parse(dateStr, FORMATTER) : null;
        }
    }

    // --- [2] 싱글톤 생성자 ---
    private DataRepository() {
        // Gson 설정 (날짜 어댑터 등록 필수!)
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        
        this.recordList = loadRecords();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) instance = new DataRepository();
        return instance;
    }

    // --- [3] 데이터 추가 및 저장 ---
    public void addRecord(TaskRecord record) {
        recordList.add(record);
        saveRecords();
        System.out.println("✅ 통계 기록 저장됨: " + record.getTaskName());
    }

    // --- [4] 조회 메서드들 ---
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
    
    // 주간 데이터 조회
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

    // --- [5] 파일 저장 (한글 깨짐 방지) ---
    private void saveRecords() {
        try {
            String json = gson.toJson(recordList);
            Files.write(Paths.get(FILE_NAME), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("❌ 통계 저장 실패: " + e.getMessage());
        }
    }

    // --- [6] 파일 로드 (안전장치 포함) ---
    private List<TaskRecord> loadRecords() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(FILE_NAME));
            String json = new String(bytes, StandardCharsets.UTF_8);

            if (json.trim().isEmpty()) return new ArrayList<>();

            List<TaskRecord> loaded = gson.fromJson(json, new TypeToken<List<TaskRecord>>() {}.getType());
            return loaded != null ? loaded : new ArrayList<>();

        } catch (Exception e) {
            System.out.println("⚠️ 통계 파일 초기화 (형식 불일치 등): " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
