import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;

// 假设存在 TaskRecord 和 TaskService
// TaskRecord 类（放在 TIMER.java 顶部或单独文件）
// 假设这个类在 TIMER.java 顶部或单独文件 TaskRecord.java
class TaskRecord {
    private String title;
    private int duration;
    private long completedAt;

    public void setTitle(String title) { this.title = title; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    // --- 新增 getter ---
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public long getCompletedAt() { return completedAt; }

    @Override
    public String toString() {
        return "TaskRecord{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", completedAt=" + completedAt +
                '}';
    }
}


// 模拟原 TaskRecordManager 保存功能
class TaskRecordManager {
    public static void save(TaskRecord record) {
        System.out.println("任务已保存: " + record);
    }
}
public class TIMER extends JFrame {

    private JTextField timeInput;
    private Timer timer;
    private int totalSeconds = 0;
    private int originalSeconds = 1;
    private ClockPanel clockPanel;

    // Pomodoro逻辑
    private boolean pomodoroMode = false;
    private int pomodoroWorkSeconds = 25 * 60;
    private int pomodoroRestSeconds = 5 * 60;
    private int pomodoroCycles = 4;
    private int currentCycle = 0;
    private boolean onWork = true;

    // 任务选择
    private JTextField taskInput;
    private JList<String> taskList;
    private DefaultListModel<String> taskListModel;
    // 保存完成状态
    private Map<String, Boolean> taskCompletedMap = new HashMap<>();

    public TIMER() {
        setTitle("Clock Style计时器");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar();
        buildTaskSelectionPanel();

        clockPanel = new ClockPanel();
        add(clockPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");
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
                        // ★★★ 写入统计数据（Pomodoro 工作结束）
                        saveTaskRecord(pomodoroWorkSeconds / 60, "Pomodoro任务");

                        JOptionPane.showMessageDialog(this, "专注阶段完成！休息一下", "Pomodoro", JOptionPane.INFORMATION_MESSAGE);
                        onWork = false;
                    } else {
                        JOptionPane.showMessageDialog(this, "休息结束！准备下一轮专注", "Pomodoro", JOptionPane.INFORMATION_MESSAGE);
                        onWork = true;
                        currentCycle++;
                    }
                    startPomodoroCycle();
                } else {
                    JOptionPane.showMessageDialog(this, "时间到啦！", "提醒", JOptionPane.INFORMATION_MESSAGE);

                    // ★★★ 写入统计数据（普通计时结束）
                    saveTaskRecord(originalSeconds / 60, "计时器任务");
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
        JButton backBtn = new JButton("日历");
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(new LineBorder(Color.BLACK,1));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            new Calendar(); // 打开日历
            dispose();
        });
        navPanel.add(backBtn);
        top.add(navPanel, BorderLayout.WEST);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        timePanel.setBackground(Color.WHITE);
        timeInput = new JTextField("10:00",5);
        timeInput.setHorizontalAlignment(JTextField.CENTER);
        timeInput.setFont(new Font("SansSerif", Font.PLAIN,18));
        timePanel.add(new JLabel("分钟:秒"));
        timePanel.add(timeInput);
        top.add(timePanel, BorderLayout.CENTER);

        JPanel pomodoroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        pomodoroPanel.setBackground(Color.WHITE);
        JCheckBox pomodoroCheck = new JCheckBox("Pomodoro模式");
        pomodoroCheck.setBackground(Color.WHITE);
        pomodoroCheck.addActionListener(e -> {
            pomodoroMode = pomodoroCheck.isSelected();
            currentCycle = 0;
            onWork = true;
            if(pomodoroMode){
                JOptionPane.showMessageDialog(this, "Pomodoro模式已开启", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pomodoroPanel.add(pomodoroCheck);
        top.add(pomodoroPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
    }

    private void buildTaskSelectionPanel(){
        JPanel taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBackground(Color.WHITE);
        taskPanel.setBorder(BorderFactory.createTitledBorder("任务选择"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(new JLabel("任务名:"));
        taskInput = new JTextField(10);
        inputPanel.add(taskInput);
        taskPanel.add(inputPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskListModel.addElement("任务A");
        taskListModel.addElement("任务B");
        taskListModel.addElement("任务C");
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus){
                JLabel label = (JLabel) super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                String text = value.toString();
                if(taskCompletedMap.getOrDefault(text,false)){
                    label.setText("✔ " + text);
                    label.setFont(
                    label.getFont().deriveFont(
                    java.util.Collections.singletonMap(
                    java.awt.font.TextAttribute.STRIKETHROUGH,
                    java.awt.font.TextAttribute.STRIKETHROUGH_ON
        )
    )
);
;
                } else {
                    label.setText(text);
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                }
                return label;
            }
        });

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
            JOptionPane.showMessageDialog(this, "所有Pomodoro轮次已完成！", "完成", JOptionPane.INFORMATION_MESSAGE);
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

    // 保存TaskRecord并标记完成状态
    private void saveTaskRecord(int durationMinutes, String defaultTitle){
        String title = taskInput.getText().trim();
        if(title.isEmpty() && taskList.getSelectedValue() != null){
            title = taskList.getSelectedValue();
        }
        if(title.isEmpty()) title = defaultTitle;

        TaskRecord record = new TaskRecord();
        record.setTitle(title);
        record.setDuration(durationMinutes);
        record.setCompletedAt(System.currentTimeMillis());

        // 保存到原 TaskRecordManager
        TaskRecordManager.save(record);

        // ★★★ 保存到 TaskService（你要求的功能）
        try {
        Task task = new Task(
            record.getTitle(),
            1,                 // 默认优先级
            LocalDate.now()    // 今天的日期
            );

    TaskService.getInstance().addTask(LocalDate.now(), task);

} catch (Exception e) {
    e.printStackTrace();
}


        // 更新左侧任务列表
        if(!taskListModel.contains(title)){
            taskListModel.addElement(title);
        }
        taskCompletedMap.put(title,true);
        taskList.repaint();
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
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(text);
            g2.drawString(text, getWidth()/2 - strWidth/2, getHeight()/2 + 20);
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TIMER::new);
    }
}
