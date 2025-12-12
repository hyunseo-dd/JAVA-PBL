import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class TimerAndStatsDialog extends JDialog {

    private final TaskService taskService;
    private JTabbedPane tabbedPane;
    private TenMinuteTimer timerPanel;
    private JPanel statsPanel;
    
    // 통계 라벨들
    private JLabel lblTodayCount, lblTodayTime, lblTodayRate, lblWeekTime, lblMostTask;

    public TimerAndStatsDialog(JFrame parent, TaskService taskService) {
        super(parent, "Pomodoro & Statistics", true);
        this.taskService = taskService;

        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        tabbedPane.addTab("⏱ 타이머", createTimerPanel());
        tabbedPane.addTab("📊 주간 통계", createStatsPanel());

        // ✅ 핵심: 탭을 변경할 때마다 '자동으로' 통계 갱신 (새로고침 버튼 필요 없음)
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 1) { // 1번 인덱스가 '주간 통계'
                    updateStatisticsUI();
                }
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    // 외부에서 특정 탭 열기용
    public void showStatsTab() {
        tabbedPane.setSelectedIndex(1);
        updateStatisticsUI();
    }

    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        timerPanel = new TenMinuteTimer();
        
        // 타이머 종료 시 저장 로직
        timerPanel.setOnFinishListener(() -> {
            Task record = new Task("집중 세션", 600, "기쁨", true);
            taskService.addRecord(record);
            System.out.println("✅ 타이머 종료 -> 데이터 저장 완료");
            
            // 타이머가 끝나면 통계 UI도 미리 한 번 갱신
            updateStatisticsUI();
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.WHITE);
        
        JButton startBtn = new JButton("▶ 시작");
        JButton resetBtn = new JButton("🔄 재설정");
        
        startBtn.addActionListener(e -> {
            timerPanel.startTimer();
            startBtn.setText("진행 중...");
        });
        
        resetBtn.addActionListener(e -> {
            timerPanel.resetTimer(); 
            startBtn.setText("▶ 시작");
        });

        btnPanel.add(startBtn);
        btnPanel.add(resetBtn);

        panel.add(new JLabel("집중 타이머", 0), BorderLayout.NORTH);
        panel.add(timerPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblTodayCount = new JLabel("-");
        lblTodayTime = new JLabel("-");
        lblTodayRate = new JLabel("-");
        lblWeekTime = new JLabel("-");
        lblMostTask = new JLabel("-");
        
        Font f = new Font("맑은 고딕", Font.PLAIN, 18);
        JLabel[] labels = {lblTodayCount, lblTodayTime, lblTodayRate, lblWeekTime, lblMostTask};
        
        for(JLabel l : labels){
            l.setFont(f);
            statsPanel.add(l);
            statsPanel.add(Box.createVerticalStrut(20)); // 간격 좀 더 넓게
        }
        
        // ❌ 새로고침 버튼 삭제함! (자동 갱신되니까 필요 없음)
        
        return statsPanel;
    }

    private void updateStatisticsUI() {
        try {
            // 통계 서비스 생성 (데이터 새로 읽기)
            StatisticsService stats = new StatisticsService();
            
            int count = stats.getTodayRecords().size();
            int todaySec = stats.getTodayTotalFocusSec();
            int weekSec = stats.getWeeklyTotalFocusSec();
            
            lblTodayCount.setText("오늘 완료: " + count + "건");
            lblTodayTime.setText("오늘 집중: " + (todaySec / 60) + "분");
            lblTodayRate.setText(String.format("달성률: %.1f%%", stats.getTodayAchievementRate()));
            lblWeekTime.setText("주간 집중: " + (weekSec / 60) + "분");
            lblMostTask.setText("주력 작업: " + stats.getMostFocusedTask());
            
            // 화면 강제 갱신
            statsPanel.revalidate();
            statsPanel.repaint();
            
            System.out.println("📊 통계 화면 자동 갱신 완료");
            
        } catch (Exception e) {
            e.printStackTrace();
            lblTodayCount.setText("데이터 로드 오류");
        }
    }
}