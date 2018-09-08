package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;

/**
 * Async Task for checking authentication.
 *
 * @author Priit Käärd
 */
public class CheckAuthenticationTask extends AsyncTask<Void, Void, Boolean> {
    private API api;
    private TaskCompletedCallback<Boolean> callback;
    private String errorMessage = "Not authenticated";

    public CheckAuthenticationTask(API api, TaskCompletedCallback<Boolean> callback) {
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            return api.isAuthenticated();
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            errorMessage = e.getReason();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (result) {
            callback.onSuccess(true);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
