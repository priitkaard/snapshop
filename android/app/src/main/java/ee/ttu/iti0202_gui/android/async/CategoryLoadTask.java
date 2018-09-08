package ee.ttu.iti0202_gui.android.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Category;
import ee.ttu.iti0202_gui.android.utils.LocalDBHelper;

/**
 * AsyncTask to load categories.
 *
 * @author Priit Käärd
 */
public class CategoryLoadTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "CategoryLoadTask";

    private LocalDBHelper dbHelper;
    private API api;
    private TaskCompletedCallback<List<Category>> callback;
    private List<Category> categories = null;

    public CategoryLoadTask(Context context, API api,
                            TaskCompletedCallback<List<Category>> callback) {
        dbHelper = new LocalDBHelper(context);
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        categories = dbHelper.getCategories();
        if (categories != null && !categories.isEmpty()) {
            Log.d(TAG, "doInBackground: LOADED FROM SQLITE");
            return "";
        }

        Log.d(TAG, "doInBackground: LOADING FROM REST");
        try {
            categories = api.loadCategories();
            Log.d(TAG, "doInBackground: Categories loaded.");
            dbHelper.insertParentCategories(categories);
            Log.d(TAG, "doInBackground: Categories saved.");
        } catch (API.RequestFailedException e) {
            e.printStackTrace();
            return e.getReason();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (categories != null) {
            callback.onSuccess(categories);
        } else {
            callback.onFailure(s);
        }
    }
}
