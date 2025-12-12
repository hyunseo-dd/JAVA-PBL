import javax.swing.*;
import java.awt.*;

public class CircularProgressBar extends JPanel {

    private double progress = 1.0;       
    private String timeText = "00:00";
    private Color barColor = new Color(70, 140, 255);

    public CircularProgressBar() {
        setOpaque(false);
    }

    public void setProgress(double value) {
        value = Math.max(0.0, Math.min(1.0, value));  
        if (this.progress != value) {
            this.progress = value;
            repaint(); 
        }
    }

    public void setTime(String text) {
        if (!text.equals(this.timeText)) {
            this.timeText = text;
            repaint();
        }
    }

    public void setColor(Color c) {
        if (!c.equals(this.barColor)) {
            this.barColor = c;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int size = Math.min(getWidth(), getHeight());
        int strokeWidth = 14;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        int arcSize = size - strokeWidth;

        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(230, 230, 230));
        g2.drawOval(x + strokeWidth / 2, y + strokeWidth / 2, arcSize, arcSize);

        g2.setColor(barColor);
        int angle = (int) (360 * progress);
        g2.drawArc(x + strokeWidth / 2, y + strokeWidth / 2, arcSize, arcSize, 90, -angle);

        g2.setFont(new Font("맑은 고딕", Font.BOLD, size / 7));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int textHeight = fm.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(timeText,
                getWidth() / 2 - textWidth / 2,
                getHeight() / 2 + textHeight / 4);

        g2.dispose();
    }
}
