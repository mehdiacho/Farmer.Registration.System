package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.Status;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

/** The farmer sign-up form. New registrations wait for an admin to approve them. */
public final class RegisterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final FarmerForm form = new FarmerForm();
    private final JPasswordField password = new JPasswordField(16);
    private final JPasswordField confirm = new JPasswordField(16);

    public RegisterPanel(FarmerService service, Navigator nav, Consumer<String> registered) {
        super(new BorderLayout());

        password.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "at least 8 characters");
        confirm.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(Ui.title("Register as a farmer"));
        heading.add(Box.createVerticalStrut(4));
        heading.add(Ui.subtitle("The Ministry reviews every registration. You can sign in to check its status."));

        JPanel passwords = new JPanel(new GridBagLayout());
        passwords.setOpaque(false);
        new Ui.Grid(passwords, 2).field("Password", password).field("Confirm password", confirm);

        JButton submit = Ui.primary("Submit registration");
        JButton back = Ui.link("Back to sign in");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(submit);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(back);

        JPanel card = Ui.card();
        new Ui.Grid(card, 1).full(heading).full(form).full(new JSeparator()).full(passwords).full(actions);

        JPanel centred = new JPanel(new GridBagLayout());
        Ui.padding(centred, 24, 24, 24, 24);
        centred.add(card);
        JScrollPane scroll = new JScrollPane(centred);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll);

        submit.addActionListener(e -> {
            char[] pw = password.getPassword();
            char[] pw2 = confirm.getPassword();
            try {
                var user = service.register(form.read(Status.PENDING), pw, pw2);
                JOptionPane.showMessageDialog(this,
                        "Thank you. Your registration is pending review.\nSign in at any time to check its status.",
                        "Registration received", JOptionPane.INFORMATION_MESSAGE);
                reset();
                registered.accept(user.email());
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            } finally {
                PasswordHasher.wipe(pw);
                PasswordHasher.wipe(pw2);
            }
        });
        back.addActionListener(e -> {
            reset();
            nav.showLogin();
        });
    }

    /** The form, so the screenshot tool can fill it in. */
    FarmerForm form() {
        return form;
    }

    private void reset() {
        password.setText("");
        confirm.setText("");
    }
}
