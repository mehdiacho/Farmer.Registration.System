package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GridBagLayout;

/** Lets a signed-in user change their password. */
final class ChangePasswordDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    ChangePasswordDialog(Component owner, FarmerService service, User user) {
        super(SwingUtilities.getWindowAncestor(owner), "Change password", ModalityType.APPLICATION_MODAL);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", Ui.SURFACE);
        JPasswordField current = Ui.withIcon(new JPasswordField(20), "lock");
        JPasswordField next = Ui.withIcon(new JPasswordField(20), "key");
        JPasswordField confirm = Ui.withIcon(new JPasswordField(20), "key");
        next.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "At least 8 characters");
        confirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Type it again");

        JPanel form = Ui.clear(new GridBagLayout());
        new Ui.Grid(form, 1)
                .field("Current password", current)
                .field("New password", next)
                .field("Confirm new password", confirm);

        JButton save = Ui.primary("Change password", "check");
        JButton cancel = Ui.secondary("Cancel", null);
        setContentPane(Dialogs.shell("Change your password", "You will use the new password the next time you "
                + "sign in.", "lock", Ui.FOREST, form, cancel, save));
        getRootPane().setDefaultButton(save);

        save.addActionListener(e -> {
            char[] a = current.getPassword();
            char[] b = next.getPassword();
            char[] c = confirm.getPassword();
            try {
                service.changePassword(user, a, b, c);
                dispose();
                Widgets.toast(owner, "Your password was changed");
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
        setResizable(false);
        setLocationRelativeTo(owner);
    }
}
