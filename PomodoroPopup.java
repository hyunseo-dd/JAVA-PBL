import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PomodoroPopup extends JDialog {

    private JTextField focusField;
    private JTextField breakField;
    private JTextField repeatField;

    private JLabel sessionLabel;

    private JButton startBtn;
    private JButton pauseBtn;
    private JButton resetBtn;

    private CircularProgressBar circleTimer;

    private Timer timer;

    private int focusMin;
    private int breakMin;
    private int repeatCount;

    private boolean isFocusSession = true;
    private int currentRepeat = 1;

    private int remainingSeconds;
    private int totalSeconds;

    // ===== 오늘의 할 일 =====
    private DefaultListModel<TodoItem> todoModel;
    private JList<TodoItem> todoList;
    private JButton addTodoBtn;
    private JTextField addTodoField;

    public PomodoroPopup(JFrame parent, CalendarView calendarView) {

        super(parent, "Pomodoro Timer", true);

        setSize(520, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ========== 상단 설정 ==========
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        configPanel.setBackground(Color.WHITE);
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        configPanel.add(new JLabel("집중 시간(분):"));
        focusField = new JTextField("25");
        configPanel.add(focusField);

        configPanel.add(new JLabel("휴식 시간(분):"));
        breakField = new JTextField("5");
        configPanel.add(breakField);

        configPanel.add(new JLabel("반복 횟수:"));
        repeatField = new JTextField("4");
        configPanel.add(repeatField);

        add(configPanel, BorderLayout.NORTH);

        // ========== Pomodoro 타이머 UI ==========
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        circleTimer = new CircularProgressBar();
        circleTimer.setPreferredSize(new Dimension(330, 330));

        // ⭐ “세션 준비 중” 제거 → 공백
        sessionLabel = new JLabel("", SwingConstants.CENTER);
        sessionLabel.setFont(new Font("Dialog", Font.PLAIN, 22));

        centerPanel.add(circleTimer, BorderLayout.CENTER);
        centerPanel.add(sessionLabel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // ========== 오늘의 할 일 영역 ==========
        JPanel todoPanel = new JPanel(new BorderLayout());
        todoPanel.setBackground(Color.WHITE);
        todoPanel.setBorder(BorderFactory.createTitledBorder("오늘의 할 일"));

        todoModel = new DefaultListModel<>();
        todoList = new JList<>(todoModel);
        todoList.setCellRenderer(new CheckListRenderer());
        todoList.setBackground(Color.WHITE);

        todoList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int idx = todoList.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    TodoItem item = todoModel.get(idx);
                    item.done = !item.done;
                    sortTodoList();   // ⭐ 체크 시 자동 정렬
                    todoList.repaint();
                }
            }
        });

        todoPanel.add(new JScrollPane(todoList), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new BorderLayout());
        addPanel.setBackground(Color.WHITE);

        addTodoField = new JTextField();
        addTodoBtn = new JButton("추가");

        addTodoBtn.addActionListener(e -> {
            String text = addTodoField.getText().trim();
            if (!text.isEmpty()) {
                todoModel.addElement(new TodoItem(text));
                addTodoField.setText("");
            }
        });

        addPanel.add(addTodoField, BorderLayout.CENTER);
        addPanel.add(addTodoBtn, BorderLayout.EAST);

        todoPanel.add(addPanel, BorderLayout.SOUTH);

        add(todoPanel, BorderLayout.EAST);

        // ========== 버튼 ==========
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        startBtn = new JButton("시작");
        pauseBtn = new JButton("일시정지");
        resetBtn = new JButton("리셋");

        pauseBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        startBtn.addActionListener(e -> startPomodoro());
        pauseBtn.addActionListener(e -> pauseResume());
        resetBtn.addActionListener(e -> resetPomodoro());

        btnPanel.add(startBtn);
        btnPanel.add(pauseBtn);
        btnPanel.add(resetBtn);

        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ================= 할 일 정렬 함수 =================
    private void sortTodoList() {
        java.util.List<TodoItem> tmp = new java.util.ArrayList<>();
        for (int i = 0; i < todoModel.size(); i++) tmp.add(todoModel.get(i));

        // done == false(미완료) → 위쪽
        tmp.sort((a, b) -> Boolean.compare(a.done, b.done));

        todoModel.clear();
        for (TodoItem t : tmp) todoModel.addElement(t);
    }

    // ================= Pomodoro Start =================
    private void startPomodoro() {
        try {
            focusMin = Integer.parseInt(focusField.getText().trim());
            breakMin = Integer.parseInt(breakField.getText().trim());
            repeatCount = Integer.parseInt(repeatField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "숫자를 정확히 입력해 주세요.");
            return;
        }

        isFocusSession = true;
        currentRepeat = 1;

        startSession();

        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resetBtn.setEnabled(true);

        focusField.setEnabled(false);
        breakField.setEnabled(false);
        repeatField.setEnabled(false);
    }

    private void startSession() {

        if (isFocusSession) {
            remainingSeconds = focusMin * 60;
            sessionLabel.setText("집중 세션 (" + currentRepeat + "/" + repeatCount + ")");
            circleTimer.setFillColor(new Color(70, 140, 255));
        } else {
            remainingSeconds = breakMin * 60;
            sessionLabel.setText("휴식 세션");
            circleTimer.setFillColor(new Color(70, 200, 120));
        }

        totalSeconds = remainingSeconds;

        runTimer();
    }

    private void runTimer() {

        if (timer != null) timer.stop();

        timer = new Timer(1000, e -> {

            remainingSeconds--;
            circleTimer.setTimeText(formatTime(remainingSeconds));

            double ratio = (double) remainingSeconds / totalSeconds;
            circleTimer.smoothSetProgress(ratio);

            if (remainingSeconds <= 0) {
                timer.stop();
                nextSession();
            }
        });

        circleTimer.setTimeText(formatTime(remainingSeconds));
        circleTimer.setProgress(1.0);

        timer.start();
    }

    private void nextSession() {
        if (isFocusSession) {
            isFocusSession = false;
            startSession();
        } else {
            isFocusSession = true;
            currentRepeat++;

            if (currentRepeat > repeatCount) {
                finishPomodoro();
                return;
            }

            startSession();
        }
    }

    private void finishPomodoro() {

        circleTimer.setTimeText("끝!");
        circleTimer.smoothSetProgress(0);
        sessionLabel.setText("모든 세션 완료 🎉");

        pauseBtn.setEnabled(false);

        // 주간 통계 저장
        WeeklyStats.addPomodoroSession();

        // 완료된 할 일 자동 삭제
        for (int i = todoModel.size() - 1; i >= 0; i--) {
            if (todoModel.get(i).done) {
                todoModel.remove(i);
            }
        }
    }

    private void pauseResume() {
        if (timer.isRunning()) {
            timer.stop();
            pauseBtn.setText("재개");
        } else {
            timer.start();
            pauseBtn.setText("일시정지");
        }
    }

    private void resetPomodoro() {

        if (timer != null) timer.stop();

        circleTimer.setProgress(1.0);
        circleTimer.setTimeText("00:00");
        sessionLabel.setText("");

        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        focusField.setEnabled(true);
        breakField.setEnabled(true);
        repeatField.setEnabled(true);

        pauseBtn.setText("일시정지");
    }

    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }
}
