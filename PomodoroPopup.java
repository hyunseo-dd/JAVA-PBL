import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

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

    private final TaskService taskService;
    private final LocalDate today = LocalDate.now();

    private DefaultListModel<Task> todoModel;
    private JList<Task> todoList;
    private JTextField addTodoField;

    public PomodoroPopup(JFrame parent, TaskService taskService) {
        super(parent, "Pomodoro Timer", true);
        this.taskService = taskService;

        setSize(520, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildUI();
        initTimer();

        setVisible(true);
    }

    private void initTimer() {
        timer = new Timer(1000, e -> updateTimer());
        timer.stop();
    }

    private void updateTimer() {
        remainingSeconds--;
        circleTimer.setTime(formatTime(remainingSeconds));

        double ratio = (double) remainingSeconds / totalSeconds;
        circleTimer.setProgress(ratio);

        if (remainingSeconds <= 0) {
            timer.stop();
            nextSession();
        }
    }

    private void buildUI() {
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        circleTimer = new CircularProgressBar();
        circleTimer.setPreferredSize(new Dimension(330, 330));

        sessionLabel = new JLabel("", SwingConstants.CENTER);
        sessionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        centerPanel.add(circleTimer, BorderLayout.CENTER);
        centerPanel.add(sessionLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel todoPanel = new JPanel(new BorderLayout());
        todoPanel.setBorder(BorderFactory.createTitledBorder("오늘의 할 일"));
        todoPanel.setBackground(Color.WHITE);

        todoModel = new DefaultListModel<>();
        refreshTodoModel();

        todoList = new JList<>(todoModel);
        todoList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        todoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = todoList.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    Task t = todoModel.get(idx);
                    taskService.completeTask(t.getId());
                    refreshTodoModel();
                }
            }
        });

        todoPanel.add(new JScrollPane(todoList), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new BorderLayout());
        addTodoField = new JTextField();
        JButton addButton = new JButton("추가");

        addButton.addActionListener(e -> addTodo());

        addPanel.add(addTodoField, BorderLayout.CENTER);
        addPanel.add(addButton, BorderLayout.EAST);

        todoPanel.add(addPanel, BorderLayout.SOUTH);
        add(todoPanel, BorderLayout.EAST);

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
    }

    private void addTodo() {
        String text = addTodoField.getText().trim();
        if (text.isEmpty()) return;

        taskService.addTask(text, 1, today.toString());
        refreshTodoModel();
        addTodoField.setText("");
    }

    private void refreshTodoModel() {
        todoModel.clear();
        List<Task> tasks = taskService.getTasksForToday(); 
        tasks.forEach(todoModel::addElement);
    }

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
            circleTimer.setColor(new Color(70, 140, 255));
        } else {
            remainingSeconds = breakMin * 60;
            sessionLabel.setText("휴식 세션");
            circleTimer.setColor(new Color(70, 200, 120));
        }

        totalSeconds = remainingSeconds;
        circleTimer.setTime(formatTime(remainingSeconds));
        circleTimer.setProgress(1.0);
        timer.restart();
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
        timer.stop();
        circleTimer.setTime("끝!");
        circleTimer.setProgress(0);
        sessionLabel.setText("모든 세션 완료");
        pauseBtn.setEnabled(false);

        // ✅ 통계 저장: TaskRecord 대신 Task 객체 사용
        Task statsRecord = new Task("뽀모도로 세션", focusMin * 60 * repeatCount, "완료", true);
        DataRepository.getInstance().addTask(statsRecord);

        refreshTodoModel();
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
        timer.stop();
        circleTimer.setProgress(1.0);
        circleTimer.setTime("00:00");
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