package ee.ttu.iti0202_gui.android.ui.settings.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.LoadProductsTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.settings.ISettingsActivity;
import ee.ttu.iti0202_gui.android.ui.settings.SettingsActivity;
import ee.ttu.iti0202_gui.android.ui.settings.SettingsAdapter;
import ee.ttu.iti0202_gui.android.ui.splash.SplashActivity;
import ee.ttu.iti0202_gui.android.utils.PreferenceHelper;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Class for settings list fragment.
 *
 * @author Priit Käärd
 */
public class SettingsListFragment extends Fragment {
    private ListView listView;
    private ISettingsActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment_list, container, false);
        listView = view.findViewById(R.id.settings_listview);

        setupListview();
        return view;
    }

    /**
     * Helper to set up settings list view.
     */
    private void setupListview() {
        if (getActivity() == null) return;
        listView.setAdapter(new SettingsAdapter(getActivity()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // My orders
                        goToMyOrders();
                        break;
                    case 1: // My advertisements
                        goToMyAds();
                        break;
                    case 2: // Account settings
                        goToAccountSettings();
                        break;
                    case 3: // Log out
                        PreferenceHelper.setBoolean(getActivity(),
                                PreferenceHelper.KEYS.KEEP_LOGGED_IN, false);
                        PreferenceHelper.setString(getActivity(),
                                PreferenceHelper.KEYS.PASSWORD, "");
                        Session.newSession();

                        Intent intent = new Intent(getActivity(),
                                SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                |Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                }
            }
        });
    }

    /**
     * Helper to go to account settings fragment.
     */
    private void goToAccountSettings() {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
        activity.doFragmentTransaction(fragment,
                getString(R.string.tag_settings_account_fragment), true);
    }

    /**
     * Helper to go to My Orders fragment.
     */
    private void goToMyOrders() {
        OrdersListFragment fragment = new OrdersListFragment();
        activity.doFragmentTransaction(fragment, getString(R.string.tag_order_list_fragment),
                true);
    }

    /**
     * Helper to go to My Advertisements list fragment.
     */
    private void goToMyAds() {
        // Start loading
        Map<String, String> parameters = new HashMap<String, String>() {
            {
                put("username", Session.getSession().getCredentials().getUsername());
            }
        };

        new LoadProductsTask(API.getInstance(Session.getSession().getCredentials()),
                parameters, new TaskCompletedCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> object) {
                // Disable loading
                ProductsListFragment fragment = new ProductsListFragment();
                fragment.setTitle("My advertisements");
                fragment.setProductList(object);
                activity.doFragmentTransaction(fragment,
                        getString(R.string.tag_product_list_fragment), true);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getActivity(), "Failed to load products",
                        Toast.LENGTH_SHORT).show();
                // Disable loading
            }
        }).execute();
    }
}
