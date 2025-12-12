import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

// 내부 클래스: 통계 저장용 임시 레코드
class LocalTimerRecord {
    private String title;
    private int duration;

    public void setTitle(String title) { this.title = title; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getTitle() { return title; }
    public int getDuration() { return duration; }
}

class TaskRecordManager {
    public static void save(LocalTimerRecord record) {
        // TaskService와 연동
        Task task = new Task(record.getTitle(), record.getDuration() * 60, "Timer", true);
        TaskService.getInstance().addRecord(task); 
        System.out.println("✅ 통계 기록 저장 완료: " + record.getTitle());
    }
}

public class TIMER extends JFrame {

    private JTextField timeInput;
    private Timer timer;
    private int totalSeconds = 0;
    private int originalSeconds = 1;
    private ClockPanel clockPanel;

    private boolean pomodoroMode = false;
    private int pomodoroWorkSeconds = 25 * 60;
    private int pomodoroRestSeconds = 5 * 60;
    private int pomodoroCycles = 4;
    private int currentCycle = 0;
    private boolean onWork = true;

    private JTextField taskInput;
    private JList<String> taskList;
    private DefaultListModel<String> taskListModel;

    public TIMER() {
        setTitle("시계/타이머");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar();
        buildTaskSelectionPanel();

        clockPanel = new ClockPanel();
        add(clockPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JButton startBtn = new JButton("시작");
        JButton stopBtn = new JButton("정지");
        JButton resetBtn = new JButton("리셋");
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(1000, e -> {
            if(totalSeconds > 0){
                totalSeconds--;
                clockPanel.setCurrentTime(totalSeconds, originalSeconds);
            } else {
                timer.stop();
                if(pomodoroMode){
                    if(onWork){
                        saveTaskRecord(pomodoroWorkSeconds / 60, "Pomodoro 작업");
                        JOptionPane.showMessageDialog(this, "집중 단계 완료! 잠시 휴식하세요.");
                        onWork = false;
                    } else {
                        JOptionPane.showMessageDialog(this, "휴식 끝! 다음 집중 단계를 준비하세요.");
                        onWork = true;
                        currentCycle++;
                    }
                    startPomodoroCycle();
                } else {
                    JOptionPane.showMessageDialog(this, "시간이 다 되었어요!");
                    saveTaskRecord(originalSeconds / 60, "일반 타이머 작업");
                }
            }
        });

        startBtn.addActionListener(e -> startTimer());
        stopBtn.addActionListener(e -> timer.stop());
        resetBtn.addActionListener(e -> resetTimer());
        updateClockFromInput();
        setVisible(true);
    }

    private void buildTopBar(){
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        navPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("달력");
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(new LineBorder(Color.BLACK,1));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            new Calendar(); 
            dispose();
        });
        navPanel.add(backBtn);
        top.add(navPanel, BorderLayout.WEST);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        timePanel.setBackground(Color.WHITE);
        timeInput = new JTextField("10:00",5);
        timeInput.setHorizontalAlignment(JTextField.CENTER);
        timeInput.setFont(new Font("맑은 고딕", Font.PLAIN,18));
        timePanel.add(new JLabel("분:초"));
        timePanel.add(timeInput);
        top.add(timePanel, BorderLayout.CENTER);

        JPanel pomodoroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        pomodoroPanel.setBackground(Color.WHITE);
        JCheckBox pomodoroCheck = new JCheckBox("Pomodoro 모드");
        pomodoroCheck.setBackground(Color.WHITE);
        pomodoroCheck.addActionListener(e -> {
            pomodoroMode = pomodoroCheck.isSelected();
            currentCycle = 0;
            onWork = true;
            if(pomodoroMode){
                JOptionPane.showMessageDialog(this, "Pomodoro 모드 활성화");
            }
        });
        pomodoroPanel.add(pomodoroCheck);
        top.add(pomodoroPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
    }

    private void buildTaskSelectionPanel(){
        JPanel taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBackground(Color.WHITE);
        taskPanel.setBorder(BorderFactory.createTitledBorder("작업 선택"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(new JLabel("작업명:"));
        taskInput = new JTextField(10);
        inputPanel.add(taskInput);
        taskPanel.add(inputPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskListModel.addElement("작업 A");
        taskListModel.addElement("작업 B");
        taskListModel.addElement("작업 C");
        taskList = new JList<>(taskListModel);
        taskList.setVisibleRowCount(5);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        taskPanel.add(scrollPane, BorderLayout.CENTER);

        add(taskPanel, BorderLayout.WEST);
    }

    private void startTimer(){
        if(pomodoroMode){
            startPomodoroCycle();
        } else {
            updateClockFromInput();
            if(totalSeconds > 0) timer.start();
        }
    }

    private void startPomodoroCycle(){
        if(currentCycle >= pomodoroCycles){
            JOptionPane.showMessageDialog(this, "모든 Pomodoro 세션 완료!");
            return;
        }
        totalSeconds = onWork ? pomodoroWorkSeconds : pomodoroRestSeconds;
        originalSeconds = totalSeconds;
        clockPanel.setCurrentTime(totalSeconds, originalSeconds);
        timer.start();
    }

    private void resetTimer(){
        timer.stop();
        totalSeconds = 0;
        originalSeconds = 1;
        currentCycle = 0;
        onWork = true;
        clockPanel.setCurrentTime(0, 1);
    }

    private void updateClockFromInput(){
        try{
            String[] parts = timeInput.getText().split(":");
            int min = Integer.parseInt(parts[0].trim());
            int sec = (parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0);
            if(min < 0) min = 0;
            if(sec < 0) sec = 0;
            if(sec > 59){ min += sec / 60; sec = sec % 60; }
            totalSeconds = min * 60 + sec;
            originalSeconds = Math.max(1, totalSeconds);
            clockPanel.setCurrentTime(totalSeconds, originalSeconds);
        } catch(Exception e){
            totalSeconds = 0;
            originalSeconds = 1;
        }
    }

    private void saveTaskRecord(int durationMinutes, String defaultTitle){
        String title = taskInput.getText().trim();
        if(title.isEmpty() && taskList.getSelectedValue() != null){
            title = taskList.getSelectedValue();
        }
        if(title.isEmpty()) title = defaultTitle;

        LocalTimerRecord record = new LocalTimerRecord();
        record.setTitle(title);
        record.setDuration(durationMinutes);
        
        TaskRecordManager.save(record);
    }

    class ClockPanel extends JPanel{
        private int currentSeconds = 0, maxSeconds = 1;
        public void setCurrentTime(int s, int max){
            currentSeconds = s;
            maxSeconds = Math.max(1, max);
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(230,230,230));
            g2.fillOval(x, y, size, size);
            GradientPaint gradient = new GradientPaint(x, y, new Color(250,250,255), x+size, y+size, new Color(225,225,240));
            g2.setPaint(gradient);
            g2.fillOval(x+12, y+12, size-24, size-24);

            float progress = (float) currentSeconds / maxSeconds;
            int angle = (int) (360 * progress);
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(90,140,255));
            g2.drawArc(x+20, y+20, size-40, size-40, 90, -angle);

            int min = currentSeconds / 60, sec = currentSeconds % 60;
            String text = String.format("%02d:%02d", min, sec);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("맑은 고딕", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(text);
            g2.drawString(text, getWidth()/2 - strWidth/2, getHeight()/2 + 20);
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TIMER::new);
    }
}