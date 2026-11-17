package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import klinik.view.theme.DesignSystem;

/**
 * JTextArea bersudut tegas dalam JScrollPane dengan navigasi Tab konsisten,
 * placeholder non-intrusif, dan fokus border hitam 2px.
 */
public class SharpTextArea extends JScrollPane {
    private final InnerTextArea textArea;
    private boolean focused = false;
    private boolean error = false;

    public SharpTextArea(String placeholder, int rows) {
        this.textArea = new InnerTextArea(placeholder, Math.max(1, rows - 1));
        setViewportView(textArea);
        setBackground(DesignSystem.BG_CARD);
        getViewport().setBackground(DesignSystem.BG_CARD);
        getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setPreferredSize(new Dimension(240, Math.max(68, rows * 22 + 12)));
        setMinimumSize(new Dimension(160, 64));

        setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                DesignSystem.applyQualityHints(g2);
                if (error) {
                    g2.setColor(DesignSystem.DANGER);
                    g2.setStroke(new BasicStroke(focused ? 2f : 1.5f));
                    g2.drawRect(x + 1, y + 1, width - 3, height - 3);
                } else if (focused) {
                    g2.setColor(DesignSystem.BORDER_FOCUS);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRect(x + 1, y + 1, width - 3, height - 3);
                } else {
                    g2.setColor(DesignSystem.BORDER_INPUT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRect(x, y, width - 1, height - 1);
                }
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(2, 2, 2, 2);
            }
        });

        textArea.addFocusListener(new FocusAdapter() {
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

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text != null ? text : "");
        textArea.setCaretPosition(0);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setError(boolean error) {
        if (this.error != error) {
            this.error = error;
            repaint();
        }
    }

    private static class InnerTextArea extends JTextArea {
        private final String placeholder;

        InnerTextArea(String placeholder, int rows) {
            super(rows, 20);
            this.placeholder = placeholder != null ? placeholder : "";
            setFont(DesignSystem.fontRegular(13f));
            setForeground(DesignSystem.TEXT_PRIMARY);
            setBackground(DesignSystem.BG_CARD);
            setCaretColor(DesignSystem.TEXT_PRIMARY);
            setSelectionColor(DesignSystem.TEXT_PRIMARY);
            setSelectedTextColor(DesignSystem.BG_CARD);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(new EmptyBorder(8, 10, 8, 10));
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !placeholder.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                DesignSystem.applyQualityHints(g2);
                g2.setFont(getFont());
                g2.setColor(DesignSystem.TEXT_PLACEHOLDER);
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, ins.left, ins.top + fm.getAscent());
                g2.dispose();
            }
        }
    }
}
