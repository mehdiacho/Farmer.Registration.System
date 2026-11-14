package bw.farmers.ui;

import bw.farmers.Fixtures;
import bw.farmers.auth.PasswordHasher;
import bw.farmers.db.Database;
import bw.farmers.demo.DemoData;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;
import bw.farmers.model.User;
import bw.farmers.report.ReportWriter;
import bw.farmers.service.FarmerService;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Renders each screen with fictional demo data into PNGs for the README.
 * Run with {@code ./gradlew screenshots}; needs a desktop session (not headless).
 */
public final class Screenshots {

    private static final int SCALE = 2;

    private Screenshots() {
    }

    public static void main(String[] args) throws Exception {
        Path out = Path.of(args.length > 0 ? args[0] : "docs/screenshots");
        Files.createDirectories(out);
        SwingUtilities.invokeAndWait(() -> {
            try {
                run(out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.exit(0);
    }

    private static void run(Path out) throws Exception {
        // Scale the whole UI 2x so text is crisp; layout and painting then use the same font metrics.
        System.setProperty("flatlaf.uiScale", String.valueOf(SCALE));
        Ui.installLookAndFeel();
        Database db = Database.inMemory();
        FarmerService service = new FarmerService(db, new PasswordHasher(1_000), Fixtures.CLOCK);
        DemoData.seedIfEmpty(db, 40, 2022L);
        User admin = service.createAdmin("Ministry Admin", "admin@example.com", Fixtures.pw(), Fixtures.pw());
        User farmer = service.register(Fixtures.farmer("204518337", "naledi.sebina@example.com", "74512983"),
                Fixtures.pw(), Fixtures.pw());

        Navigator nav = new Navigator() {
            @Override
            public void showLogin() {
            }

            @Override
            public void showRegister() {
            }

            @Override
            public void showHome(User user) {
            }
        };

        shoot(banner(), 1280, 360, out.resolve("banner.png"));

        LoginPanel login = new LoginPanel(service, nav);
        login.prefill("admin@example.com");
        shoot(login, 1280, 800, out.resolve("login.png"));

        RegisterPanel register = new RegisterPanel(service, nav, email -> { });
        Farmer f = Fixtures.farmer("310992458", "thabo.kgosi@example.com", "76234519");
        register.form().show(new Farmer(f.idNumber(), "Thabo", "Kgosi", f.contact(), LocalDate.of(1984, 3, 9),
                Gender.MALE, "Private Bag 12, Tutume", "Central", "Tutume", f.email(), 60, 55, Status.PENDING));
        shoot(register, 1280, 900, out.resolve("register.png"));

        shoot(new FarmerHomePanel(service, farmer, nav), 1280, 900, out.resolve("farmer-profile.png"));

        AdminPanel adminPanel = new AdminPanel(service, admin, nav);
        adminPanel.selectTab(0);
        shoot(adminPanel, 1360, 1080, out.resolve("admin-overview.png"));

        adminPanel.selectTab(1);
        adminPanel.registrationsTable().setRowSelectionInterval(1, 2);
        shoot(adminPanel, 1360, 860, out.resolve("admin-registrations.png"));

        adminPanel.selectTab(2);
        adminPanel.reportDistrict("Central");
        shoot(adminPanel, 1360, 860, out.resolve("admin-reports.png"));

        Database fresh = Database.inMemory();
        AdminSetupDialog setup = new AdminSetupDialog(null, new FarmerService(fresh), () -> { });
        JComponent setupBody = (JComponent) setup.getContentPane();
        java.awt.Dimension size = setupBody.getPreferredSize();
        setup.dispose();
        shoot(setupBody, size.width / SCALE, size.height / SCALE, out.resolve("first-run.png"));
        fresh.close();

        // A sample of the plain-text report the admin exports.
        ReportFilter central = new ReportFilter("Central", null, null, null);
        ReportWriter.write(out.getParent().resolve("sample-report.txt"), service.list(central), central,
                LocalDateTime.of(2026, 11, 14, 10, 30));
        db.close();
    }

    /** The README's header picture: the landscape with the name over it. */
    private static JComponent banner() {
        Landscape banner = new Landscape(new java.awt.GridBagLayout(), 0.95f, 0.22f);
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        c.insets = new java.awt.Insets(0, 0, 0, 64 * SCALE);
        javax.swing.JLabel name = Ui.text("FarmersBW", Ui.display(54), java.awt.Color.WHITE);
        name.setIcon(Logo.icon(64));
        name.setIconTextGap(18 * SCALE);
        banner.add(name, c);
        c.weighty = 0;
        c.anchor = java.awt.GridBagConstraints.NORTHEAST;
        c.insets = new java.awt.Insets(6 * SCALE, 0, 40 * SCALE, 64 * SCALE);
        banner.add(Ui.text("Every farm in Botswana, on one register.", Ui.font(java.awt.Font.PLAIN, 21),
                new java.awt.Color(255, 248, 234, 225)), c);
        return banner;
    }

    private static void shoot(JComponent screen, int width, int height, Path file) throws Exception {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setContentPane(screen);
        width *= SCALE;
        height *= SCALE;
        frame.setSize(width, height);
        // A real (but off-screen) window, so text is measured exactly as it is painted.
        frame.setLocation(-20_000, -20_000);
        frame.setVisible(true);
        frame.validate();
        layoutAll(screen);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        screen.paint(g); // not printAll: print mode measures text differently and clips it
        g.dispose();
        ImageIO.write(img, "png", new File(file.toString()));
        frame.dispose();
        System.out.println("wrote " + file);
    }

    private static void layoutAll(java.awt.Container c) {
        c.doLayout();
        for (java.awt.Component child : c.getComponents()) {
            if (child instanceof java.awt.Container cc) {
                layoutAll(cc);
            }
        }
    }
}
