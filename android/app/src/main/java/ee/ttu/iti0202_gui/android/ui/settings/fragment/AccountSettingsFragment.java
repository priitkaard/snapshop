package ee.ttu.iti0202_gui.android.ui.settings.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.LoadUserTask;
import ee.ttu.iti0202_gui.android.async.UpdateUserTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.models.User;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * UI Fragment for account settings.
 *
 * @author Priit Käärd
 */
public class AccountSettingsFragment extends Fragment {
    // Widgets
    private ImageView avatarView;
    private TextView usernameView;

    private EditText firstNameView;
    private EditText lastNameView;
    private EditText homeCityView;

    private EditText phoneNumberView;
    private EditText emailView;

    private EditText accountOwnerView;
    private EditText accountNumberView;

    private Button saveButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment_account,
                container, false);

        // Widgets
        avatarView = view.findViewById(R.id.avatar);
        usernameView = view.findViewById(R.id.username);
        firstNameView = view.findViewById(R.id.first_name);
        lastNameView = view.findViewById(R.id.last_name);
        homeCityView = view.findViewById(R.id.home_city);
        phoneNumberView = view.findViewById(R.id.phone_number);
        emailView = view.findViewById(R.id.email);
        accountOwnerView = view.findViewById(R.id.account_owner);
        accountNumberView = view.findViewById(R.id.account_number);
        saveButton = view.findViewById(R.id.save_button);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClicked();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        loadData();

        return view;
    }

    /**
     * Helper to get user data and display it in user input fields.
     */
    private void loadData() {
        new LoadUserTask(API.getInstance(Session.getSession().getCredentials()),
                new TaskCompletedCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        usernameView.setText(user.getUsername());

                        firstNameView.setText(user.getFirstName());
                        lastNameView.setText(user.getLastName());
                        homeCityView.setText(user.getHomeAddress());

                        phoneNumberView.setText(user.getPhoneNumber());
                        emailView.setText(user.getEmail());

                        accountOwnerView.setText(user.getAccountOwner());
                        accountNumberView.setText(user.getAccountNumber());
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }).execute();
    }

    /**
     * Helper to handle click on avatar picture.
     */
    private void onAvatarClicked() {
        Toast.makeText(getActivity(), "Under developemnt", Toast.LENGTH_SHORT).show();
    }

    /**
     * Helepr to fetch data from input ad update user data.
     */
    private void updateData() {
        // TODO: Image

        String firstName = firstNameView.getText().toString();
        String lastName = lastNameView.getText().toString();
        String homeCity = homeCityView.getText().toString();
        String phoneNumber = phoneNumberView.getText().toString();
        String email = emailView.getText().toString();
        String accountOwner = accountOwnerView.getText().toString();
        String accountNumber = accountNumberView.getText().toString();

        Credentials credentials = Credentials.getSavedCredentials(getActivity());

        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setHomeAddress(homeCity);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAccountOwner(accountOwner);
        user.setAccountNumber(accountNumber);

        new UpdateUserTask(API.getInstance(credentials), user, new TaskCompletedCallback<User>() {
            @Override
            public void onSuccess(User object) {
                Toast.makeText(getActivity(), "User updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


}
