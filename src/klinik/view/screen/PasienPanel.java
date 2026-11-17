package klinik.view.screen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import klinik.view.component.PillButton;
import klinik.view.component.SharpDateSpinner;
import klinik.view.component.SharpTextField;
import klinik.view.component.SwissCheckBox;
import klinik.view.component.SwissRadioButton;
import klinik.view.component.SwissTablePanel;
import klinik.view.table.PasienTableModel;
import klinik.view.theme.DesignSystem;

/**
 * Layar 3: Data Pasien (CRUD lengkap dengan pencarian langsung dan validasi field).
 */
public class PasienPanel extends JPanel {
    private final SharpTextField txtCari;
    private final SharpTextField txtNama;
    private final SharpTextField txtTelepon;
    private final SharpTextField txtAlamat;
    private final SharpDateSpinner spnTanggalLahir;
    private final SwissRadioButton rbLaki;
    private final SwissRadioButton rbPerempuan;
    private final SwissCheckBox chkBpjs;

    private final PillButton btnSimpan;
    private final PillButton btnUbah;
    private final PillButton btnHapus;
    private final PillButton btnBersihkan;

    private final PasienTableModel tableModel;
    private final SwissTablePanel tablePanel;
    private final JLabel lblCountFooter;

    private final Map<String, JLabel> errorLabels = new HashMap<>();

    public PasienPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(DesignSystem.BG_CANVAS);
        setBorder(new EmptyBorder(24, 34, 24, 34));

        // Top title + search bar
        JPanel topRow = new JPanel(new BorderLayout(16, 0));
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Data Pasien");
        lblTitle.setFont(DesignSystem.fontLight(32f));
        lblTitle.setForeground(DesignSystem.TEXT_PRIMARY);

        JPanel searchWrap = new JPanel();
        searchWrap.setLayout(new BoxLayout(searchWrap, BoxLayout.Y_AXIS));
        searchWrap.setOpaque(false);
        JLabel lblCari = new JLabel("Cari nama pasien");
        lblCari.setFont(DesignSystem.fontRegular(12f));
        lblCari.setForeground(DesignSystem.TEXT_SECONDARY);
        lblCari.setAlignmentX(LEFT_ALIGNMENT);

        txtCari = new SharpTextField("Ketik nama atau ID pasien...", false);
        txtCari.setPreferredSize(new Dimension(280, 38));
        txtCari.setMaximumSize(new Dimension(320, 38));
        txtCari.setAlignmentX(LEFT_ALIGNMENT);

        searchWrap.add(lblCari);
        searchWrap.add(Box.createVerticalStrut(4));
        searchWrap.add(txtCari);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(searchWrap, BorderLayout.EAST);

        // Two-column main content
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        // Left card: Form pasien
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(DesignSystem.BG_CARD);
        formCard.setBorder(new CompoundBorder(
                new LineBorder(DesignSystem.BORDER_SUBTLE, 1),
                new EmptyBorder(20, 22, 20, 22)
        ));
        formCard.setPreferredSize(new Dimension(334, 520));
        formCard.setMinimumSize(new Dimension(300, 440));

        JLabel lblFormTitle = new JLabel("Form pasien");
        lblFormTitle.setFont(DesignSystem.fontMedium(15.5f));
        lblFormTitle.setForeground(DesignSystem.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(LEFT_ALIGNMENT);

        txtNama = new SharpTextField("Nama lengkap pasien", false);
        txtTelepon = new SharpTextField("08xxxxxxxxxx", true);
        txtAlamat = new SharpTextField("Alamat tempat tinggal", false);
        spnTanggalLahir = new SharpDateSpinner();

        rbLaki = new SwissRadioButton("Laki-laki", true);
        rbPerempuan = new SwissRadioButton("Perempuan", false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbLaki);
        genderGroup.add(rbPerempuan);

        JPanel genderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        genderRow.setOpaque(false);
        genderRow.setAlignmentX(LEFT_ALIGNMENT);
        genderRow.add(rbLaki);
        genderRow.add(Box.createHorizontalStrut(18));
        genderRow.add(rbPerempuan);

        chkBpjs = new SwissCheckBox("Pasien BPJS");
        chkBpjs.setAlignmentX(LEFT_ALIGNMENT);

        btnSimpan = new PillButton("Simpan", PillButton.Variant.PRIMARY);
        btnSimpan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSimpan.setAlignmentX(LEFT_ALIGNMENT);

        btnUbah = new PillButton("Ubah", PillButton.Variant.OUTLINE);
        btnHapus = new PillButton("Hapus", PillButton.Variant.DANGER);
        btnBersihkan = new PillButton("Bersihkan", PillButton.Variant.OUTLINE);

        JPanel subButtons = new JPanel(new GridLayout(1, 3, 8, 0));
        subButtons.setOpaque(false);
        subButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        subButtons.setPreferredSize(new Dimension(280, 36));
        subButtons.setAlignmentX(LEFT_ALIGNMENT);
        subButtons.add(btnUbah);
        subButtons.add(btnHapus);
        subButtons.add(btnBersihkan);

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(14));
        addFieldGroup(formCard, "Nama lengkap", txtNama, "nama");
        addFieldGroup(formCard, "No. telepon", txtTelepon, "noTelepon");
        addFieldGroup(formCard, "Alamat", txtAlamat, "alamat");
        addFieldGroup(formCard, "Tanggal lahir", spnTanggalLahir, "tanggalLahir");

        JLabel lblGender = new JLabel("Jenis kelamin");
        lblGender.setFont(DesignSystem.fontRegular(12f));
        lblGender.setForeground(DesignSystem.TEXT_SECONDARY);
        lblGender.setAlignmentX(LEFT_ALIGNMENT);
        formCard.add(lblGender);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(genderRow);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(chkBpjs);
        formCard.add(Box.createVerticalStrut(18));
        formCard.add(btnSimpan);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(subButtons);
        formCard.add(Box.createVerticalGlue());

        // Right card: Daftar pasien
        JPanel listCard = new JPanel(new BorderLayout(0, 12));
        listCard.setBackground(DesignSystem.BG_CARD);
        listCard.setBorder(new CompoundBorder(
                new LineBorder(DesignSystem.BORDER_SUBTLE, 1),
                new EmptyBorder(20, 22, 16, 22)
        ));

        JLabel lblListTitle = new JLabel("Daftar pasien");
        lblListTitle.setFont(DesignSystem.fontMedium(15.5f));
        lblListTitle.setForeground(DesignSystem.TEXT_PRIMARY);

        tableModel = new PasienTableModel();
        tablePanel = new SwissTablePanel(
                tableModel,
                new String[]{"ID", "Nama", "Telepon", "Tgl lahir", "JK", "BPJS"}
        );
        tablePanel.configureTexts(
                "pasien",
                "Isi form di sebelah kiri, lalu klik Simpan untuk menambah pasien pertama."
        );
        tablePanel.setPrimaryBoldColumn(2);
        tablePanel.configureColumn(0, 24, 28, 28, false, false);
        tablePanel.configureColumn(1, 74, 86, 100, true, false);
        tablePanel.configureColumn(2, 140, 220, -1, false, false);
        tablePanel.configureColumn(3, 114, 140, -1, true, false);
        tablePanel.configureColumn(4, 96, 110, 140, true, false);
        tablePanel.configureColumn(5, 36, 46, 56, false, false);
        tablePanel.configureColumn(6, 52, 64, 80, false, false);

        lblCountFooter = new JLabel("Menampilkan 0 dari 0 pasien.");
        lblCountFooter.setFont(DesignSystem.fontRegular(12f));
        lblCountFooter.setForeground(DesignSystem.TEXT_SECONDARY);

        listCard.add(lblListTitle, BorderLayout.NORTH);
        listCard.add(tablePanel, BorderLayout.CENTER);
        listCard.add(lblCountFooter, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 16);
        body.add(formCard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(listCard, gbc);

        add(topRow, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        setActionButtonsState(true, false);
    }

    private void addFieldGroup(JPanel container, String labelText, javax.swing.JComponent input, String key) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(DesignSystem.fontRegular(12f));
        lbl.setForeground(DesignSystem.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        input.setAlignmentX(LEFT_ALIGNMENT);

        JLabel err = new JLabel(" ");
        err.setFont(DesignSystem.fontRegular(11.5f));
        err.setForeground(DesignSystem.DANGER);
        err.setAlignmentX(LEFT_ALIGNMENT);
        err.setVisible(false);
        errorLabels.put(key, err);

        container.add(lbl);
        container.add(Box.createVerticalStrut(4));
        container.add(input);
        container.add(Box.createVerticalStrut(3));
        container.add(err);
        container.add(Box.createVerticalStrut(9));
    }

    public String getSearchKeyword() {
        return txtCari.getText();
    }

    public void clearSearch() {
        txtCari.setText("");
    }

    public String getNamaInput() {
        return txtNama.getText();
    }

    public String getTeleponInput() {
        return txtTelepon.getText();
    }

    public String getAlamatInput() {
        return txtAlamat.getText();
    }

    public LocalDate getTanggalLahirInput() throws ParseException {
        return spnTanggalLahir.getLocalDate();
    }

    public String getJenisKelaminInput() {
        return rbLaki.isSelected() ? "L" : "P";
    }

    public boolean isBpjsInput() {
        return chkBpjs.isSelected();
    }

    public void populateForm(klinik.model.Pasien pasien) {
        clearFieldErrors();
        if (pasien == null) {
            txtNama.setText("");
            txtTelepon.setText("");
            txtAlamat.setText("");
            spnTanggalLahir.setLocalDate(LocalDate.of(1995, 1, 1));
            rbLaki.setSelected(true);
            chkBpjs.setSelected(false);
            return;
        }
        txtNama.setText(pasien.getNama());
        txtTelepon.setText(pasien.getNoTelepon());
        txtAlamat.setText(pasien.getAlamat());
        spnTanggalLahir.setLocalDate(pasien.getTanggalLahir());
        if ("P".equals(pasien.getJenisKelamin())) {
            rbPerempuan.setSelected(true);
        } else {
            rbLaki.setSelected(true);
        }
        chkBpjs.setSelected(pasien.isPunyaBpjs());
    }

    public void clearFieldErrors() {
        txtNama.setError(false);
        txtTelepon.setError(false);
        txtAlamat.setError(false);
        spnTanggalLahir.setError(false);
        for (JLabel err : errorLabels.values()) {
            err.setVisible(false);
            err.setText(" ");
        }
        revalidate();
        repaint();
    }

    public void showFieldError(String fieldKey, String message) {
        JLabel err = errorLabels.get(fieldKey);
        if (err != null) {
            err.setText("<html><div style='width:270px;'>" + message + "</div></html>");
            err.setVisible(true);
        }
        switch (fieldKey) {
            case "nama" -> {
                txtNama.setError(true);
                txtNama.requestFocusInWindow();
            }
            case "noTelepon" -> {
                txtTelepon.setError(true);
                txtTelepon.requestFocusInWindow();
            }
            case "alamat" -> {
                txtAlamat.setError(true);
                txtAlamat.requestFocusInWindow();
            }
            case "tanggalLahir" -> {
                spnTanggalLahir.setError(true);
                spnTanggalLahir.getTextField().requestFocusInWindow();
            }
            default -> {
            }
        }
        revalidate();
        repaint();
    }

    public void setTeleponInlineError(String messageOrNull) {
        JLabel err = errorLabels.get("noTelepon");
        if (messageOrNull == null) {
            txtTelepon.setError(false);
            if (err != null) {
                err.setVisible(false);
                err.setText(" ");
            }
        } else {
            txtTelepon.setError(true);
            if (err != null) {
                err.setText("<html><div style='width:270px;'>" + messageOrNull + "</div></html>");
                err.setVisible(true);
            }
        }
        revalidate();
        repaint();
    }

    public void setActionButtonsState(boolean canSave, boolean hasSelection) {
        btnSimpan.setEnabled(canSave);
        btnUbah.setEnabled(canSave && hasSelection);
        btnHapus.setEnabled(canSave && hasSelection);
        btnBersihkan.setEnabled(canSave);
    }

    public PasienTableModel getTableModel() {
        return tableModel;
    }

    public SwissTablePanel getTablePanel() {
        return tablePanel;
    }

    public void setCountSummary(int shown, int total) {
        lblCountFooter.setText("Menampilkan " + shown + " dari " + total + " pasien.");
    }

    public void addSearchDocumentListener(DocumentListener listener) {
        txtCari.getDocument().addDocumentListener(listener);
    }

    public void addTeleponDocumentListener(DocumentListener listener) {
        txtTelepon.getDocument().addDocumentListener(listener);
    }

    public void addSimpanListener(ActionListener listener) {
        btnSimpan.addActionListener(listener);
        txtNama.addActionListener(listener);
        txtTelepon.addActionListener(listener);
        txtAlamat.addActionListener(listener);
    }

    public void addUbahListener(ActionListener listener) {
        btnUbah.addActionListener(listener);
    }

    public void addHapusListener(ActionListener listener) {
        btnHapus.addActionListener(listener);
    }

    public void addBersihkanListener(ActionListener listener) {
        btnBersihkan.addActionListener(listener);
    }
}
