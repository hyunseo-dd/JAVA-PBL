import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TodoTimerApp extends JFrame {

    private Timer swingTimer;
    private int secondsRemaining = 600;
    private boolean isTimerRunning = false;
    private DefaultListModel<String> listModel;

    private JLabel timerLabel;
    private JButton startPauseButton;
    private JButton resetButton;
    private JTextField taskInputField;
    private JList<String> todoList;

    // 통계 UI 요소
    private JPanel statsPanel;
    private JLabel lblTodayCount;
    private JLabel lblTodayTime;
    private JLabel lblTodayRate;
    private JLabel lblWeekTime;
    private JLabel lblMostTask;

    public TodoTimerApp() {
        UIManager.put("OptionPane.messageFont", new Font("Malgun Gothic", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Malgun Gothic", Font.PLAIN, 14));

        setTitle("JUST 10min Todo-List (ADHD 전략 앱)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 650);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Malgun Gothic", Font.BOLD, 14));

        tabbedPane.addTab("⏰ 타이머", createTimerPanel());
        tabbedPane.addTab("☑️ 오늘 할 일", createTodoPanel());
        tabbedPane.addTab("📅 주간 계획", new JPanel());
        tabbedPane.addTab("🗓️ 월간 계획", new JPanel());
        tabbedPane.addTab("📊 통계", createStatsPanel()); // 통계 탭 연결

        // 탭 선택 시 통계 업데이트
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);

            if (title.equals("📊 통계")) {
                updateStatisticsUI();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        updateTimerLabel();
        swingTimer = new Timer(1000, new TimerActionListener());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --------------------------
    // 타이머 패널
    // --------------------------
    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        timerLabel = new JLabel("10:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 72));

        startPauseButton = new JButton("▶ 시작");
        resetButton = new JButton("🔄 재설정");

        startPauseButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        resetButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(startPauseButton);
        buttonPanel.add(resetButton);

        JLabel statusLabel = new JLabel("🕒 작업 시간 ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        centerPanel.add(statusLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 20, 0);
        centerPanel.add(timerLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        centerPanel.add(buttonPanel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        startPauseButton.addActionListener(e -> toggleTimer());
        resetButton.addActionListener(e -> resetTimer());

        return panel;
    }

    // --------------------------
    // 할 일 리스트 패널
    // --------------------------
    private JPanel createTodoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        taskInputField = new JTextField(30);
        taskInputField.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        JButton addButton = new JButton("➕ 추가");
        addButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        todoList.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(todoList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton completeButton = new JButton("✔️ 선택 완료/삭제");
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
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblTodayCount = new JLabel("오늘 총 수행 횟수: -");
        lblTodayTime = new JLabel("오늘 총 집중 시간: -");
        lblTodayRate = new JLabel("오늘 달성률: -");
        lblWeekTime = new JLabel("이번 주 총 집중 시간: -");
        lblMostTask = new JLabel("가장 많이 한 작업: -");

        lblTodayCount.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        lblTodayTime.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        lblTodayRate.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        lblWeekTime.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        lblMostTask.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));

        statsPanel.add(lblTodayCount);
        statsPanel.add(lblTodayTime);
        statsPanel.add(lblTodayRate);
        statsPanel.add(lblWeekTime);
        statsPanel.add(lblMostTask);

        return statsPanel;
    }


    // --------------------------
    // 통계 UI 업데이트
    // --------------------------
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


    // --------------------------
    // 타이머 조작
    // --------------------------
    private void updateTimerLabel() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            swingTimer.stop();
            startPauseButton.setText("▶ 계속");
        } else {
            swingTimer.start();
            startPauseButton.setText("⏸ 일시 정지");
        }
        isTimerRunning = !isTimerRunning;
    }

    private void resetTimer() {
        swingTimer.stop();
        secondsRemaining = 600;
        isTimerRunning = false;
        startPauseButton.setText("▶ 시작");
        updateTimerLabel();
    }


    // --------------------------
    // 할 일 기능
    // --------------------------

    private void addTask() {
        String taskText = taskInputField.getText().trim();
        if (!taskText.isEmpty()) {
            listModel.addElement(taskText);
            taskInputField.setText("");
            if (listModel.getSize() == 1) {
                todoList.setSelectedIndex(0);
            }
        }
    }

    private void completeTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            if (listModel.getSize() > 0) {
                int newIndex = (selectedIndex < listModel.getSize()) ? selectedIndex : 0;
                todoList.setSelectedIndex(newIndex);
            }
        }
    }


    // --------------------------
    // 타이머 종료 처리
    // --------------------------
    private class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                updateTimerLabel();
            } else {

                swingTimer.stop();
                isTimerRunning = false;
                startPauseButton.setText("▶ 시작");

                // --- 1. 사용자 평가 팝업 ---
                Object[] evaluationOptions = {"😃 기쁨", "😐 보통", "😔 슬픔"};
                int evaluationChoice = JOptionPane.showOptionDialog(
                        TodoTimerApp.this,
                        "10분 집중 시간이 끝났어요!\n오늘의 할 일은 어떠셨나요?",
                        "✅ 할 일 평가하기",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        evaluationOptions,
                        evaluationOptions[1]
                );

                String evaluation;
                switch (evaluationChoice) {
                    case 0: evaluation = "기쁨"; break;
                    case 1: evaluation = "보통"; break;
                    case 2: evaluation = "슬픔"; break;
                    default: evaluation = "평가 안 함"; break;
                }

                // --- 2. ★ TaskRecord 저장 (중요 ★)
                String taskName = "";
                int idx = todoList.getSelectedIndex();
                if (idx != -1) taskName = listModel.getElementAt(idx);
                else taskName = "작업 이름 없음";

                TaskRecord record = new TaskRecord(
                        taskName,
                        600,
                        evaluation,
                        true
                );

                DataRepository.getInstance().addRecord(record);
                System.out.println("📌 TaskRecord 저장됨: " + record);


                // --- 3. 다음 작업 이동 팝업 ---
                int actionChoice = JOptionPane.showConfirmDialog(
                        TodoTimerApp.this,
                        "다음 단계는 무엇인가요?\n이 일을 계속 진행할까요? (예)\n다음 할 일로 갈까요? (아니오)",
                        "❓ 다음 행동 결정",
                        JOptionPane.YES_NO_OPTION
                );

                if (actionChoice == JOptionPane.YES_OPTION) {
                    secondsRemaining = 600;
                    toggleTimer();
                    JOptionPane.showMessageDialog(TodoTimerApp.this, "다시 10분 집중 시작합니다!");
                } else {
                    int currentIndex = todoList.getSelectedIndex();
                    int nextIndex = currentIndex + 1;

                    if (listModel.getSize() == 0) {
                        JOptionPane.showMessageDialog(TodoTimerApp.this, "할 일이 없습니다.");
                        resetTimer();
                    } else if (nextIndex >= listModel.getSize()) {
                        todoList.setSelectedIndex(0);
                        resetTimer();
                    } else {
                        todoList.setSelectedIndex(nextIndex);
                        resetTimer();
                    }
                }
            }
        }
    }


    // --------------------------
    // 실행 함수
    // --------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoTimerApp::new);
    }
}
