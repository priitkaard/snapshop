package ee.ttu.iti0202_gui.android.models;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder class for Product entity.
 *
 * @author Priit Käärd
 */
public class ProductBuilder {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private boolean activated;
    private String location;
    private List<String> images;
    private String thumbnail;
    private Date createDate;
    private Date updateDate;
    private User owner;
    private Category category;
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    public ProductBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ProductBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setActivated(boolean activated) {
        this.activated = activated;
        return this;
    }

    public ProductBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ProductBuilder setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public ProductBuilder setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public ProductBuilder setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public ProductBuilder setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public ProductBuilder setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public ProductBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    public ProductBuilder setBitmaps(Map<String, Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        return this;
    }

    public Product createProduct() {
        return new Product(id, title, description, price, activated, location, images, thumbnail,
                createDate, updateDate, owner, category, bitmaps);
    }
}