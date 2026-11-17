package klinik.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import klinik.exception.DataTidakValidException;

/**
 * Representasi entitas pasien klinik pratama.
 */
public class Pasien extends Orang {
    public static final DateTimeFormatter FORMAT_TAMPIL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String alamat;
    private LocalDate tanggalLahir;
    private String jenisKelamin;
    private boolean punyaBpjs;

    public Pasien() {
        super();
        this.alamat = "";
        this.jenisKelamin = "L";
        this.tanggalLahir = LocalDate.of(1995, 1, 1);
    }

    public Pasien(
            int id,
            String nama,
            String noTelepon,
            String alamat,
            LocalDate tanggalLahir,
            String jenisKelamin,
            boolean punyaBpjs
    ) throws DataTidakValidException {
        super(id, nama, noTelepon);
        setAlamat(alamat);
        setTanggalLahir(tanggalLahir);
        setJenisKelamin(jenisKelamin);
        setPunyaBpjs(punyaBpjs);
    }

    @Override
    public void setNama(String nama) throws DataTidakValidException {
        String bersih = nama == null ? "" : nama.trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException(
                    "nama",
                    "Data belum lengkap",
                    "Nama lengkap tidak boleh kosong. Isi nama pasien, lalu simpan lagi."
            );
        }
        super.setNama(bersih);
    }

    public String getKodePasien() {
        return getId() > 0 ? String.format("P-%04d", getId()) : "P-BARU";
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat == null ? "" : alamat.trim();
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public String getTanggalLahirFormatted() {
        return tanggalLahir != null ? tanggalLahir.format(FORMAT_TAMPIL) : "-";
    }

    public void setTanggalLahir(LocalDate tanggalLahir) throws DataTidakValidException {
        if (tanggalLahir == null) {
            throw new DataTidakValidException(
                    "tanggalLahir",
                    "Data belum lengkap",
                    "Tanggal lahir harus diisi dengan format dd-mm-yyyy."
            );
        }
        if (tanggalLahir.isAfter(LocalDate.now())) {
            throw new DataTidakValidException(
                    "tanggalLahir",
                    "Tanggal lahir tidak valid",
                    "Tanggal lahir tidak boleh melebihi tanggal hari ini."
            );
        }
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) throws DataTidakValidException {
        if (jenisKelamin == null || (!"L".equals(jenisKelamin) && !"P".equals(jenisKelamin))) {
            throw new DataTidakValidException(
                    "jenisKelamin",
                    "Data belum lengkap",
                    "Pilih jenis kelamin pasien (Laki-laki atau Perempuan)."
            );
        }
        this.jenisKelamin = jenisKelamin;
    }

    public boolean isPunyaBpjs() {
        return punyaBpjs;
    }

    public void setPunyaBpjs(boolean punyaBpjs) {
        this.punyaBpjs = punyaBpjs;
    }

    @Override
    public String getPeran() {
        return "Pasien";
    }

    @Override
    public String tampilkanInfo() {
        return String.format("%s %s (%s)", getKodePasien(), getNama(), punyaBpjs ? "BPJS" : "Umum");
    }

    @Override
    public String toString() {
        return getNama() + " (" + getKodePasien() + ")";
    }
}
