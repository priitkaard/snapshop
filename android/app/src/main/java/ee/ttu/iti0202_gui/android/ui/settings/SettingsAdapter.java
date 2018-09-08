package ee.ttu.iti0202_gui.android.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ee.ttu.iti0202_gui.android.R;

/**
 * Adapter class for settings list.
 *
 * @author Priit Käärd
 */
public class SettingsAdapter extends ArrayAdapter {
    private List<Map.Entry<String, Integer>> settings;

    public SettingsAdapter(@NonNull Context context) {
        super(context, R.layout.settings_list_item);
        settings = new ArrayList<>(SettingsActivity.settings.entrySet());
    }

    @Override
    public int getCount() {
        return settings.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.settings_list_item, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.icon_view);
        TextView titleView = convertView.findViewById(R.id.title_view);

        titleView.setText(settings.get(position).getKey());
        iconView.setImageResource(settings.get(position).getValue());

        return convertView;
    }
}
