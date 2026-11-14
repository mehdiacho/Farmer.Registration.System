package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.Status;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.EnumMap;
import java.util.Map;

/** The admin's edit form for one farmer, including their registration status. */
final class EditFarmerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private boolean saved;

    EditFarmerDialog(Component owner, FarmerService service, Farmer farmer) {
        super(SwingUtilities.getWindowAncestor(owner), "Edit farmer", ModalityType.APPLICATION_MODAL);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", Ui.SURFACE);
        FarmerForm form = new FarmerForm();
        form.show(farmer);

        // Status as three coloured choices rather than a drop-down.
        Map<Status, JToggleButton> choices = new EnumMap<>(Status.class);
        ButtonGroup group = new ButtonGroup();
        JPanel statusRow = Ui.clear(new FlowLayout(FlowLayout.LEFT, 0, 0));
        for (Status s : Status.values()) {
            JToggleButton b = new JToggleButton(s.label(), Ui.icon(Ui.statusIcon(s), 16, Ui.statusColor(s)));
            b.setFont(Ui.semibold(13.5f));
            b.setIconTextGap(UIScale.scale(8));
            String soft = hex(Ui.statusSoft(s));
            String tone = hex(Ui.statusColor(s));
            b.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 10; margin: 8,16,8,16;"
                    + " background: #FFFFFF; borderColor: #D9CFBC; selectedBackground: " + soft + ";"
                    + " selectedForeground: " + tone + "; hoverBackground: #FBF8F2; focusWidth: 0");
            group.add(b);
            choices.put(s, b);
            statusRow.add(b);
            statusRow.add(javax.swing.Box.createHorizontalStrut(UIScale.scale(8)));
        }
        choices.get(farmer.status()).setSelected(true);

        JPanel content = Ui.clear(new GridBagLayout());
        new Ui.Grid(content, 1)
                .full(form, 0)
                .section("Registration", "Approve or decline, or put it back in the queue.", "check-circle")
                .full(statusRow, 14);
        Ui.padding(content, 0, 0, 0, 10);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UIScale.scale(16));
        int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height - UIScale.scale(320);
        Dimension pref = content.getPreferredSize();
        scroll.setPreferredSize(new Dimension(pref.width + UIScale.scale(16), Math.min(pref.height, maxHeight)));

        JButton save = Ui.primary("Save changes", "check");
        JButton cancel = Ui.secondary("Cancel", null);
        setContentPane(Dialogs.shell(farmer.fullName(), "ID " + farmer.idNumber() + "  ·  " + farmer.email(),
                "pencil", Ui.FOREST, scroll, cancel, save));
        getRootPane().setDefaultButton(save);

        save.addActionListener(e -> {
            Status chosen = choices.entrySet().stream().filter(en -> en.getValue().isSelected())
                    .map(Map.Entry::getKey).findFirst().orElse(farmer.status());
            try {
                service.adminUpdate(farmer.idNumber(), form.read(chosen));
                saved = true;
                dispose();
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            }
        });
        cancel.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(owner);
    }

    private static String hex(java.awt.Color c) {
        return String.format(java.util.Locale.ROOT, "#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    boolean saved() {
        return saved;
    }
}
