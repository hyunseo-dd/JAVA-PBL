import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CalendarMain extends JFrame {

    // ===================== 공용 서비스 =====================
    private final TaskService taskService;

    // ===================== UI 구성요소 =====================
    private CalendarView calendarView;
    private JPanel sidebar;
    private JButton toggleBtn;
    private boolean sidebarOpen = false;

    // ===================== 생성자 =====================
    public CalendarMain() {

        // 🔹 공용 TaskService (파일 기반)
        this.taskService = new TaskService("calendar_tasks.json");

        setTitle("Calendar with Pomodoro");
        setSize(1200, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===================== 상단 바 =====================
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);

        toggleBtn = new JButton("☰");
        toggleBtn.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBackground(Color.WHITE);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        toggleBtn.addActionListener(e -> toggleSidebar());

        topBar.add(toggleBtn);
        add(topBar, BorderLayout.NORTH);

        // ===================== 사이드바 =====================
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setVisible(false);

        sidebar.add(Box.createVerticalStrut(10));

        // Today Tasks
        JButton todayBtn = createSidebarButton("Today Tasks");
        todayBtn.addActionListener(e ->
                new TodayTasksPopup(this, taskService)
        );
        sidebar.add(todayBtn);

        // Weekly Stats → TimerAndStatsPanel의 통계 탭
        JButton weeklyBtn = createSidebarButton("Weekly Stats");
        weeklyBtn.addActionListener(e -> {
            TimerAndStatsDialog dialog =
                    new TimerAndStatsDialog(this, taskService);
            dialog.showStatsTab();
            dialog.setVisible(true);
        });
        sidebar.add(weeklyBtn);

        // Pomodoro Timer → TimerAndStatsPanel 기본 탭
        JButton pomoBtn = createSidebarButton("Pomodoro Timer");
        pomoBtn.addActionListener(e -> {
            TimerAndStatsDialog dialog =
                    new TimerAndStatsDialog(this, taskService);
            dialog.setVisible(true);
        });
        sidebar.add(pomoBtn);

        add(sidebar, BorderLayout.WEST);

        // ===================== 캘린더 뷰 =====================
        calendarView = new CalendarView(taskService);
        add(calendarView, BorderLayout.CENTER);

        calendarView.setDayClickListener(new CalendarView.DayClickListener() {

            @Override
            public void onSingleClick(LocalDate date) {
                var tasks = taskService.getTasks(date);

                if (tasks.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            CalendarMain.this,
                            date + "\n일정 없음"
                    );
                    return;
                }

                StringBuilder sb = new StringBuilder();
                for (Task t : tasks) {
                    sb.append("- ").append(t).append("\n");
                }

                JOptionPane.showMessageDialog(
                        CalendarMain.this,
                        sb.toString()
                );
            }

            @Override
            public void onDoubleClick(LocalDate date) {
                new SchedulePopup(CalendarMain.this, date, taskService);
                calendarView.repaint();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ===================== 사이드바 버튼 생성 =====================
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
            }
        });

        return btn;
    }

    // ===================== 사이드바 토글 =====================
    private void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        sidebar.setVisible(sidebarOpen);
        revalidate();
        repaint();
    }

    // ===================== main =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarMain::new);
    }
}
