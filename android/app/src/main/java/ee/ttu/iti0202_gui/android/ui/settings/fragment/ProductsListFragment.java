package ee.ttu.iti0202_gui.android.ui.settings.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.adapter.ProductListAdapter;
import ee.ttu.iti0202_gui.android.ui.home.HomeProductFragment;
import ee.ttu.iti0202_gui.android.ui.settings.ISettingsActivity;
import ee.ttu.iti0202_gui.android.ui.settings.SettingsActivity;

/**
 * UI Class for products list fragment.
 *
 * @author Priit Käärd
 */
public class ProductsListFragment extends Fragment {
    private List<Product> productList = new ArrayList<>();
    private ISettingsActivity activity;
    private ListView listView;
    private TextView titleView;
    private TextView errorView;
    private String title;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_list_fragment, container, false);
        listView = view.findViewById(R.id.listView);
        errorView = view.findViewById(R.id.error);
        titleView = view.findViewById(R.id.title);
        titleView.setText(title);

        setupListview();
        return view;
    }

    /**
     * Helper to set up product list view.
     */
    private void setupListview() {
        if (getActivity() == null) return;
        listView.setAdapter(new ProductListAdapter(getActivity(), productList));
        listView.setEmptyView(errorView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getItemAtPosition(position);
                if (!Product.getProducts().containsKey(product.getId()))
                    Product.getProducts().put(product.getId(), product);

                HomeProductFragment fragment = new HomeProductFragment();
                fragment.setArguments(new Bundle());
                if (fragment.getArguments() != null)
                    fragment.getArguments().putLong("product_id", product.getId());
                activity.doFragmentTransaction(fragment,
                        getString(R.string.tag_home_product_fragment), true);
            }
        });
        // TODO: click open product view.
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public void setTitle(String title) {
        if (titleView != null)
            titleView.setText(title);
        this.title = title;
    }
}
