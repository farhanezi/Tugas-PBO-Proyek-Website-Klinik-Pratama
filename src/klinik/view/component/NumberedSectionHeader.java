package klinik.view.component;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import klinik.view.theme.DesignSystem;

/**
 * Header bagian bernomor (01, 02, 03) dengan motif lingkaran bergaris sesuai layar Kunjungan.
 */
public class NumberedSectionHeader extends JPanel {
    public NumberedSectionHeader(String numberText, String titleText) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        JPanel badge = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(26, 26);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                DesignSystem.applyQualityHints(g2);

                g2.setColor(DesignSystem.TEXT_PRIMARY);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawOval(1, 1, 23, 23);

                g2.setFont(DesignSystem.monoRegular(11f));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(numberText)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(numberText, tx, ty);
                g2.dispose();
            }
        };
        badge.setOpaque(false);

        JLabel title = new JLabel("  " + titleText);
        title.setFont(DesignSystem.fontMedium(14.5f));
        title.setForeground(DesignSystem.TEXT_PRIMARY);

        add(badge);
        add(title);
    }
}
