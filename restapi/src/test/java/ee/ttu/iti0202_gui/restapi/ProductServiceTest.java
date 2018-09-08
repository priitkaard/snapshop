package ee.ttu.iti0202_gui.restapi;

import ee.ttu.iti0202_gui.restapi.rest.model.Order;
import ee.ttu.iti0202_gui.restapi.rest.model.OrderState;
import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {
    // Product parameters
    private static final String TITLE = "Toote pealkiri";
    private static final String DESCRIPTION = "Toote kirjeldus";
    private static final BigDecimal PRICE = BigDecimal.valueOf(999.99);
    private static final String LOCATION = "Mustamäe, Tallinn";

    // User Data:
    private final static String USERNAME = "priitkaard";
    private final static String PASSWORD = "testparool";
    private final static String FIRST_NAME = "Priit";
    private final static String LAST_NAME = "Kaard";
    private final static String EMAIL = "priit.kaard@coolbet.com";
    private final static String HOME_ADDRESS = "Akadeemia tee";
    private final static String PHONE_NUMBER = "55555555";
    private final static String ACCOUNT_OWNER = "Priit Kaard";
    private final static String ACCOUNT_NUMBER = "123456789";
    private final static String ROLE_NAME = "USER";

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrderService orderService;

    public Product createProduct() {
        Product product = new Product();
        product.setTitle(TITLE);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLocation(LOCATION);
        product.setCategory(categoryService.getById(1L));
        product.setActivated(true);
        product.setOwner(createOwner());
        return product;
    }

    public User createOwner() {
        Optional<User> u = userService.findByUsername("priitkaard");
        if (u.isPresent()) {
            return u.get();
        }

        User user = new User();
        user.setUsername(USERNAME);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setHomeAddress(HOME_ADDRESS);
        user.setPassword(PASSWORD);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setAccountOwner(ACCOUNT_OWNER);
        user.setAccountNumber(ACCOUNT_NUMBER);
        user.setRoles(Collections.singletonList(roleService.getRole(ROLE_NAME)));

        return userService.save(user);
    }

    private Order createOrder() {
        Order order = new Order();
        order.setOrderState(OrderState.PAYMENT_PENDING);
        order.setProducts(Collections.singletonList(createProduct()));
        order.setCustomer(createOwner());
        return order;
    }

    @Test
    public void testCreateProduct() {
        Product product = createProduct();

        product = productService.createNewProduct(product);

        assertTrue(product.getTitle().equals(TITLE));
        assertTrue(product.getDescription().equals(DESCRIPTION));
        assertTrue(product.getLocation().equals(LOCATION));
        assertTrue(product.getOwner().getLastName().equals(LAST_NAME));
        assertTrue(product.getCategory().getId().equals(1L));
        assertTrue(product.getPrice().equals(PRICE));
    }

    @Test
    public void testMakeOrder() {
        Order order = orderService.createTransaction(createOrder());

        assertTrue(order.getTransactionId() != null);
        System.out.println(order.getTransactionId());
        assertTrue(order.getCustomer().getEmail().equals(EMAIL));
        assertTrue(order.getProducts().get(0).getTitle().equals(TITLE));
        assertTrue(order.getOrderState().equals(OrderState.PAYMENT_PENDING));
    }
}
