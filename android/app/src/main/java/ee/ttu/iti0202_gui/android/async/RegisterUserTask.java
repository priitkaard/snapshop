package ee.ttu.iti0202_gui.android.async;

import android.os.AsyncTask;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.User;

/**
 * Async Task to register a new user.
 *
 * @author Priit Käärd
 */
public class RegisterUserTask extends AsyncTask<Void, Void, User> {
    private User user;
    private TaskCompletedCallback<User> callback;
    private String errorMessage = "Unknown error";

    public RegisterUserTask(User user, TaskCompletedCallback<User> callback) {
        this.user = user;
        this.callback = callback;
    }

    @Override
    protected User doInBackground(Void... voids) {
        try {
            return API.getInstance(null).registerUser(user);
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
