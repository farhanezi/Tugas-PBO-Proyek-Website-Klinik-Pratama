package klinik.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import klinik.exception.DataTidakValidException;
import klinik.util.FormatUtil;

/**
 * Entitas kunjungan pasien yang menghubungkan Pasien, Dokter, dan Layanan.
 * Perhitungan total biaya dilakukan secara polimorfik dari objek Layanan dan status BPJS pasien.
 */
public class Kunjungan implements InfoRingkas {
    public static final double DISKON_BPJS_PERSEN = 50.0;
    public static final DateTimeFormatter FORMAT_TAMPIL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private int id;
    private Pasien pasien;
    private Dokter dokter;
    private Layanan layanan;
    private LocalDate tanggal;
    private String keluhan;
    private String diagnosa;
    private double totalBiaya;

    public Kunjungan() {
        this.tanggal = LocalDate.now();
        this.keluhan = "";
        this.diagnosa = "";
    }

    public Kunjungan(
            int id,
            Pasien pasien,
            Dokter dokter,
            Layanan layanan,
            LocalDate tanggal,
            String keluhan,
            String diagnosa
    ) throws DataTidakValidException {
        this.id = id;
        setPasien(pasien);
        setDokter(dokter);
        setLayanan(layanan);
        setTanggal(tanggal);
        setKeluhan(keluhan);
        setDiagnosa(diagnosa);
        this.totalBiaya = hitungTotal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID kunjungan tidak boleh negatif.");
        }
        this.id = id;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) throws DataTidakValidException {
        if (pasien == null) {
            throw new DataTidakValidException(
                    "pasien",
                    "Data belum lengkap",
                    "Pilih pasien terlebih dahulu sebelum menyimpan kunjungan."
            );
        }
        this.pasien = pasien;
    }

    public Dokter getDokter() {
        return dokter;
    }

    public void setDokter(Dokter dokter) throws DataTidakValidException {
        if (dokter == null) {
            throw new DataTidakValidException(
                    "dokter",
                    "Data belum lengkap",
                    "Pilih dokter pemeriksa terlebih dahulu."
            );
        }
        this.dokter = dokter;
    }

    public Layanan getLayanan() {
        return layanan;
    }

    public void setLayanan(Layanan layanan) throws DataTidakValidException {
        if (layanan == null) {
            throw new DataTidakValidException(
                    "layanan",
                    "Data belum lengkap",
                    "Pilih jenis layanan klinik terlebih dahulu."
            );
        }
        this.layanan = layanan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public String getTanggalFormatted() {
        return tanggal != null ? tanggal.format(FORMAT_TAMPIL) : "-";
    }

    public void setTanggal(LocalDate tanggal) throws DataTidakValidException {
        if (tanggal == null) {
            throw new DataTidakValidException(
                    "tanggal",
                    "Data belum lengkap",
                    "Pilih tanggal kunjungan pada kalender."
            );
        }
        this.tanggal = tanggal;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan == null ? "" : keluhan.trim();
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa == null ? "" : diagnosa.trim();
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(double totalBiaya) throws DataTidakValidException {
        if (totalBiaya < 0) {
            throw new DataTidakValidException("totalBiaya", "Biaya tidak valid", "Total biaya tidak boleh negatif.");
        }
        this.totalBiaya = totalBiaya;
    }

    /**
     * Menghitung total biaya kunjungan secara polimorfik melalui referensi Layanan.
     * Pasien BPJS memanggil versi overloaded hitungTarif(50).
     */
    public double hitungTotal() {
        if (layanan == null || pasien == null) {
            return 0;
        }
        if (pasien.isPunyaBpjs()) {
            this.totalBiaya = layanan.hitungTarif(DISKON_BPJS_PERSEN);
        } else {
            this.totalBiaya = layanan.hitungTarif();
        }
        return this.totalBiaya;
    }

    @Override
    public String tampilkanInfo() {
        return String.format(
                "%s · %s · %s (%s)",
                getTanggalFormatted(),
                pasien != null ? pasien.getNama() : "-",
                layanan != null ? layanan.getNama() : "-",
                FormatUtil.formatRupiah(totalBiaya)
        );
    }
}
