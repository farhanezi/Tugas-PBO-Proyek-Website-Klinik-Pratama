package klinik.view.screen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import klinik.model.Petugas;
import klinik.view.component.NavHeaderPanel;
import klinik.view.theme.DesignSystem;

/**
 * JFrame utama aplikasi Klinik Pratama.
 * Menggunakan CardLayout luar (LOGIN / WORKSPACE) dan CardLayout dalam
 * (BERANDA / PASIEN / KUNJUNGAN) dengan header navigasi persisten.
 */
public class KlinikFrame extends JFrame {
    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_WORKSPACE = "WORKSPACE";

    public static final String TAB_BERANDA = "BERANDA";
    public static final String TAB_PASIEN = "PASIEN";
    public static final String TAB_KUNJUNGAN = "KUNJUNGAN";

    private final CardLayout rootCards = new CardLayout();
    private final JPanel rootPanel = new JPanel(rootCards);

    private final CardLayout workspaceCards = new CardLayout();
    private final JPanel workspaceContent = new JPanel(workspaceCards);

    private final LoginPanel loginPanel;
    private final NavHeaderPanel navHeader;
    private final BerandaPanel berandaPanel;
    private final PasienPanel pasienPanel;
    private final KunjunganPanel kunjunganPanel;

    private Runnable onEnterBeranda;
    private Runnable onEnterPasien;
    private Runnable onEnterKunjungan;

    public KlinikFrame() {
        super("Klinik Pratama — Sistem Pendaftaran");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 740));
        setPreferredSize(new Dimension(1280, 820));

        loginPanel = new LoginPanel();
        navHeader = new NavHeaderPanel();
        berandaPanel = new BerandaPanel();
        pasienPanel = new PasienPanel();
        kunjunganPanel = new KunjunganPanel();

        workspaceContent.setBackground(DesignSystem.BG_CANVAS);
        workspaceContent.add(berandaPanel, TAB_BERANDA);
        workspaceContent.add(pasienPanel, TAB_PASIEN);
        workspaceContent.add(kunjunganPanel, TAB_KUNJUNGAN);

        JPanel workspaceShell = new JPanel(new BorderLayout());
        workspaceShell.setBackground(DesignSystem.BG_CANVAS);
        workspaceShell.add(navHeader, BorderLayout.NORTH);
        workspaceShell.add(workspaceContent, BorderLayout.CENTER);

        rootPanel.setBackground(DesignSystem.BG_CANVAS);
        rootPanel.add(loginPanel, CARD_LOGIN);
        rootPanel.add(workspaceShell, CARD_WORKSPACE);

        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);

        navHeader.addBerandaListener(e -> showTab(TAB_BERANDA));
        navHeader.addPasienListener(e -> showTab(TAB_PASIEN));
        navHeader.addKunjunganListener(e -> showTab(TAB_KUNJUNGAN));

        berandaPanel.addBukaPasienListener(e -> showTab(TAB_PASIEN));
        berandaPanel.addDaftarkanKunjunganListener(e -> showTab(TAB_KUNJUNGAN));
    }

    public void showLogin() {
        loginPanel.resetForm();
        rootCards.show(rootPanel, CARD_LOGIN);
        loginPanel.focusUsername();
    }

    public void showWorkspace(Petugas petugas) {
        String nama = petugas != null ? petugas.getNama() : "-";
        navHeader.setNamaPetugas(nama);
        berandaPanel.setNamaPetugas(nama);
        rootCards.show(rootPanel, CARD_WORKSPACE);
        showTab(TAB_BERANDA);
    }

    public void showTab(String tabName) {
        navHeader.setActiveTab(tabName);
        workspaceCards.show(workspaceContent, tabName);
        switch (tabName) {
            case TAB_BERANDA -> {
                if (onEnterBeranda != null) {
                    onEnterBeranda.run();
                }
            }
            case TAB_PASIEN -> {
                if (onEnterPasien != null) {
                    onEnterPasien.run();
                }
            }
            case TAB_KUNJUNGAN -> {
                if (onEnterKunjungan != null) {
                    onEnterKunjungan.run();
                }
            }
            default -> {
            }
        }
    }

    public void setTabCallbacks(Runnable onBeranda, Runnable onPasien, Runnable onKunjungan) {
        this.onEnterBeranda = onBeranda;
        this.onEnterPasien = onPasien;
        this.onEnterKunjungan = onKunjungan;
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public NavHeaderPanel getNavHeader() {
        return navHeader;
    }

    public BerandaPanel getBerandaPanel() {
        return berandaPanel;
    }

    public PasienPanel getPasienPanel() {
        return pasienPanel;
    }

    public KunjunganPanel getKunjunganPanel() {
        return kunjunganPanel;
    }
}
