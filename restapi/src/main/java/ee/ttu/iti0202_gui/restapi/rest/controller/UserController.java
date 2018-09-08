package ee.ttu.iti0202_gui.restapi.rest.controller;

import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.UserService;
import ee.ttu.iti0202_gui.restapi.util.RestUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST Controller class for user objects.
 *
 * @author Priit Käärd
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to update some user values.
     *
     * @param user      Partial fields from user object.
     * @return          Update user object.
     */
    @PutMapping("/")
    public ResponseEntity updateUser(@RequestBody User user) {
        Optional<User> originalUser = userService.findByUsername(user.getUsername());
        if (originalUser.isPresent()) {
            User original = originalUser.get();
            // TODO: switch and fix products merging.
            RestUtil.copyNonNullProperties(original, user);

            return ResponseEntity.ok(userService.save(user));
        }
        return ResponseEntity.badRequest().body("Please provide a valid username.");
    }
}
