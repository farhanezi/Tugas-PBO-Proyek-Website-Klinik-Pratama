package klinik.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import klinik.view.theme.DesignSystem;

/**
 * Scrollbar minimalis monokrom tanpa tombol panah dan tanpa bayangan/gradasi.
 */
public class MinimalScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(0xD0, 0xD0, 0xCE);
        thumbDarkShadowColor = thumbColor;
        thumbHighlightColor = thumbColor;
        thumbLightShadowColor = thumbColor;
        trackColor = DesignSystem.BG_CARD;
        trackHighlightColor = trackColor;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(DesignSystem.BG_CARD);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(isDragging || isThumbRollover() ? DesignSystem.TEXT_SECONDARY : new Color(0xCE, 0xCE, 0xCC));
        g2.fillRect(thumbBounds.x + 2, thumbBounds.y + 2, Math.max(2, thumbBounds.width - 4), Math.max(8, thumbBounds.height - 4));
        g2.dispose();
    }
}
