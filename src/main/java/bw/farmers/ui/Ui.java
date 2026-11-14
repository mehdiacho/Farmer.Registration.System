package bw.farmers.ui;

import bw.farmers.model.Status;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

/** Shared look and small layout helpers for the screens. */
public final class Ui {

    public static final Color ACCENT = new Color(0x2E7D32);
    public static final Color MUTED = new Color(0x5F6B66);

    private Ui() {
    }

    /** Installs FlatLaf with a green accent. Call once, before any component is created. */
    public static void installLookAndFeel() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#2E7D32"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
        UIManager.put("TitlePane.unifiedBackground", true);
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4)); // room for glyph overhang
        return l;
    }

    public static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4)); // room for glyph overhang
        return l;
    }

    public static JButton primary(String text) {
        JButton b = new JButton(text);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        b.putClientProperty(FlatClientProperties.STYLE, "background: #2E7D32; foreground: #FFFFFF; borderWidth: 0;"
                + " focusedBorderColor: #1F5A23; hoverBackground: #276B2B; pressedBackground: #1F5A23;"
                + " margin: 6,16,6,16");
        return b;
    }

    public static JButton link(String text) {
        JButton b = new JButton(text);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        b.setForeground(ACCENT);
        return b;
    }

    /** A rounded coloured pill showing a registration status. */
    public static JLabel badge(Status s) {
        JLabel l = new JLabel(s.label());
        l.setOpaque(false);
        l.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: " + hex(statusColor(s))
                + "; border: 2,10,2,10, " + hex(statusColor(s)) + ", 1, 999");
        return l;
    }

    public static Color statusColor(Status s) {
        return switch (s) {
            case APPROVED -> new Color(0x2E7D32);
            case PENDING -> new Color(0xB26A00);
            case DECLINED -> new Color(0xC62828);
        };
    }

    private static String hex(Color c) {
        return String.format("#%06X", c.getRGB() & 0xFFFFFF);
    }

    /** A white card with padding, used to hold forms. */
    public static JPanel card() {
        JPanel p = new JPanel(new GridBagLayout());
        p.putClientProperty(FlatClientProperties.STYLE, "background: #FFFFFF; border: 20,24,20,24, #DDE3DF, 1, 12");
        return p;
    }

    public static void padding(JComponent c, int top, int left, int bottom, int right) {
        c.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    public static void showProblems(Component parent, ValidationException e) {
        StringBuilder sb = new StringBuilder("<html>Please fix the following:<ul style='margin-left:16px'>");
        for (String p : e.problems()) {
            sb.append("<li>").append(escape(p)).append("</li>");
        }
        sb.append("</ul></html>");
        JOptionPane.showMessageDialog(parent, sb.toString(), "Check the form", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }

    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** A two-column label/field grid built with GridBagLayout. */
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
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = col;
            c.gridy = row * 2;
            c.gridwidth = span;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(8, col == 0 ? 0 : 12, 4, 0);
            JLabel l = new JLabel(label);
            l.setLabelFor(field);
            l.setFont(l.getFont().deriveFont(Font.BOLD, l.getFont().getSize2D() - 1f));
            panel.add(l, c);
            c.gridy = row * 2 + 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.insets = new Insets(0, col == 0 ? 0 : 12, 0, 0);
            panel.add(field, c);
            col += span;
            return this;
        }

        public Grid full(JComponent component) {
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
            c.insets = new Insets(14, 0, 0, 0);
            panel.add(component, c);
            newRow();
            return this;
        }

        public Grid newRow() {
            row++;
            col = 0;
            return this;
        }
    }
}
