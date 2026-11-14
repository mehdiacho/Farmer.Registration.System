package bw.farmers.ui;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;

/** Dialogs in the app's own style: a header with an icon tile, the content, and a footer of buttons. */
final class Dialogs {

    private Dialogs() {
    }

    static JDialog create(Component owner, String windowTitle) {
        Window w = owner instanceof Window win ? win : owner == null ? null : SwingUtilities.getWindowAncestor(owner);
        JDialog d = new JDialog(w, windowTitle, JDialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.getRootPane().putClientProperty("JRootPane.titleBarBackground", Ui.SURFACE);
        return d;
    }

    /** Header, optional content and footer buttons, laid out as one dialog body. */
    static JPanel shell(String title, String subtitle, String iconName, Color tone, JComponent content,
                        JButton... buttons) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Ui.SURFACE);

        JPanel header = Ui.clear(new GridBagLayout());
        Ui.padding(header, 22, 28, content == null ? 22 : 6, 28);
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 0, UIScale.scale(16));
        JLabel tile = new JLabel(Ui.icon(iconName, 22, tone));
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setPreferredSize(new Dimension(UIScale.scale(44), UIScale.scale(44)));
        header.add(new Ui.Surface(new BorderLayout(), soft(tone), null, 12).with(tile), c);
        c.gridx = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(UIScale.scale(1), 0, UIScale.scale(4), 0);
        header.add(Ui.heading(title, 19), c);
        if (subtitle != null) {
            c.gridy = 1;
            c.insets = new Insets(0, 0, 0, 0);
            header.add(Ui.subtitle(wrap(subtitle, 380)), c);
        }
        root.add(header, BorderLayout.NORTH);

        if (content != null) {
            JPanel body = Ui.clear(new BorderLayout());
            Ui.padding(body, 8, 28, 24, 28);
            body.add(content);
            root.add(body);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIScale.scale(10), 0));
        footer.setBackground(Ui.SURFACE_ALT);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDER),
                BorderFactory.createEmptyBorder(UIScale.scale(14), UIScale.scale(20), UIScale.scale(14),
                        UIScale.scale(22))));
        for (JButton b : buttons) {
            footer.add(b);
        }
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    static void problems(Component parent, List<String> problems) {
        JPanel list = Ui.clear(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        for (String p : problems) {
            JLabel l = Ui.text(p, Ui.font(java.awt.Font.PLAIN, 14), Ui.INK);
            l.setIcon(Ui.icon("x", 15, Ui.CLAY));
            l.setIconTextGap(UIScale.scale(10));
            c.insets = new Insets(UIScale.scale(5), UIScale.scale(4), UIScale.scale(5), 0);
            list.add(l, c);
        }
        JDialog d = create(parent, "Check the form");
        JButton ok = Ui.primary("Fix it");
        ok.addActionListener(e -> d.dispose());
        String sub = problems.size() == 1 ? "One thing needs fixing before this can be saved."
                : problems.size() + " things need fixing before this can be saved.";
        show(d, shell("Check the form", sub, "alert", Ui.CLAY, list, ok), ok, parent);
    }

    static void message(Component parent, String title, String message, String iconName, Color tone) {
        JDialog d = create(parent, title);
        JButton ok = Ui.primary("OK");
        ok.addActionListener(e -> d.dispose());
        show(d, shell(title, message, iconName, tone, null, ok), ok, parent);
    }

    /** Asks a yes/no question. Returns true if the person chose {@code okText}. */
    static boolean confirm(Component parent, String title, String message, String okText, boolean danger) {
        JDialog d = create(parent, title);
        boolean[] answer = {false};
        JButton ok = danger ? dangerSolid(okText) : Ui.primary(okText);
        JButton cancel = Ui.secondary("Cancel", null);
        ok.addActionListener(e -> {
            answer[0] = true;
            d.dispose();
        });
        cancel.addActionListener(e -> d.dispose());
        show(d, shell(title, message, danger ? "trash" : "alert", danger ? Ui.CLAY : Ui.GOLD_INK, null, cancel, ok),
                cancel, parent);
        return answer[0];
    }

    static JButton dangerSolid(String text) {
        JButton b = Ui.primary(text);
        b.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "background: #B4472C; foreground: #FFFFFF;"
                + " borderWidth: 0; hoverBackground: #9A3A22; pressedBackground: #82301B; margin: 10,20,10,20");
        return b;
    }

    private static void show(JDialog d, JPanel body, JButton defaultButton, Component parent) {
        d.setContentPane(body);
        d.getRootPane().setDefaultButton(defaultButton);
        d.pack();
        d.setResizable(false);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    static String wrap(String text, int width) {
        return "<html><div style='width:" + UIScale.scale(width) + "px'>" + Ui.escape(text).replace("\n", "<br>")
                + "</div></html>";
    }

    static Color soft(Color tone) {
        if (tone.equals(Ui.CLAY)) {
            return Ui.CLAY_SOFT;
        }
        if (tone.equals(Ui.GOLD_INK) || tone.equals(Ui.GOLD)) {
            return Ui.GOLD_SOFT;
        }
        return Ui.FOREST_SOFT;
    }
}
