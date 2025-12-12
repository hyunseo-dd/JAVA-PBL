import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TodayTasksPopup extends JDialog {

    public TodayTasksPopup(JFrame parent, TaskService taskService) {
        super(parent, "Today's Tasks", true);

        setSize(350, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        // ✅ 전체 Task 중 오늘 날짜만 필터링
        List<Task> todayTasks = taskService.getTasks().stream()
                .filter(t -> todayStr.equals(t.getDueDate()))
                .collect(Collectors.toList());

        JLabel title = new JLabel("오늘 일정", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        todayTasks.forEach(t -> model.addElement(t.toString()));

        JList<String> list = new JList<>(model);
        list.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        add(new JScrollPane(list), BorderLayout.CENTER);

        setVisible(true);
    }
}
