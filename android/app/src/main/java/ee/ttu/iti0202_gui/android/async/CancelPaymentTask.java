package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;

/**
 * Async task class to cancel a payment.
 *
 * @author Priit Käärd
 */
public class CancelPaymentTask extends AsyncTask<Void, Void, Boolean> {
    private API api;
    private Order order;
    private TaskCompletedCallback<Boolean> callback;
    private String errorMessage = "Unknown error";

    public CancelPaymentTask(API api, Order order, TaskCompletedCallback<Boolean> callback) {
        this.api = api;
        this.order = order;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            api.cancelOrder(order);
        } catch (API.RequestFailedException e) {
            errorMessage = e.getReason();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            callback.onSuccess(true);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
