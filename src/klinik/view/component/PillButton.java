package klinik.view.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import klinik.view.theme.DesignSystem;

/**
 * Tombol berbentuk pill — satu-satunya komponen berbentuk pill sesuai panduan desain
 * agar petugas langsung mengenali elemen yang dapat diklik.
 */
public class PillButton extends JButton {
    public enum Variant {
        PRIMARY,
        OUTLINE,
        DANGER
    }

    private final Variant variant;
    private boolean hovered = false;
    private boolean pressed = false;
    private boolean focused = false;

    public PillButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setFont(DesignSystem.fontMedium(13f));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(false);
        setMargin(new Insets(8, 20, 8, 20));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(Math.max(92, super.getPreferredSize().width), 38));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });

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

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setCursor(b ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        DesignSystem.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();
        float arc = h - 1f;

        Color bg;
        Color fg;
        Color border = null;

        if (!isEnabled()) {
            bg = DesignSystem.BUTTON_DISABLED_BG;
            fg = DesignSystem.TEXT_DISABLED;
            border = variant == Variant.OUTLINE ? DesignSystem.BUTTON_DISABLED_BORDER : null;
        } else {
            switch (variant) {
                case PRIMARY -> {
                    bg = (pressed || hovered) ? DesignSystem.PRIMARY_HOVER : DesignSystem.TEXT_PRIMARY;
                    fg = DesignSystem.BG_CARD;
                }
                case DANGER -> {
                    bg = (pressed || hovered) ? DesignSystem.DANGER_HOVER : DesignSystem.DANGER;
                    fg = DesignSystem.BG_CARD;
                }
                case OUTLINE -> {
                    bg = (pressed || hovered) ? DesignSystem.OUTLINE_HOVER : DesignSystem.BG_CARD;
                    fg = DesignSystem.TEXT_PRIMARY;
                    border = DesignSystem.TEXT_PRIMARY;
                }
                default -> {
                    bg = DesignSystem.TEXT_PRIMARY;
                    fg = DesignSystem.BG_CARD;
                }
            }
        }

        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arc, arc);
        g2.setColor(bg);
        g2.fill(shape);

        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new java.awt.BasicStroke(focused ? 1.8f : 1f));
            g2.draw(shape);
        } else if (focused && isEnabled()) {
            g2.setColor(DesignSystem.BG_CARD);
            g2.setStroke(new java.awt.BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(3f, 3f, w - 6f, h - 6f, Math.max(4f, arc - 5f), Math.max(4f, arc - 5f)));
        }

        g2.setFont(getFont());
        g2.setColor(fg);
        FontMetrics fm = g2.getFontMetrics();
        String text = getText() != null ? getText() : "";
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
