package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.User;

/**
 * Async task class to update user data.
 *
 * @author Priit Käärd
 */
public class UpdateUserTask extends AsyncTask<Void, Void, User> {
    private API api;
    private User user;
    private TaskCompletedCallback<User> callback;

    private String errorMessage = "Unknown error";

    public UpdateUserTask(API api, User user, TaskCompletedCallback<User> callback) {
        this.api = api;
        this.user = user;
        this.callback = callback;
    }

    @Override
    protected User doInBackground(Void... voids) {
        try {
            return api.updateUserData(user);
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            errorMessage = e.getReason();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);

        if (user != null) {
            callback.onSuccess(user);
        } else {
            callback.onFailure(errorMessage);
        }
    }
}
