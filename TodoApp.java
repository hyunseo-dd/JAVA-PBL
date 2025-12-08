import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * 할 일 우선순위 앱의 통합 Java 파일입니다. (통합 최종본)
 */
public class TodoApp {
    
    // 이 클래스를 실행하기 위해서는 
    // Task.java, TaskService.java, TodoFileManager.java, TaskRecord.java 파일이 모두 있어야 해요!

    public static void main(String[] args) {
        
        final String DATA_FILE = "todo_list_data_final.dat"; 
        System.out.println("--- 백엔드 통합 최종 테스트 시작 ---");

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
        
        // (일반 할 일 추가)
        service.addTask("밀린 숙제 (어제 마감)", 1, yesterday);
        service.addTask("오늘 할 일 1순위 (오늘 마감)", 1, today);
        service.addTask("내일 할 일 (내일 마감)", 3, tomorrow);
        service.addTask("다음 주 보고서 (다음 주 마감)", 2, nextWeek);
        
        // (루틴 할 일 추가) - Task.java의 cycleFrequency 사용
        service.addTask("매일 영어 단어 10개", 3, today, "DAILY");
        service.addTask("주간 회의 자료 정리", 2, today, "WEEKLY");


        // 3. (R) 날짜 필터링 테스트
        System.out.println("\n[3-1. 오늘 할 일 (Today's List - 밀린 숙제 포함)]");
        List<Task> todayTasks = service.getTasksForToday();
        todayTasks.forEach(System.out::println); 

        System.out.println("\n[3-2. 마감 기한 기준으로 정렬된 모든 할 일]");
        service.getAllTasksSorted("dueDate").forEach(System.out::println);


        // 4. (U) 루틴 반복 및 수정 테스트
        System.out.println("\n[4. 루틴 반복 및 수정 테스트]");
        
        // '매일 영어 단어'의 ID 찾기
        Task dailyTask = service.searchTasks("영어 단어").stream().findFirst().orElse(null);
        Task weekTask = service.searchTasks("회의 자료").stream().findFirst().orElse(null);

        if (dailyTask != null) {
            System.out.println(">> '매일 영어 단어' 완료 처리 시도...");
            service.completeTask(dailyTask.getId());
            
            System.out.println(">> '주간 회의 자료 정리' 제목 및 우선순위 수정 시도...");
            // TaskService의 updateTaskDetails 메서드 사용
            service.updateTaskDetails(weekTask.getId(), "주간 회의 자료 최종본 검토", 1, weekTask.getDueDate(), weekTask.getCycleFrequency());
        }
        
        
        // 5. 최종 결과 확인
        System.out.println("\n[5. 최종 목록 (루틴 갱신 및 수정 확인)]");
        // 다시 우선순위 기준으로 정렬해서 출력
        service.getAllTasksSorted("priority").forEach(System.out::println);
        
        System.out.println("\n--- 백엔드 통합 최종 테스트 완료 ---");
    }
}
