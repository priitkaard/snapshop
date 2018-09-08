package ee.ttu.iti0202_gui.android.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Basket singleton class for holding basket content.
 *
 * @author Priit Käärd
 */
public class Basket {
    private static Basket instance;

    private List<Product> content = new ArrayList<>();

    static {
        instance = new Basket();
    }

    private Basket() { }

    /**
     * Static getter for singleton instance.
     */
    public static Basket getInstance() {
        return instance;
    }

    /**
     * Getter for basket content.
     *
     * @return          List of products that are in the basket.
     */
    public List<Product> getContent() {
        return content;
    }

    /**
     * Method to add a new product to the basket.
     *
     * @param product           Product instance.
     */
    public void addProduct(Product product) {
        for (Product p : content)
            if (p.getId().equals(product.getId())) return;

        content.add(product);
    }

    /**
     * Get total price of products in basket.
     *
     * @return          BigDecimal of total basket price.
     */
    public BigDecimal getTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Product product : content) {
            sum = sum.add(product.getPrice());
        }
        return sum.stripTrailingZeros();
    }
}
