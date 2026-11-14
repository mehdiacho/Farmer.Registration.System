package bw.farmers.ui;

import bw.farmers.model.User;

/** Moves between the app's screens. */
public interface Navigator {
    void showLogin();

    void showRegister();

    /** Opens the screen that fits the account: the admin area or the farmer's profile. */
    void showHome(User user);
}
