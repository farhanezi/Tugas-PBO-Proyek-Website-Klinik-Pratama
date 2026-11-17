package klinik.model;

import klinik.exception.DataTidakValidException;
import klinik.util.FormatUtil;

/**
 * Kelas induk abstrak untuk jenis layanan klinik.
 * Menerapkan abstraksi dan polimorfisme melalui method abstrak hitungTarif()
 * serta overloading hitungTarif(double diskonPersen).
 */
public abstract class Layanan implements InfoRingkas {
    private int id;
    private String nama;
    private double tarifDasar;

    protected Layanan() {
    }

    protected Layanan(int id, String nama, double tarifDasar) throws DataTidakValidException {
        this.id = id;
        setNama(nama);
        setTarifDasar(tarifDasar);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID layanan tidak boleh negatif.");
        }
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws DataTidakValidException {
        String bersih = nama == null ? "" : nama.trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException("nama", "Data belum lengkap", "Nama layanan tidak boleh kosong.");
        }
        this.nama = bersih;
    }

    public double getTarifDasar() {
        return tarifDasar;
    }

    public void setTarifDasar(double tarifDasar) throws DataTidakValidException {
        if (tarifDasar < 0) {
            throw new DataTidakValidException("tarifDasar", "Tarif tidak valid", "Tarif dasar layanan tidak boleh negatif.");
        }
        this.tarifDasar = tarifDasar;
    }

    public abstract String getJenis();

    /**
     * Menghitung tarif normal layanan sesuai komponen biaya masing-masing subkelas.
     */
    public abstract double hitungTarif();

    /**
     * Overloading hitungTarif dengan potongan persentase diskon (misal 50 untuk BPJS).
     */
    public double hitungTarif(double diskonPersen) {
        double clamped = Math.max(0.0, Math.min(100.0, diskonPersen));
        return Math.round(hitungTarif() * (1.0 - clamped / 100.0));
    }

    public String getKeteranganTarif(boolean punyaBpjs) {
        double tarifNormal = hitungTarif();
        if (punyaBpjs) {
            return "Pasien BPJS mendapat potongan 50% dari tarif " + FormatUtil.formatRupiah(tarifNormal) + ".";
        }
        return getRincianNormal();
    }

    protected String getRincianNormal() {
        return "Tarif standar layanan " + FormatUtil.formatRupiah(hitungTarif()) + " (non-BPJS).";
    }

    @Override
    public String tampilkanInfo() {
        return nama + " — " + FormatUtil.formatRupiah(hitungTarif());
    }

    @Override
    public String toString() {
        return nama;
    }
}
