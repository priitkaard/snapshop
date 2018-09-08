package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;

/**
 * Async task class to request payment methods.
 *
 * @author Priit Käärd
 */
public class PaymentMethodsTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
    private API api;
    private TaskCompletedCallback<List<Map<String, String>>> callback;

    public PaymentMethodsTask(API api, TaskCompletedCallback<List<Map<String, String>>> callback) {
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected List<Map<String, String>> doInBackground(Void... voids) {
        return api.getPaymentMethods();
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> maps) {
        super.onPostExecute(maps);

        if (maps != null) {
            callback.onSuccess(maps);
        } else {
            callback.onFailure("Error occurred");
        }
    }
}
