import javax.swing.*;
import java.awt.*;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer<Task> {

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Task> list,
            Task value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setEnabled(list.isEnabled());

        // ✅ 완료 여부: getter 사용
        setSelected(value.isCompleted());

        setFont(new Font("Dialog", Font.PLAIN, 16));

        if (value.isCompleted()) {
            setForeground(Color.GRAY);
            setText("<html><strike>" + value.getTitle() + "</strike></html>");
        } else {
            setForeground(Color.BLACK);
            setText(value.getTitle());
        }

        setBackground(isSelected ? new Color(230, 230, 230) : Color.WHITE);

        return this;
    }
}
