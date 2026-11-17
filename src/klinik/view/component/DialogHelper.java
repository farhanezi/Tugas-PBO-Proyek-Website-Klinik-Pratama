package klinik.view.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import klinik.view.theme.DesignSystem;

/**
 * Helper dialog modal berbasis JOptionPane bergaya Swiss monokrom untuk konfirmasi hapus,
 * pesan kesalahan validasi/database, dan konfirmasi keluar aplikasi.
 */
public final class DialogHelper {
    private DialogHelper() {
    }

    public static void showError(Component parent, String kategori, String judul, String pesan) {
        PillButton btnOk = new PillButton("OK", PillButton.Variant.PRIMARY);
        btnOk.setPreferredSize(new Dimension(84, 36));
        showCustomOptionPane(parent, kategori, judul, pesan, new PillButton[]{btnOk}, btnOk);
    }

    public static boolean showDeleteConfirm(Component parent, String judul, String pesan) {
        PillButton btnBatal = new PillButton("Batal", PillButton.Variant.OUTLINE);
        btnBatal.setPreferredSize(new Dimension(88, 36));
        PillButton btnHapus = new PillButton("Hapus", PillButton.Variant.DANGER);
        btnHapus.setPreferredSize(new Dimension(92, 36));
        PillButton chosen = showCustomOptionPane(
                parent,
                "Konfirmasi hapus",
                judul,
                pesan,
                new PillButton[]{btnBatal, btnHapus},
                btnBatal
        );
        return chosen == btnHapus;
    }

    public static boolean showLogoutConfirm(Component parent) {
        PillButton btnBatal = new PillButton("Batal", PillButton.Variant.OUTLINE);
        btnBatal.setPreferredSize(new Dimension(88, 36));
        PillButton btnKeluar = new PillButton("Keluar", PillButton.Variant.PRIMARY);
        btnKeluar.setPreferredSize(new Dimension(92, 36));
        PillButton chosen = showCustomOptionPane(
                parent,
                "Konfirmasi keluar",
                "Keluar dari aplikasi?",
                "Anda kembali ke halaman Login. Data yang sudah Anda simpan tetap ada di database.",
                new PillButton[]{btnBatal, btnKeluar},
                btnBatal
        );
        return chosen == btnKeluar;
    }

    public static JPanel createPreviewPanel(
            String kategori,
            String judul,
            String pesan,
            PillButton[] buttons
    ) {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(440, d.width), Math.max(204, d.height));
            }
        };
        root.setBackground(DesignSystem.BG_CARD);
        root.setBorder(new LineBorder(DesignSystem.BORDER_SUBTLE, 1));

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(DesignSystem.BG_CARD);
        headerBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DesignSystem.BORDER_SUBTLE),
                new EmptyBorder(12, 20, 12, 20)
        ));
        JLabel lblKategori = new JLabel(kategori != null ? kategori : "Informasi");
        lblKategori.setFont(DesignSystem.fontRegular(12f));
        lblKategori.setForeground(DesignSystem.TEXT_SECONDARY);
        headerBar.add(lblKategori, BorderLayout.WEST);

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setBackground(DesignSystem.BG_CARD);
        body.setBorder(new EmptyBorder(18, 20, 10, 20));

        JLabel lblJudul = new JLabel(judul != null ? judul : "");
        lblJudul.setFont(DesignSystem.fontSemiBold(17f));
        lblJudul.setForeground(DesignSystem.TEXT_PRIMARY);

        String escapedMsg = pesan != null ? pesan.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
        JLabel lblPesan = new JLabel("<html><div style='width:380px; line-height:1.45;'>" + escapedMsg + "</div></html>");
        lblPesan.setFont(DesignSystem.fontRegular(13f));
        lblPesan.setForeground(DesignSystem.TEXT_SECONDARY);

        body.add(lblJudul, BorderLayout.NORTH);
        body.add(lblPesan, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(DesignSystem.BG_CARD);
        footer.setBorder(new EmptyBorder(8, 20, 18, 20));
        for (PillButton btn : buttons) {
            footer.add(btn);
        }

        root.add(headerBar, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private static PillButton showCustomOptionPane(
            Component parent,
            String kategori,
            String judul,
            String pesan,
            PillButton[] buttons,
            PillButton defaultCancel
    ) {
        final PillButton[] result = {null};
        JPanel root = createPreviewPanel(kategori, judul, pesan, buttons);

        JOptionPane pane = new JOptionPane(
                root,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null
        );
        pane.setBackground(DesignSystem.BG_CARD);
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JFrame owner = parent instanceof JFrame ? (JFrame) parent : (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = pane.createDialog(owner, kategori != null ? kategori : "Klinik Pratama");
        dialog.setUndecorated(true);
        dialog.getContentPane().setBackground(DesignSystem.BG_CARD);
        if (dialog.getContentPane() instanceof JComponent jc) {
            jc.setBorder(new LineBorder(DesignSystem.BORDER_INPUT, 1));
        }
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        for (PillButton btn : buttons) {
            btn.addActionListener(e -> {
                result[0] = btn;
                dialog.dispose();
            });
        }

        dialog.getRootPane().registerKeyboardAction(
                e -> {
                    result[0] = defaultCancel;
                    dialog.dispose();
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialog.setVisible(true);
        return result[0];
    }
}
