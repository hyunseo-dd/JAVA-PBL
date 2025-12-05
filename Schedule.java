import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

public class Schedule extends JDialog {

    public Schedule(JFrame parent, CalendarView calendarView, Map<String, List<Task>> scheduleMap) {
        super(parent, "Schedule List", true);

        // ===== 기본 세팅 =====
        setSize(calendarView.getWidth(), calendarView.getHeight());
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE); // ⭐ 전체 배경 흰색

        JLabel title = new JLabel("Schedule List", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 24));
        title.setOpaque(true);
        title.setBackground(Color.WHITE); // ⭐ 라벨 배경 흰색
        add(title, BorderLayout.NORTH);

        // ===== 컨테이너 =====
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE); // ⭐ 배경 흰색

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

                // ⭐ 완료된 일정은 아래로 정렬
                tasks.sort(Comparator.comparing(task -> task.done));

                // 날짜 라벨
                JLabel dateLabel = new JLabel("▶ " + date);
                dateLabel.setFont(new Font("Dialog", Font.BOLD, 18));
                dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
                dateLabel.setOpaque(true);
                dateLabel.setBackground(Color.WHITE); // ⭐ 날짜도 흰 배경
                container.add(dateLabel);

                // 할 일 패널
                JPanel taskPanel = new JPanel();
                taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
                taskPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
                taskPanel.setBackground(Color.WHITE); // ⭐ 배경 흰색

                // 체크박스 UI 생성
                for (Task t : tasks) {

                    JCheckBox cb = new JCheckBox();
                    cb.setFont(new Font("Dialog", Font.PLAIN, 16));
                    cb.setOpaque(true);
                    cb.setBackground(Color.WHITE); // ⭐ 체크박스 흰 배경

                    updateCheckboxStyle(cb, t);

                    // -------- 체크 이벤트 --------
                    cb.addActionListener(e -> {
                        t.done = cb.isSelected();
                        updateCheckboxStyle(cb, t);

                        // ⭐ 정렬 후 UI 업데이트
                        tasks.sort(Comparator.comparing(task -> task.done));

                        taskPanel.removeAll();
                        for (Task x : tasks) {
                            JCheckBox newCB = new JCheckBox();
                            newCB.setFont(new Font("Dialog", Font.PLAIN, 16));
                            newCB.setOpaque(true);
                            newCB.setBackground(Color.WHITE);

                            updateCheckboxStyle(newCB, x);

                            newCB.addActionListener(ev -> {
                                x.done = newCB.isSelected();
                                updateCheckboxStyle(newCB, x);

                                tasks.sort(Comparator.comparing(task -> task.done));
                                refreshTaskPanel(taskPanel, tasks);
                            });

                            taskPanel.add(newCB);
                        }

                        taskPanel.revalidate();
                        taskPanel.repaint();
                    });

                    taskPanel.add(cb);
                }

                container.add(taskPanel);
            }
        }

        // ===== Scroll =====
        JScrollPane scroll = new JScrollPane(container);
        scroll.getViewport().setBackground(Color.WHITE);  // ⭐ 스크롤 안 배경
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // ===== Close 버튼 =====
        JButton close = new JButton("닫기");
        close.addActionListener(e -> dispose());
        close.setBackground(Color.WHITE); // ⭐ 흰색
        add(close, BorderLayout.SOUTH);

        setVisible(true);
    }

    // =====================================================
    // 체크박스 스타일 업데이트
    // =====================================================
    private void updateCheckboxStyle(JCheckBox cb, Task t) {
        if (t.done) {
            cb.setSelected(true);
            cb.setText("<html><strike>" + t.text + "</strike></html>");
            cb.setForeground(Color.GRAY);
        } else {
            cb.setSelected(false);
            cb.setText(t.text);
            cb.setForeground(Color.BLACK);
        }
    }

    // =====================================================
    // 패널 새로 고침 (정렬 후)
    // =====================================================
    private void refreshTaskPanel(JPanel panel, List<Task> tasks) {
        panel.removeAll();

        for (Task t : tasks) {
            JCheckBox cb = new JCheckBox();
            cb.setFont(new Font("Dialog", Font.PLAIN, 16));
            cb.setOpaque(true);
            cb.setBackground(Color.WHITE);

            updateCheckboxStyle(cb, t);

            cb.addActionListener(e -> {
                t.done = cb.isSelected();
                updateCheckboxStyle(cb, t);
                tasks.sort(Comparator.comparing(task -> task.done));
                refreshTaskPanel(panel, tasks);
            });

            panel.add(cb);
        }

        panel.revalidate();
        panel.repaint();
    }
}
