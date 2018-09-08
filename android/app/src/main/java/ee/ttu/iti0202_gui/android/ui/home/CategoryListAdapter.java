package ee.ttu.iti0202_gui.android.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.models.Category;

/**
 * Custom List adapter for categories.
 *
 * @author Priit Käärd
 */
public class CategoryListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CategoryListAdapter";

    private Context context;
    private List<Category> categories = new ArrayList<>();

    CategoryListAdapter(Context context, List<Category> allCategories) {
        this.context = context;

        for (Category category : allCategories) {
            if (category.getParentCategory() == null) {
                categories.add(category);
            }
        }

        Log.d(TAG, "CategoryListAdapter: Parent categories size: " + categories.size());
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categories.get(groupPosition).getSubCategories().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories.get(groupPosition).getSubCategories().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((Category) getGroup(groupPosition)).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((Category) getChild(groupPosition, childPosition)).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        Category category = (Category) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.category_list_parent, null);
        }

        TextView name = convertView.findViewById(R.id.category_name);
        name.setText(category.getName());

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        Category category = (Category) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.category_list_sub, null);
        }

        TextView name = convertView.findViewById(R.id.category_name);
        name.setText(category.getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
