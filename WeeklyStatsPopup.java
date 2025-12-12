// WeeklyStatsPopup.java 파일 내용

import javax.swing.*;
import java.awt.*;

public class WeeklyStatsPopup extends JDialog {
    // 💡 StatisticsService는 TaskService와 DataRepository를 통해 데이터 접근 가능

    public WeeklyStatsPopup(JFrame parent) {
        super(parent, "Weekly Stats", true);

        setSize(500, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("이번 주 통계", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // ✅ StatisticsService를 사용하여 실제 데이터 표시 (통계 서비스 코드가 필요함)
        // 현재는 StatisticsService 코드를 모르므로 임시로 빈 화면을 띄웁니다.
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(new JLabel("통계 데이터를 로딩 중입니다...")); 

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}