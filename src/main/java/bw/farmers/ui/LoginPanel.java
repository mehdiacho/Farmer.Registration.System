package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.Optional;

/** Sign in with email and password. The account decides whether the admin or farmer screen opens. */
public final class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField email = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);
    private final JLabel error = new JLabel(" ");

    public LoginPanel(FarmerService service, Navigator nav) {
        super(new GridBagLayout());

        email.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "name@example.com");
        password.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        error.setForeground(Ui.statusColor(bw.farmers.model.Status.DECLINED));

        JButton signIn = Ui.primary("Sign in");
        JButton register = Ui.link("New farmer? Register here");

        JPanel card = Ui.card();
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS));
        heading.add(Ui.title("FarmersBW"));
        heading.add(Box.createVerticalStrut(4));
        heading.add(Ui.subtitle("A university project, not an official Ministry system"));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(signIn);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(register);

        new Ui.Grid(card, 1)
                .full(heading)
                .field("Email address", email)
                .field("Password", password)
                .full(error)
                .full(actions);
        add(card);

        Runnable attempt = () -> {
            char[] pw = password.getPassword();
            try {
                Optional<User> user = service.signIn(email.getText(), pw);
                if (user.isPresent()) {
                    clear();
                    nav.showHome(user.get());
                } else {
                    error.setText("That email and password do not match an account.");
                    password.selectAll();
                    password.requestFocusInWindow();
                }
            } finally {
                PasswordHasher.wipe(pw);
            }
        };
        signIn.addActionListener(e -> attempt.run());
        password.addActionListener(e -> attempt.run());
        email.addActionListener(e -> password.requestFocusInWindow());
        register.addActionListener(e -> {
            clear();
            nav.showRegister();
        });
    }

    public void clear() {
        password.setText("");
        error.setText(" ");
    }

    /** Pre-fills the email, e.g. right after a successful sign-up. */
    public void prefill(String address) {
        email.setText(address);
        password.requestFocusInWindow();
    }
}
