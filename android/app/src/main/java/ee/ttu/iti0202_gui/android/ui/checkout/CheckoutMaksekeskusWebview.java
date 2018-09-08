package ee.ttu.iti0202_gui.android.ui.checkout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.CancelPaymentTask;
import ee.ttu.iti0202_gui.android.async.ConfirmPaymentTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.models.Order;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment class for MK webview.
 *
 * @author Priit Käärd
 */
public class CheckoutMaksekeskusWebview extends Fragment {
    private static final String TAG = "CheckoutMaksekeskusWebv";

    private ICheckoutActivity activity;
    private Order order;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (CheckoutActivity) getActivity();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_maksekeskus_webview_fragment,
                container, false);

        order = Session.getSession().getCurrentOrder();

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.keySet().contains("url")) {
            Toast.makeText(getActivity(), "URL not passed", Toast.LENGTH_LONG).show();
            if (getFragmentManager() != null) getFragmentManager().popBackStackImmediate();
            return view;
        }

        String url = bundle.getString("url") + order.getTransactionId();

        WebView webView = view.findViewById(R.id.webView);
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted: " + url);

                // Handle return urls.
                switch (url) {
                    case "payment://success":
                        confirmOrder(order);
                        return;
                    case "payment://cancel":
                        cancelOrder(order);
                        return;
                }

                super.onPageStarted(view, url, favicon);
            }
        });

        return view;
    }

    /**
     * Method to send cancel request to REST API.
     *
     * @param order         Order object.
     */
    private void cancelOrder(Order order) {
        Toast.makeText(getActivity(), "Cancelling order...", Toast.LENGTH_SHORT).show();

        new CancelPaymentTask(API.getInstance(Session.getSession().getCredentials()), order,
                new TaskCompletedCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean object) {
                        Toast.makeText(getActivity(), "Order cancelled", Toast.LENGTH_SHORT)
                                .show();
                        if (getFragmentManager() != null)
                            getFragmentManager().popBackStackImmediate();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        if (getFragmentManager() != null)
                            getFragmentManager().popBackStackImmediate();

                    }
                }).execute();
    }

    /**
     * Method to send confirm request to REST API.
     *
     * @param order     Order object.
     */
    private void confirmOrder(Order order) {
        Toast.makeText(getActivity(), "Confirming order...",
                Toast.LENGTH_SHORT).show();

        new ConfirmPaymentTask(API.getInstance(Session.getSession().getCredentials()), order,
                new TaskCompletedCallback<Order>() {
                    @Override
                    public void onSuccess(Order object) {
                        Basket.getInstance().getContent().clear();
                        activity.doFragmentTransaction(new CheckoutThankYouFragment(),
                                getString(R.string.tag_checkout_thankyou), false);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message,
                                Toast.LENGTH_LONG).show();
                    }
                }).execute();
    }
}
