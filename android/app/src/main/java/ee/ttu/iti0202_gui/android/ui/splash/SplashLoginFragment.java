package ee.ttu.iti0202_gui.android.ui.splash;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.config.Config;
import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for logging in.
 *
 * @author Priit Käärd
 */
public class SplashLoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SplashLoginFragment";

    private SplashActivity activity;

    // Widgets
    private TextView errorView;
    private EditText usernameView;
    private EditText passwordView;
    private AppCompatCheckBox keepLoggedIn;
    private ProgressBar progressBar;
    private Button loginButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SplashActivity) getActivity();
        Log.d(TAG, "onAttach: Activity interface attached.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_login_fragment, container, false);

        // Widgets
        usernameView = view.findViewById(R.id.splash_username);
        passwordView = view.findViewById(R.id.splash_password);
        errorView = view.findViewById(R.id.error_message);
        keepLoggedIn = view.findViewById(R.id.splash_remember_me);
        progressBar = view.findViewById(R.id.progress_bar);
        loginButton = view.findViewById(R.id.login_button);

        // Listeners
        view.findViewById(R.id.login_button).setOnClickListener(this);

        // Load username from preferences
        usernameView.setText(Credentials.getSavedCredentials(getActivity()).getUsername().trim());
        setInput(true);

        // Request focus on edit text view.
        if (!usernameView.getText().toString().trim().equals("")) {
            passwordView.requestFocus();
        } else {
            usernameView.requestFocus();
        }

        return view;
    }

    /**
     * On click listener for login button.
     *
     * @param v         Button view.
     */
    @Override
    public void onClick(View v) {
        if (v == loginButton && validateInput()) {
            setInput(false);

            Credentials credentials = new Credentials(usernameView.getText().toString(),
                    passwordView.getText().toString()).hash();
            credentials.save(getActivity(), keepLoggedIn.isChecked());
            Session.getSession().setCredentials(credentials);

            tryLogin();
        }
    }

    /**
     * Helper method to go to loading fragment and request login.
     */
    private void tryLogin() {
        Bundle arguments = new Bundle();
        arguments.putBoolean(getString(R.string.login_request_key), true);
        activity.doFragmentTransaction(new SplashLoadingFragment(),
                getString(R.string.tag_splash_loading), arguments, true);
    }

    /**
     * UI helper to set input availability.
     *
     * @param bool          Available or not.
     */
    private void setInput(boolean bool) {
        if (bool) {
            loginButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Helper method to validate input information before sending the request.
     *
     * @return          Whether the input is valid or not.
     */
    private boolean validateInput() {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        if (username.length() < Config.MIN_USERNAME_LENGTH) {
            setErrorMessage("Username has to be atleast " +
                    Config.MIN_USERNAME_LENGTH +
                    " characters long.");
        } else if (username.length() > Config.MAX_USERNAME_LENGTH) {
            setErrorMessage("Username has to be shorter than " +
                    Config.MAX_USERNAME_LENGTH +
                    " characters.");
        } else if (!username.matches(Config.USERNAME_REGEX)) {
            setErrorMessage("Username must only contain " +
                    Config.USERNAME_REGEX_READABLE);
        } else if (password.length() < Config.MIN_PASSWORD_LENGTH) {
            setErrorMessage("Password has to be atleast " +
                    Config.MIN_PASSWORD_LENGTH +
                    " characters long.");
        } else if (password.length() > Config.MAX_PASSWORD_LENGTH) {
            setErrorMessage("Password has to be shorter than " +
                    Config.MAX_PASSWORD_LENGTH +
                    " characters.");
        } else {
            return true;
        }
        return false;
    }

    /**
     * Helper method to set error message.
     *
     * @param s     Error message.
     */
    private void setErrorMessage(String s) {
        errorView.setTextColor(Color.RED);
        errorView.setText(s);
    }
}
