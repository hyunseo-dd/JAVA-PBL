import javax.swing.*;
import java.awt.*;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer<TodoItem> {

    @Override
    public Component getListCellRendererComponent(
            JList<? extends TodoItem> list,
            TodoItem value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setEnabled(list.isEnabled());
        setSelected(value.done);
        setFont(new Font("Dialog", Font.PLAIN, 16));

        if (value.done) {
            setForeground(Color.GRAY);
            setText("<html><strike>" + value.text + "</strike></html>");
        } else {
            setForeground(Color.BLACK);
            setText(value.text);
        }

        setBackground(Color.WHITE);
        return this;
    }
}
