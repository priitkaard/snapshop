package ee.ttu.iti0202_gui.restapi;

import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.RoleService;
import ee.ttu.iti0202_gui.restapi.rest.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
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
    private UserService userService;
    @Autowired
    private RoleService roleService;

    public User createUser() {
        Optional<User> u = userService.findByUsername(USERNAME);
        u.ifPresent(user1 -> userService.remove(user1));

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

        return user;
    }

    @Test
    public void createTheUser() {
        User user = createUser();

        user = userService.save(user);
        assertTrue(user.getUsername().equals(USERNAME));
        assertTrue(user.getFirstName().equals(FIRST_NAME));
        assertTrue(user.getLastName().equals(LAST_NAME));
        assertTrue(user.getEmail().equals(EMAIL));
        assertTrue(user.getHomeAddress().equals(HOME_ADDRESS));
        assertTrue(user.getPhoneNumber().equals(PHONE_NUMBER));
        assertTrue(user.getAccountNumber().equals(ACCOUNT_NUMBER));
        assertTrue(user.getAccountOwner().equals(ACCOUNT_OWNER));
        if (user.getRoles().size() > 0) {
            assertTrue(user.getRoles().get(0).getName().equals("USER"));
        } else {
            fail("User roles not set.");
        }
    }

    @Test
    public void testUserDetailsUsedCases() {
        User user = createUser();

        user = userService.save(user);
        assertTrue(userService.usernameInUse(user.getUsername()));
        assertTrue(userService.emailInUse(user.getEmail()));
    }



	/*
	@Test
	public void testMKAPI() throws IOException {
		MakseKeskusAPI api = new MakseKeskusAPI();
		Transaction transaction = new TransactionBuilder()
				.setAmount(BigDecimal.ONE)
				.setCurrency("EUR")
				.setReference("Toode123")
				.setMerchantData("")
				.setTransactionUrl(new TransactionUrlBuilder()
					.setReturnUrl(new URL("localhost:8080/mkapi/return", "POST"))
					.setCancelUrl(new URL("localhost:8080/mkapi/cancel", "POST"))
					.setNotificationUrl(new URL("localhost:8080/mkapi/notification", "POST"))
					.createTransactionUrl())
				.createTransaction();
		Customer customer = new CustomerBuilder()
				.setEmail("prkaar@ttu.ee")
				.setIp("192.168.0.1")
				.setCountry("ee")
				.setLocale("et")
				.createCustomer();
		CreateTransaction createTransaction = new CreateTransaction(transaction, customer);

		System.out.println("Transaction ID: " + api.getTransactionId(createTransaction));
	}
	*/
}
