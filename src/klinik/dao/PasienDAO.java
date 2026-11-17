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
import klinik.model.Pasien;

/**
 * DAO untuk operasi CRUD entitas Pasien pada tabel pasien.
 */
public class PasienDAO {

    public List<Pasien> findAll() throws SQLException {
        String sql = "SELECT id, nama, no_telepon, alamat, tanggal_lahir, jenis_kelamin, punya_bpjs "
                + "FROM pasien ORDER BY id ASC";
        List<Pasien> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Pasien> searchByNama(String keyword) throws SQLException {
        String clean = keyword == null ? "" : keyword.trim().toLowerCase();
        if (clean.isEmpty()) {
            return findAll();
        }
        String sql = "SELECT id, nama, no_telepon, alamat, tanggal_lahir, jenis_kelamin, punya_bpjs "
                + "FROM pasien "
                + "WHERE lower(nama) LIKE ? OR printf('p-%04d', id) LIKE ? OR no_telepon LIKE ? "
                + "ORDER BY id ASC";
        String pattern = "%" + clean + "%";
        List<Pasien> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Pasien insert(Pasien pasien) throws SQLException {
        String sql = "INSERT INTO pasien (nama, no_telepon, alamat, tanggal_lahir, jenis_kelamin, punya_bpjs) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pasien.getNama());
            ps.setString(2, pasien.getNoTelepon());
            ps.setString(3, pasien.getAlamat());
            ps.setString(4, pasien.getTanggalLahir().toString());
            ps.setString(5, pasien.getJenisKelamin());
            ps.setInt(6, pasien.isPunyaBpjs() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pasien.setId(keys.getInt(1));
                }
            }
        }
        return pasien;
    }

    public void update(Pasien pasien) throws SQLException {
        String sql = "UPDATE pasien SET nama = ?, no_telepon = ?, alamat = ?, tanggal_lahir = ?, "
                + "jenis_kelamin = ?, punya_bpjs = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pasien.getNama());
            ps.setString(2, pasien.getNoTelepon());
            ps.setString(3, pasien.getAlamat());
            ps.setString(4, pasien.getTanggalLahir().toString());
            ps.setString(5, pasien.getJenisKelamin());
            ps.setInt(6, pasien.isPunyaBpjs() ? 1 : 0);
            ps.setInt(7, pasien.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Data pasien tidak ditemukan di database.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pasien WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            if (msg.contains("foreign key") || msg.contains("constraint")) {
                throw new SQLException(
                        "Pasien masih tercatat pada riwayat kunjungan. Hapus kunjungan terkait terlebih dahulu sebelum menghapus pasien.",
                        ex
                );
            }
            throw ex;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pasien";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Pasien mapRow(ResultSet rs) throws SQLException {
        try {
            return new Pasien(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("no_telepon"),
                    rs.getString("alamat"),
                    LocalDate.parse(rs.getString("tanggal_lahir")),
                    rs.getString("jenis_kelamin"),
                    rs.getInt("punya_bpjs") == 1
            );
        } catch (DataTidakValidException e) {
            throw new SQLException("Data pasien di database tidak valid: " + e.getMessage(), e);
        }
    }
}
