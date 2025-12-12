import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private List<Task> taskList;
    private TodoFileManager fileManager; 

    public TaskService(String fileName) {
        // TodoFileManager 클래스가 별도로 존재해야 해요!
        this.fileManager = new TodoFileManager(fileName); 
        this.taskList = fileManager.loadTasks();
        System.out.println("TaskService가 준비되었습니다. " + taskList.size() + "개의 할 일을 로드했습니다.");
    }

    // (C) 새 할 일 추가 (주기 포함)
    public void addTask(String title, int priority, String dueDate, String cycle) {
        Task newTask = new Task(title, priority, dueDate);
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
                    return t.getDueDate() == null || t.getDueDate().isEmpty() ? LocalDate.MAX : LocalDate.parse(t.getDueDate());
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
        return taskList.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
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
        
        task.updateItem(newTitle, newPriority, newDueDate, newCycle); 
        
        saveAllTasks();
        return true;
    }


    // --- (U) 할 일 완료 처리 (★루틴 갱신 로직 포함) ---
    public boolean completeTask(String id) {
        Task task = getTaskById(id);
        if (task == null || task.isCompleted()) { 
            System.out.println("❌ 완료 실패: 해당 ID의 할 일을 찾을 수 없거나 이미 완료되었습니다.");
            return false;
        }

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
            isCycleTask = false;
        }

        if (isCycleTask) {
            // 루틴 할 일: 다음 날짜로 갱신하고, 완료 시각을 기록
            task.setDueDate(nextDueDate.toString());
            // setCompleted(true)를 호출하여 lastCompletedDateTime만 업데이트
            // (Task 모델의 isCompleted는 false를 유지해야 다음 날 리스트에 다시 나타나요)
            task.setCompleted(false); 
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
                    LocalDate dueDate = LocalDate.parse(task.getDueDate());
                    return !dueDate.isAfter(today); 
                } catch (Exception e) {
                    return false;
                }
            })
            .sorted(Comparator.comparing(Task::getPriority))
            .collect(Collectors.toList());
    }

    public List<Task> getTasksForThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return taskList.stream()
            .filter(task -> {
                try {
                    LocalDate dueDate = LocalDate.parse(task.getDueDate());
                    return !dueDate.isBefore(startOfWeek) && !dueDate.isAfter(endOfWeek);
                } catch (Exception e) {
                    return false;
                }
            })
            .sorted(Comparator.comparing(Task::getDueDate))
            .collect(Collectors.toList());
    }

    public List<Task> getTasks(LocalDate date) {

        return taskList.stream()
            .filter(task -> {
                if (task.isCompleted()) return false;
                try {
                    LocalDate dueDate = LocalDate.parse(task.getDueDate());
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
}
