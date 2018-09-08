package ee.ttu.iti0202_gui.android.ui.basket;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.ui.checkout.CheckoutActivity;

/**
 * UI Modal class.
 *
 * @author Priit Käärd
 */
public class BasketModal extends Dialog implements View.OnClickListener {
    private static final String TAG = "BasketModal";

    private Context context;

    public BasketModal(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_basket);

        // Set dialog layout size.
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // Set up close button
        ImageView closeButton = findViewById(R.id.dismiss_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Initialize basket.
        final Basket basket = Basket.getInstance();

        final Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setText(String.format("Checkout (%s€)",
                Basket.getInstance().getTotalPrice().toPlainString()));
        checkoutButton.setOnClickListener(this);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new BasketAdapter(Basket.getInstance()));
        listView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                BigDecimal totalPrice = basket.getTotalPrice();
                checkoutButton.setText(String.format("Checkout (%s€)", totalPrice));
                if (totalPrice.equals(BigDecimal.ZERO)) {
                    dismiss();
                }

            }
        });
        TextView errorView = findViewById(R.id.modal_error_message);
        View helperContainer = findViewById(R.id.helper_container);
        if (basket.getContent().size() < 1) {
            checkoutButton.setVisibility(View.GONE);
            helperContainer.setVisibility(View.GONE);
            errorView.setText(R.string.basket_is_empty);
        }
        Log.d(TAG, "onCreate: Basket set up");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkout_button) {
            BasketModal.this.dismiss();
            Intent intent = new Intent(v.getContext(), CheckoutActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * Adapter class for basket list view.
     */
    public static class BasketAdapter extends BaseAdapter {
        private static final String TAG = "BasketAdapter";

        private Basket basket;

        public BasketAdapter(Basket basket) {
            this.basket = basket;
        }

        @Override
        public int getCount() {
            return basket.getContent().size();
        }

        @Override
        public Product getItem(int position) {
            return basket.getContent().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basket_modal_list_item, parent, false);
            } else {
                result = convertView;
            }

            final Product product = getItem(position);

            ((ImageView) result.findViewById(R.id.avatar))
                    .setImageBitmap(product.getBitmaps().get(product.getThumbnail()));
            ((TextView) result.findViewById(R.id.title_text_view)).setText(product.getTitle());
            ((TextView) result.findViewById(R.id.price_text_view))
                    .setText(String.format("%s", product.getPrice()
                            .stripTrailingZeros().toPlainString()));

            ImageView removeButton = result.findViewById(R.id.remove_button);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (basket.getContent().contains(product)) {
                        basket.getContent().remove(product);
                        notifyDataSetChanged();
                    }
                }
            });

            Log.d(TAG, "getView: Product details set");

            return result;
        }
    }
}
