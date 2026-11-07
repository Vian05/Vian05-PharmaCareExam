package pharmacare.payment;

import java.math.BigDecimal;

/** Cash payment with no additional fee. */
public class CashPayment implements PaymentMethod {
    @Override public String getName() { return "Cash"; }
    @Override public BigDecimal getFeeRate() { return new BigDecimal("0.00"); }
}
