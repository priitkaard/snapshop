package ee.ttu.iti0202_gui.android.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.CategoryLoadTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Category;
import ee.ttu.iti0202_gui.android.ui.basket.BasketModal;
import ee.ttu.iti0202_gui.android.ui.settings.SettingsActivity;
import ee.ttu.iti0202_gui.android.utils.Session;

/**
 * Home activity class.
 *
 * @author Priit Käärd
 */
public class HomeActivity extends AppCompatActivity implements IHomeActivity, View.OnClickListener {
    private static final String TAG = "HomeActivity";

    private DrawerLayout homeDrawerLayout;

    private Bundle filters = new Bundle();
    private ExpandableListView navigationView;
    private List<Category> categoryList;

    // Helpers to remind last selected category positions. Not the best practise, but works for now.
    private int groupPosition = -1;
    private int childPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeDrawerLayout = findViewById(R.id.homeDrawerLayout);
        navigationView = findViewById(R.id.home_nav_view);

        setupSearchBar();
        setupNavigationView();
        setupActionButtons();

        doFragmentTransaction(new HomeRecyclerViewFragment(),
                getString(R.string.tag_main_recycle_fragment), false, false);
    }

    /**
     * Method to set up search bar under navigation drawer.
     */
    private void setupSearchBar() {
        EditText searchBar = findViewById(R.id.search_box);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setFilter(v.getText().toString().trim());
                    hideSoftKeyboard();
                }
                return true;
            }
        });
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager == null) return;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Method to set up categories in navigation drawer.
     */
    private void setupNavigationView() {
        Log.d(TAG, "setupNavigationView: Setting up navigation view.");

        /*
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null && fm.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
                    if (fm.findFragmentByTag(entry.getName()) instanceof HomeRecyclerViewFragment) {
                        lockDrawer(false);
                    } else {
                        lockDrawer(true);
                    }
                }
            }
        });
        */

        if (categoryList == null || categoryList.isEmpty()) {
            Log.d(TAG, "setupNavigationView: Loading categories.");
            new CategoryLoadTask(this, API.getInstance(Session.getSession().getCredentials()),
                    new TaskCompletedCallback<List<Category>>() {
                        @Override
                        public void onSuccess(List<Category> object) {
                            categoryList = object;
                            setupNavigationView();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(HomeActivity.this,
                                    "Failed to load categories.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure: Categories load failed: " + message);
                        }
                    }).execute();

            return;
        }

        navigationView.setAdapter(new CategoryListAdapter(this, categoryList));

        navigationView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.getExpandableListAdapter().getChildrenCount(groupPosition) < 1) {
                    Category newCategory = (Category) parent.getExpandableListAdapter()
                            .getGroup(groupPosition);
                    if (filters.getLong("category") == newCategory.getId()) {
                        setFilter((Category) null);
                        checkCategory(false, groupPosition);
                    } else {
                        setFilter(newCategory);
                        unCheckLastCategory();
                        checkCategory(true, groupPosition);
                    }
                    return true;
                }
                return false;
            }
        });

        navigationView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Category newCategory = (Category) parent.getExpandableListAdapter()
                        .getChild(groupPosition, childPosition);
                if (filters.getLong("category") == newCategory.getId()) {
                    setFilter((Category) null);
                    checkCategory(false, groupPosition, childPosition);
                } else {
                    setFilter(newCategory);
                    unCheckLastCategory();
                    checkCategory(true, groupPosition, childPosition);
                }
                return false;
            }
        });
    }

    /**
     * Method to set filter category.
     *
     * @param category      Category instance.
     */
    public void setFilter(Category category) {
        if (category != null) {
            Log.d(TAG, "setFilter: Category set (" + category.getName() + ").");
            filters.putLong("category", category.getId());
        } else {
            Log.d(TAG, "setFilter: Category not set.");
            filters.remove("category");
        }
        goToRecyclerView(filters);
    }

    /**
     * Helper method to check new category in expandable list view.
     *
     * @param check             Checked or not.
     * @param groupPosition     Group position.
     */
    public void checkCategory(boolean check, int groupPosition) {
        long packedPosition = ExpandableListView.getPackedPositionForGroup(groupPosition);
        int flatPosition = navigationView.getFlatListPosition(packedPosition);
        int first = navigationView.getFirstVisiblePosition();
        View view = navigationView.getChildAt(flatPosition - first);
        ImageView checked = view.findViewById(R.id.checked);
        if (check) {
            checked.setVisibility(View.VISIBLE);
            this.groupPosition = groupPosition;
            this.childPosition = -1;
        } else {
            checked.setVisibility(View.GONE);
            this.groupPosition = -1;
            this.childPosition = -1;
        }
    }

    /**
     * Helper method to check new category in expandable list view.
     *
     * @param check                 Check or not.
     * @param groupPosition         Group position.
     * @param childPosition         Child position.
     */
    public void checkCategory(boolean check, int groupPosition, int childPosition) {
        long packedPosition = ExpandableListView
                .getPackedPositionForChild(groupPosition, childPosition);
        int flatPosition = navigationView.getFlatListPosition(packedPosition);
        int first = navigationView.getFirstVisiblePosition();
        View view = navigationView.getChildAt(flatPosition - first);
        ImageView checked = view.findViewById(R.id.checked);
        if (check) {
            checked.setVisibility(View.VISIBLE);
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        } else {
            checked.setVisibility(View.GONE);
            this.groupPosition = -1;
            this.childPosition = -1;
        }
    }

    /**
     * Helper method to hide check on last checked category.
     */
    public void unCheckLastCategory() {
        if (groupPosition != -1 && childPosition != -1) {
            checkCategory(false, groupPosition, childPosition);
        } else if (groupPosition != -1) {
            checkCategory(false, groupPosition);
        }
    }

    /**
     * Method to set filter query.
     *
     * @param query     Query string.
     */
    public void setFilter(String query) {
        filters.putString("query", query);
        goToRecyclerView(filters);
    }

    /**
     * Method to set up listeners for toolbar buttons under categories list.
     */
    private void setupActionButtons() {
        Log.d(TAG, "setupActionButtons: Setting up action buttons");

        View container = findViewById(R.id.action_buttons_container);
        ImageView settingsButton = container.findViewById(R.id.settings_button);
        ImageView basketButton = container.findViewById(R.id.basket_button);

        settingsButton.setOnClickListener(this);
        basketButton.setOnClickListener(this);
    }

    /**
     * Helper method to make fragment transaction and add filters to recycler view.
     *
     * @param filters           Bundle of filters.
     */
    private void goToRecyclerView(Bundle filters) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomeRecyclerViewFragment fragment = new HomeRecyclerViewFragment();
        fragment.setArguments(filters);

        transaction.replace(R.id.home_container, fragment,
                getString(R.string.tag_main_recycle_fragment));
        transaction.commit();
    }

    /**
     * Interface method to make fragment transaction.
     *
     * @param fragment              New fragment.
     * @param tag                   New fragment tag.
     * @param addToBackStack        Add current fragment to backstack or not.
     * @param add                   Add or replace.
     */
    @Override
    public void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack,
                                      boolean add) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (add) {
            transaction.add(R.id.home_container, fragment, tag);
        } else transaction.replace(R.id.home_container, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * On click listener for action bar buttons under navigation bar.
     *
     * @param v         Action button view.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_button:{
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.basket_button:{
                BasketModal modal = new BasketModal(v.getContext());
                modal.show();
                break;
            }
        }
    }

    @Override
    public void lockDrawer(boolean bool) {
        if (bool) {
            homeDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            homeDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
