package ee.ttu.iti0202_gui.android.utils;

import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.models.Order;

/**
 * Session class to store variables.
 *
 * @author Priit Käärd
 */
public class Session {
    private static Session instance;

    public static Session getSession() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Variables
    private Credentials credentials;
    private Order currentOrder;

    private Session() { }

    // Getters and Setters
    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public static void newSession() {
        instance = new Session();
    }
}
