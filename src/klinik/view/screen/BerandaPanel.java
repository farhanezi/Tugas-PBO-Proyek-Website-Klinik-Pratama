package klinik.view.screen;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import klinik.view.component.PillButton;
import klinik.view.theme.DesignSystem;

/**
 * Layar 2: Beranda petugas menampilkan kartu aksi utama dan ringkasan angka harian.
 */
public class BerandaPanel extends JPanel {
    private final JLabel lblSubtitle;
    private final JLabel lblSummary;
    private final PillButton btnDaftarkanKunjungan;
    private final PillButton btnBukaPasien;

    public BerandaPanel() {
        setLayout(new GridBagLayout());
        setBackground(DesignSystem.BG_CANVAS);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);

        JLabel lblTitle = new JLabel("Beranda", SwingConstants.CENTER);
        lblTitle.setFont(DesignSystem.fontLight(38f));
        lblTitle.setForeground(DesignSystem.TEXT_PRIMARY);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        lblSubtitle = new JLabel("Masuk sebagai -.", SwingConstants.CENTER);
        lblSubtitle.setFont(DesignSystem.fontRegular(13.5f));
        lblSubtitle.setForeground(DesignSystem.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);

        JPanel cardsRow = new JPanel(new GridBagLayout());
        cardsRow.setOpaque(false);
        cardsRow.setAlignmentX(CENTER_ALIGNMENT);

        btnDaftarkanKunjungan = new PillButton("Daftarkan Kunjungan", PillButton.Variant.PRIMARY);
        btnDaftarkanKunjungan.setPreferredSize(new Dimension(176, 38));

        btnBukaPasien = new PillButton("Buka Data Pasien", PillButton.Variant.OUTLINE);
        btnBukaPasien.setPreferredSize(new Dimension(164, 38));

        JPanel leftCard = createActionCard(
                "Daftarkan kunjungan",
                "Pilih pasien, dokter, dan layanan. Aplikasi menghitung biayanya dari jenis layanan.",
                btnDaftarkanKunjungan,
                430,
                194
        );
        JPanel rightCard = createActionCard(
                "Data pasien",
                "Tambah, ubah, cari, dan hapus data pasien.",
                btnBukaPasien,
                320,
                168
        );

        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.gridy = 0;
        cgbc.anchor = GridBagConstraints.NORTH;
        cgbc.gridx = 0;
        cgbc.insets = new Insets(0, 0, 0, 12);
        cardsRow.add(leftCard, cgbc);

        cgbc.gridx = 1;
        cgbc.insets = new Insets(0, 12, 0, 0);
        cardsRow.add(rightCard, cgbc);

        lblSummary = new JLabel("Hari ini: 0 kunjungan terdaftar, 0 pasien tersimpan.", SwingConstants.CENTER);
        lblSummary.setFont(DesignSystem.fontRegular(13f));
        lblSummary.setForeground(DesignSystem.TEXT_SECONDARY);
        lblSummary.setAlignmentX(CENTER_ALIGNMENT);

        stack.add(lblTitle);
        stack.add(Box.createVerticalStrut(8));
        stack.add(lblSubtitle);
        stack.add(Box.createVerticalStrut(32));
        stack.add(cardsRow);
        stack.add(Box.createVerticalStrut(36));
        stack.add(lblSummary);

        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.insets = new Insets(24, 32, 32, 32);
        add(stack, rootGbc);
    }

    private JPanel createActionCard(
            String title,
            String description,
            PillButton button,
            int prefWidth,
            int prefHeight
    ) {
        JPanel card = new JPanel(new java.awt.BorderLayout(0, 10));
        card.setBackground(DesignSystem.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(DesignSystem.BORDER_SUBTLE, 1),
                new EmptyBorder(24, 26, 24, 26)
        ));
        card.setPreferredSize(new Dimension(prefWidth, prefHeight));
        card.setMinimumSize(new Dimension(280, prefHeight));

        JLabel lblCardTitle = new JLabel(title);
        lblCardTitle.setFont(DesignSystem.fontMedium(18f));
        lblCardTitle.setForeground(DesignSystem.TEXT_PRIMARY);

        JTextArea lblDesc = new JTextArea(description);
        lblDesc.setFont(DesignSystem.fontRegular(13f));
        lblDesc.setForeground(DesignSystem.TEXT_SECONDARY);
        lblDesc.setOpaque(false);
        lblDesc.setEditable(false);
        lblDesc.setFocusable(false);
        lblDesc.setLineWrap(true);
        lblDesc.setWrapStyleWord(true);
        lblDesc.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel btnWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.setBorder(new EmptyBorder(8, 0, 0, 0));
        btnWrap.add(button);

        card.add(lblCardTitle, java.awt.BorderLayout.NORTH);
        card.add(lblDesc, java.awt.BorderLayout.CENTER);
        card.add(btnWrap, java.awt.BorderLayout.SOUTH);

        return card;
    }

    public void setNamaPetugas(String nama) {
        lblSubtitle.setText("Masuk sebagai " + (nama != null && !nama.isBlank() ? nama : "-") + ".");
    }

    public void setRingkasan(int kunjunganHariIni, int totalPasien) {
        lblSummary.setText(
                "Hari ini: " + kunjunganHariIni + " kunjungan terdaftar, " + totalPasien + " pasien tersimpan."
        );
    }

    public void addDaftarkanKunjunganListener(ActionListener listener) {
        btnDaftarkanKunjungan.addActionListener(listener);
    }

    public void addBukaPasienListener(ActionListener listener) {
        btnBukaPasien.addActionListener(listener);
    }
}
