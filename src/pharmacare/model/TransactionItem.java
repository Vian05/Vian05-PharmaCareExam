package pharmacare.model;

import java.math.BigDecimal;

/** Immutable snapshot of a medicine item at the time of purchase. */
public class TransactionItem {
    private final String medicineId;
    private final String medicineName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    public TransactionItem(String medicineId, String medicineName, int quantity, BigDecimal unitPrice) {
        if (medicineId == null || medicineId.isBlank()) throw new IllegalArgumentException("Medicine ID cannot be empty.");
        if (medicineName == null || medicineName.isBlank()) throw new IllegalArgumentException("Medicine name cannot be empty.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be positive.");
        }
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /** Returns the purchased medicine ID. */
    public String getMedicineId() { return medicineId; }

    /** Returns the purchased medicine name snapshot. */
    public String getMedicineName() { return medicineName; }

    /** Returns the purchased quantity. */
    public int getQuantity() { return quantity; }

    /** Returns the unit price at purchase time. */
    public BigDecimal getUnitPrice() { return unitPrice; }

    /** Returns the line subtotal. */
    public BigDecimal getSubtotal() { return subtotal; }
}
