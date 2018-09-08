package ee.ttu.iti0202_gui.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.BitmapDownloadTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * Adapter class for products list.
 *
 * @author Priit Käärd
 */
public class ProductListAdapter extends ArrayAdapter<Product> {
    private static final String TAG = "ProductListAdapter";

    public ProductListAdapter(@NonNull Context context, @NonNull List<Product> objects) {
        super(context, R.layout.product_list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item,
                    parent, false);
        }

        final Product product = getItem(position);
        if (product == null) {
            Log.e(TAG, "getView: Product is null");
            return convertView;
        }
        ImageView imageView = convertView.findViewById(R.id.image);
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        TextView price = convertView.findViewById(R.id.price);

        if (product.getThumbnail() == null) {
            imageView.setImageResource(R.drawable.no_image);
        } else if (!product.getBitmaps().keySet().contains(product.getThumbnail())) {
            new BitmapDownloadTask(API.getInstance(Session.getSession().getCredentials()), product,
                    imageView.getWidth(), imageView.getHeight(), new TaskCompletedCallback<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap object) {
                            product.getBitmaps().put(product.getThumbnail(), object);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String message) {
                            // Failed to fetch image.
                        }
                    }).execute(product.getThumbnail());
        } else {
            imageView.setImageBitmap(product.getBitmaps().get(product.getThumbnail()));
        }

        title.setText(product.getTitle());
        description.setText(product.getDescription());
        price.setText(product.getPrice().stripTrailingZeros().toPlainString());

        return convertView;
    }
}
