package ee.ttu.iti0202_gui.android;

import android.app.Application;

import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * Main application class to run code on application startup.
 *
 * @author Priit Käärd
 */
public class App extends Application {
    private Session session;

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        TODO:
            - clear basket after login
            - picture formatting

         */
        // Initialize session singleton.
        session = Session.getSession();
        session.setCredentials(Credentials
                        .getSavedCredentials(getApplicationContext()));
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
