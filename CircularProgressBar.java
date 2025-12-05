import javax.swing.*;
import java.awt.*;

public class CircularProgressBar extends JComponent {

    private int progress = 0; // 0~100

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        // ========= 안티앨리어싱 =========
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());  // 가장 작은 변 기준

        // ========= 동적 크기 계산 =========
        int strokeWidth = size / 18;          // 선 두께
        int fontSize = size / 7;              // 글자 크기
        int padding = strokeWidth / 2 + 5;    // 여백
        int diameter = size - padding * 2;    // 원의 지름

        // ========= Stroke 적용 =========
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // ========= 배경 원 =========
        g2.setColor(new Color(230, 230, 230));
        g2.drawOval(padding, padding, diameter, diameter);

        // ========= 진행 원 =========
        g2.setColor(new Color(76, 135, 245));
        int angle = (int) (360 * (progress / 100.0));
        g2.drawArc(padding, padding, diameter, diameter, 90, -angle);

        // ========= 텍스트 =========
        String text = progress + "%";

        g2.setFont(new Font("Dialog", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() + textHeight) / 2 - 3;

        g2.setColor(Color.BLACK);
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
