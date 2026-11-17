package klinik.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Helper pemformatan Rupiah, tanggal Indonesia, dan hashing kata sandi SHA-256.
 */
public final class FormatUtil {
    private static final Locale LOCALE_ID = Locale.forLanguageTag("id-ID");
    private static final DecimalFormat FORMAT_ANGKA;
    public static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final String[] BULAN_INDONESIA = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LOCALE_ID);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        FORMAT_ANGKA = new DecimalFormat("#,##0", symbols);
    }

    private FormatUtil() {
    }

    public static String formatRupiah(double nilai) {
        return "Rp " + FORMAT_ANGKA.format(Math.round(nilai));
    }

    public static String formatTanggal(LocalDate tanggal) {
        return tanggal != null ? tanggal.format(FORMAT_TANGGAL) : "-";
    }

    public static String formatBulanTahun(YearMonth ym) {
        if (ym == null) {
            return "";
        }
        return BULAN_INDONESIA[ym.getMonthValue() - 1] + " " + ym.getYear();
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritma SHA-256 tidak tersedia.", e);
        }
    }
}
