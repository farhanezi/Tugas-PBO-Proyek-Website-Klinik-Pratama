package klinik.model;

import klinik.exception.DataTidakValidException;

/**
 * Entitas petugas pendaftaran yang dapat masuk ke aplikasi.
 */
public class Petugas extends Orang {
    private String username;
    private String passwordHash;
    private String salt;

    public Petugas() {
        super();
    }

    public Petugas(
            int id,
            String nama,
            String noTelepon,
            String username,
            String passwordHash,
            String salt
    ) throws DataTidakValidException {
        super(id, nama, noTelepon);
        setUsername(username);
        setPasswordHash(passwordHash);
        setSalt(salt);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws DataTidakValidException {
        String bersih = username == null ? "" : username.trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException(
                    "username",
                    "Data belum lengkap",
                    "Username petugas tidak boleh kosong."
            );
        }
        this.username = bersih;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash == null ? "" : passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? "" : salt;
    }

    @Override
    public String getPeran() {
        return "Petugas Pendaftaran";
    }

    @Override
    public String tampilkanInfo() {
        return getNama() + " (" + username + ")";
    }

    @Override
    public String toString() {
        return getNama();
    }
}
