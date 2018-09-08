package ee.ttu.iti0202_gui.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Helper class for Shared Preferences.
 *
 * @author Priit Käärd
 */
public class PreferenceHelper {
    private static final String PREF_FILE = "SnapShopPreferences";

    /**
     * Enums used to store data in shared preferences.
     */
    public enum KEYS {
        USERNAME, PASSWORD, KEEP_LOGGED_IN
    }

    /**
     * Store String value in shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param value         Preference value
     */
    public static void setString(Context context, KEYS key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key.toString(), value);
        editor.apply();
    }

    /**
     * Get String value from shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param defaultValue  Default value if key wasn't found
     * @return              Value or default value
     */
    public static String getString(Context context, KEYS key, String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return pref.getString(key.toString(), defaultValue);
    }

    /**
     * Store String set in shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param value         Preference value
     */
    public static void setStringSet(Context context, KEYS key, Set<String> value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key.toString(), value);
        editor.apply();
    }

    /**
     * Get String set from shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param defaultValue  Default value if key wasn't found
     * @return              Value or default value
     */
    public static Set<String> getStringSet(Context context, KEYS key, Set<String> defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return pref.getStringSet(key.toString(), defaultValue);
    }

    /**
     * Store boolean value in shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param value         Preference value
     */
    public static void setBoolean(Context context, KEYS key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key.toString(), value);
        editor.apply();
    }

    /**
     * Get boolean value from shared preferences.
     *
     * @param context       Application context
     * @param key           Preference key
     * @param defaultValue  Default value if key wasn't found
     * @return              Value or default value
     */
    public static boolean getBoolean(Context context, KEYS key, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return pref.getBoolean(key.toString(), defaultValue);
    }
}
