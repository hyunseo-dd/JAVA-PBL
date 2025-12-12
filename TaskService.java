import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// DataRepository와 TaskRecord를 사용하기 위해 import를 추가해야 합니다.
// (이 파일은 TaskService의 기존 코드에 이미 import되어 있다고 가정합니다.)
// import com.google.gson.Gson; // 필요하면 추가
// import com.google.gson.GsonBuilder; // 필요하면 추가
// import com.google.gson.TypeAdapter; // 필요하면 추가
// import com.google.gson.reflect.TypeToken; // 필요하면 추가
// import java.io.File; // TodoFileManager가 사용한다면 필요

public class TaskService {
    
    // --- (기존 필드) 일정 관리용 ---
    private List<Task> taskList;
    private TodoFileManager fileManager; 

    // ✅ TaskService를 싱글톤 패턴으로 변경 (getInstance() 사용을 위해)
    private static TaskService instance;

    // --- 생성자 ---
    public TaskService(String fileName) {
        this.fileManager = new TodoFileManager(fileName); 
        this.taskList = fileManager.loadTasks();
        System.out.println("TaskService가 준비되었습니다. " + taskList.size() + "개의 할 일을 로드했습니다.");
    }
    
    // ✅ 싱글톤 인스턴스 반환 메서드 추가
    public static synchronized TaskService getInstance() {
        if (instance == null) {
            // CalendarMain에서 호출하는 생성자와 동일하게 파일 이름을 지정해야 합니다.
            // (주의: getInstance()를 호출하는 곳에서는 파일 이름 인수를 전달할 수 없으므로,
            // 이 파일 이름은 TaskService의 다른 인스턴스(CalendarMain)와 동일해야 합니다.)
            instance = new TaskService("calendar_tasks.json"); 
        }
        return instance;
    }


    // (C) 새 할 일 추가 (주기 포함)
    public void addTask(String title, int priority, String dueDate, String cycle) {
        Task newTask = new Task(title, priority, LocalDate.parse(dueDate)); // Task 생성자 변경 반영
        newTask.setCycleFrequency(cycle);
        this.taskList.add(newTask);
        saveAllTasks();
    }
    
    // (C) 오버로드 (주기 없는 일반 할 일)
    public void addTask(String title, int priority, String dueDate) {
        addTask(title, priority, dueDate, "NONE");
    }

    // (R) 모든 할 일 가져오기 (완료/미완료 옵션 추가)
    public List<Task> getAllTasks(boolean includeCompleted) {
        if (includeCompleted) {
            return taskList;
        }
        return taskList.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
    }

    // (R) 모든 할 일 정렬 (sortKey: "priority" 또는 "dueDate")
    public List<Task> getAllTasksSorted(String sortKey) {
        Comparator<Task> comparator = null;
        
        Comparator<Task> baseComparator = Comparator.comparing(Task::isCompleted); 

        if ("priority".equalsIgnoreCase(sortKey)) {
            comparator = baseComparator.thenComparingInt(Task::getPriority);
            System.out.println("할 일 목록이 우선순위 기준으로 정렬되었습니다.");
        } else if ("dueDate".equalsIgnoreCase(sortKey)) {
            comparator = baseComparator.thenComparing(t -> {
                try {
                    // Task.java에서 dueDate가 LocalDate 객체이므로 String으로 변환 과정 제거
                    return t.getDueDate() == null ? LocalDate.MAX : t.getDueDate();
                } catch (Exception e) {
                    return LocalDate.MAX;
                }
            });
            System.out.println("할 일 목록이 마감 기한 기준으로 정렬되었습니다.");
        } else {
            System.out.printf("경고: 알 수 없는 정렬 기준 '%s' 입니다.\n", sortKey);
            return getAllTasks(true);
        }

        taskList.sort(comparator);
        return taskList;
    }
    
    // (R) ID로 특정 할 일 1개 찾기
    public Task getTaskById(String id) {
        return taskList.stream().filter(task -> task.getId().toString().equals(id)).findFirst().orElse(null);
    }
    
    // (R) 검색
    public List<Task> searchTasks(String keyword) {
        return taskList.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // (U) 할 일 내용 수정
    public boolean updateTaskDetails(String id, String newTitle, Integer newPriority, String newDueDate, String newCycle) {
        Task task = getTaskById(id);
        if (task == null) return false;
        
        // Task.java에 updateItem(String newTitle, Integer newPriority, LocalDate newDueDate)와 같은 헬퍼 함수가 필요합니다.
        // 여기서는 Getter/Setter를 직접 사용한다고 가정하고 코드를 작성합니다.
        if (newTitle != null) task.setTitle(newTitle); // setTitle 함수가 Task.java에 있어야 함
        if (newPriority != null) task.setPriority(newPriority); // setPriority 함수가 Task.java에 있어야 함
        if (newDueDate != null) task.setDueDate(LocalDate.parse(newDueDate));
        if (newCycle != null) task.setCycleFrequency(newCycle); 
        
        saveAllTasks();
        return true;
    }


    // --- (U) 할 일 완료 처리 (★루틴 갱신 로직 포함) ---
    public boolean completeTask(String id) {
        Task task = getTaskById(id);
        if (task == null) { 
            System.out.println("❌ 완료 실패: 해당 ID의 할 일을 찾을 수 없습니다.");
            return false;
        }
        // 이미 완료된 항목도 루틴 갱신을 위해 다시 처리할 수 있도록 isCompleted() 체크는 제거합니다.

        boolean isCycleTask = true;
        
        // Task.java에서 dueDate가 String이 아닌 LocalDate이므로 String 변환이 필요합니다.
        LocalDate currentDueDate = task.getDueDate();
        LocalDate nextDueDate = null;

        if (task.getCycleFrequency().equalsIgnoreCase("DAILY")) {
            nextDueDate = currentDueDate.plusDays(1);
        } else if (task.getCycleFrequency().equalsIgnoreCase("WEEKLY")) {
            nextDueDate = currentDueDate.plusWeeks(1);
        } else if (task.getCycleFrequency().equalsIgnoreCase("MONTHLY")) {
            nextDueDate = currentDueDate.plusMonths(1);
        } else {
            isCycleTask = false;
        }

        if (isCycleTask) {
            // 루틴 할 일: 다음 날짜로 갱신
            task.setDueDate(nextDueDate);
            task.setCompleted(false); // 미완료 상태 유지 (다음 날 리스트에 다시 나타나야 함)
            System.out.println("🔄 (로직) 루틴 갱신: " + task.getTitle() + " (다음 마감: " + nextDueDate + ")");
        } else {
            // 일반 할 일: '완료' 처리
            task.setCompleted(true); 
            System.out.println("🎉 (로직) 완료 처리: " + task.getTitle());
        }

        saveAllTasks();
        return true;
    }
    
    // (D) 할 일 삭제
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
    
    // --- 날짜 필터링 로직 (기존 유지) ---

    public List<Task> getTasksForToday() {
        LocalDate today = LocalDate.now();
        return taskList.stream()
            .filter(task -> {
                if (task.isCompleted()) return false;
                try {
                    LocalDate dueDate = task.getDueDate();
                    // Task.java에서 dueDate가 LocalDate 객체입니다.
                    return !dueDate.isAfter(today); 
                } catch (Exception e) {
                    return false;
                }
            })
            .sorted(Comparator.comparing(Task::getPriority))
            .collect(Collectors.toList());
    }

    // 캘린더 뷰에서 특정 날짜 일정 가져오기
    public List<Task> getTasks(LocalDate date) {

        return taskList.stream()
            .filter(task -> {
                if (task.isCompleted()) return false;
                try {
                    LocalDate dueDate = task.getDueDate();
                    return dueDate.equals(date);
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(Collectors.toList());
    }

    private void saveAllTasks() {
        fileManager.saveTasks(this.taskList);
    }
    
    // (테스트용)
    public void deleteAllTasks() {
        this.taskList.clear();
        saveAllTasks();
        System.out.println("🗑️ (로직) 모든 할 일을 삭제했습니다.");
    }

    // --- TasskSer.java에서 가져온 통계 관련 로직 ---
    
    // ✅ TasskSer의 addRecord(TaskRecord record) 함수 흡수
    public void addRecord(TaskRecord record) {
        // DataRepository의 인스턴스를 얻어 통계 기록을 저장합니다.
        // DataRepository는 TaskRecord를 저장하도록 되어있으므로 그 구조를 따릅니다.
        DataRepository.getInstance().addRecord(record);
    }
    
    // ✅ TasskSer의 getRecords() 함수 흡수
    public List<TaskRecord> getRecords() {
        // DataRepository에서 모든 통계 기록을 가져옵니다.
        return DataRepository.getInstance().getAllRecords();
    }
    // --- 로직 통합 끝 ---
}
