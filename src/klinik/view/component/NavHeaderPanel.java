package klinik.view.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import klinik.view.theme.DesignSystem;

/**
 * Header navigasi persisten menggunakan BorderLayout, berisi nama klinik,
 * tiga JToggleButton dalam ButtonGroup (Beranda, Pasien, Kunjungan) dengan garis bawah hitam 2px
 * pada tab aktif, nama petugas login, dan tombol pill Keluar.
 */
public class NavHeaderPanel extends JPanel {
    private final NavTabButton tabBeranda;
    private final NavTabButton tabPasien;
    private final NavTabButton tabKunjungan;
    private final JLabel lblPetugas;
    private final PillButton btnKeluar;

    public NavHeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(DesignSystem.BG_CARD);
        setPreferredSize(new Dimension(0, 60));
        setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DesignSystem.BORDER_SUBTLE),
                new EmptyBorder(0, 28, 0, 28)
        ));

        JLabel brandLabel = new JLabel("Klinik Pratama");
        brandLabel.setFont(DesignSystem.fontMedium(15.5f));
        brandLabel.setForeground(DesignSystem.TEXT_PRIMARY);
        brandLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabBeranda.doClick();
            }
        });
        JPanel leftWrap = new JPanel(new BorderLayout());
        leftWrap.setOpaque(false);
        leftWrap.setPreferredSize(new Dimension(240, 59));
        leftWrap.add(brandLabel, BorderLayout.WEST);

        tabBeranda = new NavTabButton("Beranda");
        tabPasien = new NavTabButton("Pasien");
        tabKunjungan = new NavTabButton("Kunjungan");

        ButtonGroup group = new ButtonGroup();
        group.add(tabBeranda);
        group.add(tabPasien);
        group.add(tabKunjungan);
        tabBeranda.setSelected(true);

        JPanel centerTabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        centerTabs.setOpaque(false);
        centerTabs.add(tabBeranda);
        centerTabs.add(createDotSeparator());
        centerTabs.add(tabPasien);
        centerTabs.add(createDotSeparator());
        centerTabs.add(tabKunjungan);

        lblPetugas = new JLabel("Petugas: -");
        lblPetugas.setFont(DesignSystem.fontRegular(12.5f));
        lblPetugas.setForeground(DesignSystem.TEXT_SECONDARY);

        btnKeluar = new PillButton("Keluar", PillButton.Variant.OUTLINE);
        btnKeluar.setFont(DesignSystem.fontMedium(12.5f));
        btnKeluar.setPreferredSize(new Dimension(88, 34));

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        rightWrap.setOpaque(false);
        rightWrap.setPreferredSize(new Dimension(290, 59));
        rightWrap.add(lblPetugas);
        rightWrap.add(btnKeluar);

        add(leftWrap, BorderLayout.WEST);
        add(centerTabs, BorderLayout.CENTER);
        add(rightWrap, BorderLayout.EAST);
    }

    private JLabel createDotSeparator() {
        JLabel dot = new JLabel("·");
        dot.setFont(DesignSystem.fontRegular(14f));
        dot.setForeground(DesignSystem.TEXT_SECONDARY);
        return dot;
    }

    public void setNamaPetugas(String nama) {
        lblPetugas.setText("Petugas: " + (nama != null && !nama.isBlank() ? nama : "-"));
    }

    public void setActiveTab(String screenName) {
        switch (screenName) {
            case "BERANDA" -> tabBeranda.setSelected(true);
            case "PASIEN" -> tabPasien.setSelected(true);
            case "KUNJUNGAN" -> tabKunjungan.setSelected(true);
            default -> {
            }
        }
        repaint();
    }

    public void addBerandaListener(ActionListener listener) {
        tabBeranda.addActionListener(listener);
    }

    public void addPasienListener(ActionListener listener) {
        tabPasien.addActionListener(listener);
    }

    public void addKunjunganListener(ActionListener listener) {
        tabKunjungan.addActionListener(listener);
    }

    public void addKeluarListener(ActionListener listener) {
        btnKeluar.addActionListener(listener);
    }

    private static class NavTabButton extends JToggleButton {
        private boolean hovered = false;

        NavTabButton(String text) {
            super(text);
            setFont(DesignSystem.fontRegular(13.5f));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 6, 0, 6));
            setPreferredSize(new Dimension(super.getPreferredSize().width + 12, 59));

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

            boolean selected = isSelected();
            g2.setFont(selected ? DesignSystem.fontMedium(13.5f) : DesignSystem.fontRegular(13.5f));
            Color fg = (selected || hovered) ? DesignSystem.TEXT_PRIMARY : DesignSystem.TEXT_SECONDARY;
            g2.setColor(fg);

            FontMetrics fm = g2.getFontMetrics();
            String txt = getText();
            int tx = (getWidth() - fm.stringWidth(txt)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(txt, tx, ty);

            if (selected) {
                g2.setColor(DesignSystem.TEXT_PRIMARY);
                g2.fillRect(4, getHeight() - 2, getWidth() - 8, 2);
            }
            g2.dispose();
        }
    }
}
