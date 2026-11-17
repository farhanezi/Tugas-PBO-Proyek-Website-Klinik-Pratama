package klinik.model;

import klinik.exception.DataTidakValidException;
import klinik.util.FormatUtil;

/**
 * Layanan tindakan medis dengan tambahan biaya bahan habis pakai.
 */
public class Tindakan extends Layanan {
    private double biayaBahan;

    public Tindakan(int id, String nama, double tarifDasar, double biayaBahan)
            throws DataTidakValidException {
        super(id, nama, tarifDasar);
        setBiayaBahan(biayaBahan);
    }

    public double getBiayaBahan() {
        return biayaBahan;
    }

    public void setBiayaBahan(double biayaBahan) throws DataTidakValidException {
        if (biayaBahan < 0) {
            throw new DataTidakValidException("biayaBahan", "Tarif tidak valid", "Biaya bahan tindakan tidak boleh negatif.");
        }
        this.biayaBahan = biayaBahan;
    }

    @Override
    public String getJenis() {
        return "TINDAKAN";
    }

    @Override
    public double hitungTarif() {
        return getTarifDasar() + biayaBahan;
    }

    @Override
    protected String getRincianNormal() {
        return "Tarif dasar " + FormatUtil.formatRupiah(getTarifDasar())
                + " + biaya bahan " + FormatUtil.formatRupiah(biayaBahan) + ".";
    }
}
