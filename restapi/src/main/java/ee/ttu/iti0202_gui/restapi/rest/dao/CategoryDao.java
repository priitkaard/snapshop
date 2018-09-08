package ee.ttu.iti0202_gui.restapi.rest.dao;

import ee.ttu.iti0202_gui.restapi.rest.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDao extends JpaRepository<Category, Long> {
    Category findById(Long id);
    List<Category> findAllByParentCategoryIsNull();
}
