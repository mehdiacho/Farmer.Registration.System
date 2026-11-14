package bw.farmers.ui;

import bw.farmers.model.Status;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/**
 * The FarmersBW design system: colour tokens, type scale, icons and the small set of
 * components every screen is built from. Colours mirror {@code theme/FlatLightLaf.properties}.
 */
public final class Ui {

    // ---- colour tokens ----
    public static final Color SAND = new Color(0xF5F0E6);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(0xFBF8F2);
    public static final Color BORDER = new Color(0xE4DACA);
    public static final Color INK = new Color(0x1D2B22);
    public static final Color MUTED = new Color(0x5E6A60);
    public static final Color FOREST = new Color(0x1E5B3A);
    public static final Color FOREST_DEEP = new Color(0x123726);
    public static final Color FOREST_SOFT = new Color(0xE2EEE5);
    public static final Color GOLD = new Color(0xD99A1E);
    public static final Color GOLD_INK = new Color(0x8A5A00);
    public static final Color GOLD_SOFT = new Color(0xFBF0D6);
    public static final Color CLAY = new Color(0xB4472C);
    public static final Color CLAY_SOFT = new Color(0xF7E2DA);
    public static final Color CREAM = new Color(0xFFF8EA);
    /** Kept for older call sites: the accent is the forest green. */
    public static final Color ACCENT = FOREST;

    private static final String ICONS = "bw/farmers/ui/icons/";

    private Ui() {
    }

    /** Installs FlatLaf with the FarmersBW theme and the Inter typeface. Call once, before any component exists. */
    public static void installLookAndFeel() {
        FlatInterFont.install();
        FlatInterFont.installSemiBold();
        FlatLaf.registerCustomDefaultsSource("bw.farmers.ui.theme");
        FlatLaf.setUseNativeWindowDecorations(true);
        FlatLightLaf.setup();
    }

    // ---- type ----

    /** Inter at {@code px} (unscaled) in the given style. */
    public static Font font(int style, float px) {
        return new Font(FlatInterFont.FAMILY, style, 1).deriveFont(style, UIScale.scale(px));
    }

    /** Inter SemiBold, for labels and table headers. */
    public static Font semibold(float px) {
        return new Font(FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 1).deriveFont(UIScale.scale(px));
    }

    /** Bold Inter with tight tracking, for headlines. */
    public static Font display(float px) {
        return font(Font.BOLD, px).deriveFont(Map.of(TextAttribute.TRACKING, -0.022f));
    }

    public static JLabel title(String text) {
        return text(text, display(26), INK);
    }

    public static JLabel heading(String text, float px) {
        return text(text, display(px), INK);
    }

    public static JLabel subtitle(String text) {
        return text(text, font(Font.PLAIN, 14), MUTED);
    }

    public static JLabel small(String text) {
        return text(text, font(Font.PLAIN, 12.5f), MUTED);
    }

    public static JLabel label(String text) {
        return text(text, semibold(13), INK);
    }

    public static JLabel text(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4)); // room for glyph overhang
        return l;
    }

    // ---- icons ----

    /** A line icon from {@code icons/}, recoloured to {@code color}. */
    public static FlatSVGIcon icon(String name, int px, Color color) {
        FlatSVGIcon icon = new FlatSVGIcon(ICONS + name + ".svg", px, px);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
        return icon;
    }

    /** A round avatar with the person's initials, tinted from their name. */
    public static Icon avatar(String name, int px) {
        return new AvatarIcon(name, px);
    }

    // ---- buttons ----

    public static JButton primary(String text) {
        return primary(text, null);
    }

    public static JButton primary(String text, String iconName) {
        JButton b = button(text, iconName, Color.WHITE);
        b.putClientProperty(FlatClientProperties.STYLE, "background: #1E5B3A; foreground: #FFFFFF; borderWidth: 0;"
                + " focusedBorderColor: #FFFFFF; focusWidth: 2; focusColor: #1E5B3A66;"
                + " hoverBackground: #174A2F; pressedBackground: #123C26; margin: 10,20,10,20;"
                + " disabledBackground: #A9C2B2; disabledText: #F5F0E6");
        return b;
    }

    public static JButton secondary(String text, String iconName) {
        JButton b = button(text, iconName, INK);
        b.putClientProperty(FlatClientProperties.STYLE, "background: #FFFFFF; foreground: #1D2B22;"
                + " borderColor: #D9CFBC; hoverBackground: #FBF8F2; pressedBackground: #F1EADD; margin: 10,18,10,18;"
                + " hoverBorderColor: #C2B7A3");
        return b;
    }

    public static JButton danger(String text, String iconName) {
        JButton b = button(text, iconName, CLAY);
        b.putClientProperty(FlatClientProperties.STYLE, "background: #FFFFFF; foreground: #B4472C;"
                + " borderColor: #EBC5B9; hoverBackground: #F7E2DA; pressedBackground: #F1D1C6; margin: 10,18,10,18");
        return b;
    }

    /** A borderless text button in the accent colour. */
    public static JButton link(String text) {
        return ghost(text, null, FOREST);
    }

    public static JButton ghost(String text, String iconName, Color color) {
        JButton b = button(text, iconName, color);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        b.setForeground(color);
        b.putClientProperty(FlatClientProperties.STYLE, "margin: 8,10,8,10; hoverBackground: #00000010;"
                + " pressedBackground: #00000018");
        return b;
    }

    /** A small square icon-only button, with a tooltip for its name. */
    public static JButton iconButton(String iconName, String tooltip, Color color) {
        JButton b = new JButton(icon(iconName, 18, color));
        b.setToolTipText(tooltip);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        b.putClientProperty(FlatClientProperties.STYLE, "margin: 8,8,8,8; arc: 10; hoverBackground: #FFFFFF22;"
                + " pressedBackground: #FFFFFF33");
        b.getAccessibleContext().setAccessibleName(tooltip);
        return b;
    }

    private static JButton button(String text, String iconName, Color iconColor) {
        JButton b = new JButton(text);
        b.setFont(semibold(14));
        if (iconName != null) {
            b.setIcon(icon(iconName, 17, iconColor));
            b.setIconTextGap(UIScale.scale(8));
        }
        return b;
    }

    // ---- inputs ----

    /** Adds a leading icon to a text field. */
    public static <T extends JTextField> T withIcon(T field, String iconName) {
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, icon(iconName, 17, MUTED));
        return field;
    }

    /** Adds a muted unit label (like "ha") inside the right edge of a text field. */
    public static <T extends JTextField> T withUnit(T field, String unit) {
        JLabel l = small(unit);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIScale.scale(4)));
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, l);
        return field;
    }

    // ---- status ----

    public static Color statusColor(Status s) {
        return switch (s) {
            case APPROVED -> FOREST;
            case PENDING -> GOLD_INK;
            case DECLINED -> CLAY;
        };
    }

    public static Color statusSoft(Status s) {
        return switch (s) {
            case APPROVED -> FOREST_SOFT;
            case PENDING -> GOLD_SOFT;
            case DECLINED -> CLAY_SOFT;
        };
    }

    /** The dot colour used in charts and pills. */
    public static Color statusSwatch(Status s) {
        return switch (s) {
            case APPROVED -> new Color(0x2F7D4A);
            case PENDING -> GOLD;
            case DECLINED -> CLAY;
        };
    }

    public static String statusIcon(Status s) {
        return switch (s) {
            case APPROVED -> "check-circle";
            case PENDING -> "clock";
            case DECLINED -> "x-circle";
        };
    }

    /** A rounded status pill: tinted background, dot, label. */
    public static Pill badge(Status s) {
        Pill p = new Pill();
        p.setStatus(s);
        return p;
    }

    // ---- surfaces ----

    /** A white rounded card that lays its children out with GridBagLayout. */
    public static JPanel card() {
        return card(new GridBagLayout(), 24, 28);
    }

    public static JPanel card(LayoutManager layout, int padV, int padH) {
        Surface p = new Surface(layout, SURFACE, BORDER, 16);
        p.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(padV), UIScale.scale(padH), UIScale.scale(padV),
                UIScale.scale(padH)));
        return p;
    }

    /** A plain transparent panel. */
    public static JPanel clear(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    public static void padding(JComponent c, int top, int left, int bottom, int right) {
        c.setBorder(BorderFactory.createEmptyBorder(UIScale.scale(top), UIScale.scale(left), UIScale.scale(bottom),
                UIScale.scale(right)));
    }

    /** A section heading inside a form: icon tile, title, and a short hint. */
    public static JComponent section(String title, String hint, String iconName) {
        JPanel row = clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 0, UIScale.scale(12));
        JLabel tile = new JLabel(icon(iconName, 18, FOREST));
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setPreferredSize(new java.awt.Dimension(UIScale.scale(36), UIScale.scale(36)));
        tile.setOpaque(false);
        tile.putClientProperty("tile", Boolean.TRUE);
        row.add(new Surface(new java.awt.BorderLayout(), FOREST_SOFT, null, 10).with(tile), c);
        c.gridx = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        row.add(text(title, semibold(15.5f), INK), c);
        c.gridy = 1;
        row.add(small(hint), c);
        return row;
    }

    // ---- dialogs (kept here so existing call sites keep working) ----

    public static void showProblems(Component parent, bw.farmers.service.ValidationException e) {
        Dialogs.problems(parent, e.problems());
    }

    public static void showError(Component parent, String message) {
        Dialogs.message(parent, "Something went wrong", message, "alert", CLAY);
    }

    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static void antialias(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    // ---- small components ----

    /** A panel that paints a rounded fill and optional hairline border. */
    public static final class Surface extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color fill;
        private final Color line;
        private final int arc;

        public Surface(LayoutManager layout, Color fill, Color line, int arc) {
            super(layout);
            this.fill = fill;
            this.line = line;
            this.arc = arc;
            setOpaque(false);
        }

        Surface with(Component child) {
            add(child);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            antialias(g);
            float a = UIScale.scale((float) arc);
            RoundRectangle2D r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, a, a);
            g.setColor(fill);
            g.fill(r);
            if (line != null) {
                g.setColor(line);
                g.setStroke(new BasicStroke(1f));
                g.draw(r);
            }
            g.dispose();
        }
    }

    /** A status pill. Also used as a table cell renderer component. */
    public static final class Pill extends JLabel {
        private static final long serialVersionUID = 1L;
        private Status status = Status.PENDING;

        Pill() {
            setFont(semibold(12.5f));
            setBorder(BorderFactory.createEmptyBorder(UIScale.scale(4), UIScale.scale(22), UIScale.scale(4),
                    UIScale.scale(11)));
        }

        void setStatus(Status s) {
            status = s;
            setText(s.label());
            setForeground(statusColor(s));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            antialias(g);
            FontMetrics fm = g.getFontMetrics(getFont());
            Insets in = getInsets();
            int w = in.left + fm.stringWidth(getText()) + in.right;
            int h = fm.getHeight() + in.top + in.bottom;
            int y = (getHeight() - h) / 2;
            g.setColor(statusSoft(status));
            g.fill(new RoundRectangle2D.Float(0, y, w, h, h, h));
            g.setColor(statusSwatch(status));
            float d = UIScale.scale(7f);
            g.fill(new java.awt.geom.Ellipse2D.Float(UIScale.scale(10f), y + (h - d) / 2f, d, d));
            g.setColor(getForeground());
            g.setFont(getFont());
            g.drawString(getText(), in.left, y + in.top + fm.getAscent());
            g.dispose();
        }

        @Override
        public java.awt.Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            Insets in = getInsets();
            return new java.awt.Dimension(in.left + fm.stringWidth(getText()) + in.right + 2,
                    fm.getHeight() + in.top + in.bottom);
        }
    }

    private static final class AvatarIcon implements Icon {
        private static final Color[][] TINTS = {
                {new Color(0xE2EEE5), new Color(0x1E5B3A)},
                {new Color(0xFBF0D6), new Color(0x8A5A00)},
                {new Color(0xF7E2DA), new Color(0x9A3A22)},
                {new Color(0xE3ECF1), new Color(0x2C5A70)},
                {new Color(0xEDE6F3), new Color(0x5B4277)},
                {new Color(0xEFE9DD), new Color(0x5E5139)},
        };
        private final String initials;
        private final Color[] tint;
        private final int size;

        AvatarIcon(String name, int px) {
            String[] parts = name.strip().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                if (!p.isEmpty() && sb.length() < 2) {
                    sb.append(Character.toUpperCase(p.charAt(0)));
                }
            }
            initials = sb.toString();
            tint = TINTS[Math.floorMod(name.hashCode(), TINTS.length)];
            size = UIScale.scale(px);
        }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            antialias(g);
            g.setColor(tint[0]);
            g.fillOval(x, y, size, size);
            g.setColor(tint[1]);
            Font f = new Font(FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 1).deriveFont(size * 0.38f);
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (size - fm.stringWidth(initials)) / 2;
            int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(initials, tx, ty);
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    /** A label/field grid built with GridBagLayout. Labels sit above their fields. */
    public static final class Grid {
        private final JPanel panel;
        private int row;
        private int col;
        private final int columns;

        public Grid(JPanel panel, int fieldColumns) {
            this.panel = panel;
            this.columns = fieldColumns;
        }

        /** Adds a label above its field; fields flow left to right, {@code columns} per row. */
        public Grid field(String label, JComponent field) {
            return field(label, field, 1);
        }

        public Grid field(String label, JComponent field, int span) {
            if (col + span > columns) {
                newRow();
            }
            int gap = UIScale.scale(16);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = col;
            c.gridy = row * 2;
            c.gridwidth = span;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(UIScale.scale(14), col == 0 ? 0 : gap, UIScale.scale(6), 0);
            JLabel l = label(label);
            l.setLabelFor(field);
            panel.add(l, c);
            c.gridy = row * 2 + 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.insets = new Insets(0, col == 0 ? 0 : gap, 0, 0);
            panel.add(field, c);
            col += span;
            return this;
        }

        public Grid full(JComponent component) {
            return full(component, 16);
        }

        /** Adds a component across every column, {@code gapAbove} px below what came before. */
        public Grid full(JComponent component, int gapAbove) {
            if (col > 0) {
                newRow();
            }
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = row * 2;
            c.gridwidth = columns;
            c.gridheight = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.insets = new Insets(row == 0 ? 0 : UIScale.scale(gapAbove), 0, 0, 0);
            panel.add(component, c);
            newRow();
            return this;
        }

        /** Adds a form section heading. */
        public Grid section(String title, String hint, String iconName) {
            return full(Ui.section(title, hint, iconName), 30);
        }

        public Grid newRow() {
            row++;
            col = 0;
            return this;
        }
    }
}
