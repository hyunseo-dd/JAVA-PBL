import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

public class CalendarView extends JPanel {

    private JLabel monthLabel;
    private JPanel calendarPanel;
    private Calendar calendar;

    private DayClickListener listener;

    public interface DayClickListener {
        void onSingleClick(String dateKey);
        void onDoubleClick(String dateKey);
    }

    public void setDayClickListener(DayClickListener listener) {
        this.listener = listener;
    }

    public CalendarView() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        calendar = Calendar.getInstance();

        // ==============================
        // 🔶 상단: 월 표시 + 이동 버튼
        // ==============================
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        top.setBackground(Color.WHITE);

        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        top.add(prevBtn);
        top.add(monthLabel);
        top.add(nextBtn);

        add(top, BorderLayout.NORTH);

        // ==============================
        // 🔶 요일 헤더 (UI 그대로 유지)
        // ==============================
        JPanel dayHeader = new JPanel(new GridLayout(1, 7));
        dayHeader.setBackground(Color.WHITE);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        Color[] colors = {
                new Color(220, 70, 70), // 일요일
                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                new Color(50, 80, 200), // 금요일
                new Color(50, 80, 200)  // 토요일
        };

        for (int i = 0; i < 7; i++) {
            JLabel lbl = new JLabel(days[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Dialog", Font.BOLD, 14));
            lbl.setForeground(colors[i]);
            dayHeader.add(lbl);
        }

        // ==============================
        // 🔶 날짜 패널 (5x7 그대로)
        // ==============================
        calendarPanel = new JPanel(new GridLayout(5, 7));
        calendarPanel.setBackground(Color.WHITE);

        // CENTER에 요일 + 날짜를 같이 넣기 위한 wrapper
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(dayHeader, BorderLayout.NORTH);
        centerWrapper.add(calendarPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        prevBtn.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthLabel();
            drawCalendar();
        });

        nextBtn.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthLabel();
            drawCalendar();
        });

        updateMonthLabel();
        drawCalendar();
    }

    private void updateMonthLabel() {
        monthLabel.setText(calendar.get(Calendar.YEAR) + " · " + (calendar.get(Calendar.MONTH) + 1));
    }

    private void drawCalendar() {

        calendarPanel.removeAll();

        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int firstDay = temp.get(Calendar.DAY_OF_WEEK);
        int lastDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        int dayCounter = 1;

        // 전달 마지막 날짜
        Calendar prev = (Calendar) temp.clone();
        prev.add(Calendar.MONTH, -1);
        int prevLast = prev.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= 35; i++) {

            JButton btn = new JButton();
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(Color.WHITE);

            if (i < firstDay) {
                // 전달 날짜
                int d = prevLast - (firstDay - i) + 1;
                btn.setText(String.valueOf(d));
                btn.setForeground(new Color(190, 190, 190));
            }
            else if (dayCounter <= lastDay) {
                // 이번 달 날짜
                int today = dayCounter;
                btn.setText(String.valueOf(today));

                String dateKey = makeDateKey(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        today
                );

                btn.setForeground(Color.BLACK);

                btn.addMouseListener(new MouseAdapter() {
                    long last = 0;

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        long now = System.currentTimeMillis();
                        if (now - last < 250) {
                            if (listener != null) listener.onDoubleClick(dateKey);
                        } else {
                            if (listener != null) listener.onSingleClick(dateKey);
                        }
                        last = now;
                    }
                });

                dayCounter++;

            } else {
                // 다음 달 날짜
                int d = i - (firstDay + lastDay) + 1;
                btn.setText(String.valueOf(d));
                btn.setForeground(new Color(190, 190, 190));
            }

            calendarPanel.add(btn);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private String makeDateKey(int y, int m, int d) {
        return y + "-" + m + "-" + d;
    }
}
