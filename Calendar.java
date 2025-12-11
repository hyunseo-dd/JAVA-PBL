import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calendar extends JFrame {

    private YearMonth currentMonth = YearMonth.now();
    private JPanel calendarPanel;
    private JLabel monthLabel;

    // 左侧任务列表
    private DefaultListModel<Tassk> taskListModel = new DefaultListModel<>();
    private JList<Tassk> taskList;

    public Calendar() {
        setTitle("日历");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar();       // 顶部月份显示 + 翻页按钮
        buildTaskPanel();    // 左侧任务列表
        buildCalendar();     // 中央日历格子

        setVisible(true);
    }

    private void buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        // 左上角“时钟”按钮（跳转到 TIMER 页面）
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        navPanel.setBackground(Color.WHITE);

        JButton timerBtn = new JButton("时钟");
        timerBtn.setFocusPainted(false);
        timerBtn.setBorder(new LineBorder(Color.BLACK, 1));

        // 点击跳转 TIMER 页面
        timerBtn.addActionListener(e -> {
            new TIMER();  // 打开时钟界面
            dispose();    // 关闭日历界面
        });

        navPanel.add(timerBtn);

        // 将按钮放在左上角，不影响原来的月份与翻页按钮位置
        top.add(navPanel, BorderLayout.WEST);

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        top.add(monthLabel, BorderLayout.CENTER);

        JPanel rightBtnPanel = new JPanel();
        rightBtnPanel.setBackground(Color.WHITE);
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        rightBtnPanel.add(prevBtn);
        rightBtnPanel.add(nextBtn);

        top.add(rightBtnPanel, BorderLayout.EAST);

        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendar();
        });

        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendar();
        });

        add(top, BorderLayout.NORTH);
    }

    private void buildTaskPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("任务列表"));
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));

        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Tassk task = taskListModel.get(index);
                    if (e.getClickCount() == 1) { // 单击切换完成状态
                        task.completed = !task.completed;
                        refreshTaskList();
                        refreshCalendar();
                    } else if (e.getClickCount() == 2) { // 双击取消完成
                        task.completed = false;
                        refreshTaskList();
                        refreshCalendar();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
    }

    private void buildCalendar() {
        calendarPanel = new JPanel(new GridLayout(0, 7));
        refreshCalendar();
        add(calendarPanel, BorderLayout.CENTER);
    }

    private void refreshCalendar() {
        if (calendarPanel == null) return;
        calendarPanel.removeAll();

        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        YearMonth ym = currentMonth;
        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
        int daysInMonth = ym.lengthOfMonth();

        for (int i = 1; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day);
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            dayPanel.setBorder(new LineBorder(Color.BLACK, 1));
            dayPanel.setBackground(Color.WHITE);

            JLabel dayLabel = new JLabel(String.valueOf(day));
            dayLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            dayPanel.add(dayLabel);

            List<Tassk> tasks = TasskSer.getInstance().getTasks(date);
            for (Tassk t : tasks) {
                JLabel tLabel = new JLabel(t.title);
                tLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
                if (t.completed) {
                    tLabel.setText("<html><strike>" + t.title + "</strike></html>");
                    tLabel.setForeground(Color.GRAY);
                } else {
                    if (t.priority == 1) tLabel.setForeground(Color.RED);
                    else if (t.priority == 2) tLabel.setForeground(Color.ORANGE);
                    else tLabel.setForeground(Color.GREEN.darker());
                }
                dayPanel.add(tLabel);
            }

            dayPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        addTaskDialog(date);
                    }
                }
            });

            calendarPanel.add(dayPanel);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addTaskDialog(LocalDate date) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField titleField = new JTextField();
        String[] options = {"高", "中", "低"};
        JComboBox<String> priorityBox = new JComboBox<>(options);
        panel.add(new JLabel("任务名称:"));
        panel.add(titleField);
        panel.add(new JLabel("优先级:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "添加任务", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            int priority = priorityBox.getSelectedIndex() + 1;
            if (!title.isEmpty()) {
                Tassk task = new Tassk(title, priority, date);
                TasskSer.getInstance().addTask(date, task);
                taskListModel.addElement(task);
                refreshTaskList();
                refreshCalendar();
            }
        }
    }

    // 刷新左侧任务列表，排序交给 TaskService
    private void refreshTaskList() {
        List<Tassk> allTasks = Collections.list(taskListModel.elements());
        // 按优先级和完成状态排序
        allTasks.sort((t1, t2) -> {
            if (t1.completed != t2.completed) {
                return Boolean.compare(t1.completed, t2.completed);
            }
            return Integer.compare(t2.priority, t1.priority);
        });
        taskListModel.clear();
        for (Tassk t : allTasks) {
            taskListModel.addElement(t);
        }
    }

    class TaskCellRenderer extends JCheckBox implements ListCellRenderer<Tassk> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Tassk> list, Tassk value, int index,
            boolean isSelected, boolean cellHasFocus) {
            setText(value.completed ? "<html><strike>" + value.title + "</strike></html>" : value.title);
            setSelected(value.completed);
            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            setFont(new Font("微软雅黑", Font.PLAIN, 14));

            if (!value.completed) {
                if (value.priority == 1) setForeground(Color.RED);
                else if (value.priority == 2) setForeground(Color.ORANGE);
                else setForeground(Color.GREEN.darker());
            } else {
                setForeground(Color.GRAY);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calendar::new);
    }
}
