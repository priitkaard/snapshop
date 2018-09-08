package ee.ttu.iti0202_gui.android.ui.splash;

import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.async.RegisterUserTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.config.Config;
import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.models.User;
import ee.ttu.iti0202_gui.android.models.UserBuilder;
import ee.ttu.iti0202_gui.android.utils.MD5;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for user registration.
 *
 * @author Priit Käärd
 */
public class SplashRegisterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SplashRegisterFragment";

    private ISplashActivity activity;
    private Map<String, String> data;

    // Widgets
    private EditText emailField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmationField;
    private AppCompatCheckBox termsCheckbox;
    private TextView errorView;
    private ProgressBar progressBar;
    private Button registerButton;


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
        View view = inflater.inflate(R.layout.splash_register_fragment, container, false);

        // Widgets
        emailField = view.findViewById(R.id.splash_email);
        usernameField = view.findViewById(R.id.splash_username);
        passwordField = view.findViewById(R.id.splash_password);
        passwordConfirmationField = view.findViewById(R.id.splash_password_confirm);
        termsCheckbox = view.findViewById(R.id.splash_register_terms);
        progressBar = view.findViewById(R.id.progress_bar);
        errorView = view.findViewById(R.id.error_message);
        registerButton = view.findViewById(R.id.register_button);

        // On click listener
        registerButton.setOnClickListener(this);
        setInput(true);
        setErrorMessage("");

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton && validateInput()) {
            tryRegister();
        }
    }

    /**
     * Method to register user.
     */
    private void tryRegister() {
        setInput(false);
        data = loadDataFromFields();

        new RegisterUserTask(createUserFromData(), new TaskCompletedCallback<User>() {
            @Override
            public void onSuccess(User object) {
                tryAutoLogin();
            }

            @Override
            public void onFailure(String message) {
                setErrorMessage(message);
                setInput(true);
            }
        }).execute();
    }

    /**
     * Method to try automatic logging after registration process.
     */
    private void tryAutoLogin() {
        final Credentials credentials = createCredentialsFromData();
        credentials.save(getActivity(), true);
        Session.getSession().setCredentials(credentials);

        // Tries to log in because has keepLoggedIn set to true.
        activity.doFragmentTransaction(new SplashLoadingFragment(),
                getString(R.string.tag_splash_loading), null, false);
    }

    /**
     * Helper to retrieve data from input fields.
     */
    private Map<String, String> loadDataFromFields() {
        Map<String, String> result = new HashMap<>();
        result.put("email", emailField.getText().toString());
        result.put("username", usernameField.getText().toString());
        result.put("password", MD5.hash(passwordField.getText().toString()));

        return result;
    }

    /**
     * Helper to create user from data fields.
     *
     * @return          User instance.
     */
    private User createUserFromData() {
        return new UserBuilder()
                .setEmail(data.get("email"))
                .setUsername(data.get("username"))
                .setPassword(data.get("password"))
                .createUser();
    }

    /**
     * Helper to get Credentials instance from data fields.
     *
     * @return          Credentials instance.
     */
    private Credentials createCredentialsFromData() {
        return new Credentials(data.get("username"), data.get("password"));
    }

    /**
     * UI Helper to enable or disable user input.
     *
     * @param b         Enabled or Disabled.
     */
    private void setInput(boolean b) {
        if (b) {
            registerButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            registerButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * UI Helper to show error message.
     *
     * @param message           Error message.
     */
    public void setErrorMessage(String message) {
        errorView.setText(message);
    }

    /**
     * Helper to validate user input.
     *
     * @return      User input valid or not.
     */
    private boolean validateInput() {
        String email = emailField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordConf = passwordConfirmationField.getText().toString();

        if (!Config.EMAIL_REGEX.matcher(email).find()) {
            setErrorMessage("Invalid email");
        } else if (username.length() < Config.MIN_USERNAME_LENGTH) {
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
        } else if (!password.equals(passwordConf)) {
            setErrorMessage("Password and confirmation does not match.");
        } else if (!termsCheckbox.isChecked()) {
            setErrorMessage("You must accept the terms of services in order to register");
        } else {
            return true;
        }
        return false;
    }
}
