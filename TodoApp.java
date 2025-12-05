import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.io.File;

// --- (Week 2) 날짜 로직을 위해 java.time 패키지를 대량으로 import 합니다! ---
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Collectors; // 필터링 기능을 위해 추가합니다.

/**
 * 할 일 우선순위 앱의 통합 Java 파일입니다. (Week 2 최종본)
 * * * Week 1 목표: I/O 안정화 (완료)
 * * Week 2 목표: 날짜 최종 점검 (루틴 반복, 날짜 필터링) (완료)
 */
public class TodoApp {

    // --- 1. 데이터 모델 (Task) ---
    // (이전 단계에서 완성! 수정 X)
    static class Task implements Serializable {
        private static final long serialVersionUID = 2L; 

        private String id;
        private String title;
        private int priority;
        private boolean isCompleted;
        private String dueDate; // 마감 기한 (예: "2025-11-20")
        private String notificationTime;
        private int strategyDuration;
        private int pomodoroDuration;
        private int restDuration;
        private String cycleFrequency; // "NONE", "DAILY", "WEEKLY", "MONTHLY"
        private String memo;
        private String completedDate;

        public Task(String title, int priority, String dueDate) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.priority = priority;
            this.dueDate = dueDate;
            this.isCompleted = false;
            this.strategyDuration = 10;
            this.pomodoroDuration = 25;
            this.restDuration = 5;
            this.cycleFrequency = "NONE";
            this.memo = "";
        }

        // --- Getter/Setter (이전 단계에서 완성! 수정 X) ---
        public String getId() { return id; }
        public String getTitle() { return title; }
        public int getPriority() { return priority; }
        public boolean isCompleted() { return isCompleted; }
        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
        public String getCycleFrequency() { return cycleFrequency; }
        public void setCycleFrequency(String freq) { this.cycleFrequency = freq; }
        
        public void setCompleted(boolean completed) { 
            this.isCompleted = completed;
            if (completed) {
                this.completedDate = LocalDate.now().toString();
            } else {
                this.completedDate = null;
            }
        }
        
        // (toString()은 가독성을 위해 이전 버전으로 축소했습니다)
        @Override
        public String toString() {
            String status = isCompleted ? "✅ 완료" : "⏳ 미완료";
            String cycle = cycleFrequency.equals("NONE") ? "" : " (" + cycleFrequency + " 반복)";
            return String.format("[%s] (우선순위: %d) %s [마감: %s]%s",
                status, priority, title, dueDate, cycle);
        }
    }

    // --- 2. 파일 관리자 (TodoFileManager) ---
    // (Week 1에서 완성! 수정 X)
    static class TodoFileManager {
        private final String FILE_NAME;

        public TodoFileManager(String fileName) { this.FILE_NAME = fileName; }

        public void saveTasks(List<Task> tasks) {
            try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(tasks);
                System.out.println("✅ (I/O) 파일 저장 성공.");
            } catch (IOException e) {
                System.err.println("❌ (I/O) 파일 저장 실패: " + e.getMessage());
            }
        }

        public List<Task> loadTasks() {
            List<Task> loadedTasks = new ArrayList<>();
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("⚠️ (I/O) 저장된 파일 없음. 새 목록 시작.");
                return loadedTasks;
            }
            try (FileInputStream fis = new FileInputStream(FILE_NAME);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                loadedTasks = (List<Task>) ois.readObject();
                System.out.println("✅ (I/O) 파일 로드 성공.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ (I/O) 파일 로드 실패: " + e.getMessage());
            }
            return loadedTasks;
        }
    }

    // --- 3. (Week 2 최종) 핵심 로직 (TaskService) ---
    // (날짜 필터링 및 루틴 반복 로직 탑재!)
    static class TaskService {
        private List<Task> taskList;
        private TodoFileManager fileManager;

        public TaskService(String fileName) {
            this.fileManager = new TodoFileManager(fileName);
            this.taskList = fileManager.loadTasks();
            System.out.println("TaskService가 준비되었습니다. " + taskList.size() + "개의 할 일을 로드했습니다.");
        }

        // C (Create): 새 할 일 추가
        public void addTask(String title, int priority, String dueDate) {
            Task newTask = new Task(title, priority, dueDate);
            this.taskList.add(newTask);
            saveAllTasks();
        }
        
        // C (Create Overload): 주기가 있는 할 일 추가
        public void addCycleTask(String title, int priority, String dueDate, String cycle) {
            Task newTask = new Task(title, priority, dueDate);
            newTask.setCycleFrequency(cycle); // 주기 설정
            this.taskList.add(newTask);
            saveAllTasks();
        }

        // R (Read): 모든 할 일 (정렬)
        public List<Task> getAllTasksSorted() {
            this.taskList.sort(Comparator.comparing(Task::getPriority)
                                         .thenComparing(Task::getDueDate));
            return this.taskList;
        }

        // R (Read): ID로 특정 할 일 1개 찾기
        public Task getTaskById(String id) {
            // (Java 8 Stream을 사용하면 코드가 깔끔해져요)
            return taskList.stream()
                           .filter(task -> task.getId().equals(id))
                           .findFirst()
                           .orElse(null); // 못 찾으면 null
        }

        // --- (Week 2 최종 목표) 날짜 필터링 로직 3가지 ---

        /**
         * 1. 일간 필터링: 오늘까지 마감인 (미완료) 할 일
         */
        public List<Task> getTasksForToday() {
            LocalDate today = LocalDate.now();
            return taskList.stream()
                .filter(task -> {
                    // 미완료 상태여야 함
                    if (task.isCompleted()) return false;
                    
                    try {
                        LocalDate dueDate = LocalDate.parse(task.getDueDate());
                        // 마감일이 오늘이거나, 이미 지났는데 미완료인 것 (밀린 숙제)
                        return !dueDate.isAfter(today); 
                    } catch (Exception e) {
                        return false; // 날짜 형식이 잘못된 데이터는 거름
                    }
                })
                .sorted(Comparator.comparing(Task::getPriority)) // 우선순위 순으로 정렬
                .collect(Collectors.toList()); // 리스트로 만듦
        }

        /**
         * 2. 주간 필터링: 이번 주 (월~일)가 마감인 할 일
         */
        public List<Task> getTasksForThisWeek() {
            LocalDate today = LocalDate.now();
            // 이번 주의 시작(월요일)
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // 이번 주의 끝(일요일)
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            return taskList.stream()
                .filter(task -> {
                    try {
                        LocalDate dueDate = LocalDate.parse(task.getDueDate());
                        // 마감일이 (월요일 이후) 그리고 (일요일 이전)
                        return !dueDate.isBefore(startOfWeek) && !dueDate.isAfter(endOfWeek);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(Task::getDueDate)) // 날짜 순으로 정렬
                .collect(Collectors.toList());
        }

        /**
         * 3. 월간 필터링: 이번 달 (1일~말일)이 마감인 할 일
         */
        public List<Task> getTasksForThisMonth() {
            LocalDate today = LocalDate.now();
            // 이번 달의 시작(1일)
            LocalDate startOfMonth = today.withDayOfMonth(1);
            // 이번 달의 끝(말일)
            LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

            return taskList.stream()
                .filter(task -> {
                    try {
                        LocalDate dueDate = LocalDate.parse(task.getDueDate());
                        return !dueDate.isBefore(startOfMonth) && !dueDate.isAfter(endOfMonth);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(Task::getDueDate)) // 날짜 순으로 정렬
                .collect(Collectors.toList());
        }

        // --- (Week 2 최종 목표) 루틴 반복 로직 ---

        /**
         * U (Update): 할 일 완료 처리 (★루틴 반복 로직 탑재!)
         */
        public boolean completeTask(String id) {
            Task task = getTaskById(id);
            if (task == null) {
                System.out.println("❌ 완료 실패: 해당 ID의 할 일을 찾을 수 없습니다.");
                return false;
            }

            // '루틴 반복'이 설정된 할 일인지 확인
            boolean isCycleTask = true;
            LocalDate currentDueDate = LocalDate.parse(task.getDueDate());
            LocalDate nextDueDate = null;

            if (task.getCycleFrequency().equalsIgnoreCase("DAILY")) {
                nextDueDate = currentDueDate.plusDays(1);
            } else if (task.getCycleFrequency().equalsIgnoreCase("WEEKLY")) {
                nextDueDate = currentDueDate.plusWeeks(1);
            } else if (task.getCycleFrequency().equalsIgnoreCase("MONTHLY")) {
                nextDueDate = currentDueDate.plusMonths(1);
            } else {
                isCycleTask = false; // "NONE" (반복 없음)
            }

            if (isCycleTask) {
                // 루틴 할 일: '완료' 대신 '다음 날짜'로 갱신
                task.setDueDate(nextDueDate.toString());
                System.out.println("🔄 (로직) 루틴 갱신: " + task.getTitle() + " (다음 마감: " + nextDueDate + ")");
            } else {
                // 일반 할 일: '완료' 처리
                task.setCompleted(true);
                System.out.println("🎉 (로직) 완료 처리: " + task.getTitle());
            }

            saveAllTasks(); // (중요) 변경 사항을 파일에 즉시 저장
            return true;
        }

        // D (Delete): 할 일 삭제
        public boolean deleteTask(String id) {
            Task task = getTaskById(id);
            if (task != null) {
                this.taskList.remove(task);
                saveAllTasks();
                System.out.println("➖ (로직) 삭제 완료: " + task.getTitle());
                return true;
            }
            return false;
        }

        private void saveAllTasks() {
            fileManager.saveTasks(this.taskList);
        }
        
        // (테스트용) 모든 데이터를 삭제하는 헬퍼 함수
        public void deleteAllTasks() {
            this.taskList.clear();
            saveAllTasks();
            System.out.println("🗑️ (로직) 모든 할 일을 삭제했습니다.");
        }
    }


    // --- 4. 메인 실행 함수 (Week 2 최종 테스트) ---
    public static void main(String[] args) {
        // Week 2 데이터 파일 (v2)
        final String DATA_FILE = "todo_list_data_v2.dat"; 
        System.out.println("--- Week 2: 날짜 최종 점검 테스트 시작 ---");

        // 1. 서비스 준비 (파일에서 데이터 로드)
        TaskService service = new TaskService(DATA_FILE);
        
        // (테스트를 위해 이전 데이터를 모두 삭제합니다)
        service.deleteAllTasks();

        // 2. (C) 날짜별/주기별 테스트 데이터 추가
        System.out.println("\n[2. 테스트 데이터 추가]");
        
        // (날짜 계산)
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String nextWeek = LocalDate.now().plusWeeks(1).toString();
        String nextMonth = LocalDate.now().plusMonths(1).toString();

        // (데이터 추가)
        service.addTask("밀린 숙제 (어제 마감)", 1, yesterday);
        service.addTask("오늘 할 일 1순위 (오늘 마감)", 1, today);
        service.addTask("오늘 할 일 2순위 (오늘 마감)", 2, today);
        service.addTask("내일 할 일 (내일 마감)", 3, tomorrow);
        service.addTask("다음 주 보고서 (다음 주 마감)", 2, nextWeek);
        service.addTask("월간 기획안 (다음 달 마감)", 1, nextMonth);
        
        // (루틴 할 일 추가)
        service.addCycleTask("매일 영어 단어 10개 (오늘 마감)", 3, today, "DAILY");


        // 3. (R) 날짜 필터링 테스트
        System.out.println("\n[3-1. 오늘 할 일 (Today's List - 밀린 숙제 포함)]");
        List<Task> todayTasks = service.getTasksForToday();
        todayTasks.forEach(System.out::println); // 람다식으로 깔끔하게 출력

        System.out.println("\n[3-2. 이번 주 할 일 (This Week's List)]");
        List<Task> weekTasks = service.getTasksForThisWeek();
        weekTasks.forEach(System.out::println);

        System.out.println("\n[3-3. 이번 달 할 일 (This Month's List)]");
        List<Task> monthTasks = service.getTasksForThisMonth();
        monthTasks.forEach(System.out::println);

        // 4. (U) 루틴 반복 테스트
        System.out.println("\n[4. 루틴 반복 테스트]");
        
        // '매일 영어 단어'의 ID 찾기
        Task dailyTask = null;
        for (Task t : service.getAllTasksSorted()) {
            if (t.getTitle().startsWith("매일 영어 단어")) {
                dailyTask = t;
                break;
            }
        }
        
        if (dailyTask != null) {
            System.out.println(">> '매일 영어 단어' 완료 처리 시도...");
            service.completeTask(dailyTask.getId());
        }
        
        // 5. 최종 결과 확인
        System.out.println("\n[5. 최종 목록 (루틴 갱신 확인)]");
        service.getAllTasksSorted().forEach(System.out::println);
        
        System.out.println("\n--- Week 2: 날짜 최종 점검 테스트 완료 ---");
    }
}