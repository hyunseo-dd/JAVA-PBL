// Calendar.java 파일 전체를 덮어쓰세요

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

    // Tassk → Task로 변경
    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList;

    public Calendar() {
        setTitle("일정 관리 달력");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar();       // 顶部月份显示 + 翻页按钮
        buildTaskPanel();    // 왼쪽 task list
        buildCalendar();     // 중앙 달력

        setVisible(true);
    }

    private void buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        navPanel.setBackground(Color.WHITE);

        JButton timerBtn = new JButton("시계/타이머");
        timerBtn.setFocusPainted(false);
        timerBtn.setBorder(new LineBorder(Color.BLACK, 1));

        // TIMER 클래스 호출 (TIMER.java 파일에 정의된 클래스)
        timerBtn.addActionListener(e -> {
            new TIMER();  
            dispose();    
        });

        navPanel.add(timerBtn);
        top.add(navPanel, BorderLayout.WEST);

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
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
        leftPanel.setBorder(BorderFactory.createTitledBorder("할 일 목록"));
        leftPanel.setPreferredSize(new Dimension(250, getHeight())); // 너비 조정

        taskList = new JList<>(taskListModel);
        // TaskCellRenderer가 Task 객체를 받도록 수정해야 합니다. (이후 단계에서)
        // taskList.setCellRenderer(new TaskCellRenderer()); 
        
        // 데이터 로드 (TaskService를 통해)
        refreshTaskList(); 
        
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Task task = taskListModel.get(index);
                    // Single-click to toggle completion status
                    if (e.getClickCount() == 1) { 
                        // TaskService의 완료/루틴 로직 사용
                        TaskService.getInstance().completeTask(task.getId().toString());
                        refreshTaskList();
                        refreshCalendar();
                    } else if (e.getClickCount() == 2) { 
                        // Double-click to un-complete or edit
                        // 여기서는 일단 완료 상태를 다시 미완료로 돌린다고 가정 (로직은 TaskService에 위임)
                        task.setCompleted(false); 
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

        monthLabel.setText(currentMonth.getYear() + "년 " + currentMonth.getMonthValue() + "월");

        String[] week = {"월", "화", "수", "목", "금", "토", "일"};
        for (String w : week) {
            JLabel lbl = new JLabel(w, SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            calendarPanel.add(lbl);
        }
        
        YearMonth ym = currentMonth;
        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
        int daysInMonth = ym.lengthOfMonth();

        int start = dayOfWeek % 7; // 월요일 시작 (1=월, 7=일 -> 0=월, 6=일)

        for (int i = 0; i < start; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day);
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            dayPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            dayPanel.setBackground(Color.WHITE);

            JLabel dayLabel = new JLabel(String.valueOf(day));
            dayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            dayPanel.add(dayLabel);

            // TaskService에서 해당 날짜의 Task를 가져옵니다.
            List<Task> tasks = TaskService.getInstance().getTasks(date); 
            
            for (Task t : tasks) {
                JLabel tLabel = new JLabel(t.getTitle());
                tLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                
                // 완료 상태 및 우선순위별 스타일 적용 (Task.java 필드에 맞춤)
                if (t.isCompleted()) {
                    tLabel.setText("<html><strike>" + t.getTitle() + "</strike></html>");
                    tLabel.setForeground(Color.GRAY);
                } else {
                    if (t.getPriority() == 1) tLabel.setForeground(Color.RED);
                    else if (t.getPriority() == 2) tLabel.setForeground(Color.ORANGE);
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

        // 남은 셀 채우기
        int remainingCells = 42 - calendarPanel.getComponentCount(); // 6주 * 7일 = 42
        for (int i = 0; i < remainingCells; i++) {
             calendarPanel.add(new JLabel(""));
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addTaskDialog(LocalDate date) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField titleField = new JTextField();
        String[] options = {"높음(1)", "중간(2)", "낮음(3)"};
        JComboBox<String> priorityBox = new JComboBox<>(options);
        
        panel.add(new JLabel("할 일 제목:"));
        panel.add(titleField);
        panel.add(new JLabel("우선순위:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "할 일 추가 - " + date, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            // 선택된 항목의 인덱스 + 1을 우선순위로 사용 (0→1, 1→2, 2→3)
            int priority = priorityBox.getSelectedIndex() + 1; 
            
            if (!title.isEmpty()) {
                // TaskService를 통해 백엔드에 저장 (I/O 포함)
                TaskService.getInstance().addTask(title, priority, date.toString()); 
                
                refreshTaskList();
                refreshCalendar();
            }
        }
    }
    
    // 왼쪽 Task List를 TaskService를 통해 다시 로드합니다.
    private void refreshTaskList() {
        taskListModel.clear();
        // 모든 할 일을 가져와서 정렬
        List<Task> allTasks = TaskService.getInstance().getAllTasksSorted("priority"); 
        
        // UI에 반영
        for (Task t : allTasks) {
            taskListModel.addElement(t);
        }
    }
