package ee.ttu.iti0202_gui.android.callback;

/**
 * Custom callback interface for AsyncTask callbacks.
 *
 * @author Priit Käärd
 */
public interface TaskCompletedCallback<T> {
    void onSuccess(T object);
    void onFailure(String message);
}
