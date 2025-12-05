import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarMain extends JFrame {

    public static Map<String, List<Task>> scheduleMapStatic;

    private CalendarView calendarView;
    private Map<String, List<Task>> scheduleMap = new HashMap<>();

    private JPanel sidebar;
    private int sidebarWidth = 180;

    private boolean isSidebarOpen = false;  // ⭐ 처음엔 닫힌 상태로 시작하도록 수정함

    private final int ANIMATION_STEP = 10;
    private final int ANIMATION_DELAY = 5;

    public CalendarMain() {

        setTitle("Calendar with Sidebar");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scheduleMapStatic = scheduleMap;

        // ===== Sidebar =====
        sidebar = new JPanel();

        // ⭐ 초기 width = 0으로 설정하여 처음엔 완전히 숨김
        sidebar.setPreferredSize(new Dimension(0, 600));

        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new GridLayout(10, 1, 0, 10));

        JButton btnPomodoro = createSidebarButton("Pomodoro");
        JButton btnSchedule = createSidebarButton("Schedule List");
        JButton btnWeeklyStats = createSidebarButton("Weekly Status");

        sidebar.add(btnPomodoro);
        sidebar.add(btnSchedule);
        sidebar.add(btnWeeklyStats);

        add(sidebar, BorderLayout.WEST);

        // ===== Top Bar =====
        JButton toggleBtn = new JButton("三");
        toggleBtn.setFont(new Font("Dialog", Font.BOLD, 28));
        toggleBtn.setBackground(Color.WHITE);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setFocusPainted(false);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);
        topBar.add(toggleBtn);

        add(topBar, BorderLayout.NORTH);

        // ===== Calendar View =====
        calendarView = new CalendarView();
        add(calendarView, BorderLayout.CENTER);

        calendarView.setDayClickListener(new CalendarView.DayClickListener() {
            @Override
            public void onSingleClick(String dateKey) {
                List<Task> list = scheduleMap.get(dateKey);

                if (list == null || list.isEmpty()) {
                    JOptionPane.showMessageDialog(CalendarMain.this,
                            dateKey + "\n일정이 없습니다.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Task t : list) {
                        sb.append("- ");
                        if (t.done) sb.append("(완료) ");
                        sb.append(t.text).append("\n");
                    }
                    JOptionPane.showMessageDialog(CalendarMain.this,
                            dateKey + "\n일정:\n" + sb);
                }
            }

            @Override
            public void onDoubleClick(String dateKey) {
                new SchedulePopup(CalendarMain.this, dateKey, scheduleMap);
                calendarView.repaint();
            }
        });

        // ===== Sidebar Button Actions =====
        btnPomodoro.addActionListener(e -> new PomodoroPopup(this, calendarView));
        btnSchedule.addActionListener(e -> new Schedule(this, calendarView, scheduleMap));
        btnWeeklyStats.addActionListener(e -> new WeeklyStatsPopup(this)); 

        toggleBtn.addActionListener(e -> toggleSidebar());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.PLAIN, 16));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setFocusPainted(false);
        return btn;
    }

    private void toggleSidebar() {

        javax.swing.Timer timer = new javax.swing.Timer(ANIMATION_DELAY, null);

        timer.addActionListener(e -> {
            int currentWidth = sidebar.getWidth();

            if (isSidebarOpen) {
                int newWidth = currentWidth - ANIMATION_STEP;
                if (newWidth <= 0) {
                    newWidth = 0;
                    isSidebarOpen = false;
                    timer.stop();
                }
                sidebar.setPreferredSize(new Dimension(newWidth, getHeight()));
            } else {
                int newWidth = currentWidth + ANIMATION_STEP;
                if (newWidth >= sidebarWidth) {
                    newWidth = sidebarWidth;
                    isSidebarOpen = true;
                    timer.stop();
                }
                sidebar.setPreferredSize(new Dimension(newWidth, getHeight()));
            }

            sidebar.revalidate();
            sidebar.repaint();
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarMain::new);
    }
}
