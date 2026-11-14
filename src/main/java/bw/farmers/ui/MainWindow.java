package bw.farmers.ui;

import bw.farmers.model.User;
import bw.farmers.service.FarmerService;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

/** The single app window. Each screen replaces the content pane. */
public final class MainWindow extends JFrame implements Navigator {

    private static final long serialVersionUID = 1L;

    private final transient FarmerService service;
    private final LoginPanel login;

    public MainWindow(FarmerService service) {
        super("FarmersBW");
        this.service = service;
        this.login = new LoginPanel(service, this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setSize(1100, 760);
        setLocationRelativeTo(null);
        showLogin();
    }

    @Override
    public void showLogin() {
        login.clear();
        swap(login, "FarmersBW");
    }

    @Override
    public void showRegister() {
        swap(new RegisterPanel(service, this, email -> {
            showLogin();
            login.prefill(email);
        }), "FarmersBW - Register");
    }

    @Override
    public void showHome(User user) {
        if (user.admin()) {
            swap(new AdminPanel(service, user, this), "FarmersBW - Admin");
        } else {
            swap(new FarmerHomePanel(service, user, this), "FarmersBW - My registration");
        }
    }

    private void swap(JComponent screen, String title) {
        setTitle(title);
        setContentPane(screen);
        revalidate();
        repaint();
    }
}
