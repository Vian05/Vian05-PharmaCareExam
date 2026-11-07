package pharmacare.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Stores a customer's selected medicines in memory before checkout. */
public class ShoppingCart {
    private final ArrayList<CartItem> items = new ArrayList<>();

    /** Adds a medicine or increases its existing cart quantity. */
    public void addMedicine(Medicine medicine, int quantity) {
        if (medicine == null) throw new IllegalArgumentException("Medicine cannot be null.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");

        CartItem existing = findItem(medicine.getId());
        int newQuantity = quantity + (existing == null ? 0 : existing.getQuantity());
        if (newQuantity > medicine.getStock()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        }
        if (medicine instanceof LimitedMedicine limited && newQuantity > limited.getMaxPurchaseQuantity()) {
            throw new IllegalArgumentException("Purchase limit is " + limited.getMaxPurchaseQuantity() + " unit(s) per purchase.");
        }

        if (existing == null) items.add(new CartItem(medicine, quantity));
        else existing.setQuantity(newQuantity);
    }

    /** Updates the quantity of an existing cart item. */
    public void updateQuantity(String medicineId, int quantity) {
        CartItem item = requireItem(medicineId);
        item.setQuantity(quantity);
    }

    /** Removes a medicine from the cart and reports whether it existed. */
    public boolean removeMedicine(String medicineId) {
        CartItem item = findItem(medicineId);
        return item != null && items.remove(item);
    }

    /** Returns a defensive copy of cart items. */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /** Calculates the total cart value before payment fees. */
    public BigDecimal getSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) total = total.add(item.getSubtotal());
        return total;
    }

    /** Returns whether the cart contains no items. */
    public boolean isEmpty() { return items.isEmpty(); }

    /** Removes all items from the cart. */
    public void clear() { items.clear(); }

    /** Finds a cart item by medicine ID. */
    private CartItem findItem(String medicineId) {
        if (medicineId == null) return null;
        for (CartItem item : items) {
            if (item.getMedicine().getId().equalsIgnoreCase(medicineId.trim())) return item;
        }
        return null;
    }

    /** Finds a cart item and throws a clear error when it is absent. */
    private CartItem requireItem(String medicineId) {
        CartItem item = findItem(medicineId);
        if (item == null) throw new IllegalArgumentException("Medicine is not in the cart: " + medicineId);
        return item;
    }
}
