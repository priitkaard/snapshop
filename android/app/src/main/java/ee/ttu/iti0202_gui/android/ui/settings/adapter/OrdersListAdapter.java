package ee.ttu.iti0202_gui.android.ui.settings.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.BitmapDownloadTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * Adapter class for orders list.
 *
 * @author Priit Käärd
 */
public class OrdersListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "OrdersListAdapter";

    private Context context;
    private List<Order> orders;

    public OrdersListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getGroupCount() {
        return orders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return orders.get(groupPosition).getProducts().size();
    }

    @Override
    public Order getGroup(int groupPosition) {
        return orders.get(groupPosition);
    }

    @Override
    public Product getChild(int groupPosition, int childPosition) {
        return orders.get(groupPosition).getProducts().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getGroup(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.order_list_item, parent, false);
        }

        Order order = getGroup(groupPosition);
        if (order == null) return convertView;

        TextView title = convertView.findViewById(R.id.title);
        TextView priceView = convertView.findViewById(R.id.price);
        TextView status = convertView.findViewById(R.id.status);

        String orderTitle = context.getString(R.string.order_number) + order.getId();
        title.setText(orderTitle);
        status.setText(order.getOrderState().toString());
        BigDecimal price = BigDecimal.ZERO;
        for (Product product : order.getProducts()) {
            price = price.add(product.getPrice());
        }
        priceView.setText(price.stripTrailingZeros().toPlainString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.product_list_item, parent, false);
        }

        final Product product = getChild(groupPosition, childPosition);
        if (product == null) return convertView;

        final ImageView image = convertView.findViewById(R.id.image);
        TextView title = convertView.findViewById(R.id.title);
        TextView price = convertView.findViewById(R.id.price);
        TextView description = convertView.findViewById(R.id.description);

        title.setText(product.getTitle());
        description.setText(product.getDescription());
        price.setText(product.getPrice().stripTrailingZeros().toPlainString());

        if (product.getThumbnail() == null) {
            image.setImageResource(R.drawable.no_image);
        } else if (product.getBitmaps().keySet().contains(product.getThumbnail())) {
            image.setImageBitmap(product.getBitmaps().get(product.getThumbnail()));
        } else {
            new BitmapDownloadTask(API.getInstance(Session.getSession().getCredentials()), product,
                    image.getWidth(), image.getHeight(), new TaskCompletedCallback<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap object) {
                            product.getBitmaps().put(product.getThumbnail(), object);
                            if (image != null)
                                image.setImageBitmap(object);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e(TAG, "onFailure: Image load failed: " + message);
                        }
                    }).execute(product.getThumbnail());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
