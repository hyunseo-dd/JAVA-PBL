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

    private int editIndex = -1; // 수정 모드 여부 판단값

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

        // ===== 상단 제목 =====
        JLabel title = new JLabel(date + " 일정", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // ===== 중앙: 일정 리스트 =====
        model = new DefaultListModel<>();
        List<Task> tasks = taskService.getTasks(date);
        tasks.forEach(model::addElement);

        taskList = new JList<>(model);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        // 더블클릭 → 수정모드 진입
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) startEdit();
            }
        });

        JScrollPane scroll = new JScrollPane(taskList);
        add(scroll, BorderLayout.CENTER);

        // ===== 아래쪽 입력창 + 버튼들 =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 입력 영역
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton addOrSaveBtn = new JButton("추가");

        addOrSaveBtn.addActionListener(e -> {
            if (editIndex == -1) addTask();
            else saveEdit();
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addOrSaveBtn, BorderLayout.EAST);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        // ===== 완료 체크 / 삭제 / 수정 버튼 =====
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

    // ===================== 일정 추가 =====================
    private void addTask() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        Task task = new Task(text);
        taskService.addTask(date, task);
        model.addElement(task);

        inputField.setText("");
    }

    // ===================== 일정 삭제 =====================
    private void deleteTask() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        Task task = model.get(idx);
        taskService.removeTask(date, task);
        model.remove(idx);

        editIndex = -1;
        inputField.setText("");
    }

    // ===================== 일정 수정 시작 =====================
    private void startEdit() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        Task task = model.get(idx);
        inputField.setText(task.title);
        editIndex = idx;
    }

    // ===================== 일정 수정 저장 =====================
    private void saveEdit() {
        if (editIndex == -1) return;

        String newText = inputField.getText().trim();
        if (newText.isEmpty()) return;

        Task task = model.get(editIndex);
        task.title = newText;

        reorderTasks();

        editIndex = -1;
        inputField.setText("");
    }

    // ===================== 완료 체크 =====================
    private void toggleDone() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) return;

        Task task = model.get(idx);
        task.done = !task.done;

        reorderTasks();
    }

    // ===================== 완료된 일정은 아래로 정렬 =====================
    private void reorderTasks() {

        List<Task> tasks = taskService.getTasks(date);

        tasks.sort((a, b) -> Boolean.compare(a.done, b.done)); // 완료된 일정 아래로

        model.clear();
        tasks.forEach(model::addElement);
    }
}
