import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class TIMER extends JFrame { // 类名改为 TIMER

    private JTextField timeInput; // 单一输入框 "分:秒"
    private Timer timer;
    private int totalSeconds = 0;
    private int originalSeconds = 1;
    private ClockPanel clockPanel;

    public TIMER() {
        setTitle("Clock Style计时器");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        buildTopBar(); // 修改后的顶部导航栏

        clockPanel = new ClockPanel();
        add(clockPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");
        buttonPanel.add(startBtn); 
        buttonPanel.add(stopBtn); 
        buttonPanel.add(resetBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(1000,e->{
            if(totalSeconds>0){
                totalSeconds--; 
                clockPanel.setCurrentTime(totalSeconds,originalSeconds);
            }
            else{
                timer.stop(); 
                JOptionPane.showMessageDialog(this,"时间到啦！","提醒",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        startBtn.addActionListener(e-> startTimer());
        stopBtn.addActionListener(e-> timer.stop());
        resetBtn.addActionListener(e-> resetTimer());
        updateClockFromInput();
        setVisible(true);
    }

    // 修改后的顶部导航栏 + 时间输入框布局
    private void buildTopBar(){
        // 顶部主面板
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        // 左侧导航按钮
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        navPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("日历");
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(new LineBorder(Color.BLACK,1));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            new Jinxing(); // 打开日历
            dispose();     // 关闭计时器
        });
        navPanel.add(backBtn);
        top.add(navPanel, BorderLayout.WEST);

        // 中间时间输入框
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        timePanel.setBackground(Color.WHITE);
        timeInput = new JTextField("10:00",5);
        timeInput.setHorizontalAlignment(JTextField.CENTER);
        timeInput.setFont(new Font("SansSerif", Font.PLAIN,18));
        timePanel.add(new JLabel("分钟:秒"));
        timePanel.add(timeInput);
        top.add(timePanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
    }

    private void startTimer(){
        updateClockFromInput();
        if(totalSeconds>0) timer.start();
    }

    private void resetTimer(){
        timer.stop();
        totalSeconds=0;
        originalSeconds=1;
        clockPanel.setCurrentTime(0,1);
    }

    private void updateClockFromInput(){
        try{
            String[] parts = timeInput.getText().split(":");
            int min=Integer.parseInt(parts[0].trim());
            int sec=(parts.length>1?Integer.parseInt(parts[1].trim()):0);
            if(min<0) min=0; 
            if(sec<0) sec=0; 
            if(sec>59){min+=sec/60; sec=sec%60;}
            totalSeconds=min*60+sec;
            originalSeconds=Math.max(1,totalSeconds);
            clockPanel.setCurrentTime(totalSeconds,originalSeconds);
        }catch(Exception e){
            totalSeconds=0; 
            originalSeconds=1;
        }
    }

    class ClockPanel extends JPanel{
        private int currentSeconds=0, maxSeconds=1;
        public void setCurrentTime(int s,int max){
            currentSeconds=s; 
            maxSeconds=Math.max(1,max); 
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g); 
            Graphics2D g2=(Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int size=Math.min(getWidth(),getHeight())-40;
            int x=(getWidth()-size)/2; 
            int y=(getHeight()-size)/2;

            g2.setColor(new Color(230,230,230)); 
            g2.fillOval(x,y,size,size);
            GradientPaint gradient=new GradientPaint(x,y,new Color(250,250,255),x+size,y+size,new Color(225,225,240));
            g2.setPaint(gradient);
            g2.fillOval(x+12,y+12,size-24,size-24);

            float progress=(float)currentSeconds/maxSeconds; 
            int angle=(int)(360*progress);
            g2.setStroke(new BasicStroke(14,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(90,140,255)); 
            g2.drawArc(x+20,y+20,size-40,size-40,90,-angle);

            int min=currentSeconds/60, sec=currentSeconds%60;
            String text=String.format("%02d:%02d",min,sec);
            g2.setColor(Color.BLACK); 
            g2.setFont(new Font("Arial",Font.BOLD,48));
            FontMetrics fm=g2.getFontMetrics(); 
            int strWidth=fm.stringWidth(text);
            g2.drawString(text,getWidth()/2-strWidth/2,getHeight()/2+20);
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TIMER::new); // 修改类名
    }
}
