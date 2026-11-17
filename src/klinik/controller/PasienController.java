package klinik.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import klinik.dao.PasienDAO;
import klinik.exception.DataTidakValidException;
import klinik.model.Pasien;
import klinik.view.component.DialogHelper;
import klinik.view.component.SwissTablePanel;
import klinik.view.screen.KlinikFrame;
import klinik.view.screen.PasienPanel;

/**
 * Controller CRUD Pasien dengan validasi model DataTidakValidException,
 * pencarian langsung via DocumentListener, dan pemuatan latar belakang via SwingWorker.
 */
public class PasienController {
    private final KlinikFrame frame;
    private final PasienPanel view;
    private final PasienDAO pasienDAO;
    private Pasien selectedPasien;
    private int loadTicket = 0;

    public PasienController(KlinikFrame frame, PasienDAO pasienDAO) {
        this.frame = frame;
        this.view = frame.getPasienPanel();
        this.pasienDAO = pasienDAO;
        bindEvents();
    }

    private void bindEvents() {
        view.addSimpanListener(e -> simpanPasien());
        view.addUbahListener(e -> ubahPasien());
        view.addHapusListener(e -> hapusPasien());
        view.addBersihkanListener(e -> bersihkanForm());

        view.getTablePanel().getRetryButton().addActionListener(e -> loadPasien(null));
        view.getTablePanel().getClearSearchButton().addActionListener(e -> {
            view.clearSearch();
            loadPasien(null);
        });

        view.addSearchDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadPasien(null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadPasien(null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadPasien(null);
            }
        });

        view.addTeleponDocumentListener(new DocumentListener() {
            private void validateLivePhone() {
                String raw = view.getTeleponInput();
                String trimmed = raw != null ? raw.trim() : "";
                if (!trimmed.isEmpty() && !trimmed.matches("\\d+")) {
                    view.setTeleponInlineError("Nomor telepon hanya boleh berisi angka.");
                } else {
                    view.setTeleponInlineError(null);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validateLivePhone();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateLivePhone();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateLivePhone();
            }
        });

        view.getTablePanel().getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTablePanel().getTable().getSelectedRow();
                if (row >= 0) {
                    Pasien picked = view.getTableModel().getPasienAt(row);
                    if (picked != null) {
                        selectedPasien = picked;
                        view.populateForm(picked);
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

    public void loadPasien(Integer selectIdAfterLoad) {
        final int ticket = ++loadTicket;
        final String keyword = view.getSearchKeyword() != null ? view.getSearchKeyword().trim() : "";

        view.getTablePanel().showState(SwissTablePanel.State.LOADING);
        view.setActionButtonsState(false, false);

        new SwingWorker<LoadResult, Void>() {
            private SQLException error;

            @Override
            protected LoadResult doInBackground() {
                try {
                    int total = pasienDAO.countAll();
                    List<Pasien> list = keyword.isEmpty() ? pasienDAO.findAll() : pasienDAO.searchByNama(keyword);
                    return new LoadResult(list, total);
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
                    view.setCountSummary(0, 0);
                    view.setActionButtonsState(false, false);
                    return;
                }
                try {
                    LoadResult res = get();
                    view.getTableModel().setData(res.list);
                    view.setCountSummary(res.list.size(), res.total);

                    if (res.total == 0) {
                        selectedPasien = null;
                        view.getTablePanel().showState(SwissTablePanel.State.EMPTY);
                        view.setActionButtonsState(true, false);
                    } else if (res.list.isEmpty()) {
                        selectedPasien = null;
                        view.getTablePanel().showState(SwissTablePanel.State.NO_RESULTS);
                        view.setActionButtonsState(true, false);
                    } else {
                        view.getTablePanel().showState(SwissTablePanel.State.TABLE);
                        Integer targetId = selectIdAfterLoad != null
                                ? selectIdAfterLoad
                                : (selectedPasien != null ? selectedPasien.getId() : null);
                        if (targetId != null) {
                            int idx = view.getTableModel().findRowById(targetId);
                            if (idx >= 0) {
                                view.getTablePanel().getTable().setRowSelectionInterval(idx, idx);
                                selectedPasien = view.getTableModel().getPasienAt(idx);
                                view.setActionButtonsState(true, true);
                                return;
                            }
                        }
                        selectedPasien = null;
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

    public void simpanPasien() {
        view.clearFieldErrors();
        Pasien pasienBaru;
        try {
            pasienBaru = buildPasienFromForm(0);
        } catch (DataTidakValidException ex) {
            handleValidationError(ex);
            return;
        }

        view.setActionButtonsState(false, selectedPasien != null);
        new SwingWorker<Pasien, Void>() {
            private SQLException error;

            @Override
            protected Pasien doInBackground() {
                try {
                    return pasienDAO.insert(pasienBaru);
                } catch (SQLException ex) {
                    this.error = ex;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (error != null) {
                    view.setActionButtonsState(true, selectedPasien != null);
                    DialogHelper.showError(
                            frame,
                            "Kesalahan database",
                            "Gagal menyimpan data pasien",
                            error.getMessage()
                    );
                    return;
                }
                try {
                    Pasien saved = get();
                    selectedPasien = null;
                    view.populateForm(null);
                    loadPasien(saved != null ? saved.getId() : null);
                } catch (Exception ex) {
                    view.setActionButtonsState(true, false);
                    DialogHelper.showError(frame, "Kesalahan aplikasi", "Gagal menyimpan pasien", ex.getMessage());
                }
            }
        }.execute();
    }

    public void ubahPasien() {
        if (selectedPasien == null) {
            DialogHelper.showError(
                    frame,
                    "Pilih pasien",
                    "Belum ada pasien terpilih",
                    "Klik salah satu baris pada Daftar pasien sebelum mengubah data."
            );
            return;
        }

        view.clearFieldErrors();
        Pasien updated;
        try {
            updated = buildPasienFromForm(selectedPasien.getId());
        } catch (DataTidakValidException ex) {
            handleValidationError(ex);
            return;
        }

        view.setActionButtonsState(false, true);
        new SwingWorker<Void, Void>() {
            private SQLException error;

            @Override
            protected Void doInBackground() {
                try {
                    pasienDAO.update(updated);
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
                            "Gagal mengubah data pasien",
                            error.getMessage()
                    );
                    return;
                }
                selectedPasien = updated;
                loadPasien(updated.getId());
            }
        }.execute();
    }

    public void hapusPasien() {
        if (selectedPasien == null) {
            DialogHelper.showError(
                    frame,
                    "Pilih pasien",
                    "Belum ada pasien terpilih",
                    "Klik salah satu baris pada Daftar pasien sebelum menghapus."
            );
            return;
        }

        Pasien target = selectedPasien;
        boolean confirmed = DialogHelper.showDeleteConfirm(
                frame,
                "Hapus data pasien?",
                "Data " + target.getNama() + " (" + target.getKodePasien() + ") hilang permanen setelah Anda klik Hapus."
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
                    pasienDAO.delete(target.getId());
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
                            "Tidak dapat menghapus",
                            "Hapus pasien gagal",
                            error.getMessage()
                    );
                    return;
                }
                selectedPasien = null;
                view.populateForm(null);
                loadPasien(null);
            }
        }.execute();
    }

    public void bersihkanForm() {
        selectedPasien = null;
        view.getTablePanel().getTable().clearSelection();
        view.populateForm(null);
        view.setActionButtonsState(true, false);
    }

    private Pasien buildPasienFromForm(int id) throws DataTidakValidException {
        Pasien p = new Pasien();
        p.setId(id);
        p.setNama(view.getNamaInput());
        p.setNoTelepon(view.getTeleponInput());
        p.setAlamat(view.getAlamatInput());

        LocalDate tglLahir;
        try {
            tglLahir = view.getTanggalLahirInput();
        } catch (ParseException ex) {
            throw new DataTidakValidException(
                    "tanggalLahir",
                    "Tanggal lahir tidak valid",
                    "Masukkan tanggal lahir dengan format dd-mm-yyyy yang benar."
            );
        }
        p.setTanggalLahir(tglLahir);
        p.setJenisKelamin(view.getJenisKelaminInput());
        p.setPunyaBpjs(view.isBpjsInput());
        return p;
    }

    private void handleValidationError(DataTidakValidException ex) {
        view.showFieldError(ex.getFieldKey(), ex.getMessage());
        DialogHelper.showError(frame, "Input tidak valid", ex.getJudul(), ex.getMessage());
    }

    private record LoadResult(List<Pasien> list, int total) {
    }
}
