package ee.ttu.iti0202_gui.android.ui.settings.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.LoadOrdersTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.home.HomeProductFragment;
import ee.ttu.iti0202_gui.android.ui.settings.ISettingsActivity;
import ee.ttu.iti0202_gui.android.ui.settings.SettingsActivity;
import ee.ttu.iti0202_gui.android.ui.settings.adapter.OrdersListAdapter;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Class for orders list fragment.
 *
 * @author Priit Käärd
 */
public class OrdersListFragment extends Fragment {
    private ISettingsActivity activity;
    private ExpandableListView listView;
    private TextView errorView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_fragment_list, container, false);
        listView = view.findViewById(R.id.listview);
        errorView = view.findViewById(R.id.error);
        setupListview();

        return view;
    }

    /**
     * Helper to set up list view for orders.
     */
    private void setupListview() {
        new LoadOrdersTask(API.getInstance(Session.getSession().getCredentials()),
                new TaskCompletedCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> object) {
                        if (getActivity() == null) return;
                        listView.setAdapter(new OrdersListAdapter(getActivity(), object));
                        listView.setEmptyView(errorView);

                        listView.setOnChildClickListener(
                                new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v,
                                                        int groupPosition, int childPosition,
                                                        long id) {
                                Product product = (Product) parent.getExpandableListAdapter()
                                        .getChild(groupPosition, childPosition);
                                if (product == null) return false;

                                if (!Product.getProducts().containsKey(product.getId()))
                                    Product.getProducts().put(product.getId(), product);

                                HomeProductFragment fragment = new HomeProductFragment();
                                fragment.setArguments(new Bundle());
                                if (fragment.getArguments() != null)
                                    fragment.getArguments().putLong("product_id", product.getId());

                                activity.doFragmentTransaction(fragment,
                                        getString(R.string.tag_home_product_fragment),
                                        true);
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }).execute();
    }
}
