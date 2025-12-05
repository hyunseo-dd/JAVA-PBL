import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarView extends JPanel {

    private YearMonth currentYearMonth;
    private LocalDate today;

    private JLabel monthLabel;
    private JPanel calendarPanel;

    public interface DayClickListener {
        void onSingleClick(LocalDate date);
        void onDoubleClick(LocalDate date);
    }

    private DayClickListener listener;

    public void setDayClickListener(DayClickListener listener) {
        this.listener = listener;
    }

    public CalendarView() {
        setLayout(new BorderLayout());

        today = LocalDate.now();
        currentYearMonth = YearMonth.from(today);

        JPanel top = new JPanel(new BorderLayout());
        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 22));

        prevBtn.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar();
        });
        nextBtn.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar();
        });

        top.add(prevBtn, BorderLayout.WEST);
        top.add(monthLabel, BorderLayout.CENTER);
        top.add(nextBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        calendarPanel = new JPanel();
        add(calendarPanel, BorderLayout.CENTER);

        refreshCalendar();
    }

    public void refreshCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(7, 7));

        monthLabel.setText(currentYearMonth.getYear() + "년 " +
                           currentYearMonth.getMonthValue() + "월");

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Dialog", Font.BOLD, 14));
            calendarPanel.add(lbl);
        }

        LocalDate firstDay = currentYearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int lastDay = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < dayOfWeek; i++)
            calendarPanel.add(new JLabel(""));

        for (int day = 1; day <= lastDay; day++) {

            LocalDate date = currentYearMonth.atDay(day);
            JButton btn = new JButton(String.valueOf(day));
            btn.setFocusPainted(false);

            if (date.equals(today)) {
                btn.setBackground(new Color(220, 240, 255));
            }

            btn.addMouseListener(new MouseAdapter() {
                long lastClick = 0;

                @Override
                public void mouseClicked(MouseEvent e) {
                    long now = System.currentTimeMillis();
                    if (now - lastClick < 200) {
                        if (listener != null) listener.onDoubleClick(date);
                    } else {
                        if (listener != null) listener.onSingleClick(date);
                    }
                    lastClick = now;
                }
            });

            calendarPanel.add(btn);
        }

        revalidate();
        repaint();
    }
}
