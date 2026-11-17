package klinik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import klinik.exception.DataTidakValidException;
import klinik.model.Petugas;
import klinik.util.FormatUtil;

/**
 * DAO untuk autentikasi dan pembacaan akun petugas dari tabel petugas.
 */
public class PetugasDAO {

    public Petugas findByUsername(String username) throws SQLException {
        String sql = "SELECT id, nama, no_telepon, username, password_hash, salt FROM petugas WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new Petugas(
                                rs.getInt("id"),
                                rs.getString("nama"),
                                rs.getString("no_telepon"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("salt")
                        );
                    } catch (DataTidakValidException e) {
                        throw new SQLException("Data petugas di database tidak valid: " + e.getMessage(), e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Memverifikasi username dan kata sandi menggunakan SHA-256(salt + password).
     */
    public Petugas autentikasi(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        Petugas petugas = findByUsername(username.trim());
        if (petugas == null) {
            return null;
        }
        String candidateHash = FormatUtil.sha256Hex(petugas.getSalt() + password);
        if (candidateHash.equalsIgnoreCase(petugas.getPasswordHash())) {
            return petugas;
        }
        return null;
    }
}
