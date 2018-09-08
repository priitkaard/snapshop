package ee.ttu.iti0202_gui.android.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Category entity.
 *
 * @author Priit Käärd
 */
public class Category {
    // DB variables
    public static final String TABLE_NAME = "category";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT = "parent_category_id";
    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PARENT + " INTEGER, "
            + " FOREIGN KEY (" + COLUMN_PARENT + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + ")"
            + ")";

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Category parentCategory;
    @JsonProperty("subCategories")
    private List<Category> subCategories = new ArrayList<>();

    public Category() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }
}
