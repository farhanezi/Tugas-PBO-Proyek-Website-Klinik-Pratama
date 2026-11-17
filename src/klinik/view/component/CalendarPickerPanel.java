package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import klinik.util.FormatUtil;
import klinik.view.theme.DesignSystem;

/**
 * Date picker kalender interaktif berbasis JPanel (GridLayout 7 kolom) dengan
 * JToggleButton per tanggal dalam ButtonGroup. Tanggal terpilih ditandai lingkaran hitam penuh,
 * tanggal yang sudah lewat dinonaktifkan, dan panah kiri/kanan mengganti bulan.
 */
public class CalendarPickerPanel extends JPanel {
    private static final String[] HARI_HEADER = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private final JLabel lblMonthYear;
    private final JPanel gridPanel;
    private final List<Consumer<LocalDate>> listeners = new ArrayList<>();

    public CalendarPickerPanel() {
        this.selectedDate = LocalDate.now();
        this.currentMonth = YearMonth.from(selectedDate);

        setLayout(new BorderLayout(0, 8));
        setBackground(DesignSystem.BG_CARD);
        setOpaque(false);

        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setOpaque(false);
        navRow.setBorder(new EmptyBorder(4, 2, 6, 2));

        CircleArrowButton btnPrev = new CircleArrowButton(true);
        CircleArrowButton btnNext = new CircleArrowButton(false);

        lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(DesignSystem.fontMedium(13.5f));
        lblMonthYear.setForeground(DesignSystem.TEXT_PRIMARY);

        btnPrev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            rebuildCalendar();
        });
        btnNext.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            rebuildCalendar();
        });

        navRow.add(btnPrev, BorderLayout.WEST);
        navRow.add(lblMonthYear, BorderLayout.CENTER);
        navRow.add(btnNext, BorderLayout.EAST);

        JPanel daysHeader = new JPanel(new GridLayout(1, 7, 2, 0));
        daysHeader.setOpaque(false);
        daysHeader.setBorder(new EmptyBorder(0, 0, 4, 0));
        for (String h : HARI_HEADER) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setFont(DesignSystem.fontRegular(11.5f));
            lbl.setForeground(DesignSystem.TEXT_SECONDARY);
            daysHeader.add(lbl);
        }

        gridPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        gridPanel.setOpaque(false);

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        topWrap.add(navRow, BorderLayout.NORTH);
        topWrap.add(daysHeader, BorderLayout.SOUTH);

        add(topWrap, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        rebuildCalendar();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date != null ? date : LocalDate.now();
        this.currentMonth = YearMonth.from(this.selectedDate);
        rebuildCalendar();
    }

    public void addDateSelectionListener(Consumer<LocalDate> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void rebuildCalendar() {
        lblMonthYear.setText(FormatUtil.formatBulanTahun(currentMonth));
        gridPanel.removeAll();

        ButtonGroup group = new ButtonGroup();
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int offset = firstOfMonth.getDayOfWeek().getValue() - 1; // 0 = Senin .. 6 = Minggu
        int daysInMonth = currentMonth.lengthOfMonth();

        for (int cell = 0; cell < 42; cell++) {
            int dayNum = cell - offset + 1;
            if (dayNum < 1 || dayNum > daysInMonth) {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                empty.setPreferredSize(new Dimension(32, 30));
                gridPanel.add(empty);
            } else {
                LocalDate cellDate = currentMonth.atDay(dayNum);
                boolean isSelected = cellDate.equals(selectedDate);
                boolean isPast = cellDate.isBefore(today);

                DateToggleButton btn = new DateToggleButton(String.valueOf(dayNum), isSelected);
                btn.setEnabled(!isPast || isSelected);
                if (isPast && isSelected) {
                    btn.setToolTipText("Tanggal kunjungan tersimpan");
                }
                group.add(btn);
                btn.addActionListener(e -> {
                    selectedDate = cellDate;
                    for (Consumer<LocalDate> l : listeners) {
                        l.accept(selectedDate);
                    }
                    rebuildCalendar();
                });
                gridPanel.add(btn);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private static class CircleArrowButton extends JButton {
        private final boolean left;
        private boolean hovered = false;

        CircleArrowButton(boolean left) {
            this.left = left;
            setPreferredSize(new Dimension(28, 28));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            DesignSystem.applyQualityHints(g2);

            int size = Math.min(getWidth(), getHeight()) - 2;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(hovered ? DesignSystem.OUTLINE_HOVER : DesignSystem.BG_CARD);
            g2.fillOval(x, y, size, size);

            g2.setColor(hovered ? DesignSystem.TEXT_PRIMARY : DesignSystem.BORDER_SUBTLE);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(x, y, size, size);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            Path2D arrow = new Path2D.Float();
            if (left) {
                arrow.moveTo(cx + 2.0, cy - 4.0);
                arrow.lineTo(cx - 2.5, cy);
                arrow.lineTo(cx + 2.0, cy + 4.0);
            } else {
                arrow.moveTo(cx - 2.0, cy - 4.0);
                arrow.lineTo(cx + 2.5, cy);
                arrow.lineTo(cx - 2.0, cy + 4.0);
            }
            g2.setColor(DesignSystem.TEXT_PRIMARY);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(arrow);
            g2.dispose();
        }
    }

    private static class DateToggleButton extends JToggleButton {
        private boolean hovered = false;

        DateToggleButton(String dayText, boolean selected) {
            super(dayText, selected);
            setPreferredSize(new Dimension(32, 30));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            setCursor(b ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            DesignSystem.applyQualityHints(g2);

            int d = Math.min(getWidth(), getHeight()) - 2;
            int cx = (getWidth() - d) / 2;
            int cy = (getHeight() - d) / 2;

            boolean selected = isSelected();
            if (selected) {
                g2.setColor(DesignSystem.TEXT_PRIMARY);
                g2.fillOval(cx, cy, d, d);
            } else if (hovered && isEnabled()) {
                g2.setColor(DesignSystem.BORDER_SUBTLE);
                g2.fillOval(cx, cy, d, d);
            }

            Color fg;
            if (selected) {
                fg = DesignSystem.BG_CARD;
            } else if (!isEnabled()) {
                fg = DesignSystem.TEXT_DISABLED;
            } else {
                fg = DesignSystem.TEXT_PRIMARY;
            }

            g2.setFont(selected ? DesignSystem.monoSemiBold(12f) : DesignSystem.monoRegular(12f));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            String txt = getText();
            int tx = (getWidth() - fm.stringWidth(txt)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(txt, tx, ty);

            g2.dispose();
        }
    }
}
