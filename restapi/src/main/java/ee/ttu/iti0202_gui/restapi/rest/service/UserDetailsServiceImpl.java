package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.UserDao;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service layer class for Custom User Details.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDao userDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Overridden function for loading User Details by Username.
     *
     * @param username                          Username as a string.
     * @return                                  UserDetails instance.
     * @throws UsernameNotFoundException        Thrown if username was not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) return null;

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        user.getRoles().forEach(r -> authoritySet.add(new SimpleGrantedAuthority(r.getName())));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authoritySet);
    }
}
