import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TenMinuteTimer extends JPanel {
    private Timer timer;
    private int totalSeconds = 600; // 10분 고정
    private int remainingSeconds;
    
    // JLabel 대신 CircularProgressBar 사용
    private CircularProgressBar circleTimer; 
    
    private Runnable onFinishListener; 

    public TenMinuteTimer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 원형 프로그레스 바 생성 및 설정
        circleTimer = new CircularProgressBar();
        circleTimer.setColor(new Color(70, 140, 255)); // 예쁜 파란색
        
        // 패널 중앙에 배치
        add(circleTimer, BorderLayout.CENTER);

        remainingSeconds = totalSeconds;
        updateDisplay(); // 초기 화면(꽉 찬 원 + 10:00) 설정

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    updateDisplay(); // 1초마다 화면 갱신
                } else {
                    ((Timer)e.getSource()).stop();
                    
                    if (onFinishListener != null) {
                        onFinishListener.run();
                    }
                    
                    JOptionPane.showMessageDialog(TenMinuteTimer.this, "집중 시간 종료! 휴식하세요.");
                }
            }
        });
    }
    
    public void setOnFinishListener(Runnable listener) {
        this.onFinishListener = listener;
    }

    public void startTimer() {
        if (remainingSeconds == 0) {
            remainingSeconds = totalSeconds;
            updateDisplay();
        }
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
    
    // ✅ 재설정 기능 포함
    public void resetTimer() {
        stopTimer();
        remainingSeconds = totalSeconds;
        updateDisplay();
    }

    // ✅ 화면(글자 + 원형 게이지) 갱신 로직
    private void updateDisplay() {
        // 1. 시간 텍스트 업데이트
        int min = remainingSeconds / 60;
        int sec = remainingSeconds % 60;
        circleTimer.setTime(String.format("%02d:%02d", min, sec));

        // 2. 원형 게이지 업데이트 (남은 시간 / 전체 시간)
        double progress = (double) remainingSeconds / totalSeconds;
        circleTimer.setProgress(progress);
    }
}