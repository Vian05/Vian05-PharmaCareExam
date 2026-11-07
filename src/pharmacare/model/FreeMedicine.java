package pharmacare.model;

import java.math.BigDecimal;

/** Medicine that can be purchased without prescription or a special quantity cap. */
public class FreeMedicine extends Medicine {
    public FreeMedicine(String id, String name, String description, BigDecimal price, int stock) {
        super(id, name, description, price, stock);
    }

    /** Returns the medicine category. */
    @Override
    public String getCategory() { return "Obat Bebas"; }

    /** Returns the purchase rule for this category. */
    @Override
    public String getPurchaseRule() { return "No prescription required."; }
}
