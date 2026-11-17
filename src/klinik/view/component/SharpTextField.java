package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import klinik.view.theme.DesignSystem;

/**
 * Kolom input teks bersudut tegas dengan state fokus (garis hitam 2px),
 * state error (garis merah #C62828), dan placeholder non-intrusif.
 */
public class SharpTextField extends JTextField {
    private String placeholder = "";
    private boolean focused = false;
    private boolean error = false;

    public SharpTextField(String placeholder, boolean mono) {
        this.placeholder = placeholder != null ? placeholder : "";
        setFont(mono ? DesignSystem.monoRegular(13f) : DesignSystem.fontRegular(13f));
        setForeground(DesignSystem.TEXT_PRIMARY);
        setCaretColor(DesignSystem.TEXT_PRIMARY);
        setSelectionColor(DesignSystem.TEXT_PRIMARY);
        setSelectedTextColor(DesignSystem.BG_CARD);
        setBackground(DesignSystem.BG_CARD);
        setOpaque(true);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(220, 38));
        setMinimumSize(new Dimension(120, 38));

        addFocusListener(new FocusAdapter() {
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
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        repaint();
    }

    public void setError(boolean error) {
        if (this.error != error) {
            this.error = error;
            repaint();
        }
    }

    public boolean isError() {
        return error;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((getText() == null || getText().isEmpty()) && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            DesignSystem.applyQualityHints(g2);
            g2.setFont(getFont());
            g2.setColor(DesignSystem.TEXT_PLACEHOLDER);
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, ins.left, y);
            g2.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        DesignSystem.applyQualityHints(g2);
        int w = getWidth();
        int h = getHeight();
        if (error) {
            g2.setColor(DesignSystem.DANGER);
            g2.setStroke(new BasicStroke(focused ? 2f : 1.5f));
            float off = focused ? 1f : 0.75f;
            g2.drawRect(Math.round(off), Math.round(off), w - 1 - Math.round(off * 2), h - 1 - Math.round(off * 2));
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
}
