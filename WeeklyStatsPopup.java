import javax.swing.*;
import java.awt.*;

public class WeeklyStatsPopup extends JDialog {

    public WeeklyStatsPopup(JFrame parent) {
        super(parent, "Weekly Stats", true);

        setSize(300, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("이번 주 통계", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JLabel countLabel = new JLabel(
                "Pomodoro 완료 횟수: " + WeeklyStats.getCount(),
                SwingConstants.CENTER
        );
        countLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        add(title, BorderLayout.NORTH);
        add(countLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}
