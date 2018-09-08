package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.RoleDao;
import ee.ttu.iti0202_gui.restapi.rest.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer class for Roles.
 */
@Service
public class RoleService {
    private final RoleDao roleDao;

    @Autowired
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    /**
     * Helper to create a new role.
     *
     * @param role      Transient Role instance.
     */
    private void addRole(Role role) {
        roleDao.save(role);
    }

    /**
     * Method to get a Role instance by name or create a new one.
     *
     * @param name          Role name.
     * @return              Role instance.
     */
    public Role getRole(String name) {
        Role role = roleDao.findRoleByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            addRole(role);
        }
        return role;
    }
}
