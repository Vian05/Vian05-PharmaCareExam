package pharmacare.service;

import pharmacare.model.CartItem;
import pharmacare.model.Customer;
import pharmacare.model.PrescriptionMedicine;
import pharmacare.model.ShoppingCart;
import pharmacare.model.Transaction;
import pharmacare.payment.PaymentMethod;

import java.math.BigDecimal;

/** Validates and processes a simulated checkout. */
public class CheckoutService {

    /** Calculates the fee for a selected payment method. */
    public BigDecimal calculateFee(ShoppingCart cart, PaymentMethod paymentMethod) {
        validateCartAndPayment(cart, paymentMethod);
        return paymentMethod.calculateFee(cart.getSubtotal());
    }

    /** Processes payment and deducts stock only after simulated payment succeeds. */
    public Transaction checkout(Customer customer, ShoppingCart cart,
                                 PrescriptionService prescriptionService, PaymentMethod paymentMethod,
                                 TransactionService transactionService) {
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null.");
        if (prescriptionService == null) throw new IllegalArgumentException("Prescription service cannot be null.");
        if (transactionService == null) throw new IllegalArgumentException("Transaction service cannot be null.");
        validateCheckout(customer, cart, prescriptionService);

        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal fee = paymentMethod.calculateFee(subtotal);
        BigDecimal total = subtotal.add(fee);

        if (!paymentMethod.processPayment(total)) {
            throw new IllegalStateException("Payment was unsuccessful. Stock was not changed.");
        }

        Transaction transaction = transactionService.recordTransaction(customer, cart.getItems(),
                paymentMethod.getName(), subtotal, fee, total);

        for (CartItem item : cart.getItems()) {
            if (item.getMedicine() instanceof PrescriptionMedicine) {
                prescriptionService.consumeApprovedPrescription(customer.getId(), item.getMedicine().getId());
            }
        }
        for (CartItem item : cart.getItems()) {
            item.getMedicine().reduceStock(item.getQuantity());
        }
        cart.clear();
        return transaction;
    }


    /** Validates customer identity, prescription approval, and stock before payment. */
    public void validateCheckout(Customer customer, ShoppingCart cart,
                                 PrescriptionService prescriptionService) {
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null.");
        if (prescriptionService == null) throw new IllegalArgumentException("Prescription service cannot be null.");
        if (cart == null || cart.isEmpty()) throw new IllegalArgumentException("Shopping cart is empty.");

        for (CartItem item : cart.getItems()) {
            if (item.getMedicine() instanceof PrescriptionMedicine
                    && !prescriptionService.hasApprovedPrescription(customer.getId(), item.getMedicine().getId())) {
                throw new IllegalArgumentException("An approved prescription is required for "
                        + item.getMedicine().getName() + ".");
            }
            if (item.getQuantity() > item.getMedicine().getStock()) {
                throw new IllegalArgumentException("Insufficient stock for " + item.getMedicine().getName());
            }
        }
    }

    /** Checks that cart and payment method are valid before checkout. */
    private void validateCartAndPayment(ShoppingCart cart, PaymentMethod paymentMethod) {
        if (cart == null || cart.isEmpty()) throw new IllegalArgumentException("Shopping cart is empty.");
        if (paymentMethod == null) throw new IllegalArgumentException("Payment method is required.");
    }
}
