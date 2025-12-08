import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarView extends JPanel {

    private LocalDate currentDate;
    private TaskService taskService;
    private DayClickListener listener;

    public interface DayClickListener {
        void onSingleClick(LocalDate date);
        void onDoubleClick(LocalDate date);
    }

    public void setDayClickListener(DayClickListener listener) {
        this.listener = listener;
    }

    public CalendarView(TaskService taskService) {
        this.taskService = taskService;
        currentDate = LocalDate.now();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        refreshCalendar();
    }

    // ============================================================
    //                       달력 다시 그리기
    // ============================================================
    private void refreshCalendar() {

        removeAll();

        // ----------------- 상단 월 네비게이션 -----------------
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        top.setBackground(Color.WHITE);

        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");

        styleButton(prevBtn);
        styleButton(nextBtn);

        JLabel monthLabel = new JLabel(currentDate.getYear() + "년 " + currentDate.getMonthValue() + "월");
        monthLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        prevBtn.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            refreshCalendar();
        });

        nextBtn.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            refreshCalendar();
        });

        top.add(prevBtn);
        top.add(monthLabel);
        top.add(nextBtn);

        add(top, BorderLayout.NORTH);

        // ----------------- 6×7 Grid -----------------
        JPanel grid = new JPanel(new GridLayout(6, 7));
        grid.setBackground(Color.WHITE);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};

        // 요일 라벨 (1행)
        for (int i = 0; i < days.length; i++) {

            JLabel lbl = new JLabel(days[i], SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            if (i == 0) lbl.setForeground(Color.RED);      // 일요일
            else if (i == 6) lbl.setForeground(Color.BLUE); // 토요일
            else lbl.setForeground(Color.BLACK);

            grid.add(lbl);
        }

        YearMonth ym = YearMonth.from(currentDate);
        int daysInMonth = ym.lengthOfMonth();

        LocalDate first = currentDate.withDayOfMonth(1);
        int firstDayIndex = first.getDayOfWeek().getValue() % 7;

        int totalCells = 35; // 날짜 표시 5줄

        // 앞달 계산
        YearMonth prevYm = ym.minusMonths(1);
        int prevDays = prevYm.lengthOfMonth();
        int prevCount = firstDayIndex;
        int nextCount = totalCells - (prevCount + daysInMonth);

        // 앞달 칸
        for (int i = 0; i < prevCount; i++) {
            grid.add(createDisabledDayButton(prevDays - prevCount + i + 1));
        }

        // 현재 달 날짜
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            grid.add(createDayButton(day, date));
        }

        // 다음 달 칸
        for (int i = 1; i <= nextCount; i++) {
            grid.add(createDisabledDayButton(i));
        }

        add(grid, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // ============================================================
    //                 날짜 버튼 (일정 점 포함)
    // ============================================================
    private JButton createDayButton(int day, LocalDate date) {

        // 일정 점 ● 표시
        int taskCount = taskService.getTasks(date).size();
        String indicator = (taskCount > 0)
                ? "<div style='font-size:10px; color:#0070F0;'>●</div>"
                : "";

        // 요일 색 지정
        String color;
        if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY)
            color = "#FF3B30"; // 빨간색
        else if (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY)
            color = "#0070F0"; // 파란색
        else
            color = "#000000";

        JButton btn = new JButton(
                "<html><div style='text-align:left; padding:2px; color:" + color + ";'>" +
                        day +
                        "<br>" +
                        indicator +
                "</div></html>"
        );

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.TOP);
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setMargin(new Insets(2, 4, 2, 2));

        // 싱글 / 더블 클릭 타이머 기반
        btn.addActionListener(new ActionListener() {

            private Timer clickTimer = null;
            private final int DELAY = 250;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (clickTimer != null && clickTimer.isRunning()) {
                    clickTimer.stop();
                    if (listener != null) listener.onDoubleClick(date);
                    return;
                }

                clickTimer = new Timer(DELAY, ev -> {
                    if (listener != null) listener.onSingleClick(date);
                });

                clickTimer.setRepeats(false);
                clickTimer.start();
            }
        });

        return btn;
    }

    // ============================================================
    //              앞/뒷달 날짜 버튼 (비활성)
    // ============================================================
    private JButton createDisabledDayButton(int day) {

        JButton btn = new JButton(
                "<html><div style='text-align:left; padding:2px; color:#999999;'>" +
                        day +
                "</div></html>"
        );

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.TOP);
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.GRAY);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setEnabled(false);
        btn.setMargin(new Insets(2, 4, 2, 2));

        return btn;
    }

    // ============================================================
    //               네비게이션 버튼 스타일
    // ============================================================
    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }
}
