package ee.ttu.iti0202_gui.restapi.rest.dao;

import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product, Long> {
    Product findProductById(Long id);
}
