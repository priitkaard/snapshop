package ee.ttu.iti0202_gui.android.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.BitmapDownloadTask;
import ee.ttu.iti0202_gui.android.async.LoadProductsTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.config.Config;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.addproduct.AddProductActivity;
import ee.ttu.iti0202_gui.android.utils.ImageHelper;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for Home recycler view.
 *
 * @author Priit Käärd
 */
public class HomeRecyclerViewFragment extends Fragment {
    private static final String TAG = "HomeRecyclerViewFragmen";

    private static final int COLUMN_COUNT = 2;

    private IHomeActivity activity;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;

    private ConstraintLayout root;
    private Bundle filters;

    @Override
    public void onResume() {
        super.onResume();
        activity.lockDrawer(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (HomeActivity) getActivity();
        Log.d(TAG, "onAttach: Activity interface attached.");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ConstraintLayout) inflater
                .inflate(R.layout.home_products_recyclerview, container, false);
        filters = getArguments();

        setupFloatingButton();
        setupSwipeRefreshLayout();
        setupRecyclerViewAndProductAdapter();

        reloadProducts();

        return root;
    }

    /**
     * Helper to set up floating action button for adding new products.
     */
    private void setupFloatingButton() {
        root.findViewById(R.id.floatingCameraButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddProductActivity.class);
                        startActivity(intent);
                    }
                });
    }

    /**
     * Helper to set up Swipe Refresh Layout.
     */
    private void setupSwipeRefreshLayout() {
        SwipeRefreshLayout layout = root.findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadProducts();
            }
        });
    }

    /**
     * Helper to reload products from server.
     */
    private void reloadProducts() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("limit", String.valueOf(Config.FRONT_PAGE_PRODUCTS_AMOUNT));
        parameters.put("page", String.valueOf(0));

        if (filters != null) {
            if (filters.containsKey("category")) {
                parameters.put("category", String.valueOf(filters.getLong("category")));
                Log.d(TAG, "reloadProducts: Category: " + filters.getLong("category"));
            }
            if (filters.containsKey("query")) {
                parameters.put("query", filters.getString("query"));
                Log.d(TAG, "reloadProducts: Query: " + filters.getString("query"));
            }
        }

        new LoadProductsTask(API.getInstance(Session.getSession().getCredentials()),
                parameters, new TaskCompletedCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> object) {
                Log.d(TAG, "onSuccess: Products updated");
                onProductsRefreshed();

                productList = object;
                productAdapter.notifyDataSetChanged();

                // Update loaded products map
                for (Product product : object) {
                    Product.getProducts().put(product.getId(), product);
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "onFailure: Products update failed: " + message);
                onProductsRefreshed();

                Toast.makeText(getActivity(), "Could not update products", Toast.LENGTH_LONG)
                        .show();
            }
        }).execute();
    }

    /**
     * Helper to set up Recycler View.
     */
    private void setupRecyclerViewAndProductAdapter() {
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);

        productAdapter = new ProductAdapter();
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(COLUMN_COUNT,
                StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                        .invalidateSpanAssignments();
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productAdapter);
    }

    /**
     * Callback on products refreshed.
     */
    private void onProductsRefreshed() {
        ((SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout))
                .setRefreshing(false);
    }

    /**
     * Go to product view.
     *
     * @param product       Product instance.
     */
    private void goToProductView(Product product) {
        Bundle data = new Bundle();
        data.putLong("product_id", product.getId());
        HomeProductFragment frag = new HomeProductFragment();
        frag.setArguments(data);
        activity.doFragmentTransaction(frag,
                getString(R.string.tag_home_product_fragment), true, true);
    }

    /**
     * Method to load product thumbnail.
     *
     * @param product           Product instance.
     */
    private void loadProductThumbnail(final Product product) {
        new BitmapDownloadTask(API.getInstance(Session.getSession().getCredentials()), product,
                ImageHelper.getDeviceScreenWidth(getActivity()) / 2, 0,
                new TaskCompletedCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap object) {
                product.getBitmaps().put(product.getThumbnail(), object);
                productAdapter.notifyItemChanged(productList.indexOf(product));
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "onFailure: Error loading image: " + message);
                Toast.makeText(getActivity(), "Could not load a product thumbnail",
                        Toast.LENGTH_LONG).show();
            }
        }).execute(product.getThumbnail());
    }

    /**
     * Product adapter for Home Recycler View.
     */
    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_home_product, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            final Product product = productList.get(position);

            if (product.getThumbnail() != null) {

                if (product.getBitmaps().get(product.getThumbnail()) == null) {
                    loadProductThumbnail(product);
                } else {
                    Bitmap bitmap = product.getBitmaps().get(product.getThumbnail());
                    bitmap = ImageHelper.getFullWidthBitmap(getActivity(), bitmap);
                    holder.imageView.setImageBitmap(bitmap);
                }
            } else {
                holder.imageView.setImageResource(R.drawable.no_image);
            }

            holder.titleView.setText(product.getTitle());
            holder.priceView.setText(String.format("%s €",
                    product.getPrice().stripTrailingZeros().toPlainString()));

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProductView(product);
                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        /**
         * Products view holder class for recycler view.
         */
        class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView priceView;
            TextView titleView;

            ProductViewHolder(View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.avatar);
                priceView = itemView.findViewById(R.id.price_view);
                titleView = itemView.findViewById(R.id.title_view);
            }
        }
    }
}
