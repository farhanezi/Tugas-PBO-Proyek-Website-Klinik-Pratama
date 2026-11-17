package klinik.model;

import klinik.exception.DataTidakValidException;
import klinik.util.FormatUtil;

/**
 * Layanan konsultasi dokter umum.
 */
public class KonsultasiUmum extends Layanan {
    public KonsultasiUmum(int id, String nama, double tarifDasar) throws DataTidakValidException {
        super(id, nama, tarifDasar);
    }

    @Override
    public String getJenis() {
        return "UMUM";
    }

    @Override
    public double hitungTarif() {
        return getTarifDasar();
    }

    @Override
    protected String getRincianNormal() {
        return "Tarif dasar konsultasi umum " + FormatUtil.formatRupiah(getTarifDasar()) + " (non-BPJS).";
    }
}
