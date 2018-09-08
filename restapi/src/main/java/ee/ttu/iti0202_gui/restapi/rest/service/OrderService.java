package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.config.MakseKeskus;
import ee.ttu.iti0202_gui.restapi.rest.dao.OrderDao;
import ee.ttu.iti0202_gui.restapi.rest.model.Order;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service layer class for Orders.
 */
@Service
public class OrderService {
    private OrderDao orderDao;
    private ProductService productService;
    private MakseKeskus makseKeskus;

    public OrderService(OrderDao orderDao, ProductService productService, MakseKeskus makseKeskus) {
        this.orderDao = orderDao;
        this.productService = productService;
        this.makseKeskus = makseKeskus;
    }

    /**
     * Method to create order, deactivate products and generate transaction ID.
     *
     * @param order         Order instance.
     * @return              Order instance.
     */
    public Order createTransaction(Order order) {
        // Disable ordered products temporarily
        order.getProducts().forEach(p -> productService.setActivated(p, false));

        // Get transaction ID
        String transactionID = "";
        try {
            transactionID = makseKeskus.generateTransaction(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        order.setTransactionId(transactionID);

        // Save Order instance
        orderDao.saveAndFlush(order);

        return order;
    }

    /**
     * Method to list all orders given to current account.
     *
     * @param user      User instance.
     * @return          List of orders.
     */
    public List<Order> getAllUserOrders(User user) {
        return orderDao.findAllByCustomer(user);
    }

    public Order save(Order order) {
        return orderDao.save(order);
    }

    public Order getById(Long id) {
        return orderDao.findById(id);
    }

    public void deleteOrder(Order order) {
        orderDao.delete(order);
    }
}
