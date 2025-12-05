import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SchedulePopup extends JDialog {

    public SchedulePopup(JFrame parent, String dateKey, Map<String, String> scheduleMap) {
        super(parent, "일정 추가", true);

        setSize(300, 200);
        setLayout(new BorderLayout());

        JLabel label = new JLabel(dateKey + " 일정 입력", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setLineWrap(true);

        // 기존 일정 있으면 불러오기
        String old = scheduleMap.get(dateKey);
        if (old != null) area.setText(old);

        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton saveBtn = new JButton("저장");
        add(saveBtn, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            scheduleMap.put(dateKey, area.getText());
            JOptionPane.showMessageDialog(this, "일정이 저장되었습니다!");
            dispose();
        });

        setLocationRelativeTo(parent);
    }
}
