package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;

/**
 * Async task class for image uploading.
 *
 * @author Priit Käärd
 */
public class ImageUploadTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ImageUploadTask";

    private API api;
    private TaskCompletedCallback<Boolean> callback;
    private Product product;
    private File file;
    private String errorMessage = "Unknown error";

    public ImageUploadTask(API api, Product product, File file, TaskCompletedCallback<Boolean> callback) {
        this.api = api;
        this.product = product;
        this.file = file;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            return api.uploadImage(product, file);
        } catch (API.RequestFailedException e) {
            Log.e(TAG, "doInBackground: Error: " + e.getReason());
            errorMessage = e.getReason();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);

        if (b) {
            callback.onSuccess(true);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
