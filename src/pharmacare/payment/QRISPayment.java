package pharmacare.payment;

import java.math.BigDecimal;

/** QRIS payment with a simulated 0.7 percent fee. */
public class QRISPayment implements PaymentMethod {
    @Override public String getName() { return "QRIS"; }
    @Override public BigDecimal getFeeRate() { return new BigDecimal("0.007"); }
}
