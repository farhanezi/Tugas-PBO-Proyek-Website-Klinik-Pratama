package klinik.view.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import klinik.model.Kunjungan;
import klinik.util.FormatUtil;

/**
 * TableModel untuk JTable Riwayat Kunjungan berbasis List<Kunjungan>.
 */
public class KunjunganTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"", "Tanggal", "Pasien", "Dokter", "Layanan", "Total"};
    private List<Kunjungan> data = new ArrayList<>();

    public void setData(List<Kunjungan> list) {
        this.data = list != null ? new ArrayList<>(list) : new ArrayList<>();
        fireTableDataChanged();
    }

    public Kunjungan getKunjunganAt(int row) {
        if (row < 0 || row >= data.size()) {
            return null;
        }
        return data.get(row);
    }

    public int findRowById(int id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Kunjungan k = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> "";
            case 1 -> k.getTanggalFormatted();
            case 2 -> k.getPasien() != null ? k.getPasien().getNama() : "-";
            case 3 -> k.getDokter() != null ? k.getDokter().toString() : "-";
            case 4 -> k.getLayanan() != null ? k.getLayanan().getNama() : "-";
            case 5 -> FormatUtil.formatRupiah(k.getTotalBiaya());
            default -> "";
        };
    }
}
