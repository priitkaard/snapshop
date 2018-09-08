package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;

/**
 * Async task class to confirm an orders payment.
 *
 * @author Priit Käärd
 */
public class ConfirmPaymentTask extends AsyncTask<Void, Void, Order> {
    private API api;
    private Order order;
    private TaskCompletedCallback<Order> callback;
    private String errorMessage = "Unknown error";

    public ConfirmPaymentTask(API api, Order order, TaskCompletedCallback<Order> callback) {
        this.api = api;
        this.order = order;
        this.callback = callback;
    }

    @Override
    protected Order doInBackground(Void... voids) {
        try {
            return api.confirmOrder(order);
        } catch (API.RequestFailedException e) {
            errorMessage = e.getReason();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Order order) {
        super.onPostExecute(order);

        if (order != null) {
            callback.onSuccess(order);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
