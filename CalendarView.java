import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

public class CalendarView extends JPanel {

    private JLabel monthLabel;
    private JPanel calendarPanel;
    private Calendar calendar;

    public interface DayClickListener {
        void onSingleClick(String dateKey);
        void onDoubleClick(String dateKey);
    }

    private DayClickListener dayClickListener;

    public void setDayClickListener(DayClickListener listener) {
        this.dayClickListener = listener;
    }

    public CalendarView() {

        setLayout(new BorderLayout());

        // ** 달력 전체 배경 흰색 **
        setBackground(Color.WHITE);

        calendar = Calendar.getInstance();

        // --------------------------
        // 상단: 월 이동 버튼 + 월 표시
        // --------------------------
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        top.setBackground(Color.WHITE); // 흰색
        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        top.add(prevBtn);
        top.add(monthLabel);
        top.add(nextBtn);

        add(top, BorderLayout.NORTH);

        // --------------------------
        // 달력 격자 패널
        // --------------------------
        calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBackground(Color.WHITE);  // 흰색
        add(calendarPanel, BorderLayout.CENTER);

        updateMonthLabel();
        drawCalendar();

        // 월 이동 버튼
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
    }

    private void updateMonthLabel() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        monthLabel.setText(year + "년 " + month + "월");
    }

    public void drawCalendar() {
        calendarPanel.removeAll();

        // --------------------------
        // 요일 헤더
        // --------------------------
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};

        for (int i = 0; i < 7; i++) {
            JLabel lbl = new JLabel(days[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Dialog", Font.BOLD, 12));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);     // 요일 배경도 흰색

            if (i == 0) lbl.setForeground(Color.RED); // 일요일 빨간색

            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(lbl);
        }

        // --------------------------
        // 날짜 채우기
        // --------------------------
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int startDay = temp.get(Calendar.DAY_OF_WEEK);
        int lastDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 빈 칸
        for (int i = 1; i < startDay; i++) {
            JLabel blank = new JLabel();
            blank.setOpaque(true);
            blank.setBackground(Color.WHITE);   // 빈칸도 흰색
            calendarPanel.add(blank);
        }

        // 날짜 표시
        for (int day = 1; day <= lastDay; day++) {

            int finalDay = day;
            JLabel lbl = new JLabel(String.valueOf(day));
            lbl.setFont(new Font("Dialog", Font.PLAIN, 16));
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            lbl.setVerticalAlignment(SwingConstants.TOP);
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);     // 날짜 칸도 흰색
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            int weekIndex = (startDay - 1 + day - 1) % 7;
            if (weekIndex == 0) lbl.setForeground(Color.RED);

            String dateKey = getDateKey(finalDay);

            // --------------------------
            // 싱글 / 더블 클릭 이벤트 처리
            // --------------------------
            lbl.addMouseListener(new MouseAdapter() {
                long lastClick = 0;

                @Override
                public void mouseClicked(MouseEvent e) {
                    long now = System.currentTimeMillis();

                    if (now - lastClick < 300) {
                        if (dayClickListener != null) dayClickListener.onDoubleClick(dateKey);
                    } else {
                        if (dayClickListener != null) dayClickListener.onSingleClick(dateKey);
                    }

                    lastClick = now;
                }
            });

            calendarPanel.add(lbl);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private String getDateKey(int day) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return year + "-" + month + "-" + day;
    }
}
