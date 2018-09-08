package ee.ttu.iti0202_gui.android.ui.home;

import android.support.v4.app.Fragment;

/**
 * Custom interface for Home Activity to allow communication between fragment and activity.
 *
 * @author Priit Käärd
 */
public interface IHomeActivity {
    void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, boolean add);
    void lockDrawer(boolean bool);
}
