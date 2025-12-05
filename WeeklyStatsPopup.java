import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.Map;

public class WeeklyStatsPopup extends JDialog {

    public WeeklyStatsPopup(JFrame parent) {
        super(parent, "Pomodoro 주간 통계", true);

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("Weekly Pomodoro", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // 그래프 패널
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Map<DayOfWeek, Integer> stats = WeeklyStats.getWeeklyStats();

                int max = stats.values().stream().max(Integer::compare).orElse(1);
                int barHeight = 25;
                int gap = 15;
                int xStart = 120;

                int y = 40;

                g2.setFont(new Font("Dialog", Font.PLAIN, 16));

                for (DayOfWeek day : DayOfWeek.values()) {

                    int count = stats.getOrDefault(day, 0);
                    int barWidth = (int) ((getWidth() - xStart - 50) * (count / (double) max));

                    // 요일 텍스트
                    g2.setColor(Color.BLACK);
                    g2.drawString(dayToKorean(day), 30, y + barHeight - 5);

                    // 막대
                    g2.setColor(new Color(120, 170, 255));
                    g2.fillRoundRect(xStart, y, barWidth, barHeight, 10, 10);

                    // 숫자 표시
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(count + "회", xStart + barWidth + 10, y + barHeight - 5);

                    y += barHeight + gap;
                }
            }
        };

        graphPanel.setBackground(Color.WHITE);
        add(graphPanel, BorderLayout.CENTER);

        JButton close = new JButton("닫기");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String dayToKorean(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "월요일";
            case TUESDAY -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY -> "목요일";
            case FRIDAY -> "금요일";
            case SATURDAY -> "토요일";
            case SUNDAY -> "일요일";
        };
    }
}

