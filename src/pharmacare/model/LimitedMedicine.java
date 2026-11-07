package pharmacare.model;

import java.math.BigDecimal;

/** Medicine that does not require a prescription but has a per-purchase quantity limit. */
public class LimitedMedicine extends Medicine {
    private int maxPurchaseQuantity;

    public LimitedMedicine(String id, String name, String description, BigDecimal price,
                           int stock, int maxPurchaseQuantity) {
        super(id, name, description, price, stock);
        setMaxPurchaseQuantity(maxPurchaseQuantity);
    }

    /** Returns the maximum units allowed in one purchase. */
    public int getMaxPurchaseQuantity() { return maxPurchaseQuantity; }

    /** Updates the purchase limit to a positive quantity. */
    public void setMaxPurchaseQuantity(int maxPurchaseQuantity) {
        if (maxPurchaseQuantity <= 0) {
            throw new IllegalArgumentException("Maximum purchase quantity must be greater than zero.");
        }
        this.maxPurchaseQuantity = maxPurchaseQuantity;
    }

    /** Returns the medicine category. */
    @Override
    public String getCategory() { return "Obat Bebas Terbatas"; }

    /** Returns the purchase rule for this category. */
    @Override
    public String getPurchaseRule() {
        return "Maximum " + maxPurchaseQuantity + " unit(s) per purchase.";
    }
}
