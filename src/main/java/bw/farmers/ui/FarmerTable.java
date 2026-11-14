package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.Status;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

/** The farmer table: avatars, two-line cells, status pills, row hover and an empty state. */
final class FarmerTable extends JTable {

    private static final long serialVersionUID = 1L;
    private static final Color HOVER = new Color(0xFBF8F2);

    private final FarmerTableModel farmers;
    private int hoverRow = -1;
    private String emptyTitle = "No farmers match";
    private String emptyHint = "Try another filter or search.";

    FarmerTable(FarmerTableModel model) {
        super(model);
        this.farmers = model;
        setFillsViewportHeight(true);
        setShowVerticalLines(false);
        setRowHeight(UIScale.scale(58));
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setFont(Ui.semibold(12.5f));

        setDefaultRenderer(String.class, new TextCell());
        setDefaultRenderer(Double.class, new NumberCell());
        setDefaultRenderer(Status.class, new StatusCell());
        getColumnModel().getColumn(1).setCellRenderer(new NameCell());
        getColumnModel().getColumn(4).setCellRenderer(new DistrictCell());
        // the district cell shows the sub-district under it
        removeColumn(getColumnModel().getColumn(5));

        int[] widths = {124, 280, 92, 104, 150, 100, 108, 124};
        for (int i = 0; i < widths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(UIScale.scale(widths[i]));
        }
        getColumnModel().getColumn(1).setHeaderValue("Farmer");
        getColumnModel().getColumn(4).setHeaderValue("District");
        getColumnModel().getColumn(6).setHeaderValue("Farmed");
        // Line headers up with their cells: text on the left, numbers on the right.
        TableCellRenderer header = getTableHeader().getDefaultRenderer();
        getTableHeader().setDefaultRenderer((t, v, sel, focus, r, col) -> {
            Component c = header.getTableCellRendererComponent(t, v, sel, focus, r, col);
            if (c instanceof JLabel l) {
                boolean number = t.getColumnClass(col) == Double.class;
                l.setHorizontalAlignment(number ? SwingConstants.RIGHT : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createEmptyBorder(0, UIScale.scale(16), 0,
                        UIScale.scale(number ? 20 : 12)));
            }
            return c;
        });

        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setHover(rowAtPoint(e.getPoint()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHover(-1);
            }
        };
        addMouseMotionListener(hover);
        addMouseListener(hover);
    }

    void setEmptyMessage(String title, String hint) {
        emptyTitle = title;
        emptyHint = hint;
    }

    private void setHover(int row) {
        if (row != hoverRow) {
            hoverRow = row;
            repaint();
        }
    }

    Farmer farmerAt(int viewRow) {
        return farmers.at(convertRowIndexToModel(viewRow));
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            c.setBackground(row == hoverRow ? HOVER : getBackground());
        }
        return c;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (getRowCount() > 0) {
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        Ui.antialias(g);
        int cy = Math.max(UIScale.scale(90), getVisibleRect().height / 2 - UIScale.scale(30));
        int cx = getVisibleRect().x + getVisibleRect().width / 2;
        Ui.icon("search", 28, Ui.MUTED).paintIcon(this, g, cx - UIScale.scale(14), cy - UIScale.scale(44));
        g.setFont(Ui.semibold(15));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Ui.INK);
        g.drawString(emptyTitle, cx - fm.stringWidth(emptyTitle) / 2, cy);
        g.setFont(Ui.font(Font.PLAIN, 13.5f));
        fm = g.getFontMetrics();
        g.setColor(Ui.MUTED);
        g.drawString(emptyHint, cx - fm.stringWidth(emptyHint) / 2, cy + UIScale.scale(22));
        g.dispose();
    }

    private static void pad(JComponent c) {
        c.setBorder(BorderFactory.createEmptyBorder(0, UIScale.scale(16), 0, UIScale.scale(12)));
    }

    private static final class TextCell extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setFont(Ui.font(Font.PLAIN, 14));
            setForeground(Ui.INK);
            pad(this);
            return this;
        }
    }

    private static final class NumberCell extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        NumberCell() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            setText(value instanceof Double d ? String.format(Locale.ROOT, "%,.0f", d) : String.valueOf(value));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setFont(Ui.font(Font.PLAIN, 14));
            setForeground(Ui.INK);
            setBorder(BorderFactory.createEmptyBorder(0, UIScale.scale(8), 0, UIScale.scale(20)));
            return this;
        }
    }

    private static final class NameCell extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            Farmer f = ((FarmerTable) t).farmerAt(r);
            setText("<html><b>" + Ui.escape(f.fullName()) + "</b><br><span style='color:#5E6A60'>"
                    + Ui.escape(f.email()) + "</span></html>");
            setIcon(Ui.avatar(f.fullName(), 34));
            setIconTextGap(UIScale.scale(12));
            setFont(Ui.font(Font.PLAIN, 13.5f));
            setForeground(Ui.INK);
            pad(this);
            return this;
        }
    }

    private static final class DistrictCell extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            Farmer f = ((FarmerTable) t).farmerAt(r);
            setText("<html>" + Ui.escape(f.district()) + "<br><span style='color:#5E6A60'>"
                    + Ui.escape(f.subDistrict()) + "</span></html>");
            setFont(Ui.font(Font.PLAIN, 13.5f));
            setForeground(Ui.INK);
            pad(this);
            return this;
        }
    }

    private static final class StatusCell extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final Ui.Pill pill = Ui.badge(Status.PENDING);

        StatusCell() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            add(pill);
            pad(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            pill.setStatus((Status) v);
            setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            int top = Math.max(0, (t.getRowHeight() - pill.getPreferredSize().height) / 2);
            setBorder(BorderFactory.createEmptyBorder(top, UIScale.scale(14), 0, 0));
            return this;
        }
    }

    /** A label used in toolbars to show how many rows are selected. */
    static JLabel countLabel() {
        return Ui.text(" ", Ui.semibold(13.5f), Ui.INK);
    }
}
