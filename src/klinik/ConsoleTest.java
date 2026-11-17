package klinik;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import klinik.dao.DatabaseConnection;
import klinik.dao.DokterDAO;
import klinik.dao.KunjunganDAO;
import klinik.dao.LayananDAO;
import klinik.dao.PasienDAO;
import klinik.dao.PetugasDAO;
import klinik.exception.DataTidakValidException;
import klinik.model.Dokter;
import klinik.model.Kunjungan;
import klinik.model.Layanan;
import klinik.model.Orang;
import klinik.model.Pasien;
import klinik.model.Petugas;

/**
 * Uji konsol lapisan model + DAO (syarat Progres 1: "dapat diuji lewat konsol").
 *
 * Jalankan dari folder root proyek (agar folder db/ terbaca), tanpa GUI:
 *   java -cp "bin;lib/*" klinik.ConsoleTest
 *
 * Pengujian memakai database sementara di folder temp, bukan data/klinik.db.
 * Exit code 0 bila semua lulus, 1 bila ada yang gagal.
 */
public final class ConsoleTest {
    private static int lulus = 0;
    private static int gagal = 0;

    private ConsoleTest() {
    }

    public static void main(String[] args) throws Exception {
        Path tempDb = Files.createTempFile("klinik-test-", ".db");
        Files.delete(tempDb); // initializeIfNeeded hanya membuat DB bila file belum ada
        System.setProperty("klinik.db", tempDb.toString());

        try {
            DatabaseConnection.initializeIfNeeded();
            System.out.println("Database uji: " + tempDb);

            ujiEnkapsulasiDanExceptionModel();
            ujiPolimorfismeTarif();
            ujiLogin();
            ujiCrudPasien();
            ujiCrudKunjungan();
            ujiIntegritasReferensi();
        } finally {
            hapusQuietly(tempDb);
            hapusQuietly(Path.of(tempDb + "-journal"));
        }

        System.out.printf("%nHasil: %d lulus, %d gagal%n", lulus, gagal);
        System.exit(gagal == 0 ? 0 : 1);
    }

    // ---------- Model: enkapsulasi, custom exception, pewarisan ----------

    private static void ujiEnkapsulasiDanExceptionModel() {
        judul("Model: validasi setter & DataTidakValidException");
        cekMelempar("nama kosong ditolak", () -> new Pasien().setNama("   "));
        cekMelempar("telepon berisi huruf ditolak", () -> new Pasien().setNoTelepon("08ab1234567"));
        cekMelempar("telepon < 8 digit ditolak", () -> new Pasien().setNoTelepon("0812"));
        cekMelempar("tanggal lahir null ditolak", () -> new Pasien().setTanggalLahir(null));
        cekMelempar("jenis kelamin selain L/P ditolak", () -> new Pasien().setJenisKelamin("X"));

        try {
            Pasien p = pasienContoh("Budi Santoso", false);
            Orang sebagaiOrang = p; // pewarisan + referensi induk
            cek("Pasien adalah Orang, peran = " + sebagaiOrang.getPeran(),
                    sebagaiOrang.getPeran() != null && !sebagaiOrang.getPeran().isBlank());
            cek("tampilkanInfo() terisi", !sebagaiOrang.tampilkanInfo().isBlank());
        } catch (DataTidakValidException e) {
            cek("pasien valid dapat dibuat", false);
        }
    }

    // ---------- Polimorfisme: hitungTarif() overriding + overloading ----------

    private static void ujiPolimorfismeTarif() throws SQLException {
        judul("Polimorfisme: hitungTarif() per jenis layanan");
        List<Layanan> semua = new LayananDAO().findAll();
        cek("3 layanan seed terbaca", semua.size() == 3);

        double[] harapanNormal = {50_000, 200_000, 175_000};
        for (int i = 0; i < semua.size(); i++) {
            Layanan l = semua.get(i); // referensi kelas induk
            System.out.printf("    %-22s (%s) -> %s%n", l.getNama(), l.getClass().getSimpleName(), l.tampilkanInfo());
            cek(l.getNama() + " tarif normal", l.hitungTarif() == harapanNormal[i]);
            cek(l.getNama() + " tarif BPJS (overload, diskon 50%)",
                    l.hitungTarif(50) == Math.round(harapanNormal[i] / 2));
        }
        cek("diskon > 100 dijepit ke 100% (gratis)", semua.get(0).hitungTarif(150) == 0);
    }

    // ---------- Login ----------

    private static void ujiLogin() throws SQLException {
        judul("Login: PetugasDAO.autentikasi");
        PetugasDAO dao = new PetugasDAO();
        Petugas ok = dao.autentikasi("admin", "admin123");
        cek("admin/admin123 berhasil", ok != null && "admin".equals(ok.getUsername()));
        cek("password salah ditolak", dao.autentikasi("admin", "salah") == null);
        cek("username tak dikenal ditolak", dao.autentikasi("hantu", "admin123") == null);
        cek("input kosong ditolak", dao.autentikasi("", "") == null && dao.autentikasi(null, null) == null);
        cek("SQL injection pada username tidak lolos",
                dao.autentikasi("admin' OR '1'='1", "apa saja") == null);
    }

    // ---------- CRUD Pasien ----------

    private static void ujiCrudPasien() throws Exception {
        judul("CRUD Pasien: PasienDAO");
        PasienDAO dao = new PasienDAO();
        int awal = dao.countAll();

        Pasien baru = dao.insert(pasienContoh("Siti Rahmawati", true));
        cek("insert memberi id > 0", baru.getId() > 0);
        cek("countAll bertambah 1", dao.countAll() == awal + 1);

        Pasien dibaca = cari(dao.findAll(), baru.getId());
        cek("findAll memuat data baru (BPJS tersimpan)", dibaca != null && dibaca.isPunyaBpjs());

        cek("searchByNama (huruf besar/kecil tak berpengaruh)",
                cari(dao.searchByNama("rahmawati"), baru.getId()) != null
                        && cari(dao.searchByNama("SITI RAHMAWATI"), baru.getId()) != null);
        cek("search via kode pasien " + baru.getKodePasien(),
                cari(dao.searchByNama(baru.getKodePasien()), baru.getId()) != null);
        cek("search tanpa hasil mengembalikan list kosong", dao.searchByNama("zzzz-tidak-ada").isEmpty());

        baru.setNama("Siti Rahmawati Putri");
        baru.setPunyaBpjs(false);
        dao.update(baru);
        Pasien diubah = cari(dao.findAll(), baru.getId());
        cek("update tersimpan", diubah != null && diubah.getNama().endsWith("Putri") && !diubah.isPunyaBpjs());

        Pasien hantu = pasienContoh("Tidak Ada", false);
        hantu.setId(999_999);
        cekMelemparSql("update id tak ada -> SQLException", () -> dao.update(hantu));

        dao.delete(baru.getId());
        cek("delete menghapus data", cari(dao.findAll(), baru.getId()) == null && dao.countAll() == awal);
    }

    // ---------- CRUD Kunjungan ----------

    private static void ujiCrudKunjungan() throws Exception {
        judul("CRUD Kunjungan: KunjunganDAO (total biaya polimorfik + diskon BPJS)");
        PasienDAO pasienDAO = new PasienDAO();
        KunjunganDAO dao = new KunjunganDAO();
        List<Dokter> dokter = new DokterDAO().findAll();
        List<Layanan> layanan = new LayananDAO().findAll();
        Layanan spesialis = layanan.get(1);

        Pasien bpjs = pasienDAO.insert(pasienContoh("Pasien BPJS Uji", true));
        Pasien umum = pasienDAO.insert(pasienContoh("Pasien Umum Uji", false));
        int awal = dao.findAll().size();

        Kunjungan k1 = new Kunjungan(0, bpjs, dokter.get(1), spesialis, LocalDate.now(), "Demam", "");
        Kunjungan k2 = new Kunjungan(0, umum, dokter.get(1), spesialis, LocalDate.now(), "Batuk", "");
        dao.insert(k1);
        dao.insert(k2);
        cek("insert memberi id > 0", k1.getId() > 0 && k2.getId() > 0);
        cek("total BPJS = 100.000 (50% dari 200.000)", cariK(dao.findAll(), k1.getId()).getTotalBiaya() == 100_000);
        cek("total non-BPJS = 200.000", cariK(dao.findAll(), k2.getId()).getTotalBiaya() == 200_000);
        cek("findAll bertambah 2 (join pasien/dokter/layanan)", dao.findAll().size() == awal + 2);
        cek("countHariIni >= 2", dao.countHariIni() >= 2);

        // Ganti layanan -> total dihitung ulang
        Kunjungan ubah = cariK(dao.findAll(), k2.getId());
        ubah.setLayanan(layanan.get(0));
        ubah.setDiagnosa("ISPA");
        dao.update(ubah);
        Kunjungan setelah = cariK(dao.findAll(), k2.getId());
        cek("update: total dihitung ulang (50.000) & diagnosa tersimpan",
                setelah.getTotalBiaya() == 50_000 && "ISPA".equals(setelah.getDiagnosa()));

        cekMelempar("kunjungan tanpa pasien ditolak",
                () -> new Kunjungan(0, null, dokter.get(0), spesialis, LocalDate.now(), "", ""));
        cekMelempar("total negatif ditolak", () -> k1.setTotalBiaya(-1));

        dao.delete(k1.getId());
        dao.delete(k2.getId());
        cek("delete menghapus kunjungan", dao.findAll().size() == awal);
        pasienDAO.delete(bpjs.getId());
        pasienDAO.delete(umum.getId());
    }

    // ---------- Foreign key ----------

    private static void ujiIntegritasReferensi() throws Exception {
        judul("Integritas: pasien yang punya kunjungan tidak boleh terhapus");
        PasienDAO pasienDAO = new PasienDAO();
        KunjunganDAO kunjunganDAO = new KunjunganDAO();
        Pasien p = pasienDAO.insert(pasienContoh("Pasien Terikat", false));
        Kunjungan k = new Kunjungan(0, p, new DokterDAO().findAll().get(0),
                new LayananDAO().findAll().get(0), LocalDate.now(), "", "");
        kunjunganDAO.insert(k);

        try {
            pasienDAO.delete(p.getId());
            cek("delete ditolak karena FK", false);
        } catch (SQLException e) {
            cek("delete ditolak karena FK (pesan informatif: \"" + e.getMessage() + "\")",
                    e.getMessage().contains("riwayat kunjungan"));
        }
        kunjunganDAO.delete(k.getId());
        pasienDAO.delete(p.getId());
        cek("setelah kunjungan dihapus, pasien dapat dihapus", cari(pasienDAO.findAll(), p.getId()) == null);
    }

    // ---------- Helper ----------

    private static Pasien pasienContoh(String nama, boolean bpjs) throws DataTidakValidException {
        return new Pasien(0, nama, "081234567890", "Jl. Contoh No. 1",
                LocalDate.of(1995, 5, 17), "L", bpjs);
    }

    private static Pasien cari(List<Pasien> list, int id) {
        return list.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private static Kunjungan cariK(List<Kunjungan> list, int id) {
        return list.stream().filter(k -> k.getId() == id).findFirst().orElseThrow();
    }

    private interface Aksi {
        void jalankan() throws Exception;
    }

    private static void cekMelempar(String nama, Aksi aksi) {
        try {
            aksi.jalankan();
            cek(nama, false);
        } catch (DataTidakValidException | IllegalArgumentException e) {
            cek(nama, true);
        } catch (Exception e) {
            cek(nama + " (exception tak terduga: " + e + ")", false);
        }
    }

    private static void cekMelemparSql(String nama, Aksi aksi) {
        try {
            aksi.jalankan();
            cek(nama, false);
        } catch (SQLException e) {
            cek(nama, true);
        } catch (Exception e) {
            cek(nama + " (exception tak terduga: " + e + ")", false);
        }
    }

    private static void judul(String teks) {
        System.out.println("\n== " + teks);
    }

    private static void cek(String nama, boolean kondisi) {
        if (kondisi) {
            lulus++;
        } else {
            gagal++;
        }
        System.out.println("  [" + (kondisi ? "LULUS" : "GAGAL") + "] " + nama);
    }

    private static void hapusQuietly(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }
}
