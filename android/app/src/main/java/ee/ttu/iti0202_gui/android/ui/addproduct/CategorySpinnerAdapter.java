package ee.ttu.iti0202_gui.android.ui.addproduct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.models.Category;

/**
 * UI Adapter for spinner dropdown list for Category entity.
 *
 * @author Priit Käärd
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    private static final String TAG = "CategorySpinnerAdapter";
    private static final int LAYOUT = R.layout.spinner_category_item;

    CategorySpinnerAdapter(@NonNull Context context, @NonNull List<Category> objects) {
        super(context, LAYOUT, R.id.text1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            Log.d(TAG, "createItemView: Convert view null.");
            convertView = LayoutInflater.from(getContext())
                    .inflate(LAYOUT, parent, false);
        }

        Category category = getItem(position);

        if (category != null) {
            TextView textView = convertView.findViewById(R.id.text1);
            textView.setText(category.getName());
        } else {
            Log.e(TAG, "createItemView: Category null");
        }
        return convertView;
    }
}
