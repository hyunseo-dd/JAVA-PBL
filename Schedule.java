import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule extends JDialog {

    private static class TaskRow {
        Task task;
        JCheckBox checkBox;
        JPanel panel;

        TaskRow(Task t) {
            this.task = t;
            this.checkBox = new JCheckBox();
            this.panel = new JPanel(new BorderLayout());

            checkBox.setFont(new Font("Dialog", Font.PLAIN, 16));
            checkBox.setOpaque(true);
            checkBox.setBackground(Color.WHITE);

            updateStyle();

            checkBox.addActionListener(e -> {
                task.setCompleted(checkBox.isSelected());   // ✅ setter
                updateStyle();
            });

            panel.setBackground(Color.WHITE);
            panel.add(checkBox, BorderLayout.CENTER);
        }

        void updateStyle() {
            if (task.isCompleted()) {   // ✅ getter
                checkBox.setSelected(true);
                checkBox.setText("<html><strike>" + task.getTitle() + "</strike></html>");
                checkBox.setForeground(Color.GRAY);
            } else {
                checkBox.setSelected(false);
                checkBox.setText(task.getTitle());
                checkBox.setForeground(Color.BLACK);
            }
        }
    }

    public Schedule(JFrame parent,
                    CalendarView calendarView,
                    Map<String, List<Task>> scheduleMap) {

        super(parent, "Schedule List", true);

        setSize(calendarView.getWidth(), calendarView.getHeight());
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("Schedule List", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 24));
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        TreeMap<String, List<Task>> sorted = new TreeMap<>(scheduleMap);

        if (sorted.isEmpty()) {
            JLabel empty = new JLabel("저장된 일정이 없습니다.", SwingConstants.CENTER);
            empty.setFont(new Font("Dialog", Font.PLAIN, 18));
            empty.setOpaque(true);
            empty.setBackground(Color.WHITE);
            container.add(empty);
        } else {
            for (Map.Entry<String, List<Task>> entry : sorted.entrySet()) {

                String date = entry.getKey();
                List<Task> tasks = entry.getValue();

                // ✅ 완료 여부 기준 정렬
                tasks.sort(Comparator.comparing(Task::isCompleted));

                JLabel dateLabel = new JLabel("▶ " + date);
                dateLabel.setFont(new Font("Dialog", Font.BOLD, 18));
                dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
                dateLabel.setOpaque(true);
                dateLabel.setBackground(Color.WHITE);
                container.add(dateLabel);

                JPanel taskPanel = new JPanel();
                taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
                taskPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
                taskPanel.setBackground(Color.WHITE);

                List<TaskRow> rows = tasks.stream()
                        .map(TaskRow::new)
                        .collect(Collectors.toList());

                refreshTaskPanel(taskPanel, tasks, rows);

                for (TaskRow row : rows) {
                    row.checkBox.addActionListener(e -> {

                        row.task.setCompleted(row.checkBox.isSelected()); // ✅ setter

                        tasks.sort(Comparator.comparing(Task::isCompleted));

                        rows.forEach(TaskRow::updateStyle);

                        refreshTaskPanel(taskPanel, tasks, rows);
                    });
                }

                container.add(taskPanel);
            }
        }

        JScrollPane scroll = new JScrollPane(container);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton close = new JButton("닫기");
        close.addActionListener(e -> dispose());
        close.setBackground(Color.WHITE);
        add(close, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshTaskPanel(JPanel panel,
                                  List<Task> sortedTasks,
                                  List<TaskRow> rows) {

        panel.removeAll();

        for (Task t : sortedTasks) {
            TaskRow row = rows.stream()
                    .filter(r -> r.task == t)
                    .findFirst()
                    .orElse(null);

            if (row != null) {
                panel.add(row.panel);
            }
        }

        panel.revalidate();
        panel.repaint();
    }
}
