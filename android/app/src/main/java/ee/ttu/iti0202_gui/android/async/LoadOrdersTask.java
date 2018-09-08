package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import java.util.List;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Order;

/**
 * Async task class to load user orders.
 *
 * @author Priit Käärd
 */
public class LoadOrdersTask extends AsyncTask<Void, Void, List<Order>> {
    private API api;
    private TaskCompletedCallback<List<Order>> callback;
    private String errorMessage = "Unknown error";

    public LoadOrdersTask(API api, TaskCompletedCallback<List<Order>> callback) {
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected List<Order> doInBackground(Void... voids) {
        try {
            return api.loadUserOrders();
        } catch (API.RequestFailedException e) {
            errorMessage = e.getReason();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Order> orders) {
        super.onPostExecute(orders);

        if (orders != null) {
            callback.onSuccess(orders);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
