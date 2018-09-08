package ee.ttu.iti0202_gui.android.models;

import java.util.List;

/**
 * User Builder class for more elegant way to create User objects.
 *
 * @author  Priit Käärd
 */
public class UserBuilder {
    private Long id;
    private String username;
    private String password;
    private String email;
    private List<Role> roles;

    public UserBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setRoles(List<Role> roles) {
        this.roles = roles;
        return this;
    }

    public User createUser() {
        return new User(id, username, password, email, roles);
    }
}