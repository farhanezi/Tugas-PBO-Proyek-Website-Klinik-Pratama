-- Data awal Klinik Pratama. Jalankan setelah schema.sql.
-- Semua nama dan tarif di bawah adalah DATA CONTOH. Ganti dengan data asli
-- klinik (atau data yang disepakati tim) sebelum demo.

-- Akun petugas demo: username "admin", password "admin123".
-- Ganti password ini sebelum aplikasi dipakai siapa pun selain tim.
INSERT INTO petugas (nama, no_telepon, username, password_hash, salt) VALUES
    ('Petugas Contoh', '081200000000', 'admin',
     'dc0df645424ce704a7772e2ec0cc814f7bc87e3ca4799b88c14080d06124ffb5',
     'c615ab51d1fa1d9ff5c5add0f4c820cd');

INSERT INTO dokter (nama, no_telepon, spesialisasi) VALUES
    ('dr. Contoh Umum',    '081200000001', NULL),
    ('dr. Contoh Anak',    '081200000002', 'Anak');

-- Tarif contoh, sama dengan yang tampil di wireframe:
--   Konsultasi Umum      = 50.000
--   Konsultasi Spesialis = 150.000 + 50.000 biaya spesialis = 200.000
--   Tindakan             = 150.000 + 25.000 biaya bahan     = 175.000
INSERT INTO layanan (nama, jenis, tarif_dasar, biaya_spesialis, biaya_bahan) VALUES
    ('Konsultasi Umum',      'UMUM',      50000,  0,     0),
    ('Konsultasi Spesialis', 'SPESIALIS', 150000, 50000, 0),
    ('Tindakan',             'TINDAKAN',  150000, 0,     25000);
