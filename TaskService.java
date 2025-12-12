import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    
    private List<Task> taskList;
    private TodoFileManager fileManager; 
    private static TaskService instance;

    public TaskService(String fileName) {
        this.fileManager = new TodoFileManager(fileName); 
        this.taskList = fileManager.loadTasks();
    }
    
    public static synchronized TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService("calendar_tasks.json"); 
        }
        return instance;
    }

    // (C) 일정 추가
    public void addTask(String title, int priority, String dueDate, String cycle) {
        Task newTask = new Task(title, priority, LocalDate.parse(dueDate)); 
        newTask.setCycleFrequency(cycle);
        this.taskList.add(newTask);
        saveAllTasks();
    }
    
    public void addTask(String title, int priority, String dueDate) {
        addTask(title, priority, dueDate, "NONE");
    }

    // (R) 조회
    public List<Task> getAllTasks(boolean includeCompleted) {
        if (includeCompleted) {
            return taskList;
        }
        return taskList.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
    }

    public List<Task> getAllTasksSorted(String sortKey) {
        Comparator<Task> comparator;
        Comparator<Task> baseComparator = Comparator.comparing(Task::isCompleted); 

        if ("priority".equalsIgnoreCase(sortKey)) {
            comparator = baseComparator.thenComparingInt(Task::getPriority);
        } else if ("dueDate".equalsIgnoreCase(sortKey)) {
            comparator = baseComparator.thenComparing(t -> 
                t.getDueDate() == null ? LocalDate.MAX : t.getDueDate()
            );
        } else {
            return getAllTasks(true);
        }

        taskList.sort(comparator);
        return taskList;
    }
    
    public Task getTaskById(String id) {
        return taskList.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
    }
    
    public List<Task> searchTasks(String keyword) {
        return taskList.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // (U) 수정
    public boolean updateTaskDetails(String id, String newTitle, Integer newPriority, String newDueDate, String newCycle) {
        Task task = getTaskById(id);
        if (task == null) return false;
        
        if (newTitle != null) task.setTitle(newTitle); 
        if (newPriority != null) task.setPriority(newPriority); 
        if (newDueDate != null) task.setDueDate(LocalDate.parse(newDueDate));
        if (newCycle != null) task.setCycleFrequency(newCycle); 
        
        saveAllTasks();
        return true;
    }

    public boolean completeTask(String id) {
        Task task = getTaskById(id);
        if (task == null) return false;

        boolean isCycleTask = true;
        LocalDate currentDueDate = task.getDueDate();
        LocalDate nextDueDate = null;

        if ("DAILY".equalsIgnoreCase(task.getCycleFrequency())) {
            nextDueDate = currentDueDate.plusDays(1);
        } else if ("WEEKLY".equalsIgnoreCase(task.getCycleFrequency())) {
            nextDueDate = currentDueDate.plusWeeks(1);
        } else if ("MONTHLY".equalsIgnoreCase(task.getCycleFrequency())) {
            nextDueDate = currentDueDate.plusMonths(1);
        } else {
            isCycleTask = false;
        }

        if (isCycleTask) {
            task.setDueDate(nextDueDate);
            task.setCompleted(false); 
        } else {
            task.setCompleted(true); 
        }

        saveAllTasks();
        return true;
    }
    
    // (D) 삭제
    public boolean deleteTask(String id) {
        Task task = getTaskById(id);
        if (task != null) {
            this.taskList.remove(task);
            saveAllTasks();
            return true;
        }
        return false;
    }
    
    // 오늘 할 일
    public List<Task> getTasksForToday() {
        LocalDate today = LocalDate.now();
        return taskList.stream()
            .filter(task -> {
                if (task.isCompleted()) return false;
                try {
                    return !task.getDueDate().isAfter(today); 
                } catch (Exception e) { return false; }
            })
            .sorted(Comparator.comparing(Task::getPriority))
            .collect(Collectors.toList());
    }

    public List<Task> getTasks(LocalDate date) {
        return taskList.stream()
            .filter(task -> {
                if (task.isCompleted()) return false;
                try {
                    return task.getDueDate().equals(date);
                } catch (Exception e) { return false; }
            })
            .collect(Collectors.toList());
    }

    private void saveAllTasks() {
        fileManager.saveTasks(this.taskList);
    }
    
    public void deleteAllTasks() {
        this.taskList.clear();
        saveAllTasks();
    }

    // --- 통계 관련 로직 (TaskRecord 대체) ---
    
    public void addRecord(Task record) {
        DataRepository.getInstance().addTask(record);
    }
    
    public List<Task> getRecords() {
        return DataRepository.getInstance().getAllTasks();
    }
}