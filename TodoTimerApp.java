import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class TodoTimerApp extends JFrame {

    private TenMinuteTimer timerPanel; 
    
    // ★ 백엔드 서비스 연결
    private TaskService taskService;
    
    private JButton startPauseButton;
    private JButton resetButton;
    private JTextField taskInputField;
    
    // ★ String 대신 Task 객체를 담도록 변경
    private JList<Task> todoList;
    private DefaultListModel<Task> listModel;

    // 통계 UI 요소
    private JPanel statsPanel;
    private JLabel lblTodayCount;
    private JLabel lblTodayTime;
    private JLabel lblTodayRate;
    private JLabel lblWeekTime;
    private JLabel lblMostTask;

    public TodoTimerApp() {
        // 1. 백엔드 서비스 시작 (파일 이름 지정)
        taskService = new TaskService("todo_list_data.json");

        UIManager.put("OptionPane.messageFont", new Font("Malgun Gothic", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Malgun Gothic", Font.PLAIN, 14));

        setTitle("JUST 10min Todo-List (ADHD 전략 앱) - 통합 완성본");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Malgun Gothic", Font.BOLD, 14));

        tabbedPane.addTab("\u23F0 타이머", createTimerPanel());
        tabbedPane.addTab("\u2713 오늘 할 일", createTodoPanel());
        // (주간/월간 탭은 나중에 CalendarMain 등과 합칠 수 있게 비워둠)
        tabbedPane.addTab("\ud83d\udcc5 주간 계획", new JPanel());
        tabbedPane.addTab("\ud83d\uddd3 월간 계획", new JPanel());
        tabbedPane.addTab("\ud83d\udcca 통계", createStatsPanel());

        // 2. 탭을 누를 때마다 데이터 새로고침 (아주 중요!)
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);
            
            if (title.equals("\u2713 오늘 할 일")) {
                loadTodayTasks(); // 할 일 목록 새로고침
            } else if (title.equals("\ud83d\udcca 통계")) {
                updateStatisticsUI(); // 통계 새로고침
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // 앱 켜자마자 오늘 할 일 불러오기
        loadTodayTasks();
    }

    // --------------------------
    // 타이머 패널
    // --------------------------
    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        timerPanel = new TenMinuteTimer(); 
        
        startPauseButton = new JButton("▶ 시작");
        resetButton = new JButton("\ud83d\udd04 재설정");

        startPauseButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        resetButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(startPauseButton);
        buttonPanel.add(resetButton);

        JLabel statusLabel = new JLabel("🕒 작업 시간 ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(timerPanel, BorderLayout.CENTER); 
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        startPauseButton.addActionListener(e -> {
            if (startPauseButton.getText().equals("▶ 시작") || startPauseButton.getText().equals("▶ 계속")) {
                timerPanel.startTimer();
                startPauseButton.setText("⏸ 일시 정지");
            } else {
                timerPanel.stopTimer();
                startPauseButton.setText("▶ 계속");
            }
        });

        resetButton.addActionListener(e -> {
            timerPanel.stopTimer();
            startPauseButton.setText("▶ 시작");
        });

        return panel;
    }

    // --------------------------
    // 할 일 리스트 패널 (백엔드 연동됨!)
    // --------------------------
    private JPanel createTodoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        taskInputField = new JTextField(30);
        taskInputField.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        taskInputField.setToolTipText("할 일을 입력하고 엔터를 누르세요");
        
        JButton addButton = new JButton("\u2722 추가");
        addButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Task 객체를 담는 모델
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        todoList.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        
        // 더블 클릭 시 완료 처리 이벤트 추가
        todoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    completeTask();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(todoList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton completeButton = new JButton("\u2714 선택 완료");
        completeButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(completeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        taskInputField.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> completeTask());

        return panel;
    }

    // --------------------------
    // 통계 패널
    // --------------------------
    private JPanel createStatsPanel() {
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblTodayCount = new JLabel("오늘 총 수행 횟수: -");
        lblTodayTime = new JLabel("오늘 총 집중 시간: -");
        lblTodayRate = new JLabel("오늘 달성률: -");
        lblWeekTime = new JLabel("이번 주 총 집중 시간: -");
        lblMostTask = new JLabel("가장 많이 한 작업: -");

        // 폰트 통일
        Font statFont = new Font("Malgun Gothic", Font.PLAIN, 18);
        lblTodayCount.setFont(statFont);
        lblTodayTime.setFont(statFont);
        lblTodayRate.setFont(statFont);
        lblWeekTime.setFont(statFont);
        lblMostTask.setFont(statFont);

        // 간격 추가
        statsPanel.add(lblTodayCount); statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(lblTodayTime); statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(lblTodayRate); statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(lblWeekTime); statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(lblMostTask);

        return statsPanel;
    }

    // --------------------------
    // ★ 핵심 로직 구현부
    // --------------------------

    // 1. 오늘 할 일 목록 불러오기 (백엔드 -> UI)
    private void loadTodayTasks() {
        listModel.clear();
        // TaskService에서 '오늘 할 일'만 가져와서 리스트에 채움
        List<Task> tasks = taskService.getTasksForToday();
        
        if (tasks.isEmpty()) {
            // (안내 메시지를 띄우고 싶다면 별도 처리 가능)
        }
        
        for (Task t : tasks) {
            listModel.addElement(t);
        }
        todoList.repaint();
    }

    // 2. 할 일 추가
    private void addTask() {
        String taskText = taskInputField.getText().trim();
        if (!taskText.isEmpty()) {
            // 백엔드에 저장 (제목, 우선순위 1, 마감일 오늘)
            taskService.addTask(taskText, 1, LocalDate.now().toString());
            taskInputField.setText("");
            
            // 목록 갱신
            loadTodayTasks();
        }
    }

    // 3. 할 일 완료
    private void completeTask() {
        Task selectedTask = todoList.getSelectedValue();
        if (selectedTask != null) {
            // 백엔드에 완료 요청 (루틴이면 자동 갱신됨)
            boolean success = taskService.completeTask(selectedTask.getId());
            
            if (success) {
                // 성공하면 목록 갱신
                loadTodayTasks();
                JOptionPane.showMessageDialog(this, "완료 처리되었습니다! 🎉");
            } else {
                JOptionPane.showMessageDialog(this, "처리 중 오류가 발생했습니다.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "완료할 할 일을 선택해주세요.");
        }
    }

    // 4. 통계 갱신
    private void updateStatisticsUI() {
        StatisticsService stats = new StatisticsService();

        int todayCount = stats.getTodayRecords().size();
        int todaySec = stats.getTodayTotalFocusSec();
        double todayRate = stats.getTodayAchievementRate();
        int weekSec = stats.getWeeklyTotalFocusSec();
        String mostTask = stats.getMostFocusedTask();

        lblTodayCount.setText("오늘 총 수행 횟수: " + todayCount + "회");
        lblTodayTime.setText("오늘 총 집중 시간: " + todaySec / 60 + "분");
        lblTodayRate.setText(String.format("오늘 달성률: %.1f%%", todayRate));
        lblWeekTime.setText("이번 주 총 집중 시간: " + weekSec / 60 + "분");
        lblMostTask.setText("가장 많이 한 작업: " + mostTask);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoTimerApp::new);
    }
}
