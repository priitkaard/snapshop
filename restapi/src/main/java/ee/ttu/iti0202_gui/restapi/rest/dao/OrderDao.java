package ee.ttu.iti0202_gui.restapi.rest.dao;

import ee.ttu.iti0202_gui.restapi.rest.model.Order;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDao extends JpaRepository<Order, Long> {
    Order findById(Long id);
    List<Order> findAllByCustomer(User user);
}
