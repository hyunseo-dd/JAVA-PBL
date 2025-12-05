import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Jinxing extends JFrame {

    private YearMonth currentMonth = YearMonth.now();
    private JPanel calendarPanel;
    private JLabel monthLabel;

    private Map<LocalDate, List<Task>> schedules = new HashMap<>();
    private final File storageFile = new File("schedules.dat");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Jinxing() {
        setTitle("Jinxing Minimal Schedule Calendar");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        loadFromFile();
        buildTopBar();
        buildCalendarGrid();
        updateCalendar();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveToFile();
            }
        });

        setVisible(true);
    }

    //顶部导航栏
    private void buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(Color.WHITE);
        top.setBorder(new LineBorder(Color.BLACK, 1));

        // 导航按钮
        JButton prev = new JButton("< Prev");
        JButton next = new JButton("Next >");
        JButton switchToTimer = new JButton("计时器");

        // 设置样式统一
        JButton[] buttons = {prev, next, switchToTimer};
        for (JButton b : buttons) {
            b.setBackground(Color.WHITE);
            b.setBorder(new LineBorder(Color.BLACK, 1));
            b.setFocusPainted(false);
        }

        prev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); updateCalendar(); });
        next.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); updateCalendar(); });
        switchToTimer.addActionListener(e -> { new ClockStyleTimer(); dispose(); });

        top.add(prev);
        top.add(next);
        top.add(switchToTimer);

        // 月份标签
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        monthLabel.setBorder(new LineBorder(Color.BLACK, 1));
        monthLabel.setOpaque(true);
        monthLabel.setBackground(Color.WHITE);
        top.add(monthLabel);

        add(top, BorderLayout.NORTH);
    }

    private JButton createTopButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setBorder(new LineBorder(Color.BLACK, 1));
        b.setFont(new Font("SansSerif", Font.PLAIN, 18));
        b.setFocusPainted(false);
        b.setOpaque(true);
        return b;
    }

    // 日历网格布局
    private void buildCalendarGrid() {
        calendarPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        calendarPanel.setBackground(Color.WHITE);
        add(calendarPanel, BorderLayout.CENTER);

        System.out.println("currentMonth: " + currentMonth);
        System.out.println("calendarPanel: " + calendarPanel);
    }

    // 更新日历
    private void updateCalendar() {
        if (calendarPanel == null) return; // 防止空指针
        calendarPanel.removeAll();

        // 周一到周日
        String[] week = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String w : week) {
            JLabel lbl = new JLabel(w, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setBorder(new LineBorder(Color.BLACK, 1));
            calendarPanel.add(lbl);
        }

        if (monthLabel != null) {
            monthLabel.setText(currentMonth.getYear() + " - " + currentMonth.getMonthValue());
        }

        LocalDate first = currentMonth.atDay(1);
        int start = (first.getDayOfWeek().getValue() + 6) % 7; // 周一=0
        int days = currentMonth.lengthOfMonth();

        for (int i = 0; i < start; i++) {
            calendarPanel.add(createDayCell("", null));
        }

        for (int day = 1; day <= days; day++) {
            final int currentDay = day;
            LocalDate date = currentMonth.atDay(currentDay);
            calendarPanel.add(createDayCell(String.valueOf(currentDay), date));
        }

        int cells = 6 * 7;
        while (calendarPanel.getComponentCount() < cells) {
            calendarPanel.add(createDayCell("", null));
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // createDayCell 修改版，显示任务标题并按优先级改变字体颜色
    private JButton createDayCell(String text, LocalDate date) {
        JButton btn = new JButton();
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(Color.BLACK, 1));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setOpaque(true);

        if (date != null) {
            List<Task> list = schedules.getOrDefault(date, new ArrayList<>());
            StringBuilder sb = new StringBuilder("<html><center>");
            sb.append("<b>").append(text).append("</b>");
            if (!list.isEmpty()) {
                sb.append("<br>");
                for (Task t : list) {
                    String color;
                    switch (t.priority) {
                        case HIGH -> color = "red";
                        case MEDIUM -> color = "orange";
                        case LOW -> color = "green";
                        default -> color = "black";
                    }
                    sb.append("<span style='font-size:10px;color:")
                      .append(color)
                      .append(";'>")
                      .append(t.title)
                      .append("</span><br>");
                }
            }
            sb.append("</center></html>");
            btn.setText(sb.toString());

            btn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        addTaskDialog(date, null);
                    } else if (e.getClickCount() == 1) {
                        showTasks(date);
                    }
                }
            });
        } else {
            btn.setText(text);
        }

        return btn;
    }

    private void showTasks(LocalDate date) {
        List<Task> list = schedules.getOrDefault(date, new ArrayList<>());
        list.sort(null);

        StringBuilder sb = new StringBuilder("日期: " + date.format(formatter) + "\n\n");
        if (list.isEmpty()) sb.append("没有日程。");
        else {
            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);
                sb.append((i + 1) + ". [" + t.priority + "] " + t.title + "\n");
                if (!t.description.isEmpty()) sb.append("    描述: " + t.description + "\n");
            }
        }

        String[] options = {"新增", "编辑", "删除", "关闭"};
        int choice = JOptionPane.showOptionDialog(this, sb.toString(),
                "日程列表", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[3]);

        if (choice == 0) addTaskDialog(date, null);
        else if (choice == 1 && !list.isEmpty()) {
            String idx = JOptionPane.showInputDialog("输入要编辑的序号:");
            if (idx != null) {
                try {
                    int i = Integer.parseInt(idx) - 1;
                    if (i >= 0 && i < list.size()) addTaskDialog(date, list.get(i));
                } catch (Exception ignored) {}
            }
        } else if (choice == 2 && !list.isEmpty()) {
            String idx = JOptionPane.showInputDialog("输入要删除的序号:");
            if (idx != null) {
                try {
                    int i = Integer.parseInt(idx) - 1;
                    if (i >= 0 && i < list.size()) {
                        list.remove(i);
                        if (list.isEmpty()) schedules.remove(date);
                        updateCalendar();
                    }
                } catch (Exception ignored) {}
            }
        }

        updateCalendar();
    }

    private void addTaskDialog(LocalDate date, Task editing) {
        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea(4, 20);
        JComboBox<TaskPriority> priorityBox = new JComboBox<>(TaskPriority.values());

        if (editing != null) {
            titleField.setText(editing.title);
            descArea.setText(editing.description);
            priorityBox.setSelectedItem(editing.priority);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("标题:"));
        panel.add(titleField);
        panel.add(new JLabel("描述:"));
        panel.add(new JScrollPane(descArea));
        panel.add(new JLabel("优先级:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                (editing == null ? "新增日程 - " : "编辑日程 - ") + date.format(formatter),
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "标题不能为空！");
                return;
            }

            List<Task> list = schedules.getOrDefault(date, new ArrayList<>());
            Task newTask = new Task(title, desc, (TaskPriority) priorityBox.getSelectedItem());

            if (editing != null) {
                int idx = list.indexOf(editing);
                if (idx >= 0) list.set(idx, newTask);
            } else list.add(newTask);

            schedules.put(date, list);
            updateCalendar();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storageFile))) {
            oos.writeObject(schedules);
        } catch (Exception ignored) {}
    }

    private void loadFromFile() {
        if (!storageFile.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storageFile))) {
            schedules = (Map<LocalDate, List<Task>>) ois.readObject();
        } catch (Exception ignored) {}
    }

    static class Task implements Serializable, Comparable<Task> {
        String title;
        String description;
        TaskPriority priority;

        public Task(String t, String d, TaskPriority p) {
            title = t;
            description = d;
            priority = p;
        }

        public int compareTo(Task o) {
            return priority.ordinal() - o.priority.ordinal();
        }
    }

    enum TaskPriority {
        HIGH, MEDIUM, LOW
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Jinxing::new);
    }
}