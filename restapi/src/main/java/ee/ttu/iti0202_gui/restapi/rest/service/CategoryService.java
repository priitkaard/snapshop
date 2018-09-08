package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.CategoryDao;
import ee.ttu.iti0202_gui.restapi.rest.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer class for Categories.
 */
@Service
public class CategoryService {
    private CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * Get Category by its ID.
     *
     * @param id        Category ID.
     * @return          Category instance.
     */
    public Category getById(long id) {
        return categoryDao.findById(id);
    }

    /**
     * Get list of current parent categories.
     *
     * @return      List of parent categories.
     */
    public List<Category> getParentCategories() {
        return categoryDao.findAllByParentCategoryIsNull();
    }
}
