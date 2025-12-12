import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TodayTasksPopup extends JDialog {

    public TodayTasksPopup(JFrame parent, TaskService taskService) {
        super(parent, "일정", true);

        setSize(350, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // ✅ [수정 핵심] taskService.getTasks() (X) -> taskService.getTasksForToday() (O)
        // TaskService에 이미 정의된 "오늘 할 일 가져오기" 함수를 사용하여 필터링 로직을 단순화합니다.
        List<Task> todayTasks = taskService.getTasksForToday();

        JLabel title = new JLabel("Tasks", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Task t : todayTasks) {
            // Task 객체의 내용을 문자열로 변환하여 리스트에 추가
            model.addElement(t.toString());
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        add(new JScrollPane(list), BorderLayout.CENTER);

        setVisible(true);
    }
}