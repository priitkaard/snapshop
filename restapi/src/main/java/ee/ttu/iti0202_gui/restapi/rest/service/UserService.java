package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.UserDao;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for users.
 */
@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Find User by username.
     *
     * @param username      Users username.
     * @return              Optional of nullable User.
     */
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userDao.findByUsername(username));
    }

    /**
     * Save a new user instance.
     *
     * @param user      Transient user instance.
     * @return          Update user object.
     */
    public User save(User user) {
        return userDao.save(user);
    }

    /**
     * Delete the user from database.
     *
     * @param user      User object.
     */
    public void remove(User user) {
        userDao.delete(user);
    }

    /**
     * Check if email is in use already.
     *
     * @param email     Email as a string.
     * @return          Boolean if exists or not.
     */
    public boolean emailInUse(String email) {
        return userDao.existsByEmail(email);
    }

    /**
     * Check if username is in use already.
     *
     * @param username  Username as a string.
     * @return          Boolean if exists or not.
     */
    public boolean usernameInUse(String username) {
        return userDao.existsByUsername(username);
    }
}
