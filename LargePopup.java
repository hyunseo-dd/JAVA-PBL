import javax.swing.*;
import java.awt.*;

public class LargePopup extends JDialog {

    public LargePopup(JFrame parent) {
        super(parent, "큰 팝업 창", true); // true = 모달

        // 화면 크기 가져오기
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 화면의 2/3 크기로 계산
        int width = (int) (screenSize.width * 0.66);
        int height = (int) (screenSize.height * 0.66);

        // 팝업 사이즈 지정
        setSize(width, height);

        // 화면 중앙에 배치
        setLocation(
                (screenSize.width - width) / 2,
                (screenSize.height - height) / 2
        );

        // 기본 레이아웃
        setLayout(new BorderLayout());

        // 예시 내용
        JLabel label = new JLabel("여기에 내용을 넣으면 됩니다!", SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }
}
