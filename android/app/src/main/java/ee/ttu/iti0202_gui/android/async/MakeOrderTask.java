package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;

/**
 * Async task class to make an order.
 *
 * @author Priit Käärd
 */

public class MakeOrderTask extends AsyncTask<Void, Void, Order> {
    private API api;
    private Order order;
    private TaskCompletedCallback<Order> callback;
    private String errorMessage = "Unknown error";

    public MakeOrderTask(API api, Order order, TaskCompletedCallback<Order> callback) {
        this.api = api;
        this.order = order;
        this.callback = callback;
    }

    @Override
    protected Order doInBackground(Void... voids) {
        try {
            return api.makeOrder(order);
        } catch (API.RequestFailedException e) {
            errorMessage = e.getReason();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Order s) {
        super.onPostExecute(s);

        if (s != null) {
            callback.onSuccess(s);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
