package ee.ttu.iti0202_gui.android.ui.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.CheckAuthenticationTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.utils.PreferenceHelper;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment to load data.
 *
 * @author Priit Käärd
 */
public class SplashLoadingFragment extends Fragment {
    private static final String TAG = "SplashLoadingFragment";

    private ISplashActivity activity;
    private TextView statusTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SplashActivity) getActivity();
        Log.d(TAG, "onAttach: Activity interface attached");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_loading_fragment, container, false);

        statusTextView = view.findViewById(R.id.status_text_view);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("");

        start();

        return view;
    }

    /**
     * Start loading fragment functions.
     */
    private void start() {
        final boolean loginRequest = (getArguments() != null) && getArguments()
                .getBoolean(getString(R.string.login_request_key), false);

        // Check if there even was a need to log in.
        if (!loginRequest && !PreferenceHelper.getBoolean(getActivity(),
                PreferenceHelper.KEYS.KEEP_LOGGED_IN, false)) {
            goToFirstFragment();
            return;
        }

        // Try to log in
        statusTextView.setText(R.string.trying_to_log_in);
        new CheckAuthenticationTask(API.getInstance(Session.getSession().getCredentials()),
                new TaskCompletedCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean object) {
                        statusTextView.setText(R.string.logged_in);
                        activity.handleLogin();
                    }

                    @Override
                    public void onFailure(String message) {
                        if (loginRequest) {
                            Toast.makeText(getActivity(),
                                    "Invalid username or password.", Toast.LENGTH_LONG).show();

                            FragmentManager fm = getFragmentManager();
                            if (fm != null && fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack();
                                return;
                            }
                        } else if (PreferenceHelper.getBoolean(getActivity(),
                                PreferenceHelper.KEYS.KEEP_LOGGED_IN, false)) {
                            Toast.makeText(getActivity(),
                                    "Unable to log in with your last credentials.",
                                    Toast.LENGTH_LONG).show();
                        }
                        goToFirstFragment();
                    }
                }).execute();
    }

    /**
     * Helper method to go to the first splash activity fragment.
     */
    private void goToFirstFragment() {
        activity.doFragmentTransaction(new SplashFirstFragment(),
                getString(R.string.tag_splash_first), null, false);
    }
}
