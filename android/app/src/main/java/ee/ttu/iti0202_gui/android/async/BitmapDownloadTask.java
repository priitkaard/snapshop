package ee.ttu.iti0202_gui.android.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Product;

/**
 * Async Task to load product image bitmap from server.
 *
 * @author Priit Käärd
 */
public class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private API api;
    private Product product;
    private int imageWidth;
    private int imageHeight;
    private TaskCompletedCallback<Bitmap> callback;
    private String errorMessage = "Unknown error";

    public BitmapDownloadTask(API api, Product product, int imageWidth, int imageHeight,
                              TaskCompletedCallback<Bitmap> callback) {
        this.api = api;
        this.product = product;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String filename = strings[0];
        if (filename == null) {
            errorMessage = "URL not passed";
            return null;
        }

        try {
            return api.loadProductBitmap(product.getId(), filename, imageWidth, imageHeight);
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            errorMessage = e.getReason();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
            callback.onFailure(errorMessage);
        } else {
            callback.onSuccess(bitmap);
        }
    }
}
