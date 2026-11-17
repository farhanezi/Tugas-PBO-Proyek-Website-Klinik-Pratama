package klinik;

import java.sql.SQLException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import klinik.controller.KunjunganController;
import klinik.controller.LoginController;
import klinik.controller.PasienController;
import klinik.dao.DatabaseConnection;
import klinik.dao.DokterDAO;
import klinik.dao.KunjunganDAO;
import klinik.dao.LayananDAO;
import klinik.dao.PasienDAO;
import klinik.dao.PetugasDAO;
import klinik.view.component.DialogHelper;
import klinik.view.screen.KlinikFrame;
import klinik.view.theme.DesignSystem;

/**
 * Titik masuk aplikasi desktop Klinik Pratama.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        DesignSystem.init();

        try {
            DatabaseConnection.initializeIfNeeded();
        } catch (SQLException ex) {
            SwingUtilities.invokeLater(() -> DialogHelper.showError(
                    null,
                    "Kesalahan database",
                    "Gagal menyiapkan database",
                    ex.getMessage()
            ));
        }

        SwingUtilities.invokeLater(() -> {
            PetugasDAO petugasDAO = new PetugasDAO();
            PasienDAO pasienDAO = new PasienDAO();
            DokterDAO dokterDAO = new DokterDAO();
            LayananDAO layananDAO = new LayananDAO();
            KunjunganDAO kunjunganDAO = new KunjunganDAO();

            KlinikFrame frame = new KlinikFrame();
            new LoginController(frame, petugasDAO);
            PasienController pasienController = new PasienController(frame, pasienDAO);
            KunjunganController kunjunganController = new KunjunganController(
                    frame,
                    kunjunganDAO,
                    pasienDAO,
                    dokterDAO,
                    layananDAO
            );

            frame.setTabCallbacks(
                    () -> refreshBeranda(frame, kunjunganDAO, pasienDAO),
                    () -> pasienController.loadPasien(null),
                    () -> kunjunganController.loadData(null)
            );

            frame.setVisible(true);
            frame.showLogin();
        });
    }

    private static void refreshBeranda(KlinikFrame frame, KunjunganDAO kunjunganDAO, PasienDAO pasienDAO) {
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                try {
                    return new int[]{kunjunganDAO.countHariIni(), pasienDAO.countAll()};
                } catch (SQLException ex) {
                    return new int[]{0, 0};
                }
            }

            @Override
            protected void done() {
                try {
                    int[] counts = get();
                    frame.getBerandaPanel().setRingkasan(counts[0], counts[1]);
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }
}
