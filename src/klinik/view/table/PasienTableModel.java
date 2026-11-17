package klinik.view.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import klinik.model.Pasien;

/**
 * TableModel untuk JTable Daftar Pasien berbasis List<Pasien>.
 */
public class PasienTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"", "ID", "Nama", "Telepon", "Tgl lahir", "JK", "BPJS"};
    private List<Pasien> data = new ArrayList<>();

    public void setData(List<Pasien> list) {
        this.data = list != null ? new ArrayList<>(list) : new ArrayList<>();
        fireTableDataChanged();
    }

    public Pasien getPasienAt(int row) {
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
        Pasien p = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> "";
            case 1 -> p.getKodePasien();
            case 2 -> p.getNama();
            case 3 -> p.getNoTelepon();
            case 4 -> p.getTanggalLahirFormatted();
            case 5 -> p.getJenisKelamin();
            case 6 -> p.isPunyaBpjs() ? "Ya" : "Tidak";
            default -> "";
        };
    }
}
