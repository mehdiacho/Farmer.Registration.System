package bw.farmers.ui;

import bw.farmers.model.Districts;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;
import bw.farmers.model.User;
import bw.farmers.report.ReportWriter;
import bw.farmers.service.FarmerService;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** The admin area: review registrations, and print reports. */
public final class AdminPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String ANY = "Any";

    private final transient FarmerService service;

    // Registrations tab
    private final FarmerTableModel registrations = new FarmerTableModel();
    private final JTable regTable = new JTable(registrations);
    private final TableRowSorter<FarmerTableModel> regSorter = new TableRowSorter<>(registrations);
    private final JComboBox<Object> statusFilter = new JComboBox<>(new Object[]{"All", Status.PENDING,
            Status.APPROVED, Status.DECLINED});
    private final JTextField search = new JTextField(18);
    private final JLabel regCount = Ui.subtitle(" ");

    // Reports tab
    private final FarmerTableModel report = new FarmerTableModel();
    private final JComboBox<String> repDistrict = new JComboBox<>();
    private final JComboBox<String> repSubDistrict = new JComboBox<>(new String[]{ANY});
    private final JComboBox<Object> repGender = new JComboBox<>(new Object[]{ANY, Gender.MALE, Gender.FEMALE});
    private final JComboBox<Object> repStatus = new JComboBox<>(new Object[]{ANY, Status.APPROVED, Status.PENDING,
            Status.DECLINED});
    private final JLabel repSummary = Ui.subtitle(" ");

    private final JTabbedPane tabs = new JTabbedPane();

    public AdminPanel(FarmerService service, User admin, Navigator nav) {
        super(new BorderLayout());
        this.service = service;

        JPanel top = new JPanel(new BorderLayout());
        Ui.padding(top, 16, 20, 8, 20);
        JPanel titles = new JPanel(new BorderLayout());
        titles.add(Ui.title("Farmer register"), BorderLayout.NORTH);
        titles.add(Ui.subtitle("Signed in as " + admin.displayName() + " (admin)"), BorderLayout.SOUTH);
        top.add(titles, BorderLayout.WEST);
        JButton signOut = Ui.link("Sign out");
        signOut.addActionListener(e -> nav.showLogin());
        top.add(signOut, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        tabs.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_TYPE, FlatClientProperties.TABBED_PANE_TAB_TYPE_CARD);
        tabs.addTab("Registrations", registrationsTab());
        tabs.addTab("Reports", reportsTab());
        tabs.addChangeListener(e -> refreshAll());
        add(tabs);

        refreshAll();
    }

    // ---- registrations ----------------------------------------------------------------------

    private JPanel registrationsTab() {
        styleTable(regTable);
        regTable.setRowSorter(regSorter);
        regTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search name, ID, district...");
        search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearch();
            }
        });
        statusFilter.addActionListener(e -> refreshRegistrations());

        JButton approve = Ui.primary("Approve");
        JButton decline = new JButton("Decline");
        JButton edit = new JButton("Edit...");
        JButton delete = new JButton("Delete");
        delete.putClientProperty(FlatClientProperties.STYLE, "foreground: #C62828");

        approve.addActionListener(e -> decide(Status.APPROVED));
        decline.addActionListener(e -> decide(Status.DECLINED));
        edit.addActionListener(e -> editSelected());
        delete.addActionListener(e -> deleteSelected());
        regTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && regTable.getSelectedRow() >= 0) {
                    editSelected();
                }
            }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("Show"));
        filters.add(statusFilter);
        filters.add(search);
        filters.add(regCount);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(delete);
        actions.add(edit);
        actions.add(decline);
        actions.add(approve);

        JPanel bar = new JPanel(new BorderLayout());
        Ui.padding(bar, 12, 12, 12, 12);
        bar.add(filters, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);

        JPanel tab = new JPanel(new BorderLayout());
        tab.add(bar, BorderLayout.NORTH);
        tab.add(new JScrollPane(regTable));
        return tab;
    }

    private void refreshRegistrations() {
        Object s = statusFilter.getSelectedItem();
        registrations.setRows(service.list(new ReportFilter(null, null, null, s instanceof Status st ? st : null)));
        applySearch();
    }

    private void applySearch() {
        String q = search.getText().strip();
        regSorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
        int shown = regTable.getRowCount();
        regCount.setText(shown + (shown == 1 ? " farmer" : " farmers"));
    }

    private List<Farmer> selected() {
        return java.util.Arrays.stream(regTable.getSelectedRows())
                .map(regTable::convertRowIndexToModel)
                .mapToObj(registrations::at)
                .toList();
    }

    private void decide(Status status) {
        List<Farmer> chosen = selected();
        if (chosen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select one or more farmers first.");
            return;
        }
        for (Farmer f : chosen) {
            service.setStatus(f.idNumber(), status);
        }
        refreshRegistrations();
    }

    private void editSelected() {
        List<Farmer> chosen = selected();
        if (chosen.size() != 1) {
            JOptionPane.showMessageDialog(this, "Select one farmer to edit.");
            return;
        }
        EditFarmerDialog d = new EditFarmerDialog(this, service, chosen.get(0));
        d.setVisible(true);
        if (d.saved()) {
            refreshRegistrations();
        }
    }

    private void deleteSelected() {
        List<Farmer> chosen = selected();
        if (chosen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select one or more farmers first.");
            return;
        }
        String who = chosen.size() == 1 ? chosen.get(0).fullName() : chosen.size() + " farmers";
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + who + " and their login? This cannot be undone.", "Delete farmer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.OK_OPTION) {
            chosen.forEach(f -> service.delete(f.idNumber()));
            refreshRegistrations();
        }
    }

    // ---- reports ----------------------------------------------------------------------------

    private JPanel reportsTab() {
        repDistrict.addItem(ANY);
        Districts.all().forEach(repDistrict::addItem);
        repDistrict.addActionListener(e -> {
            String d = (String) repDistrict.getSelectedItem();
            DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
            m.addElement(ANY);
            Districts.subDistrictsOf(d).forEach(m::addElement);
            repSubDistrict.setModel(m);
            refreshReport();
        });
        repSubDistrict.addActionListener(e -> refreshReport());
        repGender.addActionListener(e -> refreshReport());
        repStatus.addActionListener(e -> refreshReport());

        JButton export = Ui.primary("Export report...");
        export.addActionListener(e -> exportReport());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("District"));
        filters.add(repDistrict);
        filters.add(new JLabel("Sub-district"));
        filters.add(repSubDistrict);
        filters.add(new JLabel("Gender"));
        filters.add(repGender);
        filters.add(new JLabel("Status"));
        filters.add(repStatus);

        JPanel bar = new JPanel(new BorderLayout());
        Ui.padding(bar, 12, 12, 12, 12);
        bar.add(filters, BorderLayout.WEST);
        bar.add(export, BorderLayout.EAST);

        JPanel foot = new JPanel(new BorderLayout());
        Ui.padding(foot, 8, 12, 8, 12);
        foot.add(repSummary);

        JTable table = new JTable(report);
        styleTable(table);
        table.setAutoCreateRowSorter(true);

        JPanel tab = new JPanel(new BorderLayout());
        tab.add(bar, BorderLayout.NORTH);
        tab.add(new JScrollPane(table));
        tab.add(foot, BorderLayout.SOUTH);
        return tab;
    }

    private ReportFilter reportFilter() {
        String d = (String) repDistrict.getSelectedItem();
        String s = (String) repSubDistrict.getSelectedItem();
        return new ReportFilter(
                ANY.equals(d) ? null : d,
                s == null || ANY.equals(s) ? null : s,
                repGender.getSelectedItem() instanceof Gender g ? g : null,
                repStatus.getSelectedItem() instanceof Status st ? st : null);
    }

    private void refreshReport() {
        ReportFilter filter = reportFilter();
        List<Farmer> rows = service.list(filter);
        report.setRows(rows);
        double ha = rows.stream().mapToDouble(Farmer::farmSize).sum();
        repSummary.setText(filter.describe() + "  |  " + rows.size() + (rows.size() == 1 ? " farmer" : " farmers")
                + ", " + String.format(Locale.ROOT, "%,.0f", ha) + " ha of farmland");
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save report");
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text report (*.txt)", "txt"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Spreadsheet (*.csv)", "csv"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("farmers-report-" + LocalDate.now() + ".txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        String ext = ((FileNameExtensionFilter) chooser.getFileFilter()).getExtensions()[0];
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith("." + ext)) {
            file = new File(file.getParentFile(), file.getName().replaceFirst("\\.(txt|csv)$", "") + "." + ext);
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this, file.getName() + " already exists. Replace it?",
                "Replace file", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            ReportWriter.write(file.toPath(), report.rows(), reportFilter(), LocalDateTime.now());
            JOptionPane.showMessageDialog(this, "Saved " + report.getRowCount() + " farmers to\n" + file,
                    "Report saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            Ui.showError(this, "Could not save the report: " + ex.getMessage());
        }
    }

    // ---- shared -----------------------------------------------------------------------------

    private void refreshAll() {
        refreshRegistrations();
        refreshReport();
    }

    /** Selects a tab by index; used by the screenshot tool. */
    void selectTab(int index) {
        tabs.setSelectedIndex(index);
    }

    JTable registrationsTable() {
        return regTable;
    }

    /** Sets the report's district filter; used by the screenshot tool. */
    void reportDistrict(String district) {
        repDistrict.setSelectedItem(district);
    }

    private static void styleTable(JTable table) {
        table.setRowHeight(com.formdev.flatlaf.util.UIScale.scale(28));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer number = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object value) {
                setText(value instanceof Double d ? String.format(Locale.ROOT, "%,.0f", d) : String.valueOf(value));
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus,
                                                           int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 16));
                return this;
            }
        };
        number.setHorizontalAlignment(SwingConstants.RIGHT);
        table.setDefaultRenderer(Double.class, number);
        table.setDefaultRenderer(Status.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus,
                                                           int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                if (value instanceof Status s && !selected) {
                    setForeground(Ui.statusColor(s));
                }
                setFont(getFont().deriveFont(java.awt.Font.BOLD));
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 6));
                return this;
            }
        });
        int[] widths = {90, 170, 70, 90, 110, 120, 80, 90, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(com.formdev.flatlaf.util.UIScale.scale(widths[i]));
        }
    }
}
