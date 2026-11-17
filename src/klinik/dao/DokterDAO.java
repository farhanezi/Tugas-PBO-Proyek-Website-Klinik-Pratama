package klinik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import klinik.exception.DataTidakValidException;
import klinik.model.Dokter;

/**
 * DAO untuk membaca daftar dokter praktik dari tabel dokter.
 */
public class DokterDAO {

    public List<Dokter> findAll() throws SQLException {
        String sql = "SELECT id, nama, no_telepon, spesialisasi FROM dokter ORDER BY id ASC";
        List<Dokter> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Dokter mapRow(ResultSet rs) throws SQLException {
        try {
            return new Dokter(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("no_telepon"),
                    rs.getString("spesialisasi")
            );
        } catch (DataTidakValidException e) {
            throw new SQLException("Data dokter di database tidak valid: " + e.getMessage(), e);
        }
    }
}
