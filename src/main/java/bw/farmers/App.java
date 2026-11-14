package bw.farmers;

import bw.farmers.db.Database;
import bw.farmers.demo.DemoData;
import bw.farmers.service.FarmerService;
import bw.farmers.ui.AdminSetupDialog;
import bw.farmers.ui.MainWindow;
import bw.farmers.ui.Ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.nio.file.Path;

/**
 * FarmersBW: register farmers, review registrations and print reports.
 *
 * <pre>
 * Usage: farmersbw [--db FILE] [--demo]
 *   --db FILE   database file (default: ~/.farmersbw/farmersbw.db, or $FARMERSBW_DB)
 *   --demo      add fictional farmers to an empty database, to try the admin screens
 * </pre>
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        Path dbFile = defaultDatabase();
        boolean demo = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--db" -> {
                    if (i + 1 >= args.length) {
                        usage("--db needs a file path");
                    }
                    dbFile = Path.of(args[++i]);
                }
                case "--demo" -> demo = true;
                case "-h", "--help" -> usage(null);
                default -> usage("Unknown option: " + args[i]);
            }
        }

        Database db = Database.open(dbFile);
        Runtime.getRuntime().addShutdownHook(new Thread(db::close));
        if (demo) {
            int added = DemoData.seedIfEmpty(db, 40, 2022L);
            System.out.println(added > 0 ? "Added " + added + " demo farmers." : "Database not empty; demo data skipped.");
        }
        System.out.println("Database: " + dbFile.toAbsolutePath());

        FarmerService service = new FarmerService(db);
        SwingUtilities.invokeLater(() -> {
            Ui.installLookAndFeel();
            MainWindow window = new MainWindow(service);
            if (service.needsAdminSetup()) {
                AdminSetupDialog setup = new AdminSetupDialog(null, service);
                setup.setVisible(true);
                if (setup.created() == null) {
                    JOptionPane.showMessageDialog(null, "FarmersBW needs an admin account to start.");
                    System.exit(0);
                }
            }
            window.setVisible(true);
        });
    }

    static Path defaultDatabase() {
        String env = System.getenv("FARMERSBW_DB");
        if (env != null && !env.isBlank()) {
            return Path.of(env);
        }
        return Path.of(System.getProperty("user.home"), ".farmersbw", "farmersbw.db");
    }

    private static void usage(String problem) {
        if (problem != null) {
            System.err.println(problem);
        }
        System.err.println("Usage: farmersbw [--db FILE] [--demo]");
        System.exit(problem == null ? 0 : 2);
    }
}
