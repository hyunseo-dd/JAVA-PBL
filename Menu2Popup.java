import javax.swing.*;
import java.awt.*;

public class Menu2Popup extends JDialog {

    public Menu2Popup(JFrame parent, CalendarView calendarView) {
        super(parent, "메뉴 2 팝업", true);

        // 달력 크기 가져오기
        Dimension size = calendarView.getSize();
        Point pos = calendarView.getLocationOnScreen();

        // 팝업 크기 / 위치 달력과 동일하게
        setSize(size.width, size.height);
        setLocation(pos.x, pos.y);

        // 배경색 흰색
        getContentPane().setBackground(Color.WHITE);

        // 내부 패널도 흰색
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        JLabel text = new JLabel("Menu 2 팝업입니다.", SwingConstants.CENTER);
        text.setFont(new Font("Dialog", Font.BOLD, 24));
        content.add(text, BorderLayout.CENTER);

        add(content);

        setVisible(true);
    }
}

