import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CalendarMain extends JFrame {

    private final TaskService taskService;
    private CalendarView calendarView;

    public CalendarMain() {

        this.taskService = new TaskService();

        setTitle("Calendar with Pomodoro");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        calendarView = new CalendarView();
        add(calendarView, BorderLayout.CENTER);

        calendarView.setDayClickListener(new CalendarView.DayClickListener() {
            @Override
            public void onSingleClick(LocalDate date) {
                var tasks = taskService.getTasks(date);
                if (tasks.isEmpty()) {
                    JOptionPane.showMessageDialog(CalendarMain.this, date + "\n일정 없음");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (Task t : tasks) sb.append("- ").append(t).append("\n");
                JOptionPane.showMessageDialog(CalendarMain.this, sb.toString());
            }

            @Override
            public void onDoubleClick(LocalDate date) {
                new SchedulePopup(CalendarMain.this, date, taskService);
                calendarView.repaint();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton pomodoroBtn = new JButton("Pomodoro");
        pomodoroBtn.addActionListener(e ->
                new PomodoroPopup(this, calendarView, taskService)
        );

        top.add(pomodoroBtn);
        add(top, BorderLayout.NORTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarMain::new);
    }
}
