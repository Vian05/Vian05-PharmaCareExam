package pharmacare.payment;

import java.math.BigDecimal;

/** Card payment with a simulated 2 percent fee. */
public class CardPayment implements PaymentMethod {
    @Override public String getName() { return "Card"; }
    @Override public BigDecimal getFeeRate() { return new BigDecimal("0.02"); }
}
