import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;

public class TodoFileManager {
    private final String fileName;
    private final Gson gson;

    // --- [1] LocalDateTime 처리를 위한 어댑터 (필수) ---
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            // 문자열을 읽어서 날짜 객체로 변환
            String dateStr = in.nextString();
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDateTime.parse(dateStr, FORMATTER) : null;
        }
    }

    // --- [2] 생성자 ---
    public TodoFileManager(String fileName) {
        this.fileName = fileName;
        // Gson 설정: 줄바꿈(PrettyPrinting) + 날짜 어댑터 등록
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // --- [3] 파일 불러오기 (깨진 파일 자동 초기화 기능 추가) ---
    public List<Task> loadTasks() {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            String json = new String(bytes, StandardCharsets.UTF_8);

            if (json.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<Task> tasks = gson.fromJson(json, new TypeToken<List<Task>>() {}.getType());
            return tasks != null ? tasks : new ArrayList<>();

        } catch (Exception e) { 
            // ★ 여기가 핵심! (JsonSyntaxException 등 모든 에러를 잡음)
            // 파일 내용이 이상하면(기존 데이터 형식이 다르면) 그냥 빈 리스트로 시작
            System.out.println("⚠️ 기존 데이터 파일이 손상되었거나 형식이 달라서 초기화합니다.");
            return new ArrayList<>();
        }
    }

    // --- [4] 파일 저장하기 (한글 깨짐 방지) ---
    public void saveTasks(List<Task> tasks) {
        try {
            // 자바 리스트 -> JSON 변환
            String json = gson.toJson(tasks);
            
            // ★ 중요: UTF-8 인코딩으로 쓰기
            Files.write(Paths.get(fileName), json.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            System.err.println("❌ 파일 저장 실패: " + e.getMessage());
        }
    }
}