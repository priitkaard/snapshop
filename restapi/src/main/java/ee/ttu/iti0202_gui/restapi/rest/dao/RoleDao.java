package ee.ttu.iti0202_gui.restapi.rest.dao;

import ee.ttu.iti0202_gui.restapi.rest.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
}