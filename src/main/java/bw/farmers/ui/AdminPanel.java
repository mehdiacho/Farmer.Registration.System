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
import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** The admin area: a sidebar, an overview dashboard, the registrations queue and reports. */
public final class AdminPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String ANY = "Any";
    private static final String[] PAGES = {"overview", "registrations", "reports"};
    private static final Color SIDEBAR_TEXT = new Color(0xB9CDBF);

    private final transient FarmerService service;
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = Ui.clear(cards);
    private final List<NavItem> nav = new ArrayList<>();
    private int page;

    // overview
    private final JPanel totalTile = Widgets.statTile("users", Ui.FOREST, Ui.FOREST_SOFT, "0", "Farmers registered",
            " ");
    private final JPanel pendingTile = Widgets.statTile("clock", Ui.GOLD_INK, Ui.GOLD_SOFT, "0",
            "Waiting for review", " ");
    private final JPanel approvedTile = Widgets.statTile("check-circle", Ui.FOREST, Ui.FOREST_SOFT, "0",
            "Approved", " ");
    private final JPanel landTile = Widgets.statTile("map", Ui.CLAY, Ui.CLAY_SOFT, "0", "Hectares of farmland",
            " ");
    private final Charts.DistrictBars districtBars = new Charts.DistrictBars();
    private final Charts.StatusDonut donut = new Charts.StatusDonut();
    private final JPanel queue = Ui.clear(new GridBagLayout());

    // registrations
    private final FarmerTableModel registrations = new FarmerTableModel();
    private final FarmerTable regTable = new FarmerTable(registrations);
    private final TableRowSorter<FarmerTableModel> regSorter = new TableRowSorter<>(registrations);
    private final Map<Status, JToggleButton> segments = new EnumMap<>(Status.class);
    private final JToggleButton allSegment = segment("All");
    private transient Status statusFilter;
    private final JTextField search = Ui.withIcon(new JTextField(20), "search");
    private final JLabel selection = Ui.text(" ", Ui.semibold(13.5f), Ui.INK);
    private final JButton approve = Ui.primary("Approve", "check");
    private final JButton decline = Ui.secondary("Decline", "x");
    private final JButton edit = Ui.secondary("Edit", "pencil");
    private final JButton delete = Ui.danger("Delete", "trash");

    // reports
    private final FarmerTableModel report = new FarmerTableModel();
    private final FarmerTable reportTable = new FarmerTable(report);
    private final JComboBox<String> repDistrict = new JComboBox<>();
    private final JComboBox<String> repSubDistrict = new JComboBox<>(new String[]{ANY});
    private final JComboBox<Object> repGender = new JComboBox<>(new Object[]{ANY, Gender.MALE, Gender.FEMALE});
    private final JComboBox<Object> repStatus = new JComboBox<>(new Object[]{ANY, Status.APPROVED, Status.PENDING,
            Status.DECLINED});
    private final JPanel repFarmers = Widgets.statTile("users", Ui.FOREST, Ui.FOREST_SOFT, "0", "Farmers", " ");
    private final JPanel repLand = Widgets.statTile("map", Ui.CLAY, Ui.CLAY_SOFT, "0", "Hectares of farmland", " ");
    private final JPanel repFarmed = Widgets.statTile("sprout", Ui.GOLD_INK, Ui.GOLD_SOFT, "0",
            "Hectares farmed", " ");
    private final JLabel repDescription = Ui.small(" ");

    public AdminPanel(FarmerService service, User admin, Navigator navigator) {
        super(new BorderLayout());
        this.service = service;
        setBackground(Ui.SAND);

        add(sidebar(admin, navigator), BorderLayout.WEST);
        pages.add(overviewPage(), PAGES[0]);
        pages.add(registrationsPage(), PAGES[1]);
        pages.add(reportsPage(), PAGES[2]);
        add(pages);

        refreshAll();
        selectTab(0);
    }

    // ---- sidebar ----------------------------------------------------------------------------

    private JPanel sidebar(User admin, Navigator navigator) {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(Ui.FOREST_DEEP);
        side.setPreferredSize(new Dimension(UIScale.scale(248), 10));
        Ui.padding(side, 22, 16, 18, 16);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        JLabel brand = Ui.text("<html>FarmersBW<br><span style='font-size:90%;color:#9DB8A6'>Admin console"
                + "</span></html>", Ui.display(18), Ui.CREAM);
        brand.setIcon(Logo.icon(38));
        brand.setIconTextGap(UIScale.scale(12));
        brand.setBorder(BorderFactory.createEmptyBorder(0, UIScale.scale(6), 0, 0));
        side.add(brand, c);

        JLabel menu = Ui.text("MENU", Ui.semibold(11), new Color(0x7F9E89));
        menu.setBorder(BorderFactory.createEmptyBorder(0, UIScale.scale(12), 0, 0));
        c.insets = new Insets(UIScale.scale(34), 0, UIScale.scale(8), 0);
        side.add(menu, c);
        String[][] items = {{"grid", "Overview"}, {"users", "Registrations"}, {"report", "Reports"}};
        for (int i = 0; i < items.length; i++) {
            int index = i;
            NavItem item = new NavItem(items[i][0], items[i][1], () -> selectTab(index));
            nav.add(item);
            c.insets = new Insets(UIScale.scale(2), 0, UIScale.scale(2), 0);
            side.add(item, c);
        }

        c.weighty = 1;
        side.add(Ui.clear(null), c);
        c.weighty = 0;

        JPanel note = new Ui.Surface(new BorderLayout(), new Color(255, 255, 255, 14), new Color(255, 255, 255, 26), 14);
        JLabel noteText = Ui.text("<html><b style='color:#F2C46B'>Tip</b><br>Double-click a farmer to edit<br>"
                + "them. Select several rows to<br>approve them all at once.</html>", Ui.font(Font.PLAIN, 12.5f),
                SIDEBAR_TEXT);
        noteText.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(12), UIScale.scale(14), UIScale.scale(12),
                UIScale.scale(12)));
        note.add(noteText);
        c.insets = new Insets(0, 0, UIScale.scale(16), 0);
        side.add(note, c);

        JPanel me = Ui.clear(new BorderLayout(UIScale.scale(8), 0));
        me.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 30)),
                BorderFactory.createEmptyBorder(UIScale.scale(16), UIScale.scale(4), 0, 0)));
        JLabel who = Ui.text("<html>" + Ui.escape(admin.displayName()) + "<br><span style='color:#9DB8A6'>"
                + "Administrator</span></html>", Ui.semibold(13), Ui.CREAM);
        who.setIcon(Ui.avatar(admin.displayName(), 34));
        who.setIconTextGap(UIScale.scale(10));
        me.add(who);
        JButton signOut = Ui.iconButton("logout", "Sign out", SIDEBAR_TEXT);
        signOut.addActionListener(e -> navigator.showLogin());
        me.add(signOut, BorderLayout.EAST);
        c.insets = new Insets(0, 0, 0, 0);
        side.add(me, c);
        return side;
    }

    /** One row of the sidebar menu. */
    private static final class NavItem extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String iconName;
        private final String text;
        private String badge;
        private boolean selected;
        private boolean hover;

        NavItem(String iconName, String text, Runnable onClick) {
            this.iconName = iconName;
            this.text = text;
            setFont(Ui.semibold(14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setFocusable(true);
            getAccessibleContext().setAccessibleName(text);
            for (String key : new String[]{"ENTER", "SPACE"}) {
                getInputMap().put(javax.swing.KeyStroke.getKeyStroke(key), "open");
            }
            getActionMap().put("open", new javax.swing.AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    onClick.run();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        void setBadge(String badge) {
            this.badge = badge;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(UIScale.scale(200), UIScale.scale(44));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            int w = getWidth();
            int h = getHeight();
            float arc = UIScale.scale(10f);
            if (selected || hover) {
                g.setColor(new Color(255, 255, 255, selected ? 26 : 12));
                g.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
            }
            if (selected) {
                g.setColor(new Color(0xF2B33D));
                g.fill(new RoundRectangle2D.Float(0, UIScale.scale(11f), UIScale.scale(3f), h - UIScale.scale(22f),
                        3, 3));
            }
            Color fg = selected ? Ui.CREAM : SIDEBAR_TEXT;
            Ui.icon(iconName, 19, fg).paintIcon(this, g, UIScale.scale(14), (h - UIScale.scale(19)) / 2);
            g.setFont(getFont());
            g.setColor(fg);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, UIScale.scale(46), (h - fm.getHeight()) / 2 + fm.getAscent());
            if (badge != null) {
                g.setFont(Ui.semibold(12));
                fm = g.getFontMetrics();
                int bw = Math.max(UIScale.scale(24), fm.stringWidth(badge) + UIScale.scale(14));
                int bh = UIScale.scale(22);
                int bx = w - bw - UIScale.scale(10);
                int by = (h - bh) / 2;
                g.setColor(new Color(0xF2B33D));
                g.fill(new RoundRectangle2D.Float(bx, by, bw, bh, bh, bh));
                g.setColor(Ui.FOREST_DEEP);
                g.drawString(badge, bx + (bw - fm.stringWidth(badge)) / 2f, by + (bh - fm.getHeight()) / 2f
                        + fm.getAscent());
            }
            g.dispose();
        }
    }

    private static JPanel pageHeader(String title, String subtitle, JComponent right) {
        JPanel head = Ui.clear(new BorderLayout(UIScale.scale(16), 0));
        JPanel titles = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        titles.add(Ui.text(title, Ui.display(30), Ui.INK), c);
        c.insets = new Insets(UIScale.scale(4), 0, 0, 0);
        titles.add(Ui.subtitle(subtitle), c);
        head.add(titles, BorderLayout.WEST);
        if (right != null) {
            JPanel holder = Ui.clear(new GridBagLayout());
            holder.add(right);
            head.add(holder, BorderLayout.EAST);
        }
        return head;
    }

    private static JPanel page() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Ui.SAND);
        return p;
    }

    // ---- overview ---------------------------------------------------------------------------

    private JComponent overviewPage() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH));
        JLabel date = Ui.text(today, Ui.semibold(13.5f), Ui.MUTED);
        date.setIcon(Ui.icon("calendar", 16, Ui.MUTED));
        date.setIconTextGap(UIScale.scale(8));

        JPanel bars = Ui.card(new BorderLayout(0, UIScale.scale(16)), 22, 24);
        bars.add(cardTitle("Farmers by district", "Approved, pending and declined in each district."),
                BorderLayout.NORTH);
        bars.add(districtBars);
        JPanel ring = Ui.card(new BorderLayout(0, UIScale.scale(12)), 22, 24);
        ring.add(cardTitle("Registration status", "Where every registration stands."), BorderLayout.NORTH);
        JPanel ringHolder = Ui.clear(new GridBagLayout());
        ringHolder.add(donut);
        ring.add(ringHolder);

        JPanel charts = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.64;
        c.insets = new Insets(0, 0, 0, UIScale.scale(20));
        charts.add(bars, c);
        c.weightx = 0.36;
        c.insets = new Insets(0, 0, 0, 0);
        charts.add(ring, c);

        JButton viewAll = Ui.link("Open the queue →");
        viewAll.addActionListener(e -> {
            setStatusFilter(Status.PENDING);
            selectTab(1);
        });
        JPanel pendingCard = Ui.card(new BorderLayout(0, UIScale.scale(10)), 22, 24);
        JPanel pendingHead = Ui.clear(new BorderLayout());
        pendingHead.add(cardTitle("Waiting for review", "The newest registrations that need a decision."));
        pendingHead.add(viewAll, BorderLayout.EAST);
        pendingCard.add(pendingHead, BorderLayout.NORTH);
        pendingCard.add(queue);

        Widgets.Column column = new Widgets.Column(1280, 30, 34);
        column.block(pageHeader("Overview", "Registrations across Botswana at a glance.", date), 0)
                .block(Widgets.row(18, totalTile, pendingTile, approvedTile, landTile), 26)
                .block(charts, 20)
                .block(pendingCard, 20);
        JScrollPane scroll = new JScrollPane(column);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Ui.SAND);
        scroll.getVerticalScrollBar().setUnitIncrement(UIScale.scale(16));
        JPanel p = page();
        p.add(scroll);
        return p;
    }

    private static JPanel cardTitle(String title, String hint) {
        JPanel t = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        t.add(Ui.heading(title, 17), c);
        c.insets = new Insets(UIScale.scale(2), 0, 0, 0);
        t.add(Ui.small(hint), c);
        return t;
    }

    private void refreshOverview(List<Farmer> all) {
        int approved = count(all, Status.APPROVED);
        int pending = count(all, Status.PENDING);
        int declined = count(all, Status.DECLINED);
        double land = all.stream().mapToDouble(Farmer::farmSize).sum();
        double farmed = all.stream().mapToDouble(Farmer::farmingArea).sum();
        long districts = all.stream().map(Farmer::district).distinct().count();
        Widgets.setTile(totalTile, fmt(all.size()), "in " + districts + " districts");
        Widgets.setTile(pendingTile, fmt(pending), pending == 0 ? "All caught up" : "Needs a decision");
        Widgets.setTile(approvedTile, fmt(approved), all.isEmpty() ? " "
                : Math.round(100.0 * approved / all.size()) + "% of all registrations");
        Widgets.setTile(landTile, String.format(Locale.ROOT, "%,.0f", land),
                String.format(Locale.ROOT, "%,.0f ha farmed", farmed));
        districtBars.setData(Charts.byDistrict(all));
        donut.setData(approved, pending, declined);
        nav.get(1).setBadge(pending == 0 ? null : String.valueOf(pending));

        queue.removeAll();
        List<Farmer> waiting = all.stream().filter(f -> f.status() == Status.PENDING).limit(6).toList();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        if (waiting.isEmpty()) {
            JLabel done = Ui.text("All caught up. No registrations are waiting.", Ui.semibold(14), Ui.FOREST);
            done.setIcon(Ui.icon("check-circle", 20, Ui.FOREST));
            done.setIconTextGap(UIScale.scale(10));
            done.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(18), 0, UIScale.scale(8), 0));
            queue.add(done, c);
        }
        for (Farmer f : waiting) {
            queue.add(queueRow(f), c);
        }
        queue.revalidate();
        queue.repaint();
    }

    private JPanel queueRow(Farmer f) {
        JPanel row = Ui.clear(new GridBagLayout());
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(0xEFE8DC)), BorderFactory.createEmptyBorder(UIScale.scale(12), 0, UIScale.scale(12), 0)));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.42;
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel who = Ui.text("<html><b>" + Ui.escape(f.fullName()) + "</b><br><span style='color:#5E6A60'>"
                + Ui.escape(f.email()) + "</span></html>", Ui.font(Font.PLAIN, 13.5f), Ui.INK);
        who.setIcon(Ui.avatar(f.fullName(), 36));
        who.setIconTextGap(UIScale.scale(12));
        who.setPreferredSize(new Dimension(1, who.getPreferredSize().height));
        row.add(who, c);
        c.weightx = 0.28;
        JLabel where = Ui.text("<html>" + Ui.escape(f.district()) + "<br><span style='color:#5E6A60'>"
                + Ui.escape(f.subDistrict()) + "</span></html>", Ui.font(Font.PLAIN, 13.5f), Ui.INK);
        where.setIcon(Ui.icon("pin", 16, Ui.MUTED));
        where.setIconTextGap(UIScale.scale(8));
        where.setPreferredSize(new Dimension(1, where.getPreferredSize().height));
        row.add(where, c);
        c.weightx = 0.12;
        JLabel size = Ui.text(String.format(Locale.ROOT, "%,.0f ha", f.farmSize()), Ui.semibold(13.5f), Ui.INK);
        size.setPreferredSize(new Dimension(1, size.getPreferredSize().height));
        row.add(size, c);
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JButton no = compact(Ui.secondary("Decline", "x"));
        JButton yes = compact(Ui.primary("Approve", "check"));
        no.addActionListener(e -> decide(List.of(f), Status.DECLINED));
        yes.addActionListener(e -> decide(List.of(f), Status.APPROVED));
        c.insets = new Insets(0, UIScale.scale(8), 0, 0);
        row.add(no, c);
        row.add(yes, c);
        return row;
    }

    private static JButton compact(JButton b) {
        Object style = b.getClientProperty(FlatClientProperties.STYLE);
        b.putClientProperty(FlatClientProperties.STYLE, style + "; margin: 6,12,6,14");
        b.setFont(Ui.semibold(13));
        return b;
    }

    // ---- registrations ----------------------------------------------------------------------

    private JComponent registrationsPage() {
        regTable.setRowSorter(regSorter);
        regTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        regTable.getSelectionModel().addListSelectionListener(e -> updateSelection());
        regTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && regTable.getSelectedRow() >= 0) {
                    editSelected();
                }
            }
        });

        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search name, email, ID or district");
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

        ButtonGroup group = new ButtonGroup();
        JPanel segmented = new Ui.Surface(new FlowLayout(FlowLayout.LEFT, UIScale.scale(2), 0), Ui.SURFACE,
                Ui.BORDER, 12);
        segmented.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(4), UIScale.scale(4), UIScale.scale(4),
                UIScale.scale(4)));
        group.add(allSegment);
        segmented.add(allSegment);
        allSegment.addActionListener(e -> setStatusFilter(null));
        for (Status s : new Status[]{Status.PENDING, Status.APPROVED, Status.DECLINED}) {
            JToggleButton b = segment(s.label());
            b.addActionListener(e -> setStatusFilter(s));
            segments.put(s, b);
            group.add(b);
            segmented.add(b);
        }
        allSegment.setSelected(true);

        JPanel toolbar = Ui.clear(new BorderLayout(UIScale.scale(16), 0));
        toolbar.add(segmented, BorderLayout.WEST);
        toolbar.add(search, BorderLayout.EAST);

        approve.addActionListener(e -> decide(selected(), Status.APPROVED));
        decline.addActionListener(e -> decide(selected(), Status.DECLINED));
        edit.addActionListener(e -> editSelected());
        delete.addActionListener(e -> deleteSelected());
        JPanel buttons = Ui.clear(new FlowLayout(FlowLayout.RIGHT, UIScale.scale(8), 0));
        buttons.add(delete);
        buttons.add(edit);
        buttons.add(decline);
        buttons.add(approve);
        JPanel actions = Ui.clear(new BorderLayout());
        actions.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(16), UIScale.scale(4), UIScale.scale(14),
                0));
        actions.add(selection, BorderLayout.WEST);
        actions.add(buttons, BorderLayout.EAST);

        JPanel top = Ui.clear(new BorderLayout());
        top.add(pageHeader("Registrations", "Review new farmers, and keep the register correct.", null),
                BorderLayout.NORTH);
        JPanel bars = Ui.clear(new BorderLayout());
        bars.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(24), 0, 0, 0));
        bars.add(toolbar, BorderLayout.NORTH);
        bars.add(actions, BorderLayout.SOUTH);
        top.add(bars, BorderLayout.SOUTH);

        JPanel p = page();
        Ui.padding(p, 30, 34, 30, 34);
        p.add(top, BorderLayout.NORTH);
        p.add(tableCard(regTable));
        updateSelection();
        return p;
    }

    private static JToggleButton segment(String text) {
        JToggleButton b = new JToggleButton(text);
        b.setFont(Ui.semibold(13.5f));
        b.setForeground(Ui.MUTED);
        b.addItemListener(e -> b.setForeground(b.isSelected() ? Color.WHITE : Ui.MUTED));
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 9; margin: 7,14,7,14; focusWidth: 0; borderWidth: 0;"
                + " innerFocusWidth: 0; shadowWidth: 0; background: #FFFFFF; borderColor: #FFFFFF;"
                + " selectedBackground: #1E5B3A;"
                + " hoverBackground: #F1EADD; pressedBackground: #E9E0CF");
        return b;
    }

    private static JComponent tableCard(JTable table) {
        Widgets.ClipCard card = new Widgets.ClipCard(new BorderLayout(), 16);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Ui.SURFACE);
        scroll.getVerticalScrollBar().setUnitIncrement(UIScale.scale(20));
        card.add(scroll);
        return card;
    }

    private void setStatusFilter(Status s) {
        statusFilter = s;
        (s == null ? allSegment : segments.get(s)).setSelected(true);
        refreshRegistrations();
    }

    private void refreshRegistrations() {
        List<Farmer> all = service.list(ReportFilter.ALL);
        allSegment.setText("All  " + all.size());
        segments.forEach((s, b) -> b.setText(s.label() + "  " + count(all, s)));
        registrations.setRows(statusFilter == null ? all
                : all.stream().filter(f -> f.status() == statusFilter).toList());
        regTable.setEmptyMessage(statusFilter == Status.PENDING ? "Nothing waiting for review"
                : "No farmers match", statusFilter == Status.PENDING ? "Every registration has a decision."
                : "Try another filter or search.");
        applySearch();
        refreshOverview(all);
    }

    private void applySearch() {
        String q = search.getText().strip().toLowerCase(Locale.ROOT);
        regSorter.setRowFilter(q.isEmpty() ? null : new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends FarmerTableModel, ? extends Integer> entry) {
                Farmer f = entry.getModel().at(entry.getIdentifier());
                return (f.fullName() + " " + f.email() + " " + f.idNumber() + " " + f.district() + " "
                        + f.subDistrict() + " " + f.contact()).toLowerCase(Locale.ROOT).contains(q);
            }
        });
        updateSelection();
    }

    private void updateSelection() {
        int n = regTable.getSelectedRowCount();
        int shown = regTable.getRowCount();
        if (n == 0) {
            selection.setText(shown + (shown == 1 ? " farmer" : " farmers"));
            selection.setForeground(Ui.MUTED);
        } else {
            selection.setText(n + " selected");
            selection.setForeground(Ui.INK);
        }
        approve.setEnabled(n > 0);
        decline.setEnabled(n > 0);
        delete.setEnabled(n > 0);
        edit.setEnabled(n == 1);
    }

    private List<Farmer> selected() {
        return Arrays.stream(regTable.getSelectedRows()).mapToObj(regTable::farmerAt).toList();
    }

    private void decide(List<Farmer> chosen, Status status) {
        if (chosen.isEmpty()) {
            return;
        }
        for (Farmer f : chosen) {
            service.setStatus(f.idNumber(), status);
        }
        refreshAll();
        String who = chosen.size() == 1 ? chosen.get(0).fullName() : chosen.size() + " farmers";
        Widgets.toast(this, (status == Status.APPROVED ? "Approved " : "Declined ") + who);
    }

    private void editSelected() {
        List<Farmer> chosen = selected();
        if (chosen.size() != 1) {
            return;
        }
        EditFarmerDialog d = new EditFarmerDialog(this, service, chosen.get(0));
        d.setVisible(true);
        if (d.saved()) {
            refreshAll();
            Widgets.toast(this, "Saved changes to " + chosen.get(0).fullName());
        }
    }

    private void deleteSelected() {
        List<Farmer> chosen = selected();
        if (chosen.isEmpty()) {
            return;
        }
        String who = chosen.size() == 1 ? chosen.get(0).fullName() : chosen.size() + " farmers";
        if (Dialogs.confirm(this, "Delete " + who + "?", "This removes " + (chosen.size() == 1 ? "the farmer"
                : "these farmers") + " and their sign-in from the register. It cannot be undone.", "Delete", true)) {
            chosen.forEach(f -> service.delete(f.idNumber()));
            refreshAll();
            Widgets.toast(this, "Deleted " + who);
        }
    }

    // ---- reports ----------------------------------------------------------------------------

    private JComponent reportsPage() {
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

        JButton export = Ui.primary("Export report", "download");
        export.addActionListener(e -> exportReport());

        JPanel filters = Ui.card(new GridBagLayout(), 6, 22);
        new Ui.Grid(filters, 4)
                .field("District", repDistrict)
                .field("Sub-district", repSubDistrict)
                .field("Gender", repGender)
                .field("Status", repStatus);
        Ui.padding(filters, 6, 22, 22, 22);

        repDescription.setIcon(Ui.icon("report", 15, Ui.MUTED));
        repDescription.setIconTextGap(UIScale.scale(8));
        JPanel tableHead = Ui.clear(new BorderLayout());
        tableHead.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(20), UIScale.scale(4), UIScale.scale(10),
                0));
        tableHead.add(repDescription, BorderLayout.WEST);

        JPanel top = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        top.add(pageHeader("Reports", "Filter the register, then export it as a text report or spreadsheet.",
                export), c);
        c.insets = new Insets(UIScale.scale(24), 0, 0, 0);
        top.add(filters, c);
        c.insets = new Insets(UIScale.scale(18), 0, 0, 0);
        top.add(Widgets.row(18, repFarmers, repLand, repFarmed), c);
        c.insets = new Insets(0, 0, 0, 0);
        top.add(tableHead, c);

        reportTable.setEmptyMessage("No farmers match this report", "Loosen one of the filters above.");
        reportTable.setAutoCreateRowSorter(true);

        JPanel p = page();
        Ui.padding(p, 30, 34, 30, 34);
        p.add(top, BorderLayout.NORTH);
        p.add(tableCard(reportTable));
        return p;
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
        double land = rows.stream().mapToDouble(Farmer::farmSize).sum();
        double farmed = rows.stream().mapToDouble(Farmer::farmingArea).sum();
        Widgets.setTile(repFarmers, fmt(rows.size()), count(rows, Status.APPROVED) + " approved");
        Widgets.setTile(repLand, String.format(Locale.ROOT, "%,.0f", land), rows.isEmpty() ? " "
                : String.format(Locale.ROOT, "%,.0f ha on average", land / rows.size()));
        Widgets.setTile(repFarmed, String.format(Locale.ROOT, "%,.0f", farmed), land <= 0 ? " "
                : Math.round(100 * farmed / land) + "% of the farmland");
        repDescription.setText(filter.describe());
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
        if (file.exists() && !Dialogs.confirm(this, "Replace " + file.getName() + "?",
                "A file with this name already exists in that folder.", "Replace", false)) {
            return;
        }
        try {
            ReportWriter.write(file.toPath(), report.rows(), reportFilter(), LocalDateTime.now());
            Widgets.toast(this, "Saved " + report.getRowCount() + " farmers to " + file.getName());
        } catch (IOException ex) {
            Ui.showError(this, "Could not save the report: " + ex.getMessage());
        }
    }

    // ---- shared -----------------------------------------------------------------------------

    private void refreshAll() {
        refreshRegistrations();
        refreshReport();
    }

    private static int count(List<Farmer> farmers, Status s) {
        return (int) farmers.stream().filter(f -> f.status() == s).count();
    }

    private static String fmt(int n) {
        return String.format(Locale.ROOT, "%,d", n);
    }

    /** Shows a page: 0 overview, 1 registrations, 2 reports. Also used by the screenshot tool. */
    void selectTab(int index) {
        page = index;
        cards.show(pages, PAGES[index]);
        for (int i = 0; i < nav.size(); i++) {
            nav.get(i).setSelected(i == index);
        }
        if (index == 1) {
            regTable.requestFocusInWindow();
        }
    }

    int selectedTab() {
        return page;
    }

    JTable registrationsTable() {
        return regTable;
    }

    /** Sets the report's district filter; used by the screenshot tool. */
    void reportDistrict(String district) {
        repDistrict.setSelectedItem(district);
    }
}
