import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TodoTimerApp extends JFrame {

    private Timer swingTimer;
    private int secondsRemaining = 600;
    private boolean isTimerRunning = false;
    private DefaultListModel<String> listModel;
    
    private JLabel timerLabel;
    private JButton startPauseButton;
    private JButton resetButton;
    private JTextField taskInputField;
    private JList<String> todoList;

    public TodoTimerApp() {
        UIManager.put("OptionPane.messageFont", new Font("Malgun Gothic", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Malgun Gothic", Font.PLAIN, 14));

        setTitle("JUST 10min Todo-List (ADHD 전략 앱)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 650);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        
        tabbedPane.addTab("⏰ 타이머", createTimerPanel());
        tabbedPane.addTab("☑️ 오늘 할 일", createTodoPanel());
        tabbedPane.addTab("📅 주간 계획", new JPanel());
        tabbedPane.addTab("🗓️ 월간 계획", new JPanel());
        tabbedPane.addTab("📊 통계", new JPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        updateTimerLabel();
        swingTimer = new Timer(1000, new TimerActionListener());
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        timerLabel = new JLabel("10:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 72)); 
        
        startPauseButton = new JButton("▶ 시작");
        resetButton = new JButton("🔄 재설정");
        
        startPauseButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        resetButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(startPauseButton);
        buttonPanel.add(resetButton);
        
        JLabel statusLabel = new JLabel("🕒 작업 시간 ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        centerPanel.add(statusLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 20, 0);
        centerPanel.add(timerLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        centerPanel.add(buttonPanel, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        startPauseButton.addActionListener(e -> toggleTimer());
        resetButton.addActionListener(e -> resetTimer());

        return panel;
    }
    
    private JPanel createTodoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        taskInputField = new JTextField(30);
        taskInputField.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        JButton addButton = new JButton("➕ 추가");
        addButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        todoList.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        
        JScrollPane scrollPane = new JScrollPane(todoList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton completeButton = new JButton("✔️ 선택 완료/삭제");
        completeButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(completeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> addTask());
        taskInputField.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> completeTask());
        
        return panel;
    }

    private void updateTimerLabel() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            swingTimer.stop();
            startPauseButton.setText("▶ 계속");
        } else {
            swingTimer.start();
            startPauseButton.setText("⏸ 일시 정지");
        }
        isTimerRunning = !isTimerRunning;
    }

    private void resetTimer() {
        swingTimer.stop();
        secondsRemaining = 600;
        isTimerRunning = false;
        startPauseButton.setText("▶ 시작");
        updateTimerLabel();
    }
    
    private void addTask() {
        String taskText = taskInputField.getText().trim();
        if (!taskText.isEmpty()) {
            listModel.addElement(taskText);
            taskInputField.setText("");
            if (listModel.getSize() == 1) {
                todoList.setSelectedIndex(0); // 첫 할 일이면 자동 선택
            }
        } else {
            System.out.println("할 일 내용을 입력해 주세요.");
        }
    }
    
    private void completeTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            if (listModel.getSize() > 0) {
                // 다음 항목이 있다면 선택, 없다면 첫 항목 선택 (새로운 시작)
                int newIndex = (selectedIndex < listModel.getSize()) ? selectedIndex : 0;
                todoList.setSelectedIndex(newIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "완료할 할 일을 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                updateTimerLabel();
            } else {
                swingTimer.stop();
                isTimerRunning = false;
                startPauseButton.setText("▶ 시작");
                
                // --- 1. 자체 평가 팝업 (DB에 저장될 평가 기록) ---
                Object[] evaluationOptions = {"😃 기쁨", "😐 보통", "😔 슬픔"};
                int evaluationChoice = JOptionPane.showOptionDialog(
                    TodoTimerApp.this, 
                    "10분 집중 시간이 끝났어요!\n오늘의 할 일은 어떠셨나요?", 
                    "✅ 할 일 평가하기", 
                    JOptionPane.YES_NO_CANCEL_OPTION, 
                    JOptionPane.QUESTION_MESSAGE,
                    null, 
                    evaluationOptions, 
                    evaluationOptions[1] 
                );
                
                String evaluation = "";
                switch (evaluationChoice) {
                    case 0: evaluation = "기쁨"; break;
                    case 1: evaluation = "보통"; break;
                    case 2: evaluation = "슬픔"; break;
                    case JOptionPane.CLOSED_OPTION: evaluation = "평가 안 함"; break;
                }
                System.out.println("사용자 할 일 평가 (DB 저장 시점): " + evaluation); 
                // TODO: 팀원 2는 여기에 평가 결과를 DB에 저장하는 로직 추가
                
                // --- 2. 다음 행동 결정 팝업 (계속할지, 넘어갈지) ---
                int actionChoice = JOptionPane.showConfirmDialog(
                    TodoTimerApp.this, 
                    "다음 단계는 무엇인가요?\n이 일을 계속 진행할까요? (예) 아니면 다음 할 일로 넘어갈까요? (아니오)", 
                    "❓ 다음 행동 결정", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (actionChoice == JOptionPane.YES_OPTION) {
                    // '계속 진행' 선택: 타이머만 다시 시작
                    secondsRemaining = 600;
                    toggleTimer();
                    JOptionPane.showMessageDialog(TodoTimerApp.this, "다시 10분 집중 시작! 힘내세요!", "집중 시작", JOptionPane.INFORMATION_MESSAGE);
                    
                } else if (actionChoice == JOptionPane.NO_OPTION) {
                    // '다음 할 일로 넘어갈게요' 선택: 다음 할 일로 이동
                    
                    int currentIndex = todoList.getSelectedIndex();
                    int nextIndex = currentIndex + 1;
                    
                    if (listModel.getSize() == 0) {
                        JOptionPane.showMessageDialog(TodoTimerApp.this, "할 일 목록이 비어있어요. 새로운 할 일을 추가해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                        resetTimer();
                    } else if (currentIndex == -1 || nextIndex >= listModel.getSize()) {
                        // 선택된 항목이 없거나, 마지막 항목일 경우
                        todoList.setSelectedIndex(0); // 첫 번째 항목으로 이동 (순환)
                        JOptionPane.showMessageDialog(TodoTimerApp.this, "목록의 끝입니다. 다시 첫 할 일에 집중해 보세요!", "다음 할 일", JOptionPane.INFORMATION_MESSAGE);
                        resetTimer();
                    } else {
                        // 다음 항목으로 이동
                        todoList.setSelectedIndex(nextIndex);
                        JOptionPane.showMessageDialog(TodoTimerApp.this, "\"" + listModel.getElementAt(nextIndex) + "\" 할 일에 집중해 보세요!", "다음 할 일", JOptionPane.INFORMATION_MESSAGE);
                        resetTimer();
                    }
                } else {
                    // 팝업 닫기 (Cancel): 타이머 초기화만
                    resetTimer();
                    JOptionPane.showMessageDialog(TodoTimerApp.this, "타이머가 초기화되었습니다. 필요할 때 다시 시작해 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TodoTimerApp());
    }
}