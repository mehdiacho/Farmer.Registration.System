package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
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
        this(owner, service, null);
    }

    /**
     * @param addSampleFarmers if not null, the dialog offers to fill the empty register with fictional
     *                         farmers, and runs this when the admin is created with the box ticked
     */
    public AdminSetupDialog(Window owner, FarmerService service, Runnable addSampleFarmers) {
        super(owner, "Set up FarmersBW", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImages(Logo.windowIcons());
        getRootPane().putClientProperty("JRootPane.titleBarBackground", Ui.FOREST_DEEP);
        getRootPane().putClientProperty("JRootPane.titleBarForeground", Ui.CREAM);

        JTextField name = Ui.withIcon(new JTextField(18), "user");
        JTextField email = Ui.withIcon(new JTextField(18), "mail");
        JPasswordField password = Ui.withIcon(new JPasswordField(18), "lock");
        JPasswordField confirm = Ui.withIcon(new JPasswordField(18), "lock");
        name.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Kabo Molefe");
        email.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "you@agriculture.gov.bw");
        password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "At least 8 characters");
        confirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Type it again");

        JCheckBox sample = new JCheckBox("Add 40 fictional sample farmers, to try out the admin screens");
        sample.setFont(Ui.font(Font.PLAIN, 13.5f));
        sample.setForeground(Ui.INK);
        sample.setOpaque(false);
        sample.setSelected(true);

        JPanel form = Ui.clear(new GridBagLayout());
        Ui.Grid grid = new Ui.Grid(form, 2)
                .field("Your name", name)
                .field("Email address", email)
                .field("Password", password)
                .field("Confirm password", confirm);
        if (addSampleFarmers != null) {
            grid.full(sample, 20);
        }

        JButton create = Ui.primary("Create admin account", "check");
        JButton quit = Ui.secondary("Quit", null);
        JPanel body = Dialogs.shell("Welcome. Create the first admin.",
                "This only happens once. The admin reviews registrations and prints reports. There is no default password: choose a "
                        + "strong one, and keep it safe.", "key", Ui.FOREST, form, quit, create);

        JPanel root = new JPanel(new BorderLayout());
        root.add(banner(), BorderLayout.NORTH);
        root.add(body);
        setContentPane(root);
        getRootPane().setDefaultButton(create);

        create.addActionListener(e -> {
            char[] a = password.getPassword();
            char[] b = confirm.getPassword();
            try {
                created = service.createAdmin(name.getText(), email.getText(), a, b);
                if (addSampleFarmers != null && sample.isSelected()) {
                    addSampleFarmers.run();
                }
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
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private static JPanel banner() {
        Landscape banner = new Landscape(new GridBagLayout(), 0.9f, 0.72f);
        banner.setPreferredSize(new Dimension(UIScale.scale(600), UIScale.scale(150)));
        Ui.padding(banner, 20, 26, 20, 26);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        JLabel brand = Ui.text("FarmersBW", Ui.display(17), Ui.CREAM);
        brand.setIcon(Logo.icon(32));
        brand.setIconTextGap(UIScale.scale(10));
        banner.add(brand, c);
        c.weighty = 1;
        banner.add(Ui.clear(null), c);
        return banner;
    }

    /** The admin created, or {@code null} if the dialog was closed without one. */
    public User created() {
        return created;
    }
}
