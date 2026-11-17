package klinik.view.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import klinik.model.Dokter;
import klinik.model.Layanan;
import klinik.model.Pasien;
import klinik.view.component.CalendarPickerPanel;
import klinik.view.component.NumberedSectionHeader;
import klinik.view.component.PillButton;
import klinik.view.component.SharpComboBox;
import klinik.view.component.SharpTextArea;
import klinik.view.component.SwissTablePanel;
import klinik.view.table.KunjunganTableModel;
import klinik.view.theme.DesignSystem;

/**
 * Layar 4: Kunjungan (CRUD) dengan form 3 bagian bernomor (01, 02, 03),
 * kalender tanggal kunjungan, preview biaya polimorfik, dan tabel riwayat kunjungan.
 */
public class KunjunganPanel extends JPanel {
    private final SharpComboBox<Pasien> cmbPasien;
    private final SharpComboBox<Dokter> cmbDokter;
    private final SharpComboBox<Layanan> cmbLayanan;

    private final PillButton btnSimpan;
    private final PillButton btnUbah;
    private final PillButton btnHapus;
    private final PillButton btnBersihkan;

    private final CalendarPickerPanel calendarPicker;
    private final SharpTextArea txtKeluhan;
    private final SharpTextArea txtDiagnosa;

    private final JLabel lblPerkiraanBiaya;
    private final JLabel lblKeteranganBiaya;

    private final KunjunganTableModel tableModel;
    private final SwissTablePanel tablePanel;
    private final JLabel lblFooterCount;

    public KunjunganPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(DesignSystem.BG_CANVAS);
        setBorder(new EmptyBorder(22, 34, 22, 34));

        JLabel lblTitle = new JLabel("Kunjungan");
        lblTitle.setFont(DesignSystem.fontLight(32f));
        lblTitle.setForeground(DesignSystem.TEXT_PRIMARY);

        // Top unified 3-column card
        JPanel topCard = new JPanel(new GridBagLayout());
        topCard.setBackground(DesignSystem.BG_CARD);
        topCard.setBorder(new LineBorder(DesignSystem.BORDER_SUBTLE, 1));

        // Section 01: Pasien & layanan
        JPanel sec01 = new JPanel();
        sec01.setLayout(new BoxLayout(sec01, BoxLayout.Y_AXIS));
        sec01.setBackground(DesignSystem.BG_CARD);
        sec01.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, DesignSystem.BORDER_SUBTLE),
                new EmptyBorder(18, 20, 18, 20)
        ));

        NumberedSectionHeader hdr01 = new NumberedSectionHeader("01", "Pasien & layanan");
        hdr01.setAlignmentX(LEFT_ALIGNMENT);

        cmbPasien = new SharpComboBox<>();
        cmbDokter = new SharpComboBox<>();
        cmbLayanan = new SharpComboBox<>();

        btnSimpan = new PillButton("Simpan", PillButton.Variant.PRIMARY);
        btnSimpan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSimpan.setAlignmentX(LEFT_ALIGNMENT);

        btnUbah = new PillButton("Ubah", PillButton.Variant.OUTLINE);
        btnHapus = new PillButton("Hapus", PillButton.Variant.DANGER);
        btnBersihkan = new PillButton("Bersihkan", PillButton.Variant.OUTLINE);

        JPanel subButtons = new JPanel(new GridLayout(1, 3, 8, 0));
        subButtons.setOpaque(false);
        subButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        subButtons.setPreferredSize(new Dimension(260, 36));
        subButtons.setAlignmentX(LEFT_ALIGNMENT);
        subButtons.add(btnUbah);
        subButtons.add(btnHapus);
        subButtons.add(btnBersihkan);

        sec01.add(hdr01);
        sec01.add(Box.createVerticalStrut(14));
        addLabeledControl(sec01, "Pasien", cmbPasien);
        addLabeledControl(sec01, "Dokter", cmbDokter);
        addLabeledControl(sec01, "Layanan", cmbLayanan);
        sec01.add(Box.createVerticalStrut(6));
        sec01.add(btnSimpan);
        sec01.add(Box.createVerticalStrut(10));
        sec01.add(subButtons);
        sec01.add(Box.createVerticalGlue());

        // Section 02: Tanggal kunjungan
        JPanel sec02 = new JPanel(new BorderLayout(0, 14));
        sec02.setBackground(DesignSystem.BG_CARD);
        sec02.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, DesignSystem.BORDER_SUBTLE),
                new EmptyBorder(18, 20, 18, 20)
        ));
        NumberedSectionHeader hdr02 = new NumberedSectionHeader("02", "Tanggal kunjungan");
        calendarPicker = new CalendarPickerPanel();
        sec02.add(hdr02, BorderLayout.NORTH);
        sec02.add(calendarPicker, BorderLayout.CENTER);

        // Section 03: Catatan medis + black Perkiraan biaya card
        JPanel sec03 = new JPanel();
        sec03.setLayout(new BoxLayout(sec03, BoxLayout.Y_AXIS));
        sec03.setBackground(DesignSystem.BG_CARD);
        sec03.setBorder(new EmptyBorder(18, 20, 18, 20));

        NumberedSectionHeader hdr03 = new NumberedSectionHeader("03", "Catatan medis");
        hdr03.setAlignmentX(LEFT_ALIGNMENT);

        txtKeluhan = new SharpTextArea("Keluhan yang disampaikan pasien...", 3);
        txtKeluhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        txtKeluhan.setPreferredSize(new Dimension(280, 72));
        txtKeluhan.setAlignmentX(LEFT_ALIGNMENT);

        txtDiagnosa = new SharpTextArea("Diagnosa dari dokter...", 3);
        txtDiagnosa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        txtDiagnosa.setPreferredSize(new Dimension(280, 72));
        txtDiagnosa.setAlignmentX(LEFT_ALIGNMENT);

        JPanel costCard = new JPanel(new BorderLayout(0, 6));
        costCard.setBackground(DesignSystem.TEXT_PRIMARY);
        costCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        costCard.setAlignmentX(LEFT_ALIGNMENT);
        costCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));

        JPanel costTop = new JPanel(new BorderLayout(8, 0));
        costTop.setOpaque(false);
        JLabel lblBiayaTitle = new JLabel("Perkiraan biaya");
        lblBiayaTitle.setFont(DesignSystem.fontRegular(12.5f));
        lblBiayaTitle.setForeground(new Color(0xE4, 0xE4, 0xE4));
        lblBiayaTitle.putClientProperty(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        lblPerkiraanBiaya = new JLabel("Rp 0");
        lblPerkiraanBiaya.setFont(DesignSystem.monoSemiBold(21f));
        lblPerkiraanBiaya.setForeground(DesignSystem.BG_CARD);
        lblPerkiraanBiaya.putClientProperty(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        costTop.add(lblBiayaTitle, BorderLayout.WEST);
        costTop.add(lblPerkiraanBiaya, BorderLayout.EAST);

        lblKeteranganBiaya = new JLabel("Pilih pasien dan layanan untuk menghitung biaya.");
        lblKeteranganBiaya.setFont(DesignSystem.fontRegular(11.5f));
        lblKeteranganBiaya.setForeground(new Color(0xCC, 0xCC, 0xCC));
        lblKeteranganBiaya.putClientProperty(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        costCard.add(costTop, BorderLayout.NORTH);
        costCard.add(lblKeteranganBiaya, BorderLayout.SOUTH);

        sec03.add(hdr03);
        sec03.add(Box.createVerticalStrut(14));
        addLabeledTextArea(sec03, "Keluhan", txtKeluhan);
        addLabeledTextArea(sec03, "Diagnosa", txtDiagnosa);
        sec03.add(Box.createVerticalStrut(4));
        sec03.add(costCard);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.34;
        topCard.add(sec01, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.31;
        topCard.add(sec02, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.35;
        topCard.add(sec03, gbc);

        // Bottom table card: Riwayat kunjungan
        JPanel bottomSection = new JPanel(new BorderLayout(0, 10));
        bottomSection.setOpaque(false);

        JLabel lblRiwayat = new JLabel("Riwayat kunjungan");
        lblRiwayat.setFont(DesignSystem.fontMedium(15.5f));
        lblRiwayat.setForeground(DesignSystem.TEXT_PRIMARY);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(DesignSystem.BG_CARD);
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(DesignSystem.BORDER_SUBTLE, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        tableModel = new KunjunganTableModel();
        tablePanel = new SwissTablePanel(
                tableModel,
                new String[]{"Tanggal", "Pasien", "Dokter", "Layanan", "Total"}
        );
        tablePanel.configureTexts(
                "kunjungan",
                "Isi form di atas, lalu klik Simpan untuk mencatat kunjungan pertama."
        );
        tablePanel.setPrimaryBoldColumn(2);
        tablePanel.configureColumn(0, 24, 28, 28, false, false);
        tablePanel.configureColumn(1, 100, 118, 140, true, false);
        tablePanel.configureColumn(2, 140, 200, -1, false, false);
        tablePanel.configureColumn(3, 160, 230, -1, false, false);
        tablePanel.configureColumn(4, 150, 200, -1, false, false);
        tablePanel.configureColumn(5, 110, 130, 170, true, true);
        tablePanel.setPreferredSize(new Dimension(0, 210));
        tablePanel.setMinimumSize(new Dimension(0, 175));

        tableCard.add(tablePanel, BorderLayout.CENTER);

        lblFooterCount = new JLabel("Menampilkan 0 kunjungan.");
        lblFooterCount.setFont(DesignSystem.fontRegular(12f));
        lblFooterCount.setForeground(DesignSystem.TEXT_SECONDARY);

        bottomSection.add(lblRiwayat, BorderLayout.NORTH);
        bottomSection.add(tableCard, BorderLayout.CENTER);
        bottomSection.add(lblFooterCount, BorderLayout.SOUTH);

        JPanel mainStack = new JPanel(new BorderLayout(0, 18));
        mainStack.setOpaque(false);
        mainStack.add(topCard, BorderLayout.NORTH);
        mainStack.add(bottomSection, BorderLayout.CENTER);

        add(lblTitle, BorderLayout.NORTH);
        add(mainStack, BorderLayout.CENTER);

        setActionButtonsState(true, false);
    }

    private void addLabeledControl(JPanel parent, String label, javax.swing.JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(DesignSystem.fontRegular(12f));
        lbl.setForeground(DesignSystem.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comp.setAlignmentX(LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
        parent.add(comp);
        parent.add(Box.createVerticalStrut(12));
    }

    private void addLabeledTextArea(JPanel parent, String label, SharpTextArea area) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(DesignSystem.fontRegular(12f));
        lbl.setForeground(DesignSystem.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
        parent.add(area);
        parent.add(Box.createVerticalStrut(12));
    }

    public void setPasienOptions(List<Pasien> list) {
        Pasien selected = getSelectedPasien();
        DefaultComboBoxModel<Pasien> model = new DefaultComboBoxModel<>();
        for (Pasien p : list) {
            model.addElement(p);
        }
        cmbPasien.setModel(model);
        if (selected != null) {
            selectPasienById(selected.getId());
        } else if (model.getSize() > 0) {
            cmbPasien.setSelectedIndex(0);
        }
    }

    public void setDokterOptions(List<Dokter> list) {
        Dokter selected = getSelectedDokter();
        DefaultComboBoxModel<Dokter> model = new DefaultComboBoxModel<>();
        for (Dokter d : list) {
            model.addElement(d);
        }
        cmbDokter.setModel(model);
        if (selected != null) {
            selectDokterById(selected.getId());
        } else if (model.getSize() > 0) {
            cmbDokter.setSelectedIndex(0);
        }
    }

    public void setLayananOptions(List<Layanan> list) {
        Layanan selected = getSelectedLayanan();
        DefaultComboBoxModel<Layanan> model = new DefaultComboBoxModel<>();
        for (Layanan l : list) {
            model.addElement(l);
        }
        cmbLayanan.setModel(model);
        if (selected != null) {
            selectLayananById(selected.getId());
        } else if (model.getSize() > 0) {
            cmbLayanan.setSelectedIndex(0);
        }
    }

    public void selectPasienById(int id) {
        for (int i = 0; i < cmbPasien.getItemCount(); i++) {
            if (cmbPasien.getItemAt(i).getId() == id) {
                cmbPasien.setSelectedIndex(i);
                return;
            }
        }
    }

    public void selectDokterById(int id) {
        for (int i = 0; i < cmbDokter.getItemCount(); i++) {
            if (cmbDokter.getItemAt(i).getId() == id) {
                cmbDokter.setSelectedIndex(i);
                return;
            }
        }
    }

    public void selectLayananById(int id) {
        for (int i = 0; i < cmbLayanan.getItemCount(); i++) {
            if (cmbLayanan.getItemAt(i).getId() == id) {
                cmbLayanan.setSelectedIndex(i);
                return;
            }
        }
    }

    public Pasien getSelectedPasien() {
        return (Pasien) cmbPasien.getSelectedItem();
    }

    public Dokter getSelectedDokter() {
        return (Dokter) cmbDokter.getSelectedItem();
    }

    public Layanan getSelectedLayanan() {
        return (Layanan) cmbLayanan.getSelectedItem();
    }

    public LocalDate getSelectedTanggal() {
        return calendarPicker.getSelectedDate();
    }

    public void setSelectedTanggal(LocalDate tanggal) {
        calendarPicker.setSelectedDate(tanggal);
    }

    public String getKeluhanInput() {
        return txtKeluhan.getText();
    }

    public void setKeluhanInput(String text) {
        txtKeluhan.setText(text);
    }

    public String getDiagnosaInput() {
        return txtDiagnosa.getText();
    }

    public void setDiagnosaInput(String text) {
        txtDiagnosa.setText(text);
    }

    public void resetFormSelection() {
        if (cmbPasien.getItemCount() > 0) {
            cmbPasien.setSelectedIndex(0);
        }
        if (cmbDokter.getItemCount() > 0) {
            cmbDokter.setSelectedIndex(0);
        }
        if (cmbLayanan.getItemCount() > 0) {
            cmbLayanan.setSelectedIndex(0);
        }
        calendarPicker.setSelectedDate(LocalDate.now());
        txtKeluhan.setText("");
        txtDiagnosa.setText("");
    }

    public void setBiayaPreview(String hargaFormatted, String keterangan) {
        lblPerkiraanBiaya.setText(hargaFormatted);
        lblKeteranganBiaya.setText(keterangan);
    }

    public void setActionButtonsState(boolean canSave, boolean hasSelection) {
        btnSimpan.setEnabled(canSave);
        btnUbah.setEnabled(canSave && hasSelection);
        btnHapus.setEnabled(canSave && hasSelection);
        btnBersihkan.setEnabled(canSave);
    }

    public KunjunganTableModel getTableModel() {
        return tableModel;
    }

    public SwissTablePanel getTablePanel() {
        return tablePanel;
    }

    public void setFooterCount(int count) {
        lblFooterCount.setText("Menampilkan " + count + " kunjungan terbaru.");
    }

    public void addComboChangeListener(ActionListener listener) {
        cmbPasien.addActionListener(listener);
        cmbDokter.addActionListener(listener);
        cmbLayanan.addActionListener(listener);
    }

    public void addSimpanListener(ActionListener listener) {
        btnSimpan.addActionListener(listener);
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
