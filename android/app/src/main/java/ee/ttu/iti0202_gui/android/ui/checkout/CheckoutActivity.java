package ee.ttu.iti0202_gui.android.ui.checkout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ee.ttu.iti0202_gui.android.R;

/**
 * UI Activity class for checkout.
 *
 * @author Priit Käärd
 */
public class CheckoutActivity extends AppCompatActivity implements ICheckoutActivity {
    private static final String TAG = "CheckoutActivity";

    private View container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Log.d(TAG, "onCreate: Started");

        container = findViewById(R.id.container);

        doFragmentTransaction(new CheckoutConfirmFragment(),
                getString(R.string.tag_checkout_confirm), false);
    }

    public void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(container.getId(), fragment);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Do not allow
    }
}
