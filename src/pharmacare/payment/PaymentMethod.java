package pharmacare.payment;

import java.math.BigDecimal;

/** Defines the common behavior of simulated pharmacy payment methods. */
public interface PaymentMethod {
    String getName();

    BigDecimal getFeeRate();

    /** Calculates the payment fee from the cart subtotal. */
    default BigDecimal calculateFee(BigDecimal subtotal) {
        return subtotal.multiply(getFeeRate()).setScale(0, java.math.RoundingMode.HALF_UP);
    }

    /** Simulates a successful payment because no external gateway is required. */
    default boolean processPayment(BigDecimal total) {
        return total != null && total.compareTo(BigDecimal.ZERO) > 0;
    }
}
