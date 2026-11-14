package bw.farmers.ui;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.model.Status;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;

/** The farmer sign-up form. New registrations wait for an admin to approve them. */
public final class RegisterPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color ON_DARK = new Color(255, 248, 234, 210);

    private final FarmerForm form = new FarmerForm();
    private final JPasswordField password = Ui.withIcon(new JPasswordField(16), "lock");
    private final JPasswordField confirm = Ui.withIcon(new JPasswordField(16), "lock");

    public RegisterPanel(FarmerService service, Navigator nav, Consumer<String> registered) {
        super(new BorderLayout());
        setBackground(Ui.SAND);

        password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "At least 8 characters");
        confirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Type it again");

        Landscape side = side();
        add(side, BorderLayout.WEST);

        JButton back = Ui.ghost("Back to sign in", "arrow-left", Ui.FOREST);
        JPanel top = Ui.clear(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.add(back);

        JPanel passwords = Ui.clear(new GridBagLayout());
        new Ui.Grid(passwords, 2).field("Password", password).field("Confirm password", confirm);

        JButton submit = Ui.primary("Submit registration", "check");
        JPanel actions = Ui.clear(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.add(submit);
        actions.add(javax.swing.Box.createHorizontalStrut(UIScale.scale(16)));
        actions.add(Ui.small("Your registration goes to the Ministry for review."));

        JPanel card = Ui.card(new GridBagLayout(), 28, 32);
        new Ui.Grid(card, 2)
                .full(form, 0)
                .section("Account", "You will sign in with your email and this password.", "key")
                .full(passwords, 0)
                .full(Widgets.rule(), 30)
                .full(actions, 22);

        Widgets.Column column = new Widgets.Column(820, 28, 36);
        column.block(top, 0)
                .block(Ui.title("Your details"), 12)
                .block(Ui.subtitle("Every field is needed. You can change them later from your profile."), 6)
                .block(card, 24);
        JScrollPane scroll = new JScrollPane(column);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Ui.SAND);
        add(scroll);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                side.setVisible(getWidth() >= UIScale.scale(1000));
            }
        });

        submit.addActionListener(e -> {
            char[] pw = password.getPassword();
            char[] pw2 = confirm.getPassword();
            try {
                var user = service.register(form.read(Status.PENDING), pw, pw2);
                reset();
                Dialogs.message(this, "Registration received",
                        "Thank you. The Ministry will review your registration. Sign in at any time to see where it "
                                + "stands.", "check-circle", Ui.FOREST);
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

    private static Landscape side() {
        Landscape side = new Landscape(new GridBagLayout(), 1f, 0.78f);
        side.setPreferredSize(new Dimension(UIScale.scale(400), 10));
        Ui.padding(side, 36, 36, 40, 36);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel brand = Ui.text("FarmersBW", Ui.display(19), Ui.CREAM);
        brand.setIcon(Logo.icon(34));
        brand.setIconTextGap(UIScale.scale(12));
        side.add(brand, c);
        c.weighty = 1;
        side.add(Ui.clear(null), c);
        c.weighty = 0;
        side.add(Ui.text(Dialogs.wrap("Register your farm", 300), Ui.display(32), Color.WHITE), c);
        c.insets = new Insets(UIScale.scale(8), 0, UIScale.scale(18), 0);
        side.add(Ui.text(Dialogs.wrap("It takes about five minutes. Here is what happens next.", 240),
                Ui.font(Font.PLAIN, 15), ON_DARK), c);
        String[][] steps = {
                {"Fill in your details", "About you, how to reach you, and your farm."},
                {"The Ministry reviews it", "An administrator checks and approves it."},
                {"Sign in to see the decision", "Keep your details current at any time."},
        };
        for (int i = 0; i < steps.length; i++) {
            JLabel step = Ui.text("<html><div style='width:" + UIScale.scale(200) + "px'><b>" + steps[i][0]
                    + "</b><br><span style='color:#E8DFCC'>" + steps[i][1] + "</span></div></html>", Ui.font(Font.PLAIN, 14), Color.WHITE);
            step.setIcon(new StepIcon(i + 1));
            step.setIconTextGap(UIScale.scale(14));
            step.setVerticalTextPosition(JLabel.TOP);
            c.insets = new Insets(UIScale.scale(14), 0, 0, 0);
            side.add(step, c);
        }
        return side;
    }

    /** A gold numbered circle for the steps list. */
    private static final class StepIcon implements Icon {
        private final String n;
        private final int size = UIScale.scale(30);

        StepIcon(int n) {
            this.n = String.valueOf(n);
        }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            Ui.antialias(g);
            g.setColor(new Color(0xF2B33D));
            g.fill(new Ellipse2D.Float(x, y, size, size));
            g.setColor(Ui.FOREST_DEEP);
            g.setFont(Ui.semibold(14));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(n, x + (size - fm.stringWidth(n)) / 2f, y + (size - fm.getHeight()) / 2f + fm.getAscent());
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
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
