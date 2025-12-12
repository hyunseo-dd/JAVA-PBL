import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

// TaskRenderer는 SchedulePopup.java 밖에 정의된 클래스라고 가정합니다.

public class SchedulePopup extends JDialog {

    private final LocalDate date;
    private final TaskService taskService;

    private DefaultListModel<Task> model;
    private JList<Task> taskList;
    private JTextField inputField;

    private int editIndex = -1;

    public SchedulePopup(JFrame parent, LocalDate date, TaskService taskService) {
        // SchedulePopup.java:29 에서 setVisible(true)가 호출됩니다.
        super(parent, date.toString() + " 일정 관리", true);

        this.date = date;
        this.taskService = taskService;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        loadUI();
        setVisible(true); // SchedulePopup.java:29
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
        // taskList.setCellRenderer(new TaskRenderer()); // TaskRenderer 클래스가 정의되어 있다면 활성화

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
        deleteBtn.addActionListener(e -> deleteTask()); // SchedulePopup.java:77
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

        // TaskService.addTask(String title, int priority, String dueDate) 호출
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
        
        // ✅ [복구 완료] Task.getId()가 이미 String을 반환하므로 toString() 제거
        // SchedulePopup.java:110
        taskService.deleteTask(task.getId()); 

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

        // Service에 수정 위임
        taskService.updateTaskDetails(
                task.getId(),
                newText,
                null,              // priority 변경 없음
                date.toString(),   // dueDate는 현재 팝업 날짜로 유지
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
        
        // ✅ [복구 완료] Task.getId()가 이미 String을 반환하므로 toString() 제거
        // SchedulePopup.java:152
        taskService.completeTask(task.getId()); 

        refreshModel();
    }

    private void refreshModel() {
        model.clear();
        // 캘린더 날짜에 해당하는 Task만 가져옵니다.
        List<Task> tasks = taskService.getTasks(date); 
        tasks.forEach(model::addElement);
    }
}