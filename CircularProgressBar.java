import javax.swing.*;
import java.awt.*;

public class CircularProgressBar extends JPanel {

    private double progress = 1.0; // 100%
    private Color fillColor = new Color(70, 140, 255);
    private String timeText = "00:00";

    private int strokeWidth = 12; // 원형 두께

    public void setProgress(double p) {
        this.progress = Math.max(0, Math.min(1, p));
        repaint();
    }

    // 부드러운 애니메이션
    public void smoothSetProgress(double target) {
        Timer smoothTimer = new Timer(15, null);
        smoothTimer.addActionListener(e -> {
            if (Math.abs(progress - target) < 0.005) {
                progress = target;
                smoothTimer.stop();
            } else {
                progress = progress + (target - progress) * 0.12;
            }
            repaint();
        });
        smoothTimer.start();
    }

    public void setFillColor(Color c) {
        this.fillColor = c;
        repaint();
    }

    public void setTimeText(String text) {
        this.timeText = text;
        repaint();
    }

    public void setStrokeWidth(int w) {
        this.strokeWidth = w;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경을 100% 흰색으로 깔기
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 부드러운 렌더링
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int diameter = size - strokeWidth * 2;

        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        // 배경 원
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(230, 230, 230));
        g2.drawOval(x, y, diameter, diameter);

        // 진행 원
        g2.setColor(fillColor);
        int angle = (int) (360 * progress);
        g2.drawArc(x, y, diameter, diameter, 90, -angle);

        // 중앙 텍스트
        g2.setFont(new Font("Dialog", Font.BOLD, size / 7));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int textHeight = fm.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(timeText,
                getWidth() / 2 - textWidth / 2,
                getHeight() / 2 + textHeight / 3);

        g2.dispose();
    }
}
