package ee.ttu.iti0202_gui.android.models;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product entity.
 *
 * @author Priit Käärd
 */
public class Product {
    @SuppressLint("UseSparseArrays")
    private static Map<Long, Product> products = new HashMap<>();

    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("activated")
    private boolean activated;
    @JsonProperty("location")
    private String location;
    @JsonProperty("images")
    private List<String> images;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("createDate")
    private Date createDate;
    @JsonProperty("updateDate")
    private Date updateDate;
    @JsonProperty("owner")
    private User owner;
    @JsonProperty("category")
    private Category category;
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    /**
     * Static getter for loaded products.
     *
     * @return      Map of loaded products.
     */
    public static Map<Long, Product> getProducts() {
        return products;
    }

    /**
     * Default constructor needed for Jackson XML object mapping.
     */
    Product() { }

    public Product(Long id, String title, String description, BigDecimal price, boolean activated,
                   String location, List<String> images, String thumbnail, Date createDate,
                   Date updateDate, User owner, Category category, Map<String, Bitmap> bitmaps) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.activated = activated;
        this.location = location;
        this.images = images;
        this.thumbnail = thumbnail;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.owner = owner;
        this.category = category;
        this.bitmaps = bitmaps;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getImages() {
        return images;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public User getOwner() {
        return owner;
    }

    public Category getCategory() {
        return category;
    }

    public Map<String, Bitmap> getBitmaps() {
        return bitmaps;
    }
}
