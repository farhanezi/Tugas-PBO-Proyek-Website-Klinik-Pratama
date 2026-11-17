package klinik.exception;

/**
 * Custom exception untuk validasi input model dan form sebelum disimpan ke database.
 * Menyimpan nama field terkait dan judul ringkas agar UI dapat menandai input bermasalah
 * sekaligus menampilkan dialog pesan kesalahan yang informatif.
 */
public class DataTidakValidException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String fieldKey;
    private final String judul;

    public DataTidakValidException(String message) {
        this("umum", "Input tidak valid", message);
    }

    public DataTidakValidException(String fieldKey, String judul, String message) {
        super(message);
        this.fieldKey = fieldKey != null ? fieldKey : "umum";
        this.judul = judul != null ? judul : "Input tidak valid";
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getJudul() {
        return judul;
    }
}
