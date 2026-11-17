package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;
import klinik.view.theme.DesignSystem;

/**
 * JSpinner tanggal lahir berbasis SpinnerDateModel (dd-MM-yyyy) bersudut tegas
 * dengan font Atkinson Hyperlegible Mono dan tombol stepper monokrom.
 */
public class SharpDateSpinner extends JSpinner {
    private boolean focused = false;
    private boolean error = false;

    public SharpDateSpinner() {
        super(new SpinnerDateModel(defaultDate(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(this, "dd-MM-yyyy");
        setEditor(editor);

        setUI(new SharpSpinnerUI());
        setBackground(DesignSystem.BG_CARD);
        setOpaque(true);
        setPreferredSize(new Dimension(220, 38));
        setMinimumSize(new Dimension(140, 38));

        editor.setBorder(new EmptyBorder(0, 0, 0, 0));
        editor.setBackground(DesignSystem.BG_CARD);

        JFormattedTextField tf = editor.getTextField();
        tf.setFont(DesignSystem.monoRegular(13f));
        tf.setForeground(DesignSystem.TEXT_PRIMARY);
        tf.setBackground(DesignSystem.BG_CARD);
        tf.setCaretColor(DesignSystem.TEXT_PRIMARY);
        tf.setSelectionColor(DesignSystem.TEXT_PRIMARY);
        tf.setSelectedTextColor(DesignSystem.BG_CARD);
        tf.setHorizontalAlignment(JFormattedTextField.LEFT);
        tf.setBorder(new EmptyBorder(6, 12, 6, 6));

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            if (!isEnabled()) {
                return;
            }
            Object nextVal = e.getWheelRotation() < 0 ? getNextValue() : getPreviousValue();
            if (nextVal != null) {
                setValue(nextVal);
            }
        });

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g.create();
        DesignSystem.applyQualityHints(g2);
        int w = getWidth();
        int h = getHeight();
        if (error) {
            g2.setColor(DesignSystem.DANGER);
            g2.setStroke(new BasicStroke(focused ? 2f : 1.5f));
            g2.drawRect(1, 1, w - 3, h - 3);
        } else if (focused) {
            g2.setColor(DesignSystem.BORDER_FOCUS);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(1, 1, w - 3, h - 3);
        } else {
            g2.setColor(DesignSystem.BORDER_INPUT);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(0, 0, w - 1, h - 1);
        }
        g2.dispose();
    }

    private static Date defaultDate() {
        return Date.from(LocalDate.of(1995, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setError(boolean error) {
        if (this.error != error) {
            this.error = error;
            repaint();
        }
    }

    public JFormattedTextField getTextField() {
        return ((JSpinner.DateEditor) getEditor()).getTextField();
    }

    public LocalDate getLocalDate() throws ParseException {
        commitEdit();
        Date value = (Date) getValue();
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setLocalDate(LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.of(1995, 1, 1);
        setValue(Date.from(target.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private static class SharpSpinnerUI extends BasicSpinnerUI {
        @Override
        protected Component createNextButton() {
            Component c = createChevronButton(true);
            installNextButtonListeners(c);
            return c;
        }

        @Override
        protected Component createPreviousButton() {
            Component c = createChevronButton(false);
            installPreviousButtonListeners(c);
            return c;
        }

        private JButton createChevronButton(boolean up) {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    DesignSystem.applyQualityHints(g2);
                    g2.setColor(getModel().isRollover() ? DesignSystem.OUTLINE_HOVER : DesignSystem.BG_CARD);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2 + (up ? 1 : -1);
                    Path2D path = new Path2D.Float();
                    if (up) {
                        path.moveTo(cx - 3.5, cy + 2.0);
                        path.lineTo(cx, cy - 2.0);
                        path.lineTo(cx + 3.5, cy + 2.0);
                    } else {
                        path.moveTo(cx - 3.5, cy - 2.0);
                        path.lineTo(cx, cy + 2.0);
                        path.lineTo(cx + 3.5, cy - 2.0);
                    }
                    g2.setColor(isEnabled() ? DesignSystem.TEXT_PRIMARY : DesignSystem.TEXT_DISABLED);
                    g2.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(path);
                    g2.dispose();
                }
            };
            btn.setBorder(new EmptyBorder(0, 0, 0, 0));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(26, 16));
            return btn;
        }
    }
}
