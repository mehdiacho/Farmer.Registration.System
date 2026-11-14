package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.Status;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;

/** A signed-in farmer's own page: registration status and their details, which they can update. */
public final class FarmerHomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient FarmerService service;
    private final transient User user;
    private final FarmerForm form = new FarmerForm();
    private final Ui.Pill pill = Ui.badge(Status.PENDING);
    private final Charts.Stepper stepper = new Charts.Stepper();
    private final JLabel statusTitle = Ui.heading(" ", 19);
    private final JLabel statusNote = Ui.subtitle(" ");
    private final JLabel farmSize = Ui.heading(" ", 28);
    private final JLabel farmingArea = Ui.heading(" ", 28);
    private final JLabel where = Ui.small(" ");
    private final JLabel usage = Ui.small(" ");
    private final Charts.UsageBar usageBar = new Charts.UsageBar();
    private transient Farmer current;

    public FarmerHomePanel(FarmerService service, User user, Navigator nav) {
        super(new BorderLayout());
        this.service = service;
        this.user = user;
        setBackground(Ui.SAND);

        JButton signOut = Ui.ghost("Sign out", "logout", Ui.MUTED);
        signOut.addActionListener(e -> nav.showLogin());
        add(appBar(user, signOut), BorderLayout.NORTH);

        JPanel main = Ui.clear(new BorderLayout());
        main.add(banner(user), BorderLayout.NORTH);

        JButton save = Ui.primary("Save changes", "check");
        JButton changePassword = Ui.secondary("Change password", "key");
        JPanel actions = Ui.clear(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.add(save);
        actions.add(javax.swing.Box.createHorizontalStrut(UIScale.scale(10)));
        actions.add(changePassword);

        JPanel details = Ui.card(new GridBagLayout(), 28, 32);
        new Ui.Grid(details, 1)
                .full(Ui.heading("Your details", 20), 0)
                .full(Ui.subtitle("If anything changes, update it here and save."), 4)
                .full(form, 22)
                .full(Widgets.rule(), 30)
                .full(actions, 22);

        Widgets.Column column = new Widgets.Column(1040, 24, 32);
        column.block(summaryRow(), 0).block(details, 20);
        JScrollPane scroll = new JScrollPane(column);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Ui.SAND);
        main.add(scroll);
        add(main);

        save.addActionListener(e -> {
            try {
                Status before = current.status();
                current = service.updateOwnProfile(user, form.read(current.status()));
                refresh();
                Widgets.toast(this, before == Status.DECLINED && current.status() == Status.PENDING
                        ? "Saved and sent for review again" : "Your details were saved");
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            }
        });
        changePassword.addActionListener(e -> new ChangePasswordDialog(this, service, user).setVisible(true));

        current = service.profileOf(user).orElse(null);
        if (current == null) {
            statusTitle.setText("No farmer profile");
            statusNote.setText("This account has no farmer profile.");
            form.setVisible(false);
            save.setEnabled(false);
        } else {
            refresh();
        }
    }

    /** The white bar across the top: the brand, who is signed in, and sign out. */
    static JPanel appBar(User user, JButton signOut) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Ui.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDER),
                BorderFactory.createEmptyBorder(UIScale.scale(10), UIScale.scale(24), UIScale.scale(10),
                        UIScale.scale(16))));
        JLabel brand = Ui.text("FarmersBW", Ui.display(17), Ui.INK);
        brand.setIcon(Logo.icon(30));
        brand.setIconTextGap(UIScale.scale(10));
        bar.add(brand, BorderLayout.WEST);

        JPanel right = Ui.clear(new FlowLayout(FlowLayout.RIGHT, UIScale.scale(12), 0));
        JLabel who = Ui.text("<html>" + Ui.escape(user.displayName()) + "<br><span style='color:#5E6A60'>"
                + Ui.escape(user.email()) + "</span></html>", Ui.semibold(13), Ui.INK);
        who.setIcon(Ui.avatar(user.displayName(), 34));
        who.setIconTextGap(UIScale.scale(10));
        right.add(who);
        right.add(signOut);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JComponent banner(User user) {
        Landscape banner = new Landscape(new GridBagLayout(), 0.9f, 0.82f);
        banner.setPreferredSize(new java.awt.Dimension(10, UIScale.scale(190)));
        Ui.padding(banner, 24, 40, 28, 40);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.SOUTHWEST;
        String first = user.displayName().strip().split("\\s+")[0];
        banner.add(Ui.text("Dumela, " + first, Ui.display(34), Color.WHITE), c);
        c.weighty = 0;
        c.insets = new Insets(UIScale.scale(6), 0, 0, 0);
        banner.add(Ui.text("Here is where your farm registration stands.", Ui.font(Font.PLAIN, 15.5f),
                new Color(255, 248, 234, 215)), c);
        return banner;
    }

    private JPanel summaryRow() {
        JPanel status = Ui.card(new GridBagLayout(), 22, 26);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel head = Ui.clear(new BorderLayout());
        head.add(Ui.text("Registration", Ui.semibold(13.5f), Ui.MUTED), BorderLayout.WEST);
        head.add(pill, BorderLayout.EAST);
        status.add(head, c);
        c.insets = new Insets(UIScale.scale(8), 0, 0, 0);
        status.add(statusTitle, c);
        c.insets = new Insets(UIScale.scale(2), 0, 0, 0);
        status.add(statusNote, c);
        c.insets = new Insets(UIScale.scale(22), 0, 0, 0);
        status.add(stepper, c);
        c.weighty = 1;
        status.add(Ui.clear(null), c);

        JPanel farm = Ui.card(new GridBagLayout(), 22, 26);
        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 0;
        f.gridwidth = 2;
        f.anchor = GridBagConstraints.WEST;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        farm.add(Ui.text("Your farm", Ui.semibold(13.5f), Ui.MUTED), f);
        where.setIcon(Ui.icon("pin", 15, Ui.MUTED));
        where.setIconTextGap(UIScale.scale(6));
        f.insets = new Insets(UIScale.scale(6), 0, 0, 0);
        farm.add(where, f);
        f.gridwidth = 1;
        f.gridy = 2;
        f.insets = new Insets(UIScale.scale(16), 0, 0, 0);
        farm.add(farmSize, f);
        f.gridx = 1;
        farm.add(farmingArea, f);
        f.gridy = 3;
        f.gridx = 0;
        f.insets = new Insets(0, 0, 0, 0);
        farm.add(Ui.small("farm size"), f);
        f.gridx = 1;
        farm.add(Ui.small("farmed"), f);
        f.gridy = 4;
        f.gridx = 0;
        f.gridwidth = 2;
        f.insets = new Insets(UIScale.scale(18), 0, 0, 0);
        farm.add(usageBar, f);
        f.gridy = 5;
        f.insets = new Insets(UIScale.scale(8), 0, 0, 0);
        farm.add(usage, f);
        f.gridy = 6;
        f.weighty = 1;
        farm.add(Ui.clear(null), f);

        JPanel row = Ui.clear(new GridBagLayout());
        GridBagConstraints r = new GridBagConstraints();
        r.fill = GridBagConstraints.BOTH;
        r.weighty = 1;
        r.weightx = 0.6;
        r.insets = new Insets(0, 0, 0, UIScale.scale(20));
        row.add(status, r);
        r.weightx = 0.4;
        r.insets = new Insets(0, 0, 0, 0);
        row.add(farm, r);
        return row;
    }

    private void refresh() {
        form.show(current);
        Status s = current.status();
        pill.setStatus(s);
        stepper.setStatus(s);
        statusTitle.setText(switch (s) {
            case PENDING -> "Waiting for review";
            case APPROVED -> "You are a registered farmer";
            case DECLINED -> "Your registration was declined";
        });
        statusNote.setText(Dialogs.wrap(switch (s) {
            case PENDING -> "The Ministry has your registration and will review it. Nothing to do for now.";
            case APPROVED -> "Your registration is approved. Keep your details up to date below.";
            case DECLINED -> "Correct your details below and save. That sends it for review again.";
        }, 440));
        where.setText(current.district() + "  ·  " + current.subDistrict());
        farmSize.setText(String.format(Locale.ROOT, "%,.0f ha", current.farmSize()));
        farmingArea.setText(String.format(Locale.ROOT, "%,.0f ha", current.farmingArea()));
        double used = current.farmSize() > 0 ? current.farmingArea() / current.farmSize() : 0;
        usageBar.setFraction(used);
        usage.setText(String.format(Locale.ROOT, "%d%% of the farm is available for farming", Math.round(used * 100)));
        revalidate();
        repaint();
    }

    User user() {
        return user;
    }
}
