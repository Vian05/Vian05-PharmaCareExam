package pharmacare.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Represents one medicine and its quantity inside a shopping cart. */
public class CartItem {
    private final Medicine medicine;
    private int quantity;

    public CartItem(Medicine medicine, int quantity) {
        this.medicine = Objects.requireNonNull(medicine, "Medicine cannot be null.");
        setQuantity(quantity);
    }

    /** Returns the medicine associated with this cart item. */
    public Medicine getMedicine() { return medicine; }

    /** Returns the requested quantity. */
    public int getQuantity() { return quantity; }

    /** Updates quantity after checking stock and category-specific purchase limits. */
    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        if (quantity > medicine.getStock()) throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        if (medicine instanceof LimitedMedicine limited && quantity > limited.getMaxPurchaseQuantity()) {
            throw new IllegalArgumentException("Purchase limit is " + limited.getMaxPurchaseQuantity() + " unit(s).");
        }
        this.quantity = quantity;
    }

    /** Calculates the line subtotal. */
    public BigDecimal getSubtotal() {
        return medicine.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
