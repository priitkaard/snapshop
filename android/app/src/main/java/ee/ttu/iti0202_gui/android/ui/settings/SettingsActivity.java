package ee.ttu.iti0202_gui.android.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.LinkedHashMap;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.ui.settings.fragment.SettingsListFragment;

/**
 * UI Activity class for Settings.
 *
 * @author Priit Käärd
 */
public class SettingsActivity extends AppCompatActivity implements ISettingsActivity {
    public static final Map<String, Integer> settings = new LinkedHashMap<String, Integer>() {
        {
            put("My orders", R.drawable.icon_orders_black);
            put("My advertisements", R.drawable.icon_ads_black);
            put("Account settings", R.drawable.icon_settings_black);
            put("Log out", R.drawable.icon_logout_black);
        }
    };

    private FrameLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        container = findViewById(R.id.container);

        doFragmentTransaction(new SettingsListFragment(),
                getString(R.string.tag_settings_list_fragment), false);
    }


    @Override
    public void doFragmentTransaction(Fragment fragment, String tag, boolean backStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(container.getId(), fragment, tag);
        if (backStack) transaction.addToBackStack(tag);
        transaction.commit();
    }
}
