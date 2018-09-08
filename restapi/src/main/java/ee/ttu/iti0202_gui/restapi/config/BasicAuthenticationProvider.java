package ee.ttu.iti0202_gui.restapi.config;

import ee.ttu.iti0202_gui.restapi.rest.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Class for providing HTTP Basic Authentication for Spring Application.
 */
@Component
public class BasicAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final String CREDENTIALS_EMPTY_ERROR = "Credentials may not be empty";

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public BasicAuthenticationProvider(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Implemented method for checking authentication.
     *
     * @param userDetails                   User details instance.
     * @param auth                          Auth Token instance.
     * @throws AuthenticationException      Possible authentication exception thrown.
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken auth)
            throws AuthenticationException {

        if (auth.getCredentials() == null || userDetails.getPassword() == null) {
            throw new BadCredentialsException(CREDENTIALS_EMPTY_ERROR);
        }
        if (!passwordEncoder.matches((String) auth.getCredentials(), userDetails.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }
    }

    /**
     * Implemented method for retrieving the User Details instance.
     *
     * @param username                      Username as a string
     * @param authentication                Authentication token.
     * @return                              UserDetails instance.
     * @throws AuthenticationException      Possible auth exceptions thrown.
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) throw new BadCredentialsException(INVALID_CREDENTIALS);
        return userDetails;
    }
}
