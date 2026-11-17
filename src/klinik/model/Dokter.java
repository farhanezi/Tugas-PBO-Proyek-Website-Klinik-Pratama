package klinik.model;

import klinik.exception.DataTidakValidException;

/**
 * Entitas dokter praktik pada klinik pratama.
 */
public class Dokter extends Orang {
    private String spesialisasi;

    public Dokter() {
        super();
    }

    public Dokter(int id, String nama, String noTelepon, String spesialisasi) throws DataTidakValidException {
        super(id, nama, noTelepon);
        setSpesialisasi(spesialisasi);
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        if (spesialisasi == null || spesialisasi.trim().isEmpty()) {
            this.spesialisasi = null;
        } else {
            this.spesialisasi = spesialisasi.trim();
        }
    }

    public boolean isSpesialis() {
        return spesialisasi != null && !spesialisasi.isEmpty();
    }

    @Override
    public String getPeran() {
        return isSpesialis() ? "Dokter Spesialis " + spesialisasi : "Dokter Umum";
    }

    @Override
    public String tampilkanInfo() {
        return isSpesialis() ? getNama() + " (Sp. " + spesialisasi + ")" : getNama() + " (Umum)";
    }

    @Override
    public String toString() {
        return isSpesialis() ? getNama() + ", Sp." + spesialisasi : getNama();
    }
}
