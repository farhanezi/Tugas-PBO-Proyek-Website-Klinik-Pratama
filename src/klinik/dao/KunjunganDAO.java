package klinik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import klinik.exception.DataTidakValidException;
import klinik.model.Dokter;
import klinik.model.KonsultasiSpesialis;
import klinik.model.KonsultasiUmum;
import klinik.model.Kunjungan;
import klinik.model.Layanan;
import klinik.model.Pasien;
import klinik.model.Tindakan;

/**
 * DAO untuk operasi CRUD kunjungan beserta join ke pasien, dokter, dan layanan.
 */
public class KunjunganDAO {

    public List<Kunjungan> findAll() throws SQLException {
        String sql = """
                SELECT
                    k.id AS k_id,
                    k.tanggal AS k_tanggal,
                    k.keluhan AS k_keluhan,
                    k.diagnosa AS k_diagnosa,
                    k.total_biaya AS k_total_biaya,
                    p.id AS p_id,
                    p.nama AS p_nama,
                    p.no_telepon AS p_telepon,
                    p.alamat AS p_alamat,
                    p.tanggal_lahir AS p_tgl_lahir,
                    p.jenis_kelamin AS p_jk,
                    p.punya_bpjs AS p_bpjs,
                    d.id AS d_id,
                    d.nama AS d_nama,
                    d.no_telepon AS d_telepon,
                    d.spesialisasi AS d_spesialisasi,
                    l.id AS l_id,
                    l.nama AS l_nama,
                    l.jenis AS l_jenis,
                    l.tarif_dasar AS l_tarif_dasar,
                    l.biaya_spesialis AS l_biaya_spesialis,
                    l.biaya_bahan AS l_biaya_bahan
                FROM kunjungan k
                JOIN pasien p ON k.pasien_id = p.id
                JOIN dokter d ON k.dokter_id = d.id
                JOIN layanan l ON k.layanan_id = l.id
                ORDER BY k.tanggal DESC, k.id DESC
                """;
        List<Kunjungan> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Kunjungan insert(Kunjungan kunjungan) throws SQLException {
        String sql = "INSERT INTO kunjungan (pasien_id, dokter_id, layanan_id, tanggal, keluhan, diagnosa, total_biaya) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        double total = kunjungan.hitungTotal();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, kunjungan.getPasien().getId());
            ps.setInt(2, kunjungan.getDokter().getId());
            ps.setInt(3, kunjungan.getLayanan().getId());
            ps.setString(4, kunjungan.getTanggal().toString());
            ps.setString(5, kunjungan.getKeluhan());
            ps.setString(6, kunjungan.getDiagnosa());
            ps.setDouble(7, total);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    kunjungan.setId(keys.getInt(1));
                }
            }
        }
        return kunjungan;
    }

    public void update(Kunjungan kunjungan) throws SQLException {
        String sql = "UPDATE kunjungan SET pasien_id = ?, dokter_id = ?, layanan_id = ?, "
                + "tanggal = ?, keluhan = ?, diagnosa = ?, total_biaya = ? WHERE id = ?";
        double total = kunjungan.hitungTotal();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kunjungan.getPasien().getId());
            ps.setInt(2, kunjungan.getDokter().getId());
            ps.setInt(3, kunjungan.getLayanan().getId());
            ps.setString(4, kunjungan.getTanggal().toString());
            ps.setString(5, kunjungan.getKeluhan());
            ps.setString(6, kunjungan.getDiagnosa());
            ps.setDouble(7, total);
            ps.setInt(8, kunjungan.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Data kunjungan tidak ditemukan di database.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM kunjungan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM kunjungan WHERE tanggal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Kunjungan mapRow(ResultSet rs) throws SQLException {
        try {
            Pasien pasien = new Pasien(
                    rs.getInt("p_id"),
                    rs.getString("p_nama"),
                    rs.getString("p_telepon"),
                    rs.getString("p_alamat"),
                    LocalDate.parse(rs.getString("p_tgl_lahir")),
                    rs.getString("p_jk"),
                    rs.getInt("p_bpjs") == 1
            );
            Dokter dokter = new Dokter(
                    rs.getInt("d_id"),
                    rs.getString("d_nama"),
                    rs.getString("d_telepon"),
                    rs.getString("d_spesialisasi")
            );
            Layanan layanan = switch (rs.getString("l_jenis")) {
                case "UMUM" -> new KonsultasiUmum(
                        rs.getInt("l_id"),
                        rs.getString("l_nama"),
                        rs.getDouble("l_tarif_dasar")
                );
                case "SPESIALIS" -> new KonsultasiSpesialis(
                        rs.getInt("l_id"),
                        rs.getString("l_nama"),
                        rs.getDouble("l_tarif_dasar"),
                        rs.getDouble("l_biaya_spesialis")
                );
                case "TINDAKAN" -> new Tindakan(
                        rs.getInt("l_id"),
                        rs.getString("l_nama"),
                        rs.getDouble("l_tarif_dasar"),
                        rs.getDouble("l_biaya_bahan")
                );
                default -> throw new SQLException("Jenis layanan tidak dikenali.");
            };
            Kunjungan k = new Kunjungan(
                    rs.getInt("k_id"),
                    pasien,
                    dokter,
                    layanan,
                    LocalDate.parse(rs.getString("k_tanggal")),
                    rs.getString("k_keluhan"),
                    rs.getString("k_diagnosa")
            );
            k.setTotalBiaya(rs.getDouble("k_total_biaya"));
            return k;
        } catch (DataTidakValidException e) {
            throw new SQLException("Data kunjungan di database tidak valid: " + e.getMessage(), e);
        }
    }
}
