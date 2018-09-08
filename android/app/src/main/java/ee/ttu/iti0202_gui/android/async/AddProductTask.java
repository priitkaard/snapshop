package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;

/**
 * Async Task class to add a new product.
 *
 * @author Priit Käärd
 */
public class AddProductTask extends AsyncTask<Void, Void, Product> {
    private API api;
    private Product product;
    private TaskCompletedCallback<Product> callback;
    private String errorMessage = "Unknown error";

    public AddProductTask(API api, Product product, TaskCompletedCallback<Product> callback) {
        this.api = api;
        this.product = product;
        this.callback = callback;
    }

    @Override
    protected Product doInBackground(Void... voids) {
        try {
            return api.addNewProduct(product);
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            errorMessage = e.getReason();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Product product) {
        super.onPostExecute(product);

        if (product != null) {
            callback.onSuccess(product);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
