package pharmacare.model;

import java.math.BigDecimal;

/** Medicine that may only be purchased after a prescription has been approved. */
public class PrescriptionMedicine extends Medicine {
    public PrescriptionMedicine(String id, String name, String description, BigDecimal price, int stock) {
        super(id, name, description, price, stock);
    }

    /** Returns the medicine category. */
    @Override
    public String getCategory() { return "Obat Keras"; }

    /** Returns the purchase rule for this category. */
    @Override
    public String getPurchaseRule() { return "Approved prescription required before checkout."; }
}
