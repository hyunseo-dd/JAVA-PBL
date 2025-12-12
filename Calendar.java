import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calendar extends JFrame {

    private YearMonth currentMonth = YearMonth.now();
    private JPanel calendarPanel;
    private JLabel monthLabel;

    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList;

    public Calendar() {
        setTitle("일정 관리 달력");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar();     
        buildTaskPanel();  
        buildCalendar();    

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
        leftPanel.setPreferredSize(new Dimension(250, getHeight()));

        taskList = new JList<>(taskListModel);
        
        refreshTaskList(); 
        
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Task task = taskListModel.get(index);
                    if (e.getClickCount() == 1) { 
                        TaskService.getInstance().completeTask(task.getId());
                        refreshTaskList();
                        refreshCalendar();
                    } else if (e.getClickCount() == 2) { 
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
        // 7열 그리드 (일, 월, 화, 수, 목, 금, 토)
        calendarPanel = new JPanel(new GridLayout(0, 7));
        refreshCalendar();
        add(calendarPanel, BorderLayout.CENTER);
    }

    private void refreshCalendar() {
        if (calendarPanel == null) return;
        calendarPanel.removeAll();

        monthLabel.setText(currentMonth.getYear() + "년 " + currentMonth.getMonthValue() + "월");

        // ✅ 요일 헤더: 일요일부터 시작하도록 수정
        String[] week = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < week.length; i++) {
            JLabel lbl = new JLabel(week[i], SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            
            // ✅ 요일 색상 고정 (일요일:빨강, 토요일:파랑)
            if (i == 0) lbl.setForeground(Color.RED);
            else if (i == 6) lbl.setForeground(Color.BLUE);
            else lbl.setForeground(Color.BLACK);
            
            calendarPanel.add(lbl);
        }
        
        YearMonth ym = currentMonth;
        LocalDate firstOfMonth = ym.atDay(1);
        int daysInMonth = ym.lengthOfMonth();

        // ✅ 시작 요일 계산 수정 (일요일=0, 월요일=1 ... 토요일=6)
        // DayOfWeek.getValue()는 월(1)~일(7). 
        // 일요일을 0으로 만들기 위해 % 7 연산 사용.
        int dayOfWeekVal = firstOfMonth.getDayOfWeek().getValue(); 
        int startOffset = dayOfWeekVal % 7; 

        // 빈 칸 채우기
        for (int i = 0; i < startOffset; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // 날짜 채우기
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day);
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            dayPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            dayPanel.setBackground(Color.WHITE);

            JLabel dayLabel = new JLabel(String.valueOf(day));
            dayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            
            // ✅ 날짜 숫자 색상 적용 (일요일:빨강, 토요일:파랑)
            DayOfWeek dow = date.getDayOfWeek();
            if (dow == DayOfWeek.SUNDAY) dayLabel.setForeground(Color.RED);
            else if (dow == DayOfWeek.SATURDAY) dayLabel.setForeground(Color.BLUE);
            else dayLabel.setForeground(Color.BLACK);
            
            dayPanel.add(dayLabel);

            // TaskService에서 해당 날짜의 Task 가져오기
            List<Task> tasks = TaskService.getInstance().getTasks(date); 
            
            for (Task t : tasks) {
                JLabel tLabel = new JLabel(t.getTitle());
                tLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                
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

        // 남은 셀 채우기 (레이아웃 유지용)
        int totalCells = calendarPanel.getComponentCount();
        int remainingCells = 42 - (totalCells - 7); // 헤더 제외 계산 로직 보정 필요 시 수정 가능하나, GridLayout이라 자동 줄바꿈 됨
        // GridLayout 특성상 빈 라벨을 채워주면 모양이 더 이쁨
        while (calendarPanel.getComponentCount() % 7 != 0) {
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
            int priority = priorityBox.getSelectedIndex() + 1; 
            
            if (!title.isEmpty()) {
                TaskService.getInstance().addTask(title, priority, date.toString()); 
                
                refreshTaskList();
                refreshCalendar();
            }
        }
    }
    
    private void refreshTaskList() {
        taskListModel.clear();
        List<Task> allTasks = TaskService.getInstance().getAllTasksSorted("priority"); 
        
        for (Task t : allTasks) {
            taskListModel.addElement(t);
        }
    }
}