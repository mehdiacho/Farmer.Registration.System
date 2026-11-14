package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

/** Lets a signed-in user change their password. */
final class ChangePasswordDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    ChangePasswordDialog(Component owner, FarmerService service, User user) {
        super(SwingUtilities.getWindowAncestor(owner), "Change password", ModalityType.APPLICATION_MODAL);
        JPasswordField current = new JPasswordField(20);
        JPasswordField next = new JPasswordField(20);
        JPasswordField confirm = new JPasswordField(20);

        JButton save = Ui.primary("Change password");
        JButton cancel = new JButton("Cancel");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(save);

        JPanel content = new JPanel(new GridBagLayout());
        Ui.padding(content, 16, 20, 16, 20);
        new Ui.Grid(content, 1)
                .field("Current password", current)
                .field("New password", next)
                .field("Confirm new password", confirm)
                .full(actions);
        setContentPane(content);
        getRootPane().setDefaultButton(save);

        save.addActionListener(e -> {
            char[] a = current.getPassword();
            char[] b = next.getPassword();
            char[] c = confirm.getPassword();
            try {
                service.changePassword(user, a, b, c);
                JOptionPane.showMessageDialog(this, "Your password was changed.");
                dispose();
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            } finally {
                PasswordHasher.wipe(a);
                PasswordHasher.wipe(b);
                PasswordHasher.wipe(c);
            }
        });
        cancel.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(owner);
    }
}
