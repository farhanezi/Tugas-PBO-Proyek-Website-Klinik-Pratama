package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import klinik.view.theme.DesignSystem;

/**
 * Radio button dengan motif lingkaran bergaris dan titik hitam penuh saat terpilih.
 */
public class SwissRadioButton extends JRadioButton {
    public SwissRadioButton(String text, boolean selected) {
        super(text, selected);
        setFont(DesignSystem.fontRegular(13f));
        setForeground(DesignSystem.TEXT_PRIMARY);
        setBackground(DesignSystem.BG_CARD);
        setOpaque(false);
        setFocusPainted(false);
        setIconTextGap(8);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIcon(new RadioIcon());
    }

    private static class RadioIcon implements Icon {
        private static final int SIZE = 16;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            JRadioButton rb = (JRadioButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            DesignSystem.applyQualityHints(g2);

            boolean selected = rb.isSelected();
            boolean hover = rb.getModel().isRollover();

            g2.setColor(DesignSystem.BG_CARD);
            g2.fillOval(x + 1, y + 1, SIZE - 2, SIZE - 2);

            g2.setColor(selected || hover ? DesignSystem.TEXT_PRIMARY : DesignSystem.BORDER_INPUT);
            g2.setStroke(new BasicStroke(1.25f));
            g2.drawOval(x + 1, y + 1, SIZE - 2, SIZE - 2);

            if (selected) {
                g2.setColor(DesignSystem.TEXT_PRIMARY);
                g2.fillOval(x + 4, y + 4, SIZE - 8, SIZE - 8);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
