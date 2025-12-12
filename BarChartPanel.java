import javax.swing.*;
import java.awt.*;

public class BarChartPanel extends JPanel {

    private int[] data;
    private String[] labels = {"월", "화", "수", "목", "금", "토", "일"};

    public BarChartPanel(int[] data) {
        this.data = data;
        setPreferredSize(new Dimension(500, 300));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        int max = 1;
        for (int v : data) max = Math.max(max, v);

        int barWidth = 40;     
        int barGap = 20;       

        int totalBarsWidth = data.length * barWidth + (data.length - 1) * barGap;

        int startX = (width - totalBarsWidth) / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);


        g2.drawLine(30, height - 50, width - 20, height - 50);

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) ((double) data[i] / max * (height - 120));

            int x = startX + i * (barWidth + barGap);
            int y = height - 50 - barHeight;

            g2.setColor(new Color(70, 140, 255));
            g2.fillRect(x, y, barWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            g2.drawString(String.valueOf(data[i]),
                    x + barWidth / 3,
                    y - 5
            );

            g2.drawString(labels[i],
                    x + barWidth / 3,
                    height - 30
            );
        }
    }
}
