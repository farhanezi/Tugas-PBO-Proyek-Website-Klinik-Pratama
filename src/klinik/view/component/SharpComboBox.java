package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import klinik.view.theme.DesignSystem;

/**
 * JComboBox bersudut tegas bergaya monokrom Swiss tanpa aksen biru bawaan sistem.
 */
public class SharpComboBox<T> extends JComboBox<T> {
    private boolean focused = false;
    private boolean error = false;

    public SharpComboBox() {
        setFont(DesignSystem.fontRegular(13f));
        setForeground(DesignSystem.TEXT_PRIMARY);
        setBackground(DesignSystem.BG_CARD);
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 38));
        setMinimumSize(new Dimension(140, 38));
        setRenderer(createMonochromeRenderer());
        setUI(new SharpComboUI());

        setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                DesignSystem.applyQualityHints(g2);
                if (error) {
                    g2.setColor(DesignSystem.DANGER);
                    g2.setStroke(new BasicStroke(focused || isPopupVisible() ? 2f : 1.5f));
                    g2.drawRect(x + 1, y + 1, width - 3, height - 3);
                } else if (focused || isPopupVisible()) {
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
                return new Insets(2, 10, 2, 4);
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

    public void setError(boolean error) {
        if (this.error != error) {
            this.error = error;
            repaint();
        }
    }

    private ListCellRenderer<Object> createMonochromeRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(DesignSystem.fontRegular(13f));
                setBorder(new EmptyBorder(7, 10, 7, 10));
                if (index == -1) {
                    setBackground(DesignSystem.BG_CARD);
                    setForeground(value == null ? DesignSystem.TEXT_PLACEHOLDER : DesignSystem.TEXT_PRIMARY);
                    setBorder(new EmptyBorder(0, 2, 0, 4));
                } else if (isSelected) {
                    setBackground(DesignSystem.TEXT_PRIMARY);
                    setForeground(DesignSystem.BG_CARD);
                } else {
                    setBackground(DesignSystem.BG_CARD);
                    setForeground(DesignSystem.TEXT_PRIMARY);
                }
                return this;
            }
        };
    }

    private class SharpComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton arrow = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    DesignSystem.applyQualityHints(g2);
                    g2.setColor(DesignSystem.BG_CARD);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    Path2D chevron = new Path2D.Float();
                    chevron.moveTo(cx - 4.5, cy - 2.0);
                    chevron.lineTo(cx, cy + 2.5);
                    chevron.lineTo(cx + 4.5, cy - 2.0);
                    g2.setColor(isEnabled() ? DesignSystem.TEXT_PRIMARY : DesignSystem.TEXT_DISABLED);
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(chevron);
                    g2.dispose();
                }
            };
            arrow.setBorder(new EmptyBorder(0, 0, 0, 0));
            arrow.setContentAreaFilled(false);
            arrow.setFocusPainted(false);
            arrow.setPreferredSize(new Dimension(28, 34));
            return arrow;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(DesignSystem.BG_CARD);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        protected ComboPopup createPopup() {
            return new BasicComboPopup(comboBox) {
                @Override
                protected void configurePopup() {
                    super.configurePopup();
                    setBorder(new LineBorder(DesignSystem.TEXT_PRIMARY, 1));
                }

                @Override
                protected JScrollPane createScroller() {
                    JScrollPane sp = super.createScroller();
                    sp.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
                    sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
                    return sp;
                }
            };
        }
    }
}
