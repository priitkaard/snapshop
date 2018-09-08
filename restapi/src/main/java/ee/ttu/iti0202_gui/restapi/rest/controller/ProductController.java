package ee.ttu.iti0202_gui.restapi.rest.controller;

import ee.ttu.iti0202_gui.restapi.rest.model.Category;
import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.CategoryService;
import ee.ttu.iti0202_gui.restapi.rest.service.ProductService;
import ee.ttu.iti0202_gui.restapi.rest.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller class for Product entities.
 */
@RestController
@RequestMapping(value = "/product")
public class ProductController {
    private ProductService productService;
    private UserService userService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, UserService userService, CategoryService categoryService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    /**
     * Method for loading products by search parameters.
     *          Endpoint:   /product/load
     *
     * @param limit         Amount of products in output.
     * @param page          Page of products.
     * @param categoryId    Category ID.
     * @param query         Query string.
     * @return              List of products.
     */
    @GetMapping(value = "/load")
    public List<Product> loadProducts(@RequestParam(value = "limit", required = false) final Integer limit,
                                      @RequestParam(value = "page", required = false) final Integer page,
                                      @RequestParam(value = "category", required = false) final Long categoryId,
                                      @RequestParam(value = "query", required = false) final String query,
                                      @RequestParam(value = "username", required = false) final String username) {
        List<Product> result = productService.getAllProducts();

        // Filter out unactivated
        result = result.stream().filter(Product::isActivated).collect(Collectors.toList());

        // Filter by category
        if (categoryId != null) {
            result = result.stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // Filter by query
        if (query != null) {
            List<Product> allProducts = new ArrayList<>(result);
            result = new ArrayList<>();
            final List<String> keywords = Arrays.asList(query.toLowerCase().trim().split(" "));

            // Filter title by keywords.
            for (String k : keywords) {
                result.addAll(allProducts.stream()
                        .filter(p -> p.getTitle().toLowerCase().contains(k))
                        .collect(Collectors.toList()));
            }

            // Add filtered descriptions to the end.
            for (String k : keywords) {
                result.addAll(allProducts.stream()
                        .filter(p -> p.getDescription().toLowerCase().contains(k))
                        .collect(Collectors.toList()));
            }

            result = result.stream().distinct().collect(Collectors.toList());
        }

        // Filter by owners username
        if (username != null) {
            result = result.stream()
                    .filter(p -> p.getOwner().getUsername().equals(username))
                    .collect(Collectors.toList());
        }

        // Filter by page and limit
        if (limit != null && page != null) {
            // No more results
            if (result.size() <= page * limit) return new ArrayList<>();

            int start = page * limit;
            int end = page * limit + limit;
            end = (result.size() > end) ? end : result.size();

            result = result.subList(start, end);
        }

        return result;
    }

    /**
     * Method for posting a new product.
     *          Endpoint: /product/new
     *
     * @param product       Product instance.
     * @return              Response entity of the saved product.
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addNewProduct(@RequestBody Product product) {
        // Authenticating and setting as owner
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> owner = userService.findByUsername(username);
        if (!owner.isPresent()) return ResponseEntity.badRequest().body("You are not a registered user.");
        product.setOwner(owner.get());

        // Validating category.
        if (product.getCategory() == null) return ResponseEntity.badRequest().body("Category can not be empty");
        product.setCategory(categoryService.getById(product.getCategory().getId()));

        productService.createNewProduct(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Endpoint to load an individual product.
     * /product/load/{id}
     *
     * @param id        Product ID.
     * @return          Response entity of Product instance.
     */
    @RequestMapping(value="/load/{id}", method = RequestMethod.GET)
    public ResponseEntity getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.badRequest().body("Product with that ID does not exist."));

    }

    /**
     * Endpoint to delete a product.
     *
     * @param id        Product ID.
     * @return          Results response entity.
     */
    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteProductById(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userService.findByUsername(username);
        if (!currentUser.isPresent()) return ResponseEntity.badRequest().body("You are not the owner of this product");

        Optional<Product> product = productService.getProductById(id);
        if (!product.isPresent()) return ResponseEntity.badRequest().body("Product with that ID was not found.");

        if (!product.get().getOwner().getId().equals(currentUser.get().getId()))
            return ResponseEntity.badRequest().body("You are not the owner of this product.");

        productService.deleteProductById(id);
        return ResponseEntity.ok("Product deleted.");
    }

    /**
     * Endpoint to get all parent categories.
     * /product/categories
     *
     * @return      List of parent categories.
     */
    @GetMapping(value = "/categories")
    public ResponseEntity<List<Category>> getParentCategories() {
        return ResponseEntity.ok(categoryService.getParentCategories());
    }
}
