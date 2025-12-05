import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CalendarMain extends JFrame {

    private CalendarView calendarView;
    private Map<String, String> scheduleMap = new HashMap<>();

    // Sidebar animation settings
    private JPanel sidebar;
    private int sidebarWidth = 180;
    private boolean isSidebarOpen = true;
    private final int ANIMATION_STEP = 10;
    private final int ANIMATION_DELAY = 5;

    public CalendarMain() {

        setTitle("Calendar with Sidebar");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =====================================
        // 0) 전체 배경을 흰색으로 설정
        // =====================================
        getContentPane().setBackground(Color.WHITE);

        // =====================================
        // 1) 왼쪽 사이드바 (접힘/펼침)
        // =====================================
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(sidebarWidth, 600));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new GridLayout(10, 1, 0, 10));

        JButton btnMenu1 = createSidebarMenuButton("메뉴 1");
        JButton btnMenu2 = createSidebarMenuButton("메뉴 2");
        JButton btnMenu3 = createSidebarMenuButton("메뉴 3");

        sidebar.add(btnMenu1);
        sidebar.add(btnMenu2);
        sidebar.add(btnMenu3);

        add(sidebar, BorderLayout.WEST);

        // =====================================
        // 2) 상단 “三” 버튼
        // =====================================
        JButton toggleBtn = new JButton("三");
        toggleBtn.setFont(new Font("Dialog", Font.BOLD, 28));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);  // 상단바도 흰색
        topBar.add(toggleBtn);

        add(topBar, BorderLayout.NORTH);

        // =====================================
        // 3) CalendarView (이미 흰색)
        // =====================================
        calendarView = new CalendarView();
        add(calendarView, BorderLayout.CENTER);

        // 일정 클릭 이벤트 연결
        calendarView.setDayClickListener(new CalendarView.DayClickListener() {
            @Override
            public void onSingleClick(String dateKey) {
                String value = scheduleMap.get(dateKey);

                if (value == null) {
                    JOptionPane.showMessageDialog(CalendarMain.this,
                            dateKey + "\n일정이 없습니다.");
                } else {
                    JOptionPane.showMessageDialog(CalendarMain.this,
                            dateKey + "\n일정:\n" + value);
                }
            }

            @Override
            public void onDoubleClick(String dateKey) {
                SchedulePopup popup = new SchedulePopup(CalendarMain.this, dateKey, scheduleMap);
                popup.setVisible(true);
                calendarView.repaint();
            }
        });

        // =====================================
        // 4) 메뉴 버튼 → 팝업 실행
        // =====================================
        btnMenu1.addActionListener(e -> new Menu1Popup(this, calendarView));
        btnMenu2.addActionListener(e -> new Menu2Popup(this, calendarView));
        btnMenu3.addActionListener(e -> new Menu3Popup(this, calendarView));

        // =====================================
        // 5) 사이드바 토글 ("三")
        // =====================================
        toggleBtn.addActionListener(e -> toggleSidebar());

        setLocationRelativeTo(null);
        setVisible(true);
    }


    // -------------------------------
    // 메뉴 버튼 스타일 (흰색)
    // -------------------------------
    private JButton createSidebarMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.PLAIN, 16));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return btn;
    }

    // -------------------------------
    // 사이드바 접힘/펼침 애니메이션
    // -------------------------------
    private void toggleSidebar() {
        Timer timer = new Timer(ANIMATION_DELAY, null);

        timer.addActionListener(e -> {
            int currentWidth = sidebar.getWidth();

            if (isSidebarOpen) { // 접힘
                int newWidth = currentWidth - ANIMATION_STEP;
                if (newWidth <= 0) {
                    newWidth = 0;
                    isSidebarOpen = false;
                    timer.stop();
                }
                sidebar.setPreferredSize(new Dimension(newWidth, getHeight()));
            } else { // 펼침
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
