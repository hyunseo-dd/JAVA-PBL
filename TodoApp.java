import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TodoApp {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusWeeks(1);
        
        TodoManager manager = new TodoManager();
        System.out.println("\n--- 1. 할 일 추가 ---");
        Task task1 = new Task("프로젝트 제안서 작성", 1, tomorrow);
        Task task2 = new Task("팀원들과 주간 회의", 2, today);
        Task task3 = new Task("기술 동향 리서치", 4, nextWeek);
        Routine routine1 = new Routine("매일 코딩 한 시간", " daily", 1, null);
        Task task4 = new Task("Figma 디자인 최종 검토", 1, tomorrow);
        manager.addItem(task1);
        manager.addItem(task2);
        manager.addItem(task3);
        manager.addItem(routine1);
        manager.addItem(task4);
        System.out.println("\n--- 2. 현재 할 일 목록 (정렬 전) ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---3. 우선 순위 기준으로 정렬 ---");
        manager.sortItems("priority");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---4. 특정 할 일 완료 처리 ---");
        TodoItem targetTask1 = manager.findItemById(task1.getId());
        if (targetTask1 != null) {
            targetTask1.markAsCompleted();
        }
        TodoItem targetRoutine1 = manager.findItemById(routine1.getId());
        if (targetRoutine1 != null) {
            targetRoutine1.markAsCompleted();
        }
        System.out.println("\n---5. 완료 후 할 일 목록 (완료된 항목 제외) ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---6. 마감 기한 기준으로 정렬 (완료된 항목 제외) ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---7. 할 일 수정 ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---8. 수정 후 목록 확인 ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---9. 할 일 검색 ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---10. 할일 삭제 ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
        System.out.println("\n---11. 삭제 후 최종 할 일 목록 ---");
        for (TodoItem item : manager.getAllItems(false)) {
            System.out.println(item);
        }
    }
}