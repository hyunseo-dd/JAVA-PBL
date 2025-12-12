import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SmartTimerFrame extends JFrame {
    private TenMinuteTimer timerPanel; // Week 1에서 만든 타이머 패널
    private JButton startButton;
    private JButton stopButton;

    public SmartTimerFrame() {
        setTitle("스마트 10분 타이머");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // 1. 타이머 패널 배치 (화면 중앙)
        timerPanel = new TenMinuteTimer();
        add(timerPanel, BorderLayout.CENTER);

        // 2. 컨트롤 버튼 패널 생성
        JPanel controlPanel = new JPanel();
        startButton = new JButton("시작");
        stopButton = new JButton("정지");

        // 버튼 스타일 설정 (가독성)
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        stopButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        // 3. 이벤트 리스너 등록 (핵심 로직 연결)
        initializeEvents();

        setLocationRelativeTo(null); // 화면 정중앙 배치
        setVisible(true);
    }

    private void initializeEvents() {
        // [버튼 이벤트] 시작 버튼 -> 타이머 시작
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[Log] 시작 버튼 클릭됨 -> 타이머 가동"); // 디버깅용 로그
                timerPanel.startTimer();
            }
        });

        // [버튼 이벤트] 정지 버튼 -> 타이머 멈춤
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[Log] 정지 버튼 클릭됨 -> 타이머 일시정지");
                timerPanel.stopTimer();
            }
        });

        // [키보드 이벤트] 'ESC' 키 누르면 타이머 정지 (KeyBinding 사용)
        // 포커스와 상관없이 동작하도록 RootPane에 등록
        KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKey, "STOP_ACTION");
        getRootPane().getActionMap().put("STOP_ACTION", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[Log] ESC 키 입력됨 -> 타이머 일시정지");
                timerPanel.stopTimer();
            }
        });
    }

    // 메인 실행부
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartTimerFrame());
    }
}