package klinik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import klinik.exception.DataTidakValidException;
import klinik.model.KonsultasiSpesialis;
import klinik.model.KonsultasiUmum;
import klinik.model.Layanan;
import klinik.model.Tindakan;

/**
 * DAO untuk membaca hierarki Layanan dari tabel single-table layanan.
 * Memetakan kolom jenis menjadi subkelas KonsultasiUmum, KonsultasiSpesialis, atau Tindakan.
 */
public class LayananDAO {

    public List<Layanan> findAll() throws SQLException {
        String sql = "SELECT id, nama, jenis, tarif_dasar, biaya_spesialis, biaya_bahan FROM layanan ORDER BY id ASC";
        List<Layanan> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    static Layanan mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nama = rs.getString("nama");
        String jenis = rs.getString("jenis");
        double tarifDasar = rs.getDouble("tarif_dasar");
        double biayaSpesialis = rs.getDouble("biaya_spesialis");
        double biayaBahan = rs.getDouble("biaya_bahan");

        try {
            return switch (jenis) {
                case "UMUM" -> new KonsultasiUmum(id, nama, tarifDasar);
                case "SPESIALIS" -> new KonsultasiSpesialis(id, nama, tarifDasar, biayaSpesialis);
                case "TINDAKAN" -> new Tindakan(id, nama, tarifDasar, biayaBahan);
                default -> throw new SQLException("Jenis layanan tidak dikenali: " + jenis);
            };
        } catch (DataTidakValidException e) {
            throw new SQLException("Data layanan di database tidak valid: " + e.getMessage(), e);
        }
    }
}
