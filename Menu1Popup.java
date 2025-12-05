import javax.swing.*;
import java.awt.*;

public class Menu1Popup extends JDialog {

    public Menu1Popup(JFrame parent, CalendarView calendarView) {
        super(parent, "Pomodoro", true);

        // 달력 크기 가져오기
        Dimension size = calendarView.getSize();

        // 달력의 실제 화면 위치 가져오기
        Point pos = calendarView.getLocationOnScreen();

        // 팝업 크기 = 달력 크기
        setSize(size.width, size.height);

        // 팝업 위치 = 달력 위치
        setLocation(pos.x, pos.y);

        // ===============================
        //  배경을 하얀색으로 지정
        // ===============================
        getContentPane().setBackground(Color.WHITE);

        // 내부 컴포넌트도 흰색 배경 적용 가능
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        JLabel text = new JLabel("Pomodoro", SwingConstants.CENTER);
        text.setFont(new Font("Dialog", Font.BOLD, 24));
        content.add(text, BorderLayout.CENTER);

        add(content);

        setVisible(true);
    }
}
