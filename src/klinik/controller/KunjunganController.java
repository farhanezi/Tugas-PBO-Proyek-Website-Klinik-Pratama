package klinik.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.SwingWorker;
import klinik.dao.DokterDAO;
import klinik.dao.KunjunganDAO;
import klinik.dao.LayananDAO;
import klinik.dao.PasienDAO;
import klinik.exception.DataTidakValidException;
import klinik.model.Dokter;
import klinik.model.Kunjungan;
import klinik.model.Layanan;
import klinik.model.Pasien;
import klinik.util.FormatUtil;
import klinik.view.component.DialogHelper;
import klinik.view.component.SwissTablePanel;
import klinik.view.screen.KlinikFrame;
import klinik.view.screen.KunjunganPanel;

/**
 * Controller CRUD Kunjungan beserta pembaruan perkiraan biaya polimorfik
 * dari jenis Layanan dan status BPJS Pasien.
 */
public class KunjunganController {
    private final KlinikFrame frame;
    private final KunjunganPanel view;
    private final KunjunganDAO kunjunganDAO;
    private final PasienDAO pasienDAO;
    private final DokterDAO dokterDAO;
    private final LayananDAO layananDAO;

    private Kunjungan selectedKunjungan;
    private int loadTicket = 0;

    public KunjunganController(
            KlinikFrame frame,
            KunjunganDAO kunjunganDAO,
            PasienDAO pasienDAO,
            DokterDAO dokterDAO,
            LayananDAO layananDAO
    ) {
        this.frame = frame;
        this.view = frame.getKunjunganPanel();
        this.kunjunganDAO = kunjunganDAO;
        this.pasienDAO = pasienDAO;
        this.dokterDAO = dokterDAO;
        this.layananDAO = layananDAO;
        bindEvents();
    }

    private void bindEvents() {
        view.addComboChangeListener(e -> perbaruiPerkiraanBiaya());
        view.addSimpanListener(e -> simpanKunjungan());
        view.addUbahListener(e -> ubahKunjungan());
        view.addHapusListener(e -> hapusKunjungan());
        view.addBersihkanListener(e -> bersihkanForm());

        view.getTablePanel().getRetryButton().addActionListener(e -> loadData(null));

        view.getTablePanel().getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTablePanel().getTable().getSelectedRow();
                if (row >= 0) {
                    Kunjungan picked = view.getTableModel().getKunjunganAt(row);
                    if (picked != null) {
                        selectedKunjungan = picked;
                        populateFormFromSelection(picked);
                        view.setActionButtonsState(true, true);
                    }
                }
            }
        });

        view.getTablePanel().getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    bersihkanForm();
                }
            }
        });
    }

    public void loadData(Integer selectIdAfterLoad) {
        final int ticket = ++loadTicket;
        view.getTablePanel().showState(SwissTablePanel.State.LOADING);
        view.setActionButtonsState(false, false);

        new SwingWorker<LoadBundle, Void>() {
            private SQLException error;

            @Override
            protected LoadBundle doInBackground() {
                try {
                    List<Pasien> pasienList = pasienDAO.findAll();
                    List<Dokter> dokterList = dokterDAO.findAll();
                    List<Layanan> layananList = layananDAO.findAll();
                    List<Kunjungan> kunjunganList = kunjunganDAO.findAll();
                    return new LoadBundle(pasienList, dokterList, layananList, kunjunganList);
                } catch (SQLException ex) {
                    this.error = ex;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (ticket != loadTicket) {
                    return;
                }
                if (error != null) {
                    view.getTablePanel().setErrorDetail(error.getMessage());
                    view.getTablePanel().showState(SwissTablePanel.State.ERROR);
                    view.setFooterCount(0);
                    view.setActionButtonsState(false, false);
                    return;
                }
                try {
                    LoadBundle b = get();
                    view.setPasienOptions(b.pasienList);
                    view.setDokterOptions(b.dokterList);
                    view.setLayananOptions(b.layananList);
                    view.getTableModel().setData(b.kunjunganList);
                    view.setFooterCount(b.kunjunganList.size());
                    perbaruiPerkiraanBiaya();

                    if (b.kunjunganList.isEmpty()) {
                        selectedKunjungan = null;
                        view.getTablePanel().showState(SwissTablePanel.State.EMPTY);
                        view.setActionButtonsState(true, false);
                    } else {
                        view.getTablePanel().showState(SwissTablePanel.State.TABLE);
                        Integer targetId = selectIdAfterLoad != null
                                ? selectIdAfterLoad
                                : (selectedKunjungan != null ? selectedKunjungan.getId() : null);
                        if (targetId != null) {
                            int idx = view.getTableModel().findRowById(targetId);
                            if (idx >= 0) {
                                view.getTablePanel().getTable().setRowSelectionInterval(idx, idx);
                                selectedKunjungan = view.getTableModel().getKunjunganAt(idx);
                                view.setActionButtonsState(true, true);
                                return;
                            }
                        }
                        selectedKunjungan = null;
                        view.getTablePanel().getTable().clearSelection();
                        view.setActionButtonsState(true, false);
                    }
                } catch (Exception ex) {
                    view.getTablePanel().setErrorDetail(ex.getMessage());
                    view.getTablePanel().showState(SwissTablePanel.State.ERROR);
                    view.setActionButtonsState(true, false);
                }
            }
        }.execute();
    }

    public void perbaruiPerkiraanBiaya() {
        Pasien pasien = view.getSelectedPasien();
        Layanan layanan = view.getSelectedLayanan();
        if (pasien == null || layanan == null) {
            view.setBiayaPreview("Rp 0", "Tambahkan atau pilih pasien dan layanan untuk menghitung biaya.");
            return;
        }
        Kunjungan draft = new Kunjungan();
        try {
            draft.setPasien(pasien);
            draft.setLayanan(layanan);
        } catch (DataTidakValidException ignored) {
            return;
        }
        double total = draft.hitungTotal();
        view.setBiayaPreview(
                FormatUtil.formatRupiah(total),
                layanan.getKeteranganTarif(pasien.isPunyaBpjs())
        );
    }

    public void simpanKunjungan() {
        Kunjungan baru;
        try {
            baru = buildKunjunganFromForm(0);
        } catch (DataTidakValidException ex) {
            DialogHelper.showError(frame, "Input tidak valid", ex.getJudul(), ex.getMessage());
            return;
        }

        view.setActionButtonsState(false, selectedKunjungan != null);
        new SwingWorker<Kunjungan, Void>() {
            private SQLException error;

            @Override
            protected Kunjungan doInBackground() {
                try {
                    return kunjunganDAO.insert(baru);
                } catch (SQLException ex) {
                    this.error = ex;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (error != null) {
                    view.setActionButtonsState(true, selectedKunjungan != null);
                    DialogHelper.showError(
                            frame,
                            "Kesalahan database",
                            "Gagal menyimpan kunjungan",
                            error.getMessage()
                    );
                    return;
                }
                try {
                    Kunjungan saved = get();
                    selectedKunjungan = null;
                    view.resetFormSelection();
                    perbaruiPerkiraanBiaya();
                    loadData(saved != null ? saved.getId() : null);
                } catch (Exception ex) {
                    view.setActionButtonsState(true, false);
                    DialogHelper.showError(frame, "Kesalahan aplikasi", "Gagal menyimpan kunjungan", ex.getMessage());
                }
            }
        }.execute();
    }

    public void ubahKunjungan() {
        if (selectedKunjungan == null) {
            DialogHelper.showError(
                    frame,
                    "Pilih kunjungan",
                    "Belum ada kunjungan terpilih",
                    "Klik salah satu baris pada Riwayat kunjungan sebelum mengubah data."
            );
            return;
        }

        Kunjungan updated;
        try {
            updated = buildKunjunganFromForm(selectedKunjungan.getId());
        } catch (DataTidakValidException ex) {
            DialogHelper.showError(frame, "Input tidak valid", ex.getJudul(), ex.getMessage());
            return;
        }

        view.setActionButtonsState(false, true);
        new SwingWorker<Void, Void>() {
            private SQLException error;

            @Override
            protected Void doInBackground() {
                try {
                    kunjunganDAO.update(updated);
                } catch (SQLException ex) {
                    this.error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    view.setActionButtonsState(true, true);
                    DialogHelper.showError(
                            frame,
                            "Kesalahan database",
                            "Gagal mengubah kunjungan",
                            error.getMessage()
                    );
                    return;
                }
                selectedKunjungan = updated;
                loadData(updated.getId());
            }
        }.execute();
    }

    public void hapusKunjungan() {
        if (selectedKunjungan == null) {
            DialogHelper.showError(
                    frame,
                    "Pilih kunjungan",
                    "Belum ada kunjungan terpilih",
                    "Klik salah satu baris pada Riwayat kunjungan sebelum menghapus."
            );
            return;
        }

        Kunjungan target = selectedKunjungan;
        String namaPasien = target.getPasien() != null ? target.getPasien().getNama() : "pasien";
        boolean confirmed = DialogHelper.showDeleteConfirm(
                frame,
                "Hapus data kunjungan?",
                "Riwayat kunjungan " + namaPasien + " pada " + target.getTanggalFormatted()
                        + " hilang permanen setelah Anda klik Hapus."
        );
        if (!confirmed) {
            return;
        }

        view.setActionButtonsState(false, true);
        new SwingWorker<Void, Void>() {
            private SQLException error;

            @Override
            protected Void doInBackground() {
                try {
                    kunjunganDAO.delete(target.getId());
                } catch (SQLException ex) {
                    this.error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    view.setActionButtonsState(true, true);
                    DialogHelper.showError(
                            frame,
                            "Kesalahan database",
                            "Gagal menghapus kunjungan",
                            error.getMessage()
                    );
                    return;
                }
                selectedKunjungan = null;
                view.resetFormSelection();
                perbaruiPerkiraanBiaya();
                loadData(null);
            }
        }.execute();
    }

    public void bersihkanForm() {
        selectedKunjungan = null;
        view.getTablePanel().getTable().clearSelection();
        view.resetFormSelection();
        perbaruiPerkiraanBiaya();
        view.setActionButtonsState(true, false);
    }

    private void populateFormFromSelection(Kunjungan k) {
        if (k.getPasien() != null) {
            view.selectPasienById(k.getPasien().getId());
        }
        if (k.getDokter() != null) {
            view.selectDokterById(k.getDokter().getId());
        }
        if (k.getLayanan() != null) {
            view.selectLayananById(k.getLayanan().getId());
        }
        view.setSelectedTanggal(k.getTanggal());
        view.setKeluhanInput(k.getKeluhan());
        view.setDiagnosaInput(k.getDiagnosa());
        perbaruiPerkiraanBiaya();
    }

    private Kunjungan buildKunjunganFromForm(int id) throws DataTidakValidException {
        Kunjungan k = new Kunjungan();
        k.setId(id);
        k.setPasien(view.getSelectedPasien());
        k.setDokter(view.getSelectedDokter());
        k.setLayanan(view.getSelectedLayanan());
        k.setTanggal(view.getSelectedTanggal());
        k.setKeluhan(view.getKeluhanInput());
        k.setDiagnosa(view.getDiagnosaInput());
        k.hitungTotal();
        return k;
    }

    private record LoadBundle(
            List<Pasien> pasienList,
            List<Dokter> dokterList,
            List<Layanan> layananList,
            List<Kunjungan> kunjunganList
    ) {
    }
}
