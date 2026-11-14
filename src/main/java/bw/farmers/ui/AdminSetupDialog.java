package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

/**
 * Shown on first run, when the database has no admin yet. The person installing the app
 * chooses the admin login, so no default password ships with it.
 */
public final class AdminSetupDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private User created;

    public AdminSetupDialog(Window owner, FarmerService service) {
        super(owner, "Set up FarmersBW", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextField name = new JTextField(22);
        JTextField email = new JTextField(22);
        JPasswordField password = new JPasswordField(22);
        JPasswordField confirm = new JPasswordField(22);

        JButton create = Ui.primary("Create admin account");
        JButton quit = new JButton("Quit");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(quit);
        actions.add(create);

        JPanel content = new JPanel(new GridBagLayout());
        Ui.padding(content, 16, 20, 16, 20);
        new Ui.Grid(content, 1)
                .full(Ui.title("Create the first admin"))
                .full(Ui.subtitle("<html>There is no admin account yet. The admin approves registrations<br>"
                        + "and prints reports. Choose a strong password.</html>"))
                .field("Your name", name)
                .field("Email address", email)
                .field("Password", password)
                .field("Confirm password", confirm)
                .full(actions);
        setContentPane(content);
        getRootPane().setDefaultButton(create);

        create.addActionListener(e -> {
            char[] a = password.getPassword();
            char[] b = confirm.getPassword();
            try {
                created = service.createAdmin(name.getText(), email.getText(), a, b);
                dispose();
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            } finally {
                PasswordHasher.wipe(a);
                PasswordHasher.wipe(b);
            }
        });
        quit.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(owner);
    }

    /** The admin created, or {@code null} if the dialog was closed without one. */
    public User created() {
        return created;
    }
}
