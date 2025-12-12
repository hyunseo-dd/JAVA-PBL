import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class SchedulePopup extends JDialog {

    private final LocalDate date;
    private final TaskService taskService;

    private DefaultListModel<Task> model;
    private JList<Task> taskList;
    private JTextField inputField;

    private int editIndex = -1;

    public SchedulePopup(JFrame parent, LocalDate date, TaskService taskService) {
        super(parent, date.toString() + " 일정 관리", true);

        this.date = date;
        this.taskService = taskService;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        loadUI();
        setVisible(true);
    }

    private void loadUI() {

        JLabel title = new JLabel(date + " 일정", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultListModel<>();
        refreshModel();

        taskList = new JList<>(model);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) startEdit();
            }
        });

        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        JButton addOrSaveBtn = new JButton("추가");

        addOrSaveBtn.addActionListener(e -> {
            if (editIndex == -1) addTask();
            else saveEdit();
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addOrSaveBtn, BorderLayout.EAST);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new GridLayout(1, 3, 10, 0));

        JButton doneBtn = new JButton("완료");
        JButton deleteBtn = new JButton("삭제");
        JButton editBtn = new JButton("수정");

        doneBtn.addActionListener(e -> toggleDone());
        deleteBtn.addActionListener(e -> deleteTask());
        editBtn.addActionListener(e -> startEdit());

        actionPanel.add(doneBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(editBtn);

        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ===================== Task 처리 =====================

    private void addTask() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // ✅ 파일 기반 TaskService API 사용
        taskService.addTask(
                text,
                1,                 // priority (임시값)
                date.toString()    // dueDate
        );

        refreshModel();
        inputField.setText("");
    }

    private void deleteTask() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        Task task = model.get(idx);
        taskService.deleteTask(task.getId());   // ✅ ID 기반 삭제

        refreshModel();
        editIndex = -1;
        inputField.setText("");
    }

    private void startEdit() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        inputField.setText(model.get(idx).getTitle());
        editIndex = idx;
    }

    private void saveEdit() {
        if (editIndex == -1) return;

        String newText = inputField.getText().trim();
        if (newText.isEmpty()) return;

        Task task = model.get(editIndex);

        // ✅ Service에 수정 위임
        taskService.updateTaskDetails(
                task.getId(),
                newText,
                null,              // priority 변경 없음
                null,              // dueDate 변경 없음
                null               // cycle 변경 없음
        );

        refreshModel();
        editIndex = -1;
        inputField.setText("");
    }

    private void toggleDone() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        Task task = model.get(idx);
        taskService.completeTask(task.getId());   // ✅ 완료/루틴 로직 포함

        refreshModel();
    }

    private void refreshModel() {
        model.clear();
        List<Task> tasks = taskService.getTasks(date);
        tasks.forEach(model::addElement);
    }
}
