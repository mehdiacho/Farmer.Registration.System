package bw.farmers.ui;

import bw.farmers.model.Farmer;
import bw.farmers.model.User;
import bw.farmers.service.FarmerService;
import bw.farmers.service.ValidationException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

/** A signed-in farmer's own page: registration status and their details, which they can update. */
public final class FarmerHomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final FarmerService service;
    private final User user;
    private final FarmerForm form = new FarmerForm();
    private final JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JLabel statusNote = Ui.subtitle(" ");
    private Farmer current;

    public FarmerHomePanel(FarmerService service, User user, Navigator nav) {
        super(new BorderLayout());
        this.service = service;
        this.user = user;

        statusRow.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(Ui.title("Welcome, " + user.displayName()));
        heading.add(Box.createVerticalStrut(8));
        heading.add(statusRow);
        heading.add(Box.createVerticalStrut(4));
        heading.add(statusNote);
        statusRow.setAlignmentX(LEFT_ALIGNMENT);
        statusNote.setAlignmentX(LEFT_ALIGNMENT);

        JButton save = Ui.primary("Save changes");
        JButton changePassword = new JButton("Change password");
        JButton signOut = Ui.link("Sign out");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(save);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(changePassword);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(signOut);

        JPanel card = Ui.card();
        new Ui.Grid(card, 1).full(heading).full(form).full(actions);
        JPanel centred = new JPanel(new GridBagLayout());
        Ui.padding(centred, 24, 24, 24, 24);
        centred.add(card);
        JScrollPane scroll = new JScrollPane(centred);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll);

        save.addActionListener(e -> {
            try {
                current = service.updateOwnProfile(user, form.read(current.status()));
                refresh();
                JOptionPane.showMessageDialog(this, "Your details were saved.", "Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException ex) {
                Ui.showProblems(this, ex);
            }
        });
        changePassword.addActionListener(e -> new ChangePasswordDialog(this, service, user).setVisible(true));
        signOut.addActionListener(e -> nav.showLogin());

        current = service.profileOf(user).orElse(null);
        if (current == null) {
            heading.add(Ui.subtitle("This account has no farmer profile."));
            form.setVisible(false);
            save.setEnabled(false);
        } else {
            refresh();
        }
    }

    private void refresh() {
        form.show(current);
        statusRow.removeAll();
        statusRow.add(new JLabel("Registration status:  "));
        statusRow.add(Ui.badge(current.status()));
        statusNote.setText(switch (current.status()) {
            case PENDING -> "The Ministry has not reviewed your registration yet.";
            case APPROVED -> "You are a registered farmer.";
            case DECLINED -> "Your registration was declined. Correct your details and save to send it for review again.";
        });
        statusRow.revalidate();
        statusRow.repaint();
    }
}
