package ee.ttu.iti0202_gui.android.ui.checkout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ee.ttu.iti0202_gui.android.R;

/**
 * UI Fragment class to show thank you screen after confirmed payment.
 *
 * @author Priit Käärd
 */
public class CheckoutThankYouFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_fragment_thankyou,
                container, false);

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) getActivity().finish();
            }
        });
        return view;
    }


}
