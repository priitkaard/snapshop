package ee.ttu.iti0202_gui.restapi.rest.controller;

import ee.ttu.iti0202_gui.restapi.rest.model.Order;
import ee.ttu.iti0202_gui.restapi.rest.model.OrderState;
import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import ee.ttu.iti0202_gui.restapi.rest.model.User;
import ee.ttu.iti0202_gui.restapi.rest.service.OrderService;
import ee.ttu.iti0202_gui.restapi.rest.service.ProductService;
import ee.ttu.iti0202_gui.restapi.rest.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private OrderService orderService;
    private UserService userService;
    private ProductService productService;

    public OrderController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    /**
     * Endpoint to make an order.
     *
     * @param data      Order instance including User and List of products.
     * @return          Response entity of checkout state with Order instance.
     */
    @PostMapping(value = "/makeorder")
    public ResponseEntity makeOrder(@RequestBody Order data) {
        Order result = new Order();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByUsername(username);
        if (!user.isPresent())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        result.setCustomer(user.get());

        List<Product> productList = new ArrayList<>();
        for (Product p : data.getProducts()) {
            Optional<Product> product = productService.getProductById(p.getId());
            if (!product.isPresent())
                return ResponseEntity.badRequest().body("One of your products do not exist");
            if (!product.get().isActivated()) {
                return ResponseEntity.badRequest().body("'" + product.get().getTitle() + "' is not available");
            }
            productList.add(product.get());
        }
        result.setProducts(productList);

        result = orderService.createTransaction(result);
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint to list all orders of current user.
     *
     * @return List of orders.
     */
    @GetMapping(value = "/myorders")
    public ResponseEntity myOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByUsername(username);
        return user.<ResponseEntity>map(user1 ->
                ResponseEntity.ok(orderService.getAllUserOrders(user1))).orElseGet(() ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

    }

    /**
     * Endpoint to confirm orders payment.
     *
     * @param order     Order instance.
     * @return          Updated order instance.
     */
    @PostMapping("/confirm")
    public ResponseEntity confirmPayment(@RequestBody Order order) {
        order = orderService.getById(order.getId());
        if (order == null)
            return ResponseEntity.badRequest().body("Order has expired.");

        if (!order.getOrderState().equals(OrderState.PAYMENT_PENDING)) {
            return ResponseEntity.badRequest().body("Order is already paid.");
        }

        order.setOrderState(OrderState.CONFIRMED);
        return ResponseEntity.ok(orderService.save(order));
    }

    /**
     * Endpoint to cancel paymend on posted order.
     *
     * @param order     Order instance.
     * @return          Response status.
     */
    @PostMapping("/cancel")
    public ResponseEntity cancelPayment(@RequestBody Order order) {
        order = orderService.getById(order.getId());
        if (order == null)
            return ResponseEntity.badRequest().body("Order has already expired");
        if (!order.getOrderState().equals(OrderState.PAYMENT_PENDING))
            return ResponseEntity.badRequest().body("Order is already paid");

        order.getProducts().forEach(p -> productService.setActivated(p, true));
        orderService.deleteOrder(order);
        return ResponseEntity.ok("Order cancelled");
    }
}
