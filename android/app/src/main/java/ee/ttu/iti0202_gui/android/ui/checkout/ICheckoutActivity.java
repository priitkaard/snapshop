package ee.ttu.iti0202_gui.android.ui.checkout;

import android.support.v4.app.Fragment;

/**
 * Interface for Checkout activity.
 *
 * @author Priit Käärd
 */
public interface ICheckoutActivity {
    void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack);
}
