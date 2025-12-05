public class TestRecordCheck {
    public static void main(String[] args) {

        DataRepository repo = DataRepository.getInstance();

        System.out.println("=== 저장된 Record 개수 ===");
        System.out.println(repo.getAllRecords().size());

        System.out.println("=== 모든 기록 출력 ===");
        for (TaskRecord r : repo.getAllRecords()) {
            System.out.println(r);
        }

        System.out.println("=== 오늘 기록 개수 ===");
        System.out.println(repo.getRecordsByDate(java.time.LocalDate.now()).size());
    }
}
