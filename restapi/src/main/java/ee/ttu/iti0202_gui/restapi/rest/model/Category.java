package ee.ttu.iti0202_gui.restapi.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for Categories.
 */
@Entity
@Table(name = "category")
public class Category {
    private Long id;
    private String name;
    private Category parentCategory;
    private List<Category> subCategories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    public Category() { }

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public Category setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    public Category getParentCategory() {
        return parentCategory;
    }

    public Category setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
        return this;
    }

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
