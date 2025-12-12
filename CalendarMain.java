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
    private int sidebarWidth = 180;

    private boolean sidebarOpen = false;

    // ===================== 생성자 =====================
    public CalendarMain() {

        // 🔹 공용 TaskService (파일 기반)
        this.taskService = new TaskService("calendar_tasks.json");

        setTitle("Calendar with Pomodoro");
        setSize(1200, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ======================================================
        //              상단 왼쪽 고정 메뉴 버튼
        // ======================================================
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);

        // ✅ 폰트 깨짐 문제 해결: "메뉴" 텍스트로 변경
        toggleBtn = new JButton("메뉴"); 
        toggleBtn.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBackground(Color.WHITE);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        toggleBtn.addActionListener(e -> toggleSidebar());

        topBar.add(toggleBtn);
        add(topBar, BorderLayout.NORTH);


        // ======================================================
        //                     사이드바 (버튼 정의)
        // ======================================================
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(sidebarWidth, getHeight()));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setVisible(sidebarOpen);

        sidebar.add(Box.createVerticalStrut(20));

        // ✅ [복구/재연결] Today Tasks
        JButton todayBtn = createSidebarButton("Tasks");
        todayBtn.addActionListener(e -> new TodayTasksPopup(this, taskService)); 
        sidebar.add(todayBtn);

        // ✅ [복구/재연결] Weekly Stats
        JButton weeklyBtn = createSidebarButton("Weekly Stats");
        weeklyBtn.addActionListener(e -> {
            // TimerAndStatsDialog는 미리 정의되어 있어야 에러가 안 납니다.
            TimerAndStatsDialog dialog = new TimerAndStatsDialog(this, taskService);
            dialog.showStatsTab(); 
            dialog.setVisible(true);
        });
        sidebar.add(weeklyBtn);

        // ✅ [복구/재연결] Pomodoro Timer
        JButton pomoBtn = createSidebarButton("Pomodoro Timer");
        pomoBtn.addActionListener(e -> {
            TimerAndStatsDialog dialog = new TimerAndStatsDialog(this, taskService);
            dialog.setVisible(true);
        });
        sidebar.add(pomoBtn);

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);


        // ======================================================
        //                 중앙 캘린더 뷰 (이벤트 리스너 복구)
        // ======================================================
        // (주의: CalendarView의 생성자가 TaskService만 받도록 수정되었다고 가정)
        calendarView = new CalendarView(taskService); 
        add(calendarView, BorderLayout.CENTER);

        // ✅ [복구] CalendarView에 DayClickListener를 설정하는 코드를 추가합니다.
        calendarView.setDayClickListener(new CalendarView.DayClickListener() {

            @Override
            public void onSingleClick(LocalDate date) {
                // 싱글 클릭 시 일정 간략 표시 로직
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
                // 더블 클릭 시 SchedulePopup 호출 로직
                new SchedulePopup(CalendarMain.this, date, taskService);
                // 팝업에서 변경된 내용이 캘린더에 즉시 반영되도록 refreshCalendar 호출
                // (CalendarView.refreshCalendar() 함수가 CalendarView에 정의되어 있어야 함)
                calendarView.refreshCalendar(); 
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ======================================================
    //          사이드바 버튼 생성 헬퍼
    // ======================================================
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

        // (주의: 버튼 리스너 로직은 위에서 별도로 정의했으므로 여기서는 제거)
        // btn.addActionListener(e -> { System.out.println("사이드바: " + text + " 클릭됨"); });

        return btn;
    }

    // ======================================================
    //      사이드바 표시/숨김 (메뉴 버튼)
    // ======================================================
    private void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        sidebar.setVisible(sidebarOpen);
        revalidate();
        repaint();
    }

    // ======================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarMain::new);
    }
}