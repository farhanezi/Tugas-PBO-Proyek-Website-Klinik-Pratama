package klinik.model;

import klinik.exception.DataTidakValidException;

/**
 * Kelas induk abstrak untuk seluruh entitas manusia di klinik (Pasien, Dokter, Petugas).
 * Menerapkan enkapsulasi dengan validasi pada setter serta abstraksi peran.
 */
public abstract class Orang implements InfoRingkas {
    private int id;
    private String nama;
    private String noTelepon;

    protected Orang() {
    }

    protected Orang(int id, String nama, String noTelepon) throws DataTidakValidException {
        this.id = id;
        setNama(nama);
        setNoTelepon(noTelepon);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID tidak boleh negatif.");
        }
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws DataTidakValidException {
        String bersih = nama == null ? "" : nama.trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException(
                    "nama",
                    "Data belum lengkap",
                    "Nama lengkap tidak boleh kosong. Isi nama terlebih dahulu, lalu simpan lagi."
            );
        }
        this.nama = bersih;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) throws DataTidakValidException {
        String bersih = noTelepon == null ? "" : noTelepon.trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException(
                    "noTelepon",
                    "Data belum lengkap",
                    "Nomor telepon tidak boleh kosong. Masukkan 8 sampai 15 digit angka."
            );
        }
        if (!bersih.matches("\\d+")) {
            throw new DataTidakValidException(
                    "noTelepon",
                    "Nomor telepon tidak valid",
                    "Nomor telepon hanya boleh berisi angka."
            );
        }
        if (bersih.length() < 8 || bersih.length() > 15) {
            throw new DataTidakValidException(
                    "noTelepon",
                    "Nomor telepon tidak valid",
                    "Nomor telepon harus terdiri dari 8 sampai 15 digit angka."
            );
        }
        this.noTelepon = bersih;
    }

    /**
     * Mengembalikan peran spesifik turunan Orang.
     */
    public abstract String getPeran();
}
