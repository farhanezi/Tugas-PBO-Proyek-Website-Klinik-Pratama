package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import klinik.view.theme.DesignSystem;

/**
 * Checkbox bersudut tegas dengan isi hitam penuh dan tanda centang putih saat aktif.
 */
public class SwissCheckBox extends JCheckBox {
    public SwissCheckBox(String text) {
        super(text);
        setFont(DesignSystem.fontRegular(13f));
        setForeground(DesignSystem.TEXT_PRIMARY);
        setBackground(DesignSystem.BG_CARD);
        setOpaque(false);
        setFocusPainted(false);
        setIconTextGap(8);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIcon(new CheckIcon());
    }

    private static class CheckIcon implements Icon {
        private static final int SIZE = 16;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            JCheckBox cb = (JCheckBox) c;
            Graphics2D g2 = (Graphics2D) g.create();
            DesignSystem.applyQualityHints(g2);

            boolean selected = cb.isSelected();
            boolean hover = cb.getModel().isRollover();

            if (selected) {
                g2.setColor(DesignSystem.TEXT_PRIMARY);
                g2.fillRect(x + 1, y + 1, SIZE - 2, SIZE - 2);

                Path2D check = new Path2D.Float();
                check.moveTo(x + 4.0, y + 8.3);
                check.lineTo(x + 6.8, y + 11.2);
                check.lineTo(x + 12.0, y + 5.2);
                g2.setColor(DesignSystem.BG_CARD);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(check);
            } else {
                g2.setColor(DesignSystem.BG_CARD);
                g2.fillRect(x + 1, y + 1, SIZE - 2, SIZE - 2);
                g2.setColor(hover ? DesignSystem.TEXT_PRIMARY : DesignSystem.BORDER_INPUT);
                g2.setStroke(new BasicStroke(1.25f));
                g2.drawRect(x + 1, y + 1, SIZE - 2, SIZE - 2);
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
