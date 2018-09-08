package ee.ttu.iti0202_gui.android.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Order entity class.
 *
 * @author Priit Käärd
 */
public class Order {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("customer")
    private User customer;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("orderState")
    private OrderState orderState = OrderState.PAYMENT_PENDING;
    @JsonProperty("products")
    private List<Product> products = new ArrayList<>();

    public Order() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
