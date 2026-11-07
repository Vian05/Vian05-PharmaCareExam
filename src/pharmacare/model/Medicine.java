package pharmacare.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Base type for all medicines in the pharmacy catalog.
 * Encapsulates common medicine data and stock operations.
 */
public abstract class Medicine {
    private final String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    protected Medicine(String id, String name, String description, BigDecimal price, int stock) {
        this.id = requireText(id, "Medicine ID");
        setName(name);
        setDescription(description);
        setPrice(price);
        setStock(stock);
    }

    /** Returns the medicine ID. */
    public String getId() { return id; }

    /** Returns the medicine name. */
    public String getName() { return name; }

    /** Updates the medicine name. */
    public void setName(String name) { this.name = requireText(name, "Medicine name"); }

    /** Returns the medicine description. */
    public String getDescription() { return description; }

    /** Updates the medicine description. */
    public void setDescription(String description) { this.description = requireText(description, "Description"); }

    /** Returns the unit price. */
    public BigDecimal getPrice() { return price; }

    /** Updates the unit price after validating it is positive. */
    public void setPrice(BigDecimal price) {
        Objects.requireNonNull(price, "Price cannot be null.");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = price;
    }

    /** Returns the available stock quantity. */
    public int getStock() { return stock; }

    /** Sets the stock to a non-negative quantity. */
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.stock = stock;
    }

    /** Adds a positive quantity to the current stock. */
    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Restock quantity must be greater than zero.");
        stock = Math.addExact(stock, quantity);
    }

    /** Reduces stock only when enough units are available. */
    public void reduceStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        if (quantity > stock) throw new IllegalArgumentException("Insufficient stock.");
        stock -= quantity;
    }

    /** Returns the medicine category displayed in the catalog. */
    public abstract String getCategory();

    /** Returns purchase guidance specific to this medicine category. */
    public abstract String getPurchaseRule();

    /** Validates a non-empty text value and returns its trimmed form. */
    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Stock: %d | %s",
                id, name, getCategory(), price.toPlainString(), stock, getPurchaseRule());
    }
}
