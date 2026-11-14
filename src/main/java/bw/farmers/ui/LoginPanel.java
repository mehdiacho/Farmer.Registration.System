package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.Districts;
import bw.farmers.model.Farmer;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Sign in with email and password. The account decides whether the admin or farmer screen opens. */
public final class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color ON_DARK = new Color(255, 248, 234, 215);

    private final transient FarmerService service;
    private final JTextField email = Ui.withIcon(new JTextField(22), "mail");
    private final JPasswordField password = Ui.withIcon(new JPasswordField(22), "lock");
    private final JLabel error = Ui.text(" ", Ui.font(Font.PLAIN, 13), Ui.CLAY);
    private final JLabel farmers = stat("0");
    private final JLabel hectares = stat("0");
    private final JLabel districts = stat(String.valueOf(Districts.all().size()));

    public LoginPanel(FarmerService service, Navigator nav) {
        super(new GridBagLayout());
        this.service = service;
        setBackground(Ui.SAND);

        email.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "name@example.com");
        password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Your password");

        Landscape hero = hero();
        JPanel form = form(nav);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.56;
        hero.setPreferredSize(new Dimension(10, 10));
        add(hero, c);
        c.weightx = 0.44;
        form.setPreferredSize(new Dimension(10, 10));
        form.setMinimumSize(new Dimension(UIScale.scale(420), 10));
        add(form, c);

        // On a narrow window the picture gives way to the form.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boolean wide = getWidth() >= UIScale.scale(860);
                if (hero.isVisible() != wide) {
                    hero.setVisible(wide);
                    revalidate();
                }
            }
        });
        refreshStats();
    }

    private Landscape hero() {
        Landscape hero = new Landscape(new GridBagLayout(), 0.95f, 0.70f);
        Ui.padding(hero, 40, 48, 44, 48);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel brand = Ui.text("FarmersBW", Ui.display(21), Ui.CREAM);
        brand.setIcon(Logo.icon(38));
        brand.setIconTextGap(UIScale.scale(12));
        hero.add(brand, c);

        c.weighty = 1;
        hero.add(Ui.clear(null), c);
        c.weighty = 0;

        JLabel eyebrow = Ui.text("MINISTRY OF AGRICULTURE  ·  FARMER REGISTER", Ui.semibold(12), new Color(0xF2C46B));
        eyebrow.setFont(eyebrow.getFont().deriveFont(java.util.Map.of(java.awt.font.TextAttribute.TRACKING, 0.08f)));
        hero.add(eyebrow, c);
        c.insets = new Insets(UIScale.scale(12), 0, 0, 0);
        hero.add(Ui.text(Dialogs.wrap("Every farm in Botswana, on one register.", 470), Ui.display(40),
                Color.WHITE), c);
        c.insets = new Insets(UIScale.scale(14), 0, 0, 0);
        hero.add(Ui.text(Dialogs.wrap("Farmers register once and follow their review here. The Ministry approves "
                + "registrations and reports on farmland in every district.", 470), Ui.font(Font.PLAIN, 16),
                ON_DARK), c);

        JPanel stats = Ui.clear(new GridBagLayout());
        GridBagConstraints s = new GridBagConstraints();
        s.anchor = GridBagConstraints.WEST;
        addStat(stats, s, 0, farmers, "farmers registered");
        addStat(stats, s, 1, hectares, "hectares of farmland");
        addStat(stats, s, 2, districts, "districts covered");
        c.insets = new Insets(UIScale.scale(32), 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        hero.add(stats, c);
        return hero;
    }

    private static JLabel stat(String value) {
        return Ui.text(value, Ui.display(28), Color.WHITE);
    }

    private static void addStat(JPanel stats, GridBagConstraints s, int i, JLabel value, String label) {
        JPanel block = Ui.clear(new GridBagLayout());
        GridBagConstraints b = new GridBagConstraints();
        b.gridx = 0;
        b.anchor = GridBagConstraints.WEST;
        block.add(value, b);
        block.add(Ui.text(label, Ui.font(Font.PLAIN, 13), ON_DARK), b);
        if (i > 0) {
            block.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(255, 248, 234, 70)),
                    BorderFactory.createEmptyBorder(0, UIScale.scale(24), 0, 0)));
        }
        s.gridx = i;
        s.insets = new Insets(0, 0, 0, UIScale.scale(24));
        stats.add(block, s);
    }

    private JPanel form(Navigator nav) {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(Ui.SAND);

        JButton signIn = Ui.primary("Sign in");
        JButton register = Ui.secondary("Register your farm", "sprout");
        error.setIconTextGap(UIScale.scale(8));

        JPanel col = Ui.clear(new GridBagLayout());
        new Ui.Grid(col, 1)
                .full(Ui.title("Welcome back"), 0)
                .full(Ui.subtitle("<html>Sign in to see your registration,<br>or to review applications.</html>"), 6)
                .field("Email address", email)
                .field("Password", password)
                .full(error, 10)
                .full(signIn, 10)
                .full(Widgets.divider("New to FarmersBW?"), 28)
                .full(register, 16);
        col.setPreferredSize(new Dimension(UIScale.scale(360), col.getPreferredSize().height));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weighty = 1;
        side.add(col, c);
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(0, 0, UIScale.scale(22), 0);
        side.add(Ui.small("FarmersBW 2.0  ·  Farmer register for the Ministry of Agriculture"), c);

        Runnable attempt = () -> {
            char[] pw = password.getPassword();
            try {
                Optional<User> user = service.signIn(email.getText(), pw);
                if (user.isPresent()) {
                    clear();
                    nav.showHome(user.get());
                } else {
                    showError("That email and password do not match an account.");
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
        return side;
    }

    private void showError(String message) {
        error.setText(message);
        error.setIcon(Ui.icon("x-circle", 16, Ui.CLAY));
        password.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
    }

    /** Recounts the figures shown over the picture. */
    void refreshStats() {
        List<Farmer> all = service.list(ReportFilter.ALL);
        farmers.setText(String.format(Locale.ROOT, "%,d", all.size()));
        hectares.setText(String.format(Locale.ROOT, "%,.0f", all.stream().mapToDouble(Farmer::farmSize).sum()));
    }

    public void clear() {
        password.setText("");
        error.setText(" ");
        error.setIcon(null);
        password.putClientProperty(FlatClientProperties.OUTLINE, null);
        refreshStats();
    }

    /** Pre-fills the email, e.g. right after a successful sign-up. */
    public void prefill(String address) {
        email.setText(address);
        password.requestFocusInWindow();
    }
}
