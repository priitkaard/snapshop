package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;

/**
 * Async task to load products.
 *
 * @author Priit Käärd
 */
public class LoadProductsTask extends AsyncTask<Void, Void, List<Product>> {
    private API api;
    private Map<String, String> parameters = new HashMap<>();
    private TaskCompletedCallback<List<Product>> callback;
    private String errorMessage = "Unknown error";

    public LoadProductsTask(API api, Map<String, String> parameters,
                            TaskCompletedCallback<List<Product>> callback) {
        this.api = api;
        this.parameters = parameters;
        this.callback = callback;
    }

    @Override
    protected List<Product> doInBackground(Void... voids) {
        try {
            return api.loadProducts(parameters);
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            errorMessage = e.getReason();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
        super.onPostExecute(products);

        if (products != null) {
            callback.onSuccess(products);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
