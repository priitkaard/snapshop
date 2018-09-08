package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.ProductDao;
import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer class for Product entities.
 */
@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Method to save a new product instance.
     *
     * @param product       Transient product instance.
     * @return              Update product object.
     */
    public Product createNewProduct(Product product) {
        return productDao.save(product);
    }

    /**
     * Method to get an Optional instance of Product by ID.
     *
     * @param id        Product ID.
     * @return          Optional of Product instance.
     */
    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productDao.findProductById(id));
    }

    /**
     * Method to delete product by its ID.
     *
     * @param id        Product ID.
     */
    public void deleteProductById(Long id) {
        productDao.delete(id);
    }

    /**
     * Method to add a new image file name to product.
     *
     * @param product           Product instance.
     * @param filename          Image file name with extension.
     */
    public void addImageNameById(Product product, String filename) {
        product.getImages().add(filename);
        if (product.getThumbnail() == null) product.setThumbnail(filename);
        productDao.save(product);
    }

    /**
     * Method to get all products as a list.
     *
     * @return      List of all products available.
     */
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    /**
     * Method to set acticated state for product instance.
     *
     * @param product   Product instance.
     * @param b         Activated or not.
     */
    public void setActivated(Product product, boolean b) {
        product.setActivated(b);
        productDao.save(product);
    }
}
