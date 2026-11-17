-- Skema database Klinik Pratama (SQLite)
-- Jalankan sekali saat file klinik.db belum ada.
-- Penting: SQLite baru menegakkan foreign key kalau setiap koneksi
-- menjalankan "PRAGMA foreign_keys = ON;" (lakukan di kelas koneksi).

PRAGMA foreign_keys = ON;

-- Tiga subkelas Orang disimpan di tabel masing-masing (table-per-class).
-- Kolom nama dan no_telepon berasal dari kelas induk Orang.

CREATE TABLE IF NOT EXISTS pasien (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,  -- ditampilkan sebagai P-0001
    nama           TEXT    NOT NULL CHECK (length(trim(nama)) > 0),
    no_telepon     TEXT    NOT NULL CHECK (no_telepon NOT GLOB '*[^0-9]*' AND length(no_telepon) BETWEEN 8 AND 15),
    alamat         TEXT,
    tanggal_lahir  TEXT    NOT NULL,                   -- format ISO yyyy-mm-dd, dibaca sebagai LocalDate
    jenis_kelamin  TEXT    NOT NULL CHECK (jenis_kelamin IN ('L', 'P')),
    punya_bpjs     INTEGER NOT NULL DEFAULT 0 CHECK (punya_bpjs IN (0, 1))
);

CREATE TABLE IF NOT EXISTS dokter (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nama          TEXT NOT NULL CHECK (length(trim(nama)) > 0),
    no_telepon    TEXT NOT NULL,
    spesialisasi  TEXT                                 -- NULL berarti dokter umum
);

CREATE TABLE IF NOT EXISTS petugas (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    nama           TEXT NOT NULL CHECK (length(trim(nama)) > 0),
    no_telepon     TEXT NOT NULL,
    username       TEXT NOT NULL UNIQUE,
    password_hash  TEXT NOT NULL,                      -- SHA-256 dari (salt + password), bukan password asli
    salt           TEXT NOT NULL
);

-- Hierarki Layanan disimpan di satu tabel (single-table).
-- Kolom jenis menentukan subkelas yang dibuat LayananDAO:
--   UMUM -> KonsultasiUmum, SPESIALIS -> KonsultasiSpesialis, TINDAKAN -> Tindakan
CREATE TABLE IF NOT EXISTS layanan (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    nama             TEXT NOT NULL UNIQUE,
    jenis            TEXT NOT NULL CHECK (jenis IN ('UMUM', 'SPESIALIS', 'TINDAKAN')),
    tarif_dasar      REAL NOT NULL CHECK (tarif_dasar >= 0),
    biaya_spesialis  REAL NOT NULL DEFAULT 0 CHECK (biaya_spesialis >= 0),  -- dipakai KonsultasiSpesialis
    biaya_bahan      REAL NOT NULL DEFAULT 0 CHECK (biaya_bahan >= 0)       -- dipakai Tindakan
);

CREATE TABLE IF NOT EXISTS kunjungan (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    pasien_id    INTEGER NOT NULL REFERENCES pasien(id)  ON DELETE RESTRICT,
    dokter_id    INTEGER NOT NULL REFERENCES dokter(id)  ON DELETE RESTRICT,
    layanan_id   INTEGER NOT NULL REFERENCES layanan(id) ON DELETE RESTRICT,
    tanggal      TEXT    NOT NULL,                     -- format ISO yyyy-mm-dd
    keluhan      TEXT,
    diagnosa     TEXT,
    total_biaya  REAL    NOT NULL CHECK (total_biaya >= 0)  -- hasil hitungTotal() saat disimpan
);

CREATE INDEX IF NOT EXISTS idx_pasien_nama       ON pasien(nama);
CREATE INDEX IF NOT EXISTS idx_kunjungan_tanggal ON kunjungan(tanggal);
CREATE INDEX IF NOT EXISTS idx_kunjungan_pasien  ON kunjungan(pasien_id);
