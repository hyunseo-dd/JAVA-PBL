import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClockStyleTimer extends JFrame {

    private JSpinner minuteSpinner;
    private JSpinner secondSpinner;

    private Timer timer;
    private int totalSeconds;
    private int originalSeconds;

    private ClockPanel clockPanel;

    public ClockStyleTimer() {
        setTitle("Clock Style计时器");
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);  // 居中
        //时钟样式显示区
        clockPanel = new ClockPanel();
        add(clockPanel, BorderLayout.CENTER);
        //输入时间区域：分钟秒和自动修正
        JPanel setPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        setPanel.setBorder(BorderFactory.createTitledBorder("设置时间"));

        minuteSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 999, 1));
        secondSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        JFormattedTextField minField =
                ((JSpinner.NumberEditor) minuteSpinner.getEditor()).getTextField();
        minField.setColumns(3);
        minField.setHorizontalAlignment(JTextField.CENTER);

        JFormattedTextField secField =
                ((JSpinner.NumberEditor) secondSpinner.getEditor()).getTextField();
        secField.setColumns(3);
        secField.setHorizontalAlignment(JTextField.CENTER);

        //自动修正和实时更新
        minField.addPropertyChangeListener("value", e -> {
            clampInput();
            updateClockFromInput();
        });
        secField.addPropertyChangeListener("value", e -> {
            clampInput();
            updateClockFromInput();
        });

        setPanel.add(new JLabel("分钟:"));
        setPanel.add(minuteSpinner);
        setPanel.add(new JLabel("秒:"));
        setPanel.add(secondSpinner);

        add(setPanel, BorderLayout.NORTH);
        //按钮区
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");

        startBtn.setPreferredSize(new Dimension(90, 38));
        stopBtn.setPreferredSize(new Dimension(90, 38));
        resetBtn.setPreferredSize(new Dimension(90, 38));

        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);

        add(buttonPanel, BorderLayout.SOUTH);
        //Timer的逻辑
        timer = new Timer(1000, e -> {
            if (totalSeconds > 0) {
                totalSeconds--;
                clockPanel.setCurrentTime(totalSeconds, originalSeconds);
            } else {
                timer.stop();
                showAlert();
            }
        });

        startBtn.addActionListener(e -> startTimer());
        stopBtn.addActionListener(e -> timer.stop());
        resetBtn.addActionListener(e -> resetTimer());

        updateClockFromInput();
    }
    //自动修正clamp功能 整合分钟 秒合理化
    private void clampInput() {
        try {
            int min = (Integer) minuteSpinner.getValue();
            int sec = (Integer) secondSpinner.getValue();

            if (min < 0) minuteSpinner.setValue(0);
            if (sec < 0) secondSpinner.setValue(0);

            //输入时间自动进位
            if (sec > 59) {
                int newMin = min + sec / 60;
                int newSec = sec % 60;
                minuteSpinner.setValue(newMin);
                secondSpinner.setValue(newSec);
            }

        } catch (Exception ex) {
            minuteSpinner.setValue(0);
            secondSpinner.setValue(0);
        }
    }

    //读取输入后更新时钟
    private void updateClockFromInput() {
        int min = (Integer) minuteSpinner.getValue();
        int sec = (Integer) secondSpinner.getValue();

        totalSeconds = min * 60 + sec;
        originalSeconds = Math.max(1, totalSeconds);

        clockPanel.setCurrentTime(totalSeconds, originalSeconds);
    }

    //开始计时
    private void startTimer() {
        updateClockFromInput();  // 开始前再次确保同步
        if (totalSeconds > 0) timer.start();
    }

    //重置归零
    private void resetTimer() {
        timer.stop();
        totalSeconds = 0;
        originalSeconds = 1;
        clockPanel.setCurrentTime(0, 1);
    }

    //弹窗
    private void showAlert() {
        JOptionPane.showMessageDialog(
                this,
                "时间到啦！",
                "提醒",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    //时钟样式：绘制圆盘 渐变背景 进度环 大数字
    class ClockPanel extends JPanel {

        private int currentSeconds = 0;
        private int maxSeconds = 1;

        public void setCurrentTime(int seconds, int maxSeconds) {
            this.currentSeconds = seconds;
            this.maxSeconds = Math.max(1, maxSeconds);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;


            //外部圆盘
            g2.setColor(new Color(230, 230, 230));
            g2.fillOval(x, y, size, size);

            //内部渐变圆形
            GradientPaint gradient = new GradientPaint(
                    x, y, new Color(250, 250, 255),
                    x + size, y + size, new Color(225, 225, 240)
            );
            g2.setPaint(gradient);
            g2.fillOval(x + 12, y + 12, size - 24, size - 24);

            //进度环
            float progress = (float) currentSeconds / maxSeconds;
            int angle = (int) (360 * progress);

            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(90, 140, 255));
            g2.drawArc(x + 20, y + 20, size - 40, size - 40, 90, -angle);


            //中间倒计时数字
            int min = currentSeconds / 60;
            int sec = currentSeconds % 60;
            String text = String.format("%02d:%02d", min, sec);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 48));

            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(text);

            g2.drawString(text, getWidth() / 2 - strWidth / 2, getHeight() / 2 + 20);
        }
    }


    public static void main(String[] args) {
        new ClockStyleTimer().setVisible(true);
    }
}
