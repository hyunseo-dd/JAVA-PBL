import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;

public class SchedulePopup extends JDialog {

    private JList<Task> scheduleListUI;
    private DefaultListModel<Task> listModel;
    private JTextArea inputArea;
    private Map<String, List<Task>> scheduleMap;
    private String dateKey;

    public SchedulePopup(JFrame parent, String dateKey, Map<String, List<Task>> scheduleMap) {
        super(parent, dateKey + " 일정 관리", true);

        this.scheduleMap = scheduleMap;
        this.dateKey = dateKey;

        setSize(350, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel title = new JLabel(dateKey + " 일정", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        List<Task> existing = scheduleMap.get(dateKey);

        if (existing != null) {
            existing.sort(Comparator.comparing(task -> task.done));
            for (Task t : existing) listModel.addElement(t);
        }

        scheduleListUI = new JList<>(listModel);
        scheduleListUI.setCellRenderer(new TaskRenderer());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(scheduleListUI), BorderLayout.CENTER);

        inputArea = new JTextArea(3, 10);
        inputArea.setLineWrap(true);
        centerPanel.add(new JScrollPane(inputArea), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));

        JButton addBtn = new JButton("추가");
        JButton doneBtn = new JButton("완료");
        JButton deleteBtn = new JButton("삭제");

        addBtn.addActionListener(e -> addSchedule());
        doneBtn.addActionListener(e -> toggleDone());
        deleteBtn.addActionListener(e -> deleteSchedule());

        bottomPanel.add(addBtn);
        bottomPanel.add(doneBtn);
        bottomPanel.add(deleteBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addSchedule() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "추가할 일정이 없습니다.");
            return;
        }

        List<Task> list = scheduleMap.get(dateKey);
        if (list == null) list = new ArrayList<>();

        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                list.add(new Task(line.trim()));
            }
        }

        list.sort(Comparator.comparing(task -> task.done));

        listModel.clear();
        for (Task t : list) listModel.addElement(t);

        scheduleMap.put(dateKey, list);
        inputArea.setText("");
    }

    private void deleteSchedule() {
        int idx = scheduleListUI.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 일정을 선택해 주세요.");
            return;
        }

        List<Task> list = scheduleMap.get(dateKey);
        if (list != null) list.remove(idx);

        listModel.remove(idx);

        if (list.isEmpty()) scheduleMap.remove(dateKey);
    }

    private void toggleDone() {
        int idx = scheduleListUI.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "완료할 일정을 선택해 주세요.");
            return;
        }

        List<Task> list = scheduleMap.get(dateKey);
        Task t = list.get(idx);

        t.done = !t.done;

        list.sort(Comparator.comparing(task -> task.done));

        listModel.clear();
        for (Task task : list) listModel.addElement(task);
    }
}

// 렌더러 (취소선)
class TaskRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Task t = (Task) value;

        if (t.done) {
            lbl.setText("<html><strike>" + t.text + "</strike></html>");
            lbl.setForeground(Color.GRAY);
        } else {
            lbl.setText(t.text);
            lbl.setForeground(Color.BLACK);
        }

        return lbl;
    }
}
