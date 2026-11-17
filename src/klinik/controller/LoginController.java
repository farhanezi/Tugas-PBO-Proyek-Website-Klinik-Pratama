package klinik.controller;

import java.sql.SQLException;
import javax.swing.SwingWorker;
import klinik.dao.PetugasDAO;
import klinik.model.Petugas;
import klinik.view.component.DialogHelper;
import klinik.view.screen.KlinikFrame;
import klinik.view.screen.LoginPanel;

/**
 * Controller autentikasi petugas dan transisi masuk/keluar aplikasi.
 */
public class LoginController {
    private final KlinikFrame frame;
    private final LoginPanel view;
    private final PetugasDAO petugasDAO;

    public LoginController(KlinikFrame frame, PetugasDAO petugasDAO) {
        this.frame = frame;
        this.view = frame.getLoginPanel();
        this.petugasDAO = petugasDAO;
        bindEvents();
    }

    private void bindEvents() {
        view.addLoginActionListener(e -> login());
        frame.getNavHeader().addKeluarListener(e -> logout());
    }

    public void login() {
        String username = view.getUsername() != null ? view.getUsername().trim() : "";
        String password = view.getPassword() != null ? view.getPassword() : "";

        view.setInputError(false, false);

        if (username.isEmpty() && password.isEmpty()) {
            view.setInputError(true, true);
            DialogHelper.showError(
                    frame,
                    "Input tidak valid",
                    "Data login belum diisi",
                    "Masukkan username dan kata sandi petugas terlebih dahulu."
            );
            view.focusUsername();
            return;
        }
        if (username.isEmpty()) {
            view.setInputError(true, false);
            DialogHelper.showError(
                    frame,
                    "Input tidak valid",
                    "Username belum diisi",
                    "Username tidak boleh kosong. Isi username akun petugas, lalu coba masuk lagi."
            );
            view.focusUsername();
            return;
        }
        if (password.isEmpty()) {
            view.setInputError(false, true);
            DialogHelper.showError(
                    frame,
                    "Input tidak valid",
                    "Kata sandi belum diisi",
                    "Kata sandi tidak boleh kosong. Masukkan kata sandi akun petugas."
            );
            view.clearPasswordAndFocus();
            return;
        }

        view.setLoading(true);
        new SwingWorker<Petugas, Void>() {
            private SQLException error;

            @Override
            protected Petugas doInBackground() {
                try {
                    return petugasDAO.autentikasi(username, password);
                } catch (SQLException ex) {
                    this.error = ex;
                    return null;
                }
            }

            @Override
            protected void done() {
                view.setLoading(false);
                if (error != null) {
                    DialogHelper.showError(
                            frame,
                            "Kesalahan database",
                            "Gagal membaca akun petugas",
                            error.getMessage()
                    );
                    view.clearPasswordAndFocus();
                    return;
                }
                try {
                    Petugas petugas = get();
                    if (petugas == null) {
                        view.setInputError(true, true);
                        DialogHelper.showError(
                                frame,
                                "Login gagal",
                                "Username atau kata sandi salah",
                                "Akun tidak ditemukan atau kata sandi tidak cocok. Periksa kembali lalu coba lagi."
                        );
                        view.clearPasswordAndFocus();
                    } else {
                        view.setInputError(false, false);
                        frame.showWorkspace(petugas);
                    }
                } catch (Exception ex) {
                    DialogHelper.showError(
                            frame,
                            "Kesalahan aplikasi",
                            "Gagal memproses login",
                            ex.getMessage()
                    );
                    view.clearPasswordAndFocus();
                }
            }
        }.execute();
    }

    public void logout() {
        if (DialogHelper.showLogoutConfirm(frame)) {
            frame.showLogin();
        }
    }
}
