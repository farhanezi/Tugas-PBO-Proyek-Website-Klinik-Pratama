package klinik.model;

import klinik.exception.DataTidakValidException;
import klinik.util.FormatUtil;

/**
 * Layanan konsultasi dokter spesialis dengan tambahan biaya spesialis.
 */
public class KonsultasiSpesialis extends Layanan {
    private double biayaSpesialis;

    public KonsultasiSpesialis(int id, String nama, double tarifDasar, double biayaSpesialis)
            throws DataTidakValidException {
        super(id, nama, tarifDasar);
        setBiayaSpesialis(biayaSpesialis);
    }

    public double getBiayaSpesialis() {
        return biayaSpesialis;
    }

    public void setBiayaSpesialis(double biayaSpesialis) throws DataTidakValidException {
        if (biayaSpesialis < 0) {
            throw new DataTidakValidException("biayaSpesialis", "Tarif tidak valid", "Biaya spesialis tidak boleh negatif.");
        }
        this.biayaSpesialis = biayaSpesialis;
    }

    @Override
    public String getJenis() {
        return "SPESIALIS";
    }

    @Override
    public double hitungTarif() {
        return getTarifDasar() + biayaSpesialis;
    }

    @Override
    protected String getRincianNormal() {
        return "Tarif dasar " + FormatUtil.formatRupiah(getTarifDasar())
                + " + biaya spesialis " + FormatUtil.formatRupiah(biayaSpesialis) + ".";
    }
}
