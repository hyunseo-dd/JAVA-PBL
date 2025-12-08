import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule extends JDialog {

    /** TaskRow: UI 컴포넌트를 한 번만 생성해서 계속 재사용 */
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
                task.done = checkBox.isSelected();
                updateStyle();
            });

            panel.setBackground(Color.WHITE);
            panel.add(checkBox, BorderLayout.CENTER);
        }

        void updateStyle() {
            if (task.done) {
                checkBox.setSelected(true);
                checkBox.setText("<html><strike>" + task.title + "</strike></html>");
                checkBox.setForeground(Color.GRAY);
            } else {
                checkBox.setSelected(false);
                checkBox.setText(task.title);
                checkBox.setForeground(Color.BLACK);
            }
        }
    }

    // =========================================================
    // 생성자
    // =========================================================
    public Schedule(JFrame parent, CalendarView calendarView, Map<String, List<Task>> scheduleMap) {
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

                // 정렬 후 표시
                tasks.sort(Comparator.comparing(task -> task.done));

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

                // **TaskRow 객체를 한 번 만들어서 재사용**
                List<TaskRow> rows = tasks.stream()
                        .map(TaskRow::new)
                        .collect(Collectors.toList());

                // 패널에 TaskRow 배치
                refreshTaskPanel(taskPanel, tasks, rows);

                // 체크박스 리스너에서 정렬 후 패널만 다시 배치하도록 설정
                for (TaskRow row : rows) {
                    row.checkBox.addActionListener(e -> {
                        row.task.done = row.checkBox.isSelected();

                        // Task 리스트만 정렬
                        tasks.sort(Comparator.comparing(task -> task.done));

                        // 스타일 업데이트
                        rows.forEach(TaskRow::updateStyle);

                        // 패널 순서만 재배치
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

    // ======================================================
    // UI 패널 순서만 재배치 (컴포넌트는 재사용!)
    // ======================================================
    private void refreshTaskPanel(JPanel panel, List<Task> sortedTasks, List<TaskRow> rows) {
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
