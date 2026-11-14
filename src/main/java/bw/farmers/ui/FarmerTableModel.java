package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.Status;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Farmers as table rows. */
final class FarmerTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = {"ID number", "Name", "Gender", "Contact", "District", "Sub-district",
            "Farm (ha)", "Farming (ha)", "Status"};

    private transient List<Farmer> rows = new ArrayList<>();

    void setRows(List<Farmer> farmers) {
        rows = new ArrayList<>(farmers);
        fireTableDataChanged();
    }

    Farmer at(int modelRow) {
        return rows.get(modelRow);
    }

    List<Farmer> rows() {
        return List.copyOf(rows);
    }

    @Override
    public int getRowCount() {
        return rows.size();
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
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 6, 7 -> Double.class;
            case 8 -> Status.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Farmer f = rows.get(row);
        return switch (column) {
            case 0 -> f.idNumber();
            case 1 -> f.fullName();
            case 2 -> f.gender().label();
            case 3 -> f.contact();
            case 4 -> f.district();
            case 5 -> f.subDistrict();
            case 6 -> f.farmSize();
            case 7 -> f.farmingArea();
            case 8 -> f.status();
            default -> throw new IllegalArgumentException("column " + column);
        };
    }
}
