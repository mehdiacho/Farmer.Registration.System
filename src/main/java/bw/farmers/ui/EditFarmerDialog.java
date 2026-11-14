package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.Status;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

/** The admin's edit form for one farmer, including their registration status. */
final class EditFarmerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private boolean saved;

    EditFarmerDialog(Component owner, FarmerService service, Farmer farmer) {
        super(SwingUtilities.getWindowAncestor(owner), "Edit " + farmer.fullName(), ModalityType.APPLICATION_MODAL);
        FarmerForm form = new FarmerForm();
        form.show(farmer);
        JComboBox<Status> status = new JComboBox<>(Status.values());
        status.setSelectedItem(farmer.status());

        JButton save = Ui.primary("Save");
        JButton cancel = new JButton("Cancel");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(save);

        JPanel content = new JPanel(new GridBagLayout());
        Ui.padding(content, 16, 20, 16, 20);
        new Ui.Grid(content, 2)
                .full(form)
                .field("Registration status", status)
                .full(actions);
        setContentPane(content);

        save.addActionListener(e -> {
            try {
                service.adminUpdate(farmer.idNumber(), form.read((Status) status.getSelectedItem()));
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

    boolean saved() {
        return saved;
    }
}
