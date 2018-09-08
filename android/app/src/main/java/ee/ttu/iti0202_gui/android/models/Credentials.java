package ee.ttu.iti0202_gui.android.models;

import android.content.Context;

import ee.ttu.iti0202_gui.android.utils.MD5;
import ee.ttu.iti0202_gui.android.utils.PreferenceHelper;

/**
 * Credentials entity class.
 *
 * @author Priit Käärd
 */
public class Credentials {
    private String username;
    private String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Credentials hash() {
        password = MD5.hash(password);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    /**
     * Save credentials to shared preferences.
     */
    public void save(Context context, boolean keepLoggedIn) {
        PreferenceHelper.setString(context, PreferenceHelper.KEYS.USERNAME, getUsername());
        PreferenceHelper.setString(context, PreferenceHelper.KEYS.PASSWORD, getPassword());
        PreferenceHelper.setBoolean(context, PreferenceHelper.KEYS.KEEP_LOGGED_IN, keepLoggedIn);
    }

    /**
     * Get credentials from shared preferences.
     *
     * @return          Credentials object
     */
    public static Credentials getSavedCredentials(Context context) {
        return new Credentials(
                PreferenceHelper.getString(context, PreferenceHelper.KEYS.USERNAME, ""),
                PreferenceHelper.getString(context, PreferenceHelper.KEYS.PASSWORD, ""));
    }
}
