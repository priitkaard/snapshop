package ee.ttu.iti0202_gui.android.ui.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ee.ttu.iti0202_gui.android.R;

/**
 * UI Fragment to choose register or login fragment.
 *
 * @author Priit Käärd
 */
public class SplashFirstFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SplashFirstFragment";

    private ISplashActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (SplashActivity) getActivity();
        Log.d(TAG, "onAttach: Activity interface attached.");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_first_fragment, container, false);

        //  Widgets
        Button registerButton = view.findViewById(R.id.register_button);
        Button loginButton = view.findViewById(R.id.login_button);

        // OnClickListeners
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        return view;
    }

    /**
     * On click listeners for register and login buttons.
     *
     * @param view          Button view.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                activity.doFragmentTransaction(new SplashLoginFragment(),
                        getString(R.string.tag_splash_login), null, true);
                break;

            case R.id.register_button:
                activity.doFragmentTransaction(new SplashRegisterFragment(),
                        getString(R.string.tag_splash_register), null, true);
                break;
        }
    }
}
