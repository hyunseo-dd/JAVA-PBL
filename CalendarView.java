import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

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

    public void refreshCalendar() {
        removeAll();

        // 1. 상단 년/월 이동 패널
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

        // 2. 달력 그리드 (요일 + 날짜)
        JPanel grid = new JPanel(new GridLayout(0, 7)); 
        grid.setBackground(Color.WHITE);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};

        // 요일 헤더
        for (int i = 0; i < days.length; i++) {
            JLabel lbl = new JLabel(days[i], SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            if (i == 0) lbl.setForeground(Color.RED);      
            else if (i == 6) lbl.setForeground(Color.BLUE); 
            else lbl.setForeground(Color.BLACK);

            grid.add(lbl);
        }

        // 날짜 계산 로직 (여기가 문제였음 -> 수정 완료)
        YearMonth ym = YearMonth.from(currentDate);
        int daysInMonth = ym.lengthOfMonth();
        LocalDate first = currentDate.withDayOfMonth(1);
        
        // 🚨 [수정됨] 일요일=7 -> 0, 월요일=1 -> 1, ... 토요일=6 -> 6
        // getDayOfWeek().getValue()는 월(1)~일(7)을 반환함.
        // 따라서 일요일(7)일 때만 0으로 만들어주면 됨.
        int dayVal = first.getDayOfWeek().getValue(); 
        int emptyCells = (dayVal == 7) ? 0 : dayVal;

        // 앞쪽 빈칸 채우기
        for (int i = 0; i < emptyCells; i++) {
            grid.add(createDisabledDayButton(""));
        }

        // 날짜 채우기
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            grid.add(createDayButton(day, date));
        }

        // 레이아웃 깨짐 방지용 뒤쪽 빈칸
        while (grid.getComponentCount() % 7 != 0) {
            grid.add(createDisabledDayButton(""));
        }

        add(grid, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JButton createDayButton(int day, LocalDate date) {
        int taskCount = taskService.getTasks(date).size();
        String indicator = (taskCount > 0) ? "<div style='font-size:10px; color:#0070F0;'>●</div>" : "";

        String color = "#000000";
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SUNDAY) color = "#FF3B30"; 
        else if (dow == DayOfWeek.SATURDAY) color = "#0070F0"; 

        JButton btn = new JButton(
                "<html><div style='text-align:left; padding:2px; color:" + color + ";'>" +
                        day + "<br>" + indicator +
                "</div></html>"
        );

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.TOP);
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setMargin(new Insets(2, 4, 2, 2));

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
    
    private JButton createDisabledDayButton(String text) {
        JButton btn = new JButton(text);
        btn.setOpaque(true);
        btn.setBackground(new Color(250, 250, 250));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setEnabled(false);
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setPreferredSize(new Dimension(50, 30));
    }
}