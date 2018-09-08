package ee.ttu.iti0202_gui.restapi.rest.dao;

import ee.ttu.iti0202_gui.restapi.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
