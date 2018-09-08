package ee.ttu.iti0202_gui.android.ui.checkout;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.MakeOrderTask;
import ee.ttu.iti0202_gui.android.async.PaymentMethodsTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.models.Order;
import ee.ttu.iti0202_gui.android.models.User;
import ee.ttu.iti0202_gui.android.ui.basket.BasketModal;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for check out confirmation.
 *
 * @author Priit Käärd
 */
public class CheckoutConfirmFragment extends Fragment {
    private static final String TAG = "CheckoutConfirmFragment";

    private ICheckoutActivity iCheckoutActivity;
    private List<Map<String, String>> paymentMethodList = new ArrayList<>();

    // Widgets
    private ListView productListView;
    private TextView orderTotalView;
    private Spinner paymentMethodsView;
    private Button payButton;
    private TextView errorMessageView;
    private TextView closeButton;
    private ConstraintLayout loadingPanel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        iCheckoutActivity = (CheckoutActivity) getActivity();
        Log.d(TAG, "onAttach: Activity interface attached.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_confirm_order_fragment,
                container, false);

        // Widgets
        productListView = view.findViewById(R.id.product_list_view);
        orderTotalView = view.findViewById(R.id.total_price_text_view);
        paymentMethodsView = view.findViewById(R.id.payment_spinner);
        payButton = view.findViewById(R.id.pay_button);
        errorMessageView = view.findViewById(R.id.error_message);
        closeButton = view.findViewById(R.id.close_button);
        loadingPanel = view.findViewById(R.id.loading_panel);

        // Setup
        setupOverview();
        setupUserInput();

        return view;
    }

    /**
     * Helper to set up widgets that user interacts with.
     */
    private void setupUserInput() {
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
        setupPaymentMethods();
    }

    /**
     * Helper to set up products list view.
     */
    private void setupOverview() {
        final Basket basket = Basket.getInstance();

        orderTotalView.setText(basket.getTotalPrice().toPlainString());

        productListView.setAdapter(new BasketModal.BasketAdapter(basket));
        productListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                orderTotalView.setText(basket.getTotalPrice().toPlainString());
                if (basket.getContent().isEmpty() && getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    /**
     * Method to checkout and create Order instance for REST API.
     */
    private void checkout() {
        loadingPanel.setVisibility(View.VISIBLE);
        Basket basket = Basket.getInstance();

        final Order order = new Order();
        order.setProducts(basket.getContent());
        order.setCustomer(new User());
        order.getCustomer().setUsername(Session.getSession().getCredentials().getUsername());

        new MakeOrderTask(API.getInstance(Session.getSession().getCredentials()),
                order, new TaskCompletedCallback<Order>() {
            @Override
            public void onSuccess(Order order) {
                errorMessageView.setText("");
                loadingPanel.setVisibility(View.GONE);

                Session.getSession().setCurrentOrder(order);

                Bundle bundle = new Bundle();
                bundle.putString("url", getSelectedBanklink());

                CheckoutMaksekeskusWebview webview = new CheckoutMaksekeskusWebview();
                webview.setArguments(bundle);

                iCheckoutActivity.doFragmentTransaction(webview,
                        getString(R.string.tag_checkout_maksekeskus), true);
            }

            @Override
            public void onFailure(String message) {
                loadingPanel.setVisibility(View.GONE);
                errorMessageView.setText(message);
            }
        }).execute();
    }

    /**
     * Helper method to get selected bank link url.
     *
     * @return      Bank link url.
     */
    private String getSelectedBanklink() {
        return paymentMethodList.get(paymentMethodsView.getSelectedItemPosition()).get("url");
    }

    /**
     * Helper to set up payment methods spinner.
     */
    private void setupPaymentMethods() {
        // Create view for payment methods.
        if (paymentMethodList == null || paymentMethodList.isEmpty()) {
            Toast.makeText(getActivity(), "Loading payment methods...", Toast.LENGTH_SHORT)
                    .show();

            new PaymentMethodsTask(API.getInstance(Session.getSession().getCredentials()),
                    new TaskCompletedCallback<List<Map<String, String>>>() {
                @Override
                public void onSuccess(List<Map<String, String>> object) {
                    paymentMethodList = object;

                    // Update view
                    updatePaymentMethodSpinner();
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure: Payment methods: " + message);
                    Toast.makeText(getActivity(),
                            "Error on loading payment methods.", Toast.LENGTH_LONG).show();
                }
            }).execute();
        }

        updatePaymentMethodSpinner();
    }

    /**
     * Helper to update payment methods spinner content.
     */
    private void updatePaymentMethodSpinner() {
        List<String> methods = new ArrayList<>();
        for (Map<String, String> map : paymentMethodList) {
            methods.add(map.get("name"));
        }

        if (getActivity() == null) return;

        paymentMethodsView.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_category_item, R.id.text1, methods));
    }
}
