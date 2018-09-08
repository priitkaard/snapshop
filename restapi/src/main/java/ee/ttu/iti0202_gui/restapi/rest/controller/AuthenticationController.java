package ee.ttu.iti0202_gui.restapi.rest.controller;

import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.RoleService;
import ee.ttu.iti0202_gui.restapi.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

/**
 * Authentication system controller class.
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint for checking user authorization.
     * /auth/check
     *
     * @return      Response entity with proper status code and user instance.
     */
    @GetMapping(value = "/check")
    public ResponseEntity check() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByUsername(username);
        return user.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.badRequest().body("Invalid username"));
    }

    /**
     * Endpoint to register a new user.
     * /auth/register
     *
     * @param user      User instance.
     * @return          Response entity with User instance or error message.
     */
    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody User user) {
        if (userService.usernameInUse(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exists");
        }
        if (userService.emailInUse(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList(roleService.getRole("USER")));

        userService.save(user);
        return ResponseEntity.ok(user);
    }
}
