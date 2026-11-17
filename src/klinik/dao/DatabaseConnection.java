package klinik.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Pengelola koneksi SQLite menuju data/klinik.db dengan penegakan PRAGMA foreign_keys = ON.
 */
public final class DatabaseConnection {
    /**
     * Lokasi database. Default data/klinik.db; dapat diganti lewat -Dklinik.db=path
     * (dipakai ConsoleTest agar pengujian tidak menyentuh data asli).
     */
    private static final Path DB_PATH = Paths.get(System.getProperty("klinik.db", "data/klinik.db"));

    private DatabaseConnection() {
    }

    public static Path getDatabasePath() {
        return DB_PATH;
    }

    /**
     * Membuka koneksi ke data/klinik.db yang sudah ada.
     * Melempar SQLException jika file database tidak ditemukan agar status error tabel bekerja akurat.
     */
    public static Connection getConnection() throws SQLException {
        if (!Files.exists(DB_PATH)) {
            throw new SQLException("Aplikasi tidak menemukan klinik.db di folder data.");
        }
        String url = "jdbc:sqlite:" + DB_PATH.toAbsolutePath();
        Connection conn = DriverManager.getConnection(url);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Menginisialisasi data/klinik.db saat pertama kali aplikasi dijalankan bila file belum tersedia.
     */
    public static void initializeIfNeeded() throws SQLException {
        if (Files.exists(DB_PATH)) {
            return;
        }
        try {
            Path parent = DB_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String url = "jdbc:sqlite:" + DB_PATH.toAbsolutePath();
            try (Connection conn = DriverManager.getConnection(url);
                 Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
                executeSqlFile(st, Paths.get("db", "schema.sql"));
                executeSqlFile(st, Paths.get("db", "seed.sql"));
                Path samplePath = Paths.get("db", "seed_sample.sql");
                if (Files.exists(samplePath)) {
                    executeSqlFile(st, samplePath);
                }
            }
        } catch (IOException e) {
            throw new SQLException("Gagal menyiapkan file database awal: " + e.getMessage(), e);
        }
    }

    private static void executeSqlFile(Statement st, Path sqlFile) throws IOException, SQLException {
        if (!Files.exists(sqlFile)) {
            throw new IOException("Berkas SQL tidak ditemukan: " + sqlFile);
        }
        String content = Files.readString(sqlFile, StandardCharsets.UTF_8);
        StringBuilder cleaned = new StringBuilder();
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                cleaned.append(line).append('\n');
            }
        }
        for (String rawStmt : cleaned.toString().split(";")) {
            String stmt = rawStmt.trim();
            if (!stmt.isEmpty()) {
                st.execute(stmt);
            }
        }
    }
}
