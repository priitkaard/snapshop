package ee.ttu.iti0202_gui.android.ui.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Custom interface for Splash Activity to allow communication between fragments and activity.
 *
 * @author Priit Käärd
 */
public interface ISplashActivity {
    void doFragmentTransaction(Fragment fragment, String tag, Bundle arguments,
                               boolean addToBackStack);
    void handleLogin();
}
