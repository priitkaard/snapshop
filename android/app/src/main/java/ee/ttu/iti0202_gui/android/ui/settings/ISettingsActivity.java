package ee.ttu.iti0202_gui.android.ui.settings;

import android.support.v4.app.Fragment;

/**
 * Interface for Settings activity.
 *
 * @author Priit Käärd
 */
public interface ISettingsActivity {
    void doFragmentTransaction(Fragment fragment, String tag, boolean backStack);
}
