-- Data sampel awal untuk demo interaktif Klinik Pratama.
-- Dijalankan opsional setelah db/schema.sql dan db/seed.sql.

INSERT INTO pasien (nama, no_telepon, alamat, tanggal_lahir, jenis_kelamin, punya_bpjs) VALUES
    ('Budi Santoso',    '081234567890', 'Jl. Merdeka No. 14, Bandung',     '1988-03-12', 'L', 1),
    ('Siti Aminah',     '081345678901', 'Jl. Asia Afrika No. 42, Bandung', '1994-07-25', 'P', 1),
    ('Andi Wijaya',     '082198765432', 'Jl. Dago No. 88, Bandung',        '1991-11-04', 'L', 0),
    ('Dewi Lestari',    '085611223344', 'Jl. Buah Batu No. 19, Bandung',   '1997-01-19', 'P', 1),
    ('Rudi Hartono',    '081987654321', 'Jl. Soekarno Hatta No. 105',      '1983-09-30', 'L', 0),
    ('Nadia Putri',     '081299887766', 'Jl. Setiabudi No. 27, Bandung',   '2001-05-08', 'P', 1);

INSERT INTO kunjungan (pasien_id, dokter_id, layanan_id, tanggal, keluhan, diagnosa, total_biaya) VALUES
    (1, 2, 2, date('now', 'localtime'), 'Demam tinggi dan batuk kering sejak 2 hari.', 'Infeksi saluran pernapasan akut ringan.', 100000),
    (3, 1, 1, date('now', 'localtime'), 'Pusing dan pegal di bagian tengkuk.', 'Ketegangan otot dan kelelahan ringan.', 50000),
    (2, 1, 3, date('now', 'localtime', '-1 day'), 'Luka gores pada lengan kanan butuh perawatan.', 'Pembersihan luka dan balut steril.', 87500);
