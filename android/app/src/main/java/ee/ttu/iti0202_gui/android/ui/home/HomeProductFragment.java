package ee.ttu.iti0202_gui.android.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.BitmapDownloadTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.component.ImageSliderPager;
import ee.ttu.iti0202_gui.android.utils.ImageHelper;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for Product Detail View.
 *
 * @author Priit Käärd
 */
public class HomeProductFragment extends Fragment {
    private static final String TAG = "HomeProductFragment";

    private ScrollView container;
    private Product product;
    private ImageViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        container = (ScrollView) inflater
                .inflate(R.layout.home_product_fragment, container, false);

        product = getProduct();
        if (product == null) {
            Log.e(TAG, "onCreateView: Product not found by id.");
            Toast.makeText(getActivity(), "Product with that ID was not found.",
                    Toast.LENGTH_LONG).show();
            return container;
        }

        setupImageSlider();
        setupProductData();

        return container;
    }

    /**
     * UI Helper to set up product data in fragment.
     */
    private void setupProductData() {
        ((TextView) container.findViewById(R.id.title_text_view)).setText(product.getTitle());
        ((TextView) container.findViewById(R.id.price_text_view)).setText(String.format("%s€",
                product.getPrice().stripTrailingZeros().toPlainString()));
        ((TextView) container.findViewById(R.id.description_text_view))
                .setText(product.getDescription());

        final Button basketButton = container.findViewById(R.id.basket_button);
        if (product.getOwner().getUsername()
                .equals(Session.getSession().getCredentials().getUsername())
                || !product.isActivated()) {
            Log.d(TAG, "setupProductData: Product activated: " + product.isActivated());
            basketButton.setVisibility(View.GONE);
        } else if (!Basket.getInstance().getContent().contains(product)) {
            basketButton.setText(R.string.add_to_basket);
        } else {
            basketButton.setText(R.string.remove_from_basket);
        }

        basketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Basket basket = Basket.getInstance();
                if (basket.getContent().contains(product)) {
                    basket.getContent().remove(product);
                    Toast.makeText(getActivity(), product.getTitle() + " removed from your basket",
                            Toast.LENGTH_SHORT).show();
                    basketButton.setText(getString(R.string.add_to_basket));
                } else {
                    basket.addProduct(product);
                    Toast.makeText(getActivity(), product.getTitle() + " added to your basket",
                            Toast.LENGTH_SHORT).show();
                    basketButton.setText(getString(R.string.remove_from_basket));

                }
            }
        });
    }

    /**
     * UI Helper to set up image gallery.
     */
    private void setupImageSlider() {
        ImageSliderPager pager = container.findViewById(R.id.view_pager);
        adapter = new ImageViewPagerAdapter(product);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(10);
        pager.setCurrentItem(0);
    }

    /**
     * Helper method to get Product instance from passed arguments.
     *
     * @return      Product instance.
     */
    private Product getProduct() {
        if (getArguments() == null) {
            Log.e(TAG, "onCreateView: Arguments not set");
            Toast.makeText(getActivity(), "Product ID was not passed.", Toast.LENGTH_LONG)
                    .show();
            return null;
        }

        return Product.getProducts().get(getArguments().getLong("product_id", -1));
    }

    /**
     * Helper method to load product iamge.
     *
     * @param product           Product instance.
     * @param name              Product image file name.
     */
    private void loadProductImage(final Product product, final String name) {
        new BitmapDownloadTask(API.getInstance(Session.getSession().getCredentials()),
                product, ImageHelper.getDeviceScreenWidth(getActivity()), 0,
                new TaskCompletedCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap object) {
                product.getBitmaps().put(name, object);
                Log.d(TAG, "onSuccess: Product image " + name + " bitmap downloaded.");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "onFailure: Error loading image: " + message);
                Toast.makeText(getActivity(),
                        "Error loading image: " + message, Toast.LENGTH_LONG).show();
            }
        }).execute(name);
    }
    
    /**
     * Adapter class for product image slider.
     */
    public class ImageViewPagerAdapter extends PagerAdapter {
        private static final String TAG = "ImageViewPagerAdapter";

        private int currentPosition = -1;
        private Product product;

        ImageViewPagerAdapter(Product product) {
            this.product = product;
        }

        @Override
        public int getCount() {
            return product.getImages().size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.home_product_gallery_item, container, false);

            final ImageView imageView = itemView.findViewById(R.id.product_gallery_image_view);
            container.addView(itemView);

            final String name = product.getImages().get(position);
            if (product.getBitmaps().get(name) == null) {
                Log.d(TAG, "instantiateItem: Product image not set. Loading image...");
                loadProductImage(product, name);
            } else {
                Log.d(TAG, "instantiateItem: Product image set. Setting product image...");
                imageView.setImageBitmap(product.getBitmaps().get(name));
            }
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position,
                                   @NonNull Object object) {
            super.setPrimaryItem(container, position, object);

            if (position != currentPosition) {
                ImageSliderPager pager = (ImageSliderPager) container;
                ImageView imageView = ((View) object).findViewById(R.id.product_gallery_image_view);
                if (imageView == null) return;

                currentPosition = position;
                pager.measureCurrentView(imageView);
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
