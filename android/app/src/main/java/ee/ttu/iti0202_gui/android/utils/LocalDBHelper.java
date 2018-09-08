package ee.ttu.iti0202_gui.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ee.ttu.iti0202_gui.android.models.Category;

/**
 * Helper class to handle local SQLite database.
 *
 * @author Priit Käärd
 */
public class LocalDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "snapshop_db";
    private static final int DB_VERSION = 1;

    public LocalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Category.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);

        onCreate(db);
    }

    /**
     * Method to insert list of new categories to local database.
     *
     * @param categories          list of catefory instances.
     */
    public void insertParentCategories(List<Category> categories) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;
        for (Category parent : categories) {
            values = new ContentValues();
            values.put(Category.COLUMN_ID, parent.getId());
            values.put(Category.COLUMN_NAME, parent.getName());

            db.insertWithOnConflict(Category.TABLE_NAME, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);

            // Also save subcategories.
            for (Category cat : parent.getSubCategories()) {
                values = new ContentValues();
                values.put(Category.COLUMN_ID, cat.getId());
                values.put(Category.COLUMN_NAME, cat.getName());
                values.put(Category.COLUMN_PARENT, parent.getId());

                db.insertWithOnConflict(Category.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
        db.close();
    }

    /**
     * Method to get the list of categories from local db.
     *
     * @return      List of categories.
     */
    public List<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Retrieve all parent categories
        String sql = "SELECT * FROM " + Category.TABLE_NAME
                + " WHERE " + Category.COLUMN_PARENT + " IS NULL";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Category category = new Category();
                category.setId(cursor.getLong(cursor.getColumnIndex(Category.COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME)));
                result.add(category);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // Retrieve all sub categories
        sql = "SELECT * FROM " + Category.TABLE_NAME
                + " WHERE " + Category.COLUMN_PARENT + " IS NOT NULL";
        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long parentID = cursor.getLong(cursor.getColumnIndex(Category.COLUMN_PARENT));
                for (Category parent : result) {
                    if (parent.getId().equals(parentID)) {
                        Category category = new Category();
                        category.setId(cursor.getLong(cursor.getColumnIndex(Category.COLUMN_ID)));
                        category.setName(cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME)));
                        category.setParentCategory(parent);
                        parent.getSubCategories().add(category);
                        break;
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return result;
    }
}
